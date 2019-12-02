package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/10/11.
 */

public class RecordingPage {
    private String pageUrl;
    private String savedLocalPath;
    private int pageNumber;
    private long recordingTime;
    private boolean isDownloading;
    private String itemId;
    private String showUrl;

    public String getShowUrl() {
        return showUrl;
    }

    public void setShowUrl(String showUrl) {
        this.showUrl = showUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public long getRecordingTime() {
        return recordingTime;
    }

    public void setRecordingTime(long recordingTime) {
        this.recordingTime = recordingTime;
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


    @Override
    public String toString() {
        return "RecordingPage{" +
                "pageUrl='" + pageUrl + '\'' +
                ", savedLocalPath='" + savedLocalPath + '\'' +
                ", pageNumber=" + pageNumber +
                ", recordingTime=" + recordingTime +
                ", isDownloading=" + isDownloading +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordingPage page = (RecordingPage) o;

        return recordingTime == page.recordingTime;
    }

    @Override
    public int hashCode() {
        return (int) (recordingTime ^ (recordingTime >>> 32));
    }
}
