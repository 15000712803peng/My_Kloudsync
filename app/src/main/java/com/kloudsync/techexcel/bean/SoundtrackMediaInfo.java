package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/12/25.
 */

public class SoundtrackMediaInfo {

    private int ItemID;
    private String Title;
    private String Description;
    private int AttachmentFileID;
    private int AttachmentID;
    private int FileID;
    private String FileName;
    private String SourceFileName;
    private String AttachmentUrl;
    private String SourceFileUrl;
    private String CreatedDate;
    private int Status;
    private int PageCount;
    private String BlankPageNumber;
    private int FileType;
    private String VideoSize;
    private String VideoDuration;
    private boolean IsTemporary;
    private String QueryToken;
    private boolean isPreparing;
    private boolean isPrepared;
    private boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPreparing() {
        return isPreparing;
    }

    public void setPreparing(boolean preparing) {
        isPreparing = preparing;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setPrepared(boolean prepared) {
        isPrepared = prepared;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int itemID) {
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

    public int getAttachmentFileID() {
        return AttachmentFileID;
    }

    public void setAttachmentFileID(int attachmentFileID) {
        AttachmentFileID = attachmentFileID;
    }

    public int getAttachmentID() {
        return AttachmentID;
    }

    public void setAttachmentID(int attachmentID) {
        AttachmentID = attachmentID;
    }

    public int getFileID() {
        return FileID;
    }

    public void setFileID(int fileID) {
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

    public String getBlankPageNumber() {
        return BlankPageNumber;
    }

    public void setBlankPageNumber(String blankPageNumber) {
        BlankPageNumber = blankPageNumber;
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
}
