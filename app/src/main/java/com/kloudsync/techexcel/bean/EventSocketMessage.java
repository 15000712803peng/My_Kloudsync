package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/11/19.
 */

public class EventSocketMessage {
    private String action;
    private String data;
    private String retData;

    public String getRetData() {
        return retData;
    }

    public void setRetData(String retData) {
        this.retData = retData;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EventSocketMessage{" +
                "action='" + action + '\'' +
                ", data='" + data + '\'' +
                ", retData='" + retData + '\'' +
                '}';
    }
}
