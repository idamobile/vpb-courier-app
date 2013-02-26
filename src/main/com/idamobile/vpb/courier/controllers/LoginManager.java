package com.idamobile.vpb.courier.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.core.RequestService;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.login.LoginCallback;
import com.idamobile.vpb.courier.network.login.LoginRequest;
import com.idamobile.vpb.courier.network.login.LoginResponse;
import com.idamobile.vpb.courier.network.login.LoginResult;
import com.idamobile.vpb.courier.preferences.LoginPreference;
import com.idamobile.vpb.courier.security.crypto.CryptoPreferences;
import com.idamobile.vpb.courier.util.CryptoUtil;
import com.idamobile.vpb.courier.util.Hashs;
import com.idamobile.vpb.courier.util.Logger;

import javax.crypto.spec.SecretKeySpec;

public class LoginManager {

    public static final String TAG = LoginManager.class.getSimpleName();

    private ApplicationMediator mediator;
    private LoginPreference loginPreference;
    private SecretKeySpec cryptoKey;
    private CryptoPreferences cryptoPreferences;

    public LoginManager(ApplicationMediator mediator) {
        this.mediator = mediator;
        this.loginPreference = new LoginPreference(PreferenceManager.getDefaultSharedPreferences(mediator.getContext()));
        this.cryptoPreferences = new CryptoPreferences();
    }

    public void login(String login, String password) {
        login(null, login, password);
    }

    public void login(RequestWatcherCallbacks<LoginResponse> watcher, String login, String password) {
        String passwordMd5 = Hashs.getSHA1(password);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        loginRequest.setPasswordHash(passwordMd5);
        loginRequest.setUpdateModelCallback(new LoginCallback());
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
        cryptoKey = null;
        loginPreference.clear();

        mediator.getCache().clear();
        mediator.getNetworkManager().cleanUpSession();
    }

    public void saveLastCredentials(String login, String passwordHash) {
        loginPreference.setLogin(login);
        String key = login + "-" + passwordHash;
        try {
            this.cryptoKey = CryptoUtil.getKey(key, cryptoPreferences.getSalt(),
                    cryptoPreferences.getIterationsCount(), cryptoPreferences.getKeyLength());
        } catch (Exception e) {
            Logger.debug(TAG, "error getting crypto key", e);
        }
    }

    public SecretKeySpec getSecretKey() {
        return cryptoKey;
    }

    public String getLastLogin() {
        return loginPreference.getLogin();
    }

    public void registerForLogin(Context context, BroadcastReceiver receiver) {
        context.registerReceiver(receiver, new IntentFilter(
                mediator.getCache().getHolder(LoginResponse.class).getBroadcastAction()));
    }
}
