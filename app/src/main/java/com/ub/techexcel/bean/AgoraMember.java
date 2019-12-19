package com.ub.techexcel.bean;

import android.support.annotation.NonNull;
import android.view.SurfaceView;

import java.util.Objects;


public class AgoraMember implements Comparable<AgoraMember>{

    private SurfaceView surfaceView;
    private boolean muteAudio = true;  // true   禁止音频流
    private boolean muteVideo = true;  // true   禁止视频流
    private boolean isSelect;
    private String userName;
    private boolean isSelf;
    private boolean isAdd;
    private boolean isUserHide;
    private int userId;
    private String iconUrl;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isUserHide() {
        return isUserHide;
    }

    public void setUserHide(boolean userHide) {
        isUserHide = userHide;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public AgoraMember(){

    }

    public AgoraMember(int userId){
        this.userId = userId;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    public boolean isMuteAudio() {
        return muteAudio;
    }

    public void setMuteAudio(boolean muteAudio) {
        this.muteAudio = muteAudio;
    }

    public boolean isMuteVideo() {
        return muteVideo;
    }

    public void setMuteVideo(boolean muteVideo) {
        this.muteVideo = muteVideo;
    }



    @Override
    public int compareTo(@NonNull AgoraMember o) {
        if(isSelf){
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AgoraMember member = (AgoraMember) o;

        return userId == member.userId;
    }

    @Override
    public int hashCode() {
        return userId;
    }

    @Override
    public String toString() {
        return "AgoraMember{" +
                "surfaceView=" + surfaceView +
                ", muteAudio=" + muteAudio +
                ", muteVideo=" + muteVideo +
                ", isSelect=" + isSelect +
                ", userName='" + userName + '\'' +
                ", isSelf=" + isSelf +
                ", isAdd=" + isAdd +
                ", isUserHide=" + isUserHide +
                ", userId=" + userId +
                '}';
    }
}
