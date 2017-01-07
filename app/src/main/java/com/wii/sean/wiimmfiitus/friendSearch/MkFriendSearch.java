package com.wii.sean.wiimmfiitus.friendSearch;

import android.util.Log;

import com.wii.sean.wiimmfiitus.friendSearch.Constants.UrlConstants;
import com.wii.sean.wiimmfiitus.helpers.LogHelper;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MkFriendSearch {

    public static List<MiiCharacter> searchFriendList() {
        MiiCharacter miiCharacter = new MiiCharacter();
        List<MiiCharacter> miiProfile = new ArrayList<>();
//        try {
//            Document mkDocument = Jsoup.connect(UrlConstants.WiimFiiUrl).get();
//            Element table = mkDocument.select("table").get(0);
//            Elements tr = table.select("tr");
            // dummy miiCharacter for testing
            miiCharacter.setFriendCode("0000-0000-0000");
            miiCharacter.setMii("Sean");
            miiCharacter.setVr(5000);
            miiProfile.add(miiCharacter);
            MiiCharacter miiCharacter2 = new MiiCharacter("1234-5677-8876", "CuntPhase", 1110);
            miiProfile.add(miiCharacter2);

//        } catch (IOException e) {
//            Log.e(LogHelper.getTag(MkFriendSearch.class), e.getMessage());
//        }
        return miiProfile;
    }
}
