package com.wii.sean.wiimmfiitus.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
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
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FavouritesFragment extends BaseFragment implements MkWiiHomeActivity.PreferenceUpdateListener, AsyncTaskCompleteListener {

    private OnFragmentInteractionListener mListener;
    private android.view.View favouritesView;
    private CustomWiiCyclerViewAdapter wiiCyclerViewAdapter;
    private RecyclerView wiiCyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PreferencesManager preferencesManager;
    private ItemTouchHelper miiItemTouchHelper;
    private ItemTouchHelper.Callback simpleMiiItemTouchCallback;
    private List<MiiCharacter> miiList;
    private ImageView defaultFriendsImageButton;
    private Toolbar toolbar;
    private TextView refreshButtonTextViewTerribleCode;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView onlineStatusImageView;
    private CustomWiiCyclerViewAdapter.FriendViewHolder friendViewHolder;
    private boolean isGroupSearch = false;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
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
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        favouritesView = inflater.inflate(R.layout.fragment_favourites, container, false);
        toolbar = (Toolbar) favouritesView.findViewById(R.id.favourites_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        wiiCyclerView = (RecyclerView) favouritesView.findViewById(R.id.favourites_fragment_recycler_view);
        preferencesManager = new PreferencesManager(favouritesView.getContext());
        setAdapter();
        defaultFriendsImageButton = (ImageView) favouritesView.findViewById(R.id.mario_favourites_tolbar_icon);
        refreshButtonTextViewTerribleCode = (TextView) favouritesView.findViewById(R.id.refresh_favourites);
        swipeRefreshLayout = (SwipeRefreshLayout) favouritesView.findViewById(R.id.swipe_refresh_layout);

        setOnBoardingAnimation();
        setDefaultFriends();
        enableSwipeDragFavourites();
        setRefreshListener();
        return favouritesView;
    }

    private void setRefreshListener() {
        swipeRefreshLayout.setDistanceToTriggerSync(280);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAll();
            }
        });
        refreshButtonTextViewTerribleCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAll();
            }
        });
    }

    private void refreshAll() {
        preferencesManager = new PreferencesManager(favouritesView.getContext());
        final List<MiiCharacter> favouritesFriendCodesList = preferencesManager.getPreferencesAsList(PreferencesManager.FAVOURITESPREFERENCES);
        View parent = favouritesView.findViewById(R.id.parent_linearlayout);
        SnackBarHelper.showSnackBar(getContext(),
                parent, "",
                Snackbar.LENGTH_SHORT,
                ContextCompat.getDrawable(getContext(), R.drawable.nintendo_logo_red_light));
        swipeRefreshLayout.setRefreshing(false);
        isGroupSearch = true;
        searchTask(favouritesFriendCodesList);
    }

    //implement itemtouch helper in adapter
    private void enableSwipeDragFavourites() {
        //todo this is a mess
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int swipes = ItemTouchHelper.LEFT;
                int drags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                return makeMovementFlags(drags, swipes);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                if(viewHolder.getAdapterPosition() < target.getAdapterPosition()) {
                    for(int i = viewHolder.getAdapterPosition(); i < target.getAdapterPosition(); i++) {
                        Collections.swap(miiList, i, i + 1);
                    }
                } else {
                    for(int i = viewHolder.getAdapterPosition(); i > target.getAdapterPosition(); i--) {
                        Collections.swap(miiList, i, i - 1);
                    }
                }
                preferencesManager.overwritePreferenceWith(miiList, PreferencesManager.FAVOURITESPREFERENCES);
                preferencesManager = new PreferencesManager(favouritesView.getContext());
                wiiCyclerViewAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                preferencesManager.removeFromPreference(PreferencesManager.FAVOURITESPREFERENCES,
                        miiList.get(viewHolder.getAdapterPosition()));
                miiList.remove(viewHolder.getAdapterPosition());
                wiiCyclerViewAdapter.notifyDataSetChanged();
            }
        };
        miiItemTouchHelper = new ItemTouchHelper(callback);
        miiItemTouchHelper.attachToRecyclerView(wiiCyclerView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.wii_base, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setOnBoardingAnimation() {
        if(preferencesManager.isFirstRun()) {
            // todo ?!
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
        if(favouritesView != null) {
            setAdapter();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setAdapter() {
        if(preferencesManager.getPreferencesAsList(PreferencesManager.FAVOURITESPREFERENCES) != null)
            miiList = new ArrayList<>(preferencesManager.getPreferencesAsList(PreferencesManager.FAVOURITESPREFERENCES));
        else
            miiList = new ArrayList<>();
        wiiCyclerViewAdapter = new CustomWiiCyclerViewAdapter(miiList);
        layoutManager = new LinearLayoutManager(favouritesView.getContext());
        wiiCyclerView.setAdapter(wiiCyclerViewAdapter);
        RecyclerItemClickSupport.addTo(wiiCyclerView).setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.e("TAAAAAAG", "YAAAAAAAAAAY");
                friendViewHolder = (CustomWiiCyclerViewAdapter.FriendViewHolder) recyclerView.getChildViewHolder(v);
                searchTask(((CustomWiiCyclerViewAdapter.FriendViewHolder)recyclerView.getChildViewHolder(v)).friendCode.getText().toString());
            }
        });
        wiiCyclerView.setLayoutManager(layoutManager);
        wiiCyclerView.setHasFixedSize(false);
        wiiCyclerViewAdapter.notifyDataSetChanged();
        favouritesView.invalidate(); // Is this necessary?
    }

    private void setDefaultFriends() {
        defaultFriendsImageButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                for(final MiiCharacter mii : FriendCodes.getDefaultMiis()) {
                    if(!Iterables.any(preferencesManager.getPreferencesAsList(PreferencesManager.FAVOURITESPREFERENCES),
                            new Predicate<MiiCharacter>() {
                                @Override
                                public boolean apply(MiiCharacter input) {
                                    return input.getFriendCode().equals(mii.getFriendCode());
                                }
                            })) {
                        miiList.add(mii);
                        preferencesManager.addToPreference(PreferencesManager.FAVOURITESPREFERENCES, mii);
                    }
                }
                wiiCyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void searchTask(Object args) {
        SearchAsyncHelper searchAsyncHelper = new SearchAsyncHelper(getContext(), this);
        searchAsyncHelper.execute(args);
    }

    @Override
    public void onTaskComplete(Object result) {
        if(result != null) {
            processResult(result);
        }
    }

    private void processResult(Object result) {
        int i;
        boolean found = false;
        for(i = 0; i < wiiCyclerViewAdapter.wiiList.size(); i++) {
            for (MiiCharacter mii : (List<MiiCharacter>) result) {
                if (mii.getFriendCode().equals(wiiCyclerViewAdapter.wiiList.get(i).getFriendCode())) {
                    found = true;
                    wiiCyclerViewAdapter.wiiList.set(i, mii);
                    break;
                }
            }
        }
        if(found) {
            int[] sounds = {R.raw.coin, R.raw.nsmbwiicoin, R.raw.smw_coin};
            Random r = new Random();
            int Low = 0;
            int High = 2;
            int rndm = r.nextInt(High-Low) + Low;
            MediaPlayer mp1 = MediaPlayer.create(getContext(),sounds[rndm]);
            mp1.start();
        }
        if(isGroupSearch) {
            String totalResult = String.valueOf(((List) result).size());
            Toast.makeText(getContext(), totalResult + getString(R.string.group_search), Toast.LENGTH_SHORT).show();
            isGroupSearch = false;
        }
        else
            Toast.makeText(favouritesView.getContext(),
                    friendViewHolder.miiName.getText() +
                            ( ((List<MiiCharacter>)result).size() > 0 ? getResources().getString(R.string.online) : getResources().getString(R.string.offline)),
                    Toast.LENGTH_SHORT).show();
        wiiCyclerViewAdapter.notifyDataSetChanged();
    }
}
