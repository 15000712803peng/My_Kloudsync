package com.kloudsync.techexcel.bean;

public class InviteInfo {
    private String Mobile;
    private String inviteTo;
    private int inviteToType;
    private String CompanyID;

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getInviteTo() {
        return inviteTo;
    }

    public void setInviteTo(String inviteTo) {
        this.inviteTo = inviteTo;
    }

    public int getInviteToType() {
        return inviteToType;
    }

    public void setInviteToType(int inviteToType) {
        this.inviteToType = inviteToType;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
    }
}
