package com.zijingdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by wangzhen on 2018/1/23.
 */

public class LoginActivity extends Activity {

    public static final String URL_REGISTER = "https://mcu.myvmr.cn/api/registrations/%s/new_session";
    public static final String JPUSH_APPKEY = "faa244992c1d3d35530e3607";
    private static final String TAG = "LoginActivity";

    private EditText accountEdit, pwdEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        accountEdit = (EditText) findViewById(R.id.account);
        pwdEdit = (EditText) findViewById(R.id.pwd);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    /**
     * 1.账号密码使用Base64编码，放在post请求的Header中
     * 2.极光推送的RegistrationID和APPKey放在post请求的device_id参数中
     */

    private void register() {

        //1.url
        //  将account放入url中
        String address = accountEdit.getText().toString();
        String name = address.split("@")[0];
        String addressEncoded = null;
        try {
            addressEncoded = URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(URL_REGISTER, addressEncoded);
        //2.Header
        //  把"用户名:密码"的base64编码放在请求的Header的认证信息中
        //  举例：
        //  账号test@zijingcloud.com，账号密码123456。
        //  用户名为test，将test:123456进行Base64编码为dGVzdDoxMjM0NTY=
        //  "X-Cloud-Authorization" "x-cloud-basic dGVzdDoxMjM0NTY="
        //  "Authorization" "x-cloud-basic dGVzdDoxMjM0NTY="
        String pwd = pwdEdit.getText().toString();
        byte[] bytes = (name + ":" + pwd).getBytes();
        String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("X-Cloud-Authorization", "x-cloud-basic " + base64);
        headersMap.put("Authorization", "x-cloud-basic " + base64);
        //3.Body
        //  device_id，格式：极光推送RegistrationID__极光推送APPKEY
        final String regId = JPushInterface.getRegistrationID(this);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("device_id", regId + "__" + JPUSH_APPKEY);
        paramsMap.put("device_type", "android");
        OkHttpUtils.doPost(
                url,
                paramsMap,
                headersMap,
                new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onResponse: " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() == 200){
                            Log.i(TAG, "onResponse: 登录成功");
                            finish();
                        }else{
                            Log.e(TAG, "onResponse: "+response.code());
                        }
                    }
                }
        );

    }
}
