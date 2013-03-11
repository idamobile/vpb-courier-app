package com.idamobile.vpb.courier.widget.orders.images;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.controllers.ImageManager;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.network.core.*;
import com.idamobile.vpb.courier.network.images.ImageInfo;
import com.idamobile.vpb.courier.network.images.OrderImages;
import com.idamobile.vpb.courier.security.SecuredActivity;
import com.idamobile.vpb.courier.widget.dialogs.DialogRequestListener;
import com.idamobile.vpb.courier.widget.dialogs.ProgressDialogFactory;

import java.util.List;
import java.util.Map;

public class OrdersImageUploader {

    public interface OnImageStatusChangedListener {
        void onImagesChanged();
    }

    private ApplicationMediator mediator;
    private FragmentActivity fragmentActivity;
    private ProgressDialogFactory progressDialog;
    private RequestWatcherCallbacks<RequestGroup.ModelCollection> watcherCallbacks;

    private ResultCodeToMessageConverter converter;

    private boolean imagesWatcherRegistered;
    private BroadcastReceiver imagesWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshProgressDialog();
            if (imageStatusChangedListener != null) {
                imageStatusChangedListener.onImagesChanged();
            }
        }
    };
    private boolean ordersWatcherRegistered;
    private BroadcastReceiver ordersWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshImageWatcherRegistration();
        }
    };

    private OnImageStatusChangedListener imageStatusChangedListener;

    public OrdersImageUploader(FragmentActivity fragmentActivity, Bundle savedInstanceState) {
        this.mediator = CoreApplication.getMediator(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
        this.converter = new ResultCodeToMessageConverter(fragmentActivity);

        createDialogs();
        createCallbacks(savedInstanceState);
    }

    public void setImageStatusChangedListener(OnImageStatusChangedListener imageStatusChangedListener) {
        this.imageStatusChangedListener = imageStatusChangedListener;
    }

    private void createDialogs() {
        progressDialog = new ProgressDialogFactory(fragmentActivity, "images-upload-progress-dialog");
        progressDialog.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                watcherCallbacks.cancel(true);
            }
        });
        progressDialog.setMessage(fragmentActivity.getText(R.string.upload_progress_dialog_message_connecting));
        progressDialog.setTitle(fragmentActivity.getText(R.string.upload_progress_dialog_title_format));
        progressDialog.setNumberFormat("%dKiB");
        progressDialog.setSpinner(false);
    }

    private void createCallbacks(Bundle savedInstanceState) {
        watcherCallbacks = new RequestWatcherCallbacks<RequestGroup.ModelCollection>(
                fragmentActivity, "image-uploader", savedInstanceState);
        watcherCallbacks.registerListener(new DialogRequestListener<RequestGroup.ModelCollection>(progressDialog));
        watcherCallbacks.registerListener(new RequestWatcherCallbacks.SimpleRequestListener<RequestGroup.ModelCollection>() {
            @Override
            public void onStarted(Request<RequestGroup.ModelCollection> request) {
                if (fragmentActivity instanceof SecuredActivity) {
                    ((SecuredActivity) fragmentActivity).pauseSecurity();
                }
            }

            @Override
            public void onSuccess(Request<RequestGroup.ModelCollection> request, ResponseDTO<RequestGroup.ModelCollection> result) {
                RequestGroup.ModelCollection data = result.getData();
                Map<String,ResponseDTO<?>> responseMap = data.getResponseMap();
                int total = responseMap.size();
                if (total > 0) {
                    int done = 0;
                    for (ResponseDTO<?> dto : responseMap.values()) {
                        if (dto.isSuccess()) {
                            done++;
                        }
                    }
                    Toast.makeText(fragmentActivity,
                            fragmentActivity.getString(R.string.uploaded_result_format, done, total),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Request<RequestGroup.ModelCollection> request, ResponseDTO<RequestGroup.ModelCollection> result) {
                converter.showToast(result.getResultCode());
            }

            @Override
            public void onFinished(Request<RequestGroup.ModelCollection> request) {
                if (fragmentActivity instanceof SecuredActivity) {
                    ((SecuredActivity) fragmentActivity).resumeSecurity();
                }
            }
        });
    }

    public void onPause() {
        if (imagesWatcherRegistered) {
            fragmentActivity.unregisterReceiver(imagesWatcher);
            imagesWatcherRegistered = false;
        }
        if (ordersWatcherRegistered) {
            fragmentActivity.unregisterReceiver(ordersWatcher);
            ordersWatcherRegistered = false;
        }
    }

    public void onResume() {
        refreshImageWatcherRegistration();
        if (!ordersWatcherRegistered) {
            mediator.getOrdersManager().registerForOrders(fragmentActivity, ordersWatcher);
            ordersWatcherRegistered = true;
        }
        refreshProgressDialog();
    }

    private void refreshImageWatcherRegistration() {
        if (imagesWatcherRegistered) {
            fragmentActivity.unregisterReceiver(imagesWatcher);
            imagesWatcherRegistered = false;
        }
        List<Order> orders = mediator.getOrdersManager().getOrders();
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.STATUS_ACTIVATED) {
                mediator.getImageManager().registerReceiver(fragmentActivity, imagesWatcher, order);
                imagesWatcherRegistered = true;
            }
        }
    }

    private void refreshProgressDialog() {
        ImageManager imageManager = mediator.getImageManager();

        boolean uploadingFound = false;
        int totalImages = 0;
        for (Order order : mediator.getOrdersManager().getOrders()) {
            if (order.getStatus() != OrderStatus.STATUS_ACTIVATED) {
                continue;
            }

            OrderImages orderImages = imageManager.getImages(order);
            if (orderImages != null) {
                for (ImageInfo imageInfo : orderImages.getImages()) {
                    if (!uploadingFound && imageInfo.isUploading()) {
                        String message = fragmentActivity.getString(R.string.upload_progress_dialog_message_format,
                                order.getImageType(imageInfo.getTypeId()).getDescription(),
                                order.getClientAddress());
                        progressDialog.setMessage(message);
                        progressDialog.setProgress((int) imageInfo.getUploadedBytes() / 1024);
                        progressDialog.setMax((int) (imageInfo.getTotalBytes() / 1024));
                        uploadingFound = true;
                    }

                    if (!imageManager.isUploaded(order.getId(), imageInfo.getTypeId())) {
                        totalImages++;
                    }
                }
            }
        }

        String title = fragmentActivity.getString(R.string.upload_progress_dialog_title_format, totalImages);
        progressDialog.setTitle(title);
        if (!uploadingFound) {
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.setMessage(fragmentActivity.getString(R.string.upload_progress_dialog_message_connecting));
        }
    }

    public void upload() {
        if (mediator.getImageManager().hasImagesToUpload()) {
            refreshProgressDialog();
            mediator.getImageManager().startUploadImages(watcherCallbacks);
        } else {
            Toast.makeText(fragmentActivity, fragmentActivity.getString(R.string.all_images_uploaded), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveState(Bundle outState) {
        watcherCallbacks.saveInstanceState(outState);
    }
}