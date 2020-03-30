package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventCloseWebView;
import com.kloudsync.techexcel.bean.EventPlayWebVedio;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.RecordActionsManager;
import com.kloudsync.techexcel.help.RecordAudioManager;
import com.kloudsync.techexcel.help.RecordShareVedioManager;

import com.kloudsync.techexcel.help.SoundtrackActionsManager;
import com.kloudsync.techexcel.help.UserVedioManager;
import com.kloudsync.techexcel.help.WebVedioManager;
import com.kloudsync.techexcel.tool.SyncWebActionsCache;
import com.ub.techexcel.bean.ChannelVO;
import com.ub.techexcel.bean.PartWebActions;
import com.ub.techexcel.bean.Record;
import com.ub.techexcel.bean.RecordDetail;
import com.ub.techexcel.bean.WebAction;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.DateUtil;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class RecordPlayDialog implements View.OnClickListener, HeaderRecyclerAdapter.OnItemClickListener, Dialog.OnDismissListener, SeekBar.OnSeekBarChangeListener {
    public Activity host;
    public Dialog dialog;
    public int width;
    public int heigth;
    private RecordDetail recordDetail;
    private Record record;
    //view
    private View view;
    private TextView playTimeText;

    private LinearLayout centerLoaing;
    private LinearLayout playLayout;
    //    private ImageView centerStartImage;
    private SeekBar seekBar;
    private SurfaceView webVedioSurface;
    private SurfaceView shareVedioSurface;
    private RecyclerView userList;
    // play status
    private volatile long playTime;
    private volatile long totalTime;
    private volatile boolean isFinished;
    private volatile boolean isStarted;
    private long maxProgress;
    private volatile long currentProgress;
    private volatile boolean isSeek;
    // data
    private static Handler playHandler;
    private PlayTask playTask;
    // message
    private static final int MESSAGE_PLAY_TIME_REFRESHED = 1;
    private static final int MESSAGE_HIDE_CENTER_LOADING = 2;
    private static final int MESSAGE_PLAY_START = 3;
    private static final int MESSAGE_PLAY_FINISH = 4;
    private ImageView close;
    private RelativeLayout webVedioLayout;

    //
    String totalTimeStr = "00:00";
    String timeStr = "00:00/00:00";

    //
    RecordAudioManager audioManager;
    RecordActionsManager actionsManager;
    RecordShareVedioManager recordShareVedioManager;
    UserVedioManager userVedioManager;
    WebView web;

    TextView statusText;
    ImageView startPauseImage;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
    private SyncWebActionsCache webActionsCache;
    private ImageView closeVedioImage;
    private MeetingConfig meetingConfig;
    private TextView onlyShowTimeText;
    private ImageView hideControllerImage;



    public void setRecord(Record record) {
        this.record = record;
    }

    public RecordPlayDialog(Activity host, Record record, MeetingConfig meetingConfig) {
        Log.e("check_dialog", "new_dialog");
        this.host = host;
        this.record = record;
        this.meetingConfig = meetingConfig;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.dialog_play_record, null);
        dialog = new Dialog(host, R.style.my_dialog);
        playTimeText = view.findViewById(R.id.txt_play_time);
        startPauseImage = view.findViewById(R.id.image_play_pause);
        startPauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("check_clicked", "startPauseImage clicked,isStarted:" + isStarted);
                if (isStarted) {
                    pause();
                    isStarted = false;
                } else {
                    restart();
                    isStarted = true;
                }
            }
        });
        statusText = view.findViewById(R.id.txt_status);
        centerLoaing = view.findViewById(R.id.layout_center_loading);
        playLayout = view.findViewById(R.id.layout_play);
        onlyShowTimeText = view.findViewById(R.id.txt_only_show_time);
        onlyShowTimeText.setOnClickListener(this);
        webActionsCache = SyncWebActionsCache.getInstance(host);
