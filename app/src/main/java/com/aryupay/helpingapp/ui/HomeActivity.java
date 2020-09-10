package com.aryupay.helpingapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.addBlog.AddBlogActivity;
import com.aryupay.helpingapp.ui.chats.ChatListActivity;
import com.aryupay.helpingapp.ui.fragments.ChatFragment;
import com.aryupay.helpingapp.ui.fragments.HomeFragment;
import com.aryupay.helpingapp.ui.fragments.MyPingFragment;
import com.aryupay.helpingapp.ui.profile.ProfileActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    LoginModel loginModel;
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loginModel = PrefUtils.getUser(this);

        //getting bottom navigation view and attaching the listener
        navigation = findViewById(R.id.nav_view);
        navigation.setItemIconTintList(null);

        navigation.setOnNavigationItemSelectedListener(this);
        if (loginModel.getData().getUser().getUserDetail().getPhoto() != null) {
            Glide.with(getApplicationContext()).asBitmap().load(BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage"))
                    .into(new CustomTarget<Bitmap>() {

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Drawable profileImage = new BitmapDrawable(getResources(), resource);
                            navigation.getMenu().getItem(4).setIcon(profileImage);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }


        openFragment(HomeFragment.newInstance());

    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                openFragment(HomeFragment.newInstance());
                return true;
            case R.id.navigation_chat:
//                loadFragment(new ChatFragment());
                Intent chat = new Intent(HomeActivity.this, ChatListActivity.class);
                startActivity(chat);
//                openFragment(ChatFragment.newInstance("", ""));
                return true;
            case R.id.navigation_Add:
                Intent addBlog = new Intent(HomeActivity.this, AddBlogActivity.class);
                startActivity(addBlog);
                return true;
            case R.id.navigation_ping:
                openFragment(MyPingFragment.newInstance("", ""));
                return true;
            case R.id.navigation_profile:
                Intent pro = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(pro);
                return true;
        }
        return false;
    }

 /*   @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        int seletedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.home != seletedItemId) {
            setHomeItem(HomeActivity.this);
        } else {
            super.onBackPressed();
        }
    }
    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }*/
}