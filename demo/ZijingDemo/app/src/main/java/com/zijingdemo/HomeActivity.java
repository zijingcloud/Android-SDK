package com.zijingdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.zjrtc.ZjCallListenerBase;
import com.zjrtc.ZjVideoActivity;
import com.zjrtc.ZjVideoManager;
import com.zjrtc.ZjVideoPreferences;

/**
 * Created by wangzhen on 2017/8/21.
 */

public class HomeActivity extends Activity implements View.OnClickListener{

    private final String TAG = "HomeActivity";

    private EditText domain,displayName,address,pwd,
            upWidth,upHeight,downWidth,downHeight,upBw,downBw,upFps,downFps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        findViewById(R.id.join).setOnClickListener(this);
        domain = (EditText) findViewById(R.id.domain);
        displayName = (EditText) findViewById(R.id.displayName);
        address = (EditText) findViewById(R.id.address);
        pwd = (EditText) findViewById(R.id.pwd);
        upWidth = (EditText) findViewById(R.id.up_width);
        upHeight = (EditText) findViewById(R.id.up_height);
        downWidth = (EditText) findViewById(R.id.down_width);
        downHeight = (EditText) findViewById(R.id.down_height);
        upFps = (EditText) findViewById(R.id.up_fps);
        downFps = (EditText) findViewById(R.id.down_fps);
        upBw = (EditText) findViewById(R.id.up_bw);
        downBw = (EditText) findViewById(R.id.down_bw);

    }

    @Override
    public void onClick(View v) {
        //设置服务器地址、显示名称、呼叫地址、呼叫密码；每次呼叫都需要进行设置
        ZjVideoManager manager = ZjVideoManager.getInstance();
        manager.setDisplayName(displayName.getText().toString());
        manager.setAddress(address.getText().toString());
        manager.setPwd(pwd.getText().toString());   //有密码则需要设置，没有密码不需要设置


        int up_w = 0,up_h = 0,down_w = 0,down_h = 0,up_fps = 0,down_fps = 0,up_bw = 0,down_bw = 0;
        if (!TextUtils.isEmpty(upWidth.getText().toString())){
            up_w = Integer.parseInt(upWidth.getText().toString());
        }
        if (!TextUtils.isEmpty(upHeight.getText().toString())) {
            up_h = Integer.parseInt(upHeight.getText().toString());
        }
        if (!TextUtils.isEmpty(downWidth.getText().toString())) {
            down_w = Integer.parseInt(downWidth.getText().toString());
        }
        if (!TextUtils.isEmpty(downHeight.getText().toString())) {
            down_h = Integer.parseInt(downHeight.getText().toString());
        }
        if (!TextUtils.isEmpty(upFps.getText().toString())) {
            up_fps = Integer.parseInt(upFps.getText().toString());
        }
        if (!TextUtils.isEmpty(downFps.getText().toString())) {
            down_fps = Integer.parseInt(downFps.getText().toString());
        }
        if (!TextUtils.isEmpty(upBw.getText().toString())) {
            up_bw = Integer.parseInt(upBw.getText().toString());
        }
        if (!TextUtils.isEmpty(downBw.getText().toString())) {
            down_bw = Integer.parseInt(downBw.getText().toString());
        }

        //设置视频参数，设置一次即可生效，不用每次呼叫前都设置
        ZjVideoPreferences prefs = new ZjVideoPreferences(this);
        prefs.setDomain(domain.getText().toString());
        prefs.setVideoSize(up_w,up_h,down_w,down_h);
        prefs.setBandwidth(up_bw,down_bw);
        prefs.setVideoFps(up_fps,down_fps);

        //其他功能
//        prefs.setTvSupport(true);     //使用TV或盒子
//        prefs.setSoftCode(true);      //关闭硬编解，使用软编解
//        prefs.setPrintLogs(true);     //打印日志
        ZjVideoManager.getInstance().addZjCallListener(new ZjCallListenerBase(){
            @Override
            public void videoState(String state) {
                super.videoState(state);
                Log.i(TAG, "videoState: "+state );
            }

            @Override
            public void callState(String state, String info) {
                super.callState(state, info);
                Log.i(TAG, "callState: "+state+"  "+info);
            }
        });

        prefs.setHideRNUI(false);
        startActivity(new Intent(this,ZjVideoActivity.class));//启动手机会中界面

        //如果需要使用自定义通话界面
//        prefs.setHideRNUI(true);
//        startActivity(new Intent(this,MyVideoActivity.class));//启动自定义会中界面

    }
}