//        centerStartImage = view.findViewById(R.id.image_center_start);
//        centerStartImage.setOnClickListener(this);
        seekBar = view.findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        userList = view.findViewById(R.id.list_user);
            hideControllerImage = view.findViewById(R.id.txt_hidden);
        hideControllerImage.setOnClickListener(this);

        close = view.findViewById(R.id.close);
        close.setOnClickListener(this);

        closeVedioImage = view.findViewById(R.id.image_close_veido);
        closeVedioImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebVedioManager.getInstance(host).closeVedio();
                webVedioLayout.setVisibility(View.GONE);

            }
        });

        webVedioLayout = view.findViewById(R.id.layout_web_vedio);
        userList.setLayoutManager(new LinearLayoutManager(host, RecyclerView.HORIZONTAL, false));
        webVedioSurface = view.findViewById(R.id.web_vedio_surface);
        shareVedioSurface = view.findViewById(R.id.share_vedio_surface);
        web = view.findViewById(R.id.web);
        initWeb();
        //* (0.95f)
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
        playTask = new PlayTask();
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
        audioManager = RecordAudioManager.getInstance(host);
        userVedioManager = UserVedioManager.getInstance(host);
        recordShareVedioManager = RecordShareVedioManager.getInstance(host);
        recordShareVedioManager.setSurfaceView(shareVedioSurface);
        actionsManager = RecordActionsManager.getInstance(host);
        actionsManager.setWeb(web, meetingConfig);
        actionsManager.setUserVedioManager(userVedioManager);
        actionsManager.setSurfaceView(webVedioSurface);
        userVedioManager.setAdapter(userList);
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        Log.e("check_dialog", "dialog_dismiss");
        release();
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
//        actionsManager.release();
//        recordShareVedioManager.release();
//        audioManager.release();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                dismiss();
                break;
            case R.id.close:
                release();
                dismiss();
                //结束播放
                break;

            case R.id.doc_img_close:
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.txt_only_show_time:
                if(onlyShowTimeText.getVisibility() == View.VISIBLE){
                    onlyShowTimeText.setVisibility(View.GONE);
                    playLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.txt_hidden:
                onlyShowTimeText.setVisibility(View.VISIBLE);
                playLayout.setVisibility(View.GONE);
                break;
//            case R.id.image_center_start:
//                if (playHandler != null) {
//                    playHandler.obtainMessage(MESSAGE_PLAY_START).sendToTarget();
//                }
//                break;
            default:
                break;

        }
    }


    public void changeDocument(final MeetingConfig meetingConfig){
        this.meetingConfig=meetingConfig;
        Observable.just("delay_load_parentpage").delay(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (centerLoaing.getVisibility() == View.VISIBLE) {
                    centerLoaing.setVisibility(View.GONE);
                }
                DocumentPage documentPage = meetingConfig.getCurrentDocumentPage();
                if (documentPage != null) {
                    web.loadUrl("javascript:ShowPDF('" + documentPage.getShowingPath() + "'," + (documentPage.getPageNumber()) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
                    web.loadUrl("javascript:Record()", null);
                }
            }
        });
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            Observable.just("delay_load_parentpage").delay(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    if (centerLoaing.getVisibility() == View.VISIBLE) {
                        centerLoaing.setVisibility(View.GONE);
                    }
                    DocumentPage documentPage = meetingConfig.getCurrentDocumentPage();
                    if (documentPage != null) {
                        web.loadUrl("javascript:ShowPDF('" + documentPage.getShowingPath() + "'," + (documentPage.getPageNumber()) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
                        web.loadUrl("javascript:Record()", null);
                    }
                }
            });

            Observable.just("load_detail").observeOn(Schedulers.io()).map(new Function<String, RecordDetail>() {
                @Override
                public RecordDetail apply(String s) throws Exception {
                    recordDetail = syncGetMeetingRecordDetail();
                    Log.e("check_play_step", "step_one:load_detail");
                    return recordDetail;
                }
            }).map(new Function<RecordDetail, Integer>() {
                @Override
                public Integer apply(RecordDetail s) throws Exception {
                    Log.e("check_play_step", "step_two:parse_detail,record_detail:" + recordDetail);
                    return parseRecordDetail(recordDetail);

                }
            }).doOnNext(new Consumer<Integer>() {
                @Override
                public void accept(Integer result) throws Exception {
                    if (result == 1) {
                        Log.e("check_play_step", "step_three:start_execute");
                        syncDownloadFirstActions(recordDetail.getRecordingId());
                        playTask.execute();
                        downloadActions(recordDetail.getDuration(), recordDetail.getRecordingId());
                    }
                }
            }).subscribe();
        }
    }

    private void downloadActions(long totalTime, final int recordId) {
        int secends = (int) (totalTime / 1000) + 1;
        int partSize = secends / 20 + 1;
        Integer[] parts = new Integer[partSize];
        for (int i = 0; i < parts.length; ++i) {
            parts[i] = i;
        }

        Observable.fromArray(parts).delay(5000, TimeUnit.MILLISECONDS).observeOn(Schedulers.io()).doOnNext(new Consumer<Integer>() {
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


    private void syncDownloadFirstActions(int recordId) {
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


    @Override
    public void onItemClick(int position, Object data) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();
    }

    private void requestMeetingRecord() {
        if (record != null) {
            String url = "https://wss.peertime.cn/MeetingServer/recording/recording_item?recordingId=" + record.getRecordingId();
            ServiceInterfaceTools.getinstance().getRecordingItem(url, ServiceInterfaceTools.GETRECORDINGITEM, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    recordDetail = (RecordDetail) object;
                    if (recordDetail != null) {
                        parseRecordDetail(recordDetail);
                    }
//                    playHandler.obtainMessage(MESSAGE_HIDE_CENTER_LOADING).sendToTarget();

                }
            });
        }

    }

    private RecordDetail syncGetMeetingRecordDetail() {
        String url = "https://wss.peertime.cn/MeetingServer/recording/recording_item?recordingId=" + record.getRecordingId();
        return ServiceInterfaceTools.getinstance().syncGetRecordingItemDetail(url);

    }


    class PlayTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            playTime = 0;
            isFinished = false;
            isStarted = true;
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // 播放完成或者手动关闭dialog isFinished = true;
            while (!isFinished) {
                boolean isPlaying = RecordAudioManager.getInstance(host).isPlaying();

                Log.e("PlayTask", "audio_is_playing:" + isPlaying + ",is_started:" + isStarted);

                if (!isStarted) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                if (playTime >= totalTime) {
                    playTime = totalTime;
                    playHandler.obtainMessage(MESSAGE_PLAY_TIME_REFRESHED).sendToTarget();
                    break;
                }
                playHandler.obtainMessage(MESSAGE_PLAY_TIME_REFRESHED).sendToTarget();

                if (isPlaying) {
                    playTime = RecordAudioManager.getInstance(host).getPlayTime();
                } else {
                    playTime += 500;
                }

                actionsManager.setTotalTime(totalTime);
                actionsManager.setPlayTime(playTime);
                RecordAudioManager.getInstance(host).setPlayTime(playTime);
                recordShareVedioManager.setPlayTime(playTime);
                userVedioManager.setPlayTime(playTime);
                try {
                    Thread.sleep(500);
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
            if (actionsManager != null) {
                actionsManager.release();
            }
            if (audioManager != null) {
                audioManager.release();
            }

            if (recordShareVedioManager != null) {
                recordShareVedioManager.release();
            }
            if (userVedioManager != null) {
                userVedioManager.release();
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.cancel();
            }
        }

    }


    private void release() {
        isFinished = true;
        isStarted = false;
        playHandler = null;
    }

    private void handlePlayMessage(Message message) {
        switch (message.what) {
            case MESSAGE_PLAY_TIME_REFRESHED:
                setTimeText();
                if (centerLoaing.getVisibility() == View.VISIBLE) {
                    centerLoaing.setVisibility(View.GONE);
                }

                break;
            case MESSAGE_HIDE_CENTER_LOADING:
                centerLoaing.setVisibility(View.GONE);
                playLayout.setVisibility(View.VISIBLE);
                if (totalTime > 0) {
                    maxProgress = (long) (totalTime / 100);
                    seekBar.setMax((int) maxProgress);
                }
                break;
            case MESSAGE_PLAY_START:
                isStarted = true;
//                centerStartImage.setVisibility(View.GONE);
                break;

            case MESSAGE_PLAY_FINISH:

                break;
        }

    }


    private void refreshTimeText() {
        if (playTimeText != null) {
            timeStr = DateUtil.getMeetingTime(playTime) + "/" + totalTimeStr;
            playTimeText.setText(timeStr);
        }
    }


    private boolean isPlayFinished() {
        return playTime >= totalTime;
    }

    private void resetPlay() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        playTime = seekBar.getProgress() * 100;
//        refreshTimeText();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeek = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeek = false;
    }


    private int parseRecordDetail(RecordDetail recordDetail) {
        if (recordDetail == null) {
            return -1;
        }

        totalTime = recordDetail.getDuration();
        if (totalTime < 1000 * 60 * 60) {
            _time = new SimpleDateFormat("mm:ss").format(totalTime);

        } else {
            int hour = (int) (totalTime / (1000 * 60 * 60));
            if (hour < 10) {
                _time = "0" + hour + ":";
            } else {
                _time = hour + ":";
            }
            _time += new SimpleDateFormat("mm:ss").format(totalTime);
        }

        Log.e("parseRecordDetail", "total_time:" + totalTime + ",time_str:" + _time);
//        totalTimeStr = DateUtil.getMeetingTime(totalTime);
        List<ChannelVO> channels = recordDetail.getChannelVOList();
        if (channels != null && channels.size() > 0) {
            for (ChannelVO channel : channels) {
                Log.e("check_parse_detail", "type:" + channel.getType());

                if (channel.getType() == 1) {
                    audioManager.setAudioDatas(channel.getSectionVOList());
                }
                if (channel.getType() == 2) {
                    recordShareVedioManager.setVedioDatas(channel.getSectionVOList());
                }
                if (channel.getType() == 3) {
                    userVedioManager.saveUserVedios(channel.getUserId() + "", channel.getSectionVOList());
                }
                if (channel.getType() == 4) {
                    userVedioManager.saveUserVedios(channel.getUserId() + "", channel.getSectionVOList());
                }
            }
        }
        userVedioManager.predownLoadUserVedio(host);
        actionsManager.setRecordId(recordDetail.getRecordingId());
        actionsManager.setTotalTime(recordDetail.getDuration());
        return 1;
    }

    private void initWeb() {
//        web.setZOrderOnTop(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.addJavascriptInterface(this, "AnalyticsWebInterface");
//        XWalkPreferences.setValue("enable-javascript", true);
//        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
//        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
//        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        loadWebIndex();
    }

    private void loadWebIndex() {
        int deviceType = DeviceManager.getDeviceType(host);
        String indexUrl = "file:///android_asset/index.html";
        if (deviceType == SupportDevice.BOOK) {
            indexUrl += "?devicetype=4";
        }
        final String url = indexUrl;
        host.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (web == null) {
                    return;
                }
                web.loadUrl(url, null);
                web.loadUrl("javascript:ShowToolbar(" + false + ")", null);
                web.loadUrl("javascript:Record()", null);

            }
        });
    }

    @JavascriptInterface
    public void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        if (actionsManager != null) {
//            actionsManager.preloadFile(url, currentpageNum);
        }
        Log.e("JavascriptInterface", "preLoadFileFunctiona," + url + "     currentpageNum   " + currentpageNum + "   showLoading    " + showLoading);

    }

    @JavascriptInterface
    public void afterLoadPageFunction() {
        Log.e("JavascriptInterface", "afterLoadPageFunction");
        RecordActionsManager.getInstance(host).setLoadingPage(false);
        if (isSeek) {
            RecordActionsManager.getInstance(host).setCurrentPartWebActions(null);
            isSeek = false;
        }
        host.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                web.loadUrl("javascript:ShowToolbar(" + false + ")", null);
                web.loadUrl("javascript:Record()", null);

            }
        });
    }

    private String time;
    private String _time;

    private void setTimeText() {
        time = simpleDateFormat.format(playTime);
        playTimeText.setText(time + "/" + _time);
        seekBar.setMax((int) (totalTime / 10));
        seekBar.setProgress((int) (playTime / 10));
        statusText.setText(R.string.playing);
        startPauseImage.setImageResource(R.drawable.video_stop);
        if(onlyShowTimeText.getVisibility() != View.VISIBLE){
            if(playLayout.getVisibility() != View.VISIBLE){
                playLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showWebVedio(EventPlayWebVedio webVedio) {
        Log.e("showWebVedio", "showWebVedio");
        pause();
        webVedioLayout.setVisibility(View.VISIBLE);
        WebVedioManager.getInstance(host).execute(webVedio.getWebVedio(), playTime);
    }

    private void pause() {
        isStarted = false;
        RecordAudioManager.getInstance(host).pause();
        RecordShareVedioManager.getInstance(host).pause();
        statusText.setText(R.string.paused);
        startPauseImage.setImageResource(R.drawable.video_play);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeWebView(EventCloseWebView webVedio) {
        Log.e("showWebVedio", "showWebVedio");
        webVedioLayout.setVisibility(View.GONE);
        restart();
//        WebVedioManager.getInstance(host).closeVedio();
    }

    private void restart() {
        RecordAudioManager.getInstance(host).restart();
        RecordShareVedioManager.getInstance(host).restart();
        isStarted = true;
        statusText.setText(R.string.playing);
        startPauseImage.setImageResource(R.drawable.video_stop);
    }


}
