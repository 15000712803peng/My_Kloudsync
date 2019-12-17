package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EventExit;
import com.kloudsync.techexcel.bean.EventHideMembers;
import com.kloudsync.techexcel.bean.EventMute;
import com.kloudsync.techexcel.bean.EventRefreshMembers;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.techexcel.adapter.AgoraCameraAdapter;
import com.ub.techexcel.adapter.FullAgoraCameraAdapter;
import com.ub.techexcel.adapter.MeetingMembersAdapter;
import com.ub.techexcel.bean.AgoraBean;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.bean.AgoraUser;
import com.ub.techexcel.tools.MeetingSettingDialog;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import io.agora.openlive.model.AGEventHandler;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.internal.RtcEngineImpl;
import io.agora.rtc.video.VideoCanvas;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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

    public void setCameraAdapter(AgoraCameraAdapter cameraAdapter) {
        this.cameraAdapter = cameraAdapter;
    }

    public void setFullCameraAdaptero(FullAgoraCameraAdapter fullCameraAdapter) {
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
        Log.e("prepareStart","role:" + meetingConfig.getRole());
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
        settingDialog.show(host);
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
        settingDialog.show(host);
    }

    public void startMeeting() {
        Log.e("MeetingKit", "start_meeting");
//        meetingConfig.setRole(MeetingConfig.MeetingRole.HOST);
        if(!TextUtils.isEmpty(newMeetingId)){
            meetingConfig.setMeetingId(newMeetingId);
        }
        rtcManager = RtcManager.getDefault(host);
        rtcManager.doConfigEngine(CLIENT_ROLE_BROADCASTER);
        Log.e("MeetingKit", "joinChannel:" + meetingConfig.getMeetingId());
        rtcManager.joinRtcChannle(meetingConfig.getMeetingId());

    }

    @Override
    public void onUserStart() {
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
    public void onUserJoin() {
        SocketMessageManager.getManager(host).sendMessage_JoinMeeting(meetingConfig);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        isStarted = true;
        Log.e("MeetingKit", "onJoinChannelSuccess:" + channel);
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
        EventBus.getDefault().post(createSelfCamera(meetingConfig.getAgoraChannelId(), uid));
//        Log.e("MeetingKit", "onJoinChannelSuccess,uid:" + uid + ",elapsed:" + elapsed);
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.e("MeetingKit", "onUserOffline:" + uid);
        AgoraMember member = new AgoraMember();
        member.setUserId(uid);
        EventBus.getDefault().post(member);
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
        eventMute.setMute(muted);
        eventMute.setAgoraMember(member);
        EventBus.getDefault().post(eventMute);


    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {
        Log.e("MeetingKit", "onUserMuteAudio:" + uid);

    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {

    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.e("MeetingKit", "onUserJoined,uid:" + uid);
        if (meetingConfig != null) {
            if (!meetingConfig.isInRealMeeting()) {
                return;
            }
            EventBus.getDefault().post(createMemberCamera(meetingConfig.getAgoraChannelId(), uid));
        }
    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

    }

    private AgoraMember createMemberCamera(String channelId, int userId) {
        AgoraMember member = new AgoraMember();
        member.setUserId(userId);
        member.setMuteVideo(false);
        member.setAdd(true);
        SurfaceView surfaceV = RtcEngine.CreateRendererView(host.getApplicationContext());
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);
        getRtcManager().rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, userId));
        member.setSurfaceView(surfaceV);
        return member;
    }


    private AgoraMember createSelfCamera(String channelId, int userId) {
        AgoraMember member = new AgoraMember();
        member.setUserId(userId);
        boolean isMute = !MeetingSettingCache.getInstance(host).getMeetingSetting().isCameraOn();
        member.setMuteVideo(isMute);
        member.setAdd(true);
        SurfaceView surfaceV = RtcEngine.CreateRendererView(host.getApplicationContext());
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);
        getRtcManager().rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, userId));
        getRtcManager().worker().getRtcEngine().muteLocalVideoStream(isMute);
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
            case R.id.image_members_close:
                EventBus.getDefault().post(new EventHideMembers());
                break;
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
            engine.setVideoCamera(0);
        } catch (Exception e) {

        }
    }

    private List<MeetingMember> meetingMembers;
    private List<MeetingMember> meetingAuditors;
    private LinearLayout meetingMembersLayout;
    private RecyclerView membersList;
    private MeetingMembersAdapter membersAdapter;
    private TextView audienceNumbersText;
    private ImageView closeMembersImage;

    public void showMeetingMembers(Activity host, MeetingConfig meetingConfig, LinearLayout meetingMembersLayout) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        this.host = host;
        this.meetingConfig = meetingConfig;
        meetingMembers = meetingConfig.getMeetingMembers();
        meetingAuditors = meetingConfig.getMeetingAuditor();
        this.meetingMembersLayout = meetingMembersLayout;
        membersList = meetingMembersLayout.findViewById(R.id.list_meeting_member);
        audienceNumbersText = meetingMembersLayout.findViewById(R.id.audience_number);
        if (meetingAuditors != null && meetingAuditors.size() > 0) {
            audienceNumbersText.setText(meetingAuditors.size() + "");
        } else {
            audienceNumbersText.setText("0");
        }
        closeMembersImage = meetingMembersLayout.findViewById(R.id.image_members_close);
        closeMembersImage.setOnClickListener(this);
        membersList.setLayoutManager(new LinearLayoutManager(host, RecyclerView.HORIZONTAL, false));
        Collections.sort(meetingMembers);
        if (membersAdapter == null) {
            membersAdapter = new MeetingMembersAdapter(host, meetingMembers);
            membersList.setAdapter(membersAdapter);
        } else {
            membersAdapter.updateMembers(meetingMembers);
        }

        membersAdapter.setOnMemberClickedListener((DocAndMeetingActivity) host);

    }

    public void refreshMeetingMembers(MeetingConfig meetingConfig, LinearLayout meetingMembersLayout) {
        this.meetingConfig = meetingConfig;
        if (meetingMembersLayout.getVisibility() != View.VISIBLE) {
            return;
        }
        if (membersAdapter != null) {
            Collections.sort(meetingConfig.getMeetingMembers());
            membersAdapter.updateMembers(meetingConfig.getMeetingMembers());
        }
    }

    private MeetingSettingCache settingCache;

    private MeetingSettingCache getSettingCache(Activity host) {
        if (settingCache == null) {
            settingCache = MeetingSettingCache.getInstance(host);
        }
        return settingCache;
    }

    public void requestMeetingMembers(MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        Observable.just(meetingConfig).observeOn(Schedulers.io()).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.MEMBER);
                if (result.has("RetCode")) {
                    if (result.getInt("RetCode") == 0) {
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("userList").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        if(members != null){
                            meetingConfig.setMeetingMembers(members);
                        }
                    }
                }
            }
        }).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.AUDIENCE);
                if (result.has("RetCode")) {
                    if (result.getInt("RetCode") == 0) {
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("userList").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        if(members != null){
                            meetingConfig.setMeetingAuditor(members);
                        }
                    }
                }
            }
        }).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.MEMBER);
                if (result.has("RetCode")) {
                    if (result.getInt("RetCode") == 0) {
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("userList").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        if(members != null){
                            meetingConfig.setMeetingInvitors(members);
                        }
                    }
                }
                EventBus.getDefault().post(new EventRefreshMembers());
            }
        }).subscribe();
    }

    private RelativeLayout defaultDocumentView;
    private LinearLayout createBlankPage, inviteAttendee, shareDocument;


    public void handleMeetingDefaultDocument(final RelativeLayout defaultDocumentView){
        this.defaultDocumentView = defaultDocumentView;
        Observable.just(meetingConfig).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {

            }
        }).subscribe();
    }

    public void templeDisableLocalVideo(){
        try {
            getRtcManager().worker().getRtcEngine().disableVideo();
            Log.e("templeDisableLocalVideo","disableVideo");
        }catch (Exception e){
            Log.e("templeDisableLocalVideo","exception:" + e);

        }
    }

    public void restoreLocalVedeo(){
        boolean isCameraOn = MeetingSettingCache.getInstance(host).getMeetingSetting().isCameraOn();
        try {
            getRtcManager().worker().getRtcEngine().enableVideo();
            Log.e("restoreLocalVedeo","enableVideo:" + isCameraOn);
        }catch (Exception e){
            Log.e("restoreLocalVedeo","exception:" + e);

        }
    }
}