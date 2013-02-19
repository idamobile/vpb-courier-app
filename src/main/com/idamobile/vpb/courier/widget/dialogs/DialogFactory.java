package com.idamobile.vpb.courier.widget.dialogs;


import android.support.v4.app.DialogFragment;

public interface DialogFactory {

    DialogFragment newDialog();

    void showDialog();

    void hideDialog();

    String getTag();

}
