package com.wii.sean.wiimmfiitus.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.adapters.CustomFragmentStatePagerAdapter;
import com.wii.sean.wiimmfiitus.fragments.FavouritesFragment;
import com.wii.sean.wiimmfiitus.fragments.MiiSearchFragment;

public class MkWiiHomeActivity extends AppCompatActivity implements MiiSearchFragment.OnFragmentInteractionListener, FavouritesFragment.OnFragmentInteractionListener {

    private ViewPager mViewPager;
    private CustomFragmentStatePagerAdapter customFragmentPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_wii_home_activity);
        mViewPager = (ViewPager) findViewById(R.id.homescreen_view_pager);
        customFragmentPagerAdapter = new CustomFragmentStatePagerAdapter(getSupportFragmentManager(), 0);
        mViewPager.setAdapter(customFragmentPagerAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
