package com.idamobile.vpb.courier.widget.dialogs;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class EditTextDialogFactory extends AlertDialogFactory {

    private int editTextLayoutResId;
    private CharSequence text;

    public EditTextDialogFactory(FragmentManager manager, String tag) {
        super(manager, tag);
    }

    public EditTextDialogFactory(FragmentActivity activity, String tag) {
        super(activity, tag);
    }

    public int getEditTextLayoutResId() {
        return editTextLayoutResId;
    }

    public EditTextDialogFactory setEditTextLayoutResId(int editTextLayoutResId) {
        this.editTextLayoutResId = editTextLayoutResId;
        return this;
    }

    @Override
    protected AlertDialogFragment createDialogFragment() {
        return EditTextDialogFragment.newInstance(title, message,
                posButtonText, neutButtonText, negButtonText,
                dialogWidht == DIMENSION_NOT_SET ? null : dialogWidht,
                dialogHeight == DIMENSION_NOT_SET ? null : dialogHeight,
                editTextLayoutResId, text);
    }

    public CharSequence getText() {
        EditTextDialogFragment dialogFragment = findDialog();
        if (dialogFragment != null) {
            CharSequence text = dialogFragment.getText();
            return text != null ? text : "";
        } else {
            return "";
        }
    }

    public EditTextDialogFactory setText(CharSequence text) {
        this.text = text;
        setText(this.<EditTextDialogFragment>findDialog());
        return this;
    }

    private void setText(EditTextDialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.setText(text);
        }
    }
}
