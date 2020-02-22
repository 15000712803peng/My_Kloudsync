package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/2/22.
 */

public class ContactDetailData {

    private long userId;
    private String userName;
    private String avatarUrl;
    private String email;
    private String primaryPhone;
    private String description;
    private boolean showTeamSpace;
    private String teamCount;
    private String spaceCount;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isShowTeamSpace() {
        return showTeamSpace;
    }

    public void setShowTeamSpace(boolean showTeamSpace) {
        this.showTeamSpace = showTeamSpace;
    }

    public String getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(String teamCount) {
        this.teamCount = teamCount;
    }

    public String getSpaceCount() {
        return spaceCount;
    }

    public void setSpaceCount(String spaceCount) {
        this.spaceCount = spaceCount;
    }
}
