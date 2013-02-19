package com.idamobile.vpb.courier.navigation;

import android.content.Intent;

public interface NavigationListener {

    /**
     * @return false to call super.onBackPressed(), true otherwise
     */
    boolean onBackPressed();

    void onCreate();

    void onNewIntent(Intent intent);

    void onActivityResult(int requestCode, int resultCode, Intent data);

}