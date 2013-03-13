package com.idamobile.vpb.courier.widget.orders;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.controllers.AwardManager;
import com.idamobile.vpb.courier.model.CancellationReason;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.network.core.Request;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.core.ResponseDTO;
import com.idamobile.vpb.courier.network.core.ResultCodeToMessageConverter;
import com.idamobile.vpb.courier.network.orders.UpdateOrderResponse;
import com.idamobile.vpb.courier.widget.dialogs.AlertDialogFactory;
import com.idamobile.vpb.courier.widget.dialogs.DialogRequestListener;
import com.idamobile.vpb.courier.widget.dialogs.ProgressDialogFactory;

public class OrderStatusPresenter {

    public static final String STATUS_PRESENTER_BUNDLE_EXTRA = "status-presenter";
    public static final String ORDER_EXTRA = "order";

    private FragmentActivity fragmentActivity;
    private ApplicationMediator mediator;
    private AlertDialogFactory rejectOrderDialog;
    private OrderCloseReasonsAdapter rejectReasonsAdapter;
    private AlertDialogFactory confirmRejectDialog;

    private AlertDialogFactory metResultChooserDialog;
    private OrderFinalOptionsAdapter orderFinalOptionsAdapter;
    private AlertDialogFactory finalOptionsDialog;
    private AlertDialogFactory confirmSubmitDialog;

    private CancellationReason cancellationReason;
    private boolean metWithClient;
    private boolean hasMarks;
    private boolean resident;

    private ResultCodeToMessageConverter messageConverter;

    private RequestWatcherCallbacks<UpdateOrderResponse> updateOrderCallbacks;
    private ProgressDialogFactory progressDialog;
    private Order order;

    public OrderStatusPresenter(FragmentActivity fragmentActivity, Bundle savedInstanceState) {
        this.fragmentActivity = fragmentActivity;
        this.messageConverter = new ResultCodeToMessageConverter(fragmentActivity);
        this.mediator = CoreApplication.getMediator(fragmentActivity);

        Bundle bundle = null;
        if (savedInstanceState != null) {
            bundle = savedInstanceState.getBundle(STATUS_PRESENTER_BUNDLE_EXTRA);
            if (bundle != null) {
                order = (Order) bundle.get(ORDER_EXTRA);
            }

        }
        createDialogs();
        createCallbacks(bundle);
    }

    private void createCallbacks(Bundle savedInstanceState) {
        updateOrderCallbacks = new RequestWatcherCallbacks<UpdateOrderResponse>(
                fragmentActivity, "order-status-update", savedInstanceState);
        updateOrderCallbacks.registerListener(new RequestWatcherCallbacks.SimpleRequestListener<UpdateOrderResponse>(){
            @Override
            public void onSuccess(Request<UpdateOrderResponse> request, ResponseDTO<UpdateOrderResponse> result) {
                AwardManager awardManager = mediator.getAwardManager();
                OrderStatus newStatus = result.getData().getNewStatus();
                if (newStatus == OrderStatus.STATUS_DOCUMENTS_SUBMITTED) {
                    awardManager.onOrderCompleted();
                } else if (newStatus == OrderStatus.STATUS_DOCUMENTS_NOT_SUBMITTED) {
                    awardManager.onOrderCancelled(cancellationReason, metWithClient);
                }
            }

            @Override
            public void onError(Request<UpdateOrderResponse> request, ResponseDTO<UpdateOrderResponse> result) {
                messageConverter.showToast(result.getResultCode());
            }
        });
        updateOrderCallbacks.registerListener(new DialogRequestListener<UpdateOrderResponse>(progressDialog));
    }

