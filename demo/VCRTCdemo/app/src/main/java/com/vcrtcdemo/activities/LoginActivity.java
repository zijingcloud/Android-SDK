package com.vcrtcdemo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vcrtc.VCSevice;
import com.vcrtc.registration.VCRegistrationUtil;
import com.vcrtcdemo.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etAccount, etPwd;

    private LoninReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etAccount = findViewById(R.id.account);
        etPwd = findViewById(R.id.pwd);

        receiver = new LoninReceiver();
        IntentFilter filter = new IntentFilter(VCSevice.VC_ACTION);
        registerReceiver(receiver, filter);
    }

    public void login(View view) {
        VCRegistrationUtil.login(this, etAccount.getText().toString(), etPwd.getText().toString());
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class LoninReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra(com.vcrtc.VCSevice.MSG);
            switch (msg) {
                case VCSevice.MSG_LOGIN_SUCCESS:
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case VCSevice.MSG_LOGIN_FAILED:
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
