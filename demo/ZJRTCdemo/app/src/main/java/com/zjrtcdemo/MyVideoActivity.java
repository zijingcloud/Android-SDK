package com.zjrtcdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.zjrtc.ZJRTC;
import com.zjrtc.ZJRTCPreferences;
import com.zjrtc.ZJRTCView;
import com.zjrtc.callbacks.CallBack;
import com.zjrtc.entities.ResponseCode;
import com.zjrtc.listeners.ZJRTCListener;
import com.zjrtc.listeners.ZJRTCListenerImpl;

public class MyVideoActivity extends AppCompatActivity {

    private FrameLayout flLocal,flRemote;

    private ZJRTC zjrtc;

    private boolean isMuteAudio, isMuteVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        flLocal = findViewById(R.id.fl_local);
        flRemote = findViewById(R.id.fl_remote);

        ZJRTCPreferences prefs = new ZJRTCPreferences(this);
        prefs.setSimulcast(false);

        makeCall();
    }

    private void makeCall() {
        zjrtc = new ZJRTC(this);
        zjrtc.setZJRTCListener(listener);
        zjrtc.connect("1865", "123456", "王富贵", new CallBack() {
            @Override
            public void success(String s) {

            }

            @Override
            public void failure(ResponseCode responseCode) {

            }
        });
    }

    public void muteAudio(View view) {
        zjrtc.muteAudio(isMuteAudio);
        isMuteAudio = !isMuteAudio;
    }

    public void muteVideo(View view) {
        zjrtc.muteVideo(isMuteVideo);
        isMuteVideo = !isMuteVideo;
    }

    public void switchCamera(View view) {
        zjrtc.switchCamera();
    }

    public void disconnect(View view) {
        zjrtc.disconnect();
    }

    private ZJRTCListener listener = new ZJRTCListenerImpl() {

        @Override
        public void onLocalVideo(String uuid, ZJRTCView view) {
            view.setMirror(true);
            flLocal.addView(view,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        }

        @Override
        public void onRemoteVideo(String uuid, ZJRTCView view) {
            flRemote.addView(view,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        }
    };
}
