package com.wii.sean.wiimmfiitus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.friendSearch.SearchAsyncHelper;
import com.wii.sean.wiimmfiitus.interfaces.AsyncTaskCompleteListener;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

public class LobbyActivity extends AppCompatActivity implements AsyncTaskCompleteListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Bundle bundle = this.getIntent().getExtras();
        MiiCharacter mii = (MiiCharacter) bundle.getSerializable("mii");
        // modify search to group all miis in room to a list
        // create a room model maybe or expand Miicharacter or use Builder pattern?
        // search for mii
        // where does room title and race type come from? Put it in every mii object or put a list of builder miicharacters in a race model object and return it as a result?
        // connection fail threshold above 3 change card background to red

        //Race model:
        // new Race(String region, String roomLabel, String numberRaces, String lastStart, List<MiiCharacter> list)
        //

        // create initial adapter with all miis in group
        // search every 15 seconds in this activities lifetime. add option to change refresh time. Maybe use a spinner to limit server abuse. Save it in sharedpreferneces
        // if list changes notifydatasetchanged in ontaskcomplete which should redraw recyclerview
        // set recyclerview as a compact layout. Single column?
        // enable swipe add favourites
        // Layout
        // Lobby Name on top underneath lobby title
        // could be issue with animations due to constant updating. Maybe add a flag to vieewholder animate to disable
    }

    @Override
    public void onTaskComplete(Object result) {

    }
}
