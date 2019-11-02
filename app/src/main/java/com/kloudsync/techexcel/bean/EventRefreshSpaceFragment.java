package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/11/2.
 */

public class EventRefreshSpaceFragment {

    private int itemID;
    private String spaceName;
    private int teamId;

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return "EventRefreshSpaceFragment{" +
                "itemID=" + itemID +
                ", spaceName='" + spaceName + '\'' +
                ", teamId=" + teamId +
                '}';
    }
}
