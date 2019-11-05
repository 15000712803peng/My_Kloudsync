package com.ub.techexcel.bean;

import java.io.Serializable;

public class Note implements Serializable {
    /**
     * 解决方案具体信息(片段)
     */
    private static final long serialVersionUID = 0x110;


    private int documentItemID;
    private int pageNumber;
    private String localFileID;
    private int noteID;
    private String title;
    private int attachmentFileID;
    private int attachmentID;
    private int fileID;
    private String fileName;
    private String attachmentUrl;
    private String sourceFileUrl;
    private String createdDate;
    private int status;
    private int linkID;

    public int getLinkID() {
        return linkID;
    }

    public void setLinkID(int linkID) {
        this.linkID = linkID;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getDocumentItemID() {
        return documentItemID;
    }

    public void setDocumentItemID(int documentItemID) {
        this.documentItemID = documentItemID;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getLocalFileID() {
        return localFileID;
    }

    public void setLocalFileID(String localFileID) {
        this.localFileID = localFileID;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAttachmentFileID() {
        return attachmentFileID;
    }

    public void setAttachmentFileID(int attachmentFileID) {
        this.attachmentFileID = attachmentFileID;
    }

    public int getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(int attachmentID) {
        this.attachmentID = attachmentID;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getSourceFileUrl() {
        return sourceFileUrl;
    }

    public void setSourceFileUrl(String sourceFileUrl) {
        this.sourceFileUrl = sourceFileUrl;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
