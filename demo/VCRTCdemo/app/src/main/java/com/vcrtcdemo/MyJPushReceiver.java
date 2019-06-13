package com.vcrtcdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.vcrtc.VCRTCPreferences;
import com.vcrtc.utils.OkHttpUtil;
import com.vcrtcdemo.entities.InComingCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyJPushReceiver extends BroadcastReceiver {

    private static final String TAG = "MyJPushReceiver";

    public static boolean isShowIncoming = false;

    private VCRTCPreferences vcPrefs;

    @Override
    public void onReceive(Context context, Intent intent) {

        vcPrefs = new VCRTCPreferences(context);

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle.toString());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

            //消息内容已使用base64编码，需要先解码
            String json = base64DecodeMsg(bundle);
            Log.i(TAG, "onReceive: msg >>>>>"+json);

            if (isCancelMsg(json)){//对方取消呼叫，收到取消通知
                finishIncomingView(context);
            } else {
                InComingCall inComingCall = parseIncomingMsg(json);
                if (isInvalidCall(inComingCall)){
                    Log.e(TAG, " isInvalidCall timeout 30 second ");
                    return;
                }
                if (isShowIncoming){ //已经在通话中，直接挂掉；
                    decline(inComingCall);
                } else { //没有在通话中，显示来电界面
                    isShowIncoming = true;
                    showInComingView(context,inComingCall);
                }
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.d(TAG, "用户收到了通知: "+ json);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private boolean isInvalidCall(InComingCall inComingCall) {
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

    private void showInComingView(Context context, InComingCall inComingCall) {
        Bundle b = new Bundle();
        b.putSerializable("inComingCall", inComingCall);
        Intent i = new Intent(context, CallIncomingActivity.class);
        i.putExtras(b);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        context.startActivity(i);
    }


    private InComingCall parseIncomingMsg(String json) {
        try {
            JSONObject root = new JSONObject(json);
            String type = root.optString("service_type");
            String token = root.optString("token");
            String remoteAlias = root.optString("remote_alias");
            String remoteName = root.optString("remote_display_name");
            String conference = root.optString("conference_alias");
            String time = root.optString("time");
            String bssKey = root.optString("bsskey");
            InComingCall inComingCall = new InComingCall(remoteAlias, remoteName, conference, type, token, time, bssKey, json);
            Log.i(TAG, "inComingCall: "+inComingCall.toString());
            return inComingCall;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void decline(InComingCall inComingCall) {
        String address = inComingCall.getConference();
        String addressEncoded = null;
        try {
            addressEncoded = URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = String.format("https://" + vcPrefs.getMcuHost() + "/api/services/%s/end_session?token=%s", addressEncoded, inComingCall.getToken());

        JSONObject jsonObject = new JSONObject();

        OkHttpUtil.doPost(url, jsonObject.toString(), null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {

            }
        });
    }
}
