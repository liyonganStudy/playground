package com.demo.playground;

import android.app.Application;

/**
 * Created by liyongan on 17/12/7.
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;

    public MyApplication() {
        mInstance = this;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}
