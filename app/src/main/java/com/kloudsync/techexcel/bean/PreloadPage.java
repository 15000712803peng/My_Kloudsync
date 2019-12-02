package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/10/11.
 */

public class PreloadPage {
    private String pageUrl;
    private String savedLocalPath;
    private int pageNumber;
    private boolean isDownloading;
    private String notifyUrl;


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

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PreloadPage that = (PreloadPage) o;

        if (pageNumber != that.pageNumber) return false;
        return pageUrl.equals(that.pageUrl);
    }

    @Override
    public int hashCode() {
        int result = pageUrl.hashCode();
        result = 31 * result + pageNumber;
        return result;
    }

    @Override
    public String toString() {
        return "PreloadPage{" +
                "pageUrl='" + pageUrl + '\'' +
                ", savedLocalPath='" + savedLocalPath + '\'' +
                ", pageNumber=" + pageNumber +
                ", isDownloading=" + isDownloading +
                ", notifyUrl='" + notifyUrl + '\'' +
                '}';
    }
}
