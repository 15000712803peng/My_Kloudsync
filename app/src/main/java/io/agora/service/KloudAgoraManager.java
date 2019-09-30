package io.agora.service;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.config.MeetingConfig;
import com.ub.techexcel.bean.AgoraUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class KloudAgoraManager {

    private static final KloudAgoraManager instance = new KloudAgoraManager();
    private AgoraWorkerThread mWorkerThread;
    private AgoraEventListener agoraEventListener;
    private MeetingConfig meetingConfig;


    public void setMeetingConfig(MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
    }

    public void setAgoraEventListener(AgoraEventListener agoraEventListener) {
        this.agoraEventListener = agoraEventListener;
    }

    public static KloudAgoraManager getInstance() {
        return instance;
    }

    public interface AgoraEventListener {
        void onSelfJoined(String id);

        void onMeetingMemeberJoined(String id);

        void showMemberScreen(String id);
    }

    private KloudAgoraManager() {

    }

    public void init(Context context) {
        initEventHandler();
        initWorkerThread(context);
    }

    public void release() {
        releaseWorkerThread();
    }

    private synchronized void initWorkerThread(Context context) {
        if (mWorkerThread == null) {
            mWorkerThread = new AgoraWorkerThread(context, mRtcEventHandler);
            mWorkerThread.start();
            mWorkerThread.waitForReady();
        }
    }

    private synchronized void releaseWorkerThread() {
        if (mWorkerThread != null) {
            mWorkerThread.exit();
            try {
                mWorkerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mWorkerThread = null;
        }

    }

    public synchronized AgoraWorkerThread getWorkerThread() {
        return mWorkerThread;
    }


    private void initEventHandler() {
        if (mRtcEventHandler == null) {
            mRtcEventHandler = new IRtcEngineEventHandler() {
                private final Logger log = LoggerFactory.getLogger(this.getClass());

                @Override
                public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
                    Log.e("EngineEventHandler", "onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + width + " " + height + " " + elapsed);

                }

                @Override
                public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
                    Log.e("EngineEventHandler", "onFirstLocalVideoFrame " + width + " " + height + " " + elapsed);
                }

                @Override
                public void onUserJoined(int uid, int elapsed) {
                    Log.e("EngineEventHandler", "onUserJoined,uid:" + uid + ",elapsed:" + elapsed);
                    if (uid < 1000000000) {
                        if (agoraEventListener != null && !AppConfig.UserID.equals(uid + "")) {
                            agoraEventListener.onMeetingMemeberJoined(uid + "");
                        }
                    } else if (uid > 1000000000 && uid < 1500000000) {
                        if (agoraEventListener != null && !AppConfig.UserID.equals(uid + "")) {
                            agoraEventListener.showMemberScreen(uid + "");
                        }
                    }
                }

                @Override
                public void onUserOffline(int uid, int reason) {
                    // FIXME this callback may return times
                    Log.e("EngineEventHandler", "onUserOffline,uid:" + uid + ",reason:" + reason);

                }

                @Override
                public void onUserMuteVideo(int uid, boolean muted) {
                    Log.e("EngineEventHandler", "onUserMuteVideo,uid:" + uid + ",muted:" + muted);
                }


                @Override
                public void onUserMuteAudio(int uid, boolean muted) {
                    Log.e("EngineEventHandler", "onUserMuteAudio,uid:" + uid + ",muted:" + muted);
                }

                @Override
                public void onRtcStats(RtcStats stats) {
                    Log.e("EngineEventHandler", "onRtcStats,stats:" + stats);
                }


                @Override
                public void onLeaveChannel(RtcStats stats) {
                    Log.e("EngineEventHandler", "onLeaveChannel,stats:" + stats);
                }

                @Override
                public void onLastmileQuality(int quality) {
                    Log.e("EngineEventHandler", "onLastmileQuality " + quality);
                }

                @Override
                public void onError(int err) {
                    super.onError(err);
                    Log.e("EngineEventHandler", "onError " + err);
                }

                @Override
                public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                    Log.e("EngineEventHandler", "onJoinChannelSuccess, channel" + channel + ",uid:" + uid + ",elapsed:" + elapsed);
                    if (AppConfig.UserID.equals(uid + "")) {
                        if (agoraEventListener != null) {
                            agoraEventListener.onSelfJoined(uid + "");
                        }
                    }

                }


                @Override
                public void onAudioRouteChanged(int routing) {
                    Log.e("EngineEventHandler", "onAudioRouteChanged,routing" + routing);
                }


                @Override
                public void onRemoteVideoStats(RemoteVideoStats stats) {
                    super.onRemoteVideoStats(stats);
                    Log.e("EngineEventHandler", "onRemoteVideoStats,stats:" + stats);
                }


                @Override
                public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
                    super.onAudioVolumeIndication(speakers, totalVolume);
                    Log.e("EngineEventHandler", "onAudioVolumeIndication,speakers:" + speakers + ",totalVolume：" + totalVolume);

                }

                public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
                    Log.e("EngineEventHandler", "onRejoinChannelSuccess " + channel + " " + uid + " " + elapsed);
                }

                public void onWarning(int warn) {
                    Log.e("EngineEventHandler", "onWarning " + warn);
                }
            };

        }
    }

    private IRtcEngineEventHandler mRtcEventHandler;

    public AgoraUser addSelfVedio(Context context, String id) {
        AgoraUser hostUser = new AgoraUser();
        hostUser.setId(id);
        SurfaceView surface = RtcEngine.CreateRendererView(context);
        surface.setZOrderOnTop(true);
        surface.setZOrderMediaOverlay(true);
        hostUser.setSurfaceView(surface);
        hostUser.setMuteVideo(false);
        getWorkerThread().getRtcEngine().switchCamera();
        getWorkerThread().getRtcEngine().enableLocalVideo(true);
        getWorkerThread().getRtcEngine().muteLocalVideoStream(false);
        getWorkerThread().getRtcEngine().setupLocalVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, Integer.parseInt(id)));
        return hostUser;
    }

    public SurfaceView addScreenSurface(Context context, String id) {
        SurfaceView surface = RtcEngine.CreateRendererView(context);
        surface.setZOrderOnTop(true);
        surface.setZOrderMediaOverlay(true);
        surface.setTag(id);
        getWorkerThread().getRtcEngine().enableVideo();
        getWorkerThread().getRtcEngine().setupRemoteVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_FIT, Integer.parseInt(id)));
        return surface;
    }

    public AgoraUser addMemberVedio(Context context, String id) {
        AgoraUser hostUser = new AgoraUser();
        hostUser.setId(id);
        SurfaceView surface = RtcEngine.CreateRendererView(context);
        surface.setZOrderOnTop(true);
        surface.setZOrderMediaOverlay(true);
        hostUser.setSurfaceView(surface);
        hostUser.setMuteVideo(false);

        getWorkerThread().getRtcEngine().enableVideo();
        getWorkerThread().getRtcEngine().setupRemoteVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, Integer.parseInt(id)));
        return hostUser;
    }

}
