package com.idamobile.vpb.courier.util;

import android.util.Log;
import com.idamobile.vpb.courier.BuildConfig;
import com.idamobile.vpb.courier.config.Config;

/**
 * 
 * @author zjor
 * @since Jun 29, 2012
 * 
 */
public class Logger {

    public static void network(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void persistence(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void debug(String tag, String message, Throwable t) {
        if (Config.LOG_LEVEL <= Log.DEBUG) {
            Log.d(tag, message, t);
        }
    }

    public static void debug(String tag, String message) {
        if (Config.LOG_LEVEL <= Log.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void warn(String tag, String message) {
        if (Config.LOG_LEVEL <= Log.WARN) {
            Log.w(tag, message);
        }
    }

    public static void warn(String tag, String message, Throwable t) {
        if (Config.LOG_LEVEL <= Log.WARN) {
            Log.w(tag, message, t);
        }
    }

    public static void error(String tag, String message, Throwable t) {
        if (Config.LOG_LEVEL <= Log.ERROR) {
            Log.e(tag, message, t);
        }
    }

    public static void wtf(String tag, String message, Throwable t) {
        Log.wtf(tag, message, t);
    }

    public static void error(String tag, String message) {
        Log.e(tag, message);
    }

}
