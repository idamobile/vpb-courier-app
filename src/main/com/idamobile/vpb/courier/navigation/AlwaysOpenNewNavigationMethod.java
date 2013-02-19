package com.idamobile.vpb.courier.navigation;

import android.content.Context;
import android.content.Intent;

public class AlwaysOpenNewNavigationMethod extends AbstractNavigationMethod {

    public AlwaysOpenNewNavigationMethod(Context context, Class<?> actClass) {
        super(context, actClass);
    }

    @Override
    protected void setupIntent(Intent intent) {
        AlwaysOpenNewNavigationUtils.setPreviousActivity(getContext(), intent);
    }

}