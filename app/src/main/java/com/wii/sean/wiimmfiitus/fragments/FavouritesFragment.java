package com.wii.sean.wiimmfiitus.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.activities.MkWiiHomeActivity;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.Constants.FriendCodes;
import com.wii.sean.wiimmfiitus.friendSearch.MkFriendSearch;
import com.wii.sean.wiimmfiitus.helpers.PreferencesManager;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends BaseFragment implements MkWiiHomeActivity.PreferenceUpdateListener, CustomWiiCyclerViewAdapter.Clicklistener {
    private OnFragmentInteractionListener mListener;
    private View favouritesView;
    private CustomWiiCyclerViewAdapter wiiCyclerViewAdapter;
    private RecyclerView wiiCyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PreferencesManager preferencesManager;
    private ItemTouchHelper miiItemTouchHelper;
    private ItemTouchHelper.Callback simpleMiiItemTouchCallback;
    private List<MiiCharacter> miiList;
    private List<MiiCharacter> foundMiis;
    private Button defaultFriendsImageButton;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        wiiCyclerView.invalidate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        favouritesView = inflater.inflate(R.layout.fragment_favourites, container, false);
        toolbar = (Toolbar) favouritesView.findViewById(R.id.favourites_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        wiiCyclerView = (RecyclerView) favouritesView.findViewById(R.id.favourites_fragment_recycler_view);
        preferencesManager = new PreferencesManager(favouritesView.getContext());
        setAdapter();
        defaultFriendsImageButton = (Button) favouritesView.findViewById(R.id.default_friends);
        swipeRefreshLayout = (SwipeRefreshLayout) favouritesView.findViewById(R.id.swipe_refresh_layout);

        setOnBoardingAnimation();
        setDefaultFriends();
        swipeRemoveMiiFromFavourites();
        setRefreshListener();

        return favouritesView;
    }

    private void setRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                wiiCyclerViewAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void swipeRemoveMiiFromFavourites() {
        simpleMiiItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                preferencesManager.removeFromPreference(PreferencesManager.FAVOURITESPREFERENCES,
                        miiList.get(viewHolder.getAdapterPosition()).toGson());
                miiList.remove(viewHolder.getAdapterPosition());
                wiiCyclerViewAdapter.notifyDataSetChanged();
                if(!miiList.containsAll(FriendCodes.getDefaultMiis())) {
                    defaultFriendsImageButton.setVisibility(View.VISIBLE);
                }
            }
        };
        simpleMiiItemTouchCallback.getSwipeVelocityThreshold(0f);
        miiItemTouchHelper = new ItemTouchHelper(simpleMiiItemTouchCallback);
        miiItemTouchHelper.attachToRecyclerView(wiiCyclerView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.wii_base, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setOnBoardingAnimation() {
        if(preferencesManager.isFirstRun()) {

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

    @Override
    public void preferenceUpdate() {
        setAdapter();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setAdapter() {
        miiList = new ArrayList<>(preferencesManager.getPreferencesAsList(PreferencesManager.FAVOURITESPREFERENCES));
        wiiCyclerViewAdapter = new CustomWiiCyclerViewAdapter(miiList);
        layoutManager = new LinearLayoutManager(favouritesView.getContext());
        wiiCyclerView.setLayoutManager(layoutManager);
        wiiCyclerView.setHasFixedSize(false);
        wiiCyclerView.setAdapter(wiiCyclerViewAdapter);
        wiiCyclerViewAdapter.notifyDataSetChanged();
        wiiCyclerViewAdapter.setClickListener(this);
        favouritesView.invalidate();
    }

    private void setDefaultFriends() {
        defaultFriendsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!miiList.contains(FriendCodes.PONCHO)) {
//                    miiList.add(FriendCodes.PONCHO);
//                    preferencesManager.addToPreference(PreferencesManager.FAVOURITESPREFERENCES,
//                            FriendCodes.PONCHO.toGson());
//                }
//                if(!miiList.contains(FriendCodes.FARTFACE)) {
//                    miiList.add(FriendCodes.FARTFACE);
//                    preferencesManager.addToPreference(PreferencesManager.FAVOURITESPREFERENCES,
//                            FriendCodes.FARTFACE.toGson());
//                }
//                if(!miiList.contains(FriendCodes.DIKROT)) {
//                    miiList.add(FriendCodes.DIKROT);
//                    preferencesManager.addToPreference(PreferencesManager.FAVOURITESPREFERENCES,
//                            FriendCodes.DIKROT.toGson());
//                }
//                if(!miiList.contains(FriendCodes.SEAN)) {
//                    miiList.add(FriendCodes.SEAN);
//                    preferencesManager.addToPreference(PreferencesManager.FAVOURITESPREFERENCES,
//                            FriendCodes.SEAN.toGson());
//                }
                for(MiiCharacter mii : FriendCodes.getDefaultMiis()) {
                    if(!miiList.contains(mii)) {
                        miiList.add(mii);
                        preferencesManager.addToPreference(PreferencesManager.FAVOURITESPREFERENCES, mii.toGson());
                    }
                }
                wiiCyclerViewAdapter.notifyDataSetChanged();
                defaultFriendsImageButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void itemLongClicked(View v, int position) {
        TextView mii = (TextView) v.findViewById(R.id.mii_name_textview);
        TextView friendCode = (TextView) v.findViewById(R.id.friend_code_textview);
        foundMiis = new ArrayList<>();
        wiiCyclerView.setClickable(false);
//        new FriendSearchAsyncTask().execute(friendCode.getText().toString(), mii.toString());
    }

    private class FriendSearchAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            foundMiis = new MkFriendSearch().searchFriendList(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(), foundMiis.toString(), Toast.LENGTH_SHORT).show();
            // set an online label to card
        }

    }
}
