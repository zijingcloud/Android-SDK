package com.vcrtcdemo;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.vcrtc.VCSevice;
import com.vcrtc.entities.IncomingCall;
import com.vcrtc.registration.VCRegistrationUtil;
import com.vcrtcdemo.activities.CallIncomingActivity;
import com.vcrtcdemo.activities.conference.VCVideoActivity;
import com.vcrtcdemo.activities.conference.VCVideoP2PActivity;
import com.vcrtcdemo.activities.conference.VCVideoShiTongActivity;
import com.vcrtcdemo.activities.conference.VCVideoSimulcastActivity;

import java.util.Date;

public class MyPushReceiver extends BroadcastReceiver {

    private static final String TAG = "MyPushReceiver";

    public static boolean isShowIncoming = false;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String msg = intent.getStringExtra(VCSevice.MSG);
        switch (msg) {
            case VCSevice.MSG_LOGIN_SUCCESS:
                Log.i(TAG, "登录成功");
                break;
            case VCSevice.MSG_LOGIN_FAILED:
                String reason = intent.getStringExtra(VCSevice.DATA_BROADCAST);
                Log.i(TAG, "登录失败" + reason);
                break;
            case VCSevice.MSG_USER_INFO:
                String userJson = intent.getStringExtra(VCSevice.DATA_BROADCAST);
                Log.i(TAG, "用户信息" + userJson);
                break;
            case VCSevice.MSG_SESSION_ID:
                String sessionID = intent.getStringExtra(VCSevice.DATA_BROADCAST);
                Log.i(TAG, "sessionID:" + sessionID);
                break;
            case VCSevice.MSG_ONLINE_STATUS:
                boolean onlineStatus = intent.getBooleanExtra(VCSevice.DATA_BROADCAST, false);
                Log.i(TAG, "在线状态:" + onlineStatus);
                break;
            case VCSevice.MSG_LOGOUT:
                Log.i(TAG, "账号在别的端登录");
                break;
            case VCSevice.MSG_INCOMING:
                Log.i(TAG, "收到消息");
                IncomingCall incomingCall = (IncomingCall) intent.getSerializableExtra(VCSevice.DATA_BROADCAST);
                if (isInvalidCall(incomingCall)){
                    Log.e(TAG, " isInvalidCall timeout 30 second ");
                    return;
                }
                if (isInConference() || isShowIncoming){ //已经在通话中，直接挂掉；
                    VCRegistrationUtil.hangup(context);
                } else {//没有在通话中，显示来电界面
                    isShowIncoming = true;
                    showInComingView(context, incomingCall);
                }
                break;
            case VCSevice.MSG_INCOMING_CANCELLED:
                Log.i(TAG, "呼叫端撤销了呼叫");
                finishIncomingView(context);
                break;
        }
    }

    private void showInComingView(Context context, IncomingCall inComingCall) {
        Bundle b = new Bundle();
        b.putSerializable("inComingCall",inComingCall);
        Intent i = new Intent(context, CallIncomingActivity.class);
        i.putExtras(b);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        context.startActivity(i);
    }


    private boolean isInConference() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName().equals(VCVideoActivity.class.getName())
                || cn.getClassName().equals(VCVideoP2PActivity.class.getName())
                || cn.getClassName().equals(VCVideoShiTongActivity.class.getName())
                || cn.getClassName().equals(VCVideoSimulcastActivity.class.getName());
    }

    private boolean isInvalidCall(IncomingCall inComingCall) {
        if (TextUtils.isEmpty(inComingCall.getTime())){
            Log.e(TAG, " InComingCall time is null " );
            return false;
        }
        long callTime = Long.parseLong(inComingCall.getTime());
        long nowTime = new Date().getTime()/1000;
        int diff = (int) (nowTime-callTime);
        if (diff > 30){
            return true;
        }
        return false;
    }

    private void finishIncomingView(Context context) {
        Intent intent = new Intent();
        intent.setAction("finish_incoming");
        context.sendBroadcast(intent);
    }
}
