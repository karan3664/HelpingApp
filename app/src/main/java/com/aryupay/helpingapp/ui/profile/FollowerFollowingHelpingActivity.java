package com.aryupay.helpingapp.ui.profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.profile.ui.SuggestedFragment;
import com.aryupay.helpingapp.ui.profile.ui.FollowersFragment;
import com.aryupay.helpingapp.ui.profile.ui.FollowingFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FollowerFollowingHelpingActivity extends AppCompatActivity {
    private ViewPager view_pager;
    private TabLayout tab_layout;
    LoginModel loginModel;
    TextView tvMyPing;
    ImageView ivBack;
EditText edtSearch;
    RelativeLayout rlSearch;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_following_helping2);
        loginModel = PrefUtils.getUser(FollowerFollowingHelpingActivity.this);
        tvMyPing = findViewById(R.id.tvMyPing);
        ivBack = findViewById(R.id.ivBack);
        tvMyPing.setText(loginModel.getData().getUser().getFullname() + "");
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);
//        int fragmentId = getIntent().getIntExtra("frag1", 0);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        int defaultValue = 0;
        int page = getIntent().getIntExtra("frag1", defaultValue);
        view_pager.setCurrentItem(page);
        view_pager.setOffscreenPageLimit(2);
//        view_pager.setCurrentItem(page);


        if (page == defaultValue) {

            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
            Intent i = new Intent("TAG_REFRESH");
            lbm.sendBroadcast(i);

        }
        tab_layout.setupWithViewPager(view_pager);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FollowerFollowingHelpingActivity.this, SearchFollowFollowingActivity.class);
                startActivity(i);
            }
        });
    }
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.


    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(FollowersFragment.newInstance("", ""), "Followers");
        adapter.addFragment(FollowingFragment.newInstance("", ""), "Following");
        adapter.addFragment(SuggestedFragment.newInstance("", ""), "Suggested");


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