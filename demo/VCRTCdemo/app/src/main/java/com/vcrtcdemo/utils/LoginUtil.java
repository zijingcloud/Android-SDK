package com.vcrtcdemo.utils;

import android.content.Context;
import android.util.Base64;

import com.vcrtc.VCRTCPreferences;
import com.vcrtc.callbacks.CallBack;
import com.vcrtc.utils.OkHttpUtil;
import com.vcrtcdemo.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginUtil {

    private Context context;
    private VCRTCPreferences vcPrefs;
    private String account;
    private String password;
    private CallBack callBack;

    private String sipkey = "";

    public LoginUtil(Context context, String account, String password) {
        vcPrefs = new VCRTCPreferences(context);
        this.context = context;
        this.account = account;
        this.password = password;
    }

    public void login(CallBack callBack) {
        this.callBack = callBack;
        userVerify();
    }

    private void userVerify() {

        String url = "https://" + vcPrefs.getApiServer() + "/api/v3/app/user/login/verify_user.shtml";

        JSONObject params = new JSONObject();
        try {
            params.put("account", account);
            params.put("pwd", password);
            params.put("host", vcPrefs.getApiServer());
            params.put("plat_type", "webrtcand");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> header = new HashMap<>();
        header.put("Accept-Charset", "utf-8");

        OkHttpUtil.doPost(url, params.toString(), header, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.failure("login failed. " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        JSONObject root = new JSONObject(response.body().string());
                        if (root.getInt("code") == 200) {
                            JSONObject results = root.optJSONObject("results");
                            sipkey = results.optString("sipkey");
                            if (results.has("userName")) {
                                account = results.optString("userName");
                            }
                            String sessionId = results.optString("session_id");
                            registerMcu(sessionId);
                        } else {
                            callBack.failure("login failed " + root.optString("results"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callBack.failure("login failed. " + e.getMessage());
                    }
                } else {
                    callBack.failure("login failed. " + response.code());
                }
            }
        });
    }

    private void registerMcu(String sessionId) {
        String address = account;
        String name = address.split("@")[0];
        String addressEncoded = null;
        try {
            addressEncoded = URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = String.format("https://" + vcPrefs.getMcuHost() + "/api/registrations/%s/new_session", addressEncoded);

        if (vcPrefs.isShiTongPlatform()) {
            url = "https://" + vcPrefs.getApiServer() + "/api/v3/app/registrations/jpush_token";
        }

        byte[] bytes = (name + ":" + password).getBytes();
        String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
        String regId = JPushInterface.getRegistrationID(context);

        Map<String, String> header = new HashMap<>();
        header.put("X-Cloud-Authorization","x-cloud-basic "+base64);
        header.put("Authorization","x-cloud-basic "+base64);
        header.put("sessionId", sessionId);

        if (vcPrefs.isShiTongPlatform()) {

            JSONObject params = new JSONObject();
            try {
                params.put("device_id", regId + "__" + BuildConfig.JPUSH_APP_KEY);
                params.put("device_type", "android");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpUtil.doPost(url, params.toString(), header, registerCallback);

        } else {

            Map<String, String> params = new HashMap<>();
            params.put("device_id", regId + "__" + BuildConfig.JPUSH_APP_KEY);
            params.put("device_type", "android");

            OkHttpUtil.doPost(url, params, header, registerCallback);

        }
    }

    private Callback registerCallback = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            callBack.failure("login failed. " + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) {
            if (response.code() == 200) {
                //登录成功，sipkey为公有云被呼短号；没有则账号未入网
                callBack.success("login success." + sipkey);
            } else {
                callBack.failure("login failed.");
            }
        }
    };
}
