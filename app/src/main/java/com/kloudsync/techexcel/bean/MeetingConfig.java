package com.kloudsync.techexcel.bean;

import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.bean.AgoraUser;

import java.util.ArrayList;
import java.util.List;

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
    private int role = MeetingRole.MEMBER;
    private String documentId;
    private MeetingDocument document;
    private boolean isMicroOn;
    private boolean isRecordOn;
    private boolean isCameraOn;
    private boolean isInRealMeeting;
    private boolean isMembersCameraToggle;
    private String presenterSessionId;
    private boolean docModifide;
    private String notifyUrl;
    private List<MeetingMember> meetingMembers = new ArrayList<>();
    private List<MeetingMember> meetingAuditor = new ArrayList<>();
    private List<MeetingMember> meetingInvitors = new ArrayList<>();
    private String meetingHostId;
    private String agoraChannelId;
    private String presenterId;
    private List<AgoraMember> agoraMembers = new ArrayList<>();
    private boolean fromMeeting;

    public boolean isFromMeeting() {
        return fromMeeting;
    }

    public void setFromMeeting(boolean fromMeeting) {
        this.fromMeeting = fromMeeting;
    }

    public List<AgoraMember> getAgoraMembers() {
        return agoraMembers;
    }

    public void setAgoraMembers(List<AgoraMember> agoraMembers) {
        this.agoraMembers = agoraMembers;
    }

    public void addAgoraMember(AgoraMember member){
        if(!agoraMembers.contains(member)){
            agoraMembers.add(member);
        }
    }

    public void deleteAgoraMember(AgoraMember member){
        agoraMembers.remove(member);
    }

    public String getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(String presenterId) {
        this.presenterId = presenterId;
    }

    public String getAgoraChannelId() {
        return agoraChannelId;
    }

    public void setAgoraChannelId(String agoraChannelId) {
        this.agoraChannelId = agoraChannelId;
    }

    public String getMeetingHostId() {
        return meetingHostId;
    }

    public void setMeetingHostId(String meetingHostId) {
        this.meetingHostId = meetingHostId;
    }

    public List<MeetingMember> getMeetingAuditor() {
        return meetingAuditor;
    }

    public void setMeetingAuditor(List<MeetingMember> meetingAuditor) {
        this.meetingAuditor.clear();
        this.meetingAuditor.addAll(meetingAuditor);
    }

    public List<MeetingMember> getMeetingInvitors() {
        return meetingInvitors;
    }

    public void setMeetingInvitors(List<MeetingMember> meetingInvitors) {
        this.meetingInvitors.clear();
        this.meetingInvitors.addAll(meetingInvitors);
    }

    public List<MeetingMember> getMeetingMembers() {
        return meetingMembers;
    }

    public void setMeetingMembers(List<MeetingMember> meetingMembers) {
        this.meetingMembers.clear();
        this.meetingMembers.addAll(meetingMembers);
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public boolean isDocModifide() {
        return docModifide;
    }

    public void setDocModifide(boolean docModifide) {
        this.docModifide = docModifide;
    }

    public String getPresenterSessionId() {
        return presenterSessionId;
    }

    public void setPresenterSessionId(String presenterSessionId) {
        this.presenterSessionId = presenterSessionId;
    }

    public boolean isMembersCameraToggle() {
        return isMembersCameraToggle;
    }

    public void setMembersCameraToggle(boolean membersCameraToggle) {
        isMembersCameraToggle = membersCameraToggle;
    }

    public boolean isInRealMeeting() {
        return isInRealMeeting;
    }

    public void setInRealMeeting(boolean inRealMeeting) {
        isInRealMeeting = inRealMeeting;
    }

    public boolean isMicroOn() {
        return isMicroOn;
    }

    public void setMicroOn(boolean microOn) {
        isMicroOn = microOn;
    }

    public boolean isRecordOn() {
        return isRecordOn;
    }

    public void setRecordOn(boolean recordOn) {
        isRecordOn = recordOn;
    }

    public boolean isCameraOn() {
        return isCameraOn;
    }

    public void setCameraOn(boolean cameraOn) {
        isCameraOn = cameraOn;
    }

    public MeetingDocument getDocument() {
        return document;
    }

    public void setDocument(MeetingDocument document) {
        this.document = document;
    }

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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public static class MeetingRole {
        public static final int DEFULT = -1;
        public static final int MEMBER = 1;
        public static final int HOST = 2;
        public static final int AUDIENCE = 3;
        public static final int BE_INVITED = 4;
    }

    public void reset(){
        this.meetingMembers.clear();
        this.meetingAuditor.clear();
    }

    @Override
    public String toString() {
        return "MeetingConfig{" +
                "type=" + type +
                ", meetingId='" + meetingId + '\'' +
                ", fileId=" + fileId +
                ", pageNumber=" + pageNumber +
                ", userToken='" + userToken + '\'' +
                ", lessionId=" + lessionId +
                ", role=" + role +
                ", documentId='" + documentId + '\'' +
                ", document=" + document +
                ", isMicroOn=" + isMicroOn +
                ", isRecordOn=" + isRecordOn +
                ", isCameraOn=" + isCameraOn +
                ", isInRealMeeting=" + isInRealMeeting +
                ", isMembersCameraToggle=" + isMembersCameraToggle +
                ", agoraMembers=" + agoraMembers +
                ", presenterSessionId='" + presenterSessionId + '\'' +
                ", docModifide=" + docModifide +
                '}';
    }
}
