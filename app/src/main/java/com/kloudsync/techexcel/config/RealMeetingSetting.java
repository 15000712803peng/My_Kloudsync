package com.kloudsync.techexcel.config;

/**
 * Created by tonyan on 2019/11/28.
 */

public class RealMeetingSetting {

    private boolean isMicroOn;
    private boolean isRecordOn;
    private boolean isCameraOn;
    private int voiceStatus;

    public int getVoiceStatus() {
        return voiceStatus;
    }

    public void setVoiceStatus(int voiceStatus) {
        this.voiceStatus = voiceStatus;
    }

    public boolean isMicroOn() {
        return isMicroOn;
    }

    public void setMicroOn(boolean microOn) {
        isMicroOn = microOn;
    }

    public boolean isRecordOn() {
        return isRecordOn;
    }

    public void setRecordOn(boolean recordOn) {
        isRecordOn = recordOn;
    }

    public boolean isCameraOn() {
        return isCameraOn;
    }

    public void setCameraOn(boolean cameraOn) {
        isCameraOn = cameraOn;
    }
}
