package com.zjrtcdemo;

import android.app.Application;

import com.zjrtc.webrtc.RTCManager;

import java.util.UUID;

public class MyApplication extends Application {

    public String checkDup = UUID.randomUUID().toString();
    @Override
    public void onCreate() {
        super.onCreate();

        RTCManager.init(this);
    }
}
