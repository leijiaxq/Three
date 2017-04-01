package com.maymeng.three.base;

import android.app.Application;

/**
 * Create by  leijiaxq
 * Date       2017/3/2 14:31
 * Describe
 */

public class BaseApplication extends Application {

    public static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }
}
