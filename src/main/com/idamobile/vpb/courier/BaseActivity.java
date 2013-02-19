package com.idamobile.vpb.courier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.idamobile.vpb.courier.navigation.AlwaysOpenNewNavigationController;
import com.idamobile.vpb.courier.navigation.NavigationController;

public class BaseActivity extends FragmentActivity {

    private NavigationController navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigationController = new AlwaysOpenNewNavigationController(this);
        navigationController.onCreate();
    }

    public ApplicationMediator getMediator() {
        return ((CoreApplication) getApplication()).getMediator();
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        navigationController.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (!navigationController.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        navigationController.onActivityResult(requestCode, resultCode, data);
    }


}
