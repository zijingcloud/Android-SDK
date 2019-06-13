package com.vcrtcdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vcrtc.callbacks.CallBack;
import com.vcrtcdemo.utils.LoginUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText etAccount, etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etAccount = findViewById(R.id.account);
        etPwd = findViewById(R.id.pwd);
    }

    public void login(View view) {
        LoginUtil loginUtil = new LoginUtil(this, etAccount.getText().toString(), etPwd.getText().toString());
        loginUtil.login(new CallBack() {
            @Override
            public void success(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void failure(String reason) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, reason, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
