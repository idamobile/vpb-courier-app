package com.idamobile.vpb.courier.widget.orders;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.CancellationReason;
import com.idamobile.vpb.courier.widget.dialogs.AlertDialogFactory;

public class OrderStatusPresenter {

    private FragmentActivity fragmentActivity;
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

    public OrderStatusPresenter(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;

        createDialogs();
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
                fragmentActivity.getString(R.string.met_result_cannot_bi_signed),
                fragmentActivity.getString(R.string.client_rejected_order)
        };
        ArrayAdapter<String> metResultAdapter = new ArrayAdapter<String>(fragmentActivity,
                android.R.layout.select_dialog_item, metResultVariants);
        metResultChooserDialog = new AlertDialogFactory(fragmentActivity, "met-result-chooser-dialog");
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
            }
        });

        orderFinalOptionsAdapter = new OrderFinalOptionsAdapter();
        finalOptionsDialog = new AlertDialogFactory(fragmentActivity, "final-oprions-dialog");
        finalOptionsDialog.setTitle(fragmentActivity.getString(R.string.final_options_dialog_title));
        finalOptionsDialog.setCancellable(true);
        finalOptionsDialog.setNegButtonText(fragmentActivity.getText(android.R.string.cancel));
        finalOptionsDialog.setPosButton(fragmentActivity.getText(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resident = orderFinalOptionsAdapter.getItem(OrderFinalOptionsAdapter.RESIDENT_OPTION_INDEX);
                        hasMarks = orderFinalOptionsAdapter.getItem(OrderFinalOptionsAdapter.MARKS_OPTION_INDEX);
                        showConfingsubmitOrderDialog();
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
    }

    private void showConfingRejectOrderDialog() {
        confirmRejectDialog.setMessage(fragmentActivity.getString(
                R.string.confirm_reject_dialog_message_format,
                fragmentActivity.getString(cancellationReason.strResId)));
        confirmRejectDialog.showDialog();
    }

    private void showConfingsubmitOrderDialog() {
        confirmSubmitDialog.setMessage(fragmentActivity.getString(
                R.string.confirm_submit_dialog_message_format,
                fragmentActivity.getString(resident ? R.string.yes : R.string.no),
                fragmentActivity.getString(hasMarks ? R.string.yes : R.string.no)));
        confirmSubmitDialog.showDialog();
    }

    private void rejectOrder(CancellationReason reason, boolean metWithClient) {

    }

    private void confirmOrder(boolean resident, boolean hasMarks) {

    }

    public void showRejectOrderDialog() {
        reset();
        rejectOrderDialog.showDialog();
    }

    public void showMetWithClientDialog() {
        reset();
        metResultChooserDialog.showDialog();
    }

    private void reset() {
        metWithClient = false;
        cancellationReason = null;
        orderFinalOptionsAdapter.reset();
    }
}
