package com.idamobile.vpb.courier.navigation;

import android.content.Context;

public class NavigationControllerFactory {

    private static NavigationControllerFactory factory;

    public static NavigationControllerFactory getInstance() {
        if (factory == null) {
            factory = new NavigationControllerFactory();
        }
        return factory;
    }

    private NavigationControllerFactory() {
    }

    public NavigationController createController(Context context) {
        return new AlwaysOpenNewNavigationController(context);
    }

}
