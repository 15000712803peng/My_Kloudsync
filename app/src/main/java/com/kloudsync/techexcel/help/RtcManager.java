package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.kloudsync.techexcel.app.App;

import org.greenrobot.eventbus.EventBus;

import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.model.EngineConfig;
import io.agora.openlive.model.MyEngineEventHandler;
import io.agora.openlive.model.WorkerThread;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;

/**
 * Created by tonyan on 2019/11/28.
 */

public class RtcManager{

    private static RtcManager instance;
    private Activity host;

    private RtcManager(Activity host){
        this.host = host;
    }

    public void setHost(Activity host) {
        this.host = host;
    }

    public static RtcManager getDefault(Activity host) {
        if (instance == null) {
            synchronized (RtcManager.class) {
                if (instance == null) {
                    instance = new RtcManager(host);
                }
            }
        }
        return instance;
    }

    public RtcEngine rtcEngine() {
        return ((App) host.getApplication()).getWorkerThread().getRtcEngine();
    }

    public final WorkerThread worker() {
        return ((App) host.getApplication()).getWorkerThread();
    }

    public final EngineConfig config() {
        return ((App) host.getApplication()).getWorkerThread().getEngineConfig();
    }

    public final MyEngineEventHandler event() {
        return ((App) host.getApplication()).getWorkerThread().eventHandler();
    }

    public void release(){
        worker().leaveChannel(config().mChannel);
    }

    public void joinRtcChannle(String meetingId){
        worker().joinChannel(meetingId.toUpperCase(),config().mUid);
    }

    public void addEventHandler(AGEventHandler handler){
        event().addEventHandler(handler);
    }

    public void doConfigEngine(int cRole) {
        int vProfile = Constants.VIDEO_PROFILE_480P;
        worker().configEngine(cRole, vProfile);
        //启用说话者音量提示
        worker().getRtcEngine().enableAudioVolumeIndication(200, 3);
        worker().getRtcEngine().enableWebSdkInteroperability(true);
        //记录当前时间
        worker().getRtcEngine().enableVideo();
    }

}
