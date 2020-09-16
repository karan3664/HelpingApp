package com.aryupay.helpingapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.utils.PrefUtils;

public class LoginOrRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin, btnRegister;
    LoginModel loginModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);
        loginModel = PrefUtils.getUser(LoginOrRegisterActivity.this);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        try {
            if (loginModel.getStatus().matches("success")) {
                Intent i = new Intent(LoginOrRegisterActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;

            case R.id.btnRegister:
                Intent regIntent = new Intent(this, RegisterWithActivity.class);
                startActivity(regIntent);
                break;
        }
    }
}