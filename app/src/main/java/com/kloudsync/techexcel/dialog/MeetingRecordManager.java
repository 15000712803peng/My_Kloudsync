package com.kloudsync.techexcel.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.params.EventSoundSync;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.ub.kloudsync.activity.Document;
import com.ub.service.KloudWebClientManager;
import com.ub.service.audiorecord.AudioRecorder;
import com.ub.service.audiorecord.RecordEndListener;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.UploadAudioTool;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class MeetingRecordManager implements View.OnClickListener {

    private Context mContext;
    private static Handler recordHandler;
    private MeetingRecordManager(Context context) {
        this.mContext = context;
        recordHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if (recordHandler == null) {
                    return;
                }
                handlePlayMessage(msg);
                super.handleMessage(msg);
            }
        };
    }

    private static final int MESSAGE_PLAY_TIME_REFRESHED = 1;

    private void handlePlayMessage(Message message) {
        switch (message.what) {
            case MESSAGE_PLAY_TIME_REFRESHED:

                break;
        }

    }

    static volatile MeetingRecordManager instance;

    public static MeetingRecordManager getManager(Context context) {
        if (instance == null) {
            synchronized (SocketMessageManager.class) {
                if (instance == null) {
                    instance = new MeetingRecordManager(context);
                }
            }
        }
        return instance;
    }

    ImageView  recordstatus;
    SocketMessageManager messageManager;
    MeetingConfig meetingConfig;

    public void initRecording(ImageView  recordstatus,final SocketMessageManager messageManager,MeetingConfig  meetingConfig){
           this.recordstatus=recordstatus;
           this.messageManager=messageManager;
           this.meetingConfig= meetingConfig;
    }

    /**
     *
     * @param isRecordmeeting
     */
    public void startRecording(final boolean isRecordmeeting) {
        Observable.just("").observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

                Log.e("startRecording",""+meetingConfig.getMeetingHostId()+"  "+meetingConfig.getPresenterId()+"  "+isPresenter()+" "+isRecordmeeting);
                if(isRecordmeeting&&isPresenter()){
                    recordstatus.setVisibility(View.VISIBLE);
                    // 1 MEETING_STATUS 把status改成1    已发送
                    // 2
                    String url = AppConfig.URL_PUBLIC_AUDIENCE + "MeetingServer/recording/start_recording?meetingId=" + meetingConfig.getMeetingId();
                    MeetingServiceTools.getInstance().startRecording(url, MeetingServiceTools.STARTRECORDING, new ServiceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            int recordingId = (int) object;
                            // 3 通过socket 消息 通知服务器端 麦克风 摄像头 状态
                            boolean isMicroOn = getSettingCache((Activity) mContext).getMeetingSetting().isMicroOn();
                            boolean isCameraOn = getSettingCache((Activity) mContext).getMeetingSetting().isCameraOn();
                            Log.e("startRecording",isMicroOn+"    "+isCameraOn);
                            messageManager.sendMessage_recording_AgoraStatusChange(meetingConfig,isMicroOn,isCameraOn);

                            // 4 心跳增加 上面的4 个状态,所有人在meeting中就要发
      //                    KloudWebClientManager.getInstance().startMeetingRecord(true);
                        }
                    });
                }



            }
        }).subscribe();





    }


    public boolean isPresenter(){
        if (!TextUtils.isEmpty(meetingConfig.getPresenterSessionId())) {
            if (AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                Log.e("setVideoEncoder","是presenter");
                return true;
            }
        }
        Log.e("setVideoEncoder","不是presenter");
        return false;
    }

    private MeetingSettingCache settingCache;
    private MeetingSettingCache getSettingCache(Activity host) {
        if (settingCache == null) {
            settingCache = MeetingSettingCache.getInstance(host);
        }
        return settingCache;
    }


    @Override
    public void onClick(View view) {

    }
}
