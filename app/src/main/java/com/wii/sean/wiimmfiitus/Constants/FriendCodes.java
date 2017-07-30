package com.wii.sean.wiimmfiitus.Constants;

import com.wii.sean.wiimmfiitus.models.MiiCharacter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendCodes {
    public static MiiCharacter PONCHO = new MiiCharacter("4814-3953-4486", "Poncho", "");
    public static MiiCharacter DIKROT = new MiiCharacter("3526-1167-6604", "Δ¡ckΓ◎τ", "");
    public static MiiCharacter SEAN = new MiiCharacter("5284-6991-7807", "Sean", "");
    public static MiiCharacter FARTFACE = new MiiCharacter("2922-2443-6911", "fartface", "");

    public static List<MiiCharacter> getDefaultMiis() {
        PONCHO.setFriend(true);
        DIKROT.setFriend(true);
        SEAN.setFriend(true);
        FARTFACE.setFriend(true);
        return new ArrayList<>(Arrays.asList(FriendCodes.DIKROT, FriendCodes.FARTFACE, FriendCodes.PONCHO, FriendCodes.SEAN));
    }
}
