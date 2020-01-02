package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/12/17.
 */

public class EventJoinMeeting {
    private String meetingId;
    private int lessionId;
    private int role;
    private int hostId;

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public int getLessionId() {
        return lessionId;
    }

    public void setLessionId(int lessionId) {
        this.lessionId = lessionId;
    }
}
