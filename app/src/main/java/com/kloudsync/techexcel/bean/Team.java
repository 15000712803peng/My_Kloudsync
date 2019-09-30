package com.kloudsync.techexcel.bean;

public class Team {
    int ItemID;
    String Name;
    int CompanyID;
    int Type;
    int ParentID;
    long CreatedDate;
    String CreatedByName;
    int ReferenceID;
    int MemberType;
    int AttachmentCount;
    int MemberCount;
    int SyncRoomCount;
    boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int itemID) {
        ItemID = itemID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int companyID) {
        CompanyID = companyID;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
    }

    public long getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(long createdDate) {
        CreatedDate = createdDate;
    }

    public String getCreatedByName() {
        return CreatedByName;
    }

    public void setCreatedByName(String createdByName) {
        CreatedByName = createdByName;
    }

    public int getReferenceID() {
        return ReferenceID;
    }

    public void setReferenceID(int referenceID) {
        ReferenceID = referenceID;
    }

    public int getMemberType() {
        return MemberType;
    }

    public void setMemberType(int memberType) {
        MemberType = memberType;
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
}
