package com.kloudsync.techexcel.bean;

import android.support.annotation.NonNull;

public class TeamMember implements Comparable<TeamMember> {

    public static final int TYPE_MEMBER = 0;
    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_OWNER = 2;
    //-----------------
    public static final int OPERATION_SET_ADMIN = 1;
    public static final int OPERATION_CANCEL_ADMIN = 2;
    private String MemberID;
    private String MemberName;
    private String MemberAvatar;
    private String JoinDate;
    private int MemberType;
    private boolean isShowMore;

    public boolean isShowMore() {
        return isShowMore;
    }

    public void setShowMore(boolean showMore) {
        isShowMore = showMore;
    }

    public String getMemberID() {
        return MemberID;
    }

    public void setMemberID(String memberID) {
        MemberID = memberID;
    }

    public String getMemberName() {
        return MemberName;
    }

    public void setMemberName(String memberName) {
        MemberName = memberName;
    }

    public String getMemberAvatar() {
        return MemberAvatar;
    }

    public void setMemberAvatar(String memberAvatar) {
        MemberAvatar = memberAvatar;
    }

    public String getJoinDate() {
        return JoinDate;
    }

    public void setJoinDate(String joinDate) {
        JoinDate = joinDate;
    }

    public int getMemberType() {
        return MemberType;
    }

    public void setMemberType(int memberType) {
        MemberType = memberType;
    }

    @Override
    public int compareTo(@NonNull TeamMember o) {
        return o.getMemberType() - getMemberType();
    }
}
