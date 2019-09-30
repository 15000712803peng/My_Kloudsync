package com.kloudsync.techexcel.config;


public class MeetingConfig {
    private String hostId;
    private String presenterId;
    private MeetingRole meetingRole;
    private String meetingId;

    public enum MeetingRole {
        DEFULT(-1), MEMBER(0), HOST(2), AUDIENCE(3);
        private int role;

        private MeetingRole(int role) {
            this.role = role;
        }

        public static MeetingRole match(int role) {
            for (MeetingRole myRole : MeetingRole.values()) {
                if (myRole.role == role) {
                    return myRole;
                }
            }
            return MeetingRole.DEFULT;
        }
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(String presenterId) {
        this.presenterId = presenterId;
    }

    public MeetingRole getMeetingRole() {
        return meetingRole;
    }

    public void setMeetingRole(MeetingRole meetingRole) {
        this.meetingRole = meetingRole;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }
}
