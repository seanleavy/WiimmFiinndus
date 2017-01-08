package com.wii.sean.wiimmfiitus.friendSearch;

import android.util.Log;

import com.wii.sean.wiimmfiitus.friendSearch.Constants.FriendCodes;
import com.wii.sean.wiimmfiitus.friendSearch.Constants.UrlConstants;
import com.wii.sean.wiimmfiitus.helpers.LogHelper;
import com.wii.sean.wiimmfiitus.helpers.RandomUserAgent;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MkFriendSearch {

    private ArrayList<String> miis;
    private ArrayList<String> friendCodes;
    private ArrayList<String> vrpoints;
    private List<MiiCharacter> miiFriendsFound;

    public MkFriendSearch() {
    }

    public List<MiiCharacter> searchFriendList(String friendCodeSearchTag) {
        try {
            String userAgent = RandomUserAgent.getRandomUserAgent();
            Document mkDocument = Jsoup.connect(UrlConstants.WiimFiiUrl)
                    .userAgent(userAgent)
                    .get();
            Element table = mkDocument.select("table").get(0);
            Elements tr = table.select("tr[class^=tr0]");

            miis = new ArrayList<>();
            friendCodes = new ArrayList<>();
            vrpoints = new ArrayList<>();
            miiFriendsFound = new ArrayList();

            int rowCount = 0;
            for(Element row : tr) {
                if(row.text().length() > 8) {
                    friendCodes.add(row.select("a").text());
                    vrpoints.add(tr.get(rowCount).select("td").get(4).text());
                    miis.add(tr.get(rowCount).select("td").get(6).text());
                    rowCount++;
                }
            }
            searchResults(friendCodeSearchTag, vrpoints, miis, friendCodes);
        } catch (IOException e) {
            Log.e(LogHelper.getTag(MkFriendSearch.class), e.getMessage());
        }
        return miiFriendsFound;
    }

    private void searchResults(String tag, ArrayList vr, ArrayList miiName, ArrayList fCode) {
        for(int i = 0; i < fCode.size(); i++) {
            if(fCode.get(i) == tag ||
                    fCode.get(i).equals(FriendCodes.PONCHO.getFriendCode()) ||
                    fCode.get(i).equals(FriendCodes.ALAN.getFriendCode()) ||
                    fCode.get(i).equals(FriendCodes.FARTFACE.getFriendCode())) {

                miiFriendsFound.add(new MiiCharacter(fCode.get(i).toString(),
                        miiName.get(i).toString(),
                        vr.get(i).toString()));
            }
        }
    }
}
