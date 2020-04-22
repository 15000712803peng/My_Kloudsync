package com.kloudsync.techexcel.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventCloseSoundtrack;
import com.kloudsync.techexcel.bean.EventCloseWebView;
import com.kloudsync.techexcel.bean.EventPageActionsForSoundtrack;
import com.kloudsync.techexcel.bean.EventPageNotesForSoundtrack;
import com.kloudsync.techexcel.bean.EventPlayWebVedio;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.SeekData;
import com.kloudsync.techexcel.bean.SoundtrackDetail;
import com.kloudsync.techexcel.bean.SoundtrackMediaInfo;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.PageActionsAndNotesMgr;
import com.kloudsync.techexcel.help.SoundtrackActionsManagerV2;
import com.kloudsync.techexcel.help.SoundtrackAudioManagerV2;
import com.kloudsync.techexcel.help.SoundtrackBackgroundMusicManager;
import com.kloudsync.techexcel.help.SoundtrackDigitalNoteManager;
import com.kloudsync.techexcel.help.UserVedioManager;
import com.kloudsync.techexcel.help.WebVedioManager;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.kloudsync.techexcel.tool.SyncWebActionsCache;
import com.ub.techexcel.bean.PartWebActions;
import com.ub.techexcel.bean.WebAction;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SoundtrackPlayManager implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, SoundtrackAudioManagerV2.OnAudioInfoCallBack {
    public Activity host;
    public int width;
    public int heigth;
    private SoundtrackDetail soundtrackDetail;
    //view
    // play status
    private volatile long playTime;
    private volatile boolean isFinished;
    // data
    private static Handler playHandler;
    // message
    private static final int MESSAGE_PLAY_TIME_REFRESHED = 1;
    private static final int MESSAGE_HIDE_CENTER_LOADING = 2;
    private static final int MESSAGE_PLAY_START = 3;
    private static final int MESSAGE_PLAY_FINISH = 4;
    SoundtrackActionsManagerV2 actionsManager;
    SoundtrackAudioManagerV2 soundtrackAudioManager;
    SoundtrackBackgroundMusicManager backgroundMusicManager;
    private MeetingConfig meetingConfig;
    private TextView playTimeText;
    private SeekBar seekBar;
    private RelativeLayout webVedioLayout;
    private SurfaceView webVedioSurface;
    private ImageView closeVedioImage;
    private ImageView closeDialogImage;
    private ImageView startPauseImage;
    private LinearLayout controllerLayout;
    private SyncWebActionsCache webActionsCache;
    private static final int TYPE_SOUNDTRACK_STOP = 0;
    private static final int TYPE_SOUNDTRACK_PLAY = 1;
    private static final int TYPE_SOUNDTRACK_PAUSE = 2;
    private static final int TYPE_SOUNDTRACK_RESTART = 3;
    private static final int TYPE_SOUNDTRACK_FOLLOW = 4;
    private static final int TYPE_SOUNDTRACK_SEEK_PLAY = 5;
    private static final int TYPE_SOUNDTRACK_SEEK_STOP = 6;
    //
    String totalTimeStr = "00:00";
    String timeStr = "00:00/00:00";
    private long totalTime;
    //
    UserVedioManager userVedioManager;
    WebView web;
    TextView statusText;
    private TextView onlyShowTimeText;
    private ImageView hideControllerImage;
    //------
    private RelativeLayout smallNoteLayout;
    private WebView smallNoteWeb;
    private WebView mainNoteWeb;
    private ProgressBar loadingBar;

    private RelativeLayout soundtrackPlayLayout;

    private ImageView bottomMenu;

    private boolean isSeeking;
    private boolean isPlaying;
    private boolean isClosed;
    private int MESSAGE_CHECK_LOADING = 1;

    private Handler reinitHandler;

    private void resetStatus() {
        isSeeking = false;
        isPlaying = false;
    }


    public boolean isSeeking() {
        return isSeeking;
    }

    public void setSeeking(boolean seeking) {
        isSeeking = seeking;
    }

    public boolean isPlaying() {

        boolean _isPlaying = false;
        if (soundtrackAudioManager != null) {
            _isPlaying = soundtrackAudioManager.isPlaying();
        }
        return _isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    private long backgrouondDuration;

    public void setSoundtrackDetail(SoundtrackDetail soundtrackDetail) {

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        reinitHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_LOADING, 2000);
        this.soundtrackDetail = soundtrackDetail;
        totalTime = soundtrackDetail.getDuration();
        int max = (int) (totalTime / 100);
        seekBar.setMax(max);
        width = (int) (host.getResources().getDisplayMetrics().widthPixels);
        heigth = (int) (host.getResources().getDisplayMetrics().heightPixels);
        actionsManager = SoundtrackActionsManagerV2.getInstance(host);
        actionsManager.setWeb(web, meetingConfig);
        actionsManager.setUserVedioManager(userVedioManager);
        actionsManager.setSurfaceView(webVedioSurface);
        actionsManager.setRecordId(soundtrackDetail.getSoundtrackID());
        final String time = new SimpleDateFormat("mm:ss").format(playTime);
        final String _time = new SimpleDateFormat("mm:ss").format(totalTime);
        playTimeText.setText(time + "/" + _time);
    }

    public SoundtrackPlayManager(Activity host, SoundtrackDetail soundtrackDetail, MeetingConfig meetingConfig, RelativeLayout view) {
        this.meetingConfig = meetingConfig;
        Log.e("check_dialog", "new_dialog");
        this.host = host;
        this.soundtrackPlayLayout = view;
        this.soundtrackDetail = soundtrackDetail;
        totalTime = soundtrackDetail.getDuration();
        initManager(view);
        //TODO
        //  SoundtrackDigitalNoteManager.getInstance(host).initViews(meetingConfig, smallNoteLayout, smallNoteWeb, mainNoteWeb);
    }

    public SoundtrackPlayManager(Activity host, MeetingConfig meetingConfig, RelativeLayout view) {
        this.meetingConfig = meetingConfig;
        Log.e("check_dialog", "new_dialog");
        this.host = host;
        this.soundtrackPlayLayout = view;
        initManager(view);
        reinitHandler = new Handler(host.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                if (what == MESSAGE_CHECK_LOADING) {
                    if (soundtrackPlayLayout.getVisibility() == View.VISIBLE) {
                        Log.e("check_loading", "count:" + count + "，isloading，" + isLoading());
                        reinitHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_LOADING, 1200);
                        if (isLoading()) {
                            count++;
                            if (count >= 15) {
                                followClose();
                            }
                        } else {
                            count = 0;
                        }
                    }
                }
            }
        };
        //TODO
        //  SoundtrackDigitalNoteManager.getInstance(host).initViews(meetingConfig, smallNoteLayout, smallNoteWeb, mainNoteWeb);
    }

    public void init(Activity host, MeetingConfig meetingConfig, RelativeLayout view) {
        this.meetingConfig = meetingConfig;
        this.host = host;
        this.soundtrackPlayLayout = view;
        initManager(view);
        //TODO
//       SoundtrackDigitalNoteManager.getInstance(host).initViews(meetingConfig, smallNoteLayout, smallNoteWeb, mainNoteWeb);
    }

    private static boolean _fromUser;

    public void initManager(RelativeLayout view) {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        webActionsCache = SyncWebActionsCache.getInstance(host);
//        view = layoutInflater.inflate(R.layout.dialog_play_soundtrack, null);
//        dialog = new Dialog(host, R.style.my_dialog);
        smallNoteLayout = view.findViewById(R.id.layout_small_note);
//        smallNoteWeb = view.findViewById(R.id.small_web_note);
        mainNoteWeb = view.findViewById(R.id.main_note_web);
        smallNoteWeb = view.findViewById(R.id.small_web_note);
        webVedioSurface = view.findViewById(R.id.web_vedio_surface);
        web = view.findViewById(R.id.sync_web);
        loadingBar = view.findViewById(R.id.sondtrack_load_bar);
        statusText = view.findViewById(R.id.txt_status);
        onlyShowTimeText = view.findViewById(R.id.txt_only_show_time);
        onlyShowTimeText.setOnClickListener(this);
        hideControllerImage = view.findViewById(R.id.txt_hidden);
        hideControllerImage.setOnClickListener(this);
        webVedioLayout = view.findViewById(R.id.layout_web_vedio);
        playTimeText = view.findViewById(R.id.txt_play_time);
        seekBar = view.findViewById(R.id.seek_bar);
        controllerLayout = view.findViewById(R.id.layout_soundtrack_controller);
        seekBar.setOnSeekBarChangeListener(this);
        changeSeekbarStatusByRole();
        startPauseImage = view.findViewById(R.id.image_play_pause);
        startPauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermission = hasPermisson();

                if (!hasPermisson()) {
                    return;
                }

                if (isLoading()) {
                    return;
                }

                Log.e("check_permission", "have_permission:" + hasPermission + ",is_loading:" + soundtrackAudioManager.isLoading());

                if (soundtrackAudioManager.isPlaying()) {
                    pause();
                } else {
                    restart();
                }

            }
        });

        closeVedioImage = view.findViewById(R.id.image_close_veido);
        closeVedioImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasPermisson()) {
                    return;
                }

                notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_STOP, 0);
                WebVedioManager.getInstance(host).closeVedio();
                webVedioLayout.setVisibility(View.GONE);

            }
        });

        closeDialogImage = view.findViewById(R.id.soundtrack_close);
        closeDialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermisson()) {
                    close();
                }

            }
        });
        setControllerLayoutWithByOritation();
        initWeb();
        soundtrackAudioManager = SoundtrackAudioManagerV2.getInstance(host);
        soundtrackAudioManager.setOnAudioInfoCallBack(this);

    }

    private boolean hasPermisson() {
        Log.e("check_permisson", "meeting_config:" + meetingConfig);
        if (meetingConfig != null && meetingConfig.getType() == MeetingType.MEETING) {
            Log.e("check_permisson", "meeting_type:" + meetingConfig.getType());
            if (!TextUtils.isEmpty(meetingConfig.getPresenterId())) {
                Log.e("check_permisson", "presenter_id:" + meetingConfig.getPresenterId());
                if (meetingConfig.getPresenterId().equals(AppConfig.UserID)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private void setControllerLayoutWithByOritation() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) controllerLayout.getLayoutParams();
        if (Tools.isOrientationPortrait(host)) {
            //竖屏
            params.width = Tools.dip2px(host, 330);
            params.bottomMargin = Tools.dip2px(host, 20);

            Log.e("check_set_oritation", "width:" + Tools.dip2px(host, 360));
        } else {
            params.width = Tools.dip2px(host, 420);
        }
        controllerLayout.setLayoutParams(params);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.cancel:

                break;
            case R.id.txt_only_show_time:
                if (onlyShowTimeText.getVisibility() == View.VISIBLE) {
                    onlyShowTimeText.setVisibility(View.GONE);
                    controllerLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.txt_hidden:
                onlyShowTimeText.setVisibility(View.VISIBLE);
                controllerLayout.setVisibility(View.GONE);
                break;

            default:
                break;

        }
    }

    public void initLoading(ImageView bottomMenu) {
        this.bottomMenu = bottomMenu;
        bottomMenu.setImageResource(R.drawable.shape_transparent);
        bottomMenu.setEnabled(false);
        isClosed = false;
        playTime = 0;
        soundtrackPlayLayout.setVisibility(View.VISIBLE);
        playTimeText.setText("00:00" + "/" + "00:00");
        SoundtrackDigitalNoteManager.getInstance(host).initViews(meetingConfig, smallNoteLayout, smallNoteWeb, mainNoteWeb);
        web.setVisibility(View.GONE);
        mainNoteWeb.setVisibility(View.GONE);
        seekBar.setProgress(0);
        loadingBar.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.INVISIBLE);
    }

    public void doPlay(SoundtrackMediaInfo mediaInfo, long time) {

        Log.e("check_soundtrack_play", "step_one:" + "do_play:" + meetingConfig.getDocument());
        if (meetingConfig.getDocument() == null) {
            return;
        }
//        soundtrackAudioManager = SoundtrackAudioManagerV2.getInstance(host);
        if (soundtrackDetail == null) {
            return;
        }

        if (time > 0) {
            mediaInfo.setPlayType(SoundtrackMediaInfo.TYPE_SEEK);
            mediaInfo.setPlaying(true);
            mediaInfo.setSeekProgress(time / 1000);
        } else {
            mediaInfo.setPlayType(SoundtrackMediaInfo.TYPE_PLAY);
        }

        soundtrackAudioManager.setSoundtrackAudio(mediaInfo);
        soundtrackPlayLayout.setVisibility(View.VISIBLE);
        downloadActions(soundtrackDetail.getDuration(), soundtrackDetail.getSoundtrackID());
        notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_PLAY, 0);
        if (mediaInfo.getMediaType() == SoundtrackMediaInfo.MEDIA_TYPE_NEW_AUDIO) {
            backgroundMusicManager = SoundtrackBackgroundMusicManager.getInstance(host);
            backgroundMusicManager.setSoundtrackAudio(soundtrackDetail.getBackgroudMusicInfo(), soundtrackAudioManager);
        }

        Observable.just("preload").observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                syncDownloadFirst(soundtrackDetail.getSoundtrackID());
                Log.e("check_play_step", "step_one:preload");
            }

        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("check_play_step", "step_two:task_execute");
