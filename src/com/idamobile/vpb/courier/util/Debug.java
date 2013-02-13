package com.idamobile.vpb.courier.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Debug {

    public static boolean isEnabled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return (0 != (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (NameNotFoundException e) {
            return false;
        }
    }

}
