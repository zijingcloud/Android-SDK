package com.example.alan.sdkdemo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.alan.sdkdemo.R;
import com.example.alan.sdkdemo.widget.ZoomFrameLayout;
import com.example.alan.sdkdemo.widget.ZoomImageView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener;
import com.vcrtc.VCRTC;
import com.vcrtc.VCRTCView;
import com.vcrtc.adapters.StatsAdapter;
import com.vcrtc.entities.Call;
import com.vcrtc.entities.MediaStats;
import com.vcrtc.entities.Participant;
import com.vcrtc.entities.People;
import com.vcrtc.entities.StatsItemBean;
import com.vcrtc.listeners.DoubleClickListener;
import com.vcrtc.utils.BitmapUtil;
import com.vcrtc.utils.OkHttpUtil;
import com.vcrtc.utils.VCUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MediaSimulcastFragment extends android.app.Fragment implements View.OnClickListener {

    private final int PDF_PICKER_REQUEST = 46709;
    private final int HIDE_BAR = 1;
    private final int REFRESH_TIME = 2;
    private final int REFRESH_STATS = 3;
    private final int START_SCREEN_SHARE = 4;

    private View rootView;
    private LinearLayout llLiving, llRecording;
    public RelativeLayout rlTopBar, rlToolBar, rlLoading, rlUnStick, rlShareScreen;
    private ZoomFrameLayout flBigVideo;
    private LinearLayout llSmallVideo, llBigName;
    private TextView tvTime, tvChanel, tvBigName, tvPeopleNum;
    private LinearLayout llStats;
    private ImageView ivMuteAudio, ivMuteVideo, ivSwitchCamera, ivAudioMedol, ivShare, ivParticipants, ivMore, ivHangup, ivCircle, ivSignal, ivCloseVideo, ivCloseVideoBig, ivBigMute;
    private ZoomImageView ivPicture;
    private ViewPager vpShare;
    private PopupWindow popupWindowShare, popupWindowMore, popupWindowStats;

    private FrameLayout.LayoutParams bigLayoutParams;
    private FrameLayout.LayoutParams videoLayoutParams;
    private LinearLayout.LayoutParams smallLayoutParams;

    private VCRTC vcrtc;
    private VCRTCView localView, bigView;
    private Call call;

    private Timer hideBarTimer;
    private Timer durationTimer;
    private Timer getStatsTimer;
    private TimerTask hideBarTimerTask;
    private TimerTask getStatsTimerTask;
    private int time;
    public boolean isShowBar, isHideSmallView, isMuteAudio, isMuteVideo, isFront, isShare, isShareScreen, isPresentation;
    private boolean isRecord, isLive;
    private Map<String, People> peoples;
    private Map<String, People> showPeoples;
    private List<String> participantsSort;
    private People me;
    private String bigUUID;
    private String stickUUID;
    private boolean isStick;
    private boolean makeMeBig;
    private List<String> imagePathList;
    private int pictureIndex;
    private int positionPixels = 0;
    private List<StatsItemBean> itemBeanList;
    private StatsAdapter adapter;
    private long clickCount = 0;
    private BitmapUtil bitmapUtil;
    private ImageView ivWebClose;
    private boolean isWhiteB = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_media_simulcast, container, false);

        initView();
        initData();
        showBar();
        showLoading();
        initMediaStatsWindow();
        return rootView;
    }

    private void initView() {
        rlLoading = rootView.findViewById(R.id.layout_loading);
        rlTopBar = rootView.findViewById(R.id.rl_top_bar);
        rlToolBar = rootView.findViewById(R.id.rl_tool_bar);
        llLiving = rootView.findViewById(R.id.ll_living);
        llRecording = rootView.findViewById(R.id.ll_recording);
        rlUnStick = rootView.findViewById(R.id.rl_unstick);
        rlShareScreen = rootView.findViewById(R.id.rl_share_screen);
        flBigVideo = rootView.findViewById(R.id.fl_big_video);
        llSmallVideo = rootView.findViewById(R.id.ll_small_video);
        llBigName = rootView.findViewById(R.id.ll_name_big);
        tvTime = rootView.findViewById(R.id.tv_time);
        tvChanel = rootView.findViewById(R.id.tv_room_num);
        tvBigName = rootView.findViewById(R.id.tv_name_big);
        tvPeopleNum = rootView.findViewById(R.id.tv_people_num);
        llStats = rootView.findViewById(R.id.ll_stats);
        ivMuteAudio = rootView.findViewById(R.id.iv_mute_audio);
        ivMuteVideo = rootView.findViewById(R.id.iv_mute_video);
        ivSwitchCamera = rootView.findViewById(R.id.iv_switch_camera);
        ivAudioMedol = rootView.findViewById(R.id.iv_audio_model);
        ivShare = rootView.findViewById(R.id.iv_share);
        ivParticipants = rootView.findViewById(R.id.iv_participants);
        ivMore = rootView.findViewById(R.id.iv_more);
        ivHangup = rootView.findViewById(R.id.iv_hangup);
        ivPicture = rootView.findViewById(R.id.iv_picture);
        ivCircle = rootView.findViewById(R.id.iv_circle);
        ivSignal = rootView.findViewById(R.id.iv_signal);
        ivCloseVideoBig = rootView.findViewById(R.id.iv_close_video_img);
        ivBigMute = rootView.findViewById(R.id.iv_mute_big);
        vpShare = rootView.findViewById(R.id.vp_share);

        rlUnStick.setOnClickListener(this);
        llStats.setOnClickListener(this);
        ivMuteAudio.setOnClickListener(this);
        ivMuteVideo.setOnClickListener(this);
        ivSwitchCamera.setOnClickListener(this);
        ivAudioMedol.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivParticipants.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        ivHangup.setOnClickListener(this);
        ivPicture.setOnClickListener(() -> {
            if (isShowBar) {
                hideBar();
            } else {
                showBar();
            }
        });

        flBigVideo.setOnClickListener(() -> {
            clickCount++;
            mHandler.postDelayed(() -> {
                if (clickCount == 1) {
                    if (isShowBar) {
                        hideBar();
                    } else {
                        showBar();
                    }
                } else if (clickCount == 2) {
                    if (!isPresentation && !isShare) {
                        if (isStick) {
                            setUnStick();
                            showToast(getString(R.string.main_screen_unlock));
                        } else {
                            setStick(bigUUID);
                        }
                    }
                }
                mHandler.removeCallbacksAndMessages(null);
                clickCount = 0;
            }, 400);
        });

        vpShare.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffsetPixels > 0) {
                    positionPixels = positionOffsetPixels;
                }
            }

            @Override
            public void onPageSelected(int position) {
                pictureIndex = position;
                sendSharePicture();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        //无动作、初始状态
                        if (positionPixels == 0) {
                            showToast(getString(R.string.toast_slippery));
                        }
                        positionPixels = 0;
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        //点击、滑屏
                        positionPixels = 0;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        //释放
                        break;
                        default:
                }
            }
        });
    }

    private void initData() {
        call = ((ZJConferenceActivity) Objects.requireNonNull(getActivity())).call;
        vcrtc = ((ZJConferenceActivity) getActivity()).vcrtc;

        tvChanel.setText(call.getChannel());

        if (ZJConferenceActivity.joinMuteAudio) {
            ivMuteAudio.setSelected(true);
        }

        int bigVideoWidth;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        int screenWidth = dm.widthPixels;
        if (screenWidth * 9 / 16 >= screenHeight) {
            bigVideoWidth = screenHeight * 16 / 9;
        } else {
            bigVideoWidth = screenWidth;
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llSmallVideo.getLayoutParams();
        params.setMarginStart((screenWidth - bigVideoWidth) / 2);

        bigLayoutParams = new FrameLayout.LayoutParams(bigVideoWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        bigLayoutParams.gravity = Gravity.CENTER;

        videoLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        smallLayoutParams = new LinearLayout.LayoutParams(bigVideoWidth / 5 - VCUtil.dp2px(getActivity().getApplicationContext(), 1),
                (bigVideoWidth / 5 - VCUtil.dp2px(getActivity().getApplicationContext(), 1)) * 9 / 16);
        smallLayoutParams.setMargins(0, 0, VCUtil.dp2px(getActivity().getApplicationContext(), 1), 0);

        isMuteAudio = false;
        isMuteVideo = false;
        isFront = true;
        isShare = false;
        isShareScreen = false;
        //实际参会者列表
        peoples = new LinkedHashMap<>();
        //显示的参会者列表
        showPeoples = new LinkedHashMap<>();
        imagePathList = new ArrayList<>();
        // 统计参数集合
        itemBeanList = new ArrayList<>();

        bigView = new VCRTCView(getActivity());
        // 0 可以被别的视频覆盖  1 不可覆盖
        bigView.setZOrder(0);
        bigView.setObjectFit("cover");
        if (ZJConferenceActivity.allowCamera)
            bigView.setMirror(true);

        flBigVideo.addView(bigView, bigLayoutParams);

        ((ZJConferenceActivity) getActivity()).setMediaCallBack(callBack);
        bitmapUtil = new BitmapUtil(getActivity());
    }

    /**
     * 刷新音频视频图标
     */
    public void refreshAudioVideoIcon() {
        if (!ZJConferenceActivity.allowRecordAudio) {
            ivMuteAudio.setSelected(true);
        }
        if (!ZJConferenceActivity.allowCamera) {
            ivMuteVideo.setSelected(true);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_mute_audio) {
            toggleMuteAudio();
            showBar();
        } else if (i == R.id.iv_mute_video) {
            toggleMuteVideo();
            showBar();
        } else if (i == R.id.iv_switch_camera) {
            switchCamera();
            showBar();
        } else if (i == R.id.iv_audio_model) {

        } else if (i == R.id.iv_share) {
            toggleShare();
        } else if (i == R.id.iv_more) {
            showMoreWindow();
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
        } else if (i == R.id.rl_unstick) {
            setUnStick();
            showToast(getString(R.string.main_screen_unlock));
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
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
                    default:
            }
        }
    };

    /**
     * 获取统计参数的方法
     */
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
                                bean.setJitter(mediaStats.getJitter() + "ms");
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
                                bean.setJitter(mediaStats.getJitter() + "ms");
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
                                bean.setJitter(mediaStats.getJitter() + "ms");
                                bean.setFractionLost(mediaStats.getFractionLost());
                                itemBeanList.add(bean);
                            }
                        }
                        for (MediaStats mediaStats : stats) {
                            if (mediaStats.getMediaType().equals("video") && mediaStats.getDirection().equals("in")) {
                                StatsItemBean bean = new StatsItemBean();
                                for (String uuid : peoples.keySet()) {
                                    if (mediaStats.getUuid().equals(uuid)) {
                                        bean.setTerminal(peoples.get(uuid).getName());
                                    }
                                }
                                bean.setChanel(getString(R.string.stats_video_recv));
                                bean.setCodec(mediaStats.getCodec());
                                bean.setResolution(mediaStats.getResolution());
                                bean.setFrameRate(mediaStats.getFrameRate() + "");
                                bean.setBitRate(mediaStats.getBitrate() + "");
                                bean.setJitter(mediaStats.getJitter() + "ms");
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
                        e.printStackTrace();
                    }
                }
            };
        }
        if (getStatsTimer != null) {
            try {
                getStatsTimer.schedule(getStatsTimerTask, 0, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止获取统计参数
     */
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
            ivSignal.setImageResource(R.mipmap.icon_sign_5);
        } else if (ractionLost > 0 && ractionLost <= 1) {
            ivSignal.setImageResource(R.mipmap.icon_sign_4);
        } else if (ractionLost > 1 && ractionLost <= 2) {
            ivSignal.setImageResource(R.mipmap.icon_sign_3);
        } else if (ractionLost > 2 && ractionLost <= 5) {
            ivSignal.setImageResource(R.mipmap.icon_sign_2);
        } else if (ractionLost > 5 && ractionLost <= 10) {
            ivSignal.setImageResource(R.mipmap.icon_sign_1);
        } else {
            ivSignal.setImageResource(R.mipmap.icon_sign_0);
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
        }, 0, 1000);

    }

    private void refreshTime() {
        time++;
        int hour = time / 3600;
        int minute = time % 3600 / 60;
        int second = time % 60;
        String timeString = String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
        tvTime.setText(timeString);
    }

    private void showLoading() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_loading_circle);
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

        if (hideBarTimer != null) {
            hideBarTimer.schedule(hideBarTimerTask, 5000);
        }
    }

    private void hideBar() {
        isShowBar = false;

        rlTopBar.setVisibility(View.INVISIBLE);
        rlToolBar.setVisibility(View.INVISIBLE);

        if (popupWindowMore != null && popupWindowMore.isShowing())
            popupWindowMore.dismiss();
        if (popupWindowShare != null && popupWindowShare.isShowing())
            popupWindowShare.dismiss();

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

    /**
     * 隐藏小窗视频
     */
    private void hideShowSmallView() {
        llSmallVideo.setVisibility(isHideSmallView ? View.VISIBLE : View.GONE);
        if (!isHideSmallView) {
            isHideSmallView = true;
            llSmallVideo.removeAllViews();
            tvViewInView.setText(R.string.more_show_small_view);
        } else {
            isHideSmallView = false;
            sortPeopels();
            tvViewInView.setText(R.string.more_hide_small_view);
        }
    }

    /**
     * 锁定主屏
     *
     * @param uuid
     */
    private void setStick(String uuid) {
        if (uuid != null && !"".equals(uuid)) {
            if (uuid.equals(me.getUuid())) {
                makeMeBig = true;
                sortPeopels();
            } else {
                makeMeBig = false;
                // 通知服务器发送大屏幕数据流
                vcrtc.setParticipantStick(uuid, true);
            }
            stickUUID = uuid;
            rlUnStick.setVisibility(View.VISIBLE);
            showToast(getString(R.string.main_screen_lock_cancel));
            isStick = true;
        }
    }

    /**
     * 解锁主屏
     */
    private void setUnStick() {
        if (isStick) {
            makeMeBig = false;
            if (stickUUID != null) {
                if (stickUUID.equals(me.getUuid())) {
                    sortPeopels();
                } else {
                    vcrtc.setParticipantStick(stickUUID, false);
                }
                rlUnStick.setVisibility(View.INVISIBLE);
            }
            isStick = false;
        }
    }

    /**
     * 静音audio
     */
    private void toggleMuteAudio() {
        if (!ZJConferenceActivity.allowRecordAudio) {
            checkRecordAudioPermission();
            return;
        }
        vcrtc.setAudioEnable(isMuteAudio);
        ivMuteAudio.setSelected(!isMuteAudio);
        isMuteAudio = !isMuteAudio;
    }

    private void checkRecordAudioPermission() {
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.RECORD_AUDIO,
                new CheckRequestPermissionListener() {
                    @Override
                    public void onPermissionOk(Permission permission) {
                        ZJConferenceActivity.allowRecordAudio = true;
                        ivMuteAudio.setSelected(false);
                        if (ZJConferenceActivity.allowCamera) {
                            vcrtc.setCallType(VCRTC.CallType.video);
                        } else {
                            vcrtc.setCallType(VCRTC.CallType.recvAndSendAudioBitmap);
                        }
                        vcrtc.reconnectOnlyMediaCall();
                    }

                    @Override
                    public void onPermissionDenied(Permission permission) {
                        if (!permission.shouldRationale()) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.permission_tips)
                                    .setMessage(String.format(getString(R.string.permission_message_no_record_audio), getString(R.string.app_name)))
                                    .setPositiveButton(R.string.permission_go, (dialog, which) -> {
                                        SoulPermission.getInstance().goApplicationSettings();
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton(R.string.permission_cancel, (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .create()
                                    .show();
                        }
                    }
                });
    }

    /**
     * 设置本地摄像头是否关闭
     */
    private void toggleMuteVideo() {
        if (!ZJConferenceActivity.allowCamera) {
            checkCameraPermission();
            return;
        }
        vcrtc.updateVideoImage(((ZJConferenceActivity) getActivity()).closeVideoBitmap);
        vcrtc.setVideoEnable(isMuteVideo, true);
        ivMuteVideo.setSelected(!isMuteVideo);
        isMuteVideo = !isMuteVideo;
        if (isMuteVideo) {
            if (ivCloseVideo != null)
                ivCloseVideo.setVisibility(View.VISIBLE);
            if (me.isBig() || peoples.size() == 0) {
                ivCloseVideoBig.setVisibility(View.VISIBLE);
            }
        } else {
            if (ivCloseVideo != null)
                ivCloseVideo.setVisibility(View.GONE);
            if (me.isBig() || peoples.size() == 0) {
                ivCloseVideoBig.setVisibility(View.GONE);
            }
        }
    }

    private void checkCameraPermission() {
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.CAMERA,
                new CheckRequestPermissionListener() {
                    @Override
                    public void onPermissionOk(Permission permission) {
                        ZJConferenceActivity.allowCamera = true;
                        ivMuteVideo.setSelected(false);
                        if (ZJConferenceActivity.allowRecordAudio) {
                            vcrtc.setCallType(VCRTC.CallType.video);
                        } else {
                            vcrtc.setCallType(VCRTC.CallType.recvAndSendVideo);
                        }
                        vcrtc.reconnectOnlyMediaCall();
                    }

                    @Override
                    public void onPermissionDenied(Permission permission) {
                        if (!permission.shouldRationale()) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.permission_tips)
                                    .setMessage(String.format(getString(R.string.permission_message_no_camera), getString(R.string.app_name)))
                                    .setPositiveButton(R.string.permission_go, (dialog, which) -> {
                                        SoulPermission.getInstance().goApplicationSettings();
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton(R.string.permission_cancel, (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .create()
                                    .show();
                        }
                    }
                });
    }

    private void switchCamera() {
        if (localView != null) {
            vcrtc.switchCamera();
            isFront = !isFront;
            localView.setMirror(isFront);
            if ((stickUUID != null && stickUUID.equals(me.getUuid())) || peoples.size() <= 0) {
                bigView.setMirror(isFront);
            }
        }
    }

    public void toggleShare() {
        if (isShare) {
            imagePathList.clear();
            stopPresentation();
            ivShare.setSelected(false);
            isShare = false;
            isShareScreen = false;

        } else {
            showShareWindow();
        }
    }

    private void showShareWindow() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_share, null);
        TextView tvSharePicture = view.findViewById(R.id.tv_share_picture);
        TextView tvShareFile = view.findViewById(R.id.tv_share_file);
        TextView tvShareScreen = view.findViewById(R.id.tv_share_screen);
        popupWindowShare = new PopupWindow(view, VCUtil.dp2px(getActivity(), 120), RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindowShare.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowShare.setOutsideTouchable(true);
        int[] location = new int[2];
        ivShare.getLocationOnScreen(location);

        popupWindowShare.showAtLocation(ivShare, Gravity.NO_GRAVITY, location[0] - VCUtil.dp2px(getActivity(), 40), location[1] - VCUtil.dp2px(getActivity(), 130));


        tvSharePicture.setOnClickListener(v -> {
            PictureSelector.create(getActivity())
                    .openGallery(PictureMimeType.ofImage())
                    .imageSpanCount(3)
                    .isCamera(false)
                    .compress(true)
                    .minimumCompressSize(300)
                    .maxSelectNum(9)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
            popupWindowShare.dismiss();
        });

        tvShareFile.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("application/pdf");
            Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.share_choose_file));
            getActivity().startActivityForResult(chooserIntent, PDF_PICKER_REQUEST);
            popupWindowShare.dismiss();
        });

        tvShareScreen.setOnClickListener(v -> {
            vcrtc.sendPresentationScreen();
            popupWindowShare.dismiss();
        });
    }

    private TextView tvRecord, tvLive, tvViewInView, tvAdditional;
    private View line1, line2, line3;
    private int y;

    private void showMoreWindow() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_more, null);
        popupWindowMore = new PopupWindow(view, VCUtil.dp2px(getActivity(), 120), RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindowMore.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowMore.setOutsideTouchable(true);

        tvRecord = view.findViewById(R.id.tv_record);
        tvLive = view.findViewById(R.id.tv_live);
        tvViewInView = view.findViewById(R.id.tv_view_in_view);
        tvAdditional = view.findViewById(R.id.tv_additional);

        line1 = view.findViewById(R.id.line1);
        line2 = view.findViewById(R.id.line2);
        line3 = view.findViewById(R.id.line3);

        tvRecord.setOnClickListener(v -> {
            vcrtc.switchRecorder(!isRecord);
            popupWindowMore.dismiss();
        });

        tvLive.setOnClickListener(v -> {
            vcrtc.switchLiving(!isLive);
            popupWindowMore.dismiss();
        });

        tvViewInView.setOnClickListener(v -> {
            hideShowSmallView();
            popupWindowMore.dismiss();
        });

        refreshMoreWindow();

        int[] location = new int[2];
        ivMore.getLocationOnScreen(location);

        popupWindowMore.showAtLocation(ivMore, Gravity.NO_GRAVITY, location[0] - VCUtil.dp2px(getActivity(), 40), location[1] - y);
    }

    /**
     * 刷新更多状态
     */
    private void refreshMoreWindow() {
        y = VCUtil.dp2px(getActivity(), 130);

        if (vcrtc.canRecord() && call.isHost()) {
            tvRecord.setVisibility(View.VISIBLE);
        } else {
            tvRecord.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
            y -= VCUtil.dp2px(getActivity(), 40);
        }

        if (vcrtc.canLive() && call.isHost()) {
            tvLive.setVisibility(View.VISIBLE);
        } else {
            tvLive.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            y -= VCUtil.dp2px(getActivity(), 40);
        }

        tvRecord.setText(isRecord ? R.string.more_close_record : R.string.more_open_record);
        tvLive.setText(isLive ? R.string.more_close_live : R.string.more_open_live);
        tvViewInView.setText(isHideSmallView ? R.string.more_show_small_view : R.string.more_hide_small_view);
    }

    private void showToast(String message) {
        ((ZJConferenceActivity) getActivity()).showToast(message, Toast.LENGTH_SHORT);
    }

    private void disconnect() {

        releaseBarTimer();
        releaseDurationTimer();
        releaseStatsTimer();

        if (localView != null) {
            localView.release();
        }
        if (bigView != null) {
            bigView.release();
        }

        llSmallVideo.removeAllViews();
        flBigVideo.removeAllViews();

        ((ZJConferenceActivity) getActivity()).disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseBarTimer();
        releaseDurationTimer();
        releaseStatsTimer();
    }

    /**
     * 开启屏幕共享
     */
    private void startScreenShare() {
        vcrtc.updateClayout("0:1");
        ivShare.setSelected(true);
        //切到桌面
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    /**
     * 开启双流
     */
    private void startPresentation() {
        setUnStick();
        vcrtc.updateClayout("0:1");
        rlShareScreen.setVisibility(View.GONE);
        if (isShare) {
            ivShare.setSelected(true);
            ivPicture.reset();
            ivPicture.setVisibility(View.GONE);
            vpShare.setVisibility(View.VISIBLE);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), imagePathList);
            viewPagerAdapter.setOnItemImageListener(new ViewPagerAdapter.OnItemImageListener() {
                @Override
                public void onClick() {
                    if (isShowBar) {
                        hideBar();
                    } else {
                        showBar();
                    }
                }

                @Override
                public void onCutBitmap(Bitmap bitmap) {
                    vcrtc.sendPresentationImage(bitmap);
                }
            });
            vpShare.setAdapter(viewPagerAdapter);
        } else {
            ivShare.setSelected(false);
            vpShare.setVisibility(View.GONE);
            ivPicture.setVisibility(View.VISIBLE);
        }
    }

    private void sendSharePicture() {
        File file = new File(imagePathList.get(pictureIndex));
        vcrtc.sendPresentationImage(file);
    }

    /**
     * 自己控制放大缩小时
     *
     * @param bitmap
     */
    private void showPresentation(Bitmap bitmap) {
        ivPicture.setImageBitmap(bitmap);
    }

    /**
     * 停止双流
     */
    private void stopPresentation() {
        vcrtc.stopPresentation();
        vcrtc.updateClayout("1:4");
        ivPicture.reset();
        ivPicture.setVisibility(View.GONE);
        vpShare.setVisibility(View.GONE);
        rlShareScreen.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PDF_PICKER_REQUEST) {
                Uri uri = data.getData();
                if (uri != null) {
                    try {
                        imagePathList.addAll(bitmapUtil.pdfToImgs(uri));
                        pictureIndex = 0;
                        isShare = true;
                        startPresentation();
                        sendSharePicture();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList != null && selectList.size() > 0) {
                    for (LocalMedia media : selectList) {
                        imagePathList.add(media.getCompressPath());
                    }
                    pictureIndex = 0;
                    isShare = true;
                    startPresentation();
                    sendSharePicture();
                }
            }
        }
    }

    /**
     * 初始化统计参数画面
     */
    private void initMediaStatsWindow() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_stats, null);
        popupWindowStats = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, false);
        popupWindowStats.setOutsideTouchable(false);

        ImageView ivClose = view.findViewById(R.id.iv_close);
        ListView lvStats = view.findViewById(R.id.lv_stats);

        ivClose.setOnClickListener(this);

        adapter = new StatsAdapter(getActivity(), itemBeanList, false);
        lvStats.setAdapter(adapter);
    }

    private void showMediaStatsWindow() {
        popupWindowStats.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    public void showDisconnectDialog() {
        if (popupWindowStats != null && popupWindowStats.isShowing()) {
            popupWindowStats.dismiss();
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.disconnect_title)
                .setMessage(R.string.disconnect_message)
                .setPositiveButton(R.string.disconnect_sure, (dialog, which) -> {
                    disconnect();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.disconnect_cancel, (dialog, which) -> {
                    dialog.dismiss();
                }).create();

        alertDialog.show();
    }

    private void refreshUI() {
        tvPeopleNum.setText(String.valueOf(peoples.size() + 1));
        if (peoples.size() > 0) {
            for (String uuid : peoples.keySet()) {
                if (!showPeoples.containsKey(uuid) && peoples.get(uuid).getPeopleView() != null && peoples.get(uuid).getPeopleView().getParent() != null) {
                    llSmallVideo.removeView(peoples.get(uuid).getPeopleView());
                }
            }
            if (isPresentation || isShare) {
                for (String uuid : showPeoples.keySet()) {
                    if (showPeoples.get(uuid).isBig()) {
                        if (showPeoples.get(uuid).getPeopleView() != null && showPeoples.get(uuid).getPeopleView().getParent() == null) {
                            llSmallVideo.addView(showPeoples.get(uuid).getPeopleView(), smallLayoutParams);
                        }
                    } else {
                        if (showPeoples.get(uuid).getPeopleView() != null && showPeoples.get(uuid).getPeopleView().getParent() != null) {
                            llSmallVideo.removeView(showPeoples.get(uuid).getPeopleView());
                        }
                    }
                }
            } else {
                for (String uuid : showPeoples.keySet()) {
                    if (showPeoples.get(uuid).isBig()) {
                        bigView.setStreamURL(showPeoples.get(uuid).getStreamURL());
                        if (me != null && uuid.equals(me.getUuid())) {
                            bigView.setMirror(isFront && ZJConferenceActivity.allowCamera);
                            if (isMuteVideo) {
                                ivCloseVideoBig.setVisibility(View.VISIBLE);
                            }
                        } else {
                            bigView.setMirror(false);
                            ivCloseVideoBig.setVisibility(View.GONE);
                        }
                        ivBigMute.setVisibility(showPeoples.get(uuid).isMute() ? View.VISIBLE : View.GONE);
                        tvBigName.setText(showPeoples.get(uuid).getName());
                        llBigName.setVisibility(View.VISIBLE);
                        if (showPeoples.get(uuid).getPeopleView() != null && showPeoples.get(uuid).getPeopleView().getParent() != null) {
                            llSmallVideo.removeView(showPeoples.get(uuid).getPeopleView());
                        }
                    } else {
                        if (!isHideSmallView) {
                            if (showPeoples.get(uuid).getPeopleView() != null && showPeoples.get(uuid).getPeopleView().getParent() == null) {
                                if (uuid != null && uuid.equals(me.getUuid())) {
                                    llSmallVideo.addView(showPeoples.get(uuid).getPeopleView(), 0, smallLayoutParams);
                                } else {
                                    if (llSmallVideo.getChildAt(0) != null) {
                                        llSmallVideo.addView(showPeoples.get(uuid).getPeopleView(), 1, smallLayoutParams);
                                    } else {
                                        llSmallVideo.addView(showPeoples.get(uuid).getPeopleView(), smallLayoutParams);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (me != null && me.getStreamURL() != null) {
                bigView.setStreamURL(me.getStreamURL());
                bigView.setMirror(isFront && ZJConferenceActivity.allowCamera);
                llBigName.setVisibility(View.INVISIBLE);
                llSmallVideo.removeAllViews();
            }
        }
    }

    private void sortPeopels() {
        showPeoples.clear();
        if (makeMeBig) {
            if (me != null) {
                me.setBig(true);
                bigUUID = me.getUuid();
            }
            if (participantsSort != null && peoples.size() > 0) {
                for (int i = 0; i < participantsSort.size(); i++) {
                    for (String uuid : peoples.keySet()) {
                        if (uuid.equals(participantsSort.get(i))) {
                            peoples.get(uuid).setBig(false);
                            showPeoples.put(uuid, peoples.get(uuid));
                        }
                    }
                }
            }
        } else {
            if (me != null) me.setBig(false);
            if (participantsSort != null && peoples.size() > 0) {
                for (int i = 0; i < participantsSort.size(); i++) {
                    for (String uuid : peoples.keySet()) {
                        if (uuid.equals(participantsSort.get(i))) {
                            if (i == 0) {
                                peoples.get(uuid).setBig(true);
                                bigUUID = uuid;
                            } else {
                                peoples.get(uuid).setBig(false);
                            }
                            showPeoples.put(uuid, peoples.get(uuid));
                        }
                    }
                }
            }
        }
        if (me != null) showPeoples.put(me.getUuid(), me);
        refreshUI();
    }


    /**
     * 具体回调信息可查看SDK文档
     */
    ZJConferenceActivity.MediaCallBack callBack = new ZJConferenceActivity.MediaCallBack() {

        @Override
        public void onConnect() {

        }

        @Override
        public void onCallConnect() {
            if (getActivity() != null && ZJConferenceActivity.joinMuteAudio) {
                toggleMuteAudio();
                ZJConferenceActivity.joinMuteAudio = false;
            }
        }

        @Override
        public void onLocalVideo(String uuid, VCRTCView view) {

        }

        @Override
        public void onRemoteVideo(String uuid, VCRTCView view) {

        }

        @Override
        public void onLocalStream(String uuid, String streamURL) {
            stopLoading();
            if (localView == null) {
                bigView.setStreamURL(streamURL);
                bigUUID = uuid;

                localView = new VCRTCView(getActivity());
                localView.setZOrder(1);
                if (ZJConferenceActivity.allowCamera)
                    localView.setMirror(true);
                localView.setObjectFit("cover");
                localView.setStreamURL(streamURL);

                View myView = getActivity().getLayoutInflater().inflate(R.layout.video_item, null);

                ImageView ivVideoLoading = myView.findViewById(R.id.iv_video_loading);
                AnimationDrawable animationDrawable = (AnimationDrawable) ivVideoLoading.getDrawable();
                animationDrawable.start();

                FrameLayout flVideo = myView.findViewById(R.id.fl_video);
                ivCloseVideo = myView.findViewById(R.id.iv_close_video);
                TextView tvName = myView.findViewById(R.id.tv_name);
                tvName.setText(R.string.me);

                flVideo.addView(localView, videoLayoutParams);

                flVideo.setOnClickListener(new DoubleClickListener() {

                    @Override
                    public void onSingleClick(View v) {

                    }

                    @Override
                    public void onDoubleClick(View v) {
                        if (!isPresentation && !isShare) {
                            if (isStick && !stickUUID.equals(uuid)) {
                                vcrtc.setParticipantStick(stickUUID, false);
                            }
                            setStick(uuid);
                        }
                    }

                });

                me = new People(uuid, getString(R.string.me), streamURL, myView, false);

                startTheTime();
            } else {
                if (ZJConferenceActivity.allowCamera)
                    localView.setMirror(true);
                localView.setStreamURL(streamURL);
                me.setStreamURL(streamURL);
            }
        }

        @Override
        public void onAddView(String uuid, VCRTCView view, String viewType) {
            if (viewType.equals("video") && peoples.containsKey(uuid)) {
                view.setZOrder(1);
                view.setObjectFit("cover");
                View peopleView = peoples.get(uuid).getPeopleView();
                FrameLayout flVideo = peopleView.findViewById(R.id.fl_video);
                flVideo.removeAllViews();
                flVideo.addView(view, videoLayoutParams);

                flVideo.setOnClickListener(new DoubleClickListener() {

                    @Override
                    public void onSingleClick(View v) {

                    }

                    @Override
                    public void onDoubleClick(View v) {
                        if (!isPresentation && !isShare) {
                            setStick(uuid);
                        }
                    }

                });
                sortPeopels();
            }
        }

        @Override
        public void onRemoveView(String uuid, VCRTCView view) {
            if (peoples.containsKey(uuid)) {
                View peopleView = peoples.get(uuid).getPeopleView();
                FrameLayout flVideo = peopleView.findViewById(R.id.fl_video);
                flVideo.removeAllViews();
            }
        }

        @Override
        public void onRemoteStream(String uuid, String streamURL, String streamType) {
            if (streamType.equals("video") && peoples.containsKey(uuid)) {
                peoples.get(uuid).setStreamURL(streamURL);
                sortPeopels();
            } else if (streamType.equals("presentation")) {
                bigView.setStreamURL(streamURL);
            }
        }

        //2
        @Override
        public void onAddParticipant(Participant participant) {
            if (!peoples.containsKey(participant.getUuid()) && (me == null || !participant.getUuid().equals(me.getUuid()))) {
                View peopleView = getActivity().getLayoutInflater().inflate(R.layout.video_item, null);

                ImageView ivVideoLoading = peopleView.findViewById(R.id.iv_video_loading);
                AnimationDrawable animationDrawable = (AnimationDrawable) ivVideoLoading.getDrawable();
                animationDrawable.start();

                ImageView ivMute = peopleView.findViewById(R.id.iv_mute);
                TextView tvName = peopleView.findViewById(R.id.tv_name);
                ivMute.setVisibility(participant.getIs_muted().equals("YES") ? View.VISIBLE : View.GONE);
                tvName.setText(participant.getOverlay_text());
                People people = new People(participant.getUuid(), participant.getOverlay_text(), null, peopleView, false);
                people.setMute(participant.getIs_muted().equals("YES"));
                peoples.put(participant.getUuid(), people);
            }
        }

        @Override
        public void onUpdateParticipant(Participant participant) {
            if (peoples.containsKey(participant.getUuid())) {
                peoples.get(participant.getUuid()).setName(participant.getOverlay_text());
                peoples.get(participant.getUuid()).setMute(participant.getIs_muted().equals("YES"));
                if (peoples.get(participant.getUuid()).getPeopleView() != null) {
                    ImageView ivMute = peoples.get(participant.getUuid()).getPeopleView().findViewById(R.id.iv_mute);
                    TextView tvName = peoples.get(participant.getUuid()).getPeopleView().findViewById(R.id.tv_name);
                    ivMute.setVisibility(participant.getIs_muted().equals("YES") ? View.VISIBLE : View.GONE);
                    tvName.setText(participant.getOverlay_text());
                }
            } else {
                if (me == null || !participant.getUuid().equals(me.getUuid())) {
                    View peopleView = getActivity().getLayoutInflater().inflate(R.layout.video_item, null);

                    ImageView ivVideoLoading = peopleView.findViewById(R.id.iv_video_loading);
                    AnimationDrawable animationDrawable = (AnimationDrawable) ivVideoLoading.getDrawable();
                    animationDrawable.start();

                    ImageView ivMute = peoples.get(participant.getUuid()).getPeopleView().findViewById(R.id.iv_mute);
                    TextView tvName = peopleView.findViewById(R.id.tv_name);
                    ivMute.setVisibility(participant.getIs_muted().equals("YES") ? View.VISIBLE : View.GONE);
                    tvName.setText(participant.getOverlay_text());
                    People people = new People(participant.getUuid(), participant.getOverlay_text(), null, peopleView, false);
                    people.setMute(participant.getIs_muted().equals("YES"));
                    peoples.put(participant.getUuid(), people);
                }
            }

            if (me != null && participant.getUuid().equals(me.getUuid())) {
                me.setMute(participant.getIs_muted().equals("YES"));
                ImageView ivMute = me.getPeopleView().findViewById(R.id.iv_mute);
                ivMute.setVisibility(participant.getIs_muted().equals("YES") ? View.VISIBLE : View.GONE);
            }
            refreshUI();
        }

        @Override
        public void onRemoveParticipant(String uuid) {
            if (peoples.containsKey(uuid)) {
                if (peoples.get(uuid).getPeopleView() != null && peoples.get(uuid).getPeopleView().getParent() != null) {
                    llSmallVideo.removeView(peoples.get(uuid).getPeopleView());
                }
                peoples.remove(uuid);
                sortPeopels();
            }
            if (uuid.equals(stickUUID)) {
                setUnStick();
            }
        }

        @Override
        public void onLayoutUpdate(String layout, String hostLayout, String guestLayout) {

        }

        @Override
        public void onRoleUpdate(String role) {
            call.setHost("HOST".equals(role));
        }

        @Override
        public void onLayoutUpdateParticipants(List<String> participants) {
            participantsSort = participants;
            sortPeopels();
        }

        //双流回调
        @Override
        public void onPresentation(boolean isActive, String uuid) {
            isPresentation = isActive;
            sortPeopels();
            if (isActive) {
                if (isShare && !uuid.equals(me.getUuid())) {

                    showToast(getString(R.string.sharing_screen_interrupted));
                }
                imagePathList.clear();
                isShare = false;
                isShareScreen = false;
                vcrtc.stopPresentation();
                rlShareScreen.setVisibility(View.GONE);
                vpShare.setVisibility(View.GONE);
                ivShare.setSelected(false);
            } else {
                if (!isShare) {
                    stopPresentation();
                }

            }
        }

        /**
         * 双流图片信息回调
         * @param url 图片地址
         */
        @Override
        public void onPresentationReload(String url) {
            if (!isShare) {
                OkHttpUtil.loadImage(url, args -> {
                    Bitmap bitmap = (Bitmap) args[0];
                    getActivity().runOnUiThread(() -> {
                        if (isPresentation) {
                            startPresentation();
                        }
                        if (!isShare) {
                            showPresentation(bitmap);
                        }
                    });
                });
            }
        }

        /**
         * 屏幕分享状态回调
         * @param isActive
         */
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
            llRecording.setVisibility(isRecord && call.isHost() ? View.VISIBLE : View.GONE);
            if (popupWindowMore != null && popupWindowMore.isShowing()) {
                refreshMoreWindow();
            }
        }


        @Override
        public void onLiveState(boolean isActive) {
            isLive = isActive;
            llLiving.setVisibility(isLive && call.isHost() ? View.VISIBLE : View.GONE);
            if (popupWindowMore != null && popupWindowMore.isShowing()) {
                refreshMoreWindow();
            }
        }

        @Override
        public void onWhiteBoardReload(String url, String uuid) {

        }
    };


}

