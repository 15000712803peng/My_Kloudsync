package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/11/2.
 */

public class EventSpaceFragment {

    private int itemID;
    private String spaceName;
    private int teamId;
    private int type;
    private int spaceId;

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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
        return "EventSpaceFragment{" +
                "itemID=" + itemID +
                ", spaceName='" + spaceName + '\'' +
                ", teamId=" + teamId +
                '}';
    }
}
