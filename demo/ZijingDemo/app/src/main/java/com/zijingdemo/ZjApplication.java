package com.zijingdemo;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wangzhen on 2018/1/23.
 */

public class ZjApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }
}
