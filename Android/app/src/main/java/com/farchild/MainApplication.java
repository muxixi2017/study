package com.farchild;

import android.app.Application;

import com.farchild.util.AppUtil;

public class MainApplication extends Application {
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        AppUtil.init(this);
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.init(this.getApplicationContext());
    }
}
