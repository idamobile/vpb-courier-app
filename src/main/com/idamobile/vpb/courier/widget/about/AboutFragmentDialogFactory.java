package com.idamobile.vpb.courier.widget.about;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import com.idamobile.vpb.courier.widget.dialogs.AbstractDialogFactory;

public class AboutFragmentDialogFactory extends AbstractDialogFactory {

    private FragmentActivity activity;

    public AboutFragmentDialogFactory(FragmentActivity activity, String tag) {
        super(tag, activity.getSupportFragmentManager());
        this.activity = activity;
    }

    @Override
    public DialogFragment newDialog() {
        return AboutDialogFragment.newInstance(activity);
    }

}
