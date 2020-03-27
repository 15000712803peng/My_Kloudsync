package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.kloudsync.techexcel.bean.SoundtrackDetail;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.PageActionsAndNotesMgr;
import com.kloudsync.techexcel.help.SmallNoteViewHelper;
import com.kloudsync.techexcel.help.SoundtrackActionsManager;
import com.kloudsync.techexcel.help.SoundtrackAudioManagerV2;
import com.kloudsync.techexcel.help.SoundtrackBackgroundMusicManager;
import com.kloudsync.techexcel.help.SoundtrackDigitalNoteManager;
import com.kloudsync.techexcel.help.UserVedioManager;
import com.kloudsync.techexcel.help.WebVedioManager;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.tool.DocumentModel;
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
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class SoundtrackPlayDialog implements View.OnClickListener, Dialog.OnDismissListener, SeekBar.OnSeekBarChangeListener {
    public Activity host;
    public Dialog dialog;
    public int width;
    public int heigth;
    private SoundtrackDetail soundtrackDetail;
    //view
    private View view;
    private LinearLayout centerLoaing;
    // play status
    private volatile long playTime;
    private volatile boolean isFinished;
    private volatile boolean isStarted;
    private long maxProgress;
    private volatile long currentProgress;
    private volatile boolean isSeek;
    // data
    private static Handler playHandler;
    private PlayTimeTask playTimeTask;
    // message
    private static final int MESSAGE_PLAY_TIME_REFRESHED = 1;
    private static final int MESSAGE_HIDE_CENTER_LOADING = 2;
    private static final int MESSAGE_PLAY_START = 3;
    private static final int MESSAGE_PLAY_FINISH = 4;
    SoundtrackActionsManager actionsManager;
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
    //
    String totalTimeStr = "00:00";
    String timeStr = "00:00/00:00";
    private long totalTime;
    //
    UserVedioManager userVedioManager;
    XWalkView web;
    TextView statusText;
    private TextView onlyShowTimeText;
    private ImageView hideControllerImage;
    //------
    private RelativeLayout smallNoteLayout;
    private XWalkView smallNoteWeb;
    private XWalkView mainNoteWeb;
    SmallNoteViewHelper smallNoteViewHelper;

    public void setSoundtrackDetail(SoundtrackDetail soundtrackDetail) {
        this.soundtrackDetail = soundtrackDetail;
        totalTime = soundtrackDetail.getDuration();
    }

    public SoundtrackPlayDialog(Activity host, SoundtrackDetail soundtrackDetail, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        Log.e("check_dialog", "new_dialog");
        this.host = host;
        this.soundtrackDetail = soundtrackDetail;
        totalTime = soundtrackDetail.getDuration();
        initDialog();
//        smallNoteViewHelper = new SmallNoteViewHelper(smallNoteLayout,smallNoteWeb,meetingConfig);
//        smallNoteViewHelper.init(host);
        SoundtrackDigitalNoteManager.getInstance(host).initViews(meetingConfig, smallNoteLayout, smallNoteWeb, mainNoteWeb);
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        webActionsCache = SyncWebActionsCache.getInstance(host);
        view = layoutInflater.inflate(R.layout.dialog_play_soundtrack, null);
        dialog = new Dialog(host, R.style.my_dialog);
        centerLoaing = view.findViewById(R.id.layout_center_loading);
        smallNoteLayout = view.findViewById(R.id.layout_small_note);
//        smallNoteWeb = view.findViewById(R.id.small_web_note);
        mainNoteWeb = view.findViewById(R.id.main_note_web);
        smallNoteWeb = view.findViewById(R.id.small_web_note);
        webVedioSurface = view.findViewById(R.id.web_vedio_surface);
        web = view.findViewById(R.id.web);
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
        startPauseImage = view.findViewById(R.id.image_play_pause);
        startPauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundtrackAudioManager.getMediaInfo() == null) {
                    if (isStarted) {
                        pause();
                    } else {
                        restart();
                    }
                } else {
                    if (soundtrackAudioManager.isPlaying()) {
                        pause();
                    } else {
                        restart();
                    }
                }

            }
        });

        closeVedioImage = view.findViewById(R.id.image_close_veido);
        closeVedioImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_STOP, 0);
                WebVedioManager.getInstance(host).closeVedio();
                webVedioLayout.setVisibility(View.GONE);

            }
        });

        closeDialogImage = view.findViewById(R.id.close);
        closeDialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        setControllerLayoutWithByOritation();
        initWeb();
        width = (int) (host.getResources().getDisplayMetrics().widthPixels);
        heigth = (int) (host.getResources().getDisplayMetrics().heightPixels);
        dialog.setContentView(view);
        dialog.setOnDismissListener(this);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = width;
        lp.height = heigth;
        dialog.getWindow().setAttributes(lp);
        playTimeTask = new PlayTimeTask();
        playHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (playHandler == null || dialog == null || !dialog.isShowing()) {
                    return;
                }
                handlePlayMessage(msg);
                super.handleMessage(msg);
            }
        };
        //----
        actionsManager = SoundtrackActionsManager.getInstance(host);
        actionsManager.setWeb(web, meetingConfig);
        actionsManager.setUserVedioManager(userVedioManager);
        actionsManager.setSurfaceView(webVedioSurface);
        actionsManager.setRecordId(soundtrackDetail.getSoundtrackID());
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

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        Log.e("check_dialog", "dialog_dismiss");
        release();
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.cancel:
                dismiss();
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

    public void show() {

        Log.e("SoundtrackPlayDialog", "show,dialog:" + dialog);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        Observable.just("delay_load_parentpage").delay(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (centerLoaing.getVisibility() == View.VISIBLE) {
                    centerLoaing.setVisibility(View.GONE);
                }
                DocumentPage documentPage = meetingConfig.getCurrentDocumentPage();
                if (documentPage != null) {
                    web.load("javascript:ShowPDF('" + documentPage.getShowingPath() + "'," + (documentPage.getPageNumber()) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
                    web.load("javascript:Record()", null);
                }
            }
        });

        mainNoteWeb.setVisibility(View.GONE);
        EventBus.getDefault().register(this);
        downloadActions(soundtrackDetail.getDuration(), soundtrackDetail.getSoundtrackID());
        notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_PLAY, 0);
	    soundtrackAudioManager = SoundtrackAudioManagerV2.getInstance(host);
        soundtrackAudioManager.setSoundtrackAudio(soundtrackDetail.getNewAudioInfo());

        backgroundMusicManager = SoundtrackBackgroundMusicManager.getInstance(host);
        backgroundMusicManager.setSoundtrackAudio(soundtrackDetail.getBackgroudMusicInfo());

        Observable.just("preload").observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                syncDownloadFirst(soundtrackDetail.getSoundtrackID());
                Log.e("check_play_step", "step_one:preload");
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
//                https://peertime.oss-cn-shanghai.aliyuncs.com/NoteControlAction/37014/channel_1.json
                final String centerPart = "NoteControlAction" + File.separator + soundtrackDetail.getSoundtrackID();
                JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                        centerPart);
                String url = "";
                if (queryDocumentResult != null) {
                    Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());

                    if (uploadao != null) {
                        if (1 == uploadao.getServiceProviderId()) {
                            url = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
                                    + "/channel_1.json";
                        } else if (2 == uploadao.getServiceProviderId()) {
                            url = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/channel_1.json";
                        }
                        Log.e("check_transform_url", "url:" + url);
                    }
                }
                return url;
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String url) throws Exception {
                if (!TextUtils.isEmpty(url)) {
                    SoundtrackDigitalNoteManager.getInstance(host).doProcess(url);
                }
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("check_play_step", "step_two:task_execute");
                new PlayTimeTask().execute();
            }
        }).subscribe();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        EventBus.getDefault().unregister(this);
        dismiss();
    }

    class PlayTimeTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            playTime = 0;
            isFinished = false;
