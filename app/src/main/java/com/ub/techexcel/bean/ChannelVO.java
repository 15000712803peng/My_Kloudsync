package com.ub.techexcel.bean;

import java.util.ArrayList;
import java.util.List;

public class ChannelVO {

    private int channelId;

    private int type;

    private int userId;

    private List<SectionVO>  sectionVOList=new ArrayList<>();


    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<SectionVO> getSectionVOList() {
        return sectionVOList;
    }

    public void setSectionVOList(List<SectionVO> sectionVOList) {
        this.sectionVOList = sectionVOList;
    }
}
