package com.aryupay.helpingapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.ui.fragments.MyPingFragment;
import com.aryupay.helpingapp.ui.fragments.activity.SuggestedFragment;
import com.aryupay.helpingapp.ui.profile.ui.FollowersFragment;
import com.aryupay.helpingapp.ui.profile.ui.FollowingFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FollowerFollowingHelpingActivity extends AppCompatActivity {
    private ViewPager view_pager;
    private TabLayout tab_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_following_helping2);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);
//        int fragmentId = getIntent().getIntExtra("frag1", 0);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        int defaultValue = 0;
        int page = getIntent().getIntExtra("frag1", defaultValue);
        view_pager.setCurrentItem(page);
        tab_layout.setupWithViewPager(view_pager);
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