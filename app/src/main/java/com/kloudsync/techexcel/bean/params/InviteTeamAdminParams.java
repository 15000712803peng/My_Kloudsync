package com.kloudsync.techexcel.bean.params;

public class InviteTeamAdminParams {

    private String InviteTo;
    private String[] UserIDList;

    public String getInviteTo() {
        return InviteTo;
    }

    public void setInviteTo(String inviteTo) {
        InviteTo = inviteTo;
    }

    public String[] getUserIDList() {
        return UserIDList;
    }

    public void setUserIDList(String[] userIDList) {
        UserIDList = userIDList;
    }
}
