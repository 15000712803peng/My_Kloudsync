package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EventCloseShare;
import com.kloudsync.techexcel.bean.EventExit;
import com.kloudsync.techexcel.bean.EventMute;
import com.kloudsync.techexcel.bean.EventRefreshMembers;
import com.kloudsync.techexcel.bean.EventShareScreen;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.MeetingRecordManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.ub.techexcel.adapter.AgoraCameraAdapter;
import com.ub.techexcel.adapter.FullAgoraCameraAdapter;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.tools.InviteUserPopup;
import com.ub.techexcel.tools.MeetingSettingDialog;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.agora.openlive.model.AGEventHandler;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.internal.RtcEngineImpl;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;


public class MeetingKit implements MeetingSettingDialog.OnUserOptionsListener, AGEventHandler, View.OnClickListener, PopMeetingMenu.MeetingMenuOperationsListener {

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
    private FullAgoraCameraAdapter fullCameraAdapter;
    private int role;

    public void setCameraAdapter(AgoraCameraAdapter cameraAdapter) {
        this.cameraAdapter = cameraAdapter;
    }

    public void setFullCameraAdapter(FullAgoraCameraAdapter fullCameraAdapter) {
        this.fullCameraAdapter = fullCameraAdapter;
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
        Log.e("prepareStart", "role:" + meetingConfig.getRole());
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
        settingDialog.setStartMeeting(true);
        if (settingDialog.isShowing()) {
            return;
        }
        settingDialog.show(host, meetingConfig);
    }

    public void init(Activity host, MeetingConfig meetingConfig) {
        this.host = host;
        this.meetingConfig = meetingConfig;
        ((App) host.getApplication()).initWorkerThread();
        getRtcManager().setHost(host);
        getRtcManager().addEventHandler(this);
    }

    public void prepareJoin(Activity host, MeetingConfig meetingConfig) {
        this.host = host;
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
        settingDialog.setStartMeeting(false);
        if (settingDialog.isShowing()) {
            return;
        }
        settingDialog.show(host, meetingConfig);
    }

    public void startMeeting() {
        Log.e("MeetingKit", "start_meeting");
//        meetingConfig.setRole(MeetingConfig.MeetingRole.HOST);
        if (!TextUtils.isEmpty(newMeetingId)) {
            meetingConfig.setMeetingId(newMeetingId);
        }
        rtcManager = RtcManager.getDefault(host);
        rtcManager.doConfigEngine(CLIENT_ROLE_BROADCASTER);
        Log.e("MeetingKit", "joinChannel:" + meetingConfig.getMeetingId());
        getRtcManager().rtcEngine().enableWebSdkInteroperability(true);
        setEncoderConfigurationBaseMode();
//        EventAgoraLog agoraLog = new EventAgoraLog();
//        agoraLog.setMessage("加入频道号：" + meetingConfig.getMeetingId().trim() +",id:" + Integer.parseInt(AppConfig.UserID));
//        EventBus.getDefault().post(agoraLog);
        rtcManager.joinRtcChannle(meetingConfig.getMeetingId());

    }

    public void setEncoderConfigurationBaseMode() {
        Log.e("setVideoEncoder", "当前模式   " + meetingConfig.getMode());
        if (meetingConfig.getMode() == 3) {   //屏幕共享
            if (isPresenter()) {
                setVideoEncoderConfiguration(MODE_240P);
            } else {
                setVideoEncoderConfiguration(MODE_120P);
            }
        } else if (meetingConfig.getMode() == 2) { //一个放大那个模式
            if (AppConfig.UserID.equals(meetingConfig.getCurrentMaxVideoUserId())) {
                setVideoEncoderConfiguration(MODE_480P);
            } else {
                setVideoEncoderConfiguration(MODE_120P);
            }
        } else if (meetingConfig.getMode() == 1) {  //摄像头模式所有人一样大小那个模式
            if (meetingConfig.getAgoraMembers().size() <= 9) {
                setVideoEncoderConfiguration(MODE_240P);
            } else {
                setVideoEncoderConfiguration(MODE_120P);
            }
        } else { //看文档模式
            if (isPresenter()) {
                setVideoEncoderConfiguration(MODE_360P);
            } else {
                setVideoEncoderConfiguration(MODE_120P);
            }
        }
    }

