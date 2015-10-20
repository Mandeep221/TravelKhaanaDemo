package com.msarangal.travelkhaanademo;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 * Created by Mandeep on 16/6/2015.
 */
public class MyApplication extends Application {

    public static MyApplication myApplicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplicationInstance = this;

    }

    public static MyApplication getInstance() {
        return myApplicationInstance;
    }

    public static Context getAppContext() {
        return myApplicationInstance.getApplicationContext();
    }
}
