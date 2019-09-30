package com.kloudsync.techexcel.bean;

import java.util.Objects;

public class Space {
    private int itemID;
    private String name;
    private int companyID;
    private int type;
    private int parentID;
    private String createdDate;
    private String createdByName;
    private int attachmentCount;
    private int TopicType;
    private int memberCount;
    private int syncRoomCount;

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

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public int getTopicType() {
        return TopicType;
    }

    public void setTopicType(int topicType) {
        TopicType = topicType;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getSyncRoomCount() {
        return syncRoomCount;
    }

    public void setSyncRoomCount(int syncRoomCount) {
        this.syncRoomCount = syncRoomCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Space that = (Space) o;
        return itemID == that.itemID &&
                companyID == that.companyID &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID, companyID, type);
    }
}
