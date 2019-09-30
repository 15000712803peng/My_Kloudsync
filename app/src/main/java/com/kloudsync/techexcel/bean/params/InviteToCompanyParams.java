package com.kloudsync.techexcel.bean.params;

public class InviteToCompanyParams {
    private int CompanyID;
    private String Mobile;
    private int InviteToType;
    private int InviteTo;
    private int RequestAddFriend;

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int companyID) {
        CompanyID = companyID;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public int getInviteToType() {
        return InviteToType;
    }

    public void setInviteToType(int inviteToType) {
        InviteToType = inviteToType;
    }

    public int getInviteTo() {
        return InviteTo;
    }

    public void setInviteTo(int inviteTo) {
        InviteTo = inviteTo;
    }

    public int getRequestAddFriend() {
        return RequestAddFriend;
    }

    public void setRequestAddFriend(int requestAddFriend) {
        RequestAddFriend = requestAddFriend;
    }

    @Override
    public String toString() {
        return "InviteToCompanyParams{" +
                "CompanyID=" + CompanyID +
                ", Mobile='" + Mobile + '\'' +
                ", InviteToType=" + InviteToType +
                ", InviteTo=" + InviteTo +
                ", RequestAddFriend=" + RequestAddFriend +
                '}';
    }
}
