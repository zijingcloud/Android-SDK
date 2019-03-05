package com.vcrtcdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.vcrtc.VCRTCPreferences;
import com.vcrtc.activities.VCVideoActivity;
import com.vcrtc.activities.VCVideoP2PActivity;
import com.vcrtc.activities.VCVideoShiTongActivity;
import com.vcrtc.activities.VCVideoSimulcastActivity;
import com.vcrtc.activities.VCVideoTVActivity;
import com.vcrtc.entities.Call;
import com.vcrtc.utils.SystemUtil;
import com.vcrtc.utils.VCUtil;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 1000;
    private final int OVERLAY_PERMISSION_REQ_CODE = 1001;

    private EditText etAddress, etPwd, etName;
    private EditText etSipkey, etCallName, etAccount, etMyName;
    private Switch sTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAddress = findViewById(R.id.et_address);
        etPwd = findViewById(R.id.et_pwd);
        etName = findViewById(R.id.et_name);
        etSipkey = findViewById(R.id.et_sipkey);
        etCallName = findViewById(R.id.et_call_name);
        etAccount = findViewById(R.id.et_account);
        etMyName = findViewById(R.id.et_my_name);
        sTV = findViewById(R.id.s_tv);

        if (Build.VERSION.SDK_INT >= 23){
            checkPermission();
        }

    }

    public void openSetting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void joinConference(View view) {
        Call call = new Call();
        call.setChanel(etAddress.getText().toString());
        call.setPassword(etPwd.getText().toString());
        call.setNickname(etName.getText().toString());
        call.setCheckDup(VCUtil.MD5(SystemUtil.getMac(this) + etName.getText().toString()));
        call.setHideMe(false);
        call.setHost(true);

        VCRTCPreferences prefs = new VCRTCPreferences(this);

        Intent intent;

        if (prefs.isShiTongPlatform()) {
            intent = new Intent(this, VCVideoShiTongActivity.class);
        } else {
            if (sTV.isChecked()) {
                intent = new Intent(this, VCVideoTVActivity.class);
            } else {
                if (prefs.isSimulcast()) {
                    intent = new Intent(this, VCVideoSimulcastActivity.class);
                } else {
                    intent = new Intent(this, VCVideoActivity.class);
                }
            }
        }

        intent.putExtra("call",call);
        startActivity(intent);
    }

    public void p2pCall(View view) {
        Call call = new Call();
        call.setChanel(etSipkey.getText().toString());
        call.setCallName(etCallName.getText().toString());
        call.setAccount(etAccount.getText().toString());
        call.setNickname(etMyName.getText().toString());
        call.setCallOut(true);

        Intent intent = new Intent(this, VCVideoP2PActivity.class);
        intent.putExtra("call",call);
        startActivity(intent);
    }

    public void startSelfUI(View view) {
        Intent intent = new Intent(this, MyVideoActivity.class);
        startActivity(intent);
    }

    private void checkPermission() {
        PackageManager pm = getPackageManager();
        String pkgName = this.getPackageName();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(android.Manifest.permission.CAMERA, pkgName)
                && PackageManager.PERMISSION_GRANTED == pm.checkPermission(android.Manifest.permission.RECORD_AUDIO, pkgName)
                && PackageManager.PERMISSION_GRANTED == pm.checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, pkgName)
                && PackageManager.PERMISSION_GRANTED == pm.checkPermission(android.Manifest.permission.READ_PHONE_STATE, pkgName));
        if (!permission) {
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_PHONE_STATE},REQUEST_PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + pkgName));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults.length >= 4){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有摄像头权限", Toast.LENGTH_SHORT).show();
            }
            if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有录制音频权限", Toast.LENGTH_SHORT).show();
            }
            if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有读取sd卡权限", Toast.LENGTH_SHORT).show();
            }
            if (grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有读取电话状态权限", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(this, "无系统弹框权限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
