package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/12/8.
 */

public class EventRefreshMembers {


    private MeetingConfig meetingConfig;
    private boolean needRefresh;

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public MeetingConfig getMeetingConfig() {
        return meetingConfig;
    }

    public void setMeetingConfig(MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
    }
}
