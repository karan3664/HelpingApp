package com.aryupay.helpingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.ui.fragments.ChatFragment;
import com.aryupay.helpingapp.ui.fragments.HomeFragment;
import com.aryupay.helpingapp.ui.fragments.MyPingFragment;
import com.aryupay.helpingapp.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity  implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(this);


        openFragment(HomeFragment.newInstance());

    }



    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
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
                openFragment(ChatFragment.newInstance("", ""));
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
}