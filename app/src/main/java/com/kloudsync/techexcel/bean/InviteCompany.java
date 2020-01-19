package com.kloudsync.techexcel.bean;

import java.util.List;

public class InviteCompany {
    private boolean HaveComany;
    private List<Company> InvitationList;

    public boolean isHaveComany() {
        return HaveComany;
    }

    public void setHaveComany(boolean haveComany) {
        HaveComany = haveComany;
    }

    public List<Company> getInvitationList() {
        return InvitationList;
    }

    public void setInvitationList(List<Company> invitationList) {
        InvitationList = invitationList;
    }
}
