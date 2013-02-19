package com.idamobile.vpb.courier.navigation;

import android.content.Context;

public interface NavigationMethodFactory {

    NavigationMethod createNavigationMethod(Context context, Class<?> actClass);

}
