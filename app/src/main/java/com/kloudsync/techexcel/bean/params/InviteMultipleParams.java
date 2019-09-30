package com.kloudsync.techexcel.bean.params;

import com.kloudsync.techexcel.bean.InviteInfo;

import java.util.List;

public class InviteMultipleParams {
    private List<InviteInfo> inviteInfos;

    public List<InviteInfo> getInviteInfos() {
        return inviteInfos;
    }

    public void setInviteInfos(List<InviteInfo> inviteInfos) {
        this.inviteInfos = inviteInfos;
    }
}
