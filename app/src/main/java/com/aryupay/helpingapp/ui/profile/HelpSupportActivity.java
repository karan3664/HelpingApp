package com.aryupay.helpingapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aryupay.helpingapp.R;

public class HelpSupportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivClose;
    LinearLayout llHelpCenter, llRateUs, llInvite, llDeactivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);
        llHelpCenter = findViewById(R.id.llHelpCenter);
        llRateUs = findViewById(R.id.llRateUs);
        llInvite = findViewById(R.id.llInvite);
        llDeactivate = findViewById(R.id.llDeactivate);
        ivClose = findViewById(R.id.ivClose);

        ivClose.setOnClickListener(this);
        llHelpCenter.setOnClickListener(this);
        llRateUs.setOnClickListener(this);
        llInvite.setOnClickListener(this);
        llDeactivate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llHelpCenter:
                break;
            case R.id.llRateUs:
                break;
            case R.id.llInvite:
                break;
            case R.id.llDeactivate:
                break;

            case R.id.ivClose:
                onBackPressed();
                break;
        }
    }
}