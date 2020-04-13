package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/4/13.
 */

public class CompanyAccountInfo {
    private String companyName;
    private String webAddress;
    private String verifyEmailAddress;
    private boolean enableInviteCode;
    private String inviteCode;
    private int systemType;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getVerifyEmailAddress() {
        return verifyEmailAddress;
    }

    public void setVerifyEmailAddress(String verifyEmailAddress) {
        this.verifyEmailAddress = verifyEmailAddress;
    }

    public boolean isEnableInviteCode() {
        return enableInviteCode;
    }

    public void setEnableInviteCode(boolean enableInviteCode) {
        this.enableInviteCode = enableInviteCode;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public int getSystemType() {
        return systemType;
    }

    public void setSystemType(int systemType) {
        this.systemType = systemType;
    }
}
