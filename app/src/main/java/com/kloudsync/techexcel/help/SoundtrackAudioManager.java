package com.kloudsync.techexcel.help;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.kloudsync.techexcel.bean.SoundtrackMediaInfo;
import com.ub.techexcel.bean.SectionVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SoundtrackAudioManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static SoundtrackAudioManager instance;
    private MediaPlayer audioPlayer;
    private volatile long playTime;
    private Context context;
    private SoundtrackMediaInfo mediaInfo;

    private SoundtrackAudioManager(Context context) {
        this.context = context;
        audioPlayer = new MediaPlayer();

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
        prepareAudioAndPlay(mediaInfo);

    }

    public void prepareAudioAndPlay(SoundtrackMediaInfo audioData) {
        try {
            if (audioData.isPreparing() || audioData.isPrepared()) {
                return;
            }
            try {
                if(audioPlayer.isPlaying()){
                    return;
                }
            }catch (IllegalStateException exception){

            }
            audioData.setPreparing(true);
            audioPlayer.reset();
            Log.e("check_play","set_data_source:" + audioData.getAttachmentUrl());
            audioPlayer.setDataSource(context, Uri.parse(audioData.getAttachmentUrl()));
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                audioPlayer.prepareAsync();
            }catch (IllegalStateException e){

                reinit(audioData);
            }
            audioPlayer.setOnPreparedListener(this);
            audioPlayer.setOnCompletionListener(this);
            audioPlayer.setOnErrorListener(this);
        } catch (IOException e) {
            e.printStackTrace();
            audioData.setPreparing(false);
        }

    }

    private void reinit(SoundtrackMediaInfo mediaInfo){
        audioPlayer = null;
        audioPlayer = new MediaPlayer();
        try {
            audioPlayer.setDataSource(context, Uri.parse(mediaInfo.getAttachmentUrl()));
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.prepareAsync();
            mediaInfo.setPrepared(true);
        } catch (IOException e) {
            e.printStackTrace();
            mediaInfo.setPreparing(false);
        }

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mediaInfo != null) {
            Log.e("check_play", "on prepared,id:" + mediaInfo.getAttachmentUrl());
            mediaInfo.setPrepared(true);
            mediaInfo.setPreparing(false);
            mp.start();
            mediaInfo.setPlaying(true);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        mp.reset();
        mediaInfo.setPrepared(false);
        mediaInfo.setPlaying(false);
        mediaInfo.setPreparing(false);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public boolean isPlaying(){
        if(audioPlayer != null){
            return audioPlayer.isPlaying();
        }
        return false;
    }

    public long getPlayTime(){
        return audioPlayer.getCurrentPosition();
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
        return audioPlayer.getDuration();
    }

    public void pause(){
        if(audioPlayer != null){
            Log.e("vedio_check","pause_begin");
            audioPlayer.pause();
            Log.e("vedio_check","pause_");
        }
    }

    public void restart(){
        if(audioPlayer != null){
            audioPlayer.start();
        }
    }

}
