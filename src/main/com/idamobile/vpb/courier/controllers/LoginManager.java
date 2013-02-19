package com.idamobile.vpb.courier.controllers;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.core.LoaderCallback;
import com.idamobile.vpb.courier.network.core.RequestService;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.login.LoginRequest;
import com.idamobile.vpb.courier.network.login.LoginResponse;
import com.idamobile.vpb.courier.network.login.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginManager {

    private ApplicationMediator mediator;

    public LoginManager(ApplicationMediator mediator) {
        this.mediator = mediator;
    }

    public void login(String login, String password) {
        login(null, login, password);
    }

    public void login(RequestWatcherCallbacks<LoginResponse> watcher, String login, String password) {
        String passwordMd5 = getMD5(password);

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
        DataHolder<LoginResponse> holder =
                mediator.getCache().getHolder(LoginResponse.class);
        return !holder.isEmpty() && holder.get().getLoginResult() == LoginResult.OK;
    }

    private String getMD5(String text) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] textBytes = text.getBytes();
            byte[] result = md5.digest(textBytes);
            return new String(result);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void logout() {
        DataHolder<LoginResponse> holder =
                mediator.getCache().getHolder(LoginResponse.class);
        holder.clear();

        mediator.getNetworkManager().cleanUpSession();
    }
}
