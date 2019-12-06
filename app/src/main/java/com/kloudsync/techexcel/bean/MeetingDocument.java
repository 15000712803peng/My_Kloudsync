package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2019/10/10.
 */

public class MeetingDocument {
    private String LessonID;
    private int IsAttendeeUpload;
    private int CourseID;
    private int LectureID;
    private int SyncCount;
    private int NewStatus;
    private String NewPath;
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
    private List<DocumentPage> documentPages;
    private boolean isSelect;
    private boolean isTemp;
    private int progress;
    private String tempDocPrompt;

    public String getTempDocPrompt() {
        return tempDocPrompt;
    }

    public void setTempDocPrompt(String tempDocPrompt) {
        this.tempDocPrompt = tempDocPrompt;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public MeetingDocument() {

    }

    public MeetingDocument(int itemID) {
        ItemID = itemID;
    }

    public String getLessonID() {
        return LessonID;
    }

    public void setLessonID(String lessonID) {
        LessonID = lessonID;
    }

    public int getIsAttendeeUpload() {
        return IsAttendeeUpload;
    }

    public void setIsAttendeeUpload(int isAttendeeUpload) {
        IsAttendeeUpload = isAttendeeUpload;
    }

    public int getCourseID() {
        return CourseID;
    }

    public void setCourseID(int courseID) {
        CourseID = courseID;
    }

    public int getLectureID() {
        return LectureID;
    }

    public void setLectureID(int lectureID) {
        LectureID = lectureID;
    }

    public int getSyncCount() {
        return SyncCount;
    }

    public void setSyncCount(int syncCount) {
        SyncCount = syncCount;
    }

    public int getNewStatus() {
        return NewStatus;
    }

    public void setNewStatus(int newStatus) {
        NewStatus = newStatus;
    }

    public String getNewPath() {
        return NewPath;
    }

    public void setNewPath(String newPath) {
        NewPath = newPath;
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

    public List<DocumentPage> getDocumentPages() {
        return documentPages;
    }

    public void setDocumentPages(List<DocumentPage> documentPages) {
        this.documentPages = documentPages;
    }

    @Override
    public String toString() {
        return "MeetingFile{" +
                "LessonID='" + LessonID + '\'' +
                ", IsAttendeeUpload=" + IsAttendeeUpload +
                ", CourseID=" + CourseID +
                ", LectureID=" + LectureID +
                ", SyncCount=" + SyncCount +
                ", NewStatus=" + NewStatus +
                ", NewPath='" + NewPath + '\'' +
                ", ItemID=" + ItemID +
                ", Title='" + Title + '\'' +
                ", Description='" + Description + '\'' +
                ", AttachmentFileID=" + AttachmentFileID +
                ", AttachmentID=" + AttachmentID +
                ", FileID=" + FileID +
                ", FileName='" + FileName + '\'' +
                ", SourceFileName='" + SourceFileName + '\'' +
                ", AttachmentUrl='" + AttachmentUrl + '\'' +
                ", SourceFileUrl='" + SourceFileUrl + '\'' +
                ", CreatedDate='" + CreatedDate + '\'' +
                ", Status=" + Status +
                ", PageCount=" + PageCount +
                ", BlankPageNumber='" + BlankPageNumber + '\'' +
                ", FileType=" + FileType +
                ", VideoSize='" + VideoSize + '\'' +
                ", VideoDuration='" + VideoDuration + '\'' +
                ", IsTemporary=" + IsTemporary +
                ", QueryToken='" + QueryToken + '\'' +
                ", documentPages=" + documentPages +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingDocument document = (MeetingDocument) o;

        return ItemID == document.ItemID;
    }

    @Override
    public int hashCode() {
        return ItemID;
    }
}
