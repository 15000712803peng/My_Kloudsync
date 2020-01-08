package com.kloudsync.techexcel.bean;

import java.io.Serializable;

/**
 * Created by tonyan on 2019/10/11.
 */

public class DocumentPage implements Serializable{
    private int documentId;
    private int fileId;
    private String pageUrl;
    private String savedLocalPath;
    private String showingPath;
    private int pageNumber;
    private String localFileId;

    public String getLocalFileId() {
        return localFileId;
    }

    public void setLocalFileId(String localFileId) {
        this.localFileId = localFileId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getSavedLocalPath() {
        return savedLocalPath;
    }

    public void setSavedLocalPath(String savedLocalPath) {
        this.savedLocalPath = savedLocalPath;
    }

    public String getShowingPath() {
        return showingPath;
    }

    public void setShowingPath(String showingPath) {
        this.showingPath = showingPath;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentPage page = (DocumentPage) o;

        if (documentId != page.documentId) return false;
        return pageUrl != null ? pageUrl.equals(page.pageUrl) : page.pageUrl == null;
    }

    @Override
    public int hashCode() {
        int result = documentId;
        result = 31 * result + (pageUrl != null ? pageUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DocumentPage{" +
                "documentId=" + documentId +
                ", fileId=" + fileId +
                ", pageUrl='" + pageUrl + '\'' +
                ", savedLocalPath='" + savedLocalPath + '\'' +
                ", showingPath='" + showingPath + '\'' +
                ", pageNumber=" + pageNumber +
                ", localFileId='" + localFileId + '\'' +
                '}';
    }
}
