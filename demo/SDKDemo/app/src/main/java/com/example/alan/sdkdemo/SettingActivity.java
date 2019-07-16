package com.example.alan.sdkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.vcrtc.VCRTCPreferences;
import com.vcrtc.callbacks.CallBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.et_apiServer)
    EditText etApiServer;
    @BindView(R.id.et_capture_w)
    EditText etCaptureW;
    @BindView(R.id.et_capture_h)
    EditText etCaptureH;
    @BindView(R.id.et_capture_f)
    EditText etCaptureF;
    @BindView(R.id.et_up_w)
    EditText etUpW;
    @BindView(R.id.et_up_h)
    EditText etUpH;
    @BindView(R.id.et_up_f)
    EditText etUpF;
    @BindView(R.id.et_down_w)
    EditText etDownW;
    @BindView(R.id.et_down_h)
    EditText etDownH;
    @BindView(R.id.et_down_f)
    EditText etDownF;
    @BindView(R.id.et_small_w)
    EditText etSmallW;
    @BindView(R.id.et_small_h)
    EditText etSmallH;
    @BindView(R.id.et_small_f)
    EditText etSmallF;
    @BindView(R.id.et_up_bw)
    EditText etUpBw;
    @BindView(R.id.et_max_f)
    EditText etMaxF;
    @BindView(R.id.et_down_bw)
    EditText etDownBw;
    @BindView(R.id.et_small_bw)
    EditText etSmallBw;
    @BindView(R.id.et_presentation_capture_w)
    EditText etPresentationCaptureW;
    @BindView(R.id.et_presentation_capture_h)
    EditText etPresentationCaptureH;
    @BindView(R.id.et_presentation_capture_f)
    EditText etPresentationCaptureF;
    @BindView(R.id.et_presentation_up_w)
    EditText etPresentationUpW;
    @BindView(R.id.et_presentation_up_h)
    EditText etPresentationUpH;
    @BindView(R.id.et_presentation_up_f)
    EditText etPresentationUpF;
    @BindView(R.id.et_presentation_bw)
    EditText etPresentationBw;
    @BindView(R.id.et_presentation_max_f)
    EditText etPresentationMaxF;
    @BindView(R.id.s_recv_stream)
    Switch sRecvStream;
    @BindView(R.id.s_send_stream)
    Switch sSendStream;
    @BindView(R.id.s_enable_h264_encoder)
    Switch sEnableH264Encoder;
    @BindView(R.id.s_disable_h264_decoder)
    Switch sDisableH264Decoder;
    @BindView(R.id.s_disable_camera_encoder)
    Switch sDisableCameraEncoder;
    @BindView(R.id.s_print_logs)
    Switch sPrintLogs;
    @BindView(R.id.rb_auto)
    RadioButton rbAuto;
    @BindView(R.id.rb_enabled)
    RadioButton rbEnabled;
    @BindView(R.id.rb_disabled)
    RadioButton rbDisabled;

    VCRTCPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        preferences = new VCRTCPreferences(this);
        initData();
    }

    private void initData() {
        etApiServer.setText(preferences.getApiServer());
        etCaptureW.setText(String.valueOf(preferences.getVideoWidthCapture()));
        etCaptureH.setText(String.valueOf(preferences.getVideoHeightCapture()));
        etCaptureF.setText(String.valueOf(preferences.getFpsCapture()));
        etUpW.setText(String.valueOf(preferences.getVideoWidthUp()));
        etUpH.setText(String.valueOf(preferences.getVideoHeightUP()));
        etUpF.setText(String.valueOf(preferences.getFpsUp()));
        etDownW.setText(String.valueOf(preferences.getVideoWidthDown()));
        etDownH.setText(String.valueOf(preferences.getVideoHeightDown()));
        etDownF.setText(String.valueOf(preferences.getFpsDown()));
        etSmallW.setText(String.valueOf(preferences.getVideoWidthSmall()));
        etSmallH.setText(String.valueOf(preferences.getVideoHeightSmall()));
        etSmallF.setText(String.valueOf(preferences.getFpsSmall()));
        etUpBw.setText(String.valueOf(preferences.getBandwidthUp()));
        etMaxF.setText(String.valueOf(preferences.getFpsMax()));
        etDownBw.setText(String.valueOf(preferences.getBandwidthDown()));
        etSmallBw.setText(String.valueOf(preferences.getBandwidthSmall()));
        etPresentationCaptureW.setText(String.valueOf(preferences.getVideoPresentationWidthCapture()));
        etPresentationCaptureH.setText(String.valueOf(preferences.getVideoPresentationHeightCapture()));
        etPresentationCaptureF.setText(String.valueOf(preferences.getFpsPresentationCapture()));
        etPresentationUpW.setText(String.valueOf(preferences.getVideoPresentationWidthUp()));
        etPresentationUpH.setText(String.valueOf(preferences.getVideoPresentationHeightUp()));
        etPresentationUpF.setText(String.valueOf(preferences.getFpsPresentationUp()));
        etPresentationBw.setText(String.valueOf(preferences.getBandwidthPresentation()));
        etPresentationMaxF.setText(String.valueOf(preferences.getFpsPresentationMax()));
        sRecvStream.setChecked(preferences.isSimulcast());
        sSendStream.setChecked(preferences.isMultistream());
        sEnableH264Encoder.setChecked(preferences.isEnableH264HardwareEncoder());
        sDisableH264Decoder.setChecked(preferences.isDisableH264hHardwareDecoder());
        sDisableCameraEncoder.setChecked(preferences.isDisableCameraEncoder());
        sPrintLogs.setChecked(preferences.isPrintLogs());
        switch (preferences.getSpeakerphone()) {
            case "auto":
                rbAuto.setChecked(true);
                break;
            case "true":
                rbEnabled.setChecked(true);
                break;
            case "false":
                rbDisabled.setChecked(true);
                break;
                default:
        }
    }

    @OnClick({R.id.btn_check, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_check:
                check(view);
                break;
            case R.id.btn_submit:
                save(view);
                break;
                default:
        }
    }

    public void save(View view) {
        preferences.setApiServer(etApiServer.getText().toString());

        preferences.setCaptureVideoSize(
                Integer.parseInt(etCaptureW.getText().toString()),
                Integer.parseInt(etCaptureH.getText().toString()));

        preferences.setCaptureVideoFps(Integer.parseInt(etCaptureF.getText().toString()));

        preferences.setVideoSize(
                Integer.parseInt(etUpW.getText().toString()),
                Integer.parseInt(etUpH.getText().toString()),
                Integer.parseInt(etDownW.getText().toString()),
                Integer.parseInt(etDownH.getText().toString()));

        preferences.setVideoFps(
                Integer.parseInt(etUpF.getText().toString()),
                Integer.parseInt(etDownF.getText().toString()));

        preferences.setSmallVideoSize(
                Integer.parseInt(etSmallW.getText().toString()),
                Integer.parseInt(etSmallH.getText().toString()));

        preferences.setSmallVideoFps(Integer.parseInt(etSmallF.getText().toString()));

        preferences.setBandwidth(
                Integer.parseInt(etUpBw.getText().toString()),
                Integer.parseInt(etDownBw.getText().toString()));

        preferences.setMaxVideoFps(Integer.parseInt(etMaxF.getText().toString()));

        preferences.setBandwidthSmall(Integer.parseInt(etSmallBw.getText().toString()));

        preferences.setCapturePresentationVideoSize(
                Integer.parseInt(etPresentationCaptureW.getText().toString()),
                Integer.parseInt(etPresentationCaptureH.getText().toString()));

        preferences.setCapturePresentationVideoFps(Integer.parseInt(etPresentationCaptureF.getText().toString()));

        preferences.setPresentationVideoSize(
                Integer.parseInt(etPresentationUpW.getText().toString()),
                Integer.parseInt(etPresentationUpH.getText().toString()));

        preferences.setPresentationVideoFps(Integer.parseInt(etPresentationUpF.getText().toString()));

        preferences.setBandwidthPresentation(Integer.parseInt(etPresentationBw.getText().toString()));

        preferences.setPresentationMaxVideoFps(Integer.parseInt(etPresentationMaxF.getText().toString()));

        preferences.setSimulcast(sRecvStream.isChecked());
        preferences.setMultistream(sSendStream.isChecked());
        preferences.setEnableH264HardwareEncoder(sEnableH264Encoder.isChecked());
        preferences.setDisableH264hHardwareDecoder(sDisableH264Decoder.isChecked());
        preferences.setDisableCameraEncoder(sDisableCameraEncoder.isChecked());
        preferences.setPrintLogs(sPrintLogs.isChecked());

        String speakerphone = "auto";
        if (rbAuto.isChecked()) {
            speakerphone = "auto";
        } else if (rbEnabled.isChecked()) {
            speakerphone = "true";
        } else if (rbDisabled.isChecked()) {
            speakerphone = "false";
        }
        preferences.setSpeakerphone(speakerphone);

        Toast.makeText(this,"保存成功！",Toast.LENGTH_SHORT).show();

        finish();
    }

    public void check(View view) {
        preferences.setServerAddress(etApiServer.getText().toString(), new CallBack() {
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
}
