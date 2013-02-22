package com.idamobile.vpb.courier.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.core.LoaderCallback;
import com.idamobile.vpb.courier.network.core.RequestService;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.login.LoginRequest;
import com.idamobile.vpb.courier.network.login.LoginResponse;
import com.idamobile.vpb.courier.network.login.LoginResult;
import com.idamobile.vpb.courier.preferences.LoginPreference;
import com.idamobile.vpb.courier.util.Hashs;

public class LoginManager {

    private ApplicationMediator mediator;
    private LoginPreference loginPreference;

    public LoginManager(ApplicationMediator mediator) {
        this.mediator = mediator;
        this.loginPreference = new LoginPreference(PreferenceManager.getDefaultSharedPreferences(mediator.getContext()));
    }

    public void login(String login, String password) {
        login(null, login, password);
    }

    public void login(RequestWatcherCallbacks<LoginResponse> watcher, String login, String password) {
        String passwordMd5 = Hashs.getSHA1(password);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        loginRequest.setPasswordHash(passwordMd5);
        loginRequest.setUpdateModelCallback(new LoaderCallback<LoginResponse>(LoginResponse.class));
        if (watcher != null) {
            watcher.execute(loginRequest);
        } else {
            RequestService.execute(mediator.getContext(), loginRequest);
        }
    }

    public boolean isLoggedIn() {
        DataHolder<LoginResponse> holder = getLoginHolder();
        return !holder.isEmpty() && holder.get().getLoginResult() == LoginResult.OK;
    }

    private DataHolder<LoginResponse> getLoginHolder() {
        return mediator.getCache().getHolder(LoginResponse.class);
    }

    public Courier getCourier() {
        DataHolder<LoginResponse> loginHolder = getLoginHolder();
        return !loginHolder.isEmpty() ? loginHolder.get().getCourierInfo() : null;
    }

    public void logout() {
        DataHolder<LoginResponse> holder = getLoginHolder();
        holder.clear();

        mediator.getNetworkManager().cleanUpSession();
    }

    public void saveLastLogin(String login) {
        loginPreference.setLogin(login);
    }

    public String getLastLogin() {
        return loginPreference.getLogin();
    }

    public void registerForLogin(Context context, BroadcastReceiver receiver) {
        context.registerReceiver(receiver, new IntentFilter(
                mediator.getCache().getHolder(LoginResponse.class).getBroadcastAction()));
    }
}
