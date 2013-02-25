package com.idamobile.vpb.courier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.Window;
import com.idamobile.vpb.courier.navigation.NavigationController;
import com.idamobile.vpb.courier.navigation.NavigationControllerFactory;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.core.ResultCodeToMessageConverter;
import com.idamobile.vpb.courier.presenters.RefreshButtonController;
import com.idamobile.vpb.courier.util.Versions;

public class BaseActivity extends FragmentActivity implements RefreshButtonController.RefreshButtonListener {

    private NavigationController navigationController;
    private RefreshButtonController refreshButtonController;
    private ResultCodeToMessageConverter converter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Versions.hasHoneycombApi()) {
            requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        }

        refreshButtonController = new RefreshButtonController(this, this);
        converter = new ResultCodeToMessageConverter(this);

        navigationController = NavigationControllerFactory.getInstance().createController(this);
        navigationController.onCreate();
    }

    public ApplicationMediator getMediator() {
        return ((CoreApplication) getApplication()).getMediator();
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public RefreshButtonController getRefreshButtonController() {
        return refreshButtonController;
    }

    public ResultCodeToMessageConverter getResultCodeConverter() {
        return converter;
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

    @Override
    protected void onResume() {
        super.onResume();
        refreshButtonController.dispatchOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshButtonController.dispatchOnPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (refreshButtonController.dispatchOnOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void showRefreshProgressForHolders(DataHolder<?>... holders) {
        refreshButtonController.showRefreshProgressForHolders(holders);
    }

    @Override
    public void onRefreshClicked() {
    }

    @Override
    public void onRefreshFinishedWithErrors() {
    }
}
