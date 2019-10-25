package com.ub.techexcel.bean;

import java.util.List;

public class RecordingBean {

    int recordingId;
    String title;
    long createDate ;
    long duration;
    private List<ChannelVO> channelVOList;

    public List<ChannelVO> getChannelVOList() {
        return channelVOList;
    }

    public void setChannelVOList(List<ChannelVO> channelVOList) {
        this.channelVOList = channelVOList;
    }

    public int getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(int recordingId) {
        this.recordingId = recordingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
