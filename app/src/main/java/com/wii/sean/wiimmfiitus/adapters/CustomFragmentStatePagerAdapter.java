package com.wii.sean.wiimmfiitus.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wii.sean.wiimmfiitus.activities.MkWiiHomeActivity;
import com.wii.sean.wiimmfiitus.fragments.FavouritesFragment;
import com.wii.sean.wiimmfiitus.fragments.MiiSearchFragment;

public class CustomFragmentStatePagerAdapter extends FragmentStatePagerAdapter implements MkWiiHomeActivity.PreferenceUpdateListener {

    private int NUMBER_OF_FRAGMENTS = 2;

    public CustomFragmentStatePagerAdapter(FragmentManager fm, int tabcounter) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MiiSearchFragment.newInstance();
            case 1:
                return FavouritesFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }

    @Override
    public void preferenceUpdate() {
    }
}
