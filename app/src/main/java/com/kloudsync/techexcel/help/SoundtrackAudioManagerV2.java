package com.kloudsync.techexcel.help;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel.bean.SoundtrackMediaInfo;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.tool.DocumentModel;
import com.kloudsync.techexcel.tool.SoundtrackAudioCache;
import com.ywl5320.wlmedia.WlMedia;
import com.ywl5320.wlmedia.enums.WlPlayModel;
import com.ywl5320.wlmedia.listener.WlOnCompleteListener;
import com.ywl5320.wlmedia.listener.WlOnErrorListener;
import com.ywl5320.wlmedia.listener.WlOnLoadListener;
import com.ywl5320.wlmedia.listener.WlOnPreparedListener;
import com.ywl5320.wlmedia.listener.WlOnTimeInfoListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SoundtrackAudioManagerV2 implements WlOnPreparedListener, WlOnCompleteListener, WlOnErrorListener, WlOnLoadListener, WlOnTimeInfoListener {

    private static SoundtrackAudioManagerV2 instance;
    private WlMedia audioPlayer;
    private volatile long playTime;
    private Context context;
    private SoundtrackMediaInfo mediaInfo;
    private Handler handler;
    /**
     * 播放总长
     */
    private int duration = -1;
    /**
     * 播放seek拖动进度
     */
    private double progress = 0;
    /**
     * 是否处于用户拖动状态
     */
    private boolean mIsSeekStatus = false;
    /**
     * 是否需要暂停
     */
    private boolean mIsPauseStatus = false;

    private boolean isPlaying;

    private boolean isLoading;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean ismIsPauseStatus() {
        return mIsPauseStatus;
    }

    public void setmIsPauseStatus(boolean mIsPauseStatus) {
        this.mIsPauseStatus = mIsPauseStatus;
    }

    private SoundtrackAudioManagerV2(Context context) {
        this.context = context;

    }

    public static SoundtrackAudioManagerV2 getInstance(Context context) {
        if (instance == null) {
            synchronized (SoundtrackAudioManagerV2.class) {
                if (instance == null) {
                    instance = new SoundtrackAudioManagerV2(context);
                }
            }
        }
        return instance;
    }

    public void setSoundtrackAudio(SoundtrackMediaInfo _mediaInfo) {

        this.mediaInfo = _mediaInfo;
        if (mediaInfo == null) {
            return;
        }
        this.mediaInfo.setPreparing(true);
        Log.e("check_soundtrack_play", "step_two:" + "do_play:mediaInfo" + mediaInfo);
        prepareAudioAndPlay(mediaInfo);
    }


    public void setSoundtrackAudioPlayAtTime(SoundtrackMediaInfo _mediaInfo, long time) {
        Log.e("check_play", "mediaInfo:" + mediaInfo);
        this.mediaInfo = _mediaInfo;
        mediaInfo.setTime(time);
        if (mediaInfo == null || this.mediaInfo.isPreparing()) {
            return;
        }
        this.mediaInfo.setPreparing(true);
//        predownSoundtrackAudio(context, mediaInfo.getAttachmentUrl());
        Observable.just("load_in_main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                prepareAudioAndPlay(mediaInfo);
            }
        });

    }

    public SoundtrackMediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void prepareAudioAndPlay(final SoundtrackMediaInfo audioData) {

        Log.e("check_soundtrack_play", "step_two:" + "prepareAudioAndPlay:mediaInfo" + mediaInfo);
        if (audioPlayer != null) {
            try {
                audioPlayer.stop();
                audioPlayer.release();
                audioPlayer = null;
            } catch (Exception e) {

            }
        }

        audioPlayer = new WlMedia();
        try {
            try {
                if (audioPlayer.isPlay()) {
                    return;
                }
            } catch (IllegalStateException exception) {

            }
            audioPlayer.setOnLoadListener(this);
            audioPlayer.setOnPreparedListener(this);
            audioPlayer.setOnCompleteListener(this);
            audioPlayer.setOnErrorListener(this);
            audioPlayer.setOnTimeInfoListener(this);
            Uri uri = null;
//                String sourcePath = "";
//                if (audioCache.containFile(audioData.getAttachmentUrl())) {
//                    File file = new File(audioCache.getAudioPath(audioData.getAttachmentUrl()));
//                    if (file.exists()) {
//                        sourcePath = file.getAbsolutePath();
////
//                    } else {
//                        sourcePath = audioData.getAttachmentUrl();
//                    }
//
//                } else {
//
//
//                }

            Log.e("check_soundtrack_play", "step_three:setSource:" + audioData.getAttachmentUrl());
            audioPlayer.setSource(audioData.getAttachmentUrl());
//                    audioPlayer.setDataSource(context, Uri.parse(URLDecoder.decode(audioData.getAttachmentUrl(),"UTF-8")));
            audioPlayer.setPlayModel(WlPlayModel.PLAYMODEL_ONLY_AUDIO);

            try {
                Log.e("check_soundtrack_play", "step_four:prepared:");
                audioPlayer.prepared();
            } catch (IllegalStateException e) {
                Log.e("check_prepared_and_play", "IllegalStateException:" + e.getMessage());
                reinit(audioData);
            }

            audioData.setPreparing(false);
            audioData.setPrepared(true);

        } catch (Exception e) {
            e.printStackTrace();
            audioData.setPreparing(false);
        }

    }

    private void reinit(SoundtrackMediaInfo mediaInfo) {

        audioPlayer = null;
        audioPlayer = new WlMedia();
        try {
            audioPlayer.setSource(mediaInfo.getAttachmentUrl());
            audioPlayer.setPlayModel(WlPlayModel.PLAYMODEL_ONLY_AUDIO);
            audioPlayer.prepared();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean isPlaying() {
//        if (mediaInfo == null) {
//            return false;
//        }
//
//        if (audioPlayer != null) {
//            return audioPlayer.isPlay();
//        }
        return isPlaying;
    }


    public long getPlayTime() {
        Log.e("check_prepared_and_play", "mediaInfo:" + mediaInfo + ",audioPlayer:" + audioPlayer);
        if (mediaInfo == null) {
            return 0;
        }
        if (audioPlayer == null) {
            return 0;
        }

        return (long) audioPlayer.getNowClock() * 1000;
    }

    public long getTotalTime() {
        if (mediaInfo == null) {
            return 0;
        }
        return (long) audioPlayer.getDuration() * 1000;
    }

    public void release() {
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.release();
            audioPlayer = null;
        }
        mIsPauseStatus = false;
        mIsSeekStatus = false;
        isPlaying = false;
        mediaInfo = null;
        instance = null;
        progress = 0;
    }

    public long getDuration() {
        if (mediaInfo == null) {
            return 0;
        }
        return (long) audioPlayer.getDuration() * 1000;
    }

    public void pause() {
        if (mediaInfo == null) {
            return;
        }
        if (audioPlayer != null) {

            audioPlayer.pause();

            isPlaying = false;
        }

    }

    public void restart() {
        if (mediaInfo == null) {
            return;
        }
        if (audioPlayer != null) {
            audioPlayer.resume();
        }

    }

    public void start() {
        if (mediaInfo == null) {
            return;
        }
        if (audioPlayer != null) {
            Log.e("check_soundtrack_play", "start");
            audioPlayer.start();
        }

    }

    public void seekTo(int time) {
        if (mediaInfo == null) {
            return;
        }
        if (audioPlayer != null) {
            Log.e("check_soundtrack_play", "seekTo:" + time);
            audioPlayer.seek(time);
            //audioPlayer.seek(time / 1000);
            Log.e("vedio_check", "seek_to,time:" + time);
        }
    }

    private SoundtrackAudioCache audioCache;

    private void queryDocumentAndDownLoad(final String url, final String savePath) throws MalformedURLException {
        Observable.just(url).observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                String newUrl = url;
                URL _url = new URL(url);
                Log.e("check_url_path", _url.getPath());
                String path = _url.getPath();
                if (!TextUtils.isEmpty(path)) {

                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }

                    int index = path.lastIndexOf("/");
                    if (index >= 0 && index < path.length()) {
                        String centerPart = path.substring(0, index);
                        String fileName = path.substring(index + 1, path.length());
                        Log.e("check_transform_url", "centerPart:" + centerPart + ",fileName:" + fileName);
                        if (!TextUtils.isEmpty(centerPart)) {
                            JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                                    centerPart);
                            if (queryDocumentResult != null) {
                                Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
                                String part = "";
                                if (uploadao != null) {
                                    if (1 == uploadao.getServiceProviderId()) {
                                        part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
                                                + "/" + fileName;
                                    } else if (2 == uploadao.getServiceProviderId()) {
                                        part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/" + fileName;
                                    }
                                    newUrl = part;
                                    Log.e("check_transform_url", "url:" + url);
                                }

                            }
                        }
                    }
                }

                return newUrl;
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String _newUrl) throws Exception {
//                safeDownloadFile(_newUrl, savePath, true);
            }
        }).subscribe();


    }

    private Uploadao parseQueryResponse(final String jsonstring) {
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject data = returnjson.getJSONObject("Data");

                JSONObject bucket = data.getJSONObject("Bucket");
                Uploadao uploadao = new Uploadao();
                uploadao.setServiceProviderId(bucket.getInt("ServiceProviderId"));
                uploadao.setRegionName(bucket.getString("RegionName"));
                uploadao.setBucketName(bucket.getString("BucketName"));
                return uploadao;
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    /**
     * 停止播放
     */
    public void stop() {
        mIsSeekStatus = true;
        if (mediaInfo == null) {
            return;
        }
        if (audioPlayer != null) {
            Log.e("check_soundtrack_play", "stop");
            audioPlayer.stop();
        }
    }

    public void stopForSeek(double progress) {
        mIsSeekStatus = true;
        if (mediaInfo == null) {
            return;
        }
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        this.progress = progress;

        if (audioPlayer != null) {
            audioPlayer.prepared();
        }
    }

    public void seek(double progress) {

        if (mediaInfo == null) {
            return;
        }

        if (progress >= audioPlayer.getDuration()) {
            progress = audioPlayer.getDuration();
        }

        mediaInfo.setPlayType(SoundtrackMediaInfo.TYPE_SEEK);
        mediaInfo.setPlaying(isPlaying);
        mediaInfo.setSeekProgress(progress);
        Log.e("check_soundtrack_play", "seek:" + isPlaying() + ",progress:" + progress + ",thread:" + Thread.currentThread());
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
    }

    public void preparedForSeek() {
        if (audioPlayer != null) {
            if (mediaInfo != null) {
                audioPlayer.prepared();
            }
            Log.e("check_soundtrack_play", "prepared for seek");

        }
    }


    public OnAudioInfoCallBack onAudioInfoCallBack;

    public void setOnAudioInfoCallBack(OnAudioInfoCallBack onAudioInfoCallBack) {
        this.onAudioInfoCallBack = onAudioInfoCallBack;
    }

    @Override
    public void onLoad(boolean load) {
        isLoading = load;
        if (load) {
            if (onAudioInfoCallBack != null) {
                onAudioInfoCallBack.onAudioLoding();
            }
        }
        Log.e("check_soundtrack_play", "onLoad:" + load);
    }

    @Override
    public void onTimeInfo(double currentTime) {
        Log.e("check_soundtrack_play", "onTimeInfo:" + currentTime + ",Thread:" + Thread.currentThread());
        if(mediaInfo == null){
            return;
        }
        if (mediaInfo.getPlayType() == SoundtrackMediaInfo.TYPE_SEEK) {
            mediaInfo.setPlayType(SoundtrackMediaInfo.TYPE_PLAY);
            if (!mediaInfo.isPlaying()) {
                pause();
                if (onAudioInfoCallBack != null) {
                    onAudioInfoCallBack.seekCompletionAndPause(currentTime);
                }
                return;
            }
        }
        isPlaying = true;
        if (onAudioInfoCallBack != null) {
            onAudioInfoCallBack.onAudioPlayTimeInfo((long) (currentTime * 1000));
        }
    }

    @Override
    public void onComplete() {
        if(isError){
            isError = false;
            if(mediaInfo != null){
                reinit(mediaInfo);
            }
            return;
        }
        isPlaying = false;
        if (onAudioInfoCallBack != null) {
            onAudioInfoCallBack.onCompletionCalled();
        }
        Log.e("check_soundtrack_play", "onComplete");

    }

    boolean isError = false;


    @Override
    public void onError(int code, String msg) {
        isPlaying = false;
        isError = true;

        Log.e("check_soundtrack_play", "onError:" + code + ",msg:" + msg);
    }

    @Override
    public void onPrepared() {
        Log.e("check_soundtrack_play", "onPrepared" + mediaInfo);
        if (mediaInfo == null) {
            return;
        }

        isLoading = false;

        Observable.just("load_main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (mediaInfo.getPlayType() == SoundtrackMediaInfo.TYPE_PLAY) {
                    audioPlayer.start();
                    isPlaying = true;
                } else if (mediaInfo.getPlayType() == SoundtrackMediaInfo.TYPE_SEEK) {
                    Log.e("check_soundtrack_play", "onPrepared,do_seek");
                    audioPlayer.seek(mediaInfo.getSeekProgress());
                    if (mediaInfo.isPlaying()) {
                        isPlaying = true;
                        audioPlayer.start();
                    } else {
                        isPlaying = false;
                        audioPlayer.start();

                    }
                }
            }
        });

    }

    public interface OnAudioInfoCallBack {
        void onAudioPlayTimeInfo(long playTime);

        void onCompletionCalled();

        void seekCompletionAndPause(double playTime);

        void onAudioLoding();
    }

}
