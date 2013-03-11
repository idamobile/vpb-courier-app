package com.idamobile.vpb.courier.security;

import android.content.Intent;
import android.os.Bundle;
import com.idamobile.vpb.courier.BaseActivity;

public class SecuredActivity extends BaseActivity {

    private SecurityManager securityManager;
    private boolean shouldFinishIfNotLoggedIn;
    private boolean shouldAttachNotAuthorizedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        securityManager = new SecurityManager(this);

        if (shouldFinishIfNotLoggedIn) {
            securityManager.setShouldFinishIfNotLoggedIn();
        }
        if (shouldAttachNotAuthorizedListener) {
            NotAuthorizedDialogPresenter.attach(this);
        }
        securityManager.dispatchOnCreate(savedInstanceState);
    }

    public void setShouldFinishIfNotLoggedIn() {
        this.shouldFinishIfNotLoggedIn = true;
        if (securityManager != null) {
            securityManager.setShouldFinishIfNotLoggedIn();
        }
    }

    public void setShouldAttachNotAuthorizedListener() {
        this.shouldAttachNotAuthorizedListener = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        securityManager.dispatchOnResume();
    }

    @Override
    public void startActivity(Intent intent) {
        securityManager.dispatchStartActivity(intent);
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        securityManager.dispatchStartActivityForResult(intent, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        securityManager.dispatchOnPause();
    }

    public void pauseSecurity() {
        securityManager.pauseSecurity();
    }

    public void resumeSecurity() {
        securityManager.resumeSecurity();
    }
}