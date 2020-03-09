package com.kloudsync.techexcel.help;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.kloudsync.techexcel.bean.EventCloseSoundtrack;
import com.kloudsync.techexcel.bean.SoundtrackMediaInfo;
import com.ub.techexcel.bean.SectionVO;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.media.MediaPlayer.SEEK_CLOSEST;


public class SoundtrackAudioManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static SoundtrackAudioManager instance;
    private MediaPlayer audioPlayer;
    private volatile long playTime;
    private Context context;
    private SoundtrackMediaInfo mediaInfo;

    private SoundtrackAudioManager(Context context) {
        this.context = context;

    }

    public static SoundtrackAudioManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SoundtrackAudioManager.class) {
                if (instance == null) {
                    instance = new SoundtrackAudioManager(context);
                }
            }
        }
        return instance;
    }

    public void setSoundtrackAudio(SoundtrackMediaInfo mediaInfo) {
        Log.e("check_play","mediaInfo:" + mediaInfo);
        this.mediaInfo = mediaInfo;
        if(mediaInfo == null){
            return;
        }
        prepareAudioAndPlay(mediaInfo);
    }

    public SoundtrackMediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void prepareAudioAndPlay(SoundtrackMediaInfo audioData) {

        audioPlayer = new MediaPlayer();
        try {

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
        if(mediaInfo == null){
            return;
        }
        Log.e("check_play","onPrepared");
        if (mediaInfo != null) {
            Log.e("check_play", "on prepared,id:" + mediaInfo.getAttachmentUrl());
            mp.start();

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
        EventBus.getDefault().post(new EventCloseSoundtrack());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public boolean isPlaying(){
        if(mediaInfo == null){
            return false;
        }
        if(audioPlayer != null){
            return audioPlayer.isPlaying();
        }

        return false;
    }

    public long getPlayTime(){
        if(mediaInfo == null){
            return 0;
        }
        if(audioPlayer == null){
            return  0;
        }
        return audioPlayer.getCurrentPosition();
    }

    public long getTotalTime(){
        if(mediaInfo == null){
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
        if(mediaInfo == null){
            return 0;
        }
        return audioPlayer.getDuration();
    }

    public void pause(){
        if(mediaInfo == null){
            return;
        }
        if(audioPlayer != null){
            Log.e("vedio_check","pause_begin");
            audioPlayer.pause();
            Log.e("vedio_check","pause_");
        }
    }

    public void restart(){
        if(mediaInfo == null){
            return;
        }
        if(audioPlayer != null){
            audioPlayer.start();
        }
    }

    public void seekTo(int time){
        if(mediaInfo == null){
            return;
        }
        if(audioPlayer != null){
            audioPlayer.seekTo(time);
            Log.e("vedio_check","seek_to,time:" + time);
        }
    }

}
