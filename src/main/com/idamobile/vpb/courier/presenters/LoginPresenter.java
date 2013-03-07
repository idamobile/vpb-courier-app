package com.idamobile.vpb.courier.presenters;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.controllers.LoginManager;
import com.idamobile.vpb.courier.network.core.Request;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.core.ResponseDTO;
import com.idamobile.vpb.courier.network.core.ResultCodeToMessageConverter;
import com.idamobile.vpb.courier.network.login.LoginResponse;
import com.idamobile.vpb.courier.network.login.LoginResult;
import com.idamobile.vpb.courier.widget.dialogs.AlertDialogFactory;
import com.idamobile.vpb.courier.widget.dialogs.DialogRequestListener;
import com.idamobile.vpb.courier.widget.dialogs.ProgressDialogFactory;

public class LoginPresenter {

    public interface LoginListener {
        void onSuccessfulLogin(LoginPresenter loginPresenter);
        void onLoginError(LoginPresenter loginPresenter);
    }

    private FragmentActivity activity;

    private RequestWatcherCallbacks<LoginResponse> watcherCallbacks;
    private ProgressDialogFactory loginProgressDialog;
    private AlertDialogFactory loginFailedDialog;
    private LoginManager loginManager;

    private ResultCodeToMessageConverter converter;

    private LoginListener loginListener;

    public LoginPresenter(FragmentActivity activity, Bundle savedInstanceState) {
        this.activity = activity;
        this.converter = new ResultCodeToMessageConverter(activity);
        this.loginManager = CoreApplication.getMediator(activity).getLoginManager();

        createDialogs();
        createCallbacks(savedInstanceState);
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    private void createDialogs() {
        loginProgressDialog = new ProgressDialogFactory(activity, "login-progress");
        loginProgressDialog.setMessage(activity.getString(R.string.logiin_progress_dialog_message));
        loginProgressDialog.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                watcherCallbacks.cancel(true);
            }
        });

        loginFailedDialog = new AlertDialogFactory(activity, "login-failed-dialog");
        loginFailedDialog.setTitle(activity.getText(R.string.login_error_dialog_title));
        loginFailedDialog.setCancellable(true);
        loginFailedDialog.setPosButtonText(activity.getText(android.R.string.ok));
    }

    private void createCallbacks(Bundle savedInstanceState) {
        watcherCallbacks = new RequestWatcherCallbacks<LoginResponse>(
                activity, "login-callbacks", savedInstanceState);
        watcherCallbacks.registerListener(new DialogRequestListener<LoginResponse>(loginProgressDialog));
        watcherCallbacks.registerListener(new RequestWatcherCallbacks.SimpleRequestListener<LoginResponse>() {
            @Override
            public void onSuccess(Request<LoginResponse> request, ResponseDTO<LoginResponse> result) {
                if (result.getData().getLoginResult() == LoginResult.OK) {
                    if (loginListener != null) {
                        loginListener.onSuccessfulLogin(LoginPresenter.this);
                    };
                } else {
                    onError(request, result);
                }
            }

            @Override
            public void onError(Request<LoginResponse> request, ResponseDTO<LoginResponse> result) {
                if (result.getData() != null) {
                    showErrorMessage(result.getData().getLoginResult());
                } else {
                    showErrorMessage(result.getResultCode());
                }

                if (loginListener != null) {
                    loginListener.onLoginError(LoginPresenter.this);
                };
            }
        });
    }

    public void startLogin(String login, String password) {
        if (validateLoginAndPassoword(login, password)) {
            loginManager.login(watcherCallbacks, login, password);
        }
    }

    public boolean isProgressDialogShown() {
        return loginProgressDialog.findDialog() != null;
    }

    public boolean validateLoginAndPassoword(String login, String password) {
        if (TextUtils.isEmpty(login)) {
            Toast.makeText(activity, activity.getText(R.string.login_empty_error), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(activity, activity.getText(R.string.password_empty_error), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void showErrorMessage(LoginResult loginResult) {
        final CharSequence errorMessage;
        switch (loginResult) {
            case BLOCKED_ACCOUNT:
                errorMessage = activity.getText(R.string.login_account_is_blocked);
                break;
            case WRONG_CREDENTIALS:
                errorMessage = activity.getString(R.string.login_wrong_credentials);
                break;
            default:
                errorMessage = null;
                break;
        }

        if (errorMessage != null) {
            loginFailedDialog.setMessage(errorMessage);
            loginFailedDialog.showDialog();
        }
    }

    private void showErrorMessage(ResponseDTO.ResultCode code) {
        converter.showToast(code);
    }

    public void saveState(Bundle outState) {
        watcherCallbacks.saveInstanceState(outState);
    }
}
