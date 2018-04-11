package com.demo.playground.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.demo.playground.MyApplication;

public class DimensionUtils {
    public static final DisplayMetrics DISPLAY_METRICS;
    public static final float DENSITY;
    public static final int SCREEN_WIDTH_PORTRAIT;
    public static final int SCREEN_HEIGHT_PORTRAIT;

    static {
        Resources resources = MyApplication.getInstance().getResources();
        boolean portrait = resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        DISPLAY_METRICS = resources.getDisplayMetrics();
        DENSITY = DISPLAY_METRICS.density;
        SCREEN_WIDTH_PORTRAIT = portrait ? DISPLAY_METRICS.widthPixels : DISPLAY_METRICS.heightPixels;
        SCREEN_HEIGHT_PORTRAIT = portrait ? DISPLAY_METRICS.heightPixels : DISPLAY_METRICS.widthPixels;
    }

    public static int dpToPx(float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, DISPLAY_METRICS) + 0.5);
    }

    public static float pxToDp(int px) {
        return px / DENSITY;
    }

    public static int getScreenWidth() {
        return DISPLAY_METRICS.widthPixels;
    }

    public static int getScreenHeight() {
        return DISPLAY_METRICS.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int dimen2px(Context context, int dimen) {
        return context.getResources().getDimensionPixelSize(dimen);
    }
}
