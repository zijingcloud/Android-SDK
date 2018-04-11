package com.zijingdemo;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by wangzhen on 2018/1/23.
 */

public class OkHttpUtils {

    private static OkHttpClient client;

    private OkHttpUtils() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        builder.build();
    }

    public static synchronized OkHttpClient getInstance() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    public static void doGet(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    public static void doPost(String url, Map<String,String> paramsMap,Map<String,String> headersMap,Callback callback){
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (String key: paramsMap.keySet()){
            bodyBuilder.add(key,paramsMap.get(key));
        }

        Request.Builder requestBuilder = new Request.Builder();
        for (String key:headersMap.keySet()){
            requestBuilder.addHeader(key,headersMap.get(key));
        }
        Request request = requestBuilder
                .url(url)
                .post(bodyBuilder.build())
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    public static void doPost(String url,String jsonParams,Callback callback){
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonParams);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url)
                .post(body)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

}
