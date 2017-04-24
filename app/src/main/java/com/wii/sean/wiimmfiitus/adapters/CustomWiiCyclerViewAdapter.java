package com.wii.sean.wiimmfiitus.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.Constants.FriendCodes;
import com.wii.sean.wiimmfiitus.activities.LobbyActivity;
import com.wii.sean.wiimmfiitus.customViews.NintendoTextview;
import com.wii.sean.wiimmfiitus.interfaces.AsyncTaskCompleteListener;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.List;

public class CustomWiiCyclerViewAdapter extends RecyclerView.Adapter<CustomWiiCyclerViewAdapter.ViewHolder> implements AsyncTaskCompleteListener {

    public List<MiiCharacter> wiiList = new ArrayList<>();
    public TextView friendCode;
    public TextView miiName;
    public TextView vrPoints;
    public CardView licenseCard;
    public static final int DEFAULT_VIEW = 0;
    public static final int COMPACT_VIEW = 1;
    public static final int DEFAULT_VIEW_DETAILED = 2;
    public static final int COMPACT_VIEW_DETAILED = 3;
    private int mViewType = 0;

    public ViewHolder friendCardViewHolder;
    private int lastPosition = -1;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class DefaultFriendViewHolder extends ViewHolder {

        public ImageView icon;
        public ImageView onlineIcon;
        public LinearLayout lobbyButtonGroup;

        public DefaultFriendViewHolder(View v, int type) {
            super(v);
            context = v.getContext();
            licenseCard = (CardView) v.findViewById(R.id.mii_license_card);
            friendCode = (TextView)v.findViewById(R.id.friend_code_textview);
            miiName = (TextView)v.findViewById(R.id.mii_name_textview);
            vrPoints = (TextView)v.findViewById(R.id.vr_textview);
            this.icon = (ImageView)v.findViewById(R.id.mii_icon);
            this.onlineIcon = (ImageView) v.findViewById(R.id.online_offline_image);
            this.lobbyButtonGroup = (LinearLayout) v.findViewById(R.id.lobby_button_group);
        }
    }

    public class CompactFriendHolder extends ViewHolder {

        private NintendoTextview connectionFails;
        private NintendoTextview role;
        private NintendoTextview match;
        private NintendoTextview region;

        public CompactFriendHolder(View v, int type) {
            super(v);
            context = v.getContext();
            licenseCard = (CardView) v.findViewById(R.id.mii_license_card);
            friendCode = (TextView)v.findViewById(R.id.friend_code_textview);
            miiName = (TextView)v.findViewById(R.id.mii_name_textview);
            vrPoints = (TextView)v.findViewById(R.id.vr_textview);
            connectionFails = (NintendoTextview) v.findViewById(R.id.connfails);
            role = (NintendoTextview) v.findViewById(R.id.role);
            match = (NintendoTextview) v.findViewById(R.id.match);
            region = (NintendoTextview) v.findViewById(R.id.region);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        ViewHolder fvh = null;
        if(viewType == DEFAULT_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mii_license_card_default, parent, false);
            fvh = new DefaultFriendViewHolder(v, viewType);
        }
        if (viewType == COMPACT_VIEW_DETAILED) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mii_license_card_compact_detailed, parent, false);
            fvh = new CompactFriendHolder(v, viewType);
        }
        return fvh;
    }

    public CustomWiiCyclerViewAdapter(List<MiiCharacter> wiiFriendList) {
        wiiList = wiiFriendList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(getItemViewType(position) == DEFAULT_VIEW || getItemViewType(position) == DEFAULT_VIEW_DETAILED) {
            if(((DefaultFriendViewHolder)holder).icon != null)
                ((DefaultFriendViewHolder)holder).icon.setImageDrawable(ContextCompat.getDrawable(((DefaultFriendViewHolder)holder).icon.getContext(), R.drawable.mii_default));
            for (MiiCharacter m : wiiList) {
                if (wiiList.get(position).getMii().equals(FriendCodes.PONCHO.getMii())) {
                    ((DefaultFriendViewHolder)holder).icon.setImageDrawable(ContextCompat.getDrawable(((DefaultFriendViewHolder)holder).icon.getContext(), R.drawable.mii_poncho));
                }
                if (wiiList.get(position).getMii().equals(FriendCodes.FARTFACE.getMii())) {
                    ((DefaultFriendViewHolder)holder).icon.setImageDrawable(ContextCompat.getDrawable(((DefaultFriendViewHolder)holder).icon.getContext(), R.drawable.mii_fart));
                }
                if (wiiList.get(position).getMii().equals(FriendCodes.DIKROT.getMii())) {
                    ((DefaultFriendViewHolder)holder).icon.setImageDrawable(ContextCompat.getDrawable(((DefaultFriendViewHolder)holder).icon.getContext(), R.drawable.mii_dikrot));
                }
            }
            // add lobby listener
            if(((DefaultFriendViewHolder)holder).onlineIcon != null) {
                ((DefaultFriendViewHolder)holder).onlineIcon.setImageDrawable(ContextCompat.getDrawable(((DefaultFriendViewHolder)holder).onlineIcon.getContext(),
                        wiiList.get(position).isOnline() == true ? R.drawable.online : R.drawable.offline));
                if(wiiList.get(position).isOnline()) {
                    ((DefaultFriendViewHolder)holder).lobbyButtonGroup.setVisibility(View.VISIBLE);
                    ((DefaultFriendViewHolder)holder).lobbyButtonGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, LobbyActivity.class).putExtra("mii",
                                    new MiiCharacter(friendCode.getText().toString(), miiName.getText().toString(), vrPoints.getText().toString()))
                            );
                        }
                    });
                }
            }

            // Show the Lobby button and add a listener that sends a serialised Miicharacter to the Lobby Activity
            else
                ((DefaultFriendViewHolder)holder).lobbyButtonGroup.setVisibility(View.INVISIBLE);
        }
        if(getItemViewType(position) == COMPACT_VIEW_DETAILED) {
            ((CompactFriendHolder)holder).region.setText(wiiList.get(position).getRegion());
            ((CompactFriendHolder)holder).connectionFails.setText(context.getResources().getString(R.string.connection_drops_label) + wiiList.get(position).getConnectionFails());
            ((CompactFriendHolder)holder).match.setText(wiiList.get(position).getMatch());
            ((CompactFriendHolder)holder).role.setText(wiiList.get(position).getRole());
        }
        this.friendCode.setText(wiiList.get(position).getFriendCode());
        this.vrPoints.setText(String.valueOf(wiiList.get(position).getVr()));
        this.miiName.setText(wiiList.get(position).getMii());
        setAnimation(((ViewHolder) holder).itemView, position);
    }

    @Override
    public int getItemCount() {
        return wiiList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(wiiList.get(0).getType() == MiiCharacter.DEFAULT_VIEW) {
            return DEFAULT_VIEW;
        }
        if(wiiList.get(0).getType() == MiiCharacter.COMPACT_VIEW_DETAILED) {
            return COMPACT_VIEW_DETAILED;
        }
        return 0;
    }

    private void setAnimation(View viewToAnimate, int pos) {
        if(pos > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = pos;
        }
    }

    @Override
    public void onTaskComplete(Object result) {
    }
}
