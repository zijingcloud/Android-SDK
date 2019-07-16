package com.example.alan.sdkdemo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import android.widget.Switch;
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
import com.vcrtc.entities.StatsItemBean;
import com.vcrtc.utils.BitmapUtil;
import com.vcrtc.utils.OkHttpUtil;
import com.vcrtc.utils.SystemUtil;
import com.vcrtc.utils.VCUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MediaFragment extends Fragment implements View.OnClickListener {

    private final int PDF_PICKER_REQUEST = 46709;
    private final int HIDE_BAR = 1;
    private final int REFRESH_TIME = 2;
    private final int REFRESH_STATS = 3;
    private final int START_SCREEN_SHARE = 4;

    private View rootView;
    public RelativeLayout rlTopBar, rlToolBar, rlLoading, rlShareScreen;
    private FrameLayout flLocalVideo;
    private ZoomFrameLayout flRemoteVideo;
    private TextView tvTime, tvChanel, tvPeopleNum;
    private LinearLayout llStats;
    private ImageView ivMuteAudio, ivMuteVideo, ivSwitchCamera, ivShare, ivMore, ivParticipants, ivHangup, ivCircle, ivSignal, ivCloseVideo;
    private ZoomImageView ivPicture;
    private ViewPager vpShare;
    private PopupWindow popupWindowShare, popupWindowMore, popupWindowStats;

    private FrameLayout.LayoutParams layoutParams;

    private VCRTC vcrtc;
    private VCRTCView localView, remoteView;
    private Call call;

    private Timer hideBarTimer;
    private Timer durationTimer;
    private Timer getStatsTimer;
    private TimerTask hideBarTimerTask;
    private TimerTask getStatsTimerTask;
    private int time;
    public boolean isShowBar, isMuteAudio, isMuteVideo, isFront, isShare, isShareScreen, isPresentation;
    private boolean isRecord, isLive;
    private List<String> imagePathList;
    private String layout, hostLayout, guestLayout;
    private String setLayout, setGlayout;
    private int pictureIndex;
    private int peopleCount;
    private int positionPixels = 0;
    private List<StatsItemBean> itemBeanList;
    private StatsAdapter adapter;
    private BitmapUtil bitmapUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_media, container, false);

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
        rlShareScreen = rootView.findViewById(R.id.rl_share_screen);
        flLocalVideo = rootView.findViewById(R.id.fl_local_video);
        flRemoteVideo = rootView.findViewById(R.id.fl_remote_video);
        tvTime = rootView.findViewById(R.id.tv_time);
        tvChanel = rootView.findViewById(R.id.tv_room_num);
        tvPeopleNum = rootView.findViewById(R.id.tv_people_num);
        llStats = rootView.findViewById(R.id.ll_stats);
        ivMuteAudio = rootView.findViewById(R.id.iv_mute_audio);
        ivMuteVideo = rootView.findViewById(R.id.iv_mute_video);
        ivSwitchCamera = rootView.findViewById(R.id.iv_switch_camera);
        ivShare = rootView.findViewById(R.id.iv_share);
        ivParticipants = rootView.findViewById(R.id.iv_participants);
        ivMore = rootView.findViewById(R.id.iv_more);
        ivHangup = rootView.findViewById(R.id.iv_hangup);
        ivPicture = rootView.findViewById(R.id.iv_picture);
        ivCircle = rootView.findViewById(R.id.iv_circle);
        ivSignal = rootView.findViewById(R.id.iv_signal);
        ivCloseVideo = rootView.findViewById(R.id.iv_close_video);
        vpShare = rootView.findViewById(R.id.vp_share);

        flRemoteVideo.setOnClickListener(this);
        llStats.setOnClickListener(this);
        ivMuteAudio.setOnClickListener(this);
        ivMuteVideo.setOnClickListener(this);
        ivSwitchCamera.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivParticipants.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        ivHangup.setOnClickListener(this);

        flRemoteVideo.setOnClickListener(() -> {
            if (isShowBar) {
                hideBar();
            } else {
                showBar();
            }
        });

        ivPicture.setOnClickListener(() -> {
            if (isShowBar) {
                hideBar();
            } else {
                showBar();
            }
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
                switch (state){
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
                }
            }
        });
    }

    private void initData() {
        call = ((ZJConferenceActivity)getActivity()).call;
        vcrtc = ((ZJConferenceActivity)getActivity()).vcrtc;

        tvChanel.setText(call.getChannel());

        if (ZJConferenceActivity.joinMuteAudio) {
            ivMuteAudio.setSelected(true);
        }

        if (!call.isHost()) {
            ivMore.setVisibility(View.GONE);
        }

        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        isMuteAudio = false;
        isMuteVideo = false;
        isFront = true;
        isShare = false;
        isShareScreen = false;
        imagePathList = new ArrayList<>();
        itemBeanList = new ArrayList<>();
        peopleCount = 0;

        ((ZJConferenceActivity)getActivity()).setMediaCallBack(callBack);
        bitmapUtil = new BitmapUtil(getActivity());
    }

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
        } else if (i == R.id.ll_stats) {
            startGetStats();
            showMediaStatsWindow();
        } else if (i == R.id.iv_close) {
            popupWindowStats.dismiss();
            stopGetStats();
        }
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
                                bean.setTerminal(getString(R.string.stats_remote));
                                bean.setChanel(getString(R.string.stats_video_recv));
                                bean.setCodec(mediaStats.getCodec());
                                bean.setResolution(mediaStats.getResolution());
                                bean.setFrameRate(mediaStats.getFrameRate() + "");
                                bean.setBitRate(mediaStats.getBitrate() + "");
                                bean.setJitter(mediaStats.getJitter() + "ms");
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
                        e.printStackTrace();
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
            ivSignal.setImageResource(R.mipmap.icon_sign_5);
        } else if (ractionLost > 0 && ractionLost <= 1) {
            ivSignal.setImageResource(R.mipmap.icon_sign_4);
        } else if (ractionLost > 1 && ractionLost <= 2) {
            ivSignal.setImageResource(R.mipmap.icon_sign_3);
        } else if (ractionLost > 2 && ractionLost <=5) {
            ivSignal.setImageResource(R.mipmap.icon_sign_2);
        } else if (ractionLost > 5 && ractionLost <=10) {
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

    private void refreshPeopleNum() {
        tvPeopleNum.setText(peopleCount + "");
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

        if (hideBarTimer != null && hideBarTimerTask != null) {
            hideBarTimer.schedule(hideBarTimerTask, 5000);
        }
    }

    private void hideBar() {
        isShowBar = false;
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

    private void toggleMuteVideo() {
        if (!ZJConferenceActivity.allowCamera) {
            checkCameraPermission();
            return;
        }
        vcrtc.updateVideoImage(((ZJConferenceActivity)getActivity()).closeVideoBitmap);
        vcrtc.setVideoEnable(isMuteVideo, true);
        if (isMuteVideo) {
            flLocalVideo.addView(localView,layoutParams);
            ivCloseVideo.setVisibility(View.INVISIBLE);
        } else {
            flLocalVideo.removeView(localView);
            ivCloseVideo.setVisibility(View.VISIBLE);
        }
        ivMuteVideo.setSelected(!isMuteVideo);
        isMuteVideo = !isMuteVideo;
    }

    private void checkCameraPermission() {
        SoulPermission.getInstance().checkAndRequestPermission(Manifest.permission.CAMERA,
                new CheckRequestPermissionListener() {
                    @Override
                    public void onPermissionOk(Permission permission) {
                        ZJConferenceActivity.allowCamera = true;
                        ivMuteVideo.setSelected(false);
                        localView.setMirror(true);
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

    private void setLayout() {
        vcrtc.setLayout(setLayout, setGlayout);
    }

    private void showShareWindow() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_share,null);
        popupWindowShare = new PopupWindow(view, VCUtil.dp2px(getActivity(),120), RelativeLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindowShare.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowShare.setOutsideTouchable(true);
        int[] location = new int[2];
        ivShare.getLocationOnScreen(location);
        popupWindowShare.showAtLocation(ivShare, Gravity.NO_GRAVITY, location[0] - VCUtil.dp2px(getActivity(),40), location[1] - VCUtil.dp2px(getActivity(),130));

        TextView tvSharePicture = view.findViewById(R.id.tv_share_picture);
        TextView tvShareFile = view.findViewById(R.id.tv_share_file);
        TextView tvShareScreen = view.findViewById(R.id.tv_share_screen);

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
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_more,null);
        popupWindowMore = new PopupWindow(view, VCUtil.dp2px(getActivity(),400),RelativeLayout.LayoutParams.MATCH_PARENT,true);
        popupWindowMore.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowMore.setOutsideTouchable(true);
        popupWindowMore.showAtLocation(rootView, Gravity.RIGHT,0,0);

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
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_stats,null);
        popupWindowStats = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT,false);
        popupWindowStats.setOutsideTouchable(false);

        ImageView ivClose = view.findViewById(R.id.iv_close);
//        TextView tvLowLatency = view.findViewById(R.id.tv_audio_low_latency);
//        TextView tvPro = view.findViewById(R.id.tv_audio_pro);
        ListView lvStats = view.findViewById(R.id.lv_stats);

        ivClose.setOnClickListener(this);
//        tvLowLatency.setText(SystemUtil.hasLowLatencyFeature(getActivity()) ? com.vcrtc.R.string.stats_supported : com.vcrtc.R.string.stats_unsupported);
//        tvPro.setText(SystemUtil.hasProFeature(getActivity()) ? com.vcrtc.R.string.stats_supported : com.vcrtc.R.string.stats_unsupported);

        adapter = new StatsAdapter(getActivity(), itemBeanList, false);
        lvStats.setAdapter(adapter);
    }

    private void showMediaStatsWindow() {
        popupWindowStats.showAtLocation(rootView, Gravity.CENTER,0,0);
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

    private void showToast(String message) {
        ((ZJConferenceActivity)getActivity()).showToast(message, Toast.LENGTH_SHORT);
    }

    private void disconnect() {

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

        ((ZJConferenceActivity)getActivity()).disconnect();
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

        if (remoteView != null) {
            flLocalVideo.removeAllViews();
            flRemoteVideo.removeAllViews();
            flLocalVideo.addView(remoteView, layoutParams);
        }
    }

    private void sendSharePicture() {
        //        Bitmap bitmap = bitmapUtil.formatBitmap16_9(imagePathList.get(pictureIndex), 1920, 1080);
//        vcrtc.sendPresentationBitmap(bitmap);

        File file = new File(imagePathList.get(pictureIndex));
        vcrtc.sendPresentationImage(file);
    }

    private void showPresentation(Bitmap bitmap) {
        ivPicture.setImageBitmap(bitmap);
    }

    private void stopPresentation() {
        vcrtc.stopPresentation();
        ivPicture.reset();
        ivPicture.setVisibility(View.GONE);
        vpShare.setVisibility(View.GONE);
        rlShareScreen.setVisibility(View.GONE);

        if (remoteView != null && localView != null) {
            flLocalVideo.removeAllViews();
            flRemoteVideo.removeAllViews();
            flRemoteVideo.addView(remoteView, layoutParams);
            flLocalVideo.addView(localView, layoutParams);
        }
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
        public void onWhiteBoardReload(String url, String uuid) {

        }


        @Override
        public void onLocalVideo(String uuid, VCRTCView view) {
            if (ZJConferenceActivity.allowCamera)
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
        public void onLocalStream(String uuid, String streamURL) {

        }

        @Override
        public void onAddView(String uuid, VCRTCView view, String viewType) {

        }

        @Override
        public void onRemoveView(String uuid, VCRTCView view) {

        }

        @Override
        public void onRemoteStream(String uuid, String streamURL, String streamType) {

        }

        @Override
        public void onAddParticipant(Participant participant) {
            peopleCount++;
            refreshPeopleNum();
        }

        @Override
        public void onUpdateParticipant(Participant participant) {

        }

        @Override
        public void onRemoveParticipant(String uuid) {
            peopleCount--;
            refreshPeopleNum();
        }

        @Override
        public void onLayoutUpdate(String layout, String hostLayout, String guestLayout) {
            MediaFragment.this.layout = layout;
            MediaFragment.this.hostLayout = hostLayout;
            MediaFragment.this.guestLayout = guestLayout;
            if (popupWindowMore != null && popupWindowMore.isShowing()) {
                refreshMoreWindow();
            }
        }

        @Override
        public void onRoleUpdate(String role) {
            call.setHost(role.equals("HOST") ? true : false);
            getActivity().runOnUiThread(() -> ivMore.setVisibility(call.isHost() ? View.VISIBLE : View.GONE));
        }

        @Override
        public void onLayoutUpdateParticipants(List<String> participants) {

        }

        @Override
        public void onPresentation(boolean isActive, String uuid) {
            isPresentation = isActive;
            if (isActive) {
                if (isShare) {
                    showToast(getString(R.string.sharing_screen_interrupted));
                }
                imagePathList.clear();
                isShare = false;
                isShareScreen = false;
                vcrtc.stopPresentation();
                rlShareScreen.setVisibility(View.GONE);
                ivShare.setSelected(false);
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
                    getActivity().runOnUiThread(() -> {
                        if (isPresentation) {
                            startPresentation();
                        }
                        showPresentation(bitmap);
                    });
                });
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
    };
}
