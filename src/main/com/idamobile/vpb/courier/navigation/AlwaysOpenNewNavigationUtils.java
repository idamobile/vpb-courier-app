package com.idamobile.vpb.courier.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.LoginActivity_;

public class AlwaysOpenNewNavigationUtils {

    public static final String INTENT_EXTRA_PREVIOUS_ACTIVITY_CLASS = "extra_previous_activity_class";

    public static void setPreviousActivity(Context context, Intent intent) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Class<?> actClass = activity.getClass();

            if (actClass.equals(LoginActivity_.class)) {
                CoreApplication coreApplication = (CoreApplication) activity.getApplication();
                boolean loggedIn = coreApplication.getMediator().getLoginManager().isLoggedIn();
                if (loggedIn) {
                    actClass = null;
                }
            }

            if (actClass != null) {
                intent.putExtra(INTENT_EXTRA_PREVIOUS_ACTIVITY_CLASS, actClass);
            }
        }
    }

    public static boolean hasPreviousActivity(Activity activity) {
        return activity.getIntent().hasExtra(INTENT_EXTRA_PREVIOUS_ACTIVITY_CLASS);
    }
}
