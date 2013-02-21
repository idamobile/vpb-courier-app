package com.idamobile.vpb.courier.widget.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.widget.dialogs.AlertDialogFragment;

public class PinDialog extends AlertDialogFragment {

    private PinWidget pinWidget;
    private DialogInterface.OnClickListener posClickListener;

    public static PinDialog newInstance(Context context) {
        return newInstance(context.getString(R.string.login_dialog_title),
                context.getString(R.string.login_dialog_message),
                /*for login we hide login button context.getString(R.string.login_dialog_login_button)*/ null,
                null,
                context.getString(R.string.login_dialog_logout_button));
    }

    public static PinDialog newInstance(CharSequence title, CharSequence message, CharSequence posButtonText,
            CharSequence neutralButtonText, CharSequence negButtonText) {
        Bundle args = new Bundle();
        args.putCharSequence("title", title);
        args.putCharSequence("message", message);
        args.putCharSequence("posButton", posButtonText);
        args.putCharSequence("neutButton", neutralButtonText);
        args.putCharSequence("negButton", negButtonText);

        PinDialog result = new PinDialog();
        result.setArguments(args);
        return result;
    }

    @Override
    protected AlertDialog.Builder buildDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = super.buildDialog(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View fieldsView = inflater.inflate(R.layout.pin_dialog_view, null);
        pinWidget = (PinWidget) fieldsView.findViewById(R.id.widget_pin);
        pinWidget.setOnSubmitListener(new PinWidget.OnSubmitListener() {
            @Override
            public boolean submit(int pin) {
                if (posClickListener != null) {
                    posClickListener.onClick(getDialog(), AlertDialog.BUTTON_POSITIVE);
                    dismiss();
                    return true;
                } else {
                    return false;
                }
            }
        });

        pinWidget.postDelayed(new Runnable() {
            @Override
            public void run() {
                pinWidget.showKeyboard();
            }
        }, 500);

        builder.setView(fieldsView);
        return builder;
    }

    @Override
    public void setPosClickListener(DialogInterface.OnClickListener posClickListener) {
        this.posClickListener = posClickListener;
        super.setPosClickListener(posClickListener);
    }

    public int getPin() {
        return pinWidget != null ? pinWidget.getPin() : -1;
    }

}