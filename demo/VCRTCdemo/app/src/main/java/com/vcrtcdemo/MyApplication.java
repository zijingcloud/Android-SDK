package com.vcrtcdemo;

import android.app.Application;

import com.vcrtc.VCRTCPreferences;
import com.vcrtc.webrtc.RTCManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        VCRTCPreferences prefs = new VCRTCPreferences(this);
        prefs.setPrintLogs(true);

        //极光推送
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);

        RTCManager.init(this);
        RTCManager.DEVICE_TYPE = "Android";
        RTCManager.APPLICATION_ID = BuildConfig.APPLICATION_ID;
        RTCManager.VERSION_NAME = BuildConfig.VERSION_NAME;
        RTCManager.OEM = "oem";

        copyCloseVideoImageFromRaw(prefs);//复制关闭摄像头的图片到手机
    }

    private void copyCloseVideoImageFromRaw(VCRTCPreferences prefs) {
        String imagePath = getFilesDir().getAbsolutePath() + File.separator + "close_video.png";
        InputStream inputStream = getResources().openRawResource(R.raw.close_video);
        File file = new File(imagePath);
        try {
            if (!file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[inputStream.available()];
                int lenght;
                while ((lenght = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //设置关摄像头后要展示的图片的路径
        prefs.setImageFilePath(imagePath);
    }
}
