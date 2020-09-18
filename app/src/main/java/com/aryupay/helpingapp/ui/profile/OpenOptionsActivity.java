package com.aryupay.helpingapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpenOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llHelpSupport, llSettings;
    CircleImageView ivProfileImage;
    TextView tv_name;
    LoginModel loginModel;
    ImageView ivClose, iv_editprofile;
    RelativeLayout rvOpenEditProfile, rvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_options);
        loginModel = PrefUtils.getUser(OpenOptionsActivity.this);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivClose = findViewById(R.id.ivClose);
        llHelpSupport = findViewById(R.id.llHelpSupport);
        llSettings = findViewById(R.id.llSettings);
        tv_name = findViewById(R.id.tv_name);
        iv_editprofile = findViewById(R.id.iv_editprofile);
        rvOpenEditProfile = findViewById(R.id.rvOpenEditProfile);
        rvBack = findViewById(R.id.rvBack);
        tv_name.setText(loginModel.getData().getUser().getFullname() + "");
        if (loginModel.getData().getUser().getUserDetail().getPhoto() != null) {
            Glide.with(this)
                    .load(BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage"))
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(ivProfileImage);
            Log.e("Profile=>", BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage" + ""));
        }
        ivClose.setOnClickListener(this);
        llSettings.setOnClickListener(this);
        llHelpSupport.setOnClickListener(this);
        iv_editprofile.setOnClickListener(this);
        rvOpenEditProfile.setOnClickListener(this);
        rvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llSettings:
                Intent settings = new Intent(OpenOptionsActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.llHelpSupport:
                Intent help = new Intent(OpenOptionsActivity.this, HelpSupportActivity.class);
                startActivity(help);
                break;
            case R.id.ivClose:
                onBackPressed();
                break;
            case R.id.rvBack:
                onBackPressed();
                break;

            case R.id.iv_editprofile:
                Intent i = new Intent(OpenOptionsActivity.this, EditProfileActivity.class);
                startActivity(i);
                break;
            case R.id.rvOpenEditProfile:
                Intent ii = new Intent(OpenOptionsActivity.this, EditProfileActivity.class);
                startActivity(ii);
                break;
        }
    }
}