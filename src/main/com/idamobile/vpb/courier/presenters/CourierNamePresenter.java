package com.idamobile.vpb.courier.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.courier.network.login.LoginResponse;

public class CourierNamePresenter extends Fragment {

    public static final String TAG = CourierNamePresenter.class.getSimpleName();

    private ApplicationMediator mediator;
    private boolean registered;
    private BroadcastReceiver loginListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshName();
        }
    };

    public static CourierNamePresenter attach(FragmentActivity fragmentActivity) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        CourierNamePresenter presenter = find(fragmentActivity);
        if (presenter == null) {
            presenter = new CourierNamePresenter();
            manager.beginTransaction()
                    .add(presenter, TAG)
                    .commit();
        }
        return presenter;
    }

    public static CourierNamePresenter find(FragmentActivity fragmentActivity) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        return (CourierNamePresenter) manager.findFragmentByTag(TAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mediator = CoreApplication.getMediator(getActivity());
    }

    private void refreshName() {
        LoginResponse loginResponse = mediator.getCache().getHolder(LoginResponse.class).get();
        if (loginResponse == null || loginResponse.getCourierInfo() == null) {
            getActivity().setTitle(R.string.order_list_activity_label);
        } else {
            Courier courierInfo = loginResponse.getCourierInfo();
            getActivity().setTitle(getString(R.string.order_list_activity_label)
                    + " (" + courierInfo.getFirstName() + " " + courierInfo.getLastName() + ")");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        refreshName();
    }

    private void registerListener() {
        if (!registered) {
            mediator.getLoginManager().registerForLogin(getActivity(), loginListener);
            registered = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
    }

    private void unregisterListener() {
        if (registered) {
            getActivity().unregisterReceiver(loginListener);
            registered = false;
        }
    }
}
