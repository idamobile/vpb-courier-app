package com.idamobile.vpb.courier.network.login;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.network.core.LoaderCallback;
import com.idamobile.vpb.courier.network.core.Request;

public class LoginCallback extends LoaderCallback<LoginResponse> {

    public LoginCallback() {
        super(LoginResponse.class);
    }

    @Override
    protected void onSuccess(Request<LoginResponse> request, LoginResponse data, ApplicationMediator mediator) {
        super.onSuccess(request, data, mediator);
        String login = ((LoginRequest) request).getLogin();
        mediator.getLoginManager().saveLastLogin(login);
    }

}
