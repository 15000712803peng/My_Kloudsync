package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/2/6.
 */

public class HelloMessage {

    private long currentItemId;
    private int currentLine;
    private String currentMaxVideoUserId;
    private int currentMode;
    private int currentPageNumber;
    private String currentPresenter;
    private int currentStatus;
    private boolean enableSync;
    private boolean hasOwner;
    private String lastMsgSessionId;
    private String lessonId;
    private int maxChangeNumber;
    private boolean meetingOver;
    private long noteId;
    private String notePageId;
    private String noteUserId;
    private String playAudioData;
    private String prevDocInfo;
    private long recordingId;
    private int recordingStatus;
    private int shareNoteStatus;
    private long tvBindUserId;
    private int tvOwnerDeviceType;
    private String tvOwnerMeetingId;
    private int tvOwnerMeetingItemId;
    private String tvOwnerMeetingLessonId;
    private int tvOwnerMeetingType;


	public long getCurrentItemId() {
        return currentItemId;
    }

    public void setCurrentItemId(long currentItemId) {
        this.currentItemId = currentItemId;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }

    public String getCurrentMaxVideoUserId() {
        return currentMaxVideoUserId;
    }

    public void setCurrentMaxVideoUserId(String currentMaxVideoUserId) {
        this.currentMaxVideoUserId = currentMaxVideoUserId;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public String getCurrentPresenter() {
        return currentPresenter;
    }

    public void setCurrentPresenter(String currentPresenter) {
        this.currentPresenter = currentPresenter;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

    public boolean isEnableSync() {
        return enableSync;
    }

    public void setEnableSync(boolean enableSync) {
        this.enableSync = enableSync;
    }

    public boolean isHasOwner() {
        return hasOwner;
    }

    public void setHasOwner(boolean hasOwner) {
        this.hasOwner = hasOwner;
    }

    public String getLastMsgSessionId() {
        return lastMsgSessionId;
    }

    public void setLastMsgSessionId(String lastMsgSessionId) {
        this.lastMsgSessionId = lastMsgSessionId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public int getMaxChangeNumber() {
        return maxChangeNumber;
    }

    public void setMaxChangeNumber(int maxChangeNumber) {
        this.maxChangeNumber = maxChangeNumber;
    }

    public boolean isMeetingOver() {
        return meetingOver;
    }

    public void setMeetingOver(boolean meetingOver) {
        this.meetingOver = meetingOver;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public String getNotePageId() {
        return notePageId;
    }

    public void setNotePageId(String notePageId) {
        this.notePageId = notePageId;
    }

    public String getNoteUserId() {
        return noteUserId;
    }

    public void setNoteUserId(String noteUserId) {
        this.noteUserId = noteUserId;
    }

    public String getPlayAudioData() {
        return playAudioData;
    }

    public void setPlayAudioData(String playAudioData) {
        this.playAudioData = playAudioData;
    }

    public String getPrevDocInfo() {
        return prevDocInfo;
    }

    public void setPrevDocInfo(String prevDocInfo) {
        this.prevDocInfo = prevDocInfo;
    }

    public long getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(long recordingId) {
        this.recordingId = recordingId;
    }

    public int getRecordingStatus() {
        return recordingStatus;
    }

    public void setRecordingStatus(int recordingStatus) {
        this.recordingStatus = recordingStatus;
    }

    public int getShareNoteStatus() {
        return shareNoteStatus;
    }

    public void setShareNoteStatus(int shareNoteStatus) {
        this.shareNoteStatus = shareNoteStatus;
    }

    public long getTvBindUserId() {
        return tvBindUserId;
    }

    public void setTvBindUserId(long tvBindUserId) {
        this.tvBindUserId = tvBindUserId;
    }

    public int getTvOwnerDeviceType() {
        return tvOwnerDeviceType;
    }

    public void setTvOwnerDeviceType(int tvOwnerDeviceType) {
        this.tvOwnerDeviceType = tvOwnerDeviceType;
    }

    public String getTvOwnerMeetingId() {
        return tvOwnerMeetingId;
    }

    public void setTvOwnerMeetingId(String tvOwnerMeetingId) {
        this.tvOwnerMeetingId = tvOwnerMeetingId;
    }

    public int getTvOwnerMeetingItemId() {
        return tvOwnerMeetingItemId;
    }

    public void setTvOwnerMeetingItemId(int tvOwnerMeetingItemId) {
        this.tvOwnerMeetingItemId = tvOwnerMeetingItemId;
    }

    public String getTvOwnerMeetingLessonId() {
        return tvOwnerMeetingLessonId;
    }

    public void setTvOwnerMeetingLessonId(String tvOwnerMeetingLessonId) {
        this.tvOwnerMeetingLessonId = tvOwnerMeetingLessonId;
    }

    public int getTvOwnerMeetingType() {
        return tvOwnerMeetingType;
    }

    public void setTvOwnerMeetingType(int tvOwnerMeetingType) {
        this.tvOwnerMeetingType = tvOwnerMeetingType;
    }

    @Override
    public String toString() {
        return "HelloMessage{" +
                "currentItemId=" + currentItemId +
                ", currentLine=" + currentLine +
                ", currentMaxVideoUserId='" + currentMaxVideoUserId + '\'' +
                ", currentMode=" + currentMode +
                ", currentPageNumber=" + currentPageNumber +
                ", currentPresenter='" + currentPresenter + '\'' +
                ", currentStatus=" + currentStatus +
                ", enableSync=" + enableSync +
                ", hasOwner=" + hasOwner +
                ", lastMsgSessionId='" + lastMsgSessionId + '\'' +
                ", lessonId='" + lessonId + '\'' +
                ", maxChangeNumber=" + maxChangeNumber +
                ", meetingOver=" + meetingOver +
                ", noteId=" + noteId +
                ", notePageId='" + notePageId + '\'' +
                ", noteUserId='" + noteUserId + '\'' +
                ", playAudioData='" + playAudioData + '\'' +
                ", prevDocInfo='" + prevDocInfo + '\'' +
                ", recordingId=" + recordingId +
                ", recordingStatus=" + recordingStatus +
                ", shareNoteStatus=" + shareNoteStatus +
                ", tvBindUserId=" + tvBindUserId +
                ", tvOwnerDeviceType=" + tvOwnerDeviceType +
                ", tvOwnerMeetingId='" + tvOwnerMeetingId + '\'' +
                ", tvOwnerMeetingItemId=" + tvOwnerMeetingItemId +
                ", tvOwnerMeetingLessonId='" + tvOwnerMeetingLessonId + '\'' +
                ", tvOwnerMeetingType=" + tvOwnerMeetingType +
                '}';
    }
}
