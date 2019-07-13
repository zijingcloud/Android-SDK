package com.vcrtcdemo.activities.conference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vcrtc.VCRTC;
import com.vcrtc.VCRTCView;
import com.vcrtc.adapters.StatsAdapter;
import com.vcrtc.callbacks.CallBack;
import com.vcrtc.entities.Call;
import com.vcrtc.entities.ErrorCode;
import com.vcrtc.entities.MediaStats;
import com.vcrtc.entities.Participant;
import com.vcrtc.entities.StatsItemBean;
import com.vcrtc.listeners.VCRTCListener;
import com.vcrtc.listeners.VCRTCListenerImpl;
import com.vcrtc.utils.VCAudioManager;
import com.vcrtc.utils.VCHomeListener;
import com.vcrtc.utils.VCNetworkListener;
import com.vcrtc.utils.VCPhoneListener;
import com.vcrtc.utils.VCUtil;
import com.vcrtc.widget.DragFrameLayout;
import com.vcrtcdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VCVideoP2PActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "VCVideoP2PActivity";

    private final int HIDE_BAR = 1;
    private final int REFRESH_TIME = 2;
    private final int REFRESH_STATS = 3;
    private final int DISCONNECT_AND_FINISH = 4;

    private RelativeLayout rlRootView, rlTopBar, rlToolBar, rlCallOut, rlLoading;
    private DragFrameLayout flLocalVideo;
    private FrameLayout flRemoteVideo;
    private TextView tvTime, tvChanel, tvCallName;
    private LinearLayout llStats;
    private ImageView ivMuteAudio, ivMuteVideo, ivSwitchCamera, ivShare, ivMore, ivHangup, ivCancel, ivCircle, ivSignal;
    private PopupWindow popupWindowStats;

    private FrameLayout.LayoutParams layoutParams;

    private VCRTC vcrtc;
    private VCRTCView localView;
    private VCAudioManager audioManager;
    private VCAudioManager.AudioDevice audioDevice;
    private VCHomeListener homeListener;
    private VCPhoneListener phoneListener;
    private VCNetworkListener networkListener;
    private Call call;
    private Timer hideBarTimer;
    private Timer durationTimer;
    private Timer getStatsTimer;
    private Timer noNetworkTimer;
    private TimerTask hideBarTimerTask;
    private TimerTask getStatsTimerTask;
    private TimerTask noNetworkToFinishTask;
    private int time;
    private boolean isShowBar, isMuteAudio, isMuteVideo, isFront;
    private List<StatsItemBean> itemBeanList;
    private StatsAdapter adapter;
    private boolean isForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_vcvideo_p2p);

        call = (Call) getIntent().getSerializableExtra("call");
        if (call == null) {
            Toast.makeText(getApplicationContext(),"请设置呼叫参数call不能为null",Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            if (call.getNickname() == null || "".equals(call.getNickname())) {
                Toast.makeText(getApplicationContext(), "请设置显示名称",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (call.getChannel() == null || "".equals(call.getChannel())) {
                Toast.makeText(getApplicationContext(), "请设置呼叫地址",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        initView();
        initData();
        makeCall();
        showBar();
        showLoading();
        initMediaStatsWindow();
    }

    private void initView() {
        rlRootView = findViewById(R.id.rl_vc_video_p2p_root_view);
        rlLoading = findViewById(R.id.layout_loading);
        rlCallOut = findViewById(R.id.rl_call_out);
        rlTopBar = findViewById(R.id.rl_top_bar);
        rlToolBar = findViewById(R.id.rl_tool_bar);
        flLocalVideo = findViewById(R.id.fl_local_video);
        flRemoteVideo = findViewById(R.id.fl_remote_video);
        tvTime = findViewById(R.id.tv_time);
        tvChanel = findViewById(R.id.tv_room_num);
        tvCallName = findViewById(R.id.tv_call_name);
        llStats = findViewById(R.id.ll_stats);
        ivMuteAudio = findViewById(R.id.iv_mute_audio);
        ivMuteVideo = findViewById(R.id.iv_mute_video);
        ivSwitchCamera = findViewById(R.id.iv_switch_camera);
        ivShare = findViewById(R.id.iv_share);
        ivMore = findViewById(R.id.iv_more);
        ivHangup = findViewById(R.id.iv_hangup);
        ivCancel = findViewById(R.id.iv_call_cancel);
        ivCircle = findViewById(R.id.iv_circle);
        ivSignal = findViewById(R.id.iv_signal);

        flRemoteVideo.setOnClickListener(this);
        llStats.setOnClickListener(this);
        ivMuteAudio.setOnClickListener(this);
        ivMuteVideo.setOnClickListener(this);
        ivSwitchCamera.setOnClickListener(this);
        ivHangup.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
    }

    private void initData() {

        tvChanel.setText(call.getChannel());
        ivShare.setVisibility(View.GONE);
        ivMore.setVisibility(View.GONE);

        vcrtc = new VCRTC(this);

        audioManager = VCAudioManager.create(getApplicationContext());
        // This method will be called each time the number of available audio devices has changed.
        audioManager.start((audioDevice, availableAudioDevices) -> this.audioDevice = audioDevice);

        homeListener = new VCHomeListener(getApplicationContext());
        homeListener.setKeyListener(new VCHomeListener.KeyListener() {
            @Override
            public void home() {
                if (!isMuteVideo) {
                    vcrtc.setVideoEnable(false);
                }
            }

            @Override
            public void recent() {

            }

            @Override
            public void longHome() {

            }
        });

        phoneListener = new VCPhoneListener(getApplicationContext());
        phoneListener.setPhoneListener(new VCPhoneListener.MyPhoneStateListener() {
            @Override
            public void onIdle() {
                if (audioDevice.equals(VCAudioManager.AudioDevice.BLUETOOTH)) {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    audioManager.startBluetoothSco();
                    audioManager.setBluetoothScoOn(true);
                    audioManager.setSpeakerphoneOn(false);
                } else {
                    audioManager.selectAudioDevice(audioDevice);
                }
                if (!isMuteAudio) {
                    vcrtc.setAudioEnable(true);
                    ivMuteAudio.setSelected(false);
                }
                if (!isMuteVideo) {
                    vcrtc.setVideoEnable(true);
                    ivMuteVideo.setSelected(false);
                }
            }

            @Override
            public void onOffhook() {
                if (!isMuteAudio) {
                    vcrtc.setAudioEnable(false);
                }
                if (!isMuteVideo) {
                    vcrtc.setVideoEnable(false);
                }
            }
        });

        networkListener = new VCNetworkListener(getApplicationContext());
        networkListener.setmNetworkListener(new VCNetworkListener.NetworkListener() {
            @Override
            public void onNetworkChange(int networkType) {
                stopNoNetworkTimer();
                vcrtc.reconnectOnlyMediaCall();
            }

            @Override
            public void onNetworkDisconnect() {
                startNoNetworkTimer();
            }
        });

        if (call.isCallOut()) {
            tvCallName.setText(call.getCallName());
            rlCallOut.setVisibility(View.VISIBLE);
        } else {
            try {
                JSONObject root = new JSONObject(call.getMsgJson());
                String chanel = root.optString("conference_alias");
                String time = root.optString("time");
                String bssKey = root.optString("bsskey");
                String token = root.optString("token");
                call.setChannel(chanel);
                vcrtc.setTime(time);
                vcrtc.setBsskey(bssKey);
                vcrtc.setOneTimeToken(token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        isMuteAudio = false;
        isMuteVideo = false;
        isFront = true;
        itemBeanList = new ArrayList<>();
    }

    private void startNoNetworkTimer() {
        if (noNetworkTimer == null) {
            noNetworkTimer = new Timer();
        }
        if (noNetworkToFinishTask == null) {
            noNetworkToFinishTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(DISCONNECT_AND_FINISH);
                }
            };
        }

        noNetworkTimer.schedule(noNetworkToFinishTask, 40000);
    }

    private void stopNoNetworkTimer() {
        if (noNetworkToFinishTask != null) {
            noNetworkToFinishTask.cancel();
            noNetworkToFinishTask = null;
        }
        if (noNetworkTimer != null) {
            noNetworkTimer.cancel();
            noNetworkTimer = null;
        }
    }

    private void makeCall() {
        vcrtc.setVCRTCListener(listener);
        vcrtc.setAccount(call.getAccount());
        vcrtc.connect(call.getChannel(), call.getPassword(), call.getNickname(), new CallBack() {
            @Override
            public void success(String response) {

            }

            @Override
            public void failure(String reason) {
                runOnUiThread(() -> {if (isForeground) showErrorDialog(getString(R.string.call_failed), getString(R.string.can_not_connect_server));});
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDisconnectDialog();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_call_cancel) {
            disconnect();
        } else if (i == R.id.fl_remote_video) {
            if (isShowBar) {
                hideBar();
            } else {
                showBar();
            }
        } else if (i == R.id.iv_mute_audio) {
            toggleMuteAudio();
            showBar();
        } else if (i == R.id.iv_mute_video) {
            toggleMuteVideo();
            showBar();
        } else if (i == R.id.iv_switch_camera) {
            switchCamera();
            showBar();
        } else if (i == R.id.iv_hangup) {
            showDisconnectDialog();
            hideBar();
        } else if (i == R.id.ll_stats) {
            startGetStats();
            showMediaStatsWindow();
        } else if (i == R.id.iv_close) {
            popupWindowStats.dismiss();
            stopGetStats();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        if (!isMuteAudio) {
            vcrtc.setAudioEnable(true);
        }
        if (!isMuteVideo) {
            vcrtc.setVideoEnable(true);
        }
        homeListener.startListen();
        phoneListener.startListen();
        networkListener.startListen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        homeListener.stopListen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (homeListener != null) {
            homeListener = null;
        }
        if (phoneListener != null) {
            phoneListener.stopListen();
            phoneListener = null;
        }
        if (networkListener != null) {
            networkListener.stopListen();
            networkListener = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_BAR:
                    hideBar();
                    break;
                case REFRESH_TIME:
                    refreshTime();
                    break;
                case REFRESH_STATS:
                    refreshStats((double) msg.obj);
                    break;
                case DISCONNECT_AND_FINISH:
                    disconnect();
                    break;
            }
        }
    };

    private void startGetStats() {
        if (getStatsTimer == null) {
            getStatsTimer = new Timer();
        }
        if (getStatsTimerTask == null) {
            getStatsTimerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        itemBeanList.clear();
                        double fractionLost = 0;
                        List<MediaStats> stats = vcrtc.getMediaStatistics();

                        for (MediaStats mediaStats : stats) {
                            if (mediaStats.getMediaType().equals("audio") && mediaStats.getDirection().equals("out")) {
                                StatsItemBean bean = new StatsItemBean();
                                bean.setTerminal(getString(R.string.stats_local));
                                bean.setChanel(getString(R.string.stats_audio_send));
                                bean.setCodec(mediaStats.getCodec());
                                bean.setResolution("--");
                                bean.setFrameRate("--");
                                bean.setBitRate(mediaStats.getBitrate() + "");
                                bean.setFractionLost(mediaStats.getFractionLost());
                                itemBeanList.add(bean);
                                break;
                            }
                        }
                        for (MediaStats mediaStats : stats) {
                            if (mediaStats.getMediaType().equals("audio") && mediaStats.getDirection().equals("in")) {
                                StatsItemBean bean = new StatsItemBean();
                                bean.setTerminal(getString(R.string.stats_remote));
                                bean.setChanel(getString(R.string.stats_audio_recv));
                                bean.setCodec(mediaStats.getCodec());
                                bean.setResolution("--");
                                bean.setFrameRate("--");
                                bean.setBitRate(mediaStats.getBitrate() + "");
                                bean.setFractionLost(mediaStats.getFractionLost());
                                itemBeanList.add(bean);
                                break;
                            }
                        }
                        for (MediaStats mediaStats : stats) {
                            if (mediaStats.getMediaType().equals("video") && mediaStats.getDirection().equals("out")) {
                                StatsItemBean bean = new StatsItemBean();
                                bean.setTerminal(getString(R.string.stats_local));
                                bean.setChanel(getString(R.string.stats_video_send));
                                bean.setCodec(mediaStats.getCodec());
                                bean.setResolution(mediaStats.getResolution());
                                bean.setFrameRate(mediaStats.getFrameRate() + "");
                                bean.setBitRate(mediaStats.getBitrate() + "");
                                bean.setFractionLost(mediaStats.getFractionLost());
                                itemBeanList.add(bean);
                            }
                        }
                        for (MediaStats mediaStats : stats) {
                            if (mediaStats.getMediaType().equals("video") && mediaStats.getDirection().equals("in")) {
                                StatsItemBean bean = new StatsItemBean();
                                bean.setTerminal(call.getCallName());
                                bean.setChanel(getString(R.string.stats_video_recv));
                                bean.setCodec(mediaStats.getCodec());
                                bean.setResolution(mediaStats.getResolution());
                                bean.setFrameRate(mediaStats.getFrameRate() + "");
                                bean.setBitRate(mediaStats.getBitrate() + "");
                                bean.setFractionLost(mediaStats.getFractionLost());
                                itemBeanList.add(bean);
                            }
                        }
                        for (MediaStats mediaStats : stats) {
                            fractionLost += mediaStats.getFractionLost();
                        }
                        Message msg = Message.obtain();
                        msg.what = REFRESH_STATS;
                        msg.obj = fractionLost;
                        mHandler.sendMessage(msg);
                    } catch (Exception e) {
                        Log.i(TAG,"getMediaStatistics exception");
                    }
                }
            };
        }
        if (getStatsTimer != null && getStatsTimerTask != null) {
            try {
                getStatsTimer.schedule(getStatsTimerTask, 0, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopGetStats() {
        if (getStatsTimer != null) {
            getStatsTimer.cancel();
            getStatsTimer = null;
        }
        if (getStatsTimerTask != null) {
            getStatsTimerTask.cancel();
            getStatsTimerTask = null;
        }
    }

    private void refreshStats(double ractionLost) {
        if (ractionLost == 0) {
            ivSignal.setImageResource(R.mipmap.icon_signal1);
        } else if (ractionLost > 0 && ractionLost <= 1) {
            ivSignal.setImageResource(R.mipmap.icon_signal2);
        } else if (ractionLost > 1 && ractionLost <= 2) {
            ivSignal.setImageResource(R.mipmap.icon_signal3);
        } else if (ractionLost > 2 && ractionLost <=5) {
            ivSignal.setImageResource(R.mipmap.icon_signal4);
        } else if (ractionLost > 5 && ractionLost <=10) {
            ivSignal.setImageResource(R.mipmap.icon_signal5);
        } else {
            ivSignal.setImageResource(R.mipmap.icon_signal6);
        }
        adapter.notifyDataSetChanged();
    }

    private void startTheTime() {
        time = 0;

        if (durationTimer == null) {
            durationTimer = new Timer();
        }

        durationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(REFRESH_TIME);
            }
        },0,1000);
    }

    private void refreshTime() {
        time++;
        int hour = time / 3600;
        int minute = time % 3600 / 60;
        int second = time % 60;
        String timeString = String.format(Locale.CHINA, "%02d:%02d:%02d",hour,minute,second);
        tvTime.setText(timeString);
    }

    private void showLoading() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_loading_circle);
        LinearInterpolator interpolator = new LinearInterpolator();
        animation.setInterpolator(interpolator);
        rlLoading.setVisibility(View.VISIBLE);
        ivCircle.startAnimation(animation);
    }

    private void stopLoading() {
        ivCircle.clearAnimation();
        rlLoading.setVisibility(View.GONE);
    }

    private void showBar() {
        isShowBar = true;
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        rlTopBar.setVisibility(View.VISIBLE);
        rlToolBar.setVisibility(View.VISIBLE);

        releaseBarTimer();

        if (hideBarTimer == null) {
            hideBarTimer = new Timer();
        }

        if (hideBarTimerTask == null) {
            hideBarTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(HIDE_BAR);
                }
            };
        }

        if (hideBarTimer != null && hideBarTimerTask != null) {
            hideBarTimer.schedule(hideBarTimerTask, 5000);
        }
    }

    private void hideBar() {
        isShowBar = false;
        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
        rlTopBar.setVisibility(View.INVISIBLE);
        rlToolBar.setVisibility(View.INVISIBLE);

        releaseBarTimer();
    }

    private void releaseBarTimer() {
        if (hideBarTimer != null) {
            hideBarTimer.cancel();
            hideBarTimer = null;
        }

        if (hideBarTimerTask != null) {
            hideBarTimerTask.cancel();
            hideBarTimerTask = null;
        }
    }

    private void releaseDurationTimer() {
        if (durationTimer != null) {
            durationTimer.cancel();
            durationTimer = null;
        }
    }

    private void releaseStatsTimer() {
        if (getStatsTimer != null) {
            getStatsTimer.cancel();
            getStatsTimer = null;
        }
    }

    private void toggleMuteAudio() {
        vcrtc.setAudioEnable(isMuteAudio);
        ivMuteAudio.setSelected(!isMuteAudio);
        isMuteAudio = !isMuteAudio;
    }

    private void toggleMuteVideo() {
        vcrtc.setVideoEnable(isMuteVideo);
        ivMuteVideo.setSelected(!isMuteVideo);
        isMuteVideo = !isMuteVideo;
    }

    private void switchCamera() {
        if (localView != null) {
            vcrtc.switchCamera();
            isFront = !isFront;
            localView.setMirror(isFront);
        }
    }

    private void initMediaStatsWindow() {
        View view = getLayoutInflater().inflate(R.layout.popup_stats,null);
        popupWindowStats = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT,false);
        popupWindowStats.setOutsideTouchable(false);

        ImageView ivClose = view.findViewById(R.id.iv_close);
        ListView lvStats = view.findViewById(R.id.lv_stats);

        ivClose.setOnClickListener(this);

        adapter = new StatsAdapter(this, itemBeanList, false);
        lvStats.setAdapter(adapter);
    }

    private void showMediaStatsWindow() {
        popupWindowStats.showAtLocation(rlRootView, Gravity.CENTER,0,0);
    }

    private void showDisconnectDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.disconnect_p2p_title)
                .setMessage(R.string.disconnect_p2p_message)
                .setPositiveButton(R.string.disconnect_sure, (dialog, which) -> {
                    disconnect();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.disconnect_cancel, (dialog, which) -> {
                    dialog.dismiss();
                }).create();

        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    private void showBeDisconnectedDialog(String reason) {
        Toast.makeText(this, R.string.disconnect_call_end, Toast.LENGTH_SHORT).show();
        disconnect();
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.disconnect_sure, (dialog, which) -> {
                    disconnect();
                    dialog.dismiss();
                }).create();

        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    private void disconnect() {
        vcrtc.disconnect();

        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }

        releaseBarTimer();
        releaseDurationTimer();
        releaseStatsTimer();

        if (localView != null) {
            localView.release();
        }

        flLocalVideo.removeAllViews();
        flRemoteVideo.removeAllViews();

        finish();
    }

    private VCRTCListener listener = new VCRTCListenerImpl() {

        @Override
        public void onLocalVideo(String uuid, VCRTCView view) {
            view.setMirror(true);
            view.setZOrder(1);
            view.setObjectFit("cover");
            localView = view;
            flRemoteVideo.addView(view,layoutParams);
            stopLoading();
        }

        @Override
        public void onRemoteVideo(String uuid, VCRTCView view) {
            view.setZOrder(0);
            flRemoteVideo.removeAllViews();
            flLocalVideo.addView(localView,layoutParams);
            flRemoteVideo.addView(view,layoutParams);
            startTheTime();
        }

        @Override
        public void onAddView(String uuid, VCRTCView view, String viewType) {
            if (viewType.equals("video")) {
                view.setZOrder(0);
                flRemoteVideo.removeAllViews();
                flLocalVideo.addView(localView, layoutParams);
                flRemoteVideo.addView(view, layoutParams);
                startTheTime();
            }
        }

        @Override
        public void onUpdateParticipant(Participant participant) {
            rlCallOut.setVisibility(View.GONE);
        }

        @Override
        public void onPresentation(boolean isActive, String uuid) {

        }

        @Override
        public void onDisconnect(String reason) {
            if (isForeground) {
                if (Locale.getDefault().getLanguage().equals("zh")) {
                    Map<String, String> tipsMap = VCUtil.getTipsMessageMap(getApplicationContext());
                    reason = tipsMap.get(reason);
                }
                showBeDisconnectedDialog(reason);
            }
        }

        @Override
        public void onError(ErrorCode error, String description) {
            if (error.equals(ErrorCode.noCameraFound)) {
                runOnUiThread(() -> {if (isForeground) showErrorDialog(getString(R.string.no_camera_found), getString(R.string.check_camera));});
            } else if (error.equals(ErrorCode.joinConferenceFailed)) {
                runOnUiThread(() -> showErrorDialog(error.getDescription(), description));
            }
        }
    };
}
