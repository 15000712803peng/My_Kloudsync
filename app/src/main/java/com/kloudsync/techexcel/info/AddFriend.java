package com.kloudsync.techexcel.info;

import java.io.Serializable;

public class AddFriend implements Serializable {
    private String sourceID;  // 消息属性，可随意定义
    private String targetID;
    private String time;
    private String type;
    private String userID;

    private String name;
    private String phone;
    private String url;


    public AddFriend() {
        super();
    }

    public AddFriend(String sourceID, String targetID, String time,
                     String type, String userID) {
        super();
        this.sourceID = sourceID;
        this.targetID = targetID;
        this.time = time;
        this.type = type;
        this.userID = userID;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


}
