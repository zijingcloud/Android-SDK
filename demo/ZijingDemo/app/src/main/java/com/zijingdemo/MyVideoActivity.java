package com.zijingdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zjrtc.ZjCallListenerBase;
import com.zjrtc.ZjVideoActivity;
import com.zjrtc.ZjVideoManager;


/**
 * Created by wangzhen on 2017/12/6.
 * 自定义会中界面
 */

public class MyVideoActivity extends ZjVideoActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.video_layout, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(view, params);

        ZjVideoManager.getInstance().addZjCallListener(new ZjCallListenerBase(){
            @Override
            public void videoState(String state) {
                super.videoState(state);
                Toast.makeText(MyVideoActivity.this,"videoState: "+state,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void callState(String state, String info) {
                super.callState(state, info);
                Toast.makeText(MyVideoActivity.this,"callState: "+state+"  "+info,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
