package com.ub.techexcel.bean;

public class SyncRoomMember {



    private int syncroomId;
    private int memberID;
    private String memberName;
    private String memberAvatar;
    private String joinDate;
    private int memberType;

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }

    public int getSyncroomId() {
        return syncroomId;
    }

    public void setSyncroomId(int syncroomId) {
        this.syncroomId = syncroomId;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberAvatar() {
        return memberAvatar;
    }

    public void setMemberAvatar(String memberAvatar) {
        this.memberAvatar = memberAvatar;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }


}
