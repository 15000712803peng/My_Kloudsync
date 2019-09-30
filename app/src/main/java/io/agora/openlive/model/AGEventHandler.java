package io.agora.openlive.model;

import io.agora.rtc.IRtcEngineEventHandler;

public interface AGEventHandler {

    void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);

    void onJoinChannelSuccess(String channel, int uid, int elapsed);

    void onUserOffline(int uid, int reason);

    void onAudioRouteChanged(int routing);

    void onUserMuteVideo(int uid, boolean muted);

    void onUserMuteAudio(int uid, boolean muted);

    void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats);

    void onUserJoined(int uid, int elapsed);

    void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume);

}


