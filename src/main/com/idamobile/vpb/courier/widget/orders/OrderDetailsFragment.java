package com.idamobile.vpb.courier.widget.orders;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.controllers.ImageManager;
import com.idamobile.vpb.courier.model.ImageType;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.ProtoMapEntry;
import com.idamobile.vpb.courier.navigation.ExtrasBuilder;
import com.idamobile.vpb.courier.network.images.ImageInfo;
import com.idamobile.vpb.courier.security.crypto.CryptoCoder;
import com.idamobile.vpb.courier.util.Intents;
import com.idamobile.vpb.courier.util.Logger;
import com.idamobile.vpb.courier.widget.dialogs.AlertDialogFactory;
import com.idamobile.vpb.courier.widget.dialogs.ProgressDialogFactory;
import com.idamobile.vpb.courier.widget.orders.images.ImagesGrid;
import com.idamobile.vpb.courier.widget.orders.images.OrderImageView;

import java.io.File;

@EFragment(value = R.layout.order_details_fragment)
public class OrderDetailsFragment extends Fragment {

    public static final String TAG = OrderDetailsFragment.class.getSimpleName();

    private static final int TAKE_PICKTURE_REQUEST_CODE = 332;

    private static final String PICKTURE_FILENAME_EXTRA = "picture-filename";
    private static final String TMP_POSTFIX = ".tmp";

    @ViewById(R.id.client_name) TextView nameView;
    @ViewById(R.id.client_status) TextView statusView;
    @ViewById(R.id.images_grid) ViewGroup imagesView;
    @ViewById(R.id.client_order_type) TextView orderTypeView;
    @ViewById(R.id.client_address) TextView addressView;
    @ViewById(R.id.client_metro) TextView metroView;
    @ViewById(R.id.navigate_button) View navigateButton;
    @ViewById(R.id.client_phone) TextView phoneView;
    @ViewById(R.id.call_button) View callButton;
    @ViewById(R.id.client_time) TextView timeView;
    @ViewById(R.id.additional_params_section) ViewGroup additionalParamsSection;
    @ViewById(R.id.client_additional) TextView additionalParamsView;

