package com.idamobile.vpb.courier.navigation;

import android.os.Bundle;

public interface NavigationMethod {

    void start();

    void start(Bundle extras);

    void startForResult(int requestCode);

    void startForResult(int requestCode, Bundle extras);

}