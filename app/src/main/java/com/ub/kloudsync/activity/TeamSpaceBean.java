package com.ub.kloudsync.activity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class TeamSpaceBean implements Serializable {

    private int ItemID;
    private String Name;
    private int CompanyID;
    private int Type;
    private int ParentID;
    private String CreatedDate;
    private String CreatedByName;
    private int AttachmentCount;
    private int TopicType;
    private int MemberCount;
    private int SyncRoomCount;
    private List<TeamSpaceBean> SpaceList;
    private List<TeamUser> MemberList;
    private boolean isSelect;

    public List<TeamSpaceBean> getSpaceList() {
        return SpaceList;
    }

    public void setSpaceList(List<TeamSpaceBean> spaceList) {
        SpaceList = spaceList;
    }

    public List<TeamUser> getMemberList() {
        return MemberList;
    }

    public void setMemberList(List<TeamUser> memberList) {
        MemberList = memberList;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
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

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getCreatedByName() {
        return CreatedByName;
    }

    public void setCreatedByName(String createdByName) {
        CreatedByName = createdByName;
    }

    public int getAttachmentCount() {
        return AttachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        AttachmentCount = attachmentCount;
    }

    public int getTopicType() {
        return TopicType;
    }

    public void setTopicType(int topicType) {
        TopicType = topicType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamSpaceBean that = (TeamSpaceBean) o;
        return ItemID == that.ItemID &&
                CompanyID == that.CompanyID &&
                Type == that.Type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ItemID, CompanyID, Type);
    }
}
