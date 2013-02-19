package com.idamobile.vpb.courier.widget.dialogs;


import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public abstract class AbstractDialogFactory implements DialogFactory {

    private String tag;
    private FragmentManager manager;
    private Handler handler;

    public AbstractDialogFactory(String tag, FragmentManager manager) {
        this.tag = tag;
        this.manager = manager;
        this.handler = new Handler();
    }

    @Override
    public String getTag() {
        return tag;
    }

    public FragmentManager getManager() {
        return manager;
    }

    @Override
    public void showDialog() {
        if (findDialog() != null) {
            hideDialog();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Dialogs.show(manager, newDialog(), tag);
                }
            });
        } else {
            Dialogs.show(manager, newDialog(), tag);
        }
    }

    @Override
    public void hideDialog() {
        Dialogs.dismiss(manager, tag);
    }

    @SuppressWarnings("unchecked")
    public <T extends DialogFragment> T findDialog() {
        return (T) getManager().findFragmentByTag(tag);
    }
}
