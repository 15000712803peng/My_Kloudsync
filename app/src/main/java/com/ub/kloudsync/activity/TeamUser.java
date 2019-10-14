package com.ub.kloudsync.activity;

public class TeamUser {


    private int memberID;
    private String memberName;
    private String joinDate;
    private String memberAvatar;
    private int MemberType;

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

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getMemberAvatar() {
        return memberAvatar;
    }

    public void setMemberAvatar(String memberAvatar) {
        this.memberAvatar = memberAvatar;
    }

    public int getMemberType() {
        return MemberType;
    }

    public void setMemberType(int memberType) {
        MemberType = memberType;
    }

    @Override
    public String toString() {
        return "TeamUser{" +
                "memberID=" + memberID +
                ", memberName='" + memberName + '\'' +
                ", joinDate='" + joinDate + '\'' +
                ", memberAvatar='" + memberAvatar + '\'' +
                ", MemberType=" + MemberType +
                '}';
    }
}
