package com.ub.techexcel.bean;

import java.io.Serializable;

public class SyncRoomBean implements Serializable {



    private int itemID;
    private String name;
    private int companyID;
    private int type;
    private int topicType;
    private int parentID;
    private String createdDate;
    private String createdByName;
    private int linkedDocTeamID;
    private int synchronizeMember;
    private int memberType;

    private int documentCount;
    private int meetingCount;
    private int memberCount;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(int documentCount) {
        this.documentCount = documentCount;
    }

    public int getMeetingCount() {
        return meetingCount;
    }

    public void setMeetingCount(int meetingCount) {
        this.meetingCount = meetingCount;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTopicType() {
        return topicType;
    }

    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public int getLinkedDocTeamID() {
        return linkedDocTeamID;
    }

    public void setLinkedDocTeamID(int linkedDocTeamID) {
        this.linkedDocTeamID = linkedDocTeamID;
    }

    public int getSynchronizeMember() {
        return synchronizeMember;
    }

    public void setSynchronizeMember(int synchronizeMember) {
        this.synchronizeMember = synchronizeMember;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }
}
