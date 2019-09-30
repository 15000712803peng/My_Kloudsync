package com.kloudsync.techexcel.bean;

import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.List;

public class MessageSpaceList {
    private List<TeamSpaceBean> spaceList;

    public MessageSpaceList() {
    }

    public MessageSpaceList(List<TeamSpaceBean> spaceList) {
        this.spaceList = spaceList;
    }

    public List<TeamSpaceBean> getSpaceList() {
        return spaceList;
    }

    public void setSpaceList(List<TeamSpaceBean> spaceList) {
        this.spaceList = spaceList;
    }
}
