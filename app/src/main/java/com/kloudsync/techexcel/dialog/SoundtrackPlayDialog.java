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
import com.kloudsync.techexcel.bean.SoundtrackDetail;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.RecordActionsManager;
import com.kloudsync.techexcel.help.RecordAudioManager;
import com.kloudsync.techexcel.help.RecordShareVedioManager;
import com.kloudsync.techexcel.help.SoundtrackActionsManager;
import com.kloudsync.techexcel.help.UserVedioManager;
import com.ub.techexcel.bean.ChannelVO;
import com.ub.techexcel.bean.Record;
import com.ub.techexcel.bean.RecordDetail;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.DateUtil;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import java.util.List;


public class SoundtrackPlayDialog implements View.OnClickListener, HeaderRecyclerAdapter.OnItemClickListener ,Dialog.OnDismissListener,SeekBar.OnSeekBarChangeListener{
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
    SoundtrackActionsManager actionsManager;
    //
    String totalTimeStr = "00:00";
    String timeStr = "00:00/00:00";
    private long totalTime;

    //
    UserVedioManager userVedioManager;
    XWalkView web;

    public void setSoundtrackDetail(SoundtrackDetail soundtrackDetail) {
        this.soundtrackDetail = soundtrackDetail;
    }

    public SoundtrackPlayDialog(Activity host, SoundtrackDetail soundtrackDetail) {
        Log.e("check_dialog","new_dialog");
        this.host = host;
        this.soundtrackDetail = soundtrackDetail;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.dialog_play_soundtrack, null);
        dialog = new Dialog(host, R.style.my_dialog);
        centerLoaing = view.findViewById(R.id.layout_center_loading);
        SurfaceView  webVedioSurface = view.findViewById(R.id.web_vedio_surface);
        web = view.findViewById(R.id.web);
        initWeb();
        //* (0.95f)
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

    }

    public boolean isShowing() {
        if(dialog != null){
            return dialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        Log.e("check_dialog","dialog_dismiss");
        release();
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
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
        Log.e("SoundtrackPlayDialog","show,dialog:" + dialog);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
//            meetingTimeTask.execute();
        }
    }

    @Override
    public void onItemClick(int position, Object data) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();
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

                break;
            case MESSAGE_PLAY_START:
                isStarted = true;
                break;

            case MESSAGE_PLAY_FINISH:

                break;
        }

    }

    private void setTimeText(){

    }

    private void refreshTimeText(){

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
        Log.e("JavascriptInterface", "preLoadFileFunctiona," + url + "     currentpageNum   " + currentpageNum + "   showLoading    " + showLoading);

    }

}
