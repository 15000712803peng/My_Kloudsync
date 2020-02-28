package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.kloudsync.techexcel.bean.WebVedio;
import com.ub.techexcel.bean.SectionVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by tonyan on 2019/11/21.
 */

public class RecordShareVedioManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static RecordShareVedioManager instance;
    private MediaPlayer vedioPlayer;
    private volatile long playTime;
    private Context context;
    //
    private List<SectionVO> audioDatas = new ArrayList<>();
    private SectionVO audioData;
    private SurfaceView surfaceView;

    private RecordShareVedioManager(Context context) {
        this.context = context;
        vedioPlayer = new MediaPlayer();
    }

    public static RecordShareVedioManager getInstance(Context context) {
        if (instance == null) {
            synchronized (RecordShareVedioManager.class) {
                if (instance == null) {
                    instance = new RecordShareVedioManager(context);
                }
            }
        }
        return instance;
    }

    public void setVedioDatas(List<SectionVO> audioDatas) {
        this.audioDatas.clear();
        this.audioDatas.addAll(audioDatas);
        Collections.sort(audioDatas, new Comparator<SectionVO>() {
            @Override
            public int compare(SectionVO o1, SectionVO o2) {
                return o1.getStartTime() - o2.getStartTime();
            }
        });
        if (audioDatas.size() > 0) {
            audioData = audioDatas.get(0);
            prepareVedio(audioData);
        }
    }

    public void prepareVedio(SectionVO audioData) {
        try {
            if (audioData.isPreparing() || audioData.isPrepared()) {
                return;
            }
            if (vedioPlayer == null) {
                if (vedioPlayer == null) {
                    vedioPlayer = new MediaPlayer();
                    initSurface(surfaceView);
                }
            }
            try {

                if (vedioPlayer.isPlaying()) {
                    return;
                }
            } catch (IllegalStateException e) {

            }

            audioData.setPreparing(true);
            try {
                vedioPlayer.reset();
                vedioPlayer.setDataSource(context, Uri.parse(audioData.getFileUrl()));
                vedioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                vedioPlayer.prepareAsync();
            } catch (IllegalStateException e) {
                reinit(audioData);
            }
            vedioPlayer.setOnPreparedListener(this);
            vedioPlayer.setOnCompletionListener(this);
            vedioPlayer.setOnErrorListener(this);
        } catch (IOException e) {
            e.printStackTrace();
            audioData.setPreparing(false);
        }

    }

    public void prepareVedioAndPlay(SectionVO audioData) {
        try {
            if (audioData.isPreparing() || audioData.isPrepared()) {
                return;
            }
            if (vedioPlayer == null) {
                if (vedioPlayer == null) {
                    vedioPlayer = new MediaPlayer();
                    initSurface(surfaceView);
                }
            }
            try {

                if (vedioPlayer.isPlaying()) {
                    return;
                }
            } catch (IllegalStateException e) {

            }

            audioData.setPreparing(true);
            try {
                vedioPlayer.reset();
                vedioPlayer.setDataSource(context, Uri.parse(audioData.getFileUrl()));
                vedioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                vedioPlayer.prepare();
                audioData.setPrepared(true);
                audioData.setPreparing(false);
                vedioPlayer.start();
            } catch (IllegalStateException e) {
                reinit(audioData);
            }

            vedioPlayer.setOnCompletionListener(this);
            vedioPlayer.setOnErrorListener(this);
        } catch (IOException e) {
            e.printStackTrace();
            audioData.setPreparing(false);
        }

    }


    public void setPlayTime(long playTime) {
        if (audioDatas.size() <= 0) {
            return;
        }
        this.playTime = playTime;

        if ((audioData != null && audioData.isPlaying())) {
//            if (audioData.getEndTime() > playTime || playTime < audioData.getStartTime()) {
//                if (surfaceView.getVisibility() == View.VISIBLE) {
//                    surfaceView.setVisibility(View.GONE);
//                    try {
//                        if (vedioPlayer != null) {
//                            vedioPlayer.stop();
//                            vedioPlayer.reset();
//                            audioData = null;
//                        }
//                    } catch (Exception exception) {
//
//                    }
//
//                }
//            }
            return;
        }

        try {
            if (vedioPlayer != null && vedioPlayer.isPlaying()) {
                return;
            }
        } catch (IllegalStateException exception) {

        }

        checkAndPlay();
        //最近的audio
        SectionVO audio = getNearestAudioData(playTime);
        if (audio == null) {
            return;
        }

        Log.e("nearest", "audio:" + audio);
        if (audioData != null && audioData.equals(audio)) {
            return;
        }
        audioData = audio;
        Log.e("AudioData", "id:" + audioData.getId());
        prepareVedio(audioData);

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
        if (surfaceView != null) {
            surfaceView.setVisibility(View.GONE);
        }
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
                if (interval > 0) {
                    index = i;
                    break;
                }

            }
            return audioDatas.get(index);

        }
        return null;
    }

    private void checkAndPlay() {
        if (audioData == null) {
            return;
        }

        if(audioData.isPreparing()){
            return;
        }

        if (playTime > audioData.getStartTime() && playTime < audioData.getEndTime()) {
            if (audioData.isPrepared()) {
                if (!audioData.isPlaying() && !vedioPlayer.isPlaying()) {
                    audioData.setPrepared(true);
                    audioData.setPreparing(false);
                    audioData.setPlaying(true);
                    vedioPlayer.start();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (surfaceView != null) {
                                surfaceView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    Log.e("check_play_share", "start play ,id:" + audioData.getId());
                }
            } else {
                Log.e("check_play_share", "start play ,id:" + audioData.getId());

                prepareVedioAndPlay(audioData);
            }
        }

        if (audioData.getEndTime() > playTime || playTime < audioData.getStartTime()) {
            if (surfaceView.getVisibility() == View.VISIBLE) {
                surfaceView.setVisibility(View.GONE);
                try {
                    if (vedioPlayer != null) {
                        vedioPlayer.stop();
                        vedioPlayer.reset();
                    }
                } catch (Exception exception) {

                }

            }
        }

    }

    public void pause() {
        try {
            if (vedioPlayer != null) {
                if (vedioPlayer.isPlaying()) {
                    vedioPlayer.pause();
                }
            }
        } catch (Exception e) {

        }
    }

    public void restart() {
        try {
            if (vedioPlayer != null) {
                if (!vedioPlayer.isPlaying()) {
                    vedioPlayer.start();
                }
            }
        } catch (Exception e) {

        }
    }

    public void release() {
        if (vedioPlayer != null) {
            vedioPlayer.stop();
            vedioPlayer.reset();
            vedioPlayer.release();
            vedioPlayer = null;

        }

        audioData = null;
        instance = null;
    }

    private class SurfaceCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //调用MediaPlayer.setDisplay(holder)设置surfaceHolder，surfaceHolder可以通过surfaceview的getHolder()方法获得

            Log.e("WebVedioManager", "surfaceCreated");
            if (vedioPlayer != null) {
                vedioPlayer.setDisplay(holder);
            }
        }

        /**
         * 当SurfaceHolder的尺寸发生变化的时候被回调
         *
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
//            release();
        }
    }

    private void reinit(SectionVO vedioData) {
        vedioPlayer = null;
        vedioPlayer = new MediaPlayer();
        refreshSurface();
        try {
            vedioPlayer.setDataSource(context, Uri.parse(vedioData.getFileUrl()));
            vedioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            vedioPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            vedioData.setPreparing(false);
        }

    }

    public void initSurface(SurfaceView surfaceView) {
        //给surfaceHolder设置一个callback
        this.surfaceView = surfaceView;
        surfaceView.getHolder().addCallback(new RecordShareVedioManager.SurfaceCallBack());
    }

    private void refreshSurface() {
        if (this.surfaceView != null) {
            this.surfaceView.getHolder().addCallback(new RecordShareVedioManager.SurfaceCallBack());
        }
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        initSurface(surfaceView);
    }

}
