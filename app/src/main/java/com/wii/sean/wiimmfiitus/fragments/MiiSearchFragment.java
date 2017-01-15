package com.wii.sean.wiimmfiitus.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.activities.MkWiiHomeActivity;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.friendSearch.MkFriendSearch;
import com.wii.sean.wiimmfiitus.helpers.PreferencesManager;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MiiSearchFragment extends Fragment implements MkWiiHomeActivity.PreferenceUpdateListener {

    private OnFragmentInteractionListener mListener;
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
    private Snackbar snackbar;
    private EditText friendCodeEditText;
    private View parentCoordinatorLayout;
    private View miiSearchView;

    private MkFriendSearch mkFriendSearch;
    private int friendsFound = 0;
    private String searchTag = null;
    private PreferencesManager searchPreferncesManager;
    private Set<String> searchHistoryResultSet;

    private List<MiiCharacter> wiiList = new ArrayList<>();

    public MiiSearchFragment() {
        // Required empty public constructor
    }

    public static MiiSearchFragment newInstance() {
        return new MiiSearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        miiSearchView = inflater.inflate(R.layout.fragment_mii_search, container, false);
        mkFriendSearch = new MkFriendSearch();

        parentCoordinatorLayout = miiSearchView.findViewById(R.id.search_fragment_main_layout);
        showSnackBar(getResources().getString(R.string.first_run_message), Snackbar.LENGTH_LONG, null);

        // To save every fCode search
        final LayoutInflater layoutInflater = getLayoutInflater(savedInstanceState);
        searchPreferncesManager = new PreferencesManager(miiSearchView.getContext());
        searchHistoryResultSet = searchPreferncesManager.getPreferencesFor(PreferencesManager.HISTORYPREFERENCES);

        startButton = (FloatingActionButton) miiSearchView.findViewById(R.id.button_search_frame);
        wiiCyclerView = (RecyclerView) miiSearchView.findViewById(R.id.search_fragment_recycler_view);
        wiimfiiIcon = (ImageView) miiSearchView.findViewById(R.id.wiimfii_icon);
        miisFoundTextViewLabel = (TextView) miiSearchView.findViewById(R.id.miis_found_label);
        miisFoundTextViewValue = (TextView) miiSearchView.findViewById(R.id.miis_found_value);
        progressBar = (ProgressBar) miiSearchView.findViewById(R.id.progress_bar_search);

        wiiCyclerView.setHasFixedSize(false);
        recyclerLayoutManager = new LinearLayoutManager(miiSearchView.getContext());
        wiiCyclerView.setLayoutManager(recyclerLayoutManager);

        startButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MiiSearchFragment.FriendSearchAsyncTask().execute("");
                startButton.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                showSnackBar("", Snackbar.LENGTH_SHORT, ContextCompat.getDrawable(v.getContext(), R.drawable.nintendo_logo_red_light));
                return true;

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.homescreen_main_layout);
                View searchDialogView = layoutInflater.inflate(R.layout.friend_code_dialog, viewGroup, false);
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
                                searchPreferncesManager.addToPreference(PreferencesManager.HISTORYPREFERENCES, friendCodeEditText.getText().toString() );
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
                                new MiiSearchFragment.FriendSearchAsyncTask().execute("");
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
                if(searchDialog.getWindow() != null)
                searchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
                searchPreferncesManager.addToPreference(PreferencesManager.FAVOURITESPREFERENCES,
                        wiiList.get(viewHolder.getAdapterPosition()).toGson());
                wiiList.remove(viewHolder.getAdapterPosition());
                friendsFound --;
                ((MkWiiHomeActivity)getActivity()).prefernceUpdated();
                miisFoundTextViewValue.setText(String.valueOf(friendsFound));
            }
        };

        simpleMiiItemTouchCallback.getSwipeVelocityThreshold(0f);
        miiItemTouchHelper = new ItemTouchHelper(simpleMiiItemTouchCallback);
        miiItemTouchHelper.attachToRecyclerView(wiiCyclerView);
        // Inflate the layout for this fragment
        return miiSearchView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        ((MkWiiHomeActivity) getActivity()).registerPreferenceUPdateListener(this);
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        ((MkWiiHomeActivity)getActivity()).removePreferenceUpdateListener(this);
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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

    //TODO use spinner instead. this is a mess. Need to overrride LinearLayout xml probably
    private void showSearchHistoryDialog() {
        View searchHistoryDialogView = getLayoutInflater(null).inflate(R.layout.search_history_dialog, null);
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
        searchHistoryDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        searchDialogBuilder.show();
    }

    private void showSnackBar(String resource, int length, Drawable d) {
        snackbar = Snackbar.make(parentCoordinatorLayout, resource, length);
        snackbar.setActionTextColor(getResources().getColor(android.R.color.holo_red_dark));
        snackbar.show();
        View snackBarView = snackbar.getView();
        if(d != null) {
            snackBarView.setBackground(d);
        }
        TextView snackText = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        snackText.setGravity(Gravity.CENTER_HORIZONTAL);
//        snackText.setTextAlignment(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public void preferenceUpdate() {

    }
}