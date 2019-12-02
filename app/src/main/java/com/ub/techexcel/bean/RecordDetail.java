package com.ub.techexcel.bean;

import java.util.List;

public class RecordDetail {

    private int recordingId;
    private String title;
    private long createDate ;
    private long duration;
    private int status;
    private List<ChannelVO> channelVOList;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ChannelVO> getChannelVOList() {
        return channelVOList;
    }

    public void setChannelVOList(List<ChannelVO> channelVOList) {
        this.channelVOList = channelVOList;
    }
}
