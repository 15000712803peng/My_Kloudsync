package com.kloudsync.techexcel.dialog.plugin;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ub.techexcel.tools.SpliteSocket;
import com.ub.techexcel.tools.Tools;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.rong.callkit.CallFloatBoxView;
import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.common.RLog;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.UserInfo;


public class SingleCallActivity2 extends BaseCallActivity2 implements Handler.Callback {
    private static final String TAG = "VoIPSingleActivity";
    private LayoutInflater inflater;
    private RongCallSession callSession;
    private FrameLayout mLPreviewContainer;
    private FrameLayout mSPreviewContainer;
    private FrameLayout mButtonContainer;
    private LinearLayout mUserInfoContainer;
    private Boolean isInformationShow = false;
    private SurfaceView mLocalVideo = null;
    private boolean muted = false;
    private boolean handFree = false;
    private boolean startForCheckPermissions = false;

    private int EVENT_FULL_SCREEN = 1;

    private String targetId = null;
    private RongCallCommon.CallMediaType mediaType;

    BroadcastReceiver broadcastReceiver;

    Customer cus;

    public static SingleCallActivity2 instance = null;

    @Override
    final public boolean handleMessage(Message msg) {
        if (msg.what == EVENT_FULL_SCREEN) {
            hideVideoCallInformation();
            return true;
        }
        return false;
    }

