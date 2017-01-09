package com.wii.sean.wiimmfiitus.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.friendSearch.Constants.FriendCodes;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomWiiCyclerViewAdapter extends RecyclerView.Adapter<CustomWiiCyclerViewAdapter.ViewHolder> {

    private List<MiiCharacter> wiiList = new ArrayList<>();

    public static final int SEARCHED_STATE = 1;
    public static final int DEFAULT_STATE = 0;
    private int lastPosition = -1;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class DefaultViewHolder extends ViewHolder {
        public DefaultViewHolder(View v) {
            super(v);
        }
    }

    public class FriendViewHolder extends ViewHolder {

        private TextView friendCode;
        private TextView miiName;
        private TextView vrPoints;
        private ImageView icon;

        public FriendViewHolder(View v) {
            super(v);
            context = v.getContext();
            this.friendCode = (TextView)v.findViewById(R.id.friend_code_textview);
            this.miiName = (TextView)v.findViewById(R.id.mii_name_textview);
            this.vrPoints = (TextView)v.findViewById(R.id.vr_textview);
            this.icon = (ImageView)v.findViewById(R.id.mii_icon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        FriendViewHolder fvh = null;
        if(viewType == SEARCHED_STATE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mii_license_card_view, parent, false);
            fvh = new FriendViewHolder(v);
        } else if (viewType == DEFAULT_STATE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.first_card, parent, false);
            fvh = new FriendViewHolder(v);
        }
        return fvh;
    }

    public CustomWiiCyclerViewAdapter(List<MiiCharacter> wiiFriendList) {
        wiiList = wiiFriendList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FriendViewHolder friendCard = (FriendViewHolder) holder;
        for(MiiCharacter m : wiiList) {
            if(m.getMii().equals(FriendCodes.PONCHO)) {
                friendCard.icon.setImageDrawable(ContextCompat.getDrawable(friendCard.icon.getContext(), R.drawable.mii_poncho));
            }
            if(m.getMii().equals(FriendCodes.FARTFACE)) {
                friendCard.icon.setImageDrawable(ContextCompat.getDrawable(friendCard.icon.getContext(), R.drawable.mii_fart));
            }
            if(m.getMii().equals(FriendCodes.DIKROT)) {
                friendCard.icon.setImageDrawable(ContextCompat.getDrawable(friendCard.icon.getContext(), R.drawable.mii_dikrot));
            }
        }
        friendCard.friendCode.setText(wiiList.get(position).getFriendCode());
        friendCard.vrPoints.setText(String.valueOf(wiiList.get(position).getVr()));
        friendCard.miiName.setText(wiiList.get(position).getMii());
        setAnimation(((FriendViewHolder) holder).itemView, position);
    }

    @Override
    public int getItemCount() {
        return wiiList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(wiiList.isEmpty()) {
            return DEFAULT_STATE;
        } else {
            return SEARCHED_STATE;
        }
    }

    private void setAnimation(View viewToAnimate, int pos) {
        if(pos > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = pos;
        }
    }
}
