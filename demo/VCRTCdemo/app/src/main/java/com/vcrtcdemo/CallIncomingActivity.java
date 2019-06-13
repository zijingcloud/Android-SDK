package com.vcrtcdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.vcrtc.VCRTCPreferences;
import com.vcrtc.activities.VCVideoActivity;
import com.vcrtc.activities.VCVideoP2PActivity;
import com.vcrtc.activities.VCVideoShiTongActivity;
import com.vcrtc.activities.VCVideoSimulcastActivity;
import com.vcrtc.utils.OkHttpUtil;
import com.vcrtcdemo.entities.InComingCall;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CallIncomingActivity extends AppCompatActivity implements View.OnClickListener {

    private VCRTCPreferences vcPrefs;
    private TextView name;
    private InComingCall inComingCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_call_incoming);

        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().addFlags(flags);

        name = (TextView) findViewById(R.id.call_in_name);
        findViewById(R.id.call_in_hang_up).setOnClickListener(this);
        findViewById(R.id.call_in_accept_audio).setOnClickListener(this);

        inComingCall = (InComingCall) getIntent().getSerializableExtra("inComingCall");

        VCRTCPreferences prefs = new VCRTCPreferences(this);
        if (prefs.isShiTongPlatform() && inComingCall.getRemoteName().contains("@")) {
            inComingCall.setRemoteName(inComingCall.getRemoteName().split("@")[0]);
        }
        name.setText(inComingCall.getRemoteName());

        String remoteAlias = inComingCall.getRemoteAlias();
        //remoteAlias优先显示账号信息，否则为显示名称
        if (remoteAlias.contains("@")){
            //点对点呼叫，账号前会加sip:
            if (remoteAlias.contains("sip")){
                String alias = remoteAlias.split(":")[1];
                inComingCall.setRemoteAlias(alias);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_in_hang_up:
                decline(inComingCall);
                finish();
                break;
            case R.id.call_in_accept_audio:
                answer(inComingCall);
                finish();
                break;
        }
    }

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onResume() {
        super.onResume();

        vcPrefs = new VCRTCPreferences(this);

        //铃声
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        try {
            if (uri != null) {
                mediaPlayer = MediaPlayer.create(this, uri);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //震动
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            long[] pattern = {1000,1000,1000,1000};
            vibrator.vibrate(pattern,2);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("finish_incoming");
        registerReceiver(exitReceiver,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(exitReceiver);
        MyJPushReceiver.isShowIncoming = false;
    }

    private void answer(InComingCall inComingCall) {
        com.vcrtc.entities.Call call = new com.vcrtc.entities.Call();
        call.setChanel(inComingCall.getConference());
        call.setNickname("我的名字");
        call.setMsgJson(inComingCall.getMsgJson());

        Intent intent;

        if (inComingCall.getType().equals("gateway")) { //点对点被呼
            intent = new Intent(this, VCVideoP2PActivity.class);
        } else {
            if (vcPrefs.isShiTongPlatform()) {
                intent = new Intent(this, VCVideoShiTongActivity.class);
            } else {
                if (vcPrefs.isSimulcast()) {
                    intent = new Intent(this, VCVideoSimulcastActivity.class);
                } else {
                    intent = new Intent(this, VCVideoActivity.class);
                }
            }
        }

        intent.putExtra("call",call);
        startActivity(intent);

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

    private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        InComingCall inComingCall = (InComingCall) intent.getSerializableExtra("inComingCall");
        decline(inComingCall);
    }
}
