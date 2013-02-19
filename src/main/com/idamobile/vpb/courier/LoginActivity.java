package com.idamobile.vpb.courier;

import android.widget.EditText;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.idamobile.vpb.courier.presenters.LoginPresenter;
import com.idamobile.vpb.courier.widget.view.PinWidget;


@EActivity(R.layout.login_activity)
public class LoginActivity extends BaseActivity {

    @ViewById(R.id.widget_set_pin) PinWidget pinWidget;
    @ViewById(R.id.login_field) EditText loginField;

    private LoginPresenter loginPresenter;

    @AfterViews
    void setup() {
        loginPresenter = new LoginPresenter(this);
        loginPresenter.setLoginListener(new LoginPresenter.LoginListener() {
            @Override
            public void onSuccessfulLogin(LoginPresenter loginPresenter) {
                getNavigationController().processSuccessLogin();
            }

            @Override
            public void onLoginError(LoginPresenter loginPresenter) {
                pinWidget.clear();
            }
        });

        pinWidget.setOnSubmitListener(new PinWidget.OnSubmitListener() {
            @Override
            public boolean submit(int pin) {
                loginPresenter.startLogin(loginField.getText().toString(), String.valueOf(pin));
                return true;
            }
        });
    }

    public LoginPresenter getLoginPresenter() {
        return loginPresenter;
    }
}