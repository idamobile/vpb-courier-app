package com.idamobile.vpb.courier.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public abstract class AbstractNavigationMethod implements NavigationMethod {

    private Context context;
    private Class<?> actClass;

    public AbstractNavigationMethod(Context context, Class<?> actClass) {
        this.context = context;
        this.actClass = actClass;
    }

    public Context getContext() {
        return context;
    }

    public Class<?> getActClass() {
        return actClass;
    }

    @Override
    public void start() {
        start(null);
    }

    @Override
    public void start(Bundle extras) {
        startForResult(-1, extras);
    }

    @Override
    public void startForResult(int requestCode) {
        startForResult(requestCode, null);
    }

    @Override
    public void startForResult(int requestCode, Bundle extras) {
        Intent intent = createIntent(extras);
        if (requestCode >= 0) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }

    protected Intent createIntent(Bundle extras) {
        Intent intent = new Intent(context, actClass);
        if (extras != null) {
            intent.putExtras(extras);
        }
        setupIntent(intent);
        return intent;
    }

    protected abstract void setupIntent(Intent intent);

}