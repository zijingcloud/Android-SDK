package com.example.alan.sdkdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alan.sdkdemo.ui.ZJConferenceActivity;
import com.vcrtc.VCRTCPreferences;
import com.vcrtc.entities.Call;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_nickname)
    EditText etNickname;
    @BindView(R.id.et_meet_num)
    EditText etMeetNum;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.btn_setting)
    Button btnSetting;
    private final int REQUEST_PERMISSION = 1000;
    private final int OVERLAY_PERMISSION_REQ_CODE = 1001;
    private final String SHITONG_URL = "line2.51vmr.cn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermission();
    }

    @OnClick(R.id.btn_connect)
    public void onClick() {
        Call call = new Call();
        call.setApiServer(tvAddress.getText().toString());
        call.setNickname(etNickname.getText().toString());
        call.setChannel(etMeetNum.getText().toString());
        call.setPassword(etPassword.getText().toString());
        if (SHITONG_URL.equals(tvAddress.getText().toString())) {
            call.setShitongPlatform(true);
        } else {
            call.setShitongPlatform(false);
        }
        Intent intent = new Intent(this, ZJConferenceActivity.class);
        intent.putExtra("call", call);
        startActivity(intent);
    }


    @OnClick({R.id.btn_setting, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            default:
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        PackageManager pm = getPackageManager();
        String pkgName = this.getPackageName();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, pkgName)
                && PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest.permission.READ_PHONE_STATE, pkgName));
        if (!permission) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.main_tips);
                builder.setMessage(getString(R.string.main_request_system_alert_window));
                //设置确定按钮
                builder.setPositiveButton(R.string.main_go_setting, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + pkgName));
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    dialog.dismiss(); //关闭dialog
                });
                builder.setCancelable(false);

                builder.create();
                builder.show();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults.length >= 2) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            }
            if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                }
            }
        }
    }

}
