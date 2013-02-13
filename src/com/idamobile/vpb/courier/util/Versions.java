package com.idamobile.vpb.courier.util;

import android.os.Build.VERSION;

import static android.os.Build.VERSION_CODES.GINGERBREAD;
import static android.os.Build.VERSION_CODES.HONEYCOMB;

public class Versions {

    public static boolean isApiLevelAvailable(int level) {
        return VERSION.SDK_INT >= level;
    }

    public static boolean hasHoneycombApi() {
        return isApiLevelAvailable(HONEYCOMB);
    }

    public static boolean hasGingerbreadApi() {
        return isApiLevelAvailable(GINGERBREAD);
    }
}
