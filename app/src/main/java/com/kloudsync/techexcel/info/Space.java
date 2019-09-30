package com.kloudsync.techexcel.info;

import java.io.Serializable;

public class Space implements Serializable {
    private int itemID;
    private int type;
    private int parentID;
    private int companyID;
    private int AttachmentCount;
    private int MemberCount;
    private int SyncRoomCount;
    private String name;
    private String CreatedDate;
    private String CreatedByName;
    private boolean expand;

    public Space() {
    }

    public int getAttachmentCount() {
        return AttachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        AttachmentCount = attachmentCount;
    }

    public int getMemberCount() {
        return MemberCount;
    }

    public void setMemberCount(int memberCount) {
        MemberCount = memberCount;
    }

    public int getSyncRoomCount() {
        return SyncRoomCount;
    }

    public void setSyncRoomCount(int syncRoomCount) {
        SyncRoomCount = syncRoomCount;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
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

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedDate() {
        return CreatedDate == null ? "" : CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getCreatedByName() {
        return CreatedByName == null ? "" : CreatedByName;
    }

    public void setCreatedByName(String createdByName) {
        CreatedByName = createdByName;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }
}
