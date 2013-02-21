package com.idamobile.vpb.courier.widget.login;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.FragmentActivity;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.controllers.LoginManager;
import com.idamobile.vpb.courier.navigation.NavigationController;
import com.idamobile.vpb.courier.navigation.NavigationControllerFactory;
import com.idamobile.vpb.courier.widget.dialogs.AlertDialogFactory;
import com.idamobile.vpb.courier.widget.dialogs.AlertDialogFragment;

public class LoginDialogFactory extends AlertDialogFactory {

    public interface LoginListener {
        void onLogin(int pin);
    }

    private LoginManager loginManager;
    private FragmentActivity activity;
    private LoginListener loginListener;
    private NavigationController navigationController;

    public LoginDialogFactory(FragmentActivity activity, String tag) {
        super(activity, tag);
        this.activity = activity;
        loginManager = ((CoreApplication) activity.getApplication()).getMediator().getLoginManager();
        navigationController = NavigationControllerFactory.getInstance().createController(activity);
        setPosButtonListener(createPosButtonListener());
        setNegButtonListener(createNegButtonListener());
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    private OnClickListener createNegButtonListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loginManager.logout();
                navigationController.processSignOut();
            }
        };
    }

    private OnClickListener createPosButtonListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PinDialog pinDialog = findDialog();
                loginListener.onLogin(pinDialog.getPin());
            }
        };
    }

    @Override
    protected AlertDialogFragment createDialogFragment() {
        return PinDialog.newInstance(activity);
    }

}