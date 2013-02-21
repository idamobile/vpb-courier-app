package com.idamobile.vpb.courier.widget.login;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.util.Keyboard;
import com.idamobile.vpb.courier.util.Logger;

/**
 * @author colriot
 * @since Aug 30, 2012
 *
 */
public class PinWidget extends LinearLayout {

    private static final String TAG = PinWidget.class.getSimpleName();

    private PinDigitEditText firstView;
    private PinDigitEditText lastView;
    private OnSubmitListener mSubmitListener;

    public PinWidget(Context context) {
        super(context);
        init(context);
    }

    public PinWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PinWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.pin_widget_view, this, true);

        firstView = (PinDigitEditText) findViewById(R.id.first_field);
        lastView = (PinDigitEditText) findViewById(R.id.last_field);
        lastView.setParent(this);
    }

    public boolean allDigitsInputed() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof EditText) {
                if (TextUtils.isEmpty(((EditText) view).getText())) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getPin() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof EditText) {
                sb.append(((EditText) view).getText().toString());
            }
        }

        try {
            return Integer.parseInt(sb.toString());
        } catch (Exception ex) {
            Logger.warn(TAG, "error parsing login", ex);
            return -1;
        }
    }

    public void clear() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }
        }
        firstView.requestFocus();
    }

    void submit() {
        if (mSubmitListener != null && allDigitsInputed()) {
            boolean res = mSubmitListener.submit(getPin());
            if (res) {
                Keyboard.hideKeyboard(lastView);
            }
        }
    }

    public void setOnSubmitListener(OnSubmitListener submitListener) {
        mSubmitListener = submitListener;
    }

    public interface OnSubmitListener {
        /**
         * @param pin
         *            inputed login
         * @return true to hide keyboard, false otherwise
         */
        boolean submit(int pin);
    }

    public void showKeyboard() {
        firstView.requestFocus();
        Keyboard.showKeyboard(firstView);
    }
}
