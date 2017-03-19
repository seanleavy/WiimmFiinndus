package com.wii.sean.wiimmfiitus.model;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class MiiCharacter {

    private String friendCode;
    private String mii;
    private String vr;
    private boolean isOnline = false;

    public MiiCharacter() {

    }

    public MiiCharacter(String fCode, String miiName, String vrPoints) {
        this.friendCode = fCode;
        this.mii = miiName;
        this.vr = vrPoints;

    }

    public MiiCharacter(String fCode, String miiName, String vrPoints, boolean online) {
        this.friendCode = fCode;
        this.mii = miiName;
        this.vr = vrPoints;
        this.isOnline = online;
    }

    public String getFriendCode() {
        return friendCode;
    }

    public void setFriendCode(String friendCode) {
        this.friendCode = friendCode;
    }

    public String getMii() {
        return mii;
    }

    public void setMii(String mii) {
        this.mii = mii;
    }

    public String getVr() {
        return vr;
    }

    public void setVr(String vr) {
        this.vr = vr;
    }

    public void setOnlineTo(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean isOnline() {
        return this.isOnline;
    }

    public List toList() {
        List<String> list = new ArrayList<String>();
        list.add(getMii());
        list.add(getFriendCode());
        list.add(getVr());
        return list;
    }

    public String toGson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static MiiCharacter gsonToMii(String gson) {
        return new Gson().fromJson(gson, MiiCharacter.class);
    }

    @Override
    public boolean equals(Object obj) {
        return ((MiiCharacter) obj).getFriendCode().equals(this.getFriendCode()) && ((MiiCharacter) obj).getMii().equals(this.getMii());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
