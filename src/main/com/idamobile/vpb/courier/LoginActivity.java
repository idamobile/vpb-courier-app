package com.idamobile.vpb.courier;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.courier.presenters.LoginPresenter;
import com.idamobile.vpb.courier.widget.login.PinWidget;


@EActivity(R.layout.login_activity)
public class LoginActivity extends BaseActivity {

    @ViewById(R.id.widget_set_pin) PinWidget pinWidget;
    @ViewById(R.id.login_field) EditText loginField;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginPresenter = new LoginPresenter(this, savedInstanceState);
        loginPresenter.setLoginListener(new LoginPresenter.LoginListener() {
            @Override
            public void onSuccessfulLogin(LoginPresenter loginPresenter) {
                getNavigationController().processSuccessLogin();

                Courier courier = getMediator().getLoginManager().getCourier();
                getMediator().getOrdersManager().requestOrders(courier.getId());
            }

            @Override
            public void onLoginError(LoginPresenter loginPresenter) {
                pinWidget.clear();
            }
        });

    }

    @AfterViews
    void setup() {
        pinWidget.setOnSubmitListener(new PinWidget.OnSubmitListener() {
            @Override
            public boolean submit(int pin) {
                loginPresenter.startLogin(loginField.getText().toString(), String.valueOf(pin));
                return true;
            }
        });

        String login = getMediator().getLoginManager().getLastLogin();
        loginField.setText(login);
        if (!TextUtils.isEmpty(login)) {
            pinWidget.requestFocus();
        }
    }

    public LoginPresenter getLoginPresenter() {
        return loginPresenter;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        loginPresenter.saveState(outState);
    }
}