    private RtcEngine mRtcEngine;// Tutorial Step 1
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1


        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { // Tutorial Step 5
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
                        setupRemoteVideo(uid);
                    }
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showShortToast(getString(R.string.rc_voip_call_terminalted));
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showShortToast(getString(R.string.rc_voip_call_terminalted));
                    finish();
                }
            });
        }
    };

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_voip_activity_single_call);
        instance = this;

        Intent intent = getIntent();
        mLPreviewContainer = (FrameLayout) findViewById(R.id.rc_voip_call_large_preview);
        mSPreviewContainer = (FrameLayout) findViewById(R.id.rc_voip_call_small_preview);
        mButtonContainer = (FrameLayout) findViewById(R.id.rc_voip_btn);
        mUserInfoContainer = (LinearLayout) findViewById(R.id.rc_voip_user_info);

        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));

        if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO2)) {
            mediaType = RongCallCommon.CallMediaType.AUDIO;
        } else {
            mediaType = RongCallCommon.CallMediaType.VIDEO;
        }
        if (mediaType != null) {
            inflater = LayoutInflater.from(this);
            initView(mediaType, callAction);

            setupIntent();

        } else {
            RLog.w(TAG, "恢复的瞬间，对方已挂断");
            CallFloatBoxView.hideFloatBox();
            finish();
        }

        SetAVReceiver();
    }

    private void SetAVReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mediaType == RongCallCommon.CallMediaType.AUDIO) {
                    initAgoraEngineAndJoinChannel2();
                } else {

                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.Receive_Spectator));
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
        if (callAction == null) {
            return;
        }
        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO2)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
        } else if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            /*callSession = intent.getParcelableExtra("callSession");
            mediaType = callSession.getMediaType();*/
            mediaType = intent.getBooleanExtra("isVideo", false) ?
                    RongCallCommon.CallMediaType.AUDIO : RongCallCommon.CallMediaType.VIDEO;
        } else {
            /*callSession = RongCallClient.getInstance().getCallSession();
            mediaType = callSession.getMediaType();*/
            mediaType = intent.getBooleanExtra("isVideo", false) ?
                    RongCallCommon.CallMediaType.AUDIO : RongCallCommon.CallMediaType.VIDEO;
        }

        if (!requestCallPermissions(mediaType, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
            return;
        }
        if (mediaType != null) {
            setupIntent();
        }

        super.onNewIntent(intent);
    }


    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                boolean permissionGranted;
                if (mediaType == RongCallCommon.CallMediaType.AUDIO) {
                    permissionGranted = PermissionCheckUtil.checkPermissions(this, AUDIO_CALL_PERMISSIONS);
                } else {
                    permissionGranted = PermissionCheckUtil.checkPermissions(this, VIDEO_CALL_PERMISSIONS);

                }
                if (permissionGranted) {
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionGranted();
                    } else {
                        setupIntent();
                    }
                } else {
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                    } else {
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {

            String[] permissions;
            if (mediaType == RongCallCommon.CallMediaType.AUDIO) {
                permissions = AUDIO_CALL_PERMISSIONS;
            } else {
                permissions = VIDEO_CALL_PERMISSIONS;
            }
            if (PermissionCheckUtil.checkPermissions(this, permissions)) {
                if (startForCheckPermissions) {
                    RongCallClient.getInstance().onPermissionGranted();
                } else {
                    setupIntent();
                }
            } else {
                if (startForCheckPermissions) {
                    RongCallClient.getInstance().onPermissionDenied();
                } else {
                    finish();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupIntent() {
        RongCallCommon.CallMediaType mediaType;
        Intent intent = getIntent();
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
//        if (callAction.equals(RongCallAction.ACTION_RESUME_CALL)) {
//            return;
//        }
        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            /*callSession = intent.getParcelableExtra("callSession");
            mediaType = callSession.getMediaType();*/
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO2)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
//            targetId = callSession.getInviterUserId();
        } else if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO2)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }

            if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
                initAgoraEngineAndJoinChannel();
            } else {
//                initAgoraEngineAndJoinChannel2();
            }
           /* Conversation.ConversationType conversationType = Conversation.ConversationType.valueOf(intent.getStringExtra("conversationType").toUpperCase(Locale.US));
            targetId = intent.getStringExtra("targetId");

            List<String> userIds = new ArrayList<>();
            userIds.add(targetId);
            RongCallClient.getInstance().startCall(conversationType, targetId, userIds, mediaType, null);*/
        } else { // resume call
            /*callSession = RongCallClient.getInstance().getCallSession();
            mediaType = callSession.getMediaType();*/
            mediaType = intent.getBooleanExtra("isVideo", false) ?
                    RongCallCommon.CallMediaType.AUDIO : RongCallCommon.CallMediaType.VIDEO;
        }

        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            handFree = false;
        } else if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            handFree = true;
        }

        cus = (Customer) intent.getSerializableExtra("Customer");
        if (cus != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(cus.getName());
            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null && cus.getUrl() != null) {
                    userPortrait.setResource(cus.getUrl().toString(), R.drawable.rc_default_portrait);
                }
            }
        }

        /*UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
        if (userInfo != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(userInfo.getName());
            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null && userInfo.getPortraitUri() != null) {
                    userPortrait.setResource(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
                }
            }
        }*/

        createPowerManager();
        createPickupDetector();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pickupDetector != null && mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            pickupDetector.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pickupDetector != null) {
            pickupDetector.unRegister();
        }
    }

    private void initView(RongCallCommon.CallMediaType mediaType, RongCallAction callAction) {
        FrameLayout buttonLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        RelativeLayout userInfoLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_audio_call_user_info, null);
        userInfoLayout.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.GONE);

        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            buttonLayout.findViewById(R.id.rc_voip_call_mute).setVisibility(View.GONE);
            buttonLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
        }

        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(R.color.rc_voip_background_color));
            mLPreviewContainer.setVisibility(View.GONE);
            mSPreviewContainer.setVisibility(View.GONE);

            if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
                buttonLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_incoming_button_layout, null);
                TextView callInfo = (TextView) userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);
                callInfo.setText(R.string.rc_voip_audio_call_inviting);
                onIncomingCallRinging();
            }
        } else {
            userInfoLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_video_call_user_info, null);
            if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
                findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(R.color.rc_voip_background_color));
                buttonLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_incoming_button_layout, null);
                TextView callInfo = (TextView) userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);
                callInfo.setText(R.string.rc_voip_video_call_inviting);
                onIncomingCallRinging();
                ImageView answerV = (ImageView) buttonLayout.findViewById(R.id.rc_voip_call_answer_btn);
                answerV.setImageResource(R.drawable.rc_voip_vedio_answer_selector);
            }
        }
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(buttonLayout);
        mUserInfoContainer.removeAllViews();
        mUserInfoContainer.addView(userInfoLayout);
    }

   /* @Override
    public void onCallOutgoing(RongCallSession callSession, SurfaceView localVideo) {
        super.onCallOutgoing(callSession, localVideo);
        this.callSession = callSession;
        if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)) {
            mLPreviewContainer.setVisibility(View.VISIBLE);
            localVideo.setTag(callSession.getSelfUserId());
            mLPreviewContainer.addView(localVideo);
        }
        onOutgoingCallRinging();
    }*/

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        setupVideoProfile();         // Tutorial Step 2
        setupLocalVideo();           // Tutorial Step 3
        joinChannel();               // Tutorial Step 4
    }

    private void initAgoraEngineAndJoinChannel2() {
        initializeAgoraEngine();     // Tutorial Step 1
        setupLocalVideo();
        joinChannel();               // Tutorial Step 4
    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e("Exception", Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_480P_8, false);
    }

    // Tutorial Step 3
    private void setupLocalVideo() {
//        this.callSession = callSession;
        TextView remindInfo = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_call_remind_info);
        setupTime(remindInfo);


        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
