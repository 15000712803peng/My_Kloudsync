package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/10/31.
 */

public class NoteDetail {

    int LinkID;
    int NoteID;
    String LinkProperty;
    int DocumentItemID;
    int PageNumber;
    String LocalFileID;
    String Title;
    String Description;
    int AttachmentFileID;
    int AttachmentID;
    int FileID;
    String FileName;
    String SourceFileName;
    String AttachmentUrl;
    String SourceFileUrl;
    String CreatedDate;
    String Status;

    public int getLinkID() {
        return LinkID;
    }

    public void setLinkID(int linkID) {
        LinkID = linkID;
    }

    public int getNoteID() {
        return NoteID;
    }

    public void setNoteID(int noteID) {
        NoteID = noteID;
    }

    public String getLinkProperty() {
        return LinkProperty;
    }

    public void setLinkProperty(String linkProperty) {
        LinkProperty = linkProperty;
    }

    public int getDocumentItemID() {
        return DocumentItemID;
    }

    public void setDocumentItemID(int documentItemID) {
        DocumentItemID = documentItemID;
    }

    public int getPageNumber() {
        return PageNumber;
    }

    public void setPageNumber(int pageNumber) {
        PageNumber = pageNumber;
    }

    public String getLocalFileID() {
        return LocalFileID;
    }

    public void setLocalFileID(String localFileID) {
        LocalFileID = localFileID;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
