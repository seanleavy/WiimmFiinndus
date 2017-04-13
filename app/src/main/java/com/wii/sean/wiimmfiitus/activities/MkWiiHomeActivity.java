package com.wii.sean.wiimmfiitus.activities;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.adapters.CustomFragmentStatePagerAdapter;
import com.wii.sean.wiimmfiitus.customViews.NintendoTextview;
import com.wii.sean.wiimmfiitus.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import static com.wii.sean.wiimmfiitus.R.id.nintendoToolbarTextview;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.wiimmfii_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(mViewPager, true);
        customFragmentPagerAdapter = new CustomFragmentStatePagerAdapter(getSupportFragmentManager(), 0);
        mViewPager.setAdapter(customFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        preferenceUpdated();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        NintendoTextview textview = (NintendoTextview) findViewById(nintendoToolbarTextview);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if(mViewPager.getCurrentItem() == 0) {
            textview.setText(R.string.search_friends);
            menu.findItem(R.id.action_refresh).setVisible(false);
            menu.findItem(R.id.load_default_miis).setVisible(false);
            menu.findItem(R.id.menu_load_wiimfii).setVisible(true);
            menu.findItem(R.id.menu_search_history).setVisible(true);
        }
        if(mViewPager.getCurrentItem() == 1) {
            textview.setText(R.string.saved_friends);
            menu.findItem(R.id.action_refresh).setVisible(true);
            menu.findItem(R.id.load_default_miis).setVisible(true);
            menu.findItem(R.id.menu_load_wiimfii).setVisible(false);
            menu.findItem(R.id.menu_search_history).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
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

    public void updateStatusBarColor(int color) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }
}
