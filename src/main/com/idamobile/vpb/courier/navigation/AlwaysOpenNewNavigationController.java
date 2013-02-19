package com.idamobile.vpb.courier.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.LoginActivity;
import com.idamobile.vpb.courier.OrderListActivity;
import com.idamobile.vpb.courier.controllers.LoginManager;

public class AlwaysOpenNewNavigationController extends AbstractNavigationController {

    private static final int NEXT_ACTIVITY_REQUEST_CODE = 2209;

    private RootActivityBackButtonController backButtonController;

    public AlwaysOpenNewNavigationController(Context context) {
        super(context, new AlwaysOpenNewNavigationMethodFactory());
        if (context instanceof Activity) {
            backButtonController = new RootActivityBackButtonController((Activity) getContext());
        }
    }

    @Override
    public boolean onBackPressed() {
        Activity activity = (Activity) getContext();
        boolean hasPrevious = AlwaysOpenNewNavigationUtils.hasPreviousActivity(activity);
        if (!hasPrevious && backButtonController.dispatchOnBackPressed()) {
            return true;
        } else {
            if (activity instanceof OrderListActivity) {
                activity.setResult(Activity.RESULT_OK);
            }
            return false;
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onNewIntent(Intent intent) {
    }

    @Override
    public void processSuccessLogin() {
        getOrdersList().startForResult(NEXT_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Activity activity = (Activity) getContext();
        if (activity instanceof LoginActivity) {
            LoginManager loginManager = ((CoreApplication) activity.getApplication()).getMediator().getLoginManager();
            if (requestCode == NEXT_ACTIVITY_REQUEST_CODE) {
//                if (resultCode == Activity.RESULT_OK) {
//                    if (loginManager.isLoggedIn()) {
                        activity.finish();
//                    }
//                }
            }
        }
    }

    @Override
    public void processSignOut() {
        NavigationMethod method = getLogin();
        if (method instanceof AlwaysOpenNewLoginNavigationMethod) {
            ((AlwaysOpenNewLoginNavigationMethod) method).setSignOut();
        }
        method.start();
    }

}