package com.wii.sean.wiimmfiitus.activities;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.friendSearch.Constants.FriendCodes;
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
    private ImageView nintendoLogo;
    private TextView miisFoundTextViewLabel;
    private TextView miisFoundTextViewValue;
    private ItemTouchHelper miiItemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleMiiItemTouchCallback;
    private ProgressBar progressBar;
    private AlertDialog.Builder alertDialogBuilder;

    private MkFriendSearch mkFriendSearch;
    private int friendsFound = 0;
    private String searchTag = null;

    private List<MiiCharacter> wiiList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mkFriendSearch = new MkFriendSearch();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_wii_home_activity);

        startButton = (FloatingActionButton) findViewById(R.id.button_search_frame);
        wiiCyclerView = (RecyclerView) findViewById(R.id.home_screen_recycler_view);
        wiimfiiIcon = (ImageView) findViewById(R.id.wiimfii_icon);
        miisFoundTextViewLabel = (TextView) findViewById(R.id.miis_found_label);
        miisFoundTextViewValue = (TextView) findViewById(R.id.miis_found_value);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_search);
//        nintendoLogo = (ImageView) findViewById(R.id.nintendo_logo_imageview);

        wiiCyclerView.setHasFixedSize(false);
        recyclerLayoutManager = new LinearLayoutManager(this);
        wiiCyclerView.setLayoutManager(recyclerLayoutManager);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                nintendoLogo.setVisibility(View.INVISIBLE);
                alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                AlertDialog searchDialog;
                final LayoutInflater layoutInflater = getLayoutInflater();
                final View dialogView = layoutInflater.inflate(R.layout.friend_code_dialog, null);
                alertDialogBuilder.setView(dialogView);
                final EditText friendCodeEditText = (EditText) dialogView.findViewById(R.id.friend_code_edittext);
                final Button deleteEdittext = (Button) dialogView.findViewById(R.id.delete_friend_code);
                deleteEdittext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        friendCodeEditText.setText("");
                    }
                });
                friendCodeEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable text) {
                        if(text.length() == 4 || text.length() == 9)  {
                            text.append('-');
                        }
                    }
                });
                alertDialogBuilder.setTitle(R.string.dialog_title)
                        .setMessage(R.string.dialog_body).setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String friendCode = friendCodeEditText.getText().toString();
                        new FriendSearchAsyncTask().execute(friendCode);
                        startButton.setClickable(false);
                        startButton.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        nintendoLogo.setVisibility(View.VISIBLE);
                    }
                }).setNeutralButton("BumChums", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                nintendoLogo.setVisibility(View.INVISIBLE);
                                new FriendSearchAsyncTask().execute("");
                                startButton.setClickable(false);
                                startButton.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });
                searchDialog = alertDialogBuilder.create();
                searchDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                searchDialog.show();
//                needs to be on 2 lines for some reason
                final Button searchButton = searchDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                searchButton.setEnabled(false);
                friendCodeEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(friendCodeEditText.getText().toString().length() >= 14 ) {
                            searchButton.setEnabled(true);
                        } else
                            searchButton.setEnabled(false);
                    }
                });
            }
        });

        wiimfiiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.press_search, Toast.LENGTH_SHORT).show();
            }
        });

        simpleMiiItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                wiiList.remove(viewHolder.getAdapterPosition());
                friendsFound --;
                miisFoundTextViewValue.setText(String.valueOf(friendsFound));
                wiiAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };
        simpleMiiItemTouchCallback.getSwipeVelocityThreshold(0f);
        miiItemTouchHelper = new ItemTouchHelper(simpleMiiItemTouchCallback);
        miiItemTouchHelper.attachToRecyclerView(wiiCyclerView);
    }

    private class FriendSearchAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            wiiList = mkFriendSearch.searchFriendList(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(wiiList != null) {
                friendsFound = wiiList.size();
            }
            wiiAdapter = new CustomWiiCyclerViewAdapter(wiiList);
            wiiCyclerView.setAdapter(wiiAdapter);
            miisFoundTextViewLabel.setVisibility(View.VISIBLE);
            miisFoundTextViewValue.setVisibility(View.VISIBLE);
            miisFoundTextViewValue.setText(String.valueOf(friendsFound));
            startButton.setVisibility(View.VISIBLE);
            startButton.setClickable(true);
            progressBar.setVisibility(View.GONE);
        }
    }
}
