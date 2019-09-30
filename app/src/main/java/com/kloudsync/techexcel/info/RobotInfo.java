package com.kloudsync.techexcel.info;

/**
 * Created by pingfan on 2018/9/18.
 */

public class RobotInfo {
    private String type;
    private String id;
    private String timestamp;
    private String localTimestamp;
    private String channelId;
    private String fromid;
    private String fromname;
    private String conversationid;
    private String text;
    private String inputHint;
    private String replyToId;

    public RobotInfo() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocalTimestamp() {
        return localTimestamp;
    }

    public void setLocalTimestamp(String localTimestamp) {
        this.localTimestamp = localTimestamp;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getFromname() {
        return fromname;
    }

    public void setFromname(String fromname) {
        this.fromname = fromname;
    }

    public String getConversationid() {
        return conversationid;
    }

    public void setConversationid(String conversationid) {
        this.conversationid = conversationid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInputHint() {
        return inputHint;
    }

    public void setInputHint(String inputHint) {
        this.inputHint = inputHint;
    }

    public String getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(String replyToId) {
        this.replyToId = replyToId;
    }
}
