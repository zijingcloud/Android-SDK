package com.zijingdemo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.squareup.leakcanary.LeakCanary;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wangzhen on 2018/1/23.
 */

public class ZjApplication extends Application {

    private final static String PROCESS_NAME = "com.zijingdemo";
    private static ZjApplication zjApplication = null;

    public static ZjApplication getApplication() {
        return zjApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        zjApplication = this;

        if (isAppMainProcess()) {
            JPushInterface.setDebugMode(true);
            JPushInterface.init(this);

            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        }
    }

    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public static boolean isAppMainProcess() {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(ZjApplication.getApplication(), pid);
            if (TextUtils.isEmpty(process)) {
                return true;
            } else if (PROCESS_NAME.equalsIgnoreCase(process)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 根据Pid得到进程名
     */
    public static String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }
}
