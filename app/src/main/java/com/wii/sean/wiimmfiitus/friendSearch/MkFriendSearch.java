package com.wii.sean.wiimmfiitus.friendSearch;

import android.util.Log;

import com.wii.sean.wiimmfiitus.Constants.FriendCodes;
import com.wii.sean.wiimmfiitus.Constants.UrlConstants;
import com.wii.sean.wiimmfiitus.helpers.LogHelper;
import com.wii.sean.wiimmfiitus.helpers.RandomUserAgent;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;
import com.wii.sean.wiimmfiitus.model.RoomModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MkFriendSearch {

    public static final boolean ROOMENABLED = true;

    private ArrayList<String> miis;
    private ArrayList<String> friendCodes;
    private ArrayList<String> vrpoints;
    private List<MiiCharacter> resultList;

    public MkFriendSearch() {
    }

    public List searchFriendList(Object... searchTokenParam) {
        Object searchToken = searchTokenParam[0];
        try {
            String userAgent = RandomUserAgent.getRandomUserAgent();
            Document mkDocument = Jsoup.connect(UrlConstants.WiimFiiUrl)
                    .userAgent(userAgent)
                    .get();

            boolean SEARCHROOM = false;
            if(searchTokenParam.length > 1)
                SEARCHROOM =  (Boolean) searchTokenParam[1];
            if(!SEARCHROOM) {
                Element table = mkDocument.select("table").get(0);
                Elements tr = table.select("tr[class^=tr0],tr[class^=tr1]");
                miis = new ArrayList<>();
                friendCodes = new ArrayList<>();
                vrpoints = new ArrayList<>();
                resultList = new ArrayList();

                int rowCount = 0;
                for (Element row : tr) {
                    if (row.text().length() > 8) {
                        friendCodes.add(row.select("a").text().trim());
                        vrpoints.add(tr.get(rowCount).select("td").get(6).text());
                        miis.add(tr.get(rowCount).select("td").get(8).text());
                        rowCount++;
                    }
                }
                searchResults(searchToken, vrpoints, miis, friendCodes);
            }
            if(SEARCHROOM) {
                Elements elements = mkDocument.getAllElements();
                if(elements.select("tr.tr0:contains("+searchToken+"),tr.tr1:contains("+searchToken+")").size() == 0) {
                    return null;
                }
                Element uppBound = elements.select("tr.tr0:contains(" + searchToken + ")" +
                        ",tr.tr1:contains("+searchToken+")").prevAll("[id]")
                        .first()
                        .nextElementSibling();
                Element lowerBound = elements.select("tr.tr0:contains(" + searchToken + ")," +
                        " tr.tr1:contains(" + searchToken + ")")
                        .nextAll("[id]")
                        .first();

                String fCode = "";
                //todo: role
                String role = "";
                String region = "";
                String connFail = "--";
                String VRpoints = "";
                String mii = "";
                String match = "";

                Element e = uppBound;
                List<MiiCharacter> miiList = new ArrayList<>();
                while(e.nextElementSibling() != lowerBound) {
                    e = uppBound.nextElementSibling();
                    //todo MiiCharacter as builder
                    fCode = e.children().get(0).text();
                    mii = e.children().get(8).text();
                    if(mii.contains("1.") && mii.contains("2.")) {
                        mii = mii.substring(2, mii.indexOf("2"));
                    }
                    VRpoints = e.children().get(6).text();
                    role = e.children().get(1).text();
                    region = e.children().get(2).text();
                    connFail = e.children().get(5).text();
                    match = e.children().get(3).text();
                    miiList.add(new MiiCharacter(fCode, mii, VRpoints,
                            MiiCharacter.MIIONLINE, MiiCharacter.COMPACT_VIEW_DETAILED, role,
                            region, match, connFail));
                    uppBound = e;
                }

                Elements roomElements = elements.select("tr.tr0:contains(" + searchToken + "), tr.tr1:contains(" + searchToken + ")").prevAll("[id]").first().children();
                String roomTitle = roomElements.select("th").text();
                String room = roomElements.select("a").text();
                String locale = roomTitle.substring(0, roomTitle.lastIndexOf(room)).trim();
                String connfails = roomTitle.substring(roomTitle.lastIndexOf(")") + 1, roomTitle.lastIndexOf(" ")).trim();
                String races = roomTitle.substring(roomTitle.lastIndexOf(",") + 1, roomTitle.lastIndexOf("(")).trim();
                String raceTimes = roomTitle.substring(roomTitle.indexOf("(") + 1, roomTitle.indexOf(")"));
                RoomModel roomModel = new RoomModel(room, locale, connfails, races, raceTimes, miiList);
                return new ArrayList(Arrays.asList(roomModel));
            }
        } catch (IOException e) {
            Log.e(LogHelper.getTag(MkFriendSearch.class), e.getMessage());
        }
        return resultList;
    }

    //todo do a loose match for codes also
    private void searchResults(Object tag, ArrayList<String> vr, ArrayList<String> miiName, ArrayList<String> fCode) {
        boolean isOnline = true;
        if(tag instanceof String) {
            if(!tag.equals("")) {
                for(int i = 0; i < fCode.size(); i++) {
                    if (fCode.get(i).equals(tag) || miiName.get(i).contains((String)tag)) {
                        String mii = miiName.get(i);
                        // remove 1. 2. Mii name here caused by 2 people on same wii
                        if(mii.contains("1.") && mii.contains("2.")) {
                            mii = mii.substring(2, mii.indexOf("2"));
                        }
                        resultList.add(new MiiCharacter(fCode.get(i),
                                mii,
                                vr.get(i).toString(),
                                isOnline));
                    }
                }
            }
            // default friend search
            else {
                for(int i = 0; i < fCode.size(); i++) {
                    if(fCode.get(i).equals(FriendCodes.PONCHO.getFriendCode()) ||
                            fCode.get(i).equals(FriendCodes.DIKROT.getFriendCode()) ||
                            fCode.get(i).equals(FriendCodes.FARTFACE.getFriendCode()) ||
                            fCode.get(i).equals(FriendCodes.SEAN.getFriendCode())) {
                        resultList.add(new MiiCharacter(fCode.get(i),
                                miiName.get(i).toString(),
                                vr.get(i).toString(),
                                isOnline));
                    }
                }
            }
        }

        if(tag instanceof List) {
            for(int i = 0; i < fCode.size(); i++) {
                for(MiiCharacter m : (List<MiiCharacter>) tag) {
                    if(fCode.get(i).equals(m.getFriendCode())) {
                        m.setOnlineTo(true);
                        m.setVr(vrpoints.get(i));
                        resultList.add(m);
                    }
                }
            }
        }
    }
}
