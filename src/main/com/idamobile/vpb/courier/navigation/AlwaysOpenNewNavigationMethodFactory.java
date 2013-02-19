package com.idamobile.vpb.courier.navigation;

import android.content.Context;
import com.idamobile.vpb.courier.LoginActivity;

public class AlwaysOpenNewNavigationMethodFactory implements NavigationMethodFactory {

    @Override
    public NavigationMethod createNavigationMethod(Context context, Class<?> actClass) {
        final NavigationMethod method;
        if (actClass.equals(LoginActivity.class)) {
            method = new AlwaysOpenNewLoginNavigationMethod(context);
        } else {
            method = new AlwaysOpenNewNavigationMethod(context, actClass);
        }
        return method;
    }

}