//            findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);
            FrameLayout btnLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
            mButtonContainer.removeAllViews();
            mButtonContainer.addView(btnLayout);
        } else {
            mLocalVideo = RtcEngine.CreateRendererView(getBaseContext());
            mLocalVideo.setZOrderMediaOverlay(true);
            RongCallAction callAction = RongCallAction.valueOf(getIntent().getStringExtra("callAction"));
            mLPreviewContainer.removeAllViews();
            mLPreviewContainer.addView(mLocalVideo);
//          mLocalVideo.setTag(callSession.getSelfUserId());
            mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalVideo, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
            findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mLPreviewContainer.setVisibility(View.VISIBLE);

        }

//      RongCallClient.getInstance().setEnableLocalAudio(!muted);
        View muteV = mButtonContainer.findViewById(R.id.rc_voip_call_mute);
        if (muteV != null) {
            muteV.setSelected(muted);
        }

        /*AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            RongCallClient.getInstance().setEnableSpeakerphone(false);
        } else {
            RongCallClient.getInstance().setEnableSpeakerphone(handFree);
        }*/

        View handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree);
        if (handFreeV != null) {
            handFreeV.setSelected(handFree);
        }
        stopRing();
    }

    // Tutorial Step 4
    private void joinChannel() {
        RongCallAction callAction = RongCallAction.valueOf(getIntent().getStringExtra("callAction"));
        String room;
        int uid;
        cus = (Customer) getIntent().getSerializableExtra("Customer");
        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            room = cus.getUserID() + "@" + AppConfig.UserID.replace("-", "");
        } else {
            room = AppConfig.UserID.replace("-", "") + "@" + cus.getUserID();
        }
        uid = Integer.valueOf(AppConfig.UserID.replace("-", ""));
        mRtcEngine.joinChannel(null, room, "Extra Optional Data", uid); // if you do not specify the uid, we will generate the uid for you
    }

    /*@Override
    public void onCallConnected(RongCallSession callSession, SurfaceView localVideo) {
        super.onCallConnected(callSession, localVideo);
        this.callSession = callSession;
        TextView remindInfo = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_call_remind_info);
        setupTime(remindInfo);


        if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
            findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);
            FrameLayout btnLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
            mButtonContainer.removeAllViews();
            mButtonContainer.addView(btnLayout);
        } else {
            mLocalVideo = RtcEngine.CreateRendererView(getBaseContext());
            mLocalVideo.setTag(callSession.getSelfUserId());
        }

        RongCallClient.getInstance().setEnableLocalAudio(!muted);
        View muteV = mButtonContainer.findViewById(R.id.rc_voip_call_mute);
        if (muteV != null) {
            muteV.setSelected(muted);
        }

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            RongCallClient.getInstance().setEnableSpeakerphone(false);
        } else {
            RongCallClient.getInstance().setEnableSpeakerphone(handFree);
        }

        View handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree);
        if (handFreeV != null) {
            handFreeV.setSelected(handFree);
        }
        stopRing();
    }*/

    @Override
    protected void onDestroy() {
//        RongContext.getInstance().getEventBus().unregister(this);
        stopRing();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
        }
        RLog.d(TAG, "SingleCallActivity onDestroy");

        unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
        SendSocketToUB(cus);
        super.onDestroy();
    }

    // Tutorial Step 5
    private void setupRemoteVideo(int uid) {
        if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mLPreviewContainer.setVisibility(View.VISIBLE);
            mLPreviewContainer.removeAllViews();
            SurfaceView remoteVideo = RtcEngine.CreateRendererView(getBaseContext());
            remoteVideo.setTag(uid);


            mLPreviewContainer.addView(remoteVideo);
            mLPreviewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInformationShow) {
                        hideVideoCallInformation();
                    } else {
                        showVideoCallInformation();
                        handler.sendEmptyMessageDelayed(EVENT_FULL_SCREEN, 5 * 1000);
                    }
                }
            });
            mSPreviewContainer.setVisibility(View.VISIBLE);
            mSPreviewContainer.removeAllViews();
            if (mLocalVideo != null) {
                mLocalVideo.setZOrderMediaOverlay(true);
                mLocalVideo.setZOrderOnTop(true);
                mSPreviewContainer.addView(mLocalVideo);
            }
            mSPreviewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SurfaceView fromView = (SurfaceView) mSPreviewContainer.getChildAt(0);
                    SurfaceView toView = (SurfaceView) mLPreviewContainer.getChildAt(0);

                    mLPreviewContainer.removeAllViews();
                    mSPreviewContainer.removeAllViews();
                    fromView.setZOrderOnTop(false);
                    fromView.setZOrderMediaOverlay(false);
                    mLPreviewContainer.addView(fromView);
                    toView.setZOrderOnTop(true);
                    toView.setZOrderMediaOverlay(true);
                    mSPreviewContainer.addView(toView);
                }
            });
            mButtonContainer.setVisibility(View.GONE);
            mUserInfoContainer.setVisibility(View.GONE);

            mRtcEngine.setupRemoteVideo(new VideoCanvas(remoteVideo, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
        }
    }

   /* @Override
    public void onRemoteUserJoined(final String userId, RongCallCommon.CallMediaType mediaType, SurfaceView remoteVideo) {
        super.onRemoteUserJoined(userId, mediaType, remoteVideo);
        if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mLPreviewContainer.setVisibility(View.VISIBLE);
            mLPreviewContainer.removeAllViews();
            remoteVideo.setTag(userId);

            mLPreviewContainer.addView(remoteVideo);
            mLPreviewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInformationShow) {
                        hideVideoCallInformation();
                    } else {
                        showVideoCallInformation();
                        handler.sendEmptyMessageDelayed(EVENT_FULL_SCREEN, 5 * 1000);
                    }
                }
            });
            mSPreviewContainer.setVisibility(View.VISIBLE);
            mSPreviewContainer.removeAllViews();
            if (mLocalVideo != null) {
                mLocalVideo.setZOrderMediaOverlay(true);
                mLocalVideo.setZOrderOnTop(true);
                mSPreviewContainer.addView(mLocalVideo);
            }
            mSPreviewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SurfaceView fromView = (SurfaceView) mSPreviewContainer.getChildAt(0);
                    SurfaceView toView = (SurfaceView) mLPreviewContainer.getChildAt(0);

                    mLPreviewContainer.removeAllViews();
                    mSPreviewContainer.removeAllViews();
                    fromView.setZOrderOnTop(false);
                    fromView.setZOrderMediaOverlay(false);
                    mLPreviewContainer.addView(fromView);
                    toView.setZOrderOnTop(true);
                    toView.setZOrderMediaOverlay(true);
                    mSPreviewContainer.addView(toView);
                }
            });
            mButtonContainer.setVisibility(View.GONE);
            mUserInfoContainer.setVisibility(View.GONE);
        }
    }*/

    /*@Override
    public void onMediaTypeChanged(String userId, RongCallCommon.CallMediaType mediaType, SurfaceView video) {
        if (callSession.getSelfUserId().equals(userId)) {
            showShortToast(getString(R.string.rc_voip_switch_to_audio));
        } else {
            RongCallClient.getInstance().changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
            callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);
            showShortToast(getString(R.string.rc_voip_remote_switch_to_audio));
        }
        initAudioCallView();
        handler.removeMessages(EVENT_FULL_SCREEN);
        mButtonContainer.findViewById(R.id.rc_voip_call_mute).setSelected(muted);
    }*/

    private WebSocketClient mWebSocketClient; //连接客户端
    private boolean isAccept;

    private void SendSocketToUB(Customer cus) {
        mWebSocketClient = AppConfig.webSocketClient;
        String info, ss;
        JSONObject jsonObject = format(cus);
        String data_str = Tools.getBase64(jsonObject.toString()).replaceAll("[\\s*\t\n\r]", "");
        ;
        String ids = "";
        RongCallAction callAction = RongCallAction.valueOf(getIntent().getStringExtra("callAction"));
        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            ids = cus.getUserID() + "," + AppConfig.UserID.replace("-", "");
        } else {
            ids = AppConfig.UserID.replace("-", "") + "," + cus.getUserID();
        }



        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", "SEND_MESSAGE");
            loginjson.put("sessionId", AppConfig.UserToken);
            loginjson.put("type", 1);
            loginjson.put("userList",ids);
            loginjson.put("data", data_str);
            ss = loginjson.toString();
            Log.e("ffffffffffffff", ss.toString());
            SpliteSocket.sendMesageBySocket(ss);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private JSONObject format(Customer cus) {

        JSONObject jsonObject = new JSONObject();
        try {
            RongCallAction callAction = RongCallAction.valueOf(getIntent().getStringExtra("callAction"));
            boolean isIn = callAction.equals(RongCallAction.ACTION_INCOMING_CALL);
            jsonObject.put("sourceUserId", isIn ? cus.getUserID() : AppConfig.UserID.replace("-", ""));
            jsonObject.put("sourceUserName", isIn ? cus.getName() : AppConfig.UserName);
            jsonObject.put("sourceAvatarUrl", isIn ? cus.getUrl() : AppConfig.MYAVATARURL);
            jsonObject.put("targetUserName", isIn ? AppConfig.UserName : cus.getName());
            jsonObject.put("targetUserId", isIn ? AppConfig.UserID : cus.getUserID());
            jsonObject.put("targetAvatarUrl", isIn ? AppConfig.MYAVATARURL : cus.getUrl());
            jsonObject.put("mediaType", mediaType.equals(RongCallCommon.CallMediaType.AUDIO) ? 1 : 2);
            jsonObject.put("actionType", isAccept ? 6 : 7);
            isAccept = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public void MediaTypeChanged(boolean isYou) {
        if (isYou) {
            showShortToast(getString(R.string.rc_voip_switch_to_audio));
        } else {
            /*RongCallClient.getInstance().changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
            callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);*/
            mediaType = RongCallCommon.CallMediaType.AUDIO;
            showShortToast(getString(R.string.rc_voip_remote_switched_to_audio));
        }
        initAudioCallView();
        handler.removeMessages(EVENT_FULL_SCREEN);
        mButtonContainer.findViewById(R.id.rc_voip_call_mute).setSelected(muted);
    }


    private void initAudioCallView() {
        mLPreviewContainer.removeAllViews();
        mLPreviewContainer.setVisibility(View.GONE);
        mSPreviewContainer.removeAllViews();
        mSPreviewContainer.setVisibility(View.GONE);

        findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(R.color.rc_voip_background_color));
        findViewById(R.id.rc_voip_audio_chat).setVisibility(View.GONE);

        View userInfoView = inflater.inflate(R.layout.rc_voip_audio_call_user_info, null);
        TextView timeView = (TextView) userInfoView.findViewById(R.id.rc_voip_call_remind_info);
        setupTime(timeView);

        mUserInfoContainer.removeAllViews();
        mUserInfoContainer.addView(userInfoView);

        Customer cus = (Customer) getIntent().getSerializableExtra("Customer");
        if (cus != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(cus.getName());
            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null && cus.getUrl() != null) {
                    userPortrait.setResource(cus.getUrl().toString(), R.drawable.rc_default_portrait);
                }
            }
        }
        /*UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
        if (userInfo != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(userInfo.getName());
            if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null) {
                    userPortrait.setAvatar(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
                }
            }
        }*/
        mUserInfoContainer.setVisibility(View.VISIBLE);
