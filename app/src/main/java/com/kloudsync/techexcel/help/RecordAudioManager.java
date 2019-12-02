package com.kloudsync.techexcel.help;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.ub.techexcel.bean.SectionVO;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by tonyan on 2019/11/21.
 */

public class RecordAudioManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static RecordAudioManager instance;
    private MediaPlayer audioPlayer;
    private volatile long playTime;
    private Context context;
    //
    private List<SectionVO> audioDatas = new ArrayList<>();
    private SectionVO audioData;

    private RecordAudioManager(Context context) {
        this.context = context;
        audioPlayer = new MediaPlayer();

    }

    public static RecordAudioManager getInstance(Context context) {
        if (instance == null) {
            synchronized (RecordAudioManager.class) {
                if (instance == null) {
                    instance = new RecordAudioManager(context);
                }
            }
        }
        return instance;
    }

    public void setAudioDatas(List<SectionVO> audioDatas) {
        this.audioDatas.clear();
        this.audioDatas.addAll(audioDatas);
        Collections.sort(audioDatas, new Comparator<SectionVO>() {
            @Override
            public int compare(SectionVO o1, SectionVO o2) {
                return o1.getStartTime() - o2.getStartTime();
            }
        });
        Log.e("check_play","audioDatas:" + audioDatas);
        if (audioDatas.size() > 0) {
            audioData = audioDatas.get(0);
            prepareAudio(audioData);
        }
    }

    public void prepareAudio(SectionVO audioData) {
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
            audioPlayer.setDataSource(context, Uri.parse(audioData.getFileUrl()));
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

    private void reinit(SectionVO vedioData){
        audioPlayer = null;
        audioPlayer = new MediaPlayer();
        try {
            audioPlayer.setDataSource(context, Uri.parse(vedioData.getFileUrl()));
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
            vedioData.setPreparing(false);
        }

    }

    public  void setPlayTime(long playTime) {
        if(audioDatas.size() <= 0){
            return;
        }
        this.playTime = playTime;
        checkAndPlay();
        if((audioData != null && audioData.isPlaying()) || audioPlayer.isPlaying()){
            return;
        }
        //最近的audio
        SectionVO audio = getNearestAudioData(playTime);
        if(audio == null){
            return;
        }
        Log.e("nearest","audio:" + audio);
        if (audioData != null && audioData.equals(audio)) {
            return;
        }
        Log.e("AudioData", "id:" + audioData.getId());
        audioData = audio;
        prepareAudio(audioData);

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (audioData != null) {
            Log.e("check_play", "on prepared,id:" + audioData.getId());
            audioData.setPrepared(true);
            audioData.setPreparing(false);
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        mp.reset();
        audioData.setPrepared(false);
        audioData.setPlaying(false);
        audioData.setPreparing(false);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    private SectionVO getNearestAudioData(long playTime) {
        if (audioDatas.size() > 0) {
            int index = 0;
            for (int i = 0; i < audioDatas.size(); ++i) {
                //4591,37302
                long interval = audioDatas.get(i).getStartTime() - playTime;
                if(interval > 0){
                    index = i;
                    break;
                }

            }
            return audioDatas.get(index);

        }
        return null;
    }

    private void checkAndPlay() {
        if(audioData == null){
            return;
        }
        if (playTime > audioData.getStartTime() && playTime < audioData.getEndTime()) {
            if (audioData.isPrepared()) {
                if (!audioData.isPlaying() && !audioPlayer.isPlaying()) {
                    audioData.setPrepared(true);
                    audioData.setPreparing(false);
                    audioData.setPlaying(true);
                    audioPlayer.start();
                    Log.e("check_play", "start play ,id:" + audioData.getId());
                }

            }
        }

    }

    public void release(){
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.reset();
            audioPlayer.release();
            audioPlayer = null;
        }

        audioData = null;
        instance = null;
    }

}
