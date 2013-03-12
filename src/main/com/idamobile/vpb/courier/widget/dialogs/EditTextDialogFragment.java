package com.idamobile.vpb.courier.widget.dialogs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.idamobile.vpb.courier.util.Keyboard;

public class EditTextDialogFragment extends AlertDialogFragment {

    public static final String EDITTEXT_LAYOUT_RES_ID = "edittext-layout-res-id";
    public static final String EDITTEXT_TEXT = "edittext-text";

    public static EditTextDialogFragment newInstance(CharSequence title, CharSequence message, CharSequence posButtonText,
                                                  CharSequence neutralButtonText, CharSequence negButtonText,
                                                  int editTextLayoutResId, CharSequence text) {
        return newInstance(title, message, posButtonText, neutralButtonText,
                negButtonText, null, null, editTextLayoutResId, text);
    }

    public static EditTextDialogFragment newInstance(CharSequence title, CharSequence message, CharSequence posButtonText,
                                                  CharSequence neutralButtonText, CharSequence negButtonText, Integer widht,
                                                  Integer height, int editTextLayoutResId, CharSequence text) {
        Bundle args = getBundle(title, message, posButtonText, neutralButtonText, negButtonText, widht, height, false);
        args.putInt(EDITTEXT_LAYOUT_RES_ID, editTextLayoutResId);
        args.putCharSequence(EDITTEXT_TEXT, text);

        EditTextDialogFragment result = new EditTextDialogFragment();
        result.setArguments(args);
        return result;
    }

    private EditText editText;
    private Handler handler = new Handler();

    @Override
    protected AlertDialog.Builder buildDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = super.buildDialog(savedInstanceState);

        int layout = getArguments().getInt(EDITTEXT_LAYOUT_RES_ID, -1);
        if (layout == -1) {
            throw new IllegalArgumentException("you must provide layout res for " + getClass().getSimpleName());
        }

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(layout, null);
        editText = findEditText(view);
        if (editText != null) {
            CharSequence text = getArguments().getCharSequence(EDITTEXT_TEXT);
            editText.setText(text);
            if (!TextUtils.isEmpty(text)) {
                editText.setSelection(text.length());
            }
            Keyboard.showKeyboard(editText, handler);
        }

        builder.setView(view);
        return builder;
    }

    private EditText findEditText(View view) {
        if (view instanceof EditText) {
            return (EditText) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                EditText res = findEditText(child);
                if (res != null) {
                    return res;
                }
            }
        }
        return null;
    }

    public CharSequence getText() {
        if (editText != null) {
            return editText.getText();
        } else {
            return null;
        }
    }

    public void setText(CharSequence text) {
        if (editText != null) {
            editText.setText(text);
        }
    }
}
