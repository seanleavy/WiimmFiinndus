package com.wii.sean.wiimmfiitus.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.friendSearch.MkFriendSearch;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.List;

public class MkWiiHomeActivity extends AppCompatActivity {

    private RecyclerView wiiCyclerView;
    private CustomWiiCyclerViewAdapter wiiAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private FloatingActionButton startButton;
    private ImageView wiimfiiIcon;
    private TextView miisFoundTextViewLabel;
    private TextView miisFoundTextViewValue;
    private ItemTouchHelper miiItemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleMiiItemTouchCallback;

    private int friendsFound = 0;

    private List<MiiCharacter> wiiList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_wii_home_activity);

        startButton = (FloatingActionButton) findViewById(R.id.button_search_frame);
        wiiCyclerView = (RecyclerView) findViewById(R.id.home_screen_recycler_view);
        wiimfiiIcon = (ImageView) findViewById(R.id.wiimfii_icon);
        miisFoundTextViewLabel = (TextView) findViewById(R.id.miis_found_label);
        miisFoundTextViewValue = (TextView) findViewById(R.id.miis_found_value);

        wiiCyclerView.setHasFixedSize(false);
        recyclerLayoutManager = new LinearLayoutManager(this);
        wiiCyclerView.setLayoutManager(recyclerLayoutManager);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FriendSearchAsyncTask().execute();
            }
        });

        wiimfiiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.press_search, Toast.LENGTH_SHORT).show();
            }
        });

//        simpleMiiItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                wiiList.remove(viewHolder.getAdapterPosition());
//                friendsFound --;
//                miisFoundTextViewValue.setText(String.valueOf(friendsFound));
//                wiiAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//            }
//        };
//        simpleMiiItemTouchCallback.getSwipeVelocityThreshold(0f);
//        miiItemTouchHelper = new ItemTouchHelper(simpleMiiItemTouchCallback);
//        miiItemTouchHelper.attachToRecyclerView(wiiCyclerView);
    }

    private class FriendSearchAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            wiiList = MkFriendSearch.searchFriendList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            friendsFound = wiiList.size();
            wiiAdapter = new CustomWiiCyclerViewAdapter(wiiList);
            wiiCyclerView.setAdapter(wiiAdapter);
            miisFoundTextViewLabel.setVisibility(View.VISIBLE);
            miisFoundTextViewValue.setVisibility(View.VISIBLE);
            miisFoundTextViewValue.setText(String.valueOf(friendsFound));
        }
    }
}
