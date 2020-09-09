package com.aryupay.helpingapp.ui.fragments.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.fragments.activity.fragment_follower.OtherFollowersFragment;
import com.aryupay.helpingapp.ui.fragments.activity.fragment_follower.OtherFollowingFragment;
import com.aryupay.helpingapp.ui.profile.FollowerFollowingHelpingActivity;
import com.aryupay.helpingapp.ui.profile.ui.FollowersFragment;
import com.aryupay.helpingapp.ui.profile.ui.FollowingFragment;
import com.aryupay.helpingapp.ui.profile.ui.SuggestedFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class OtherFollowFollowingActivity extends AppCompatActivity {
    private ViewPager view_pager;
    private TabLayout tab_layout;
    LoginModel loginModel;
    TextView tvMyPing;
    ImageView ivBack;
    String name, user_id;
    public static final String PREFS_NAME = "profile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_follow_following);
        loginModel = PrefUtils.getUser(OtherFollowFollowingActivity.this);
        tvMyPing = findViewById(R.id.tvMyPing);
        ivBack = findViewById(R.id.ivBack);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);
        Intent i = getIntent();
        name = i.getStringExtra("name");
        user_id = i.getStringExtra("user_id");
        tvMyPing.setText(name);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user_id",  user_id+ "");
        editor.commit();
//        int fragmentId = getIntent().getIntExtra("frag1", 0);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        int defaultValue = 0;
        int page = getIntent().getIntExtra("frag1", defaultValue);
        view_pager.setCurrentItem(page);
        tab_layout.setupWithViewPager(view_pager);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {

        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bundle.putString("name", name);
        Fragment fragmentDesc = new OtherFollowersFragment();
        fragmentDesc.setArguments(bundle);

        Bundle bundle1 = new Bundle();
        bundle1.putString("user_id", user_id);
        bundle1.putString("name", name);
        Fragment fragment = new OtherFollowingFragment();
        fragment.setArguments(bundle1);

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentDesc, "Followers");
        adapter.addFragment(fragment, "Following");
//        adapter.addFragment(SuggestedFragment.newInstance("", ""), "Suggested");


        viewPager.setAdapter(adapter);
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}