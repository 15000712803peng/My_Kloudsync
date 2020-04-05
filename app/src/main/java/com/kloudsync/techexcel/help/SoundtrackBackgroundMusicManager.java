package com.kloudsync.techexcel.help;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.kloudsync.techexcel.bean.EventCloseSoundtrack;
import com.kloudsync.techexcel.bean.SoundtrackMediaInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.URLDecoder;


public class SoundtrackBackgroundMusicManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static SoundtrackBackgroundMusicManager instance;
    private MediaPlayer audioPlayer;
    private volatile long playTime;
    private Context context;
    private SoundtrackMediaInfo mediaInfo;

    private SoundtrackBackgroundMusicManager(Context context) {
        this.context = context;
    }

    public static SoundtrackBackgroundMusicManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SoundtrackBackgroundMusicManager.class) {
                if (instance == null) {
                    instance = new SoundtrackBackgroundMusicManager(context);
                }
            }
        }
        return instance;
    }

    private SoundtrackAudioManagerV2 soundtrackAudioManager;

    public void setSoundtrackAudio(SoundtrackMediaInfo mediaInfo,SoundtrackAudioManagerV2 soundtrackAudioManager) {
        Log.e("check_play","mediaInfo:" + mediaInfo);
        this.mediaInfo = mediaInfo;
        this.soundtrackAudioManager = soundtrackAudioManager;
        if(mediaInfo == null){
            return;
        }
        prepareAudioAndPlay(mediaInfo);
    }

    public SoundtrackMediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void prepareAudioAndPlay(SoundtrackMediaInfo audioData) {
        try {
            audioPlayer = new MediaPlayer();
            try {
                if(audioPlayer.isPlaying()){
                    return;
                }
            }catch (IllegalStateException exception){

            }
            audioPlayer.setOnPreparedListener(this);
            audioPlayer.setOnCompletionListener(this);
            audioPlayer.setOnErrorListener(this);
            Log.e("check_play","set_data_source:" + audioData.getAttachmentUrl());
            audioPlayer.reset();
            audioPlayer.setDataSource(context, Uri.parse(URLDecoder.decode(audioData.getAttachmentUrl(),"UTF-8")));
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                audioPlayer.prepareAsync();
            }catch (IllegalStateException e){
                Log.e("check_play","IllegalStateException," + e.getMessage());
                reinit(audioData);
            }

        } catch (IOException e) {
            Log.e("check_play","IOException," + e.getMessage());
            e.printStackTrace();
            audioData.setPreparing(false);
        }

    }

    private void reinit(SoundtrackMediaInfo mediaInfo){
        if(this.mediaInfo == null){
            return;
        }
        audioPlayer = null;
        audioPlayer = new MediaPlayer();
        try {
            audioPlayer.reset();
            audioPlayer.setDataSource(context, Uri.parse(mediaInfo.getAttachmentUrl()));
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("check_play","onPrepared");
        if (mediaInfo != null) {
            Log.e("check_play", "on prepared,id:" + mediaInfo.getAttachmentUrl());
            mp.start();
            if(soundtrackAudioManager != null){
                if(!soundtrackAudioManager.isPlaying()){
                    mp.pause();
                }
            }

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
//        EventBus.getDefault().post(new EventCloseSoundtrack());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public boolean isPlaying(){
        if(this.mediaInfo == null){
            return false;
        }
        if(audioPlayer != null){
            return audioPlayer.isPlaying();
        }

        return false;
    }

    public long getPlayTime(){
        if(this.mediaInfo == null){
            return 0;
        }
        if(audioPlayer == null){
            return  0;
        }
        return audioPlayer.getCurrentPosition();
    }

    public long getTotalTime(){
        if(this.mediaInfo == null){
            return 0;
        }
        return audioPlayer.getDuration();
    }


    public void release(){
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.reset();
            audioPlayer.release();
            audioPlayer = null;
        }

        mediaInfo = null;
        instance = null;
    }

    public long getDuration(){
        if(this.mediaInfo == null){
            return 0;
        }
        return audioPlayer.getDuration();
    }

    public void pause(){
        if(this.mediaInfo == null){
            return ;
        }
        if(audioPlayer != null){
            Log.e("vedio_check","pause_begin");
            audioPlayer.pause();
            Log.e("vedio_check","pause_");
        }
    }

    public void restart(){
        if(this.mediaInfo == null){
            return;
        }
        if(audioPlayer != null){
            audioPlayer.start();
        }
    }

    public void seekTo(int time){
        if(this.mediaInfo == null){
            return ;
        }

        try {
            if(audioPlayer != null){
                if(time < audioPlayer.getDuration()){
                    audioPlayer.seekTo(time);
                }
                Log.e("vedio_check","seek_to,time:" + time);
            }
        }catch (Exception e){

        }

    }

}