    private BroadcastReceiver ordersWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshOrder();
            refreshContent();
        }
    };

    private Order order;
    private OrderTimeFormatter orderTimeFormatter;
    private OrderActions orderActions;
    private ImagesGrid imagesGrid;
    private ApplicationMediator mediator;

    private AlertDialogFactory confirmRemoveImageDialog;
    private ProgressDialogFactory progressDialog;

    private File takePictureOutput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderTimeFormatter = new OrderTimeFormatter(getActivity());
        orderActions = new OrderActions(getActivity());
        mediator = CoreApplication.getMediator(getActivity());
        restoreOrder(savedInstanceState);
        restorePictureFilename(savedInstanceState);

        confirmRemoveImageDialog = new AlertDialogFactory(getActivity(), "confirm-remove-image-dialog");
        confirmRemoveImageDialog.setTitle(getText(R.string.confirm_remove_image_dialog_title));
        confirmRemoveImageDialog.setPosButtonText(getText(android.R.string.yes));
        confirmRemoveImageDialog.setNegButtonText(getText(android.R.string.cancel));
        confirmRemoveImageDialog.setCancellable(true);
    }

    @AfterViews
    void setup() {
        imagesGrid = new ImagesGrid(imagesView, mediator);
        imagesGrid.setImageCallbacks(new OrderImageView.OrderImageImageCallbacks() {
            ImageManager imageManager = mediator.getImageManager();

            @Override
            public void onTakeImageClicked(Order order, ImageType image) {
                ImageInfo imageInfo = imageManager.getImageInfo(order, image);
                takePictureOutput = new File(imageInfo.getFile().getAbsoluteFile() + TMP_POSTFIX);
                Intent intent = Intents.takePictureIntent(takePictureOutput);
                startActivityForResult(intent, TAKE_PICKTURE_REQUEST_CODE);
            }

            @Override
            public void onRemoveImageClicked(Order order, ImageType image) {
                final ImageInfo imageInfo = imageManager.getImageInfo(order, image);
                confirmRemoveImageDialog.setPosButtonListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageInfo.getFile().delete();
                        refreshContent();
                    }
                });
                confirmRemoveImageDialog.showDialog();
            }
        });
    }

    private void restoreOrder(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int orderId = savedInstanceState.getInt(ExtrasBuilder.EXTRA_ORDER_ID, -1);
            if (orderId >= 0) {
                order = mediator.getOrdersManager().getOrder(orderId);
            }
        }
    }

    private void saveOrder(Bundle outState) {
        if (order != null) {
            outState.putInt(ExtrasBuilder.EXTRA_ORDER_ID, order.getId());
        }
    }

    private void refreshOrder() {
        if (order != null) {
            order = mediator.getOrdersManager().getOrder(order.getId());
        }
    }

    private void savePictureFilename(Bundle outState) {
        if (takePictureOutput != null) {
            outState.putString(PICKTURE_FILENAME_EXTRA, takePictureOutput.getAbsolutePath());
        }
    }

    private void restorePictureFilename(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(PICKTURE_FILENAME_EXTRA);
            if (!TextUtils.isEmpty(path)) {
                takePictureOutput = new File(path);
            }
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        if (this.order != order) {
            this.order = order;
            refreshContent();
        }
    }

    private void refreshContent() {
        refreshImages();
        if (order != null) {
            getView().setVisibility(View.VISIBLE);

            getActivity().setTitle(
                    getActivity().getString(R.string.order_details_activity_label_format, order.getId()));
            nameView.setText(order.getFullName());
            CharSequence orderTime = orderTimeFormatter.formatSimpleOrderTime(order);
            switch (order.getStatus()) {
                case STATUS_NEW:
                    statusView.setText(getActivity().getString(R.string.order_status_new_format, orderTime));
                    break;
                case STATUS_DOCUMENTS_SUBMITTED:
                    statusView.setText(R.string.order_status_submitted);
                    break;
                case STATUS_DOCUMENTS_NOT_SUBMITTED:
                    statusView.setText(R.string.order_status_not_submitted);
                    break;
                default:
                    throw new IllegalStateException("Unknown order status: " + order.getStatus());
            }
            orderTypeView.setText(order.getOrderType());
            addressView.setText(order.getClientAddress());
            metroView.setText(order.getSubway());
            navigateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderActions.navigateToClient(order);
                }
            });
            phoneView.setText(order.getClientPhone());
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderActions.callClient(order);
                }
            });
            timeView.setText(orderTime);
            if (order.getAttributes() == null || order.getAttributes().isEmpty()) {
                additionalParamsSection.setVisibility(View.GONE);
            } else {
                additionalParamsSection.setVisibility(View.VISIBLE);
                StringBuilder builder = new StringBuilder();
                for (ProtoMapEntry entry : order.getAttributes().getEntries()) {
                    if (!TextUtils.isEmpty(entry.getValue())) {
                        if (builder.length() > 0) {
                            builder.append("\n");
                        }
                        builder.append(entry.getKey()).append(": ").append(entry.getValue());
                    }
                }
                additionalParamsView.setText(builder);
            }
        } else {
            getActivity().setTitle(getActivity().getString(R.string.order_not_found));
            getView().setVisibility(View.GONE);
        }
    }

    private void refreshImages() {
        imagesGrid.setOrder(order);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContent();
        mediator.getOrdersManager().registerForOrders(getActivity(), ordersWatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(ordersWatcher);
        confirmRemoveImageDialog.hideDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICKTURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                cryptImage();
            }
        }
    }

    private void cryptImage() {
        String filename = takePictureOutput.getAbsolutePath();
        final String resultFilename = filename.substring(0, filename.lastIndexOf(TMP_POSTFIX));
        new AsyncTask<Void, Void, Boolean>() {
            private File from = takePictureOutput;
            private File output = new File(resultFilename);

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    CryptoCoder.cryptFile(from, output, mediator.getLoginManager().getSecretKey());
                    return true;
                } catch (Exception e) {
                    Logger.debug(TAG, "error encrypting file", e);
                    return false;
                } finally {
                    from.delete();
                }
            }

            @Override
            protected void onPostExecute(Boolean res) {
                if (getActivity() != null) {
                    if (res) {
                        refreshContent();
                    } else {
                        Toast.makeText(getActivity(),
                                getString(R.string.unable_to_process_image), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveOrder(outState);
        savePictureFilename(outState);
    }
}
