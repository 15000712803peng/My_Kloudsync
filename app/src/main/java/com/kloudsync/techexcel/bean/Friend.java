package com.kloudsync.techexcel.bean;

public class Friend {
    private String UserName;
    private String AvatarUrl;
    private String RongCloudID;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public String getRongCloudID() {
        return RongCloudID;
    }

    public void setRongCloudID(String rongCloudID) {
        RongCloudID = rongCloudID;
    }
}
