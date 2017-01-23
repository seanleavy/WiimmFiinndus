package com.wii.sean.wiimmfiitus.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.activities.MkWiiHomeActivity;
import com.wii.sean.wiimmfiitus.adapters.CustomWiiCyclerViewAdapter;
import com.wii.sean.wiimmfiitus.Constants.FriendCodes;
import com.wii.sean.wiimmfiitus.friendSearch.SearchAsyncHelper;
import com.wii.sean.wiimmfiitus.helpers.PreferencesManager;
import com.wii.sean.wiimmfiitus.helpers.SnackBarHelper;
import com.wii.sean.wiimmfiitus.interfaces.AsyncTaskCompleteListener;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends BaseFragment implements MkWiiHomeActivity.PreferenceUpdateListener, CustomWiiCyclerViewAdapter.Clicklistener, AsyncTaskCompleteListener {

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
    private CustomWiiCyclerViewAdapter.Clicklistener clicklistenerCallback;
    private ImageView onlineStatusImageView;

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

    //todo search for everyone shown
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
        wiiCyclerViewAdapter = new CustomWiiCyclerViewAdapter(miiList, clicklistenerCallback);
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
                for(final MiiCharacter mii : FriendCodes.getDefaultMiis()) {
                    if(!Iterables.any(preferencesManager.getPreferencesAsList(PreferencesManager.FAVOURITESPREFERENCES),
                            new Predicate<MiiCharacter>() {
                                @Override
                                public boolean apply(MiiCharacter input) {
                                    return input.getFriendCode().equals(mii.getFriendCode());
                                }
                            })) {
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
    public void recyclerViewItemClicked(View v, int position) {
        TextView mii = (TextView) v.findViewById(R.id.mii_name_textview);
        TextView friendCode = (TextView) v.findViewById(R.id.friend_code_textview);
        foundMiis = new ArrayList<>();
        wiiCyclerView.setClickable(false);
        onlineStatusImageView = (ImageView) v.findViewById(R.id.online_offline_image);
        searchTask(friendCode);
    }

    private void searchTask(TextView friendCode) {
        SearchAsyncHelper searchAsyncHelper = new SearchAsyncHelper(getContext(), this);
        searchAsyncHelper.execute(friendCode.getText().toString());
    }

    @Override
    public void onTaskComplete(Object result) {
        //pass in search tag here maybe
        if(result != null) {
            if (((List) result).size() > 0) {
//                Toast.makeText(favouritesView.getContext(),
//                        ((List<MiiCharacter>) result).get(0).getMii() + getString(R.string.online),
//                        Toast.LENGTH_SHORT).show();
                SnackBarHelper.showSnackBar(getContext(), favouritesView,
                        ((List<MiiCharacter>) result).get(0).getMii() + getString(R.string.online),
                        Snackbar.LENGTH_LONG, null);
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(400);
                onlineStatusImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.nintendo_network_logo_online));
            }
            else {
                Toast.makeText(favouritesView.getContext(), R.string.offline, Toast.LENGTH_SHORT).show();
                onlineStatusImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.nintendo_network_logo_offline));
//                onlineStatusImageView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
