package com.kloudsync.techexcel.bean;

public class Attendee {
    public String LessonID;
    public String	MemberID;
    public int	Role;
    public String	MemberName;
    public String	Phone;
    public String	AvatarUrl;
    public String	Joined;

    public String getLessonID() {
        return LessonID;
    }

    public void setLessonID(String lessonID) {
        LessonID = lessonID;
    }

    public String getMemberID() {
        return MemberID;
    }

    public void setMemberID(String memberID) {
        MemberID = memberID;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public String getMemberName() {
        return MemberName;
    }

    public void setMemberName(String memberName) {
        MemberName = memberName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public String getJoined() {
        return Joined;
    }

    public void setJoined(String joined) {
        Joined = joined;
    }
}
