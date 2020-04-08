package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/12/17.
 */

public class EventJoinMeeting {
    private String meetingId;
	private int lessionId = -1;
    private int role = 1;
    private int hostId;
    private String orginalMeetingId;
    private boolean isHost;

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public String getOrginalMeetingId() {
        return orginalMeetingId;
    }

    public void setOrginalMeetingId(String orginalMeetingId) {
        this.orginalMeetingId = orginalMeetingId;
    }

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

    @Override
    public String toString() {
        return "EventJoinMeeting{" +
                "meetingId='" + meetingId + '\'' +
                ", lessionId=" + lessionId +
                ", role=" + role +
                ", hostId=" + hostId +
                ", orginalMeetingId='" + orginalMeetingId + '\'' +
                '}';
    }
}
