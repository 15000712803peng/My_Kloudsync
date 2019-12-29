package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventCloseWebView;
import com.kloudsync.techexcel.bean.EventPlayWebVedio;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.SoundtrackDetail;
import com.kloudsync.techexcel.bean.SoundtrackMediaInfo;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.RecordActionsManager;
import com.kloudsync.techexcel.help.RecordAudioManager;
import com.kloudsync.techexcel.help.RecordShareVedioManager;
import com.kloudsync.techexcel.help.SoundtrackActionsManager;
import com.kloudsync.techexcel.help.SoundtrackAudioManager;
import com.kloudsync.techexcel.help.UserVedioManager;
import com.kloudsync.techexcel.help.WebVedioManager;
import com.ub.techexcel.bean.ChannelVO;
import com.ub.techexcel.bean.Record;
import com.ub.techexcel.bean.RecordDetail;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.DateUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class SoundtrackPlayDialog implements View.OnClickListener, HeaderRecyclerAdapter.OnItemClickListener, Dialog.OnDismissListener, SeekBar.OnSeekBarChangeListener {
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
    SoundtrackAudioManager soundtrackAudioManager;

    private MeetingConfig meetingConfig;
    private TextView playTimeText;
    private SeekBar seekBar;
    private RelativeLayout webVedioLayout;
    private SurfaceView webVedioSurface;
    private ImageView closeVedioImage;
    private ImageView closeDialogImage;
    private ImageView startPauseImage;
    private LinearLayout controllerLayout;

    //
    String totalTimeStr = "00:00";
    String timeStr = "00:00/00:00";
    private long totalTime;
    //
    UserVedioManager userVedioManager;
    XWalkView web;
    TextView statusText;

    public void setSoundtrackDetail(SoundtrackDetail soundtrackDetail) {
        this.soundtrackDetail = soundtrackDetail;
    }

    public SoundtrackPlayDialog(Activity host, SoundtrackDetail soundtrackDetail, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        Log.e("check_dialog", "new_dialog");
        this.host = host;
        this.soundtrackDetail = soundtrackDetail;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.dialog_play_soundtrack, null);
        dialog = new Dialog(host, R.style.my_dialog);
        centerLoaing = view.findViewById(R.id.layout_center_loading);
        webVedioSurface = view.findViewById(R.id.web_vedio_surface);
        web = view.findViewById(R.id.web);
        statusText = view.findViewById(R.id.txt_status);
        webVedioLayout = view.findViewById(R.id.layout_web_vedio);
        playTimeText = view.findViewById(R.id.txt_play_time);
        seekBar = view.findViewById(R.id.seek_bar);
        controllerLayout = view.findViewById(R.id.layout_soundtrack_controller);
        seekBar.setOnSeekBarChangeListener(this);
        startPauseImage = view.findViewById(R.id.image_play_pause);
        startPauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundtrackAudioManager.isPlaying()){
                    pause();
                }else {
                    restart();
                }
            }
        });
        closeVedioImage = view.findViewById(R.id.image_close_veido);
        closeDialogImage = view.findViewById(R.id.close);
        closeVedioImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WebVedioManager.getInstance(host).closeVedio();
                webVedioLayout.setVisibility(View.GONE);

            }
        });
        closeDialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        initWeb();
        width = (int) (host.getResources().getDisplayMetrics().widthPixels * 0.95f);
        heigth = (int) (host.getResources().getDisplayMetrics().heightPixels * 0.95f);
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
            case R.id.image_record_start:
                Log.e("RecordPlayDialog", "start");
                break;
            case R.id.image_center_start:
                if (playHandler != null) {
                    playHandler.obtainMessage(MESSAGE_PLAY_START).sendToTarget();
                }
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
        EventBus.getDefault().register(this);
        soundtrackAudioManager = SoundtrackAudioManager.getInstance(host);
        soundtrackAudioManager.setSoundtrackAudio(soundtrackDetail.getNewAudioInfo());
        new PlayTimeTask().execute();

    }

    @Override
    public void onItemClick(int position, Object data) {

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
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // 播放完成或者手动关闭dialog isFinished = true;
            while (!isFinished) {
//                Log.e("RecordPlayDialog","is finish:" + isFinished);
                Log.e("check_play", "playTime:" + playTime + ",isplaying:" + soundtrackAudioManager.isPlaying());
                synchronized (SoundtrackPlayDialog.this) {
                    if (soundtrackAudioManager.isPlaying()) {
                        playTime = soundtrackAudioManager.getPlayTime();
                        totalTime = soundtrackAudioManager.getDuration();
                        actionsManager.setTotalTime(totalTime);
                        actionsManager.setPlayTime(playTime);
                        if (playHandler != null) {
                            playHandler.obtainMessage(MESSAGE_PLAY_TIME_REFRESHED).sendToTarget();
                        }
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("check_dialog", "time_task_post_execute");
            if (userVedioManager != null) {
                userVedioManager.release();
            }
            if (soundtrackAudioManager != null) {
                soundtrackAudioManager.release();
            }
            if (actionsManager != null) {
                actionsManager.release();
            }
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
    }

    private void handlePlayMessage(Message message) {
        switch (message.what) {
            case MESSAGE_PLAY_TIME_REFRESHED:
                setTimeText();
                if (centerLoaing.getVisibility() == View.VISIBLE) {
                    centerLoaing.setVisibility(View.GONE);
                }
                if(controllerLayout.getVisibility() != View.VISIBLE){
                    controllerLayout.setVisibility(View.VISIBLE);
                }
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
        final String time = new SimpleDateFormat("mm:ss").format(playTime);
        final String _time = new SimpleDateFormat("mm:ss").format(totalTime);
        playTimeText.setText(time + "/" + _time);
        seekBar.setMax((int) (totalTime / 10));
        seekBar.setProgress((int) (playTime / 10));
        statusText.setText(R.string.playing);
        startPauseImage.setImageResource(R.drawable.video_play);

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        playTime = seekBar.getProgress() * 100;
//        refreshTimeText();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.e("seek_bar","start_tracking");
        isSeek = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeek = false;
        Log.e("seek_bar","stop_tracking");
        seekTo(seekBar.getProgress() * 10);
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
        web.load("javascript:ShowToolbar(" + false + ")", null);
        web.load("javascript:Record()", null);
    }

    @org.xwalk.core.JavascriptInterface
    public void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("JavascriptInterface", "preLoadFileFunctiona," + url + "     currentpageNum   " + currentpageNum + "   showLoading    " + showLoading);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showWebVedio(EventPlayWebVedio webVedio) {
        Log.e("showWebVedio", "showWebVedio");
        webVedioLayout.setVisibility(View.VISIBLE);
        WebVedioManager.getInstance(host).execute(webVedio.getWebVedio(), playTime);
        statusText.setText(R.string.paused);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeWebView(EventCloseWebView webVedio) {
        Log.e("showWebVedio", "showWebVedio");
        webVedioLayout.setVisibility(View.GONE);
//        WebVedioManager.getInstance(host).closeVedio();

    }

    private void pause(){
        SoundtrackAudioManager.getInstance(host).pause();
        statusText.setText(R.string.paused);
        startPauseImage.setImageResource(R.drawable.video_stop);
    }

    private void restart(){
        SoundtrackAudioManager.getInstance(host).restart();
        statusText.setText(R.string.playing);
        startPauseImage.setImageResource(R.drawable.video_play);
    }

    private void close(){
        release();
        dismiss();
    }

    private void seekTo(int time){
        actionsManager.seekTo(time);
    }

}
