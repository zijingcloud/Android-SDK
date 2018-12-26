package com.zjrtcdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.zjrtc.ZJRTCPreferences;
import com.zjrtc.activities.ZJVideoActivity;
import com.zjrtc.activities.ZJVideoP2PActivity;
import com.zjrtc.activities.ZJVideoSimulcastActivity;
import com.zjrtc.entities.Call;

public class MainActivity extends AppCompatActivity {

    private EditText etAddress, etPwd, etName;
    private EditText etSipkey, etCallName, etAccount, etMyName;

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

        ZJRTCPreferences prefs = new ZJRTCPreferences(this);
        prefs.setApiServer("bss.lalonline.cn");

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
        call.setCheckDup(((MyApplication)getApplication()).checkDup);
        call.setClayout("1:4");
        call.setHideMe(false);
        call.setHost(true);

        ZJRTCPreferences prefs = new ZJRTCPreferences(this);

        Intent intent;
        if (prefs.isSimulcast()) {
            intent = new Intent(this, ZJVideoSimulcastActivity.class);
        } else {
            intent = new Intent(this, ZJVideoActivity.class);
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

        Intent intent = new Intent(this, ZJVideoP2PActivity.class);
        intent.putExtra("call",call);
        startActivity(intent);
    }

    public void startSelfUI(View view) {
        Intent intent = new Intent(this, MyVideoActivity.class);
        startActivity(intent);
    }
}
