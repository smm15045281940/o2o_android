package com.gjzg.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class CApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }

}
