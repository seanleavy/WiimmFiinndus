package com.wii.sean.wiimmfiitus.models;

import java.util.ArrayList;
import java.util.List;

public class RoomModel {

    private String roomName = "";
    private String regionName = "";
    private String connectionRating = "";
    private String timesRaced = "";
    private String lobbyCreatedTime = "";
    private List<MiiCharacter> miiList = new ArrayList<>();

    public RoomModel(String roomName, String regionName, String connectionRating, String timesRaced, String lobbyCreatedTime, List<MiiCharacter> list) {
        this.roomName = roomName;
        this.regionName = regionName;
        this.connectionRating = connectionRating;
        this.timesRaced = timesRaced;
        this.lobbyCreatedTime = lobbyCreatedTime;
        this.miiList = list;
    }


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getConnectionRating() {
        return connectionRating;
    }

    public void setConnectionRating(String connectionRating) {
        this.connectionRating = connectionRating;
    }

    public List<MiiCharacter> getMiiList() {
        return miiList;
    }

    public void setMiiList(List<MiiCharacter> miiList) {
        this.miiList = miiList;
    }

    public String getTimesRaced() {
        return timesRaced;
    }

    public void setTimesRaced(String timesRaced) {
        this.timesRaced = timesRaced;
    }

    public String getLobbyCreatedTime() {
        return lobbyCreatedTime;
    }

    public void setLobbyCreatedTime(String lobbyCreatedTime) {
        this.lobbyCreatedTime = lobbyCreatedTime;
    }
}
