package com.wii.sean.wiimmfiitus.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.adapters.CustomFragmentStatePagerAdapter;
import com.wii.sean.wiimmfiitus.fragments.BaseFragment;
import com.wii.sean.wiimmfiitus.helpers.SnackBarHelper;

import java.util.ArrayList;
import java.util.List;

public class MkWiiHomeActivity extends AppCompatActivity implements BaseFragment.OnFragmentInteractionListener {

    private ViewPager mViewPager;
    private CustomFragmentStatePagerAdapter customFragmentPagerAdapter;
    private List<PreferenceUpdateListener> listeners;
    private TabLayout tabLayout;

    public interface PreferenceUpdateListener {
        void preferenceUpdate();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        listeners = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_wii_home_activity);
        mViewPager = (ViewPager) findViewById(R.id.homescreen_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(mViewPager, true);
        customFragmentPagerAdapter = new CustomFragmentStatePagerAdapter(getSupportFragmentManager(), 0);
        mViewPager.setAdapter(customFragmentPagerAdapter);
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        SnackBarHelper.showSnackBar(this, viewGroup,getResources().getString(R.string.first_run_message), Snackbar.LENGTH_LONG, null);

        preferenceUpdated();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public synchronized void registerPreferenceUPdateListener(PreferenceUpdateListener listener) {
        listeners.add(listener);
    }

    public synchronized void removePreferenceUpdateListener(PreferenceUpdateListener listener) {
        listeners.remove(listener);
    }

    public synchronized void preferenceUpdated() {
        for(PreferenceUpdateListener l : listeners) {
            l.preferenceUpdate();
        }
    }
}
