package com.idamobile.vpb.courier.widget.dialogs;


import android.content.DialogInterface.OnCancelListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class ProgressDialogFactory extends AbstractDialogFactory {

    private CharSequence title;
    private CharSequence message;
    private OnCancelListener cancelListener;
    private boolean cancellable;

    public ProgressDialogFactory(FragmentActivity activity, String tag) {
        this(activity.getSupportFragmentManager(), tag);
    }

    public ProgressDialogFactory(FragmentManager manager, String tag) {
        super(tag, manager);
    }

    public ProgressDialogFactory setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public ProgressDialogFactory setMessage(CharSequence message) {
        this.message = message;
        return this;
    }

    public ProgressDialogFactory setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
        return this;
    }

    public ProgressDialogFactory setCancelListener(OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        if (cancelListener != null) {
            setCancellable(true);
        }

        ProgressDialogFragment fragment = findDialog();
        setCancleListener(fragment);

        return this;
    }

    @Override
    public DialogFragment newDialog() {
        ProgressDialogFragment dialogFragment =
                ProgressDialogFragment.newInstance(title, message);
        setCancleListener(dialogFragment);
        return dialogFragment;
    }

    private void setCancleListener(ProgressDialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.setCancelable(cancellable);
            dialogFragment.setCancelListener(cancelListener);
        }
    }

}