    public void retSetConfigurationBaseonNetwork(boolean isPoor) {

    }


    public void retSetResolutionRatio(boolean isExcel) {
        Log.e("checkNetWorkStatus", "改变分辨率   " + isExcel);
        if (isExcel) { //网络好
            if (!isPresenter()) {
                upLevel();
            }
        } else {
            if (!isPresenter()) {
                downLevel();
            }
        }
    }


    private void upLevel() {
        if (currentMode == MODE_120P) {// 升一级
            setVideoEncoderConfiguration(MODE_240P);
        } else if (currentMode == MODE_240P) {
            setVideoEncoderConfiguration(MODE_360P);
        } else if (currentMode == MODE_360P) {
            setVideoEncoderConfiguration(MODE_480P);
        }
    }

    private void downLevel() {
        if (currentMode == MODE_240P) {// 降一级
            setVideoEncoderConfiguration(MODE_120P);
        } else if (currentMode == MODE_360P) {
            setVideoEncoderConfiguration(MODE_240P);
        } else if (currentMode == MODE_480P) {
            setVideoEncoderConfiguration(MODE_360P);
        }
    }


    public boolean isPresenter() {
        if (!TextUtils.isEmpty(meetingConfig.getPresenterSessionId())) {
            if (AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                Log.e("setVideoEncoder", "是presenter");
                return true;
            }
        }
        Log.e("setVideoEncoder", "不是presenter");
        return false;
    }

    public static final int MODE_120P = 0;
    public static final int MODE_240P = 1;
    public static final int MODE_360P = 2;
    public static final int MODE_480P = 3;

    private int currentMode = 0;

    public void setVideoEncoderConfiguration(int mode) {
        currentMode = mode;
        VideoEncoderConfiguration.VideoDimensions dimension = VideoEncoderConfiguration.VD_160x120;
        switch (mode) {
            case MODE_120P:
                Log.e("setVideoEncoder", "120p");
                dimension = VideoEncoderConfiguration.VD_160x120;  // 120p:  160*120
                break;
            case MODE_240P:
                Log.e("setVideoEncoder", "240p");
                dimension = VideoEncoderConfiguration.VD_320x240;  // 240p: 320*240
                break;
            case MODE_360P:
                Log.e("setVideoEncoder", "360p");
                dimension = VideoEncoderConfiguration.VD_480x360;  // 360p:480*360
                break;
            case MODE_480P:
                Log.e("setVideoEncoder", "480p");
                dimension = VideoEncoderConfiguration.VD_640x480;  // 480p: 640*480
                break;
        }
        VideoEncoderConfiguration videoEncoderConfiguration = new VideoEncoderConfiguration(dimension,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15, 0, VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE);
        getRtcManager().rtcEngine().setVideoEncoderConfiguration(videoEncoderConfiguration);
    }

    private boolean isRecordMeeting = false;

    @Override
    public void onUserStart(boolean isRecordMeeting) {
        this.isRecordMeeting = isRecordMeeting;
        Observable.just(newMeetingId).observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                JSONObject response = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/UpgradeToNormalLesson?lessonID=" + newMeetingId, null);
                if (response != null && response.getInt("RetCode") == 0) {
                    SocketMessageManager.getManager(host).sendMessage_LeaveMeeting(meetingConfig);
                    SocketMessageManager.getManager(host).sendMessage_startMeeting(meetingConfig, newMeetingId);
                }
            }
        }).subscribe();

    }

    @Override
    public void onUserJoin(boolean isRecordMeeting) {
        this.isRecordMeeting = isRecordMeeting;
        SocketMessageManager.getManager(host).sendMessage_JoinMeeting(meetingConfig);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        Log.e("check_agora_status", "onFirstRemoteVideoDecoded:" + "uid:" + uid + ",elapsed:" + elapsed);
    }

    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        isStarted = true;
