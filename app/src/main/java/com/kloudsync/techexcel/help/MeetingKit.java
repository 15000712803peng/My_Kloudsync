package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.ub.techexcel.adapter.AgoraCameraAdapter;
import com.ub.techexcel.bean.AgoraBean;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.bean.AgoraUser;
import com.ub.techexcel.tools.MeetingSettingDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import io.agora.openlive.model.AGEventHandler;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;


public class MeetingKit implements MeetingSettingDialog.OnUserOptionsListener, AGEventHandler ,View.OnClickListener,PopMeetingMenu.MeetingMenuOperationsListener{

    private static MeetingKit kit;

    private MeetingConfig meetingConfig;

    private MeetingSettingDialog settingDialog;

    private Activity host;

    private String newMeetingId;

    //----
    private RtcManager rtcManager;
    private ImageView menu;
    private PopMeetingMenu popMeetingMenu;
    private boolean isStarted;
    private AgoraCameraAdapter cameraAdapter;


    public void setCameraAdapter(AgoraCameraAdapter cameraAdapter) {
        this.cameraAdapter = cameraAdapter;
    }

    public void setMenu(ImageView menu) {
        this.menu = menu;
    }

    private MeetingKit() {

    }

    public static MeetingKit getInstance() {
        if (kit == null) {
            synchronized (MeetingKit.class) {
                if (kit == null) {
                    kit = new MeetingKit();
                }
            }
        }
        return kit;
    }

    private RtcManager getRtcManager() {
        if (rtcManager == null) {
            return RtcManager.getDefault(host);
        }
        return rtcManager;
    }

    public void prepareStart(Activity host, MeetingConfig meetingConfig, String newMeetingId) {
        this.host = host;
        this.newMeetingId = newMeetingId;
        this.meetingConfig = meetingConfig;
        ((App) host.getApplication()).initWorkerThread();
        getRtcManager().setHost(host);
        getRtcManager().addEventHandler(this);
        if (settingDialog != null) {
            if (settingDialog.isShowing()) {
                settingDialog.dismiss();
            }
            settingDialog = null;
        }
        settingDialog = new MeetingSettingDialog(host);
        settingDialog.setOnUserOptionsListener(this);
        if (settingDialog.isShowing()) {
            return;
        }
        settingDialog.show();
    }

    public void startMeeting() {
        Log.e("MeetingKit", "start_meeting");
        meetingConfig.setRole(MeetingConfig.MeetingRole.HOST);
        meetingConfig.setMeetingId(newMeetingId);
        rtcManager = RtcManager.getDefault(host);
        rtcManager.doConfigEngine(CLIENT_ROLE_BROADCASTER);
        rtcManager.joinRtcChannle(meetingConfig.getMeetingId());

    }

    @Override
    public void onUserStart() {
        Observable.just(newMeetingId).observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                JSONObject response = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/UpgradeToNormalLesson?lessonID=" + newMeetingId, null);
                if (response != null && response.getInt("RetCode") == 0) {
                    SocketMessageManager.getManager(host).sendMessage_startMeeting(meetingConfig, newMeetingId);
                }
            }
        }).subscribe();
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        isStarted = true;
        if (meetingConfig != null) {
            meetingConfig.setInRealMeeting(true);
        }
        try {
            getRtcManager().worker().getEngineConfig().mUid = uid;
        } catch (Exception e) {
            Log.e("MeetingKit","mUid = uid:" + e);
        }
        EventBus.getDefault().post(createSelfCamera(uid));
//        Log.e("MeetingKit", "onJoinChannelSuccess,uid:" + uid + ",elapsed:" + elapsed);
    }

    @Override
    public void onUserOffline(int uid, int reason) {

    }

    @Override
    public void onAudioRouteChanged(int routing) {
        if(popMeetingMenu != null && popMeetingMenu.isShowing()){
            popMeetingMenu.onAudioRouteChanged(routing);
        }
    }

    @Override
    public void onUserMuteVideo(int uid, boolean muted) {

    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {

    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {

    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.e("MeetingKit","onUserJoined,uid:" + uid);
        if (meetingConfig != null) {
            if(!meetingConfig.isInRealMeeting()){
                return;
            }
            EventBus.getDefault().post(createMemberCamera(uid));
        }
    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

    }

    private AgoraMember createMemberCamera(int uid) {
        AgoraMember member = new AgoraMember();
        member.setId(uid);
        member.setMuteVideo(false);
        SurfaceView surfaceV = RtcEngine.CreateRendererView(host.getApplicationContext());
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);
        getRtcManager().rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        member.setSurfaceView(surfaceV);
        return member;
    }


    private AgoraMember createSelfCamera(int uid) {
        AgoraMember member = new AgoraMember();
        member.setId(uid);
        boolean isMute = !MeetingSettingCache.getInstance(host).getMeetingSetting().isCameraOn();
        member.setMuteVideo(isMute);
        SurfaceView surfaceV = RtcEngine.CreateRendererView(host.getApplicationContext());
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);
        getRtcManager().rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        getRtcManager().worker().getRtcEngine().muteLocalVideoStream(isMute);

        member.setSurfaceView(surfaceV);
        return member;
    }

    public void release(){
        if(settingDialog != null){
            if(settingDialog.isShowing()){
                settingDialog.dismiss();
                settingDialog = null;
            }
        }
        try {
            getRtcManager().rtcEngine().leaveChannel();
            getRtcManager().event().removeEventHandler(this);
        }catch (Exception e){
            Log.e("MeetingKit","release exception:" + e);
        }
        isStarted = false;
        kit = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    public void showMeetingMenu(ImageView menu,Activity host,MeetingConfig meetingConfig) {
        Log.e("PopMeetingMenu","showMeetingMenu");
        this.meetingConfig =  meetingConfig;
        this.host = host;
        if (popMeetingMenu != null) {
            if (popMeetingMenu.isShowing()) {
                popMeetingMenu.hide();
                popMeetingMenu = null;
            }
        }
        popMeetingMenu = new PopMeetingMenu(host);
        popMeetingMenu.show(host,menu,this);
    }

    // --- meeting menu

    @Override
    public void menuClosedClicked() {

    }

    @Override
    public void menuCameraClicked(boolean isCameraOn) {
        Log.e("menuCameraClicked","mute_locacl_video:" + !isCameraOn);
        getRtcManager().worker().getRtcEngine().muteLocalVideoStream(!isCameraOn);
        if(cameraAdapter != null){
            cameraAdapter.muteOrOpenCamera(getRtcManager().worker().getEngineConfig().mUid,!isCameraOn);
        }
    }

    @Override
    public void menuSwitchCamera() {
        getRtcManager().worker().getRtcEngine().switchCamera();
    }


}
