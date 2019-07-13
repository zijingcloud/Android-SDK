package com.vcrtcdemo.activities.conference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.vcrtc.VCRTC;
import com.vcrtc.VCRTCView;
import com.vcrtc.adapters.StatsAdapter;
import com.vcrtc.callbacks.CallBack;
import com.vcrtc.entities.Call;
import com.vcrtc.entities.ErrorCode;
import com.vcrtc.entities.MediaStats;
import com.vcrtc.entities.StatsItemBean;
import com.vcrtc.listeners.VCRTCListener;
import com.vcrtc.listeners.VCRTCListenerImpl;
import com.vcrtc.utils.BitmapUtil;
import com.vcrtc.utils.OkHttpUtil;
import com.vcrtc.utils.VCAudioManager;
import com.vcrtc.utils.VCHomeListener;
import com.vcrtc.utils.VCNetworkListener;
import com.vcrtc.utils.VCPhoneListener;
import com.vcrtc.utils.VCUtil;
import com.vcrtc.utils.VCWindowManager;
import com.vcrtc.widget.DragFrameLayout;
import com.vcrtcdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VCVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "VCVideoActivity";

    private final int PDF_PICKER_REQUEST = 46709;
    private final int HIDE_BAR = 1;
    private final int REFRESH_TIME = 2;
    private final int REFRESH_STATS = 3;
    private final int START_SCREEN_SHARE = 4;
    private final int DISCONNECT_AND_FINISH = 5;

    private RelativeLayout rlRootView, rlTopBar, rlToolBar, rlLoading, rlShareScreen;
    private DragFrameLayout flLocalVideo;
    private FrameLayout flRemoteVideo;
    private TextView tvTime, tvChanel;
    private LinearLayout llStats;
    private ImageView ivMuteAudio, ivMuteVideo, ivSwitchCamera, ivShare, ivMore, ivHangup, ivPicture, ivLast, ivNext, ivCircle, ivSignal;
    private PopupWindow popupWindowShare, popupWindowMore, popupWindowStats;

    private FrameLayout.LayoutParams layoutParams;

    private VCRTC vcrtc;
    private VCRTCView localView, remoteView;
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
    private boolean isShowBar, isMuteAudio, isMuteVideo, isFront, isShare, isShareScreen, isPresentation;
    private boolean isRecord, isLive;
    private List<String> imagePaths;
    private String layout, hostLayout, guestLayout;
    private String setLayout, setGlayout;
    private int pictureIndex;
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
        setContentView(R.layout.activity_vcvideo);

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
        rlRootView = findViewById(R.id.rl_vc_video_root_view);
        rlLoading = findViewById(R.id.layout_loading);
        rlTopBar = findViewById(R.id.rl_top_bar);
        rlToolBar = findViewById(R.id.rl_tool_bar);
        rlShareScreen = findViewById(R.id.rl_share_screen);
        flLocalVideo = findViewById(R.id.fl_local_video);
        flRemoteVideo = findViewById(R.id.fl_remote_video);
        tvTime = findViewById(R.id.tv_time);
        tvChanel = findViewById(R.id.tv_room_num);
        llStats = findViewById(R.id.ll_stats);
        ivMuteAudio = findViewById(R.id.iv_mute_audio);
        ivMuteVideo = findViewById(R.id.iv_mute_video);
        ivSwitchCamera = findViewById(R.id.iv_switch_camera);
        ivShare = findViewById(R.id.iv_share);
        ivMore = findViewById(R.id.iv_more);
        ivHangup = findViewById(R.id.iv_hangup);
        ivPicture = findViewById(R.id.iv_picture);
        ivLast = findViewById(R.id.iv_last);
        ivNext = findViewById(R.id.iv_next);
        ivCircle = findViewById(R.id.iv_circle);
        ivSignal = findViewById(R.id.iv_signal);

        flRemoteVideo.setOnClickListener(this);
        llStats.setOnClickListener(this);
        ivMuteAudio.setOnClickListener(this);
        ivMuteVideo.setOnClickListener(this);
        ivSwitchCamera.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        ivHangup.setOnClickListener(this);
        ivLast.setOnClickListener(this);
        ivNext.setOnClickListener(this);
    }

    private void initData() {
        tvChanel.setText(call.getChannel());
        if (!call.isHost()) {
            ivMore.setVisibility(View.GONE);
        }

        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        vcrtc = new VCRTC(this);

        audioManager = VCAudioManager.create(getApplicationContext());
        // This method will be called each time the number of available audio devices has changed.
        audioManager.start((audioDevice, availableAudioDevices) -> this.audioDevice = audioDevice);

        homeListener = new VCHomeListener(getApplicationContext());
        homeListener.setKeyListener(new VCHomeListener.KeyListener() {
            @Override
            public void home() {
                if (!isMuteVideo && !isShareScreen) {
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

        isMuteAudio = false;
        isMuteVideo = false;
        isFront = true;
        isShare = false;
        isShareScreen = false;
        imagePaths = new ArrayList<>();
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
        vcrtc.setCheckdup(call.getCheckDup());
        vcrtc.setHideMe(call.isHideMe());
        vcrtc.setClayout(call.getClayout());
        vcrtc.setAccount(call.getAccount());
        if (call.getMsgJson() != null) {
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
        if (i == R.id.fl_remote_video) {
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
        } else if (i == R.id.iv_share) {
            toggleShare();
        } else if (i == R.id.iv_more) {
            showMoreWindow();
            hideBar();
        } else if (i == R.id.iv_hangup) {
            showDisconnectDialog();
            hideBar();
        } else if (i == R.id.tv_layout1) {
            setLayout = "1:0";
            setLayout();
        } else if (i == R.id.tv_layout2) {
            setLayout = "4:0";
            setLayout();
        } else if (i == R.id.tv_layout3) {
            setLayout = "1:7";
            setLayout();
        } else if (i == R.id.tv_layout4) {
            setLayout = "1:21";
            setLayout();
        } else if (i == R.id.tv_layout5) {
            setLayout = "2:21";
            setLayout();
        } else if (i == R.id.tv_guest_layout1) {
            setGlayout = "1:0";
            setLayout();
        } else if (i == R.id.tv_guest_layout2) {
            setGlayout = "4:0";
            setLayout();
        } else if (i == R.id.tv_guest_layout3) {
            setGlayout = "1:7";
            setLayout();
        } else if (i == R.id.tv_guest_layout4) {
            setGlayout = "1:21";
            setLayout();
        } else if (i == R.id.tv_guest_layout5) {
            setGlayout = "2:21";
            setLayout();
        } else if (i == R.id.iv_last) {
            if (pictureIndex - 1 >= 0) {
                pictureIndex--;
                showSharePicture();
            }
        } else if (i == R.id.iv_next) {
            if (pictureIndex + 1 < imagePaths.size()) {
                pictureIndex++;
                showSharePicture();
            }
        } else if (i == R.id.ll_stats) {
            startGetStats();
            showMediaStatsWindow();
        } else if (i == R.id.iv_close) {
            popupWindowStats.dismiss();
            stopGetStats();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isStopShare = intent.getBooleanExtra("isStopShare", false);
            if (isStopShare && isShare) {
                toggleShare();
            } else {
                rlShareScreen.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        VCWindowManager.removeFloatButton(getApplicationContext());
        if (!isMuteAudio) {
            vcrtc.setAudioEnable(true);
        }
        if (!isMuteVideo) {
            vcrtc.setVideoEnable(true);
        }
        if (isShareScreen) {
            rlShareScreen.setVisibility(View.VISIBLE);
        } else {
            rlShareScreen.setVisibility(View.GONE);
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

        if (isShareScreen && audioManager != null) {
            if (VCUtil.checkFloatPermission(this)) {
                //开启悬浮按钮
                VCWindowManager.createFloatButton(getApplicationContext(), VCVideoActivity.class);
            } else {
                showToast(getString(R.string.no_system_alert_window_permission));
            }
        }
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
                case START_SCREEN_SHARE:
                    startScreenShare();
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
                                bean.setTerminal(getString(R.string.stats_remote));
                                bean.setChanel(getString(R.string.stats_video_recv));
                                bean.setCodec(mediaStats.getCodec());
                                bean.setResolution(mediaStats.getResolution());
                                bean.setFrameRate(mediaStats.getFrameRate() + "");
                                bean.setBitRate(mediaStats.getBitrate() + "");
                                bean.setFractionLost(mediaStats.getFractionLost());
                                itemBeanList.add(bean);
                                break;
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

    private void toggleShare() {
        if (isShare) {
            imagePaths.clear();
            stopPresentation();
            ivShare.setSelected(false);
            isShare = false;
            isShareScreen = false;
        } else {
            showShareWindow();
        }
    }

    private void setLayout() {
        vcrtc.setLayout(setLayout, setGlayout);
    }

    private void showToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast, null);

        TextView tvMessage = layout.findViewById(R.id.tv_toast_message);
        tvMessage.setText(message);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void showShareWindow() {
        View view = getLayoutInflater().inflate(R.layout.popup_share,null);
        popupWindowShare = new PopupWindow(view,RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindowShare.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowShare.setOutsideTouchable(true);
        popupWindowShare.showAtLocation(rlRootView, Gravity.CENTER,0,0);

        TextView tvSharePicture = view.findViewById(R.id.tv_share_picture);
        TextView tvShareFile = view.findViewById(R.id.tv_share_file);
        TextView tvShareScreen = view.findViewById(R.id.tv_share_screen);

        tvSharePicture.setOnClickListener(v -> {
            PictureSelector.create(VCVideoActivity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .imageSpanCount(3)
                    .isCamera(false)
                    .compress(true)
                    .minimumCompressSize(300)
                    .maxSelectNum(30)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
            popupWindowShare.dismiss();
        });

        tvShareFile.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("application/pdf");
            Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.share_choose_file));
            startActivityForResult(chooserIntent, PDF_PICKER_REQUEST);
            popupWindowShare.dismiss();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tvShareScreen.setOnClickListener(v -> {
                vcrtc.sendPresentationScreen();
                popupWindowShare.dismiss();
            });
        } else {
            tvShareScreen.setTextColor(getResources().getColor(R.color.vc_text_color_enable));
        }
    }

    private TextView tvLayout;
    private LinearLayout llLayout;
    private TextView tvLayout1, tvLayout2, tvLayout3, tvLayout4, tvLayout5;

    private TextView tvGuestLayout;
    private LinearLayout llGuestLayout;
    private TextView tvGuestLayout1, tvGuestLayout2, tvGuestLayout3, tvGuestLayout4, tvGuestLayout5;

    private TextView tvRecord, tvLive;
    private Switch sRecord, sLive;

    private void showMoreWindow() {
        View view = getLayoutInflater().inflate(R.layout.popup_more,null);
        popupWindowMore = new PopupWindow(view, VCUtil.dp2px(this,400),RelativeLayout.LayoutParams.MATCH_PARENT,true);
        popupWindowMore.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowMore.setOutsideTouchable(true);
        popupWindowMore.showAtLocation(rlRootView, Gravity.RIGHT,0,0);

        tvLayout = view.findViewById(R.id.tv_layout);
        llLayout = view.findViewById(R.id.ll_layout);
        tvLayout.setVisibility(View.VISIBLE);
        llLayout.setVisibility(View.VISIBLE);
        tvLayout1  = view.findViewById(R.id.tv_layout1);
        tvLayout2  = view.findViewById(R.id.tv_layout2);
        tvLayout3  = view.findViewById(R.id.tv_layout3);
        tvLayout4  = view.findViewById(R.id.tv_layout4);
        tvLayout5  = view.findViewById(R.id.tv_layout5);

        tvGuestLayout = view.findViewById(R.id.tv_guest_layout);
        llGuestLayout = view.findViewById(R.id.ll_guest_layout);
        tvGuestLayout.setVisibility(View.VISIBLE);
        llGuestLayout.setVisibility(View.VISIBLE);
        tvGuestLayout1 = view.findViewById(R.id.tv_guest_layout1);
        tvGuestLayout2 = view.findViewById(R.id.tv_guest_layout2);
        tvGuestLayout3 = view.findViewById(R.id.tv_guest_layout3);
        tvGuestLayout4 = view.findViewById(R.id.tv_guest_layout4);
        tvGuestLayout5 = view.findViewById(R.id.tv_guest_layout5);

        tvRecord = view.findViewById(R.id.tv_record);
        tvLive = view.findViewById(R.id.tv_live);
        sRecord = view.findViewById(R.id.s_record);
        sLive = view.findViewById(R.id.s_live);

        tvLayout1.setOnClickListener(this);
        tvLayout2.setOnClickListener(this);
        tvLayout3.setOnClickListener(this);
        tvLayout4.setOnClickListener(this);
        tvLayout5.setOnClickListener(this);

        tvGuestLayout1.setOnClickListener(this);
        tvGuestLayout2.setOnClickListener(this);
        tvGuestLayout3.setOnClickListener(this);
        tvGuestLayout4.setOnClickListener(this);
        tvGuestLayout5.setOnClickListener(this);

        sRecord.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vcrtc.switchRecorder(isChecked);
        });

        sLive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vcrtc.switchLiving(isChecked);
        });

        refreshMoreWindow();
    }

    private void refreshMoreWindow() {
        resetLayout();
        if (vcrtc.isVMR()) {
            tvLayout.setText(R.string.more_host_layout);
            tvGuestLayout.setVisibility(View.VISIBLE);
            llGuestLayout.setVisibility(View.VISIBLE);

            if (hostLayout != null) {
                switch (hostLayout) {
                    case "1:0":
                        tvLayout1.setSelected(true);
                        break;
                    case "4:0":
                        tvLayout2.setSelected(true);
                        break;
                    case "1:7":
                        tvLayout3.setSelected(true);
                        break;
                    case "1:21":
                        tvLayout4.setSelected(true);
                        break;
                    case "2:21":
                        tvLayout5.setSelected(true);
                        break;
                }
            }
            if (guestLayout != null) {
                switch (guestLayout) {
                    case "1:0":
                        tvGuestLayout1.setSelected(true);
                        break;
                    case "4:0":
                        tvGuestLayout2.setSelected(true);
                        break;
                    case "1:7":
                        tvGuestLayout3.setSelected(true);
                        break;
                    case "1:21":
                        tvGuestLayout4.setSelected(true);
                        break;
                    case "2:21":
                        tvGuestLayout5.setSelected(true);
                        break;
                }
            }
        } else {
            tvLayout.setText(R.string.more_layout);
            tvGuestLayout.setVisibility(View.GONE);
            llGuestLayout.setVisibility(View.GONE);

            if (layout != null) {
                switch (layout) {
                    case "1:0":
                        tvLayout1.setSelected(true);
                        break;
                    case "4:0":
                        tvLayout2.setSelected(true);
                        break;
                    case "1:7":
                        tvLayout3.setSelected(true);
                        break;
                    case "1:21":
                        tvLayout4.setSelected(true);
                        break;
                    case "2:21":
                        tvLayout5.setSelected(true);
                        break;
                }
            }
        }

        if (vcrtc.canRecord()) {
            tvRecord.setVisibility(View.VISIBLE);
            sRecord.setVisibility(View.VISIBLE);
        } else {
            tvRecord.setVisibility(View.GONE);
            sRecord.setVisibility(View.GONE);
        }

        if (vcrtc.canLive()) {
            tvLive.setVisibility(View.VISIBLE);
            sLive.setVisibility(View.VISIBLE);
        } else {
            tvLive.setVisibility(View.GONE);
            sLive.setVisibility(View.GONE);
        }

        sRecord.setChecked(isRecord);
        sLive.setChecked(isLive);
    }

    private void resetLayout() {
        tvLayout1.setSelected(false);
        tvLayout2.setSelected(false);
        tvLayout3.setSelected(false);
        tvLayout4.setSelected(false);
        tvLayout5.setSelected(false);
        tvGuestLayout1.setSelected(false);
        tvGuestLayout2.setSelected(false);
        tvGuestLayout3.setSelected(false);
        tvGuestLayout4.setSelected(false);
        tvGuestLayout5.setSelected(false);
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
                .setTitle(R.string.disconnect_title)
                .setMessage(R.string.disconnect_message)
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
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.disconnect_by_server)
                .setMessage(reason)
                .setPositiveButton(R.string.disconnect_sure, (dialog, which) -> {
                    disconnect();
                    dialog.dismiss();
                }).create();

        if (!isFinishing()) {
            alertDialog.show();
        }
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
        if (remoteView != null) {
            remoteView.release();
        }

        flLocalVideo.removeAllViews();
        flRemoteVideo.removeAllViews();

        finish();
    }

    private void startScreenShare() {
        ivShare.setSelected(true);
        //切到桌面
        Intent home=new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    private void startPresentation() {
        rlShareScreen.setVisibility(View.GONE);
        ivPicture.setVisibility(View.VISIBLE);
        if (isShare) {
            ivShare.setSelected(true);
            ivLast.setVisibility(View.VISIBLE);
            ivNext.setVisibility(View.VISIBLE);
        } else {
            ivShare.setSelected(false);
            ivLast.setVisibility(View.GONE);
            ivNext.setVisibility(View.GONE);
        }

        if (remoteView != null) {
            flLocalVideo.removeAllViews();
            flRemoteVideo.removeAllViews();
            flLocalVideo.addView(remoteView, layoutParams);
        }
    }

    private void showSharePicture() {
        File file = new File(imagePaths.get(pictureIndex));
        ivPicture.setImageURI(Uri.fromFile(file));
        vcrtc.sendPresentationImage(file);
    }

    private void showPresentation(Bitmap bitmap) {
        ivPicture.setImageBitmap(bitmap);
    }

    private void stopPresentation() {
        vcrtc.stopPresentation();
        ivPicture.setImageBitmap(null);
        ivPicture.setVisibility(View.GONE);
        ivLast.setVisibility(View.GONE);
        ivNext.setVisibility(View.GONE);
        rlShareScreen.setVisibility(View.GONE);

        if (remoteView != null && localView != null) {
            flLocalVideo.removeAllViews();
            flRemoteVideo.removeAllViews();
            flRemoteVideo.addView(remoteView, layoutParams);
            flLocalVideo.addView(localView, layoutParams);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PDF_PICKER_REQUEST) {
                Uri uri = data.getData();
                if (uri != null) {
                    try {
                        BitmapUtil bitmapUtil = new BitmapUtil(this);
                        imagePaths.addAll(bitmapUtil.pdfToImgs(uri));
                        pictureIndex = 0;
                        isShare = true;
                        startPresentation();
                        showSharePicture();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList != null && selectList.size() > 0) {
                    for (LocalMedia media : selectList) {
                        imagePaths.add(media.getCompressPath());
                    }
                    pictureIndex = 0;
                    isShare = true;
                    startPresentation();
                    showSharePicture();
                }
            }
        }
    }

    private VCRTCListener listener = new VCRTCListenerImpl(){

        @Override
        public void onLocalVideo(String uuid, VCRTCView view) {
            view.setMirror(true);
            view.setZOrder(1);
            view.setObjectFit("cover");
            localView = view;
            flRemoteVideo.addView(localView,layoutParams);
            stopLoading();
        }

        @Override
        public void onRemoteVideo(String uuid, VCRTCView view) {
            view.setZOrder(0);
            remoteView = view;
            flRemoteVideo.removeAllViews();
            flLocalVideo.addView(localView,layoutParams);
            flRemoteVideo.addView(remoteView,layoutParams);
            startTheTime();
        }

        @Override
        public void onPresentation(boolean isActive, String uuid) {
            isPresentation = isActive;
            if (isActive) {
                if (!isForeground) {
                    if (VCWindowManager.getFloatButton() != null) {
                        VCWindowManager.getFloatButton().openMeetingRoom(true);
                    }
                    VCWindowManager.removeFloatButton(getApplicationContext());
                }
                if (isShare) {
                    showToast(getString(R.string.sharing_screen_interrupted));
                }
                isShare = false;
                isShareScreen = false;
                vcrtc.stopPresentation();
            } else {
                if (!isShare) {
                    stopPresentation();
                }
            }
        }

        @Override
        public void onPresentationReload(String url) {
            if (!isShare) {
                OkHttpUtil.loadImage(url, args -> {
                    Bitmap bitmap = (Bitmap) args[0];
                    runOnUiThread(() -> {
                        if (isPresentation) {
                            startPresentation();
                        }
                        showPresentation(bitmap);
                    });
                });
            }
        }

        @Override
        public void onLayoutUpdate(String layout, String hostLayout, String guestLayout) {
            VCVideoActivity.this.layout = layout;
            VCVideoActivity.this.hostLayout = hostLayout;
            VCVideoActivity.this.guestLayout = guestLayout;
            if (popupWindowMore != null && popupWindowMore.isShowing()) {
                refreshMoreWindow();
            }
        }

        @Override
        public void onScreenShareState(boolean isActive) {
            if (isActive) {
                isShare = true;
                isShareScreen = true;
                mHandler.sendEmptyMessage(START_SCREEN_SHARE);
            } else {
                vcrtc.stopPresentation();
            }
        }

        @Override
        public void onRecordState(boolean isActive) {
            isRecord = isActive;
            if (popupWindowMore != null && popupWindowMore.isShowing()) {
                refreshMoreWindow();
            }
        }

        @Override
        public void onLiveState(boolean isActive) {
            isLive = isActive;
            if (popupWindowMore != null && popupWindowMore.isShowing()) {
                refreshMoreWindow();
            }
        }

        @Override
        public void onDisconnect(String reason) {
            if (isForeground) {
                if(Locale.getDefault().getLanguage().equals("zh")) {
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
