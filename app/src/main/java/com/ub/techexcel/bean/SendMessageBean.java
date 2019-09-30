package com.ub.techexcel.bean;

/**
 * Created by wang on 2017/9/1.
 */

public class SendMessageBean {

    private String sourceID;
    private String targetID;
    private String incidentID;
    private String roleType;
    private String attachmentUrl;
    private int actiontype;
    private String meetingId;

    public int getActiontype() {
        return actiontype;
    }

    public void setActiontype(int actiontype) {
        this.actiontype = actiontype;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
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

    public String getIncidentID() {
        return incidentID;
    }

    public void setIncidentID(String incidentID) {
        this.incidentID = incidentID;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }
}
