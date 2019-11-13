package com.kloudsync.techexcel.bean;

import org.json.JSONObject;

/**
 * Created by tonyan on 2019/11/11.
 */

public class FollowInfo {

    private int type;
    private JSONObject data;
    private String meetingId;
    private String lessionId;
    private int meetingType;
    private int itemId;
    private String actionType;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getLessionId() {
        return lessionId;
    }

    public void setLessionId(String lessionId) {
        this.lessionId = lessionId;
    }

    public int getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(int meetingType) {
        this.meetingType = meetingType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FollowInfo{" +
                "type=" + type +
                ", data=" + data +
                ", meetingId='" + meetingId + '\'' +
                ", lessionId='" + lessionId + '\'' +
                ", meetingType=" + meetingType +
                ", itemId=" + itemId +
                ", actionType='" + actionType + '\'' +
                '}';
    }
}
