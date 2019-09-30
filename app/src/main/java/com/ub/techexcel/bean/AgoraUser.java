package com.ub.techexcel.bean;

import android.view.SurfaceView;

import java.util.Objects;


public class AgoraUser {

    private String id;
    private SurfaceView surfaceView;
    private boolean muteAudio;  // true   禁止音频流
    private boolean muteVideo;  // true   禁止视频流
    private boolean isSelect;
    private String userName;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgoraUser agoraUser = (AgoraUser) o;
        return Objects.equals(id, agoraUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
