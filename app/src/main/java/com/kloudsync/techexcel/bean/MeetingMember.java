package com.kloudsync.techexcel.bean;

import android.support.annotation.NonNull;

import com.kloudsync.techexcel.config.AppConfig;

import java.io.Serializable;

/**
 * Created by tonyan on 2019/12/8.
 */

public class MeetingMember implements Comparable<MeetingMember>,Serializable {

    private int userId;
    private String userName;
    private int rongCloudId;
    private String avatarUrl;
    private String sessionId;
    private int role;
    private int presenter;
    private int isOnline;
    private int agoraStatus;
    private int microphoneStatus;
    private int cameraStatus;
    private int deviceType = -1;
    private int handStatus;
    private int tempOnStage;
    private int viewType;
    private String title;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static final int TYPE_SPARKER_TITLE = 1;
    public static final int TYPE_HANDSUP_TITLE = 2;
    public static final int TYPE_ITEM_MAIN_SPEAKER = 3;
    public static final int TYPE_ITEM_SPEAKING_SPEAKER = 4;
    public static final int TYPE_ITEM_HANDSUP_MEMBER = 5;

    public int getTempOnStage() {
        return tempOnStage;
    }

    public void setTempOnStage(int tempOnStage) {
        this.tempOnStage = tempOnStage;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHandStatus() {
        return handStatus;
    }

    public void setHandStatus(int handStatus) {
        this.handStatus = handStatus;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRongCloudId() {
        return rongCloudId;
    }

    public void setRongCloudId(int rongCloudId) {
        this.rongCloudId = rongCloudId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getPresenter() {
        return presenter;
    }

    public void setPresenter(int presenter) {
        this.presenter = presenter;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public int getAgoraStatus() {
        return agoraStatus;
    }

    public void setAgoraStatus(int agoraStatus) {
        this.agoraStatus = agoraStatus;
    }

    public int getMicrophoneStatus() {
        return microphoneStatus;
    }

    public void setMicrophoneStatus(int microphoneStatus) {
        this.microphoneStatus = microphoneStatus;
    }

    public int getCameraStatus() {
        return cameraStatus;
    }

    public void setCameraStatus(int cameraStatus) {
        this.cameraStatus = cameraStatus;
    }

    @Override
    public String toString() {
        return "MeetingMember{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", rongCloudId=" + rongCloudId +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", role=" + role +
                ", presenter=" + presenter +
                ", isOnline=" + isOnline +
                ", agoraStatus=" + agoraStatus +
                ", microphoneStatus=" + microphoneStatus +
                ", cameraStatus=" + cameraStatus +
                '}';
    }

    @Override
    public int compareTo(@NonNull MeetingMember o) {
        int sort = o.getRole() - this.getRole();

        if(sort == 0){
            sort = this.presenter;
        }

        if(sort == 0){
            if((this.userId + "").equals(AppConfig.UserID)){
                sort = 1;
            }
        }
        return sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingMember that = (MeetingMember) o;

        return userId == that.userId;
    }

    @Override
    public int hashCode() {
        return userId;
    }

    public MeetingMember(int userId) {
        this.userId = userId;
    }

    public MeetingMember() {
        this.userId = userId;
    }


}
