package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/12/24.
 */

public class SoundtrackDetail {

    private int SoundtrackID;
    private String Title;
    private int AttachmentID;
    private int NewAudioAttachmentID;
    private int SelectedAudioAttachmentID;
    private int BackgroudMusicAttachmentID;
    private int UserID;
    private String UserName;
    private String AvatarUrl;
    private String CreatedDate;
    private SoundtrackMediaInfo DocInfo;
    private SoundtrackMediaInfo NewAudioInfo;
    private SoundtrackMediaInfo SelectedAudioInfo;
    private SoundtrackMediaInfo BackgroudMusicInfo;
    //            "SoundtrackActionInfo":null,
    private int EnableBackgroud;
    private int EnableSelectVoice;
    private int EnableRecordNewVoice;
    private int SyncStatus;
    private String SyncDate;
    private String SelectedAudioTitle;
    private String BackgroudMusicTitle;
    private String NewAudioTitle;
    private long Duration;
    private int Type;
    private String PathInfo;
    private String BucketInfo;
    private int IsPublic;

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

    public int getAttachmentID() {
        return AttachmentID;
    }

    public void setAttachmentID(int attachmentID) {
        AttachmentID = attachmentID;
    }

    public int getNewAudioAttachmentID() {
        return NewAudioAttachmentID;
    }

    public void setNewAudioAttachmentID(int newAudioAttachmentID) {
        NewAudioAttachmentID = newAudioAttachmentID;
    }

    public int getSelectedAudioAttachmentID() {
        return SelectedAudioAttachmentID;
    }

    public void setSelectedAudioAttachmentID(int selectedAudioAttachmentID) {
        SelectedAudioAttachmentID = selectedAudioAttachmentID;
    }

    public int getBackgroudMusicAttachmentID() {
        return BackgroudMusicAttachmentID;
    }

    public void setBackgroudMusicAttachmentID(int backgroudMusicAttachmentID) {
        BackgroudMusicAttachmentID = backgroudMusicAttachmentID;
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

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public SoundtrackMediaInfo getDocInfo() {
        return DocInfo;
    }

    public void setDocInfo(SoundtrackMediaInfo docInfo) {
        DocInfo = docInfo;
    }

    public SoundtrackMediaInfo getNewAudioInfo() {
        return NewAudioInfo;
    }

    public void setNewAudioInfo(SoundtrackMediaInfo newAudioInfo) {
        NewAudioInfo = newAudioInfo;
    }

    public SoundtrackMediaInfo getSelectedAudioInfo() {
        return SelectedAudioInfo;
    }

    public void setSelectedAudioInfo(SoundtrackMediaInfo selectedAudioInfo) {
        SelectedAudioInfo = selectedAudioInfo;
    }

    public SoundtrackMediaInfo getBackgroudMusicInfo() {
        return BackgroudMusicInfo;
    }

    public void setBackgroudMusicInfo(SoundtrackMediaInfo backgroudMusicInfo) {
        BackgroudMusicInfo = backgroudMusicInfo;
    }

    public int getEnableBackgroud() {
        return EnableBackgroud;
    }

    public void setEnableBackgroud(int enableBackgroud) {
        EnableBackgroud = enableBackgroud;
    }

    public int getEnableSelectVoice() {
        return EnableSelectVoice;
    }

    public void setEnableSelectVoice(int enableSelectVoice) {
        EnableSelectVoice = enableSelectVoice;
    }

    public int getEnableRecordNewVoice() {
        return EnableRecordNewVoice;
    }

    public void setEnableRecordNewVoice(int enableRecordNewVoice) {
        EnableRecordNewVoice = enableRecordNewVoice;
    }

    public int getSyncStatus() {
        return SyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        SyncStatus = syncStatus;
    }

    public String getSyncDate() {
        return SyncDate;
    }

    public void setSyncDate(String syncDate) {
        SyncDate = syncDate;
    }

    public String getSelectedAudioTitle() {
        return SelectedAudioTitle;
    }

    public void setSelectedAudioTitle(String selectedAudioTitle) {
        SelectedAudioTitle = selectedAudioTitle;
    }

    public String getBackgroudMusicTitle() {
        return BackgroudMusicTitle;
    }

    public void setBackgroudMusicTitle(String backgroudMusicTitle) {
        BackgroudMusicTitle = backgroudMusicTitle;
    }

    public String getNewAudioTitle() {
        return NewAudioTitle;
    }

    public void setNewAudioTitle(String newAudioTitle) {
        NewAudioTitle = newAudioTitle;
    }

    public long getDuration() {
        return Duration;
    }

    public void setDuration(long duration) {
        Duration = duration;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getPathInfo() {
        return PathInfo;
    }

    public void setPathInfo(String pathInfo) {
        PathInfo = pathInfo;
    }

    public String getBucketInfo() {
        return BucketInfo;
    }

    public void setBucketInfo(String bucketInfo) {
        BucketInfo = bucketInfo;
    }

    public int getIsPublic() {
        return IsPublic;
    }

    public void setIsPublic(int isPublic) {
        IsPublic = isPublic;
    }
}
