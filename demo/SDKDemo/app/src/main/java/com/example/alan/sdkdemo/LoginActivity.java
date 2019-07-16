package com.example.alan.sdkdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vcrtc.VCSevice;
import com.vcrtc.registration.VCRegistrationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vcrtc.VCSevice.VC_ACTION;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.btn_login)
    Button btnLogin;
    LoginReceiver receiver = new LoginReceiver();

    // UI references.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        IntentFilter filter = new IntentFilter(VC_ACTION);
        registerReceiver(receiver, filter);
    }

    @OnClick({R.id.btn_login, R.id.btn_logout})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                VCRegistrationUtil.login(this, email.getText().toString(), password.getText().toString());
                break;
            case R.id.btn_logout:
                VCRegistrationUtil.logout(this);
                break;
                default:
        }
    }



    public class LoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(VCSevice.MSG);
            switch (message) {
                case VCSevice.MSG_LOGIN_SUCCESS:
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case VCSevice.MSG_LOGIN_FAILED:
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

