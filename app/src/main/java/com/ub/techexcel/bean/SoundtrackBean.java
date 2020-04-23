package com.ub.techexcel.bean;

import com.ub.kloudsync.activity.Document;

import java.io.Serializable;

public class SoundtrackBean implements Serializable {


    private int soundtrackID;
    private String title;
    private String userID;
    private String userName;
    private String avatarUrl;
    private String duration;
    private int attachmentId;

    private boolean isCheck = false;
    private boolean isHidden = false;

    private int newAudioAttachmentID;
    private int selectedAudioAttachmentID;
    private int backgroudMusicAttachmentID;

    private Document newAudioInfo;
    private Document selectedAudioInfo;
    private Document backgroudMusicInfo;

    private String backgroudMusicTitle, selectedAudioTitle, newAudioTitle;
    private String createdDate;
    private int fileId;
    private String path;
    private int isPublic ;
	private int actionBaseSoundtrackID;
	private int MusicType;
	private int voiceQuality;

    public int getVoiceQuality() {
        return voiceQuality;
    }

    public void setVoiceQuality(int voiceQuality) {
        this.voiceQuality = voiceQuality;
    }

    public int getMusicType() {
		return MusicType;
	}

	public void setMusicType(int musicType) {
		MusicType = musicType;
	}

	public int getActionBaseSoundtrackID() {
		return actionBaseSoundtrackID;
	}

	public void setActionBaseSoundtrackID(int actionBaseSoundtrackID) {
		this.actionBaseSoundtrackID = actionBaseSoundtrackID;
	}

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getBackgroudMusicTitle() {
        return backgroudMusicTitle;
    }

    public void setBackgroudMusicTitle(String backgroudMusicTitle) {
        this.backgroudMusicTitle = backgroudMusicTitle;
    }

    public String getSelectedAudioTitle() {
        return selectedAudioTitle;
    }

    public void setSelectedAudioTitle(String selectedAudioTitle) {
        this.selectedAudioTitle = selectedAudioTitle;
    }

    public String getNewAudioTitle() {
        return newAudioTitle;
    }

    public void setNewAudioTitle(String newAudioTitle) {
        this.newAudioTitle = newAudioTitle;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public int getNewAudioAttachmentID() {
        return newAudioAttachmentID;
    }

    public void setNewAudioAttachmentID(int newAudioAttachmentID) {
        this.newAudioAttachmentID = newAudioAttachmentID;
    }

    public int getSelectedAudioAttachmentID() {
        return selectedAudioAttachmentID;
    }

    public void setSelectedAudioAttachmentID(int selectedAudioAttachmentID) {
        this.selectedAudioAttachmentID = selectedAudioAttachmentID;
    }

    public int getBackgroudMusicAttachmentID() {
        return backgroudMusicAttachmentID;
    }

    public void setBackgroudMusicAttachmentID(int backgroudMusicAttachmentID) {
        this.backgroudMusicAttachmentID = backgroudMusicAttachmentID;
    }

    public Document getNewAudioInfo() {
        return newAudioInfo;
    }

    public void setNewAudioInfo(Document newAudioInfo) {
        this.newAudioInfo = newAudioInfo;
    }

    public Document getSelectedAudioInfo() {
        return selectedAudioInfo;
    }

    public void setSelectedAudioInfo(Document selectedAudioInfo) {
        this.selectedAudioInfo = selectedAudioInfo;
    }

    public Document getBackgroudMusicInfo() {
        return backgroudMusicInfo;
    }

    public void setBackgroudMusicInfo(Document backgroudMusicInfo) {
        this.backgroudMusicInfo = backgroudMusicInfo;
    }

    public int getSoundtrackID() {
        return soundtrackID;
    }

    public void setSoundtrackID(int soundtrackID) {
        this.soundtrackID = soundtrackID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    private boolean havePresenter;

    public void setHavePresenter(boolean havePresenter) {
        this.havePresenter = havePresenter;
    }

    public boolean isHavePresenter() {
        return havePresenter;
    }
}
