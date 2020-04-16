package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/12/24.
 */

public class SoundTrack {

    private int SoundtrackID;
    private String Title;
    private int UserID;
    private String UserName;
    private String AvatarUrl;
    private String Duration;
    private String CreatedDate;
    private int SyncStatus;
	private int MusicType;

	public int getMusicType() {
		return MusicType;
	}

	public void setMusicType(int musicType) {
		MusicType = musicType;
	}

    public int getSoundtrackID() {
        return SoundtrackID;
    }

    public void setSoundtrackID(int soundtrackID) {
        SoundtrackID = soundtrackID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getSyncStatus() {
        return SyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        SyncStatus = syncStatus;
    }
}
