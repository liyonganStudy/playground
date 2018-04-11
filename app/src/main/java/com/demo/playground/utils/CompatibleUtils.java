package com.demo.playground.utils;

import android.os.Build;

/**
 * Created by hzliyongan on 2018/4/11.
 */

public class CompatibleUtils {
    public static boolean isVersionLollipopAndUp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isVersionKitkatAndUp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
