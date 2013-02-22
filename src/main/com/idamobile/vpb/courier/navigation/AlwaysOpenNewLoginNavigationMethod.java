package com.idamobile.vpb.courier.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.LoginActivity_;
import com.idamobile.vpb.courier.controllers.LoginManager;

public class AlwaysOpenNewLoginNavigationMethod extends AbstractNavigationMethod {

    private boolean signOut;

    public AlwaysOpenNewLoginNavigationMethod(Context context) {
        super(context, LoginActivity_.class);

        Activity act = (Activity) context;
        CoreApplication coreApp = (CoreApplication) act.getApplication();
        LoginManager loginManager = coreApp.getMediator().getLoginManager();
        signOut = loginManager.isLoggedIn();
    }

    public void setSignOut() {
        this.signOut = true;
    }

    @Override
    public void startForResult(int requestCode, Bundle extras) {
        Intent intent = createIntent(extras);
        if (signOut) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            AlwaysOpenNewNavigationUtils.setPreviousActivity(getContext(), intent);
        }
        if (requestCode >= 0) {
            ((Activity) getContext()).startActivityForResult(intent, requestCode);
        } else {
            getContext().startActivity(intent);
        }
    }

    @Override
    protected void setupIntent(Intent intent) {
    }

}