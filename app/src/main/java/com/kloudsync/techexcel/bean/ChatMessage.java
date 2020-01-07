package com.kloudsync.techexcel.bean;

import io.rong.imlib.model.Message;

/**
 * Created by tonyan on 2020/1/3.
 */

public class ChatMessage {
    private String userName;
    private String avatorUrl;
    private Message message;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatorUrl() {
        return avatorUrl;
    }

    public void setAvatorUrl(String avatorUrl) {
        this.avatorUrl = avatorUrl;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
