package com.wii.sean.wiimmfiitus.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.adapters.CustomFragmentStatePagerAdapter;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.fragments.BaseFragment;
import com.wii.sean.wiimmfiitus.fragments.FavouritesFragment;
import com.wii.sean.wiimmfiitus.fragments.MiiSearchFragment;
import com.wii.sean.wiimmfiitus.helpers.PreferencesManager;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MkWiiHomeActivity extends AppCompatActivity implements BaseFragment.OnFragmentInteractionListener, BaseFragment.UpdateInterface {

    private ViewPager mViewPager;
    private CustomFragmentStatePagerAdapter customFragmentPagerAdapter;
    private List<MiiCharacter> miiFavourites;
    private List<PreferenceUpdateListener> listeners;

    public interface PreferenceUpdateListener {
        void preferenceUpdate();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        listeners = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_wii_home_activity);
        mViewPager = (ViewPager) findViewById(R.id.homescreen_view_pager);
        customFragmentPagerAdapter = new CustomFragmentStatePagerAdapter(getSupportFragmentManager(), 0);
        mViewPager.setAdapter(customFragmentPagerAdapter);
        prefernceUpdated();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void updateFavouritesView() {
    }

    public synchronized void registerPreferenceUPdateListener(PreferenceUpdateListener listener) {
        listeners.add(listener);
    }

    public synchronized void removePreferenceUpdateListener(PreferenceUpdateListener listener) {
        listeners.remove(listener);
    }

    public synchronized void prefernceUpdated() {
        for(PreferenceUpdateListener l : listeners) {
            l.preferenceUpdate();
        }
    }
}
