package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/11/19.
 */

public class MeetingConfig {
    private int type;
    private String meetingId;
    private int fileId;
    private int pageNumber;
    private String userToken;
    private int lessionId;
    private MeetingRole role = MeetingRole.MEMBER;
    private String documentId;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public int getLessionId() {
        return lessionId;
    }

    public void setLessionId(int lessionId) {
        this.lessionId = lessionId;
    }

    public MeetingRole getRole() {
        return role;
    }

    public void setRole(MeetingRole role) {
        this.role = role;
    }

    public enum MeetingRole {
        DEFULT(-1), MEMBER(0), HOST(2), AUDIENCE(3);
        private int role;

        private MeetingRole(int role) {
            this.role = role;
        }
        public static MeetingRole match(int role) {
            for (MeetingConfig.MeetingRole myRole : MeetingRole.values()) {
                if (myRole.role == role) {
                    return myRole;
                }
            }
            return MeetingConfig.MeetingRole.DEFULT;
        }

        public int getRole() {
            return role;
        }

    }
}
