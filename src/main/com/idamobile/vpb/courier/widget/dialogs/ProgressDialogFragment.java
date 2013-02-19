package com.idamobile.vpb.courier.widget.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {

    private OnCancelListener cancelListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence title = getArguments().getCharSequence("title");
        CharSequence message = getArguments().getCharSequence("message");

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (message != null) {
            dialog.setMessage(message);
        }
        if (title != null) {
            dialog.setTitle(title);
        }
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (cancelListener != null) {
            cancelListener.onCancel(dialog);
        }
    }

    public void setCancelListener(OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public static ProgressDialogFragment newInstance(
            CharSequence title, CharSequence message) {
        ProgressDialogFragment result = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putCharSequence("title", title);
        args.putCharSequence("message", message);
        result.setArguments(args);
        return result;
    }

}