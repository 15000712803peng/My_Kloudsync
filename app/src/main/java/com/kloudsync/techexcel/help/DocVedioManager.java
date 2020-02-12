package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.VedioData;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.ub.teacher.gesture.ShowChangeLayout;
import com.ub.teacher.gesture.VideoGestureRelativeLayout;
import com.ub.techexcel.bean.SectionVO;
import com.ub.techexcel.view.ControllerVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DocVedioManager  implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener ,VideoGestureRelativeLayout.VideoGestureListener,MediaPlayer.OnErrorListener,MyVedioController {

    private static DocVedioManager instance;
    private Context context;
    private MeetingConfig meetingConfig;
    private RelativeLayout vedioLayout;
    private VideoGestureRelativeLayout vedioGesture;
    private TextView loadingText;
    //
    private DocVedioManager(Context context) {
        this.context = context;

    }



    public static DocVedioManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DocVedioManager.class) {
                if (instance == null) {
                    instance = new DocVedioManager(context);
                }
            }
        }
        return instance;
    }

    public void play(Context context,RelativeLayout vedioLayout,MeetingConfig meetingConfig,int id){
        this.vedioLayout = vedioLayout;
        this.context = context;
        this.meetingConfig = meetingConfig;
        if(vedioData == null || vedioData.getId() != id){
            VedioData _vedioData =  new VedioData();
            _vedioData.setId(id);
            requestAndPlay(_vedioData);
        }else if(vedioData.getId() == id && !TextUtils.isEmpty(vedioData.getUrl())){
            doPlay(vedioData);
        }
    }

    private void requestAndPlay(VedioData vedioData){

        Observable.just(vedioData).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<VedioData>() {
            @Override
            public void accept(VedioData vedioData) throws Exception {
                vedioLayout.setVisibility(View.VISIBLE);
                loadingText = vedioLayout.findViewById(R.id.txt_vedio_loading);
                loadingText.setVisibility(View.VISIBLE);
            }
        }).observeOn(Schedulers.io()).map(new Function<VedioData, VedioData>() {
            @Override
            public VedioData apply(VedioData vedioData) throws Exception {
                JSONObject response = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC +
                        "FavoriteAttachment/GetFavoriteAttachmentByID?itemID=" + vedioData.getId());
                if(response != null){
                    if(response.getInt("RetCode") == 0){
                        JSONObject data = response.getJSONObject("RetData");
                        if(data != null && data.has("AttachmentUrl")){
                            vedioData.setUrl(data.getString("AttachmentUrl"));
                        }
                    }
                }
                Log.e("doPrepare","vedio_url:" + vedioData.getUrl());
                return vedioData;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<VedioData>() {
            @Override
            public void accept(VedioData vedioData) throws Exception {
                doPlay(vedioData);
            }
        });
    }

    public void doPlay(VedioData vedioData){
        this.vedioData = vedioData;
        Observable.just(vedioData).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<VedioData>() {
            @Override
            public void accept(VedioData vedioData) throws Exception {
                initPlayerAndPlay(vedioData);
            }
        }).subscribe();
    }


    public void doPlay(Context context,RelativeLayout vedioLayout,MeetingConfig meetingConfig,VedioData vedioData){

        this.context = context;
        this.vedioLayout = vedioLayout;
        this.meetingConfig = meetingConfig;
        if(isPlaying()){
            return;
        }else {
            try {
                if(this.vedioData != null && this.vedioData.isPrepared()){
                    vedioPlayer.start();
                    return;
                }
            }catch (Exception e){

            }
        }
        this.vedioData = vedioData;
        Observable.just(vedioData).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<VedioData>() {
            @Override
            public void accept(VedioData vedioData) throws Exception {
                initPlayerAndPlay(vedioData);
            }
        }).subscribe();
    }

    private ControllerVideoView vedioPlayer;
    private ShowChangeLayout statusLayout;
    private void initPlayerAndPlay(VedioData vedioData){
        Log.e("check_thread","thread:" + Thread.currentThread());
        vedioLayout.setVisibility(View.VISIBLE);
        vedioPlayer = vedioLayout.findViewById(R.id.doc_video);
        vedioPlayer.setVisibility(View.VISIBLE);
        vedioGesture = vedioLayout.findViewById(R.id.vedio_gesture);
        vedioGesture.setVideoGestureListener(this);
        loadingText = vedioLayout.findViewById(R.id.txt_vedio_loading);
        loadingText.setVisibility(View.VISIBLE);
        statusLayout = vedioLayout.findViewById(R.id.layout_vedio_status);
        MediaController mediaController = new MediaController(context);
        vedioPlayer.setMediaController(mediaController);
        vedioPlayer.setVedioController(this);
        vedioPlayer.setZOrderOnTop(true);
        vedioPlayer.setZOrderMediaOverlay(true);
        vedioPlayer.setOnPreparedListener(this);
        vedioPlayer.setOnCompletionListener(this);
        vedioPlayer.setOnErrorListener(this);
        vedioPlayer.setVideoURI(Uri.parse(vedioData.getUrl()));
        vedioPlayer.start();
        vedioData.setPrepared(true);
        this.vedioData = vedioData;
    }

    private int vedioId;
    private volatile JSONObject _data;
    private volatile VedioData vedioData;

    public void prepareVedio(String data) {
        if(TextUtils.isEmpty(data)){
            return;
        }

        try {
             _data = new JSONObject(data);
            if(_data.has("type")){
                if(_data.getInt("type") != 33){
                    return;
                }
                if(_data.has("Info")){
                    JSONObject vedioInfo = _data.getJSONObject("Info");
                    vedioId = vedioInfo.getInt("id");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(vedioId == 0){
            return;
        }
        doPrepare(vedioId);
    }

    private void doPrepare(final int vedioId){
        vedioData = new VedioData();
        vedioData.setId(vedioId);
        Observable.just(vedioData).observeOn(Schedulers.io()).map(new Function<VedioData, VedioData>() {
            @Override
            public VedioData apply(VedioData vedioData) throws Exception {
                JSONObject response = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC +
                        "FavoriteAttachment/GetFavoriteAttachmentByID?itemID=" + vedioData.getId());
                if(vedioId != vedioData.getId()){
                    return vedioData;
                }

                if(response != null){
                    if(response.getInt("RetCode") == 0){
                        JSONObject data = response.getJSONObject("RetData");
                        if(data != null && data.has("AttachmentUrl")){
                            vedioData.setUrl(data.getString("AttachmentUrl"));
                        }
                    }
                }
                Log.e("doPrepare","vedio_url:" + vedioData.getUrl());
                return vedioData;
            }
        }).subscribe();
    }

    public void close(){
        notifyVedioState(SocketMessageManager.MESSAGE_VIDEO_CLOSE,vedioData);
        Observable.just("close").observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if(vedioPlayer != null){
                    try {
                        vedioPlayer.suspend();
                        vedioPlayer.setVisibility(View.GONE);
                        vedioLayout.setVisibility(View.GONE);
                    }catch (Exception e){

                    }
                }
            }
        }).subscribe();
        vedioData = null;

    }

    public void doPause(){

        if(isPlaying()){
            vedioPlayer.pause();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Observable.just(mp).delay(800, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MediaPlayer>() {
            @Override
            public void accept(MediaPlayer mediaPlayer) throws Exception {
                if(mediaPlayer != null  && loadingText != null){
                    Log.e("check_thread","thread_set_gone:" + Thread.currentThread());
                    loadingText.setVisibility(View.GONE);
                }
            }
        }).subscribe();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        close();
    }

    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    }

    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    }

    @Override
    public void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {

    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {

    }

    @Override
    public void onDown(MotionEvent e) {

    }

    @Override
    public void onEndFF_REW(MotionEvent e) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("play_onError","on_error:" + what);
        return false;
    }

    @Override
    public void start() {
        Log.e("DocVedioManager","start");
        notifyVedioState(SocketMessageManager.MESSAGE_VIDEO_PLAY,vedioData);
    }

    @Override
    public void pause() {
        Log.e("DocVedioManager","pasue");
        notifyVedioState(SocketMessageManager.MESSAGE_VIDEO_PAUSE,vedioData);
    }

    @Override
    public int getDuration() {
        if(vedioPlayer != null){
            try {
                return vedioPlayer.getDuration();
            }catch (Exception e){

            }
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(vedioPlayer != null){
            try {
                return vedioPlayer.getCurrentPosition();
            }catch (Exception e){

            }

        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        if(vedioPlayer != null){
            try {
                return vedioPlayer.isPlaying();
            }catch (Exception e){

            }

        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        if(vedioPlayer != null){
            try {
                return vedioPlayer.getBufferPercentage();
            }catch (Exception e){

            }

        }
        return 0;
    }

    @Override
    public boolean canPause() {
        if(vedioPlayer != null){
            try {
                return vedioPlayer.canPause();
            }catch (Exception e){

            }

        }
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        if(vedioPlayer != null){
            try {
                return vedioPlayer.canSeekBackward();
            }catch (Exception e){

            }
        }
        return false;
    }

    @Override
    public boolean canSeekForward() {
        if(vedioPlayer != null){
            try {
                return vedioPlayer.canSeekForward();
            }catch (Exception e){

            }

        }
        return false;
    }

    @Override
    public int getAudioSessionId() {

        return 0;
    }



    private void notifyVedioState(int state,VedioData vedioData) {
        Log.e("notifyVedioState", "vedioData:" + vedioData);
        if(vedioData == null || vedioPlayer == null){
            return;
        }
        float time = getCurrentPosition() / 1000;
        if (meetingConfig.getType() != MeetingType.MEETING) {
            SocketMessageManager.getManager(context).sendMessage_VedioPlayedStatus(state,time,vedioData.getId()+"",vedioData.getUrl());
        } else {
            if (!AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                return;
            }
            SocketMessageManager.getManager(context).sendMessage_VedioPlayedStatus(state,time,vedioData.getId()+"",vedioData.getUrl());

        }
    }







}
