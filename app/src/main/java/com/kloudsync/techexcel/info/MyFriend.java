package com.kloudsync.techexcel.info;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MyFriend implements Serializable {

    private String UserID;
    private String RongCloudUserID;
    private boolean IsFriend;
    private String Name;
    private int Type;
    private String Phone;
    private String AvatarUrl;
    private String Comment;
    private String LoginAlias;
    private String Email;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getRongCloudUserID() {
        return RongCloudUserID;
    }

    public void setRongCloudUserID(String rongCloudUserID) {
        RongCloudUserID = rongCloudUserID;
    }

    public boolean isFriend() {
        return IsFriend;
    }

    public void setFriend(boolean friend) {
        IsFriend = friend;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
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

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getLoginAlias() {
        return LoginAlias;
    }

    public void setLoginAlias(String loginAlias) {
        LoginAlias = loginAlias;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
