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

    public CardView licenseCard;
    public static final int DEFAULT_VIEW = 0;
    public static final int COMPACT_VIEW = 1;
    public static final int DEFAULT_VIEW_DETAILED = 2;
    public static final int COMPACT_VIEW_DETAILED = 3;
    private int mViewType = 0;

    private int lastPosition = -1;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class DefaultFriendViewHolder extends ViewHolder {

        private TextView friendCode;
        private NintendoTextview miiName;
        private NintendoTextview vrPoints;
        private ImageView icon;
        private ImageView onlineIcon;
        private LinearLayout lobbyButtonGroup;

        public DefaultFriendViewHolder(View v, int type) {
            super(v);
            context = v.getContext();
            licenseCard = (CardView) v.findViewById(R.id.mii_license_card);
            this.friendCode = (TextView) v.findViewById(R.id.friend_code_textview);
            this.miiName = (NintendoTextview) v.findViewById(R.id.mii_name_textview);
            this.vrPoints = (NintendoTextview) v.findViewById(R.id.vr_textview);
            this.icon = (ImageView)v.findViewById(R.id.mii_icon);
            this.onlineIcon = (ImageView) v.findViewById(R.id.online_offline_image);
            this.lobbyButtonGroup = (LinearLayout) v.findViewById(R.id.lobby_button_group);
        }
    }

    public class CompactFriendHolder extends ViewHolder {

        private NintendoTextview friendCode;
        private NintendoTextview miiName;
        private NintendoTextview vrPoints;
        private NintendoTextview connectionFails;
        private NintendoTextview role;
        private NintendoTextview match;
        private NintendoTextview region;

        public CompactFriendHolder(View v, int type) {
            super(v);
            context = v.getContext();
            licenseCard = (CardView) v.findViewById(R.id.mii_license_card);
            this.friendCode = (NintendoTextview) v.findViewById(R.id.friend_code_textview);
            this.miiName = (NintendoTextview) v.findViewById(R.id.mii_name_textview);
            this.vrPoints = (NintendoTextview) v.findViewById(R.id.vr_textview);
            this.connectionFails = (NintendoTextview) v.findViewById(R.id.connfails);
            this.role = (NintendoTextview) v.findViewById(R.id.role);
            this.match = (NintendoTextview) v.findViewById(R.id.match);
            this.region = (NintendoTextview) v.findViewById(R.id.region);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        ViewHolder fvh = null;
        if(wiiList.get(0).getType() == DEFAULT_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mii_license_card_default, parent, false);
            fvh = new DefaultFriendViewHolder(v, viewType);
        }
        if (wiiList.get(0).getType() == COMPACT_VIEW_DETAILED) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mii_license_card_compact_detailed, parent, false);
            fvh = new CompactFriendHolder(v, viewType);
        }
        return fvh;
    }

    public CustomWiiCyclerViewAdapter(List<MiiCharacter> wiiFriendList) {
        wiiList = wiiFriendList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (wiiList.get(position).getType())
        {
            case MiiCharacter.DEFAULT_VIEW:
                DefaultFriendViewHolder friendViewHolder = (DefaultFriendViewHolder) holder;
                if(friendViewHolder.icon != null)
                    friendViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(friendViewHolder.icon.getContext(), R.drawable.mii_default));
                if (wiiList.get(position).getMii().equals(FriendCodes.PONCHO.getMii())) {
                    ((DefaultFriendViewHolder)holder).icon.setImageDrawable(ContextCompat.getDrawable(((DefaultFriendViewHolder)holder).icon.getContext(), R.drawable.mii_poncho));
                }
                if (wiiList.get(position).getMii().equals(FriendCodes.FARTFACE.getMii())) {
                    friendViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(friendViewHolder.icon.getContext(), R.drawable.mii_fart));
                }
                if (wiiList.get(position).getMii().equals(FriendCodes.DIKROT.getMii())) {
                    friendViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(friendViewHolder.icon.getContext(), R.drawable.mii_dikrot));
                }
                // add lobby listener
                friendViewHolder.onlineIcon.setImageDrawable(ContextCompat.getDrawable(friendViewHolder.onlineIcon.getContext(),
                        wiiList.get(position).isOnline() == true ? R.drawable.online : R.drawable.offline));
                if(wiiList.get(position).isOnline()) {
                    friendViewHolder.lobbyButtonGroup.setVisibility(View.VISIBLE);
                    friendViewHolder.lobbyButtonGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, LobbyActivity.class).putExtra("mii",
                                    new MiiCharacter(wiiList.get(position).getFriendCode(), wiiList.get(position).getMii(), wiiList.get(position).getVr()))
                            );
                        }
                    });
                }

                // Show the Lobby button and add a listener that sends a serialised Miicharacter to the Lobby Activity
                else
                    friendViewHolder.lobbyButtonGroup.setVisibility(View.INVISIBLE);

                friendViewHolder.friendCode.setText(wiiList.get(position).getFriendCode());
                friendViewHolder.vrPoints.setText(wiiList.get(position).getVr());
                friendViewHolder.miiName.setText(wiiList.get(position).getMii());
                friendViewHolder = null;
                break;

            case MiiCharacter.COMPACT_VIEW_DETAILED:
                CompactFriendHolder compactFriendHolder = (CompactFriendHolder) holder;
                compactFriendHolder.friendCode.setText(wiiList.get(position).getFriendCode());
                compactFriendHolder.vrPoints.setText(wiiList.get(position).getVr());
                compactFriendHolder.miiName.setText(wiiList.get(position).getMii());
                compactFriendHolder.region.setText(wiiList.get(position).getRegion());
                compactFriendHolder.connectionFails.setText(context.getResources().getString(R.string.connection_drops_label) + wiiList.get(position).getConnectionFails());
                compactFriendHolder.match.setText(wiiList.get(position).getMatch());
                compactFriendHolder.role.setText(wiiList.get(position).getRole());
                compactFriendHolder = null;
                break;

        }

        setAnimation(((ViewHolder) holder).itemView, position);
    }

    @Override
    public int getItemCount() {
        return wiiList.size();
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
