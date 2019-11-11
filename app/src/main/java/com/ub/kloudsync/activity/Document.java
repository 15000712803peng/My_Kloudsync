package com.ub.kloudsync.activity;

import com.ub.techexcel.bean.SoundtrackBean;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Document implements Serializable {

    private String SpaceID;
    private int SyncCount;
    private String ItemID;
    private String Title;
    private String Description;
    private String AttachmentFileID;
    private String AttachmentID = "0";
    private String FileID;
    private String FileName;
    private String SourceFileName;
    private String AttachmentUrl;
    private String SourceFileUrl;
    private String CreatedDate;
    private int Status;
    private int PageCount;
    private int FileType;
    private String VideoSize;
    String VideoDuration;
    private boolean IsTemporary;
    private String QueryToken;
    private boolean me;
    private String UserID;
    private String SchoolID;
    private String LessonId;
    private List<SoundtrackBean> soundSync;
    private int progress;
    private String size;
    private String duration;
    private JSONObject jsonObject;
    private String FileDownloadURL;
    private int IncidentID;
    private boolean isSyncExpanded;
    private String CreatedByName;
    private int tempItemId;

    public int getTempItemId() {
        return tempItemId;
    }

    public void setTempItemId(int tempItemId) {
        this.tempItemId = tempItemId;
    }

    public String getCreatedByName() {
        return CreatedByName;
    }

    public void setCreatedByName(String createdByName) {
        CreatedByName = createdByName;
    }

    public boolean isSyncExpanded() {
        return isSyncExpanded;
    }

    public void setSyncExpanded(boolean syncExpanded) {
        isSyncExpanded = syncExpanded;
    }

    public int getIncidentID() {
        return IncidentID;
    }

    public void setIncidentID(int incidentID) {
        IncidentID = incidentID;
    }

    public String getFileDownloadURL() {
        return FileDownloadURL;
    }

    public void setFileDownloadURL(String fileDownloadURL) {
        FileDownloadURL = fileDownloadURL;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    private int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public List<SoundtrackBean> getSoundSync() {
        return soundSync;
    }

    public void setSoundSync(List<SoundtrackBean> soundSync) {
        this.soundSync = soundSync;
    }

    public String getLessonId() {
        return LessonId;
    }

    public void setLessonId(String lessonId) {
        LessonId = lessonId;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(String schoolID) {
        SchoolID = schoolID;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }

    public String getSpaceID() {
        return SpaceID;
    }

    public void setSpaceID(String spaceID) {
        SpaceID = spaceID;
    }

    public int getSyncCount() {
        return SyncCount;
    }

    public void setSyncCount(int syncCount) {
        SyncCount = syncCount;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAttachmentFileID() {
        return AttachmentFileID;
    }

    public void setAttachmentFileID(String attachmentFileID) {
        AttachmentFileID = attachmentFileID;
    }

    public String getAttachmentID() {
        return AttachmentID;
    }

    public void setAttachmentID(String attachmentID) {
        AttachmentID = attachmentID;
    }

    public String getFileID() {
        return FileID;
    }

    public void setFileID(String fileID) {
        FileID = fileID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getSourceFileName() {
        return SourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        SourceFileName = sourceFileName;
    }

    public String getAttachmentUrl() {
        return AttachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        AttachmentUrl = attachmentUrl;
    }

    public String getSourceFileUrl() {
        return SourceFileUrl;
    }

    public void setSourceFileUrl(String sourceFileUrl) {
        SourceFileUrl = sourceFileUrl;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int pageCount) {
        PageCount = pageCount;
    }

    public int getFileType() {
        return FileType;
    }

    public void setFileType(int fileType) {
        FileType = fileType;
    }

    public String getVideoSize() {
        return VideoSize;
    }

    public void setVideoSize(String videoSize) {
        VideoSize = videoSize;
    }

    public String getVideoDuration() {
        return VideoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        VideoDuration = videoDuration;
    }

    public boolean isTemporary() {
        return IsTemporary;
    }

    public void setTemporary(boolean temporary) {
        IsTemporary = temporary;
    }

    public String getQueryToken() {
        return QueryToken;
    }

    public void setQueryToken(String queryToken) {
        QueryToken = queryToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return SpaceID.equals(document.SpaceID) &&
                ItemID.equals(document.ItemID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SpaceID, ItemID);
    }
}
