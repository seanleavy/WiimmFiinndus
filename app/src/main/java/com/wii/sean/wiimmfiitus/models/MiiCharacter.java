package com.wii.sean.wiimmfiitus.models;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MiiCharacter implements Serializable {

    public static boolean MIIOFFLINE = false;
    public static boolean MIIONLINE = true;

    public static final int DEFAULT_VIEW = 0;
    public static final int COMPACT_VIEW = 1;
    public static final int DEFAULT_VIEW_DETAILED = 2;
    public static final int COMPACT_VIEW_DETAILED = 3;

    private String friendCode = "";
    private String mii = "";
    private String vr = "";
    private Integer type = MiiCharacter.DEFAULT_VIEW;
    private boolean isOnline = false;
    private String role = "";
    private String region = "";
    private String match = "";
    private String connectionFails = "";
    private boolean isFriend = false;

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

    //todo: issue with snappy db preventing a builder class
    public MiiCharacter(String fCode, String miiName, String vrPoints, boolean online, int type, String role, String region, String match, String connectionFails) {
        this.friendCode = fCode;
        this.mii = miiName;
        this.vr = vrPoints;
        this.type = type;
        this.isOnline = online;
        this.role = role;
        this.region = region;
        this.match = match;
        this.connectionFails = connectionFails;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public String getRegion() {
        return region;
    }

    public String getMatch() {
        return match;
    }

    public String getConnectionFails() {
        return connectionFails;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }
}
