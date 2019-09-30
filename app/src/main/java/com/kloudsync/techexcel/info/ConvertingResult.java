package com.kloudsync.techexcel.info;

import java.io.Serializable;

public class ConvertingResult implements Serializable{



    private int currentStatus;

    private int finishPercent;

    private  String url;


    private String folderKey;

    private int count;

    private String fileName;


    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

    public int getFinishPercent() {
        return finishPercent;
    }

    public void setFinishPercent(int finishPercent) {
        this.finishPercent = finishPercent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFolderKey() {
        return folderKey;
    }

    public void setFolderKey(String folderKey) {
        this.folderKey = folderKey;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
