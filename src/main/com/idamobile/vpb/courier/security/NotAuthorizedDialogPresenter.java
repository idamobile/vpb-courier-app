package com.idamobile.vpb.courier.security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.controllers.LoginManager;
import com.idamobile.vpb.courier.network.login.NotAuthorizedResponseProcessor;
import com.idamobile.vpb.courier.presenters.LoginPresenter;
import com.idamobile.vpb.courier.widget.login.LoginDialogFactory;

public class NotAuthorizedDialogPresenter extends Fragment {

    private static final String TAG = NotAuthorizedDialogPresenter.class.getSimpleName();

    private BroadcastReceiver notAuthorizedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showNotAuthorizedDialogIfNeeded();
        }
    };

    private LoginManager loginManager;
    private LoginDialogFactory loginDialogFactory;
    private LoginPresenter loginPresenter;

    private boolean paused = true;

    public static NotAuthorizedDialogPresenter attach(FragmentActivity fragmentActivity) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        NotAuthorizedDialogPresenter presenter = find(fragmentActivity);
        if (presenter == null) {
            presenter = new NotAuthorizedDialogPresenter();
            manager.beginTransaction()
            .add(presenter, TAG)
            .commitAllowingStateLoss();
        }
        return presenter;
    }

    public static void pause(FragmentActivity fragmentActivity) {
        NotAuthorizedDialogPresenter presenter = find(fragmentActivity);
        if (presenter != null) {
            presenter.pause();
        }
    }

    public static void resume(FragmentActivity fragmentActivity) {
        NotAuthorizedDialogPresenter presenter = find(fragmentActivity);
        if (presenter != null) {
            presenter.resume();
        }
    }

    public static NotAuthorizedDialogPresenter find(FragmentActivity fragmentActivity) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        return (NotAuthorizedDialogPresenter) manager.findFragmentByTag(TAG);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ApplicationMediator mediator = ((CoreApplication) getActivity().getApplication()).getMediator();
        loginManager = mediator.getLoginManager();

        loginPresenter = new LoginPresenter(getActivity(), savedInstanceState);
        loginPresenter.setLoginListener(new LoginPresenter.LoginListener() {
            @Override
            public void onSuccessfulLogin(LoginPresenter loginPresenter) {
            }

            @Override
            public void onLoginError(LoginPresenter loginPresenter) {
                loginDialogFactory.showDialog();
            }
        });

        loginDialogFactory = new LoginDialogFactory(getActivity(), "relogin-dialog");
        loginDialogFactory.setLoginListener(createLoginListener());
    }

    private LoginDialogFactory.LoginListener createLoginListener() {
        return new LoginDialogFactory.LoginListener() {
            @Override
            public void onLogin(int pin) {
                loginPresenter.startLogin(loginManager.getLastLogin(), String.valueOf(pin));
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        resume();
    }

    public void resume() {
        if (paused) {
            getActivity().registerReceiver(notAuthorizedReceiver,
                    new IntentFilter(NotAuthorizedResponseProcessor.NOT_AUTHORIZED_BROADCAST_ACTION));
            paused = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    public void pause() {
        if (!paused) {
            getActivity().unregisterReceiver(notAuthorizedReceiver);
            paused = true;
        }
    }

    public boolean showNotAuthorizedDialogIfNeeded() {
        if (loginDialogFactory.findDialog() == null
                && !loginPresenter.isProgressDialogShown()) {
            loginDialogFactory.showDialog();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        loginPresenter.saveState(outState);
    }
}
