package io.agora.openlive.model;

import android.content.Context;
import android.util.Log;

import com.kloudsync.techexcel.bean.EventAgoraLog;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import io.agora.rtc.IRtcEngineEventHandler;

import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_DECODING;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_STOPPED;

public class MyEngineEventHandler {
    private final String TAG = MyEngineEventHandler.class.getSimpleName();
    public MyEngineEventHandler(Context ctx, EngineConfig config) {
        this.mContext = ctx;
        this.mConfig = config;
    }

    private final EngineConfig mConfig;

    private final Context mContext;

    //
    private final ConcurrentHashMap<AGEventHandler, Integer> mEventHandlerList = new ConcurrentHashMap<>();

    public void addEventHandler(AGEventHandler handler) {
        this.mEventHandlerList.put(handler, 0);
    }

    public void removeEventHandler(AGEventHandler handler) {
        this.mEventHandlerList.remove(handler);
    }

    public final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            log.debug("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + width + " " + height + " " + elapsed);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
            }
        }

        @Override
        public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
            log.debug("onFirstLocalVideoFrame " + width + " " + height + " " + elapsed);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onUserJoined(uid, elapsed);
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            // FIXME this callback may return times
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onUserOffline(uid, reason);
            }
        }

        /**
         * 远端视频状态发生已变化回调
         *
         * @param uid
         * @param state
         * @param reason
         * @param elapsed
         */
        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            switch (state) {
                case REMOTE_VIDEO_STATE_STOPPED:
                    switch (reason) {
                        case REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED:
                            myOnUserMuteVideo(uid, true);
                            break;
                    }
                    break;
                case REMOTE_VIDEO_STATE_DECODING:
                    switch (reason) {
                        case REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED:
                            myOnUserMuteVideo(uid, false);
                            break;
                    }
                    break;
            }
        }

        /**
         * 远端音频状态发生改变回调
         *当前SDK版本未回调该方法还是使用的onUserMuteAudio()
         * @param uid
         * @param state
         * @param reason
         * @param elapsed
         */
        @Override
        public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
            Log.i(TAG, "onRemoteAudioStateChanged = " + uid + "_" + state + "_" + reason + "_" + elapsed);
           /* switch (state) {
                case REMOTE_AUDIO_STATE_STOPPED:
                    switch (reason) {
                        case REMOTE_AUDIO_REASON_REMOTE_MUTED:
                            myOnUserMuteAudio(uid,true);
                            break;
                    }
                    break;
                case REMOTE_AUDIO_STATE_DECODING:
                    switch (reason) {
                        case REMOTE_AUDIO_REASON_REMOTE_UNMUTED:
                            myOnUserMuteAudio(uid,false);
                            break;
                    }
                    break;
            }*/
        }

        private void myOnUserMuteVideo(int uid, boolean muted) {
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onUserMuteVideo(uid, muted);
            }
        }


        @Override
        public void onUserMuteAudio(int uid, boolean muted) {
            Log.i(TAG,"onUserMuteAudio = " + uid +muted);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onUserMuteAudio(uid, muted);
            }
        }

        @Override
        public void onRtcStats(RtcStats stats) {

        }


        @Override
        public void onLeaveChannel(RtcStats stats) {

        }

        @Override
        public void onLastmileQuality(int quality) {
            log.debug("onLastmileQuality " + quality);
        }

        @Override
        public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onNetworkQuality(uid,txQuality,rxQuality);
            }
        }

        @Override
        public void onError(int err) {
            super.onError(err);
	        EventAgoraLog agoraLog = new EventAgoraLog();
	        agoraLog.setMessage("join error," + err);
	        EventBus.getDefault().post(agoraLog);
            log.debug("onError " + err);
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            log.debug("onJoinChannelSuccess " + channel + " " + uid + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onJoinChannelSuccess(channel, uid, elapsed);
            }
        }


        @Override
        public void onAudioRouteChanged(int routing) {
            super.onAudioRouteChanged(routing);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onAudioRouteChanged(routing);
            }
        }


        @Override
        public void onRemoteVideoStats(RemoteVideoStats stats) {
            super.onRemoteVideoStats(stats);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onRemoteVideoStats(stats);
            }
        }



        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onAudioVolumeIndication(speakers,totalVolume);
            }
        }

        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            log.debug("onRejoinChannelSuccess " + channel + " " + uid + " " + elapsed);
        }

        public void onWarning(int warn) {
            log.debug("onWarning " + warn);
        }

        @Override
        public void onActiveSpeaker(int uid) {
            super.onActiveSpeaker(uid);
            Log.e(TAG,"onActiveSpeaker = " + uid +uid);
        }



        @Override
        public void onRemoteAudioStats(RemoteAudioStats stats) {
            super.onRemoteAudioStats(stats);
            Log.e(TAG,"onRemoteAudioStats = uid:" + stats.uid + ",quality:" + stats.quality);
        }
    };

}
