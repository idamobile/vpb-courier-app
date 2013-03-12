package com.idamobile.vpb.courier.widget.orders;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.controllers.OrdersManager;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderNote;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.widget.dialogs.EditTextDialogFactory;

public class OrderNotePresenter {

    private final OrdersManager ordersManager;

    public interface OrderNoteListener {
        void onOrderNoteChanged(OrderNote orderNote);
    }

    private EditTextDialogFactory editTextDialogFactory;
    private FragmentActivity fragmentActivity;

    private TextView noteTextView;
    private View editNoteButton;

    private Order order;
    private OrderNote orderNote;
    private OrderNoteListener orderNoteListener;

    private AsyncTask<?, ?, ?> noteLoader;

    public OrderNotePresenter(FragmentActivity fragmentActivity, TextView noteTextView, View editNoteButton) {
        this.fragmentActivity = fragmentActivity;
        this.ordersManager = CoreApplication.getMediator(fragmentActivity).getOrdersManager();
        this.noteTextView = noteTextView;
        this.editNoteButton = editNoteButton;

        editNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });

        createDialogs();
    }

    public void setOrder(Order order) {
        if (order != this.order) {
            cancelLoad();
            this.order = order;
            if (order != null) {
                DataHolder<OrderNote> holder = ordersManager.getNoteDataHolder(order.getId());
                if (holder.isEmpty()) {
                    loadNote();
                } else {
                    setOrderNote(holder.get());
                }
            } else {
                this.orderNote = null;
            }
            refresh();
        }
    }

    private void loadNote() {
        this.orderNote = null;
        cancelLoad();
        noteLoader = new AsyncTask<Void, Void, OrderNote>() {
            @Override
            protected OrderNote doInBackground(Void... params) {
                return ordersManager.loadNoteFor(order.getId());
            }

            @Override
            protected void onPostExecute(OrderNote orderNote) {
                setOrderNote(orderNote);
            }
        }.execute();
    }

    private void setOrderNote(OrderNote orderNote) {
        this.orderNote = orderNote;
        refresh();
    }

    private void refresh() {
        if (orderNote != null) {
            String note = orderNote.getNote();
            if (TextUtils.isEmpty(note)) {
                noteTextView.setText(R.string.order_note_empty);
            } else {
                noteTextView.setText(note);
            }
            editNoteButton.setEnabled(true);
        } else {
            noteTextView.setText(R.string.order_note_empty);
            editNoteButton.setEnabled(false);
        }
    }

    private void cancelLoad() {
        if (noteLoader != null) {
            noteLoader.cancel(true);
        }
    }

    public void setOrderNoteListener(OrderNoteListener orderNoteListener) {
        this.orderNoteListener = orderNoteListener;
    }

    private void createDialogs() {
        editTextDialogFactory = new EditTextDialogFactory(fragmentActivity, "order-note-edit-dialog");
        editTextDialogFactory.setCancellable(true);
        editTextDialogFactory.setTitle(fragmentActivity.getString(R.string.order_note_dialog_title));
        editTextDialogFactory.setEditTextLayoutResId(R.layout.order_note_edittext_dialog_layout);
        editTextDialogFactory.setPosButton(fragmentActivity.getText(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                orderNote.setNote(editTextDialogFactory.getText().toString());
                refresh();
                saveNote();
                if (orderNoteListener != null) {
                    orderNoteListener.onOrderNoteChanged(orderNote);
                }
            }
        });
        editTextDialogFactory.setNegButtonText(fragmentActivity.getText(android.R.string.cancel));
    }

    private void saveNote() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return ordersManager.saveNote(orderNote);
            }

            @Override
            protected void onPostExecute(Boolean res) {
                if (!res) {
                    Toast.makeText(fragmentActivity,
                            fragmentActivity.getString(R.string.order_note_save_failed),
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    public void edit() {
        editTextDialogFactory.setText(orderNote.getNote());
        editTextDialogFactory.showDialog();
    }
}
