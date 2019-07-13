package com.vcrtcdemo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.vcrtc.VCRTCPreferences;
import com.vcrtc.callbacks.CallBack;
import com.vcrtcdemo.R;

public class SettingActivity extends AppCompatActivity {

    VCRTCPreferences prefs;

    private EditText etApiServer
            ,etCaptureW,etCaptureH,etCaptureF
            ,etUpW,etUpH,etUpF
            ,etDownW,etDownH,etDownF
            ,etSmallW,etSmallH,etSmallF
            ,etUpBw,etMaxF,etDownBw,etSmallBw
            ,etPresentationCaptureW,etPresentationCaptureH,etPresentationCaptureF
            ,etPresentationUpW,etPresentationUpH,etPresentationUpF
            ,etPresentationBw,etPresentationMaxF;

    private Switch sRecv,sSend,sEnableH264Encoder,sDisableH264Decoder,sPrintLogs;

    private RadioButton rbAuto,rbEnabled,rbDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        initData();
    }

    private void initView() {
        etApiServer = findViewById(R.id.et_apiServer);
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
        etPresentationCaptureW = findViewById(R.id.et_presentation_capture_w);
        etPresentationCaptureH = findViewById(R.id.et_presentation_capture_h);
        etPresentationCaptureF = findViewById(R.id.et_presentation_capture_f);
        etPresentationUpW = findViewById(R.id.et_presentation_up_w);
        etPresentationUpH = findViewById(R.id.et_presentation_up_h);
        etPresentationUpF = findViewById(R.id.et_presentation_up_f);
        etPresentationBw = findViewById(R.id.et_presentation_bw);
        etPresentationMaxF = findViewById(R.id.et_presentation_max_f);
        sRecv = findViewById(R.id.s_recv_stream);
        sSend = findViewById(R.id.s_send_stream);
        sEnableH264Encoder = findViewById(R.id.s_enable_h264_encoder);
        sDisableH264Decoder = findViewById(R.id.s_disable_h264_decoder);
        sPrintLogs = findViewById(R.id.s_print_logs);
        rbAuto = findViewById(R.id.rb_auto);
        rbEnabled = findViewById(R.id.rb_enabled);
        rbDisabled = findViewById(R.id.rb_disabled);
    }

    private void initData() {
        prefs = new VCRTCPreferences(this);
        etApiServer.setText(prefs.getApiServer());
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
        etPresentationCaptureW.setText(String.valueOf(prefs.getVideoPresentationWidthCapture()));
        etPresentationCaptureH.setText(String.valueOf(prefs.getVideoPresentationHeightCapture()));
        etPresentationCaptureF.setText(String.valueOf(prefs.getFpsPresentationCapture()));
        etPresentationUpW.setText(String.valueOf(prefs.getVideoPresentationWidthUp()));
        etPresentationUpH.setText(String.valueOf(prefs.getVideoPresentationHeightUp()));
        etPresentationUpF.setText(String.valueOf(prefs.getFpsPresentationUp()));
        etPresentationBw.setText(String.valueOf(prefs.getBandwidthPresentation()));
        etPresentationMaxF.setText(String.valueOf(prefs.getFpsPresentationMax()));
        sRecv.setChecked(prefs.isSimulcast());
        sSend.setChecked(prefs.isMultistream());
        sEnableH264Encoder.setChecked(prefs.isEnableH264HardwareEncoder());
        sDisableH264Decoder.setChecked(prefs.isDisableH264hHardwareDecoder());
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

    public void check(View view) {
        prefs.setServerAddress(etApiServer.getText().toString(), new CallBack() {
            @Override
            public void success(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void failure(String reason) {
                runOnUiThread(() -> {
                    Toast.makeText(SettingActivity.this, reason, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void save(View view) {
        prefs.setApiServer(etApiServer.getText().toString());

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

        prefs.setCapturePresentationVideoSize(
                Integer.parseInt(etPresentationCaptureW.getText().toString()),
                Integer.parseInt(etPresentationCaptureH.getText().toString()));

        prefs.setCapturePresentationVideoFps(Integer.parseInt(etPresentationCaptureF.getText().toString()));

        prefs.setPresentationVideoSize(
                Integer.parseInt(etPresentationUpW.getText().toString()),
                Integer.parseInt(etPresentationUpH.getText().toString()));

        prefs.setPresentationVideoFps(Integer.parseInt(etPresentationUpF.getText().toString()));

        prefs.setBandwidthPresentation(Integer.parseInt(etPresentationBw.getText().toString()));

        prefs.setPresentationMaxVideoFps(Integer.parseInt(etPresentationMaxF.getText().toString()));

        prefs.setSimulcast(sRecv.isChecked());
        prefs.setMultistream(sSend.isChecked());
        prefs.setEnableH264HardwareEncoder(sEnableH264Encoder.isChecked());
        prefs.setDisableH264hHardwareDecoder(sDisableH264Decoder.isChecked());
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
