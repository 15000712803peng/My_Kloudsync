package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/2/20.
 */

public class RequestContactData {
    private long userId;
    private String userName;
    private String avatarUrl;
    private long rongCloudId;
    private boolean ifMyContact;
    private boolean ifCompanyContact;
    private int status;
    private boolean ifInSameCompany;

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

    public long getRongCloudId() {
        return rongCloudId;
    }

    public void setRongCloudId(long rongCloudId) {
        this.rongCloudId = rongCloudId;
    }

    public boolean isIfMyContact() {
        return ifMyContact;
    }

    public void setIfMyContact(boolean ifMyContact) {
        this.ifMyContact = ifMyContact;
    }

    public boolean isIfCompanyContact() {
        return ifCompanyContact;
    }

    public void setIfCompanyContact(boolean ifCompanyContact) {
        this.ifCompanyContact = ifCompanyContact;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isIfInSameCompany() {
        return ifInSameCompany;
    }

    public void setIfInSameCompany(boolean ifInSameCompany) {
        this.ifInSameCompany = ifInSameCompany;
    }
}
