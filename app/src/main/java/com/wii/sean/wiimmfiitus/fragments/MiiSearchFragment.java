package com.wii.sean.wiimmfiitus.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
import com.wii.sean.wiimmfiitus.Constants.UrlConstants;
import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.activities.MkWiiHomeActivity;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.friendSearch.SearchAsyncHelper;
import com.wii.sean.wiimmfiitus.helpers.PreferencesManager;
import com.wii.sean.wiimmfiitus.helpers.SnackBarHelper;
import com.wii.sean.wiimmfiitus.interfaces.AsyncTaskCompleteListener;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.List;

public class MiiSearchFragment extends Fragment implements MkWiiHomeActivity.PreferenceUpdateListener, AsyncTaskCompleteListener {

    private OnFragmentInteractionListener mListener;
    private RecyclerView wiiCyclerView;
    private CustomWiiCyclerViewAdapter wiiAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private FloatingActionButton startButton;
    private ImageView wiimfiiIcon;
    private TextView miisFoundTextViewLabel;
    private ItemTouchHelper miiItemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleMiiItemTouchCallback;
    private ProgressBar progressBar;
    private AlertDialog.Builder alertDialogBuilder;
    private EditText friendCodeEditText;
    private View parentCoordinatorLayout;
    private View miiSearchView;

    private PreferencesManager searchPreferncesManager;

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

        parentCoordinatorLayout = miiSearchView.findViewById(R.id.search_fragment_main_layout);

        // To save every fCode search
        final LayoutInflater layoutInflater = getLayoutInflater(savedInstanceState);
        searchPreferncesManager = new PreferencesManager(miiSearchView.getContext());
        startButton = (FloatingActionButton) miiSearchView.findViewById(R.id.button_search_frame);
        wiiCyclerView = (RecyclerView) miiSearchView.findViewById(R.id.search_fragment_recycler_view);
        wiimfiiIcon = (ImageView) miiSearchView.findViewById(R.id.wiimfii_icon);
        miisFoundTextViewLabel = (TextView) miiSearchView.findViewById(R.id.miis_found_label);
        progressBar = (ProgressBar) miiSearchView.findViewById(R.id.progress_bar_search);

        wiiCyclerView.setHasFixedSize(false);
        recyclerLayoutManager = new LinearLayoutManager(miiSearchView.getContext());
        wiiCyclerView.setLayoutManager(recyclerLayoutManager);
        final View parentTabView = getActivity().findViewById(R.id.tab_dots);
        if(searchPreferncesManager.isFirstRun()) {
            SnackBarHelper.showSnackBar(getContext(), parentCoordinatorLayout,
                    getResources().getString(R.string.first_run_message),
                    Snackbar.LENGTH_LONG,
                    null,
                    parentTabView);
                searchPreferncesManager.setFirstRunToBe(true);
        }


        startButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SearchAsyncHelper searchAsyncHelper = new SearchAsyncHelper(v.getContext(),
                        MiiSearchFragment.this);
                searchAsyncHelper.execute("");
                startButton.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                SnackBarHelper.showSnackBar(v.getContext(),
                        parentCoordinatorLayout,"",
                        Snackbar.LENGTH_SHORT,
                        ContextCompat.getDrawable(v.getContext(),
                                R.drawable.nintendo_logo_red_light),
                        parentTabView);
                return true;

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchDialog(view, layoutInflater);
            }
        });

        //Listener on icon at top of view
        wiimfiiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.pressed_wiimfii, Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse(UrlConstants.WiimFiiUrl);
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.nintendo_red_dark));
                intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getContext(), R.color.nintendo_red));
                CustomTabsIntent customTabsIntent = intentBuilder.build();
                customTabsIntent.launchUrl(getContext(), uri);
            }
        });

        //todo refactor into its own class
        simpleMiiItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                searchPreferncesManager.addToPreference(PreferencesManager.FAVOURITESPREFERENCES,
                        wiiList.get(viewHolder.getAdapterPosition()).toGson());
                SnackBarHelper.showSnackBar(getContext(), miiSearchView,
                        wiiList.get(viewHolder.getAdapterPosition()).getMii()
                                + getResources().getString(R.string.friend_added), Snackbar.LENGTH_SHORT,
                        null, startButton);
                wiiList.remove(viewHolder.getAdapterPosition());
                miisFoundTextViewLabel.setVisibility(View.INVISIBLE);
                ((MkWiiHomeActivity)getActivity()).preferenceUpdated();
            }
        };

        simpleMiiItemTouchCallback.getSwipeVelocityThreshold(0f);
        miiItemTouchHelper = new ItemTouchHelper(simpleMiiItemTouchCallback);
        miiItemTouchHelper.attachToRecyclerView(wiiCyclerView);
        // Inflate the layout for this fragment
        return miiSearchView;
    }

    // Search Dialog
    // todo create a dialog class instead
    private void showSearchDialog(View view, LayoutInflater layoutInflater) {
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.homescreen_main_layout);
        // building the Search Dialog
        //
        View searchDialogView = layoutInflater.inflate(R.layout.friend_code_dialog, viewGroup, false);
        //todo refactor all custom Dialogs to seperate class
        alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        AlertDialog searchDialog;
        alertDialogBuilder.setView(searchDialogView);

        final TextView dialogSubtitle = (TextView) searchDialogView.findViewById(R.id.dialog_title);
        final TextView history = (TextView) searchDialogView.findViewById(R.id.search_history);
        friendCodeEditText = (EditText) searchDialogView.findViewById(R.id.friend_code_edittext);
        final Button deleteEdittext = (Button) searchDialogView.findViewById(R.id.delete_friend_code);

        final Switch toggleSwitch = (Switch) searchDialogView.findViewById(R.id.alertdialog_toggle_switch);
        toggleSwitch.setTextOff(getString(R.string.fcode));
        toggleSwitch.setTextOn(getString(R.string.mii));
        toggleSwitch.setChecked(false);

        alertDialogBuilder.setTitle(R.string.dialog_title)
                // SEARCH Button
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitSearch(friendCodeEditText);
                        if(!toggleSwitch.isChecked())
                        searchPreferncesManager.addToPreference(PreferencesManager.HISTORYPREFERENCES,
                                friendCodeEditText.getText().toString() );
                        dialog.dismiss();
                    }
                })
                // CANCEL Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

        //Empty the friend code editext field
        deleteEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendCodeEditText.setText("");
            }
        });

        if(searchDialog.getWindow() != null)
        searchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        searchDialog.show();

        //needs to be on 2 lines for some reason
        final Button searchButton = searchDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        searchButton.setEnabled(false);

        //format friendcode textfield with this Textwatcher
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // for friendcode entries
                if(!toggleSwitch.isChecked()) {
                    // allows credit card style formatting
                    if(s.length() == 4 || s.length() == 9) {
                        s.append('-');
                    }
                    if (friendCodeEditText.getText().toString().length() >= 14) {
                        searchButton.setEnabled(true);
                    } else
                        searchButton.setEnabled(false);
                }
                // for mii text entry
                if(toggleSwitch.isChecked()) {
                    if (friendCodeEditText.getText().toString().length() >= 1) {
                        searchButton.setEnabled(true);
                    } else
                        searchButton.setEnabled(false);
                }
            }
        };

        friendCodeEditText.addTextChangedListener(textWatcher);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if(isChecked) {
                    //change search title and keyboard type
                    dialogSubtitle.setText(R.string.dialog_body_mii);
                    friendCodeEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    friendCodeEditText.getText().clear();
                }
                else {
                    dialogSubtitle.setText(R.string.dialog_body_fcode);
                    friendCodeEditText.getText().clear();
                    friendCodeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                }
            }
        });
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

    private void submitSearch(EditText friendCodeEditText) {
        String searchToken = friendCodeEditText.getText().toString();
        searchTask(searchToken);
        startButton.setClickable(false);
        startButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void searchTask(String searchToken) {
        SearchAsyncHelper searchAsyncHelper = new SearchAsyncHelper(getContext(), this);
        searchAsyncHelper.execute(searchToken);
    }

    //TODO use spinner instead. this is a mess.
    private void showSearchHistoryDialog() {
        View searchHistoryDialogView = getLayoutInflater(null).inflate(R.layout.search_history_dialog, null);
        final TextView clearHistory = (TextView) searchHistoryDialogView.findViewById(R.id.clear_history_preferences);
        // todo why is this a miicharacter and not a string
        List<MiiCharacter> searchHistoryResultSet = searchPreferncesManager.getPreferencesAsList(PreferencesManager.HISTORYPREFERENCES);
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
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPreferncesManager.clearPrefencefromDB(PreferencesManager.HISTORYPREFERENCES);
            }
        });
        searchDialogBuilder.setView(searchHistoryDialogView);
        AlertDialog searchHistoryDialog = searchDialogBuilder.create();
        //doesnt work
        searchHistoryDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        searchDialogBuilder.show();
    }

    @Override
    public void preferenceUpdate() {

    }

    @Override
    public void onTaskComplete(Object result) {
        wiiList = new ArrayList<>();
        if(((List) result).size() > 0) {
            miisFoundTextViewLabel.setText(R.string.miis_found_text);
            miisFoundTextViewLabel.setVisibility(View.VISIBLE);
            wiiList.addAll((List) result);
            wiiAdapter = new CustomWiiCyclerViewAdapter(wiiList);
            wiiCyclerView.setAdapter(wiiAdapter);
            RecyclerItemClickSupport.addTo(wiiCyclerView).setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    Log.d("TESR", "TESTTTTTTTT");
                    Toast.makeText(getContext(),
                            "HELLO it's a me, " +
                                    ((CustomWiiCyclerViewAdapter.FriendViewHolder) recyclerView.getChildViewHolder(v)).miiName.getText(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {
            miisFoundTextViewLabel.setVisibility(View.VISIBLE);
            miisFoundTextViewLabel.setText(R.string.miis_not_found_text);
        }
        startButton.setVisibility(View.VISIBLE);
        startButton.setClickable(true);
        progressBar.setVisibility(View.GONE);
    }
}
