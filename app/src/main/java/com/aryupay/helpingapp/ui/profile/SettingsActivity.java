package com.aryupay.helpingapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aryupay.helpingapp.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llPrivacy, llLogout, llDeactivate, llNotification;
    ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        llPrivacy = findViewById(R.id.llPrivacy);
        llNotification = findViewById(R.id.llNotification);
        llLogout = findViewById(R.id.llLogout);
        llDeactivate = findViewById(R.id.llDeactivate);
        ivClose = findViewById(R.id.ivClose);

        ivClose.setOnClickListener(this);
        llPrivacy.setOnClickListener(this);
        llNotification.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llDeactivate.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llPrivacy:
                Intent changePassword = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(changePassword);
                break;
            case R.id.llNotification:
                Intent noti = new Intent(SettingsActivity.this, NotificationsActivity.class);
                startActivity(noti);
                break;
            case R.id.llLogout:
                break;
            case R.id.llDeactivate:
                break;
            case R.id.ivClose:
                onBackPressed();
                break;
        }
    }
}