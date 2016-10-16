package com.punchlag.wigt.utils;

import android.os.Build;

public class SystemUtils {

    public static boolean isAboveApi23() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }
}
