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

public class CourierNamePresenterFragment extends Fragment {

    public static final String TAG = CourierNamePresenterFragment.class.getSimpleName();

    private ApplicationMediator mediator;
    private boolean registered;
    private BroadcastReceiver loginListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    public static CourierNamePresenterFragment attach(FragmentActivity fragmentActivity) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        CourierNamePresenterFragment presenter = find(fragmentActivity);
        if (presenter == null) {
            presenter = new CourierNamePresenterFragment();
            manager.beginTransaction()
                    .add(presenter, TAG)
                    .commit();
        }
        return presenter;
    }

    public static CourierNamePresenterFragment find(FragmentActivity fragmentActivity) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        return (CourierNamePresenterFragment) manager.findFragmentByTag(TAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mediator = CoreApplication.getMediator(getActivity());
    }

    private void refresh() {
        LoginResponse loginResponse = mediator.getCache().getHolder(LoginResponse.class).get();
        if (loginResponse == null || loginResponse.getCourierInfo() == null) {
            getActivity().setTitle(R.string.order_list_activity_label);
        } else {
            Courier courierInfo = loginResponse.getCourierInfo();
            StringBuilder builder = new StringBuilder(getString(R.string.order_list_activity_label));
            builder.append(" (")
                    .append(courierInfo.getFirstName())
                    .append(" ")
                    .append(courierInfo.getLastName())
                    .append(")");
            getActivity().setTitle(builder);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        refresh();
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
