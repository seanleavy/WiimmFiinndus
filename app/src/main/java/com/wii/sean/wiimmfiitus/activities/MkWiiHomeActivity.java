package com.wii.sean.wiimmfiitus.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.friendSearch.MkFriendSearch;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private ProgressBar progressBar;
    private AlertDialog.Builder alertDialogBuilder;
    private Snackbar firstRunSnackbar;
    private EditText friendCodeEditText;

    private MkFriendSearch mkFriendSearch;
    private int friendsFound = 0;
    private String searchTag = null;
    private String searchPreferencesKey = "searchesMade";
    private Set<String> searchHistoryResultSet;

    private List<MiiCharacter> wiiList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mkFriendSearch = new MkFriendSearch();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_wii_home_activity);
        View parentCoordinatorLayout = findViewById(R.id.home_main_layout);
        firstRunSnackbar = Snackbar.make(parentCoordinatorLayout, R.string.first_run_message, Snackbar.LENGTH_LONG);
        firstRunSnackbar.setActionTextColor(getResources().getColor(android.R.color.holo_red_dark));
        firstRunSnackbar.show();

        // To save every fCode search
        final LayoutInflater layoutInflater = getLayoutInflater();
        SharedPreferences savedSearches = this.getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);
        searchHistoryResultSet = savedSearches.getStringSet(searchPreferencesKey, new HashSet<String>());

        startButton = (FloatingActionButton) findViewById(R.id.button_search_frame);
        wiiCyclerView = (RecyclerView) findViewById(R.id.home_screen_recycler_view);
        wiimfiiIcon = (ImageView) findViewById(R.id.wiimfii_icon);
        miisFoundTextViewLabel = (TextView) findViewById(R.id.miis_found_label);
        miisFoundTextViewValue = (TextView) findViewById(R.id.miis_found_value);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_search);

        wiiCyclerView.setHasFixedSize(false);
        recyclerLayoutManager = new LinearLayoutManager(this);
        wiiCyclerView.setLayoutManager(recyclerLayoutManager);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View searchDialogView = layoutInflater.inflate(R.layout.friend_code_dialog, null);
                // building the Search Dialog
                //todo refactor all custom Dialogs to seperate class
                alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                AlertDialog searchDialog;
                alertDialogBuilder.setView(searchDialogView);

                final TextView history = (TextView) searchDialogView.findViewById(R.id.search_history);
                friendCodeEditText = (EditText) searchDialogView.findViewById(R.id.friend_code_edittext);
                final Button deleteEdittext = (Button) searchDialogView.findViewById(R.id.delete_friend_code);

                //Empty the friend code editext field
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
                        .setMessage(R.string.dialog_body)

                        // SEARCH Button
                        .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitSearch(friendCodeEditText);
                        addToSearchHistory(friendCodeEditText.getText().toString());
                        dialog.dismiss();
                    }
                })
                        // CANCEL Button
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                       // Default search
                        .setNeutralButton("BumChums", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new FriendSearchAsyncTask().execute("");
                                startButton.setClickable(false);
                                startButton.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });

                // Clicking the HISTORY textlabel
                history.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSearchHistoryDialog();
                    }
                });

                // auto show keyboard
                searchDialog = alertDialogBuilder.create();
                searchDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                searchDialog.show();
//                needs to be on 2 lines for some reason
                final Button searchButton = searchDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                searchButton.setEnabled(false);

                //disable search button if textfield isnt full
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

        //Listener on icon at top of view
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

    private void submitSearch(EditText friendCodeEditText) {
        String friendCode = friendCodeEditText.getText().toString();
        new FriendSearchAsyncTask().execute(friendCode);
        startButton.setClickable(false);
        startButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void addToSearchHistory(String search) {
        if(!searchHistoryResultSet.contains(search)) {
            searchHistoryResultSet.add(search);
            saveSearchHistory();
        }
    }

    private void saveSearchHistory() {
        SharedPreferences savedSearches = getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor searchEditor = savedSearches.edit();
        // bug? need to remove shared preference then re-add it
        searchEditor.remove(searchPreferencesKey);
        searchEditor.apply();
        searchEditor.putStringSet(searchPreferencesKey, searchHistoryResultSet);
        searchEditor.commit();
    }


    //TODO use spinner instead. this is a mess. Need to overrride LinearLayout xml probably
    private void showSearchHistoryDialog() {
        View searchHistoryDialogView = getLayoutInflater().inflate(R.layout.search_history_dialog, null);
        final ArrayAdapter<String> searchResultsAdapter = new ArrayAdapter<>(searchHistoryDialogView.getContext(),
                R.layout.search_history_row,
                searchHistoryResultSet.toArray(new String[searchHistoryResultSet.size()]));

        AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(searchHistoryDialogView.getContext());
        searchDialogBuilder.setSingleChoiceItems(searchResultsAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                friendCodeEditText.setText(searchResultsAdapter.getItem(which));
                dialog.dismiss();
            }
        });
        searchDialogBuilder.setView(searchHistoryDialogView);
        AlertDialog searchHistoryDialog = searchDialogBuilder.create();
        //doesnt work
        searchHistoryDialog.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
        searchDialogBuilder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSearchHistory();
    }
}
