package com.wii.sean.wiimmfiitus.model;

public class MiiCharacter {

    private String friendCode;
    private String mii;
    private int vr;

    public MiiCharacter() {

    }

    public MiiCharacter(String fCode, String miiName, int vrPoints) {
        this.friendCode = fCode;
        this.mii = miiName;
        this.vr = vrPoints;

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

    public int getVr() {
        return vr;
    }

    public void setVr(int vr) {
        this.vr = vr;
    }
}
