package com.autosos.rescue.application;

/**
 * Created by Administrator on 2016/6/6.
 */
public class MyApplication extends android.app.Application {

    public static MyApplication application = null;
    public static boolean isAfterOrder = false;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }



}
