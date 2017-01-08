package com.wii.sean.wiimmfiitus.model;

public class MiiCharacter {

    private String friendCode;
    private String mii;
    private String vr;

    public MiiCharacter() {

    }

    public MiiCharacter(String fCode, String miiName, String vrPoints) {
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

    public String getVr() {
        return vr;
    }

    public void setVr(String vr) {
        this.vr = vr;
    }
}
