package com.idamobile.vpb.courier.widget.dialogs;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.BaseAdapter;

public class AlertDialogFactory extends AbstractDialogFactory {

    public static int DIMENSION_NOT_SET = Integer.MIN_VALUE;

    protected CharSequence title;
    protected CharSequence message;
    protected CharSequence posButtonText;
    protected CharSequence neutButtonText;
    protected CharSequence negButtonText;
    protected int dialogWidht = DIMENSION_NOT_SET;
    protected int dialogHeight = DIMENSION_NOT_SET;

    private DialogInterface.OnCancelListener cancelListener;
    private DialogInterface.OnClickListener posButtonListener;
    private DialogInterface.OnClickListener neutButtonListener;
    private DialogInterface.OnClickListener negButtonListener;

    private BaseAdapter listAdapter;
    private DialogInterface.OnClickListener itemClickListener;

    private boolean cancellable;

    public AlertDialogFactory(FragmentActivity activity, String tag) {
        this(activity.getSupportFragmentManager(), tag);
    }

    public AlertDialogFactory(FragmentManager manager, String tag) {
        super(tag, manager);
    }

    public AlertDialogFactory setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public AlertDialogFactory setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
        return this;
    }

    public AlertDialogFactory setMessage(CharSequence message) {
        this.message = message;
        return this;
    }

    public AlertDialogFactory setLayout(int widht, int height) {
        this.dialogWidht = widht;
        this.dialogHeight = height;
        return this;
    }

    public AlertDialogFactory setListAdapter(BaseAdapter listAdapter) {
        this.listAdapter = listAdapter;
        setListAdapter(this.<AlertDialogFragment>findDialog());
        return this;
    }

    public AlertDialogFactory setOnItemClickListener(DialogInterface.OnClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        setOnItemClickListener(this.<AlertDialogFragment>findDialog());
        return this;
    }

    public AlertDialogFactory setCancelListener(OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        setCancelListener(this.<AlertDialogFragment>findDialog());
        return this;
    }

    public AlertDialogFactory setPosButton(CharSequence posButtonText, OnClickListener listener) {
        return setPosButtonText(posButtonText).setPosButtonListener(listener);
    }

    public AlertDialogFactory setNeutButton(CharSequence neutButtonText, OnClickListener listener) {
        return setNeutButtonText(neutButtonText).setNeutButtonListener(listener);
    }

    public AlertDialogFactory setNegButton(CharSequence negButtonText, OnClickListener listener) {
        return setNegButtonText(negButtonText).setNegButtonListener(listener);
    }

    public AlertDialogFactory setPosButtonText(CharSequence posButtonText) {
        this.posButtonText = posButtonText;
        return this;
    }

    public AlertDialogFactory setNeutButtonText(CharSequence neutButtonText) {
        this.neutButtonText = neutButtonText;
        return this;
    }

    public AlertDialogFactory setNegButtonText(CharSequence negButtonText) {
        this.negButtonText = negButtonText;
        return this;
    }

    public AlertDialogFactory setPosButtonListener(DialogInterface.OnClickListener posButtonListener) {
        this.posButtonListener = posButtonListener;
        setPosListener(this.<AlertDialogFragment>findDialog());
        return this;
    }

    public AlertDialogFactory setNeutButtonListener(DialogInterface.OnClickListener neutButtonListener) {
        this.neutButtonListener = neutButtonListener;
        setNeutListener(this.<AlertDialogFragment>findDialog());
        return this;
    }

    public AlertDialogFactory setNegButtonListener(DialogInterface.OnClickListener negButtonListener) {
        this.negButtonListener = negButtonListener;
        setNegListener(this.<AlertDialogFragment>findDialog());
        return this;
    }

    @Override
    public DialogFragment newDialog() {
        AlertDialogFragment dialogFragment = createDialogFragment();
        setup(dialogFragment);
        return dialogFragment;
    }

    protected AlertDialogFragment createDialogFragment() {
        return AlertDialogFragment.newInstance(title, message,
                posButtonText, neutButtonText, negButtonText,
                dialogWidht == DIMENSION_NOT_SET ? null : dialogWidht,
                dialogHeight == DIMENSION_NOT_SET ? null : dialogHeight,
                listAdapter != null);
    }

    protected void setup(AlertDialogFragment dialogFragment) {
        if (listAdapter != null) {
            dialogFragment.setListAdapter(listAdapter);
        }
        setCancelListener(dialogFragment);
        setPosListener(dialogFragment);
        setNeutListener(dialogFragment);
        setNegListener(dialogFragment);
        setOnItemClickListener(dialogFragment);
    }

    private void setOnItemClickListener(AlertDialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.setOnItemClickListener(itemClickListener);
        }
    }

    private void setCancelListener(AlertDialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.setCancelable(cancellable);
            dialogFragment.setCancelListener(cancelListener);
        }
    }

    private void setPosListener(AlertDialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.setPosClickListener(posButtonListener);
        }
    }

    private void setNeutListener(AlertDialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.setNeutClickListener(neutButtonListener);
        }
    }

    private void setNegListener(AlertDialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.setNegClickListener(negButtonListener);
        }
    }

    private void setListAdapter(AlertDialogFragment dialogFragment) {
        if (dialogFragment != null && !dialogFragment.isListAdapterSet()) {
            dialogFragment.setListAdapter(listAdapter);
        }
    }

}