//                new PlayTimeTask().execute();
            }
        }).subscribe();
//        .map(new Function<String, String>() {
//            @Override
//            public String apply(String s) throws Exception {
////                https://peertime.oss-cn-shanghai.aliyuncs.com/NoteControlAction/37014/channel_1.json
//                final String centerPart = "NoteControlAction" + File.separator + soundtrackDetail.getSoundtrackID();
//                JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
//                        centerPart);
//                String url = "";
//                if (queryDocumentResult != null) {
//                    Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
//
//                    if (uploadao != null) {
//                        if (1 == uploadao.getServiceProviderId()) {
//                            url = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
//                                    + "/channel_1.json";
//                        } else if (2 == uploadao.getServiceProviderId()) {
//                            url = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/channel_1.json";
//                        }
//                        Log.e("check_transform_url", "url:" + url);
//                    }
//                }
//                return url;
//            }
//        }).doOnNext(new Consumer<String>() {
//            @Override
//            public void accept(String url) throws Exception {
//                if (!TextUtils.isEmpty(url)) {
//                    SoundtrackDigitalNoteManager.getInstance(host).doProcess(url);
//                }
//            }
//        })

    }


    public void doPause(SoundtrackMediaInfo mediaInfo, long time) {

        Log.e("check_soundtrack_play", "step_one:" + "do_play:" + meetingConfig.getDocument());
        if (meetingConfig.getDocument() == null) {
            return;
        }
//        soundtrackAudioManager = SoundtrackAudioManagerV2.getInstance(host);
        if (soundtrackDetail == null) {
            return;
        }

        mediaInfo.setPlayType(SoundtrackMediaInfo.TYPE_SEEK);
        mediaInfo.setPlaying(false);
        mediaInfo.setSeekProgress(time / 1000);

        soundtrackAudioManager.setSoundtrackAudio(mediaInfo);
        soundtrackPlayLayout.setVisibility(View.VISIBLE);
        downloadActions(soundtrackDetail.getDuration(), soundtrackDetail.getSoundtrackID());
        notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_PLAY, time);
        if (mediaInfo.getMediaType() == SoundtrackMediaInfo.MEDIA_TYPE_NEW_AUDIO) {
            backgroundMusicManager = SoundtrackBackgroundMusicManager.getInstance(host);
            backgroundMusicManager.setSoundtrackAudio(soundtrackDetail.getBackgroudMusicInfo(), soundtrackAudioManager);
        }


        Observable.just("preload").observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                syncDownloadFirst(soundtrackDetail.getSoundtrackID());
                Log.e("check_play_step", "step_one:preload");
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("check_play_step", "step_two:task_execute");
//                new PlayTimeTask().execute();
            }
        }).subscribe();
