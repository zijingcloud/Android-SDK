package com.vcrtcdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.vcrtc.VCRTCPreferences;

public class SettingActivity extends AppCompatActivity {

    VCRTCPreferences prefs;

    private EditText etApiServer,etLiveRecordServer
            ,etCaptureW,etCaptureH,etCaptureF
            ,etUpW,etUpH,etUpF
            ,etDownW,etDownH,etDownF
            ,etSmallW,etSmallH,etSmallF
            ,etUpBw,etMaxF,etDownBw,etSmallBw
            ,etScreenCaptureW,etScreenCaptureH,etScreenCaptureF
            ,etScreenUpW,etScreenUpH,etScreenUpF
            ,etScreenBw,etScreenMaxF;

    private Switch sShiTong,sRecv,sSend,sEnableH264Encoder,sDisableH264Decoder,sDisableCameraEncoder,sPrintLogs;

    private RadioButton rbAuto,rbEnabled,rbDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        initData();
    }

    private void initView() {
        sShiTong = findViewById(R.id.s_shitong);
        etApiServer = findViewById(R.id.et_apiServer);
        etLiveRecordServer = findViewById(R.id.et_liveRecordServer);
        etCaptureW = findViewById(R.id.et_capture_w);
        etCaptureH = findViewById(R.id.et_capture_h);
        etCaptureF = findViewById(R.id.et_capture_f);
        etUpW = findViewById(R.id.et_up_w);
        etUpH = findViewById(R.id.et_up_h);
        etUpF = findViewById(R.id.et_up_f);
        etDownW = findViewById(R.id.et_down_w);
        etDownH = findViewById(R.id.et_down_h);
        etDownF = findViewById(R.id.et_down_f);
        etSmallW = findViewById(R.id.et_small_w);
        etSmallH = findViewById(R.id.et_small_h);
        etSmallF = findViewById(R.id.et_small_f);
        etUpBw = findViewById(R.id.et_up_bw);
        etMaxF = findViewById(R.id.et_max_f);
        etDownBw = findViewById(R.id.et_down_bw);
        etSmallBw = findViewById(R.id.et_small_bw);
        etScreenCaptureW = findViewById(R.id.et_screen_capture_w);
        etScreenCaptureH = findViewById(R.id.et_screen_capture_h);
        etScreenCaptureF = findViewById(R.id.et_screen_capture_f);
        etScreenUpW = findViewById(R.id.et_screen_up_w);
        etScreenUpH = findViewById(R.id.et_screen_up_h);
        etScreenUpF = findViewById(R.id.et_screen_up_f);
        etScreenBw = findViewById(R.id.et_screen_bw);
        etScreenMaxF = findViewById(R.id.et_screen_max_f);
        sRecv = findViewById(R.id.s_recv_stream);
        sSend = findViewById(R.id.s_send_stream);
        sEnableH264Encoder = findViewById(R.id.s_enable_h264_encoder);
        sDisableH264Decoder = findViewById(R.id.s_disable_h264_decoder);
        sDisableCameraEncoder = findViewById(R.id.s_disable_camera_encoder);
        sPrintLogs = findViewById(R.id.s_print_logs);
        rbAuto = findViewById(R.id.rb_auto);
        rbEnabled = findViewById(R.id.rb_enabled);
        rbDisabled = findViewById(R.id.rb_disabled);
    }

    private void initData() {
        prefs = new VCRTCPreferences(this);
        sShiTong.setChecked(prefs.isShiTongPlatform());
        etApiServer.setText(prefs.getApiServer());
        etLiveRecordServer.setText(prefs.getLivingRecorderServer());
        etCaptureW.setText(String.valueOf(prefs.getVideoWidthCapture()));
        etCaptureH.setText(String.valueOf(prefs.getVideoHeightCapture()));
        etCaptureF.setText(String.valueOf(prefs.getFpsCapture()));
        etUpW.setText(String.valueOf(prefs.getVideoWidthUp()));
        etUpH.setText(String.valueOf(prefs.getVideoHeightUP()));
        etUpF.setText(String.valueOf(prefs.getFpsUp()));
        etDownW.setText(String.valueOf(prefs.getVideoWidthDown()));
        etDownH.setText(String.valueOf(prefs.getVideoHeightDown()));
        etDownF.setText(String.valueOf(prefs.getFpsDown()));
        etSmallW.setText(String.valueOf(prefs.getVideoWidthSmall()));
        etSmallH.setText(String.valueOf(prefs.getVideoHeightSmall()));
        etSmallF.setText(String.valueOf(prefs.getFpsSmall()));
        etUpBw.setText(String.valueOf(prefs.getBandwidthUp()));
        etMaxF.setText(String.valueOf(prefs.getFpsMax()));
        etDownBw.setText(String.valueOf(prefs.getBandwidthDown()));
        etSmallBw.setText(String.valueOf(prefs.getBandwidthSmall()));
        etScreenCaptureW.setText(String.valueOf(prefs.getVideoScreenWidthCapture()));
        etScreenCaptureH.setText(String.valueOf(prefs.getVideoScreenHeightCapture()));
        etScreenCaptureF.setText(String.valueOf(prefs.getFpsScreenCapture()));
        etScreenUpW.setText(String.valueOf(prefs.getVideoScreenWidthUp()));
        etScreenUpH.setText(String.valueOf(prefs.getVideoScreenHeightUp()));
        etScreenUpF.setText(String.valueOf(prefs.getFpsScreenUp()));
        etScreenBw.setText(String.valueOf(prefs.getBandwidthScreen()));
        etScreenMaxF.setText(String.valueOf(prefs.getFpsScreenMax()));
        sRecv.setChecked(prefs.isSimulcast());
        sSend.setChecked(prefs.isMultistream());
        sEnableH264Encoder.setChecked(prefs.isEnableH264HardwareEncoder());
        sDisableH264Decoder.setChecked(prefs.isDisableH264hHardwareDecoder());
        sDisableCameraEncoder.setChecked(prefs.isDisableCameraEncoder());
        sPrintLogs.setChecked(prefs.isPrintLogs());
        switch (prefs.getSpeakerphone()) {
            case "auto":
                rbAuto.setChecked(true);
                break;
            case "true":
                rbEnabled.setChecked(true);
                break;
            case "false":
                rbDisabled.setChecked(true);
                break;
        }
    }

    public void save(View view) {
        prefs.setShiTongPlatform(sShiTong.isChecked());
        prefs.setApiServer(etApiServer.getText().toString());
        prefs.setLivingRecorderServer(etLiveRecordServer.getText().toString());

        prefs.setCaptureVideoSize(
                Integer.parseInt(etCaptureW.getText().toString()),
                Integer.parseInt(etCaptureH.getText().toString()));

        prefs.setCaptureVideoFps(Integer.parseInt(etCaptureF.getText().toString()));

        prefs.setVideoSize(
                Integer.parseInt(etUpW.getText().toString()),
                Integer.parseInt(etUpH.getText().toString()),
                Integer.parseInt(etDownW.getText().toString()),
                Integer.parseInt(etDownH.getText().toString()));

        prefs.setVideoFps(
                Integer.parseInt(etUpF.getText().toString()),
                Integer.parseInt(etDownF.getText().toString()));

        prefs.setSmallVideoSize(
                Integer.parseInt(etSmallW.getText().toString()),
                Integer.parseInt(etSmallH.getText().toString()));

        prefs.setSmallVideoFps(Integer.parseInt(etSmallF.getText().toString()));

        prefs.setBandwidth(
                Integer.parseInt(etUpBw.getText().toString()),
                Integer.parseInt(etDownBw.getText().toString()));

        prefs.setMaxVideoFps(Integer.parseInt(etMaxF.getText().toString()));

        prefs.setBandwidthSmall(Integer.parseInt(etSmallBw.getText().toString()));

        prefs.setCaptureScreenVideoSize(
                Integer.parseInt(etScreenCaptureW.getText().toString()),
                Integer.parseInt(etScreenCaptureH.getText().toString()));

        prefs.setCaptureScreenVideoFps(Integer.parseInt(etScreenCaptureF.getText().toString()));

        prefs.setScreenVideoSize(
                Integer.parseInt(etScreenUpW.getText().toString()),
                Integer.parseInt(etScreenUpH.getText().toString()));

        prefs.setScreenVideoFps(Integer.parseInt(etScreenUpF.getText().toString()));

        prefs.setBandwidthScreen(Integer.parseInt(etScreenBw.getText().toString()));

        prefs.setMaxScreenVideoFps(Integer.parseInt(etScreenMaxF.getText().toString()));

        prefs.setSimulcast(sRecv.isChecked());
        prefs.setMultistream(sSend.isChecked());
        prefs.setEnableH264HardwareEncoder(sEnableH264Encoder.isChecked());
        prefs.setDisableH264hHardwareDecoder(sDisableH264Decoder.isChecked());
        prefs.setDisableCameraEncoder(sDisableCameraEncoder.isChecked());
        prefs.setPrintLogs(sPrintLogs.isChecked());

        String speakerphone = "auto";
        if (rbAuto.isChecked()) {
            speakerphone = "auto";
        } else if (rbEnabled.isChecked()) {
            speakerphone = "true";
        } else if (rbDisabled.isChecked()) {
            speakerphone = "false";
        }
        prefs.setSpeakerphone(speakerphone);

        Toast.makeText(this,"保存成功！",Toast.LENGTH_SHORT).show();

        finish();
    }
}
