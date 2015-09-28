package com.cypressworks.mensaplan;

import android.app.Application;

/**
 * @author Kirill Rakhman
 */
public class MyApplication extends Application {

    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }
}
