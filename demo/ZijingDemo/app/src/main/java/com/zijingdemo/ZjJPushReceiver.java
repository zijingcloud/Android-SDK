package com.zijingdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wangzhen on 2017/11/7.
 */

public class ZjJPushReceiver extends BroadcastReceiver {

    private static final String TAG = "ZjJPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle.toString());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

            //消息内容已使用base64编码，需要先解码
            String json = base64DecodeMsg(bundle);
            Log.i(TAG, "onReceiveMsg: "+json);

            if (isInCallMsg(json)){ //收到入会消息
                ZjInComingCall inComingCall = parseIncomingMsg(json);
                if (isInvalidCall(inComingCall)){
                    Log.e(TAG, " isInvalidCall timeout 30 seconds ");
                    return;
                }
                showInComingView(context,inComingCall,json);
            } else if (isCancelMsg(json)){  //收到取消通话的消息
                finishIncomingView(context);
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.d(TAG, "用户收到了通知: "+json );
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private boolean isInvalidCall(ZjInComingCall inComingCall) {
        if (TextUtils.isEmpty(inComingCall.getTime())){
            Log.e(TAG, " InComingCall time is null " );
            return false;
        }
        long callTime = Long.parseLong(inComingCall.getTime());
        long nowTime = new Date().getTime()/1000;
        int diff = (int) (nowTime-callTime);
        if (diff>30){
            return true;
        }
        return false;
    }


    private void finishIncomingView(Context context) {
        Intent intent = new Intent();
        intent.setAction("finish_incoming");
        context.sendBroadcast(intent);
    }

    private boolean isInCallMsg(String json) {
        boolean isInCallMsg = false;
        try {
            JSONObject root = new JSONObject(json);
            String cmd = root.optString("service_type");
            if (cmd.equals("gateway") || cmd.equals("conference"))
                isInCallMsg = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isInCallMsg;
    }

    private boolean isCancelMsg(String json) {
        boolean isCancelCmd = false;
        try {
            JSONObject root = new JSONObject(json);
            String cmd = root.optString("command");
            if (cmd.equals("call_cancelled"))
                isCancelCmd = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isCancelCmd;
    }


    private String base64DecodeMsg(Bundle bundle) {
        String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        return new String(Base64.decode(msg, Base64.CRLF));
    }

    private void showInComingView(Context context, ZjInComingCall inComingCall,String json) {
        Bundle b = new Bundle();
        b.putSerializable("inComingCall",inComingCall);
        b.putString("json",json);
        Intent i = new Intent(context, CallIncomingActivity.class);
        i.putExtras(b);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        context.startActivity(i);
    }


    private ZjInComingCall parseIncomingMsg(String json) {
        try {
            JSONObject root = new JSONObject(json);
            String type = root.optString("service_type");
            String token = root.optString("token");
            String remoteAlias = root.optString("remote_alias");
            String remoteName = root.optString("remote_display_name");
            String conference = root.optString("conference_alias");
            String time = root.optString("time");
            ZjInComingCall inComingCall = new ZjInComingCall(remoteAlias, remoteName, conference, type, token,time);
            Log.i(TAG, "inComingCall: "+inComingCall.toString());
            return inComingCall;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
