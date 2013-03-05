package com.idamobile.vpb.courier.widget.dialogs;


import android.content.DialogInterface.OnCancelListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.text.NumberFormat;

public class ProgressDialogFactory extends AbstractDialogFactory {

    private CharSequence title;
    private CharSequence message;
    private NumberFormat percentProgressFormat;
    private String numberFormat;
    private boolean spinner = true;
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
        ProgressDialogFragment dialog = findDialog();
        if (dialog != null) {
            dialog.setTitle(title);
        }
        return this;
    }

    public ProgressDialogFactory setMessage(CharSequence message) {
        this.message = message;
        ProgressDialogFragment dialog = findDialog();
        if (dialog != null) {
            dialog.setMessage(message);
        }
        return this;
    }

    public ProgressDialogFactory setPercentProgressFormat(NumberFormat percentProgressFormat) {
        this.percentProgressFormat = percentProgressFormat;
        return this;
    }

    public ProgressDialogFactory setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
        return this;
    }

    public ProgressDialogFactory setSpinner(boolean spinner) {
        this.spinner = spinner;
        return this;
    }

    public void setMax(int max) {
        ProgressDialogFragment dialog = findDialog();
        if (dialog != null) {
            dialog.setMax(max);
        }
    }

    public void setProgress(int progress) {
        ProgressDialogFragment dialog = findDialog();
        if (dialog != null) {
            dialog.setProgress(progress);
        }
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
        ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(
                title, message, spinner, numberFormat, percentProgressFormat);
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