//        .map(new Function<String, String>() {
//            @Override
//            public String apply(String s) throws Exception {
////                https://peertime.oss-cn-shanghai.aliyuncs.com/NoteControlAction/37014/channel_1.json
//                final String centerPart = "NoteControlAction" + File.separator + soundtrackDetail.getSoundtrackID();
//                JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
//                        centerPart);
//                String url = "";
//                if (queryDocumentResult != null) {
//                    Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
//
//                    if (uploadao != null) {
//                        if (1 == uploadao.getServiceProviderId()) {
//                            url = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
//                                    + "/channel_1.json";
//                        } else if (2 == uploadao.getServiceProviderId()) {
//                            url = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/channel_1.json";
//                        }
//                        Log.e("check_transform_url", "url:" + url);
//                    }
//                }
//                return url;
//            }
//        }).doOnNext(new Consumer<String>() {
//            @Override
//            public void accept(String url) throws Exception {
//                if (!TextUtils.isEmpty(url)) {
//                    SoundtrackDigitalNoteManager.getInstance(host).doProcess(url);
//                }
//            }
//        })

    }


    private void release() {
        resetStatus();
        playHandler = null;
        seekBar.setProgress(0);
        if (userVedioManager != null) {
            userVedioManager.release();
        }
        if (soundtrackAudioManager != null) {
            soundtrackAudioManager.release();
        }
        if (actionsManager != null) {
            actionsManager.release();
        }
        if (backgroundMusicManager != null) {
            backgroundMusicManager.release();
        }
        SoundtrackDigitalNoteManager.getInstance(host).release();
    }

    private void handlePlayMessage(Message message) {
        switch (message.what) {
            case MESSAGE_PLAY_TIME_REFRESHED:
                seekBar.setProgress((int) (playTime / 100));
                break;
            case MESSAGE_HIDE_CENTER_LOADING:

                break;
            case MESSAGE_PLAY_START:

                break;

            case MESSAGE_PLAY_FINISH:
                close();
                break;
        }
    }

    private void pauseAtTime(long currentTime) {
        loadingBar.setVisibility(View.INVISIBLE);
        statusText.setVisibility(View.VISIBLE);
        statusText.setText(R.string.paused);
        startPauseImage.setImageResource(R.drawable.video_play);
        final String currenttime = new SimpleDateFormat("mm:ss").format(currentTime);
        final String _timeToatl = new SimpleDateFormat("mm:ss").format(totalTime);
        String time = currenttime + "/" + _timeToatl;
        playTimeText.setText(time);
        seekBar.setProgress((int) (currentTime / 100));
        String _time = time;
        if (time.contains("/")) {
            String[] parts = time.split("/");
            if (parts != null && parts.length > 0) {
                _time = parts[0];
            }
        }
//        seekBar.setProgress((int)(currentTime / 100));
        onlyShowTimeText.setText(_time);
        actionsManager.setPlayTime(currentTime);
        if (currentTime >= totalTime) {
            close();
        }
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    private int count = 0;

    private void moveByTime(long currentTime) {
        count++;
        Log.e("check_soundtrack_play", "move_by_time:" + currentTime);
        playTime = currentTime;
        if (onlyShowTimeText.getVisibility() != View.VISIBLE) {
            if (controllerLayout.getVisibility() != View.VISIBLE) {
                controllerLayout.setVisibility(View.VISIBLE);
            }
        }

        if (loadingBar.getVisibility() == View.VISIBLE) {
            loadingBar.setVisibility(View.INVISIBLE);
        }
        if (statusText.getVisibility() != View.VISIBLE) {
            statusText.setVisibility(View.VISIBLE);
        }

        statusText.setText(R.string.playing);
        startPauseImage.setImageResource(R.drawable.video_stop);
        final String currenttime = new SimpleDateFormat("mm:ss").format(currentTime);
        final String _timeToatl = new SimpleDateFormat("mm:ss").format(totalTime);
        String time = currenttime + "/" + _timeToatl;
        playTimeText.setText(time);
        String _time = time;
        if (time.contains("/")) {
            String[] parts = time.split("/");
            if (parts != null && parts.length > 0) {
                _time = parts[0];
            }
        }
        seekBar.setProgress((int) (currentTime / 100));
        onlyShowTimeText.setText(_time);
        actionsManager.setPlayTime(currentTime);
        if (currentTime >= totalTime) {
            close();
        }
        if (backgroundMusicManager != null) {
            if (backgroundMusicManager.getMediaInfo() != null) {
                if (!backgroundMusicManager.getMediaInfo().isPrepared()) {
                    return;
                }

                if (!backgroundMusicManager.isPlaying()) {
                    long backgroundTime = backgroundMusicManager.getPlayTime();
                    if (backgroundTime >= playTime + 2) {
                        backgroundMusicManager.pause();
                    } else {
                        backgroundMusicManager.restart();
                    }

                }

//                if(!backgroundMusicManager.isPlaying()){
//                    long backgroundTime = backgroundMusicManager.getPlayTime();
//                    if(backgroundTime > playTime + 3){
//                        backgroundMusicManager.pause();
//                    }
//                }
            }
            if (count % 3 == 0) {
                notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_FOLLOW, playTime);
            }
            Log.e("check_background_play", "isPlaying:" + backgroundMusicManager.isPlaying());

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        playTime = seekBar.getProgress() * 100;
//        refreshTimeText();
        _fromUser = fromUser;
        if (fromUser) {

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.e("seek_bar", "start_tracking");
        //pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.e("seek_bar", "stop_tracking");
//        pause();
//        final int time = seekBar.getProgress() * 10;
        if (!hasPermisson()) {
            return;
        }

        if (_fromUser) {

            if (isLoading()) {
                return;
            }
            if (isPresenter()) {
                if (isPlaying()) {
                    notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_SEEK_PLAY, seekBar.getProgress() * 100);
                } else {
                    notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_SEEK_STOP, seekBar.getProgress() * 100);

                }

            }
            seek(seekBar.getProgress() * 100);
        }

    }

    public void followSeek(int currentProgress) {
        if (isLoading()) {
            return;
        }
        seekBar.setProgress(currentProgress);
        seek(seekBar.getProgress() * 100);
    }


    private void seek(final int _time) {
        if (isLoading()) {
            return;
        }


        Log.e("check_soundtrack_play", "seek,time:" + _time);
        isSeeking = true;
        statusText.setVisibility(View.INVISIBLE);
        loadingBar.setVisibility(View.VISIBLE);
        clearActionsBySeek();
        if (meetingConfig != null && meetingConfig.getDocument() != null && meetingConfig.getDocument().getDocumentPages() != null && (currentPaegNum <
                meetingConfig.getDocument().getDocumentPages().size() || currentPaegNum > 0)) {
            PageActionsAndNotesMgr.requestActionsAndNoteForSoundtrack(meetingConfig, currentPaegNum + "",
                                        /*meetingConfig.getDocument().getAttachmentID()*/0 + "", "0",
                    soundtrackDetail.getSoundtrackID() + "");
            PageActionsAndNotesMgr.requestActionsAndNoteForSoundtrackByTime(meetingConfig, currentPaegNum + "", soundtrackDetail.getSoundtrackID() + "", playTime);
        }

        soundtrackAudioManager.seek(_time / 1000);
        if (backgroundMusicManager != null) {
            backgroundMusicManager.seekTo(_time);
        }
        Collections.sort(pageActions);
        Observable.just(pageActions).observeOn(Schedulers.io()).doOnNext(new Consumer<List<WebAction>>() {
            @Override
            public void accept(List<WebAction> webActions) throws Exception {
                for (WebAction action : webActions) {
                    if (action.getTime() >= _time) {
                        Log.e("check_page_time", "seek_time:" + _time + ",action_time:" + action.getTime());
                        SoundtrackActionsManagerV2.getInstance(host).doChangePageAction(action);
                        break;
                    }
                }
                SoundtrackActionsManagerV2.getInstance(host).setCurrentPartWebActions(null);
            }
        }).subscribe();
    }

    private void seekTo2(final int time) {

        statusText.setVisibility(View.INVISIBLE);
        loadingBar.setVisibility(View.VISIBLE);
        clearActionsBySeek();
        if (meetingConfig != null && meetingConfig.getDocument() != null && meetingConfig.getDocument().getDocumentPages() != null && (currentPaegNum <
                meetingConfig.getDocument().getDocumentPages().size() || currentPaegNum > 0)) {
            PageActionsAndNotesMgr.requestActionsAndNoteForSoundtrack(meetingConfig, currentPaegNum + "",
                    /*meetingConfig.getDocument().getAttachmentID()*/0 + "", "0",
                    soundtrackDetail.getSoundtrackID() + "");
            PageActionsAndNotesMgr.requestActionsAndNoteForSoundtrackByTime(meetingConfig, currentPaegNum + "", soundtrackDetail.getSoundtrackID() + "", playTime);
        }

        soundtrackAudioManager.seek(seekBar.getProgress());
        SoundtrackBackgroundMusicManager.getInstance(host).seekTo(time);
        Collections.sort(pageActions);
        Observable.just(pageActions).observeOn(Schedulers.io()).doOnNext(new Consumer<List<WebAction>>() {
            @Override
            public void accept(List<WebAction> webActions) throws Exception {
                for (WebAction action : webActions) {
                    if (action.getTime() >= time) {
                        Log.e("check_page_time", "seek_time:" + time + ",action_time:" + action.getTime());
                        SoundtrackActionsManagerV2.getInstance(host).doChangePageAction(action);
                        break;
                    }
                }

                SoundtrackActionsManagerV2.getInstance(host).setCurrentPartWebActions(null);
            }
        }).subscribe();
    }

    private void clearActionsBySeek() {
        web.loadUrl("javascript:ClearPageAndAction()", null);
        web.loadUrl("javascript:Record()", null);
    }

    @SuppressLint("JavascriptInterface")
    private void initWeb() {
        //  web.setZOrderOnTop(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.addJavascriptInterface(this, "AnalyticsWebInterface");
        loadWebIndex();
    }

    private void loadWebIndex() {
        int deviceType = DeviceManager.getDeviceType(host);
        String indexUrl = "file:///android_asset/index.html";
        if (deviceType == SupportDevice.BOOK) {
            indexUrl += "?devicetype=4";
        }
        web.loadUrl(indexUrl, null);
    }

    @JavascriptInterface
    public void afterLoadFileFunction() {
        SoundtrackActionsManagerV2.getInstance(host).setLoadingPage(false);
//        SoundtrackActionsManagerV2.getInstance(host).setCurrentPartWebActions(null);
        host.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:ShowToolbar(" + false + ")", null);
                web.loadUrl("javascript:Record()", null);
                DocumentPage documentPage = meetingConfig.getCurrentDocumentPage();

            }
        });

    }

    @JavascriptInterface
    public void afterLoadPageFunction() {

    }

    @JavascriptInterface
    public void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("JavascriptInterface", "preLoadFileFunctiona," + url + "     currentpageNum   " + currentpageNum + "   showLoading    " + showLoading);
    }

    int currentPaegNum;

    @JavascriptInterface
    public void afterChangePageFunction(final int pageNum, int type) {
        Log.e("JavascriptInterface", "afterChangePageFunction,pageNum" + pageNum + ",type" + type);
        currentPaegNum = pageNum;
        if (web.getVisibility() != View.VISIBLE) {
            Observable.just("set_visiable").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    web.setVisibility(View.VISIBLE);
                    if (loadingBar.getVisibility() == View.VISIBLE) {
                        loadingBar.setVisibility(View.INVISIBLE);
                    }
                    if (statusText.getVisibility() != View.VISIBLE) {
                        statusText.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        SoundtrackActionsManagerV2.getInstance(host).setCurrentPage(pageNum);
        if (meetingConfig.getDocument().getDocumentPages() != null) {
            int size = meetingConfig.getDocument().getDocumentPages().size();
            if (pageNum < 0 || pageNum > size) {
                return;
            }
        }

        if (meetingConfig.getDocument() == null) {
            return;
        }

        PageActionsAndNotesMgr.requestActionsAndNoteForSoundtrack(meetingConfig, pageNum + "",
                        /*meetingConfig.getDocument().getAttachmentID()*/0 + "", "0",
                soundtrackDetail.getSoundtrackID() + "");

        PageActionsAndNotesMgr.requestActionsAndNoteForSoundtrackByTime(meetingConfig, pageNum + "", soundtrackDetail.getSoundtrackID() + "", playTime);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showWebVedio(EventPlayWebVedio webVedio) {
        Log.e("showWebVedio", "showWebVedio");
        pause();
        webVedioLayout.setVisibility(View.VISIBLE);
        WebVedioManager.getInstance(host).execute(webVedio.getWebVedio(), playTime);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeWebView(EventCloseWebView webVedio) {
        Log.e("showWebVedio", "showWebVedio");
        webVedioLayout.setVisibility(View.GONE);
        restart();
//        WebVedioManager.getInstance(host).closeVedio();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePageActions(EventPageActionsForSoundtrack pageActions) {
        String data = pageActions.getData();
        Log.e("receivePageActions", "pageActions:" + pageActions);
        if (!TextUtils.isEmpty(data)) {
            if (pageActions.getPageNumber() == currentPaegNum) {
                Log.e("check_play_txt", "PlayActionByArray:" + data);
                if (web != null) {
                    web.loadUrl("javascript:PlayActionByArray(" + data + "," + 0 + ")", null);

                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePageNotes(EventPageNotesForSoundtrack pageNotes) {
        Log.e("receivePageNotes", "page_notes:" + pageNotes);
        List<NoteDetail> notes = pageNotes.getNotes();
        if (notes != null && notes.size() > 0) {

            for (NoteDetail note : notes) {
                try {
                    JSONObject message = new JSONObject();
                    message.put("type", 38);
                    message.put("LinkID", note.getLinkID());
                    message.put("IsOther", false);
                    if (!TextUtils.isEmpty(note.getLinkProperty())) {
                        message.put("LinkProperty", new JSONObject(note.getLinkProperty()));
                    }
                    Log.e("check_play_txt", "notes_PlayActionByTxt:" + message);
                    if (web != null) {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeSoundtrackDialog(EventCloseSoundtrack closeSoundtrack) {
        close();
    }

    private void pause() {

        if (soundtrackAudioManager == null) {
            soundtrackAudioManager = SoundtrackAudioManagerV2.getInstance(host);
        }
        notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_PAUSE, playTime);

        if (soundtrackAudioManager != null) {
            soundtrackAudioManager.pause();
        }
        SoundtrackBackgroundMusicManager.getInstance(host).pause();
        statusText.setText(R.string.paused);
        startPauseImage.setImageResource(R.drawable.video_play);
    }

    public void followPause(long time) {
//        if (soundtrackAudioManager.isPlaying()) {
//
//        }

        if (soundtrackAudioManager == null) {
            soundtrackAudioManager = SoundtrackAudioManagerV2.getInstance(host);
        }

        if (soundtrackAudioManager != null) {
            soundtrackAudioManager.pause();
        }
        SoundtrackBackgroundMusicManager.getInstance(host).pause();
        statusText.setText(R.string.paused);
        startPauseImage.setImageResource(R.drawable.video_play);
        if (time > 0) {
            if (Math.abs(time - playTime) >= 1000) {
                playTime = time;
                seek((int) time);
            }
        }


    }

    public void followRestart(long time) {
        restart();
        if (time > 0) {
            if (Math.abs(time - playTime) >= 5000) {
                playTime = time;
                seek((int) time);
            }
        }

    }

    public void followTheProgress(long time) {
        if (Math.abs(time - playTime) >= 5000) {
            playTime = time;
            time += 3000;
            seek((int) time);
        }
    }

    public void followClose() {
        close();

    }

    public void followSeekTo(int audioTime) {
        seekTo2(audioTime);
    }

    private void restart() {
        if (soundtrackAudioManager == null) {
            soundtrackAudioManager = SoundtrackAudioManagerV2.getInstance(host);
        }
        notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_RESTART, playTime);
        soundtrackAudioManager.restart();
        if (backgroundMusicManager != null) {
            backgroundMusicManager.restart();
        }
        //SoundtrackBackgroundMusicManager.getInstance(host).restart();
        statusText.setText(R.string.playing);
        startPauseImage.setImageResource(R.drawable.video_stop);
    }

    private void close() {
        isClosed = true;
        if (bottomMenu != null) {
            bottomMenu.setImageResource(R.drawable.icon_menu);
            bottomMenu.setEnabled(true);
        }

        if (soundtrackPlayLayout.getVisibility() == View.VISIBLE) {
            soundtrackPlayLayout.setVisibility(View.GONE);
            webVedioLayout.setVisibility(View.GONE);
            try {
                notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_STOP, 0);
                if (EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().unregister(this);
                }
                pageActions.clear();
                release();
            } catch (Exception e) {

            }
        }

        reinitHandler.removeMessages(MESSAGE_CHECK_LOADING);

    }

    private void seekTo(int time) {
        actionsManager.seekTo(time);
    }

    private void syncDownloadFirst(int recordId) {
        final String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + recordId + "&startTime=" + 0 + "&endTime=" + 20000;
        final String cacheUrl = url + "__time__separator__" + 0 + "__" + 20000 + "__" + recordId;
        boolean isContain = webActionsCache.containPartWebActions(cacheUrl);
        if (!isContain) {
            List<WebAction> actions = ServiceInterfaceTools.getinstance().syncGetRecordActions(url);
            Log.e("check_download", "download_response:" + actions);
            if (actions != null) {
                PartWebActions partWebActions = new PartWebActions();
                partWebActions.setStartTime(0);
                partWebActions.setEndTime(20000);
                partWebActions.setUrl(cacheUrl);
                if (actions.size() > 0) {
                    partWebActions.setWebActions(actions);
                } else {
                    partWebActions.setWebActions(new ArrayList<WebAction>());
                }
                Log.e("check_download", "cache:" + cacheUrl);
                webActionsCache.cacheActions(partWebActions);
                Log.e("SoundtrackActionsManager", "step_four:request_success_and_cache:web_actions_size:" + partWebActions.getWebActions().size());
            }
        }
    }

    private void syncDownloadNoteControl(String noteid) {

    }

    private void downloadActions(long totalTime, final int recordId) {

        int secends = (int) (totalTime / 1000) + 1;
        int partSize = secends / 20 + 1;
        Integer[] parts = new Integer[partSize];
        for (int i = 0; i < parts.length; ++i) {
            parts[i] = i;
        }

        Observable.fromArray(parts).delay(3000, TimeUnit.MILLISECONDS).observeOn(Schedulers.io()).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer index) throws Exception {
                long startTime = index * 20000;
                long endTime = (index + 1) * 20000;
                final String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + recordId + "&startTime=" + startTime + "&endTime=" + endTime;
                final String cacheUrl = url + "__time__separator__" + startTime + "__" + endTime + "__" + recordId;
                boolean isContain = webActionsCache.containPartWebActions(cacheUrl);
                if (!isContain) {
                    List<WebAction> actions = ServiceInterfaceTools.getinstance().syncGetRecordActions(url);
                    Log.e("check_download", "download_response:" + actions);
                    if (actions != null) {
                        PartWebActions partWebActions = new PartWebActions();
                        partWebActions.setStartTime(startTime);
                        partWebActions.setEndTime(endTime);
                        partWebActions.setUrl(cacheUrl);
                        if (actions.size() > 0) {
                            partWebActions.setWebActions(actions);
                        } else {
                            partWebActions.setWebActions(new ArrayList<WebAction>());
                        }
                        Log.e("check_download", "cache:" + cacheUrl);
                        webActionsCache.cacheActions(partWebActions);
                        Log.e("SoundtrackActionsManager", "step_four:request_success_and_cache:web_actions_size:" + partWebActions.getWebActions().size());
//
                    }
                }
                fetchPageActions(webActionsCache.getPartWebActions(cacheUrl));
                Log.e("check_part", "url:" + url);
            }
        }).subscribe();
//        Log.e("check_part","part_size:" + partSize +", total_time:" + totalTime);
    }

    private List<WebAction> pageActions = new ArrayList<>();
//    private List<WebAction> mNoteActionList = new ArrayList<>();

    private void fetchPageActions(PartWebActions webActions) {
        if (webActions == null) {
            return;
        }
        List<WebAction> actions = webActions.getWebActions();
        if (actions != null && actions.size() > 0) {
            for (WebAction action : actions) {
                if (!TextUtils.isEmpty(action.getData()))
                    try {
                        JSONObject data = new JSONObject(action.getData());
                        if (data.getInt("type") == 2) {
                            if (!pageActions.contains(action)) {
                                pageActions.add(action);
                            }
                        }/*else {
                            if (!mNoteActionList.contains(action)) {
                                mNoteActionList.add(action);
                            }
                        }*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
        Log.e("check_page_actions", "page_actions_size:" + pageActions.size());
    }

    private void notifySoundtrackPlayStatus(SoundtrackDetail soundtrackDetail, int status, long time) {
        if (soundtrackDetail == null) {
            return;
        }
        if (meetingConfig.getType() != MeetingType.MEETING) {
            // 不是会议
        } else {
            if (isPresenter() && !meetingConfig.isMeetingPause()) {
                SocketMessageManager.getManager(host).sendMessage_Soundtrack_Playing_Status(soundtrackDetail.getSoundtrackID() + "", status, time);
            }
        }
    }

    private boolean isPresenter() {
        if (meetingConfig.getPresenterId().equals(AppConfig.UserID)) {
            return true;
        }
        return false;
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

    public void changeSeekbarStatusByRole() {

        if (meetingConfig != null && meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        if (seekBar != null) {
            if (isPresenter()) {
                seekBar.setEnabled(true);
                seekBar.setClickable(true);
            } else {
                seekBar.setEnabled(false);
                seekBar.setClickable(false);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventSeek(SeekData seekData) {
        statusText.setText(R.string.paused);
        if (seekBar != null) {
            seekBar.setProgress(seekData.getProgress());
        }
    }

    @Override
    public void onAudioPlayTimeInfo(long playTime) {
        isSeeking = false;
        moveByTime(playTime);

    }

    @Override
    public void onCompletionCalled() {
        if (!isSeeking) {
            close();
        } else {
            if (soundtrackAudioManager != null) {
                soundtrackAudioManager.preparedForSeek();
            }
        }
    }

    @Override
    public void seekCompletionAndPause(double _playTime) {
        isSeeking = false;
        playTime = (long) (_playTime * 1000);
        if (backgroundMusicManager != null) {
            backgroundMusicManager.seekTo((int) playTime);
            backgroundMusicManager.pause();
        }
        pauseAtTime(playTime);
        actionsManager.setPlayTime(playTime);

    }

    @Override
    public void onAudioLoding() {
        loadingBar.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.INVISIBLE);
        if (backgroundMusicManager != null && backgroundMusicManager.isPlaying()) {
            backgroundMusicManager.pause();
        }
    }

    public boolean isLoading() {
        boolean isLoading = false;

        if (soundtrackAudioManager != null) {
            isLoading = soundtrackAudioManager.isLoading();
        }
        if (!isLoading) {
            isLoading = isSeeking;
        }
        return isLoading;
    }


}
