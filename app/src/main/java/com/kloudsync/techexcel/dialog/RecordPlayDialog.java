package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.PlayRecordKit;
import com.kloudsync.techexcel.help.RecordActionsManager;
import com.kloudsync.techexcel.help.RecordAudioManager;
import com.kloudsync.techexcel.help.RecordShareVedioManager;
import com.kloudsync.techexcel.help.UserVedioManager;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.ChannelVO;
import com.ub.techexcel.bean.Record;
import com.ub.techexcel.bean.RecordDetail;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.DateUtil;
import org.greenrobot.eventbus.EventBus;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecordPlayDialog implements View.OnClickListener, HeaderRecyclerAdapter.OnItemClickListener ,Dialog.OnDismissListener,SeekBar.OnSeekBarChangeListener{
    public Activity host;
    public Dialog dialog;
    public int width;
    public int heigth;
    private RecordDetail recordDetail;
    private Record record;
    //view
    private View view;
    private TextView playTimeText;
    private ImageView recordStartImage;
    private LinearLayout centerLoaing;
    private RelativeLayout playLayout;
    private ImageView centerStartImage;
    private SeekBar seekBar;
    private SurfaceView webVedioSurface;
    private SurfaceView shareVedioSurface;
    private RecyclerView userList;
    // play status
    private volatile long playTime;
    private volatile long totalTime;
    private volatile boolean isFinished;
    private volatile  boolean isStarted;
    private long maxProgress;
    private volatile long currentProgress;
    private volatile boolean isSeek;
    // data
    private static Handler playHandler;
    private MeetingTimeTask meetingTimeTask;
    // message
    private static final int MESSAGE_PLAY_TIME_REFRESHED = 1;
    private static final int MESSAGE_HIDE_CENTER_LOADING = 2;
    private static final int MESSAGE_PLAY_START = 3;
    private static final int MESSAGE_PLAY_FINISH= 4;

    //
    String totalTimeStr = "00:00";
    String timeStr = "00:00/00:00";

    //
    RecordAudioManager audioManager;
    RecordActionsManager actionsManager;
    RecordShareVedioManager recordShareVedioManager;
    UserVedioManager userVedioManager;
    XWalkView web;

    public void setRecord(Record record) {
        this.record = record;
    }

    public RecordPlayDialog(Activity host,Record record) {
        Log.e("check_dialog","new_dialog");
        this.host = host;
        this.record = record;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.dialog_play_record, null);
        dialog = new Dialog(host, R.style.my_dialog);
        playTimeText = view.findViewById(R.id.txt_play_time);
        recordStartImage = view.findViewById(R.id.image_record_start);
        recordStartImage.setOnClickListener(this);
        centerLoaing = view.findViewById(R.id.layout_center_loading);
        playLayout = view.findViewById(R.id.layout_play);
        centerStartImage = view.findViewById(R.id.image_center_start);
        centerStartImage.setOnClickListener(this);
        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        userList = view.findViewById(R.id.list_user);
        userList.setLayoutManager(new LinearLayoutManager(host, LinearLayoutManager.VERTICAL, false));
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
        meetingTimeTask = new MeetingTimeTask();
        playHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(playHandler == null || dialog == null || !dialog.isShowing()){
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
        actionsManager.setWeb(web);
        actionsManager.setUserVedioManager(userVedioManager);
        actionsManager.setSurfaceView(webVedioSurface);
        userVedioManager.setAdapter(userList);
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        Log.e("check_dialog","dialog_dismiss");
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

            case R.id.doc_img_close:
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.image_record_start:
                Log.e("RecordPlayDialog","start");
                break;
            case R.id.image_center_start:
                if(playHandler != null){
                    playHandler.obtainMessage(MESSAGE_PLAY_START).sendToTarget();
                }
                break;
            default:
                break;

        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            requestMeetingRecord();
            meetingTimeTask.execute();
        }
    }

    @Override
    public void onItemClick(int position, Object data) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();
    }

    private void requestMeetingRecord(){
        if(record != null){
            String url = "https://api.peertime.cn/MeetingServer/recording/recording_item?recordingId=" + record.getRecordingId();
            ServiceInterfaceTools.getinstance().getRecordingItem(url, ServiceInterfaceTools.GETRECORDINGITEM, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    recordDetail = (RecordDetail) object;
                    if(recordDetail != null){
                        parseRecordDetail(recordDetail);
                    }
                    playHandler.obtainMessage(MESSAGE_HIDE_CENTER_LOADING).sendToTarget();

                }
            });
        }

    }

    class MeetingTimeTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            playTime = 0;
            isFinished = false;
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // 播放完成或者手动关闭dialog isFinished = true;
            while (!isFinished){
                Log.e("RecordPlayDialog","is finish:" + isFinished);
                if(isStarted){
                    if(isPlayFinished()){
                        isFinished = true;
                        playHandler.obtainMessage(MESSAGE_PLAY_FINISH).sendToTarget();
                        break;
                    }
                    if(isSeek){
                        continue;
                    }
                    playTime += 200;
                    if(playHandler != null){
                        playHandler.obtainMessage(MESSAGE_PLAY_TIME_REFRESHED).sendToTarget();
                        audioManager.setPlayTime(playTime);
                        actionsManager.setPlayTime(playTime);
                        recordShareVedioManager.setPlayTime(playTime);
                        userVedioManager.setPlayTime(playTime);
                    }

                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("check_dialog","time_task_post_execute");
            if(actionsManager != null){
                actionsManager.release();
            }
            if(audioManager != null){
                audioManager.release();
            }
            if(recordShareVedioManager != null){
                recordShareVedioManager.release();
            }
            if(userVedioManager != null){
                userVedioManager.release();
            }
        }
    }


    private void release(){
        isFinished = true;
        isStarted = false;
        playHandler = null;
    }

    private void handlePlayMessage(Message message){
        switch (message.what){
            case MESSAGE_PLAY_TIME_REFRESHED:
                setTimeText();
                break;
            case MESSAGE_HIDE_CENTER_LOADING:
                centerLoaing.setVisibility(View.GONE);
                playLayout.setVisibility(View.VISIBLE);
                if(totalTime > 0){
                    maxProgress = (long)(totalTime / 100);
                    seekBar.setMax((int)maxProgress);
                }
                break;
            case MESSAGE_PLAY_START:
                isStarted = true;
                centerStartImage.setVisibility(View.GONE);
                break;

            case MESSAGE_PLAY_FINISH:

                break;
        }

    }

    private void setTimeText(){
        if(playTimeText != null){
            timeStr = DateUtil.getMeetingTime(playTime) + "/" + totalTimeStr;
            playTimeText.setText(timeStr);
            currentProgress = playTime / 100;
            seekBar.setProgress((int)(currentProgress));
        }
    }

    private void refreshTimeText(){
        if(playTimeText != null){
            timeStr = DateUtil.getMeetingTime(playTime) + "/" + totalTimeStr;
            playTimeText.setText(timeStr);
        }
    }


    private boolean isPlayFinished(){
        return playTime >= totalTime;
    }

    private void resetPlay(){

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

    private void parseRecordDetail(RecordDetail recordDetail){
        totalTime = recordDetail.getDuration();
        totalTimeStr = DateUtil.getMeetingTime(totalTime);
        List<ChannelVO> channels = recordDetail.getChannelVOList();
        if(channels != null && channels.size() > 0){
            for(ChannelVO channel : channels){
                if(channel.getType() == 1){
                    audioManager.setAudioDatas(channel.getSectionVOList());
                }
                if(channel.getType() == 2){
                    recordShareVedioManager.setVedioDatas(channel.getSectionVOList());
                }
                if(channel.getType() == 3){
                    userVedioManager.addUserVedios(channel.getUserId() +"",channel.getSectionVOList());
                }
                if(channel.getType() == 4){
                    userVedioManager.addUserVedios(channel.getUserId() +"",channel.getSectionVOList());
                }

            }
        }
        actionsManager.setRecordId(recordDetail.getRecordingId());
        actionsManager.setTotalTime(recordDetail.getDuration());
    }

    private void initWeb(){
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
        final String url = indexUrl;
        host.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (web == null) {
                    return;
                }
                web.load(url, null);
                web.load("javascript:ShowToolbar(" + false + ")", null);
                web.load("javascript:Record()", null);

            }
        });
    }

    @org.xwalk.core.JavascriptInterface
    public void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        if(actionsManager != null){
            actionsManager.preloadFile(url, currentpageNum);
        }
        Log.e("JavascriptInterface", "preLoadFileFunctiona," + url + "     currentpageNum   " + currentpageNum + "   showLoading    " + showLoading);

    }

}
