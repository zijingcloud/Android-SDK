package com.zijingdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjrtc.ZjCall;
import com.zjrtc.ZjVideoActivity;
import com.zjrtc.ZjVideoManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CallIncomingActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String URL_DECLINE_CALL = "https://mcu.myvmr.cn/api/services/%s/end_session?token=%s";
    private static final String TAG = "CallIncomingActivity";

    private TextView name;
    private ImageView contactPicture;
    private ZjInComingCall inComingCall;
    private String msgJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.call_incoming);

        name = (TextView) findViewById(R.id.call_in_name);
        contactPicture = (ImageView) findViewById(R.id.call_in_avatar);

        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().addFlags(flags);

        findViewById(R.id.call_in_hang_up).setOnClickListener(this);
        findViewById(R.id.call_in_accept_audio).setOnClickListener(this);

        inComingCall = (ZjInComingCall) getIntent().getSerializableExtra("inComingCall");
        msgJson = (String) getIntent().getSerializableExtra("json");
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

    private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private void answer() {
        ZjCall call = new ZjCall();
        call.setMsgJson(msgJson);
        Intent intent = new Intent(this,ZjVideoActivity.class);
        intent.putExtra("call",call);
        startActivity(intent);
    }

    private void decline(ZjInComingCall inComingCall) {
        String address = inComingCall.getConference();
        String addressEncoded = null;
        try {
            addressEncoded = URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(URL_DECLINE_CALL, addressEncoded,inComingCall.getToken());
        OkHttpUtils.doPost(
                url,
                "",
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onResponse: " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e(TAG, "onResponse: "+response.toString());
                    }
                }
        );
    }

    private Ringtone ringtone;
    private Vibrator vibrator;

    @Override
    protected void onResume() {
        super.onResume();

        //铃声
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (uri == null) return;
        ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(exitReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ringtone != null && ringtone.isPlaying()){
            ringtone.stop();
            ringtone = null;
        }
        if (vibrator !=null){
            vibrator.cancel();
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
                answer();
                finish();
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZjInComingCall inComingCall = (ZjInComingCall) intent.getSerializableExtra("inComingCall");
        decline(inComingCall);
    }
}
