package com.ub.service.activity;

import com.ub.techexcel.bean.AgoraBean;

import io.agora.openlive.ui.VideoViewEventListener;

/**
 * Created by wang on 2018/9/26.
 */

public abstract class VideoControlCallback implements VideoViewEventListener {
    @Override
    public void onItemDoubleClick(Object item) {

    }

    @Override
    public void onSwitchVideo(AgoraBean item) {

    }

    @Override
    public void isEnlarge(AgoraBean user) {

    }

    @Override
    public void closeOtherAudio(AgoraBean user) {

    }

    @Override
    public void closeOtherVideo(AgoraBean user) {

    }

    @Override
    public void openMyAudio(AgoraBean user) {

    }

    @Override
    public void openMyVideo(AgoraBean user) {

    }

}
