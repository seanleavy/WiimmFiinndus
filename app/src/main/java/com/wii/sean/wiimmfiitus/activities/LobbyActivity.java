package com.wii.sean.wiimmfiitus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        Button button = (Button) findViewById(R.id.load_lobby);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchAsyncHelper searchAsyncHelper = new SearchAsyncHelper(LobbyActivity.this, LobbyActivity.this);
                searchAsyncHelper.execute(mii.getFriendCode(), MkFriendSearch.ROOMENABLED);
                Toast.makeText(LobbyActivity.this, "pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskComplete(Object result) {
        RoomModel roomModel = (RoomModel)((ArrayList)result).get(0);
        miiList.addAll(roomModel.getMiiList());
        title.setText(roomModel.getRoomName());
        regionTitle.setText(roomModel.getRegionName());
        customWiiCyclerViewAdapter.notifyDataSetChanged();
    }
}