//            isStarted = true;
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // 播放完成或者手动关闭dialog isFinished = true;
            while (!isFinished) {
                boolean isPlaying = false;
                if (soundtrackAudioManager.getMediaInfo() == null) {
                    // 没有newinfo文件
                    isPlaying = true;
                } else {
	                isPlaying = SoundtrackAudioManagerV2.getInstance(host).isPlaying();
                }


                Log.e("check_play", "mediaInfo,isPlaying:" + isPlaying);
                if (dialog == null || !dialog.isShowing()) {
                    isFinished = true;
                }

                if (!isStarted || !isPlaying) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                if (totalTime <= 0) {
                    if (soundtrackAudioManager.getMediaInfo() != null) {
	                    totalTime = SoundtrackAudioManagerV2.getInstance(host).getDuration();
                    }

                }

                if (playTime >= totalTime) {
                    playTime = totalTime;
                    playHandler.obtainMessage(MESSAGE_PLAY_TIME_REFRESHED).sendToTarget();
                    break;
                }
//                Log.e("RecordPlayDialog","is finish:" + isFinished);
                Log.e("SoundtrackActionsManager", "playTime:" + playTime + ",isplaying:");
//                synchronized (SoundtrackPlayDialog.this) {
//
//                }
                if (soundtrackAudioManager.getMediaInfo() != null) {
	                playTime = SoundtrackAudioManagerV2.getInstance(host).getPlayTime();
                } else {
                    playTime += 500;
                }

                actionsManager.setTotalTime(totalTime);
                actionsManager.setPlayTime(playTime);
                if (SoundtrackDigitalNoteManager.getInstance(host).getNoteEvents().size() > 0) {
                    SoundtrackDigitalNoteManager.getInstance(host).setPlayTime(playTime);
                }
//                    playTime = soundtrackAudioManager.getPlayTime();
                if (playHandler != null) {
                    playHandler.obtainMessage(MESSAGE_PLAY_TIME_REFRESHED).sendToTarget();
                }

                try {
                    Thread.sleep(500);
//                        playTime += 200;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("check_dialog", "time_task_post_execute");
            dismiss();

        }
    }

    private void release() {
        isFinished = true;
        isStarted = false;
        playHandler = null;
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
                setTimeText();
                break;
            case MESSAGE_HIDE_CENTER_LOADING:
                centerLoaing.setVisibility(View.GONE);


                break;
            case MESSAGE_PLAY_START:
                isStarted = true;
                break;

            case MESSAGE_PLAY_FINISH:

                break;
        }
    }

    private void setTimeText() {
        if (centerLoaing.getVisibility() == View.VISIBLE) {
            centerLoaing.setVisibility(View.GONE);
        }
        if (onlyShowTimeText.getVisibility() != View.VISIBLE) {
            if (controllerLayout.getVisibility() != View.VISIBLE) {
                controllerLayout.setVisibility(View.VISIBLE);
            }
        }
        final String time = new SimpleDateFormat("mm:ss").format(playTime);
        final String _time = new SimpleDateFormat("mm:ss").format(totalTime);
        playTimeText.setText(time + "/" + _time);
        seekBar.setMax((int) (totalTime / 10));
        seekBar.setProgress((int) (playTime / 10));
        statusText.setText(R.string.playing);
        startPauseImage.setImageResource(R.drawable.video_stop);
        onlyShowTimeText.setText(time);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        playTime = seekBar.getProgress() * 100;
//        refreshTimeText();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.e("seek_bar", "start_tracking");
//        pause();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeek = true;
        Log.e("seek_bar", "stop_tracking");
//        seekTo(seekBar.getProgress() * 10);
        pause();
        final int time = seekBar.getProgress() * 10;
        seekTo2(time);
    }

    private void seekTo2(final int time) {
        isStarted = false;
        playTime = time;
        clearActionsBySeek();
	    SoundtrackAudioManagerV2.getInstance(host).seekTo(time);
        SoundtrackBackgroundMusicManager.getInstance(host).seekTo(time);
        Collections.sort(pageActions);
        Observable.just(pageActions).observeOn(Schedulers.io()).doOnNext(new Consumer<List<WebAction>>() {
            @Override
            public void accept(List<WebAction> webActions) throws Exception {
                for (WebAction action : webActions) {
                    if (action.getTime() >= time) {
                        Log.e("check_page_time", "seek_time:" + time + ",action_time:" + action.getTime());
                        SoundtrackActionsManager.getInstance(host).doChangePageAction(action);
                        SoundtrackActionsManager.getInstance(host).setCurrentPartWebActions(null);
                        break;
                    }
                }
            }
        }).doOnNext(new Consumer<List<WebAction>>() {
            @Override
            public void accept(List<WebAction> webActions) throws Exception {

                Observable.just("restart").delay(2000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        isStarted = true;
                        restart();
                    }
                });

            }
        }).subscribe();
    }

    private void clearActionsBySeek() {
        web.load("javascript:ClearPageAndAction()", null);
        web.load("javascript:Record()", null);
    }

    private void initWeb() {
        web.setZOrderOnTop(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.addJavascriptInterface(this, "AnalyticsWebInterface");
        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        loadWebIndex();
    }

    private void loadWebIndex() {
        int deviceType = DeviceManager.getDeviceType(host);
        String indexUrl = "file:///android_asset/index.html";
        if (deviceType == SupportDevice.BOOK) {
            indexUrl += "?devicetype=4";
        }
        web.load(indexUrl, null);
    }

    @org.xwalk.core.JavascriptInterface
    public void afterLoadFileFunction() {
        SoundtrackActionsManager.getInstance(host).setLoadingPage(false);
        if (isSeek) {
            SoundtrackActionsManager.getInstance(host).setCurrentPartWebActions(null);
            isSeek = false;
        }
        host.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                web.load("javascript:ShowToolbar(" + false + ")", null);
                web.load("javascript:Record()", null);
                DocumentPage documentPage = meetingConfig.getCurrentDocumentPage();

            }
        });

    }

	@org.xwalk.core.JavascriptInterface
	public void afterLoadPageFunction() {
		isStarted = true;
	}


    @org.xwalk.core.JavascriptInterface
    public void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("JavascriptInterface", "preLoadFileFunctiona," + url + "     currentpageNum   " + currentpageNum + "   showLoading    " + showLoading);
    }

    int currentPaegNum;

    @org.xwalk.core.JavascriptInterface
    public void afterChangePageFunction(final int pageNum, int type) {
        Log.e("JavascriptInterface", "afterChangePageFunction,pageNum" + pageNum + ",type" + type);
        currentPaegNum = pageNum;
//        SoundtrackActionsManager.getInstance(host).setCurrentPage(Integer.parseInt(pageNum));
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
                    web.load("javascript:PlayActionByArray(" + data + "," + 0 + ")", null);

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
        dismiss();
    }

    private void pause() {
        notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_PAUSE, soundtrackAudioManager.getPlayTime());
        isStarted = false;
	    SoundtrackAudioManagerV2.getInstance(host).pause();
        SoundtrackBackgroundMusicManager.getInstance(host).pause();
        statusText.setText(R.string.paused);
        startPauseImage.setImageResource(R.drawable.video_play);
    }

    public void followPause() {
        if (soundtrackAudioManager.isPlaying()) {
            pause();
        }
    }

    public void followRestart() {
        if (soundtrackDetail.getNewAudioInfo() != null) {
            if (!soundtrackAudioManager.isPlaying()) {
                restart();
            }
        } else {
            restart();
        }

    }

    public void followClose() {
        close();
    }

    public void followSeekTo(int audioTime) {
        seekTo2(audioTime);
    }

    private void restart() {
        notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_RESTART, soundtrackAudioManager.getPlayTime());
	    SoundtrackAudioManagerV2.getInstance(host).restart();
        SoundtrackBackgroundMusicManager.getInstance(host).restart();
        isStarted = true;
        statusText.setText(R.string.playing);
        startPauseImage.setImageResource(R.drawable.video_stop);
    }

    private void close() {
        notifySoundtrackPlayStatus(soundtrackDetail, TYPE_SOUNDTRACK_STOP, soundtrackAudioManager.getPlayTime());
//        release();
        dismiss();
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
//
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
                        }
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
            if (isPresenter()) {
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

}