    private void createDialogs() {
        rejectOrderDialog = new AlertDialogFactory(fragmentActivity, "reject-order-dialog");
        rejectOrderDialog.setNegButtonText(fragmentActivity.getText(android.R.string.cancel));
        rejectOrderDialog.setCancellable(true);
        rejectOrderDialog.setTitle(fragmentActivity.getString(R.string.reject_order_dialog_title));
        rejectReasonsAdapter = new OrderCloseReasonsAdapter();
        rejectReasonsAdapter.setItems(CancellationReason.CLIENT_DID_NOT_ANSWER_THE_PHONE,
                CancellationReason.CLIENT_MISSED_MEETING,
                CancellationReason.COURIER_MISSED_MEETING,
                CancellationReason.CLIENT_FORGOT_PASSPORT,
                CancellationReason.CLIENT_REJECTED_ORDER);
        rejectOrderDialog.setListAdapter(rejectReasonsAdapter);
        rejectOrderDialog.setOnItemClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancellationReason = rejectReasonsAdapter.getItem(which);
                showConfingRejectOrderDialog();
                rejectOrderDialog.hideDialog();
            }
        });

        confirmRejectDialog = new AlertDialogFactory(fragmentActivity, "confirm-reject-dialog");
        confirmRejectDialog.setTitle(fragmentActivity.getString(R.string.confirm_dialog_title));
        confirmRejectDialog.setPosButton(fragmentActivity.getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rejectOrder(cancellationReason, metWithClient);
            }
        });
        confirmRejectDialog.setNegButtonText(fragmentActivity.getText(android.R.string.cancel));
        confirmRejectDialog.setCancellable(true);

        String[] metResultVariants = {
                fragmentActivity.getString(R.string.met_result_documents_ok),
                fragmentActivity.getString(R.string.met_result_cannot_be_signed),
                fragmentActivity.getString(R.string.client_rejected_order)
        };
        ArrayAdapter<String> metResultAdapter = new ArrayAdapter<String>(fragmentActivity,
                android.R.layout.select_dialog_item, metResultVariants);
        metResultChooserDialog = new AlertDialogFactory(fragmentActivity, "met-result-chooser-dialog");
        metResultChooserDialog.setTitle(fragmentActivity.getString(R.string.met_result_chooser_dialog_title));
        metResultChooserDialog.setCancellable(true);
        metResultChooserDialog.setNegButtonText(fragmentActivity.getText(android.R.string.cancel));
        metResultChooserDialog.setListAdapter(metResultAdapter);
        metResultChooserDialog.setOnItemClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                metWithClient = true;
                switch (which) {
                    case 0:
                        finalOptionsDialog.showDialog();
                        break;
                    case 1:
                        cancellationReason = CancellationReason.AGREEMENT_CAN_NOT_BE_SIGNED;
                        showConfingRejectOrderDialog();
                        break;
                    case 2:
                        cancellationReason = CancellationReason.CLIENT_REJECTED_ORDER;
                        showConfingRejectOrderDialog();
                        break;
                    default:
                        throw new IllegalArgumentException("unknown item pos: " + which);
                }
                metResultChooserDialog.hideDialog();
            }
        });

        orderFinalOptionsAdapter = new OrderFinalOptionsAdapter();
        finalOptionsDialog = new AlertDialogFactory(fragmentActivity, "final-oprions-dialog");
        finalOptionsDialog.setTitle(fragmentActivity.getString(R.string.final_options_dialog_title));
        finalOptionsDialog.setCancellable(true);
        finalOptionsDialog.setListAdapter(orderFinalOptionsAdapter);
        finalOptionsDialog.setNegButtonText(fragmentActivity.getText(android.R.string.cancel));
        finalOptionsDialog.setPosButton(fragmentActivity.getText(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resident = orderFinalOptionsAdapter.getItem(OrderFinalOptionsAdapter.RESIDENT_OPTION_INDEX);
                        hasMarks = orderFinalOptionsAdapter.getItem(OrderFinalOptionsAdapter.MARKS_OPTION_INDEX);
                        showConfirmSubmitOrderDialog();
                    }
                });
        finalOptionsDialog.setOnItemClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                orderFinalOptionsAdapter.toggle(which);
            }
        });

        confirmSubmitDialog = new AlertDialogFactory(fragmentActivity, "confirm-submit-dialog");
        confirmSubmitDialog.setTitle(fragmentActivity.getString(R.string.confirm_dialog_title));
        confirmSubmitDialog.setPosButton(fragmentActivity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmOrder(resident, hasMarks);
            }
        });
        confirmSubmitDialog.setNegButtonText(fragmentActivity.getText(android.R.string.cancel));
        confirmSubmitDialog.setCancellable(true);

        progressDialog = new ProgressDialogFactory(fragmentActivity, "update-order-progress-dialog");
        progressDialog.setMessage(fragmentActivity.getString(R.string.update_order_progess_dialog_message));
        progressDialog.setCancellable(false);
    }

    private void showConfingRejectOrderDialog() {
        confirmRejectDialog.setMessage(fragmentActivity.getString(
                R.string.confirm_reject_dialog_message_format,
                fragmentActivity.getString(metWithClient ? R.string.met_with_client : R.string.not_met_with_client),
                fragmentActivity.getString(cancellationReason.strResId).toLowerCase()));
        confirmRejectDialog.showDialog();
    }

    private void showConfirmSubmitOrderDialog() {
        confirmSubmitDialog.setMessage(fragmentActivity.getString(
                R.string.confirm_submit_dialog_message_format,
                fragmentActivity.getString(resident ? R.string.yes : R.string.no).toLowerCase(),
                fragmentActivity.getString(hasMarks ? R.string.yes : R.string.no).toLowerCase()));
        confirmSubmitDialog.showDialog();
    }

    private void rejectOrder(CancellationReason reason, boolean metWithClient) {
        mediator.getOrdersManager().requestSetOrderRejected(
                order.getId(), metWithClient, reason, updateOrderCallbacks);
    }

    private void confirmOrder(boolean resident, boolean hasMarks) {
        mediator.getOrdersManager().requestSetOrderCompleted(
                order.getId(), resident, hasMarks, updateOrderCallbacks);
    }

    public void activateCard(Order order) {
        reset();
        this.order = order;
        mediator.getOrdersManager().requestActivateCard(order.getId(), updateOrderCallbacks);
    }

    public void showRejectOrderDialog(Order order) {
        reset();
        this.order = order;
        rejectOrderDialog.showDialog();
    }

    public void showMetWithClientDialog(Order order) {
        reset();
        this.order = order;
        metResultChooserDialog.showDialog();
    }

    private void reset() {
        metWithClient = false;
        resident = true;
        hasMarks = false;
        cancellationReason = null;
        order = null;
        orderFinalOptionsAdapter.reset();
    }

    public void saveState(Bundle outState) {
        Bundle bundle = new Bundle();
        updateOrderCallbacks.saveInstanceState(bundle);
        if (order != null) {
            bundle.putSerializable(ORDER_EXTRA, order);
        }
        outState.putBundle(STATUS_PRESENTER_BUNDLE_EXTRA, bundle);
    }
}
