package com.idamobile.vpb.courier.network.login;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.network.core.DefaultLoaderCallback;
import com.idamobile.vpb.courier.network.core.Request;

public class LoginCallback extends DefaultLoaderCallback<LoginResponse> {

    public LoginCallback() {
        super(LoginResponse.class);
    }

    @Override
    protected void onSuccess(Request<LoginResponse> request, LoginResponse data, ApplicationMediator mediator) {
        super.onSuccess(request, data, mediator);
        String login = ((LoginRequest) request).getLogin();
        String key = data.getKeyHash();
        mediator.getLoginManager().saveLastLogin(login);
        mediator.getLoginManager().generateSecretKey(login, key);
    }

}
