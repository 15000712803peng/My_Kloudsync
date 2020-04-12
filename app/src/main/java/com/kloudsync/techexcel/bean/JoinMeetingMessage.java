package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/2/6.
 */

public class JoinMeetingMessage {

    private String invitedUserIds;
    private String userId;
    private String sessionId;
    private int presentStatus;
    private String phoneInfo;
    private String presenterSessionId;
    private int isAuditor;
    private int status;
    private String CurrentDocumentPage;
    private int currentMode;
    private String currentMaxVideoUserId;
    private int currentLine;
    private int prepareMode;
    private String playAudioData;
    private int hideCamera;
    private long recordingId;
    private int sizeMode;
    private int  type;
    private String lessonId;
    private int syncMode;
    private boolean isAck;
    private String prevDocInfo;
    private int  audienceCount;
    private int  memberInMeetingCount;
    private int  inviteNotJoinCount;
    private long noteId;
    private String notePageId;
    private String noteUserId;
    private int recordingStatus;
    private int shareNoteStatus;
	private boolean ifPause;
	private String pauseMsg;
	private long pauseDuration;



    public String getCurrentMaxVideoUserId() {
        return currentMaxVideoUserId;
    }

    public void setCurrentMaxVideoUserId(String currentMaxVideoUserId) {
        this.currentMaxVideoUserId = currentMaxVideoUserId;
    }

    public String getInvitedUserIds() {
        return invitedUserIds;
    }

    public void setInvitedUserIds(String invitedUserIds) {
        this.invitedUserIds = invitedUserIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(int presentStatus) {
        this.presentStatus = presentStatus;
    }

    public String getPhoneInfo() {
        return phoneInfo;
    }

    public void setPhoneInfo(String phoneInfo) {
        this.phoneInfo = phoneInfo;
    }

    public String getPresenterSessionId() {
        return presenterSessionId;
    }

    public void setPresenterSessionId(String presenterSessionId) {
        this.presenterSessionId = presenterSessionId;
    }

    public int getIsAuditor() {
        return isAuditor;
    }

    public void setIsAuditor(int isAuditor) {
        this.isAuditor = isAuditor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCurrentDocumentPage() {
        return CurrentDocumentPage;
    }

    public void setCurrentDocumentPage(String currentDocumentPage) {
        CurrentDocumentPage = currentDocumentPage;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }

    public int getPrepareMode() {
        return prepareMode;
    }

    public void setPrepareMode(int prepareMode) {
        this.prepareMode = prepareMode;
    }

    public String getPlayAudioData() {
        return playAudioData;
    }

    public void setPlayAudioData(String playAudioData) {
        this.playAudioData = playAudioData;
    }

    public int getHideCamera() {
        return hideCamera;
    }

    public void setHideCamera(int hideCamera) {
        this.hideCamera = hideCamera;
    }

    public long getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(long recordingId) {
        this.recordingId = recordingId;
    }

    public int getSizeMode() {
        return sizeMode;
    }

    public void setSizeMode(int sizeMode) {
        this.sizeMode = sizeMode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public int getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(int syncMode) {
        this.syncMode = syncMode;
    }

    public boolean isAck() {
        return isAck;
    }

    public void setAck(boolean ack) {
        isAck = ack;
    }

    public String getPrevDocInfo() {
        return prevDocInfo;
    }

    public void setPrevDocInfo(String prevDocInfo) {
        this.prevDocInfo = prevDocInfo;
    }

    public int getAudienceCount() {
        return audienceCount;
    }

    public void setAudienceCount(int audienceCount) {
        this.audienceCount = audienceCount;
    }

    public int getMemberInMeetingCount() {
        return memberInMeetingCount;
    }

    public void setMemberInMeetingCount(int memberInMeetingCount) {
        this.memberInMeetingCount = memberInMeetingCount;
    }

    public int getInviteNotJoinCount() {
        return inviteNotJoinCount;
    }

    public void setInviteNotJoinCount(int inviteNotJoinCount) {
        this.inviteNotJoinCount = inviteNotJoinCount;
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

	public boolean isIfPause() {
		return ifPause;
	}

	public void setIfPause(boolean ifPause) {
		this.ifPause = ifPause;
	}

	public String getPauseMsg() {
		return pauseMsg;
	}

	public void setPauseMsg(String pauseMsg) {
		this.pauseMsg = pauseMsg;
	}

	public long getPauseDuration() {
		return pauseDuration;
	}

	public void setPauseDuration(long pauseDuration) {
		this.pauseDuration = pauseDuration;
	}


    @Override
    public String toString() {
        return "JoinMeetingMessage{" +
                "invitedUserIds='" + invitedUserIds + '\'' +
                ", userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", presentStatus=" + presentStatus +
                ", phoneInfo='" + phoneInfo + '\'' +
                ", presenterSessionId='" + presenterSessionId + '\'' +
                ", isAuditor=" + isAuditor +
                ", status=" + status +
                ", CurrentDocumentPage='" + CurrentDocumentPage + '\'' +
                ", currentMode=" + currentMode +
		        ", currentMaxVideoUserId='" + currentMaxVideoUserId + '\'' +
                ", currentLine=" + currentLine +
                ", prepareMode=" + prepareMode +
                ", playAudioData='" + playAudioData + '\'' +
                ", hideCamera=" + hideCamera +
                ", recordingId=" + recordingId +
                ", sizeMode=" + sizeMode +
                ", type=" + type +
                ", lessonId='" + lessonId + '\'' +
                ", syncMode=" + syncMode +
                ", isAck=" + isAck +
                ", prevDocInfo='" + prevDocInfo + '\'' +
                ", audienceCount=" + audienceCount +
                ", memberInMeetingCount=" + memberInMeetingCount +
                ", inviteNotJoinCount=" + inviteNotJoinCount +
                ", noteId=" + noteId +
                ", notePageId='" + notePageId + '\'' +
                ", noteUserId='" + noteUserId + '\'' +
                ", recordingStatus=" + recordingStatus +
                ", shareNoteStatus=" + shareNoteStatus +
		        ", ifPause=" + ifPause +
		        ", pauseMsg='" + pauseMsg + '\'' +
		        ", pauseDuration=" + pauseDuration +
                '}';
    }
}
