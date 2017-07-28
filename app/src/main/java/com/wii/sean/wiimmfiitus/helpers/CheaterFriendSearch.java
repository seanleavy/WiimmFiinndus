package com.wii.sean.wiimmfiitus.helpers;


import android.content.Context;

import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.List;

public class CheaterFriendSearch {

    public static List findCheatersAndFriends(Context context, List list) {
        List<MiiCharacter> miiCheatList = new PreferencesManager(context).getPreferencesAsList(PreferencesManager.CHEATERPREFERENCES);
        List<MiiCharacter> miiFriendList = new PreferencesManager(context).getPreferencesAsList(PreferencesManager.FAVOURITESPREFERENCES);
        for(Object mii : list) {
            if(miiCheatList.contains(mii))
                ((MiiCharacter) mii).setIsCheater(true);
            if(miiFriendList.contains(mii))
                ((MiiCharacter) mii).setFriend(true);
        }
        return list;
    }

    public static final boolean isACheater(Context context, Object o) {
        return new PreferencesManager(context).getPreferencesAsList(PreferencesManager.CHEATERPREFERENCES).contains(o);
    }

}