//        EventAgoraLog agoraLog = new EventAgoraLog();
//        agoraLog.setMessage("JoinChannelSuccess:channel," + channel + ",uid:" + uid);
//        Log.e("MeetingKit", "onJoinChannelSuccess:channel:" + channel +",uid:" + uid);
//        EventBus.getDefault().post(agoraLog);

        if (meetingConfig != null) {
            meetingConfig.setInRealMeeting(true);
            meetingConfig.setAgoraChannelId(channel);
        }
        try {
            getRtcManager().worker().getEngineConfig().mUid = uid;
            initAgora(host);
        } catch (Exception e) {
            Log.e("MeetingKit", "mUid = uid:" + e);
        }
        refreshMembersAndPost(meetingConfig, uid, true);
        checkNetWorkStatus();
        MeetingRecordManager.getManager(host).startRecording(isRecordMeeting);
    }


    private Timer netCheckTimer;

    /**
     * 每隔30秒检查网络质量
     */
    private void checkNetWorkStatus() {
//        Log.e("checkNetWorkStatus", "打开网络测试");
//        getRtcManager().rtcEngine().enableLastmileTest(); // 打开网络测试
        if (netCheckTimer != null) {
            netCheckTimer.cancel();
            netCheckTimer = null;
        }
        netCheckTimer = new Timer();
        netCheckTimer.schedule(new TimerTask() {
            @Override
            public void run() {
//                Log.e("checkNetWorkStatus", "网络质量-------->  " + currentNetworkQuality);
                if (currentNetworkQuality == NetWorkQuality.QUALITY_UNKNOWN ||
                        currentNetworkQuality == NetWorkQuality.QUALITY_EXCELLENT ||
                        currentNetworkQuality == NetWorkQuality.QUALITY_GOOD) {   //网络状态良好
                    retSetResolutionRatio(true);
                } else if (currentNetworkQuality == NetWorkQuality.QUALITY_POOR ||
                        currentNetworkQuality == NetWorkQuality.QUALITY_BAD ||
                        currentNetworkQuality == NetWorkQuality.QUALITY_VBAD ||
                        currentNetworkQuality == NetWorkQuality.QUALITY_DOWN ||
                        currentNetworkQuality == NetWorkQuality.QUALITY_DETECTING) {
                    retSetResolutionRatio(false);
                }
            }
        }, 10000, 50000);
    }

    public static class NetWorkQuality {
        public static int QUALITY_UNKNOWN = 0;//质量未知
        public static int QUALITY_EXCELLENT = 1;//质量极好
        public static int QUALITY_GOOD = 2;//用户主观感觉和极好差不多，但码率可能略低于极好
        public static int QUALITY_POOR = 3;//用户主观感受有瑕疵但不影响沟通
        public static int QUALITY_BAD = 4;//勉强能沟通但不顺畅
        public static int QUALITY_VBAD = 5;//网络质量非常差，基本不能沟通
        public static int QUALITY_DOWN = 6;//网络连接断开，完全无法沟通
        public static int QUALITY_DETECTING = 8;//SDK 正在探测网络质量

    }


    private int currentNetworkQuality = 0;

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
//        Log.e("checkNetWorkStatus", "网络质量更新中  " + uid + " " + txQuality + " " + rxQuality);
        if (uid == 0 || uid == Integer.parseInt(AppConfig.UserID)) {
            currentNetworkQuality = txQuality; //上行网络质量，基于上行视频的发送码率、上行丢包率、平均往返时延和网络抖动计算
            if (currentNetworkQuality == NetWorkQuality.QUALITY_UNKNOWN ||
                    currentNetworkQuality == NetWorkQuality.QUALITY_EXCELLENT ||
                    currentNetworkQuality == NetWorkQuality.QUALITY_GOOD){
                if(meetingConfig != null){
                    meetingConfig.setNetWorkFine(true);
                }
            } else if (currentNetworkQuality == NetWorkQuality.QUALITY_VBAD || currentNetworkQuality == NetWorkQuality.QUALITY_VBAD ||
                    currentNetworkQuality == NetWorkQuality.QUALITY_DOWN){
                if(meetingConfig != null){
                    meetingConfig.setNetWorkFine(false);
                }
            }
        }
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.e("MeetingKit", "onUserOffline:" + uid);
        if (uid > 1000000000 && uid < 1500000000) {
            meetingConfig.setShareScreenUid(0);
            EventBus.getDefault().post(new EventCloseShare());
        } else {
            AgoraMember member = new AgoraMember();
            member.setUserId(uid);
            member.setMuteAudio(true);
            member.setMuteVideo(true);
            if (member.isAdd()) {
                meetingConfig.addAgoraMember(member);
            } else {
                //delete user
                meetingConfig.deleteAgoraMember(member);
            }

            EventBus.getDefault().post(member);

            requestMeetingMembers(meetingConfig, false);
        }


    }

    @Override
    public void onAudioRouteChanged(int routing) {
        if (popMeetingMenu != null && popMeetingMenu.isShowing()) {
            popMeetingMenu.onAudioRouteChanged(routing);
        }
    }

    @Override
    public void onUserMuteVideo(int uid, boolean muted) {
        Log.e("MeetingKit", "onUserMuteVideo:" + uid);
        AgoraMember member = new AgoraMember();
        member.setUserId(uid);
        EventMute eventMute = new EventMute();
        eventMute.setType(EventMute.TYPE_MUTE_VEDIO);
        eventMute.setMuteVedio(muted);
        eventMute.setAgoraMember(member);
        EventBus.getDefault().post(eventMute);
    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {
        Log.e("MeetingKit", "onUserMuteAudio:" + uid);
        AgoraMember member = new AgoraMember();
        member.setUserId(uid);
        EventMute eventMute = new EventMute();
        eventMute.setType(EventMute.TYPE_MUTE_AUDIO);
        eventMute.setMuteAudio(muted);
        eventMute.setAgoraMember(member);
        EventBus.getDefault().post(eventMute);

    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        Log.e("check_agora_status", "onRemoteVideoStats:" + "RemoteVideoStats:" + stats.uid + "," + stats.rxStreamType);
    }


	public void postShareScreenDelay(final int uid) {
//        if(meetingConfig.getMode() != 3){
//            return;
//        }
        Observable.just("main_thread").delay(10000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (uid <= 1000000000 || uid > 1500000000) {
                    return;
                }
                getRtcManager().rtcEngine().enableWebSdkInteroperability(true);
                SurfaceView surfaceView = RtcEngine.CreateRendererView(host);
                surfaceView.setZOrderOnTop(true);
                surfaceView.setZOrderMediaOverlay(true);
                surfaceView.setTag(uid);
                getRtcManager().rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
                EventShareScreen shareScreen = new EventShareScreen();
                shareScreen.setUid(uid);
                shareScreen.setShareView(surfaceView);
                EventBus.getDefault().post(shareScreen);
            }
        });

    }

    public void postShareScreen(final int uid) {
        if (meetingConfig.getMode() != 3) {
            return;
        }
        Observable.just("main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (uid <= 1000000000 || uid > 1500000000) {
                    return;
                }
                getRtcManager().rtcEngine().enableWebSdkInteroperability(true);
                SurfaceView surfaceView = RtcEngine.CreateRendererView(host.getBaseContext());
                surfaceView.setZOrderOnTop(true);
                surfaceView.setZOrderMediaOverlay(true);
                surfaceView.setTag(uid);
                getRtcManager().rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
                EventShareScreen shareScreen = new EventShareScreen();
                shareScreen.setUid(uid);
                shareScreen.setShareView(surfaceView);
                EventBus.getDefault().post(shareScreen);
            }
        });
    }

    @Override
    public void onUserJoined(final int uid, int elapsed) {
        Log.e("MeetingKit", "onUserJoined,uid:" + uid + ",user_id:" + AppConfig.UserID);
        if (meetingConfig != null) {
            if (!meetingConfig.isInRealMeeting()) {
                return;
            }

            Observable.just(meetingConfig).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingConfig>() {
                @Override
                public void accept(MeetingConfig meetingConfig) throws Exception {
                    // 屏幕共享
                    if (uid > 1000000000 && uid < 1500000000) {
                        Log.e("check_share_screen_onUserJoined", "uid:" + uid);
                        meetingConfig.setShareScreenUid(uid);
                        postShareScreen(meetingConfig.getShareScreenUid());

                    } else {
                        //  成员的camera
                        refreshMembersAndPost(meetingConfig, uid, false);
                    }
                }
            }).subscribe();

        }
    }

    public void setShareScreenStream(EventShareScreen eventShareScreen) {

        getRtcManager().rtcEngine().setupRemoteVideo(new VideoCanvas(eventShareScreen.getShareView(), VideoCanvas.RENDER_MODE_HIDDEN, eventShareScreen.getUid()));

    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

    }

    private AgoraMember createMemberCamera(int userId) {
        AgoraMember member = new AgoraMember();
        member.setUserId(userId);
        member.setAdd(true);
        SurfaceView surfaceV = RtcEngine.CreateRendererView(host.getApplicationContext());
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);
        getRtcManager().rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, userId));
        member.setSurfaceView(surfaceV);
        return member;
    }

    private AgoraMember createSelfCamera(int userId) {
        AgoraMember member = new AgoraMember();
        member.setUserId(userId);
        boolean isMuteVedio = !MeetingSettingCache.getInstance(host).getMeetingSetting().isCameraOn();
        boolean isMuteAudio = !MeetingSettingCache.getInstance(host).getMeetingSetting().isMicroOn();
        member.setMuteVideo(isMuteVedio);
        member.setMuteAudio(isMuteAudio);
        member.setAdd(true);
        SurfaceView surfaceV = RtcEngine.CreateRendererView(host.getApplicationContext());
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);
        getRtcManager().rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, userId));
        getRtcManager().worker().getRtcEngine().muteLocalVideoStream(isMuteVedio);
        member.setSurfaceView(surfaceV);
        return member;
    }

    public void release() {
        if (settingDialog != null) {
            if (settingDialog.isShowing()) {
                settingDialog.dismiss();
                settingDialog = null;
            }
        }
        if (netCheckTimer != null) {
            netCheckTimer.cancel();
            netCheckTimer = null;
            Log.e("checkNetWorkStatus", "关闭网络测试");
//            getRtcManager().rtcEngine().disableLastmileTest();// 关闭网络测试
        }

        try {
            getRtcManager().rtcEngine().leaveChannel();
            getRtcManager().event().removeEventHandler(this);
        } catch (Exception e) {
            Log.e("MeetingKit", "release exception:" + e);
        }
        isStarted = false;
        kit = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void showMeetingMenu(ImageView menu, Activity host, MeetingConfig meetingConfig) {
        Log.e("PopMeetingMenu", "showMeetingMenu");
        this.meetingConfig = meetingConfig;
        this.host = host;
        if (popMeetingMenu != null) {
            if (popMeetingMenu.isShowing()) {
                popMeetingMenu.hide();
                popMeetingMenu = null;
            }
        }

        popMeetingMenu = new PopMeetingMenu(host);
        popMeetingMenu.show(host, menu, meetingConfig, this);
    }

    // --- meeting menu

    @Override
    public void menuEndClicked() {
        EventExit exit = new EventExit();
        exit.setEnd(true);
        EventBus.getDefault().post(exit);
    }

    @Override
    public void menuLeaveClicked() {
        EventExit exit = new EventExit();
        exit.setEnd(false);
        EventBus.getDefault().post(exit);

    }

    @Override
    public void menuCameraClicked(boolean isCameraOn) {
        Log.e("menuCameraClicked", "mute_locacl_video:" + !isCameraOn);
        getRtcManager().worker().getRtcEngine().muteLocalVideoStream(!isCameraOn);
        MeetingSettingCache.getInstance(host).setCameraOn(isCameraOn);
        if (cameraAdapter != null) {
            cameraAdapter.muteOrOpenCamera(getRtcManager().worker().getEngineConfig().mUid, !isCameraOn);
        }

        if (fullCameraAdapter != null) {
            fullCameraAdapter.muteOrOpenCamera(getRtcManager().worker().getEngineConfig().mUid, !isCameraOn);
        }
        onUserMuteVideo(Integer.parseInt(AppConfig.UserID), !MeetingSettingCache.getInstance(host).getMeetingSetting().isCameraOn());

    }

    @Override
    public void menuSwitchCamera() {
        getRtcManager().worker().getRtcEngine().switchCamera();
    }

    @Override
    public void menuMicroClicked(boolean isMicroOn) {
        Log.e("menuMicroClicked", "mute_locacl_voice:" + !isMicroOn);
        getRtcManager().worker().getRtcEngine().muteLocalAudioStream(!isMicroOn);
        MeetingSettingCache.getInstance(host).setMicroOn(isMicroOn);
        onUserMuteAudio(Integer.parseInt(AppConfig.UserID), !MeetingSettingCache.getInstance(host).getMeetingSetting().isMicroOn());
    }

    @Override
    public void menuChangeVoiceStatus(int status) {
        if (status == 0) {
            getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
            getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(false);
            getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(false);
            MeetingSettingCache.getInstance(host).setVoiceStatus(1);
        } else if (status == 1) {
            getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(true);
            MeetingSettingCache.getInstance(host).setVoiceStatus(2);

        } else if (status == 2) {
            getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
            getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
            getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(true);
            MeetingSettingCache.getInstance(host).setVoiceStatus(0);
        }
    }


    @Override
    public void menuInviteClicked() {
        showInviteDialog();
    }

    @Override
    public void menuMoreClicked() {

    }

    private InviteUserPopup inviteUserPopup;

    private void showInviteDialog() {
        if (inviteUserPopup != null) {
            if (inviteUserPopup.isShowing()) {
                inviteUserPopup.dismiss();
            }
            inviteUserPopup = null;
        }
        inviteUserPopup = new InviteUserPopup();
        inviteUserPopup.getPopwindow(host, meetingConfig.getMeetingId());
        inviteUserPopup.setInvitePopupListener(new InviteUserPopup.InvitePopupListener() {
            @Override
            public void copyLink() {

            }

            @Override
            public void email(String url) {
                String[] targetemail = {"214176156@qq.com"};
                String[] email = {"1599528112@qq.com"};
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822"); // 设置邮件格式
                intent.putExtra(Intent.EXTRA_EMAIL, targetemail); // 接收人
                intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
                intent.putExtra(Intent.EXTRA_TEXT, url); // 正文
                host.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
            }

            @Override
            public void dismiss() {

            }

            @Override
            public void open() {

            }
        });
        inviteUserPopup.StartPop();

    }

    public void initVoice(int status) {
        try {
            if (status == 0) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
                getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(true);

            } else if (status == 1) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(false);
                getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(false);
            } else if (status == 2) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(true);
            }
        } catch (Exception e) {

        }

    }

    private void initAgora(Activity host) {

        int status = getSettingCache(host).getMeetingSetting().getVoiceStatus();
        boolean isMicroOn = getSettingCache(host).getMeetingSetting().isMicroOn();
        boolean isCameraOn = getSettingCache(host).getMeetingSetting().isCameraOn();
        try {
            if (status == 0) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
                getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(true);
            } else if (status == 1) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(false);
                getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(false);
            } else if (status == 2) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(true);
            }
            getRtcManager().worker().getRtcEngine().muteLocalAudioStream(!isMicroOn);
            getRtcManager().worker().getRtcEngine().muteLocalVideoStream(!isCameraOn);
        } catch (Exception e) {

        }
    }

    public void checkCameraForScan() {

        try {
            RtcEngineImpl engine = (RtcEngineImpl) getRtcManager().worker().getRtcEngine();
//            engine.setVideoCamera(0);
        } catch (Exception e) {

        }
    }

    private MeetingSettingCache settingCache;

    private MeetingSettingCache getSettingCache(Activity host) {
        if (settingCache == null) {
            settingCache = MeetingSettingCache.getInstance(host);
        }
        return settingCache;
    }

    public void requestMeetingMembers(MeetingConfig meetingConfig, final boolean needRefresh) {
        this.meetingConfig = meetingConfig;
        Observable.just(meetingConfig).observeOn(Schedulers.io()).map(new Function<MeetingConfig, MeetingConfig>() {
            @Override
            public MeetingConfig apply(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.MEMBER);

                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("data").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        if (members != null) {
                            for (MeetingMember member : members) {

                                if (member.getRole() == 2) {
                                    meetingConfig.setMeetingHostId(member.getUserId() + "");
                                }

                                if (member.getPresenter() == 1) {
                                    meetingConfig.setPresenterId(member.getUserId() + "");
                                    meetingConfig.setPresenterSessionId(member.getSessionId() + "");
                                }
                            }

                            Collections.sort(members);
                            meetingConfig.setMeetingMembers(members);
                        }
                    }
                }
                return meetingConfig;
            }

        }).map(new Function<MeetingConfig, MeetingConfig>() {
            @Override
            public MeetingConfig apply(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.AUDIENCE);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        Log.e("check_auditor", "json_array" + result.getJSONArray("data").toString());
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("data").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        Log.e("check_auditor", "auditor" + members);
                        if (members != null) {
                            for (MeetingMember member : members) {
                                member.setRole(MeetingConfig.MeetingRole.AUDIENCE);
                            }
                            Collections.sort(members);
                            meetingConfig.setMeetingAuditor(members);
                        }
                    }
                }
                return meetingConfig;
            }

        }).map(new Function<MeetingConfig, MeetingConfig>() {
            @Override
            public MeetingConfig apply(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.BE_INVITED);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {

                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("data").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());

                        if (members != null) {
                            for (MeetingMember member : members) {
                                member.setRole(MeetingConfig.MeetingRole.BE_INVITED);
                            }
                            Collections.sort(members);
                            meetingConfig.setMeetingInvitors(members);
                        }
                    }
                }
                EventRefreshMembers refreshMembers = new EventRefreshMembers();
                refreshMembers.setMeetingConfig(meetingConfig);
                refreshMembers.setNeedRefresh(needRefresh);
                Log.e("check_member", "member:" + meetingConfig.getMeetingMembers().size() + "," + "auditor:" + meetingConfig.getMeetingAuditor().size());
                EventBus.getDefault().post(refreshMembers);
                return meetingConfig;
            }

        }).subscribe();
    }

    public void templeDisableLocalVideo() {
        try {
            getRtcManager().worker().getRtcEngine().disableVideo();
            Log.e("templeDisableLocalVideo", "disableVideo");
        } catch (Exception e) {
            Log.e("templeDisableLocalVideo", "exception:" + e);

        }
    }

    public void restoreLocalVedeo() {
        boolean isCameraOn = MeetingSettingCache.getInstance(host).getMeetingSetting().isCameraOn();
        try {
            getRtcManager().worker().getRtcEngine().enableVideo();
            Log.e("restoreLocalVedeo", "enableVideo:" + isCameraOn);
        } catch (Exception e) {
            Log.e("restoreLocalVedeo", "exception:" + e);

        }
    }

    private void refreshMembersAndPost(final MeetingConfig meetingConfig, final int uid, final boolean isSelf) {
        this.meetingConfig = meetingConfig;
        Observable.just(meetingConfig).observeOn(AndroidSchedulers.mainThread()).map(new Function<MeetingConfig, AgoraMember>() {
            @Override
            public AgoraMember apply(MeetingConfig meetingConfig) throws Exception {
                AgoraMember agoraMember = null;
                if (isSelf) {
                    agoraMember = createSelfCamera(uid);
                } else {
                    agoraMember = createMemberCamera(uid);
                }

                if (agoraMember.isAdd()) {
                    meetingConfig.addAgoraMember(agoraMember);
                } else {
                    //delete user
                    meetingConfig.deleteAgoraMember(agoraMember);
                }
                return agoraMember;
            }

        }).observeOn(Schedulers.io()).map(new Function<AgoraMember, AgoraMember>() {
            @Override
            public AgoraMember apply(AgoraMember agoraMember) throws Exception {

                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.MEMBER);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("data").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        if (members != null) {
                            for (MeetingMember member : members) {
                                if (member.getRole() == 2) {
                                    meetingConfig.setMeetingHostId(member.getUserId() + "");
                                }

                                if (member.getPresenter() == 1) {
                                    meetingConfig.setPresenterId(member.getUserId() + "");
                                }
                            }

                            meetingConfig.setMeetingMembers(members);
                        }
                    }
                }

                return agoraMember;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<AgoraMember>() {
            @Override
            public void accept(AgoraMember agoraMember) throws Exception {

                if (!meetingConfig.getMeetingMembers().contains(new MeetingMember(uid))) {
                    // 加入的是member
                    Log.e("check_disable", "disable,1");
                    if ((uid + "").equals(AppConfig.UserID)) {
                        disableAudioAndVideoStream();
                    }
                }
                if (isSelf) {
                    EventBus.getDefault().post(agoraMember);
                } else {
                    EventBus.getDefault().post(agoraMember);
                }

            }
        }).subscribe();
    }

    public void disableAudioAndVideoStream() {
        MeetingKit.getInstance().menuMicroClicked(false);
        MeetingKit.getInstance().menuCameraClicked(false);
//        MeetingKit.getInstance().changeAgoraRole(CLIENT_ROLE_AUDIENCE);
    }

    public void disableAudioStream() {
        MeetingKit.getInstance().menuMicroClicked(false);
    }

    public void disableVedioStream() {
        MeetingKit.getInstance().menuCameraClicked(false);
    }

    public void enableAudioAndVideo() {
//        MeetingKit.getInstance().changeAgoraRole(CLIENT_ROLE_BROADCASTER);

    }

    public void changeAgoraRole(int role) {
        if (rtcManager != null) {
            Log.e("changeAgoraRole", "role:" + role);
            rtcManager.doConfigEngine(role);
        }
    }

    public void unsubscribeAudio(int userId) {

        if (rtcManager == null) {
            rtcManager = MeetingKit.getInstance().getRtcManager();
        }
        rtcManager.worker().getRtcEngine().muteRemoteAudioStream(userId, true);
    }

    public void unsubscribeVedio(int userId) {

        if (rtcManager == null) {
            rtcManager = MeetingKit.getInstance().getRtcManager();
        }
        rtcManager.worker().getRtcEngine().muteRemoteVideoStream(userId, true);
    }

    public void subscribeAudio(int userId) {

        if (rtcManager == null) {
            rtcManager = MeetingKit.getInstance().getRtcManager();
        }
        rtcManager.worker().getRtcEngine().muteRemoteAudioStream(userId, false);
    }

    public void unsubscribeAudiorsAudioAndVedio(int userId) {
        unsubscribeAudio(userId);
        unsubscribeVedio(userId);
    }

    public void unsubscribeMineAudioAndVedio() {
        if (rtcManager == null) {
            rtcManager = MeetingKit.getInstance().getRtcManager();
        }
        rtcManager.worker().getRtcEngine().muteLocalAudioStream(true);
        rtcManager.worker().getRtcEngine().muteLocalVideoStream(true);
    }

    public void setMyAgoraStutas(MeetingMember meetingMember) {
        if (rtcManager == null) {
            rtcManager = MeetingKit.getInstance().getRtcManager();
        }

        if (meetingMember.getMicrophoneStatus() != 2) {
            if (!settingCache.getMeetingSetting().isMicroOn()) {
                rtcManager.worker().getRtcEngine().muteLocalAudioStream(true);
            }
        }

        if (meetingMember.getCameraStatus() != 2) {
            if (!settingCache.getMeetingSetting().isCameraOn()) {
                rtcManager.worker().getRtcEngine().muteLocalVideoStream(true);
            }
        }
    }

    public void setMemberAgoraStutas(MeetingMember meetingMember) {

        if (rtcManager == null) {
            rtcManager = MeetingKit.getInstance().getRtcManager();
        }

        if (meetingMember.getMicrophoneStatus() != 2) {
            rtcManager.worker().getRtcEngine().muteRemoteAudioStream(meetingMember.getUserId(), true);
        } else {
            rtcManager.worker().getRtcEngine().muteRemoteAudioStream(meetingMember.getUserId(), false);
        }

        if (meetingMember.getCameraStatus() != 2) {
            rtcManager.worker().getRtcEngine().muteRemoteVideoStream(meetingMember.getUserId(), true);
        } else {
            rtcManager.worker().getRtcEngine().muteRemoteVideoStream(meetingMember.getUserId(), false);
        }

    }

    public void refreshMeetingMenu(){
        if(popMeetingMenu != null && popMeetingMenu.isShowing()){
            popMeetingMenu.refreshStatus();
        }
    }

}
