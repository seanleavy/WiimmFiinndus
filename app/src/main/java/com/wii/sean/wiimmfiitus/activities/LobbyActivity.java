package com.wii.sean.wiimmfiitus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.customViews.NintendoTextview;
import com.wii.sean.wiimmfiitus.friendSearch.MkFriendSearch;
import com.wii.sean.wiimmfiitus.friendSearch.SearchAsyncHelper;
import com.wii.sean.wiimmfiitus.interfaces.AsyncTaskCompleteListener;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;
import com.wii.sean.wiimmfiitus.model.RoomModel;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity implements AsyncTaskCompleteListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<MiiCharacter> miiList;
    private CustomWiiCyclerViewAdapter customWiiCyclerViewAdapter;
    private NintendoTextview title;
    private NintendoTextview regionTitle;
    private NintendoTextview connectionLabel;
    private NintendoTextview connectionDrops;
    private NintendoTextview raceCount;
    private NintendoTextview lobbyCreatedTime;
    private NintendoTextview lobbyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Bundle bundle = this.getIntent().getExtras();
        final MiiCharacter mii = (MiiCharacter) bundle.getSerializable("mii");
        recyclerView = (RecyclerView) findViewById(R.id.lobby_recyclerview);
//        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewLayoutManager = new GridLayoutManager(this, 1);
        miiList = new ArrayList<>();
        customWiiCyclerViewAdapter = new CustomWiiCyclerViewAdapter(miiList);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(customWiiCyclerViewAdapter);
        title = (NintendoTextview) findViewById(R.id.nintendoToolbarTextview);
        regionTitle = (NintendoTextview) findViewById(R.id.nintendoSecondaryToolbarTextview);
        connectionLabel = (NintendoTextview) findViewById(R.id.connection_drops_label);
        connectionDrops = (NintendoTextview) findViewById(R.id.connection_drops_value);
        raceCount = (NintendoTextview) findViewById(R.id.race_count);
        lobbyCreatedTime = (NintendoTextview) findViewById(R.id.lobby_created_time);
        lobbyCount = (NintendoTextview) findViewById(R.id.lobby_player_count);
        SearchAsyncHelper searchAsyncHelper = new SearchAsyncHelper(LobbyActivity.this, LobbyActivity.this);
        searchAsyncHelper.execute(mii.getFriendCode(), MkFriendSearch.ROOMENABLED);
    }

    @Override
    public void onTaskComplete(Object result) {
        if(result != null) {
            RoomModel roomModel = (RoomModel) ((ArrayList) result).get(0);
            miiList.addAll(roomModel.getMiiList());
            title.setText(roomModel.getRoomName());
            regionTitle.setText(roomModel.getRegionName());
            connectionLabel.setText(getString(R.string.connection_drops_label));
            String connRating = roomModel.getConnectionRating();
            if(connRating.equals(""))
                connRating = "none";
            connectionDrops.setText(connRating);
            raceCount.setText(roomModel.getTimesRaced());
            lobbyCount.setText("Players " + Integer.toString(miiList.size()));
            lobbyCreatedTime.setText(roomModel.getLobbyCreatedTime());
            customWiiCyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
