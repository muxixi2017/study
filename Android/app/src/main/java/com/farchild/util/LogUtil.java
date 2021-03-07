package com.farchild.util;

import android.util.Log;

public class LogUtil {
    public final static String PREFIX = "FCDGB";
    public static void d(String tag, String data) {
        Log.e(PREFIX + tag, data);
    }
}
