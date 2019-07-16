package com.example.alan.sdkdemo;

import android.app.Application;

import com.vcrtc.webrtc.RTCManager;

/**
 * Created by ricardo
 * 2019/7/4.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RTCManager.init(this);
        RTCManager.DEVICE_TYPE = "Android";
        RTCManager.OEM = "";
    }
}
