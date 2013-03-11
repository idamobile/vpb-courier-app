package com.idamobile.vpb.courier.security;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.WindowManager;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.config.Config;
import com.idamobile.vpb.courier.navigation.NavigationController;
import com.idamobile.vpb.courier.navigation.NavigationControllerFactory;
import com.idamobile.vpb.courier.util.Logger;
import com.idamobile.vpb.courier.util.Versions;

public class SecurityManager {

    private static final String TAG = SecurityManager.class.getSimpleName();

    private boolean movementDetected;
    private boolean startingLoginActivity;

    private SecurityPreferences securityPreferences;

    private boolean shouldFinishIfNotLoggedIn;

    private FragmentActivity activity;
    private ApplicationMediator mediator;
    private NavigationController navigationController;

    private boolean paused;
    private boolean confidenceIntervalFinished;

    public SecurityManager(FragmentActivity activity) {
        this.activity = activity;
        this.mediator = CoreApplication.getMediator(activity);
        this.securityPreferences = new SecurityPreferences(Config.LOCK_SCREEN_TIMEOUT);
        this.navigationController = NavigationControllerFactory.getInstance().createController(activity);
    }

    /**
     * Call activity finish() if user is not logged in shouldFinishIfNotLoggedIn flag is set
     */
    public void dispatchOnCreate(Bundle savedInstanceState) {
        if (shouldFinishIfNotLoggedIn && !isLoggedIn()) {
            Logger.debug(TAG, "user is not logged in, redirecting to login activity");
            closeAllAndOpenLoginActivity();
        }
    }

    public void setShouldFinishIfNotLoggedIn() {
        this.shouldFinishIfNotLoggedIn = true;
        if (Versions.hasHoneycombApi()) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    private boolean isLoggedIn() {
        return getMediator().getLoginManager().isLoggedIn();
    }

    private ApplicationMediator getMediator() {
        return ((CoreApplication) activity.getApplication()).getMediator();
    }

    private void closeAllAndOpenLoginActivity() {
        startingLoginActivity = true;

        mediator.getLoginManager().logout();
        activity.overridePendingTransition(0, 0);
        navigationController.processSignOut();
    }

    public void dispatchOnResume() {
        movementDetected = false;
        if (!activity.isFinishing() && !startingLoginActivity) {
            openLoginDialogIfNeeded();
        }
    }

    private void openLoginDialogIfNeeded() {
        if (securityPreferences.isConfidenceIntervalWasStarted()
                && securityPreferences.isConfidenceIntervalFinished()) {
            if (!paused) {
                openLoginDialog();
            } else {
                confidenceIntervalFinished = true;
            }
        }
        securityPreferences.stopConfidenceInterval();
    }

    public void dispatchStartActivity(Intent intent) {
        movementDetected = true;
    }

    public void dispatchStartActivityForResult(Intent intent, int requestCode) {
        movementDetected = true;
    }

    private void openLoginDialog() {
        NotAuthorizedDialogPresenter presenter = NotAuthorizedDialogPresenter.find(activity);
        if (presenter != null && !TextUtils.isEmpty(mediator.getLoginManager().getLastLogin())) {
            presenter.showNotAuthorizedDialogIfNeeded();
        } else {
            closeAllAndOpenLoginActivity();
        }
    }

    public void dispatchOnPause() {
        tryToSetLock();
    }

    private void tryToSetLock() {
        if (!movementDetected && isLoggedIn()) {
            securityPreferences.startConfidenceInterval();
        }
    }

    public void pauseSecurity() {
        paused = true;
    }

    public void resumeSecurity() {
        if (paused) {
            paused = false;
            if (confidenceIntervalFinished) {
                openLoginDialog();
            }
            confidenceIntervalFinished = false;
        }
    }
}
