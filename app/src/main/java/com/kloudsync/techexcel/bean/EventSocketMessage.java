package com.kloudsync.techexcel.bean;

import org.json.JSONObject;

/**
 * Created by tonyan on 2019/11/19.
 */

public class EventSocketMessage {
    private String action;
    private JSONObject data;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EventSocketMessage{" +
                "action='" + action + '\'' +
                ", data=" + data +
                '}';
    }
}
