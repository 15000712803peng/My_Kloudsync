package com.kloudsync.techexcel.info;


import java.io.Serializable;

public class CustomerYu implements Serializable{

    private String UserID;
    private String RongCloudID;
    private String Nickname;
    private String FullName;
    private String FirstName;
    private String MiddleName;
    private String LastName;
    private String LoginName;
    private String AvatarUrl;
    private String Description;
    private String PrimaryPhone;
    private String SecondaryPhone;
    private String Email;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getRongCloudID() {
        return RongCloudID;
    }

    public void setRongCloudID(String rongCloudID) {
        RongCloudID = rongCloudID;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getPrimaryPhone() {
        return PrimaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        PrimaryPhone = primaryPhone;
    }

    public String getSecondaryPhone() {
        return SecondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        SecondaryPhone = secondaryPhone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
