package com.idamobile.vpb.courier;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.idamobile.vpb.courier.widget.view.PinWidget;


@EActivity(R.layout.login_activity)
public class LoginActivity extends Activity {

    @ViewById(R.id.widget_set_pin) PinWidget pinWidget;
    @ViewById(R.id.login_field) EditText loginField;

    @AfterViews
    void setup() {
        pinWidget.setOnSubmitListener(new PinWidget.OnSubmitListener() {
            @Override
            public boolean submit(int pin) {
                tryToLogin(loginField.getText().toString(), pin);
                return true;
            }
        });
    }

    public void tryToLogin(String login, int pin) {
        if (validateParams(login, pin)) {
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validateParams(String login, int pin) {
        if (TextUtils.isEmpty(login)) {
            loginField.setError(getText(R.string.login_empty_error));
        } else {
            return true;
        }
        return false;
    }
}
