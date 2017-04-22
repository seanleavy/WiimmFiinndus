package com.wii.sean.wiimmfiitus.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
import com.wii.sean.wiimmfiitus.Constants.UrlConstants;
import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.activities.MkWiiHomeActivity;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.dialogs.SearchDialog;
import com.wii.sean.wiimmfiitus.friendSearch.SearchAsyncHelper;
import com.wii.sean.wiimmfiitus.helpers.CustomTabsLoader;
import com.wii.sean.wiimmfiitus.helpers.PreferencesManager;
import com.wii.sean.wiimmfiitus.helpers.SnackBarHelper;
import com.wii.sean.wiimmfiitus.interfaces.AsyncTaskCompleteListener;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.List;

public class MiiSearchFragment extends BaseFragment implements MkWiiHomeActivity.PreferenceUpdateListener, AsyncTaskCompleteListener, SearchDialog.DialogListener {

    private OnFragmentInteractionListener mListener;
    private RecyclerView wiiCyclerView;
    private CustomWiiCyclerViewAdapter wiiAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private FloatingActionButton startButton;
    private TextView searchResultsTextview;
    private ItemTouchHelper miiItemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleMiiItemTouchCallback;
    private ProgressBar progressBar;
    private View parentCoordinatorLayout;
    private View miiSearchView;
    private Toolbar toolbar;

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
        searchPreferncesManager = new PreferencesManager(miiSearchView.getContext());
        startButton = (FloatingActionButton) miiSearchView.findViewById(R.id.button_search_frame);
        wiiCyclerView = (RecyclerView) miiSearchView.findViewById(R.id.search_fragment_recycler_view);
        progressBar = (ProgressBar) miiSearchView.findViewById(R.id.progress_bar_search);
        toolbar = (Toolbar) miiSearchView.findViewById(R.id.wiimmfii_toolbar);
        setHasOptionsMenu(true);
        wiiCyclerView.setHasFixedSize(false);
        recyclerLayoutManager = new LinearLayoutManager(miiSearchView.getContext());
        wiiCyclerView.setLayoutManager(recyclerLayoutManager);
        searchResultsTextview = (TextView) miiSearchView.findViewById(R.id.search_result_textview);
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
                                R.drawable.nintendo_logo_black),
                        parentTabView);
                return true;
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo fix hack by passing fragment here maybe
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SearchDialog searchDialog = SearchDialog.newInstance();
                searchDialog.show(fragmentManager, "");
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
                        wiiList.get(viewHolder.getAdapterPosition()));
                SnackBarHelper.showSnackBar(getContext(), miiSearchView,
                        wiiList.get(viewHolder.getAdapterPosition()).getMii()
                                + getResources().getString(R.string.friend_added), Snackbar.LENGTH_SHORT,
                        null, startButton);
                wiiList.remove(viewHolder.getAdapterPosition());
                wiiAdapter.notifyDataSetChanged();
                ((MkWiiHomeActivity)getActivity()).preferenceUpdated();
            }
        };

        simpleMiiItemTouchCallback.getSwipeVelocityThreshold(0f);
        miiItemTouchHelper = new ItemTouchHelper(simpleMiiItemTouchCallback);
        miiItemTouchHelper.attachToRecyclerView(wiiCyclerView);
        // Inflate the layout for this fragment
        return miiSearchView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.menu_load_wiimfii) {
            CustomTabsLoader.loadWebsite(UrlConstants.WiimFiiUrl, getContext());
        }
        if(id == R.id.menu_search_history) {
            showSearchHistoryDialog();
        }
        if(id == R.id.menu_delete) {
            clearRecyclerView();
        }
        return super.onOptionsItemSelected(item);
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
    public void onResume() {
        getActivity().invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        ((MkWiiHomeActivity)getActivity()).removePreferenceUpdateListener(this);
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void submitSearch(String searchToken) {
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
        LayoutInflater layoutInflater = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View searchHistoryDialogView = layoutInflater.inflate(R.layout.search_history_dialog, null);
        final TextView clearHistory = (TextView) searchHistoryDialogView.findViewById(R.id.clear_history_preferences);
        clearHistory.setVisibility(View.INVISIBLE);
        final ArrayAdapter<String> searchResultsAdapter = new ArrayAdapter<>(searchHistoryDialogView.getContext(),
                R.layout.search_history_row, Lists.reverse(searchPreferncesManager.getPreferencesAsList(PreferencesManager.HISTORYPREFERENCES)));
        AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(searchHistoryDialogView.getContext());

        searchDialogBuilder.setSingleChoiceItems(searchResultsAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitSearch(searchResultsAdapter.getItem(which));
                dialog.dismiss();
            }
        });

        searchDialogBuilder.setView(searchHistoryDialogView);
        final AlertDialog searchHistoryDialog = searchDialogBuilder.create();
        if(searchPreferncesManager.getPreferencesFor(PreferencesManager.HISTORYPREFERENCES) != null)
            if(searchPreferncesManager.getPreferencesFor(PreferencesManager.HISTORYPREFERENCES).length > 0)
                clearHistory.setVisibility(View.VISIBLE);

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPreferncesManager.clearPrefencefromDB(PreferencesManager.HISTORYPREFERENCES);
                searchHistoryDialog.dismiss();
                Toast.makeText(getContext(), R.string.history_cleard_toast, Toast.LENGTH_SHORT).show();
            }
        });
        searchHistoryDialog.show();
        //doesnt work
        searchHistoryDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void preferenceUpdate() {

    }

    @Override
    public void onTaskComplete(Object result) {
        wiiList = new ArrayList<>();
        if(((List) result).size() > 0) {
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
            // todo put messaging here
            clearRecyclerView();
            searchResultsTextview.setVisibility(View.VISIBLE);
            searchResultsTextview.setText(R.string.miis_not_found_text);
            searchResultsTextview.postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchResultsTextview.setVisibility(View.INVISIBLE);
                }
            }, 3000);

        }
        startButton.setVisibility(View.VISIBLE);
        startButton.setClickable(true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void valuesReturned(String value, String searchType) {
        submitSearch(value);
        if(searchType.equals(SearchDialog.FRIENDCODESEARCH)) {
            searchPreferncesManager.addToPreference(PreferencesManager.HISTORYPREFERENCES, value);
        }
    }

    private void clearRecyclerView() {
        wiiList.clear();
        wiiAdapter = new CustomWiiCyclerViewAdapter(wiiList);
        wiiCyclerView.setAdapter(wiiAdapter);
    }
}
