package com.zjrtcdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.zjrtc.ZJRTCPreferences;

public class SettingActivity extends AppCompatActivity {

    ZJRTCPreferences prefs;

    private EditText etApiServer,etLiveRecordServer
            ,etPreviewW,etPreviewH,etPreviewF
            ,etUpW,etUpH,etUpF
            ,etDownW,etDownH,etDownF
            ,etSmallW,etSmallH,etSmallF
            ,etUpBw,etDownBw,etSmallBw;

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
        etLiveRecordServer = findViewById(R.id.et_liveRecordServer);
        etPreviewW = findViewById(R.id.et_preview_w);
        etPreviewH = findViewById(R.id.et_preview_h);
        etPreviewF = findViewById(R.id.et_preview_f);
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
        etDownBw = findViewById(R.id.et_down_bw);
        etSmallBw = findViewById(R.id.et_small_bw);
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
        prefs = new ZJRTCPreferences(this);
        etApiServer.setText(prefs.getApiServer());
        etLiveRecordServer.setText(prefs.getLivingRecorderServer());
        etPreviewW.setText(String.valueOf(prefs.getVideoWidthPreview()));
        etPreviewH.setText(String.valueOf(prefs.getVideoHeightPreview()));
        etPreviewF.setText(String.valueOf(prefs.getFpsPreview()));
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
        etDownBw.setText(String.valueOf(prefs.getBandwidthDown()));
        etSmallBw.setText(String.valueOf(prefs.getBandwidthSmall()));
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

    public void save(View view) {
        prefs.setApiServer(etApiServer.getText().toString());
        prefs.setLivingRecorderServer(etLiveRecordServer.getText().toString());

        prefs.setPreviewVideoSize(
                Integer.parseInt(etPreviewW.getText().toString()),
                Integer.parseInt(etPreviewH.getText().toString()));

        prefs.setPreviewVideoFps(Integer.parseInt(etPreviewF.getText().toString()));

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

        prefs.setSmallVideFps(Integer.parseInt(etSmallF.getText().toString()));

        prefs.setBandwidth(
                Integer.parseInt(etUpBw.getText().toString()),
                Integer.parseInt(etDownBw.getText().toString()));

        prefs.setBandwidthSmall(Integer.parseInt(etSmallBw.getText().toString()));

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