//        mUserInfoContainer.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);

        View button = inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(button);
        mButtonContainer.setVisibility(View.VISIBLE);
        View handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree);
        handFreeV.setSelected(handFree);

        if (pickupDetector != null) {
            pickupDetector.register(this);
        }
    }

    public void onHangupBtnClick(View view) {
        /*RongCallSession session = RongCallClient.getInstance().getCallSession();
        if (session == null || isFinishing) {
            finish();
            return;
        }
        RongCallClient.getInstance().hangUpCall(callSession.getCallId());*/
        stopRing();
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
        finish();
    }

    public void onReceiveBtnClick(View view) {
        /*RongCallSession session = RongCallClient.getInstance().getCallSession();
        if (session == null || isFinishing) {
            finish();
            return;
        }
        RongCallClient.getInstance().acceptCall(callSession.getCallId());*/
        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            initAgoraEngineAndJoinChannel2();
            isAccept = true;
            SendSocketToUB(cus);
        } else {
            initAgoraEngineAndJoinChannel();
        }
    }

    public void hideVideoCallInformation() {
        isInformationShow = false;
        mUserInfoContainer.setVisibility(View.GONE);
        mButtonContainer.setVisibility(View.GONE);

        findViewById(R.id.rc_voip_audio_chat).setVisibility(View.GONE);
    }

    public void showVideoCallInformation() {
        isInformationShow = true;
        mUserInfoContainer.setVisibility(View.VISIBLE);
//        mUserInfoContainer.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);
        mButtonContainer.setVisibility(View.VISIBLE);
        FrameLayout btnLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        btnLayout.findViewById(R.id.rc_voip_call_mute).setSelected(muted);
        btnLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
        btnLayout.findViewById(R.id.rc_voip_camera).setVisibility(View.VISIBLE);
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(btnLayout);
        View view = findViewById(R.id.rc_voip_audio_chat);
//        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*RongCallClient.getInstance().changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
                callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);*/
                mediaType = RongCallCommon.CallMediaType.AUDIO;
                showShortToast(getString(R.string.rc_voip_switch_to_audio));
                initAgoraEngineAndJoinChannel2();
                initAudioCallView();
            }
        });
    }

    public void onHandFreeButtonClick(View view) {
//        RongCallClient.getInstance().setEnableSpeakerphone(!view.isSelected());
        view.setSelected(!view.isSelected());
        handFree = view.isSelected();
    }

    public void onMuteButtonClick(View view) {
//        RongCallClient.getInstance().setEnableLocalAudio(view.isSelected());
        view.setSelected(!view.isSelected());
        muted = view.isSelected();
    }


    // Tutorial Step 7
    private void onRemoteUserLeft() {
//        String senderId;
//        String extra = "";

        isFinishing = true;
        /*if (callSession == null) {
            RLog.e(TAG, "onCallDisconnected. callSession is null!");
            postRunnableDelay(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
            return;
        }
        senderId = callSession.getInviterUserId();
        switch (reason) {
            case HANGUP:
            case REMOTE_HANGUP:
                int time = getTime();
                if (time >= 3600) {
                    extra = String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
                } else {
                    extra = String.format("%02d:%02d", (time % 3600) / 60, (time % 60));
                }
                break;
        }
*/
        postRunnableDelay(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    /*@Override
    public void onCallDisconnected(RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason) {
        super.onCallDisconnected(callSession, reason);

        String senderId;
        String extra = "";

        isFinishing = true;
        if (callSession == null) {
            RLog.e(TAG, "onCallDisconnected. callSession is null!");
            postRunnableDelay(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
            return;
        }
        senderId = callSession.getInviterUserId();
        switch (reason) {
            case HANGUP:
            case REMOTE_HANGUP:
                int time = getTime();
                if (time >= 3600) {
                    extra = String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
                } else {
                    extra = String.format("%02d:%02d", (time % 3600) / 60, (time % 60));
                }
                break;
        }

        if (!TextUtils.isEmpty(senderId)) {
            CallSTerminateMessage message = new CallSTerminateMessage();
            message.setReason(reason);
            message.setMediaType(callSession.getMediaType());
            message.setExtra(extra);
            if (senderId.equals(callSession.getSelfUserId())) {
                message.setDirection("MO");
            } else {
                message.setDirection("MT");
            }

            RongIM.getInstance().insertMessage(Conversation.ConversationType.PRIVATE, callSession.getTargetId(), senderId, message, null);
        }
        postRunnableDelay(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }*/

    @Override
    public void onRestoreFloatBox(Bundle bundle) {
        super.onRestoreFloatBox(bundle);
        if (bundle == null)
            return;
        muted = bundle.getBoolean("muted");
        handFree = bundle.getBoolean("handFree");
        setShouldShowFloat(true);

//        callSession = RongCallClient.getInstance().getCallSession();
//        RongCallCommon.CallMediaType mediaType = callSession.getMediaType();
        RongCallAction callAction = RongCallAction.valueOf(getIntent().getStringExtra("callAction"));
        inflater = LayoutInflater.from(this);
        initView(mediaType, callAction);
//        targetId = callSession.getTargetId();
        Customer cus = (Customer) getIntent().getSerializableExtra("Customer");
        if (cus != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(cus.getName());
            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null && cus.getUrl() != null) {
                    userPortrait.setResource(cus.getUrl().toString(), R.drawable.rc_default_portrait);
                }
            }
        }

        /*UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
        if (userInfo != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(userInfo.getName());
            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null) {
                    userPortrait.setAvatar(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
                }
            }
        }*/
        /*SurfaceView localVideo = null;
        SurfaceView remoteVideo = null;
        String remoteUserId = null;
        for (CallUserProfile profile : callSession.getParticipantProfileList()) {
            if (profile.getUserId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                localVideo = profile.getVideoView();
            } else {
                remoteVideo = profile.getVideoView();
                remoteUserId = profile.getUserId();
            }
        }
        if (localVideo != null && localVideo.getParent() != null) {
            ((ViewGroup) localVideo.getParent()).removeView(localVideo);
        }
        onCallOutgoing(callSession, localVideo);
//        onCallConnected(callSession, localVideo);
        if (remoteVideo != null && remoteVideo.getParent() != null) {
            ((ViewGroup) remoteVideo.getParent()).removeView(remoteVideo);
        }*/
//        onRemoteUserJoined(remoteUserId, mediaType, remoteVideo);
    }

    @Override
    public String onSaveFloatBoxState(Bundle bundle) {
        super.onSaveFloatBoxState(bundle);
        callSession = RongCallClient.getInstance().getCallSession();
        bundle.putBoolean("muted", muted);
        bundle.putBoolean("handFree", handFree);
        bundle.putInt("mediaType", mediaType.equals(RongCallCommon.CallMediaType.AUDIO) ? 1 : 2);

        return getIntent().getAction();
    }

    public void onMinimizeClick(View view) {
        super.onMinimizeClick(view);
    }

    public void onSwitchCameraClick(View view) {
//        RongCallClient.getInstance().switchCamera();
        mRtcEngine.switchCamera();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void onEventMainThread(UserInfo userInfo) {
        if (targetId != null && targetId.equals(userInfo.getUserId())) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            if (userInfo.getName() != null)
                userName.setText(userInfo.getName());
            AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
            if (userPortrait != null && userInfo.getPortraitUri() != null) {
                userPortrait.setResource(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
            }
        }
    }

}
