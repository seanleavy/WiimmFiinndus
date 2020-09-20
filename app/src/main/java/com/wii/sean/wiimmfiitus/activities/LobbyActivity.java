package com.wii.sean.wiimmfiitus.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.customViews.NintendoTextview;
import com.wii.sean.wiimmfiitus.dialogs.AmiigavelDialog;
import com.wii.sean.wiimmfiitus.friendSearch.MkFriendSearch;
import com.wii.sean.wiimmfiitus.friendSearch.SearchAsyncHelper;
import com.wii.sean.wiimmfiitus.helpers.CheaterFriendSearch;
import com.wii.sean.wiimmfiitus.helpers.LogHelper;
import com.wii.sean.wiimmfiitus.interfaces.AsyncTaskCompleteListener;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;
import com.wii.sean.wiimmfiitus.model.RoomModel;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity implements AsyncTaskCompleteListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<MiiCharacter> miiList;
    private CustomWiiCyclerViewAdapter customWiiCyclerViewAdapter;
    private NintendoTextview roomTitle;
    private NintendoTextview regionTitle;
    private NintendoTextview connectionDrops;
    private NintendoTextview connectionDropsLabel;
    private NintendoTextview raceCount;
    private NintendoTextview lobbyCreatedTime;
    private NintendoTextview lobbyCount;
    private ProgressBar progressBar;
    private Runnable runnable;
    final Handler handler = new Handler();
    private Bundle bundle;
    private MiiCharacter mii;
    private boolean viewTypeChange;
    private static boolean appIsInForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewTypeChange = false;
        super.onCreate(savedInstanceState);
        appIsInForeground = true;
        setContentView(R.layout.activity_lobby);
        bundle = this.getIntent().getExtras();
        mii = (MiiCharacter) bundle.getSerializable("mii");
        recyclerView = findViewById(R.id.lobby_recyclerview);
//        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewLayoutManager = new GridLayoutManager(this, 1);
        miiList = new ArrayList<>();
        customWiiCyclerViewAdapter = new CustomWiiCyclerViewAdapter(miiList);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(customWiiCyclerViewAdapter);
        recyclerView.setVerticalFadingEdgeEnabled(true);
        progressBar = findViewById(R.id.progress_bar_search);
        roomTitle = findViewById(R.id.nintendoSecondaryToolbarTextview);
        regionTitle = findViewById(R.id.nintendoToolbarTextview);
        connectionDrops = findViewById(R.id.connection_drops_value);
        connectionDropsLabel = findViewById(R.id.conndrops_label);
        raceCount = findViewById(R.id.race_count);
        lobbyCreatedTime = findViewById(R.id.lobby_created_time);
        lobbyCount = findViewById(R.id.lobby_player_count);
        setOnBoarding();
        refreshLobby();

        RecyclerItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) -> AmiigavelDialog.newInstance(miiList.get(position)).show(getSupportFragmentManager(), ""));
    }

    @Override
    protected void onPause() {
        appIsInForeground = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        appIsInForeground = true;
        super.onResume();
    }

    @Override
    public void onTaskComplete(Object result) {
        if(result != null) {
            mii.setType(MiiCharacter.COMPACT_VIEW_DETAILED);
            progressBar.setVisibility(View.INVISIBLE);
            RoomModel roomModel = (RoomModel) ((ArrayList) result).get(0);
            miiList.clear();
            customWiiCyclerViewAdapter.notifyDataSetChanged();
            miiList.addAll(roomModel.getMiiList());
            miiList.get(miiList.indexOf(mii)).setFriend(true);
            CheaterFriendSearch.findCheatersAndFriends(LobbyActivity.this, miiList);
            // todo hardcode shit here for default friends
//            if(miiList.contains(FriendCodes.PONCHO)) {
            regionTitle.setText(roomModel.getRegionName());
            if(roomModel.getConnectionRating().equals("")) {
                roomModel.setConnectionRating(" 0");
            }
            connectionDrops.setText(roomModel.getConnectionRating());
            connectionDropsLabel.setVisibility(View.VISIBLE);
            raceCount.setText(roomModel.getTimesRaced());
            roomTitle.setText(roomModel.getRoomName());
            lobbyCount.setText(Integer.toString(miiList.size()));
            lobbyCreatedTime.setText(roomModel.getLobbyCreatedTime());
            recyclerView.setAdapter(recyclerView.getAdapter());
            customWiiCyclerViewAdapter.notifyDataSetChanged();
            viewTypeChange = false;
            setOnBoarding();
        }



        if(result == null && mii.isFriend()) {
            if(viewTypeChange == false) {
                mii.setType(MiiCharacter.DEFAULT_VIEW);
                miiList.clear();
                miiList.add(mii);
                customWiiCyclerViewAdapter = new CustomWiiCyclerViewAdapter(miiList);
                recyclerView.setLayoutManager(recyclerViewLayoutManager);
                recyclerView.setAdapter(customWiiCyclerViewAdapter);
                viewTypeChange = true;
            }

            roomTitle.setText("waiting for friends...");
        }
        if(result == null && !mii.isFriend()) {
            Toast.makeText(this, getString(R.string.offline), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void refreshLobby() {
        final SearchAsyncHelper searchAsyncHelper = new SearchAsyncHelper(LobbyActivity.this, LobbyActivity.this);
        searchAsyncHelper.execute(mii.getFriendCode(), MkFriendSearch.ROOMENABLED);
        runnable = new Runnable() {
            @Override
            public void run() {
                final SearchAsyncHelper searchAsyncHelper = new SearchAsyncHelper(LobbyActivity.this, LobbyActivity.this);
                searchAsyncHelper.execute(mii.getFriendCode(), MkFriendSearch.ROOMENABLED);
                Log.d(LogHelper.getTag(getClass()), "I am refresh!");
                progressBar.setVisibility(View.VISIBLE);
                handler.postDelayed(this, 15000);
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void setOnBoarding() {
        int visibility = miiList.size() > 0 ? View.INVISIBLE :  View.VISIBLE;
        findViewById(R.id.image_lobby).setVisibility(visibility);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "1";
            String description = "default";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
