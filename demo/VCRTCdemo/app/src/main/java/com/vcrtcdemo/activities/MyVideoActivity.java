package com.vcrtcdemo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.vcrtc.VCRTC;
import com.vcrtc.VCRTCPreferences;
import com.vcrtc.VCRTCView;
import com.vcrtc.callbacks.CallBack;
import com.vcrtc.listeners.VCRTCListener;
import com.vcrtc.listeners.VCRTCListenerImpl;
import com.vcrtcdemo.R;

public class MyVideoActivity extends AppCompatActivity {

    private FrameLayout flLocal,flRemote;

    private VCRTC vcrtc;

    private boolean isMuteAudio, isMuteVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        flLocal = findViewById(R.id.fl_local);
        flRemote = findViewById(R.id.fl_remote);

        VCRTCPreferences prefs = new VCRTCPreferences(this);
        prefs.setSimulcast(false);

        makeCall();
    }

    private void makeCall() {
        vcrtc = new VCRTC(this);
        vcrtc.setVCRTCListener(listener);
        vcrtc.connect("1865", "123456", "王富贵", new CallBack() {
            @Override
            public void success(String s) {

            }

            @Override
            public void failure(String reason) {

            }
        });
    }

    public void muteAudio(View view) {
        vcrtc.setAudioEnable(isMuteAudio);
        isMuteAudio = !isMuteAudio;
    }

    public void muteVideo(View view) {
        vcrtc.setVideoEnable(isMuteVideo);
        isMuteVideo = !isMuteVideo;
    }

    public void switchCamera(View view) {
        vcrtc.switchCamera();
    }

    public void disconnect(View view) {
        vcrtc.disconnect();
    }

    private VCRTCListener listener = new VCRTCListenerImpl() {

        @Override
        public void onLocalVideo(String uuid, VCRTCView view) {
            view.setMirror(true);
            flLocal.addView(view,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        }

        @Override
        public void onRemoteVideo(String uuid, VCRTCView view) {
            flRemote.addView(view,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        }
    };
}
