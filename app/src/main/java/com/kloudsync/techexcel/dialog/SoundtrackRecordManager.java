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

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventCreateSync;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.params.EventSoundSync;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.ub.kloudsync.activity.Document;
import com.ub.service.audiorecord.AudioRecorder;
import com.ub.service.audiorecord.FileUtils;
import com.ub.service.audiorecord.RecordEndListener;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;
import com.ub.techexcel.tools.UploadAudioListener;
import com.ub.techexcel.tools.UploadAudioNoteActionTool;
import com.ub.techexcel.tools.UploadAudioTool;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class SoundtrackRecordManager implements View.OnClickListener,UploadAudioListener{

    private Context mContext;
    private static Handler recordHandler;
    private SoundtrackRecordManager(Context context) {
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
                String time= (String) message.obj;
                audiotime.setText(time);
//              timeShow.setText(time);
                break;
        }

    }

    static volatile SoundtrackRecordManager instance;

    public static SoundtrackRecordManager getManager(Context context) {
        if (instance == null) {
            synchronized (SocketMessageManager.class) {
                if (instance == null) {
                    instance = new SoundtrackRecordManager(context);
                }
            }
        }
        return instance;
    }

    private LinearLayout audiosyncll;
    private boolean isrecordvoice;
    private int soundtrackID;
    private int fieldId;
    private String fieldNewPath;
    private MeetingConfig meetingConfig;

    /**
     *
     * @param isrecordvoice 是否录制声音
     * @param soundtrackBean
     * @param audiosyncll
     */
    public void setInitParams(boolean isrecordvoice, SoundtrackBean soundtrackBean, LinearLayout audiosyncll, MeetingConfig meetingConfig) {
        if(Tools.isOrientationPortrait((Activity) mContext)){
            Log.e("henshupng","竖屏");
            Tools.setPortrait((Activity) mContext);
        }else{
            Log.e("henshupng","横屏");
            Tools.setLandscape((Activity) mContext);
        }
        this.audiosyncll=audiosyncll;
        this.isrecordvoice=isrecordvoice;
        this.meetingConfig=meetingConfig;
        soundtrackID = soundtrackBean.getSoundtrackID();
        fieldId = soundtrackBean.getFileId();
        fieldNewPath = soundtrackBean.getPath();
        Document backgroudMusicInfo = soundtrackBean.getBackgroudMusicInfo();
        String url="";
        if (backgroudMusicInfo == null || backgroudMusicInfo.getAttachmentID().equals("0")) {
        } else {
            url=backgroudMusicInfo.getFileDownloadURL();
        }
        String url1="";
        if (soundtrackBean.getNewAudioAttachmentID() != 0) {
            url1=soundtrackBean.getNewAudioInfo().getFileDownloadURL();
        } else if (soundtrackBean.getSelectedAudioAttachmentID() != 0) {
            url1= soundtrackBean.getSelectedAudioInfo().getFileDownloadURL();
        }
        initPlayMusic(isrecordvoice,url,url1);
    }

    private List<JSONObject> noteActionList=new ArrayList<>();

    public void recordNoteAction(NoteRecordType noteRecordType, JSONObject data){
        if(audiosyncll!=null&&audiosyncll.getVisibility()==View.VISIBLE){
            int acitontype=noteRecordType.getActiontype();
            try {
                JSONObject jsonObject=new JSONObject();
                //{actionType:302,time:1,page:1,data:{newId:123,oldId:345}}
                jsonObject.put("actionType",acitontype);
                jsonObject.put("time",tttime);
//                jsonObject.put("page",meetingConfig.getPageNumber());
                jsonObject.put("page",1);
                jsonObject.put("data",data);
                String actions=jsonObject.toString();
                Log.e("recordNoteAction",actions);
                noteActionList.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


//    public void recordNoteAction(NoteRecordType noteRecordType, JSONObject data){
//        if(isrecordvoice){
//            int acitontype=noteRecordType.getActiontype();
//            try {
//                JSONObject jsonObject=new JSONObject();
//                //{actionType:302,time:1,page:1,data:{newId:123,oldId:345}}
//                jsonObject.put("actionType",acitontype);
//                jsonObject.put("time",tttime);
//                jsonObject.put("page",meetingConfig.getPageNumber());
//                jsonObject.put("data",data);
//                String actions=jsonObject.toString();
//                Log.e("recordNoteAction",actions);
////                SocketMessageManager.getManager(mContext).sendMessage_MyActionFrame(actions, meetingConfig);
//                final List<String> ss=new ArrayList<>();
//                ss.add(actions);
//                ss.add(actions);
//                ss.add(actions);
//                ss.add(actions);
//                String noteactionname=  new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//                Observable.just(noteactionname).observeOn(Schedulers.io()).map(new Function<String, File>() {
//                    @Override
//                    public File apply(String name) throws Exception {
//                        File note=FileUtils.createNoteFile(name);
//                        if(note!=null){
////                            boolean is=FileUtils.writeNoteActonToFile(ss,note);
////                            Log.e("notename",note.getAbsolutePath()+"  "+is);
////                           if(is){
////                               return  note;
////                           }
//                        }
//                        return null;
//                    }
//                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<File>() {
//                    @Override
//                    public void accept(File note) throws Exception {
//                        if(note!=null){
//                            Toast.makeText(mContext,note.getAbsolutePath()+"",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }).subscribe();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }



    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer2;

    private void  initPlayMusic(final boolean isrecordvoice, String url,String url2){
        Log.e("syncing---", isrecordvoice+"  "+url+"  "+url2);
        EventSoundSync soundSync=new EventSoundSync();
        soundSync.setSoundtrackID(soundtrackID);
        soundSync.setStatus(1);
        soundSync.setTime(tttime);
        EventBus.getDefault().post(soundSync);
        if(isrecordvoice){
            //启动录音程序
            startAudioRecord();
        }
        //显示进度条
        displayLayout();
        if(!TextUtils.isEmpty(url)) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }else{
                mediaPlayer = new MediaPlayer();
            }
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }

        if (!TextUtils.isEmpty(url2)) {
            if (mediaPlayer2 != null) {
                mediaPlayer2.stop();
                mediaPlayer2.reset();
                mediaPlayer2.release();
                mediaPlayer2 = null;
            }
            mediaPlayer2 = new MediaPlayer();
            try {
                mediaPlayer2.setDataSource(url2);
                mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer2.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }
    }




    private ImageView playstop,syncicon,close;
    private  TextView audiotime,isStatus;
    private SeekBar mSeekBar;
    private boolean isPause=false;

    public void displayLayout() {
        noteActionList.clear();
        audiosyncll.setVisibility(View.VISIBLE);
        playstop = audiosyncll.findViewById(R.id.playstop);
        playstop.setOnClickListener(this);
        playstop.setImageResource(R.drawable.video_stop);
        syncicon =  audiosyncll.findViewById(R.id.syncicon);
        close = audiosyncll.findViewById(R.id.close);
        close.setOnClickListener(this);
        isStatus =  audiosyncll.findViewById(R.id.isStatus);
        audiotime =  audiosyncll.findViewById(R.id.audiotime);
        mSeekBar = audiosyncll.findViewById(R.id.seekBar);
        mSeekBar.setVisibility(View.GONE  );
        if (isrecordvoice) {
            isStatus.setText("Recording");
            syncicon.setVisibility(View.VISIBLE);
        } else {
            isStatus.setText("Syncing");  //只同步  不錄音
            syncicon.setVisibility(View.GONE);
        }
        refreshRecord();
    }

    private int tttime=0;
    private Timer audioplaytimer;
    /**
     * 每隔100毫秒拿录制进度
     */
    private void refreshRecord() {
        tttime = 0;
        audiotime.setText("00:00");
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
        }
        audioplaytimer = new Timer();
        audioplaytimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("refreshTime", isPause + "");
                if (!isPause) {
                    tttime = tttime + 100;
                    Log.e("refreshTime", " " + tttime);
                    if (audiotime != null) {
                        final String time = new SimpleDateFormat("mm:ss").format(tttime);
                        Message ms=Message.obtain();
                        ms.what=MESSAGE_PLAY_TIME_REFRESHED;
                        ms.obj=time;
                        recordHandler.sendMessage(ms);
                    }
                }
            }
        }, 0, 100);
    }


    private AudioRecorder audioRecorder;

    private void startAudioRecord() {
        if (audioRecorder != null) {
            audioRecorder.canel();  //取消录音
        }
        audioRecorder = AudioRecorder.getInstance();
        try {
            if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY) {
                Log.e("syncing---", "startAudioRecord");
                String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                audioRecorder.createDefaultAudio(fileName);
                audioRecorder.startRecord(null);
            }
        } catch (IllegalStateException e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void stopAudioRecord(final int soundtrackID) {
        if (audioRecorder != null) {
            audioRecorder.stopRecord(new RecordEndListener() {
                @Override
                public void endRecord(String fileName) {
                    Log.e("syncing---", "录音结束，开始上传 " + fileName);
                    File file = com.ub.service.audiorecord.FileUtils.getWavFile(fileName);
                    if (file != null) {
                        Log.e("syncing---", file.getAbsolutePath() + "   " + file.getName());
//                        uploadAudioFile(file, soundtrackID, false, false);
                        UploadAudioTool.getManager(mContext).uploadAudio(file,soundtrackID,fieldId,fieldNewPath,audiosyncll,meetingConfig,SoundtrackRecordManager.this);
                    }
                }
            });
        }
    }

    @Override
    public void uploadAudioSuccess() {
        stopRecordNoteAction();
    }

    @Override
    public void uploadAudioFail() {

    }

    private void stopRecordNoteAction(){
//        if(noteActionList.size()>0){
//            final JSONArray jsonArray=new JSONArray();
//            for (int i = 0; i < noteActionList.size(); i++) {
//                jsonArray.put(noteActionList.get(i));
//            }
//            String noteactionname=  new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//            Observable.just(noteactionname).observeOn(Schedulers.io()).map(new Function<String, File>() {
//                @Override
//                public File apply(String name) throws Exception {
//                    File note=FileUtils.createNoteFile(name);
//                    if(note!=null){
//                        boolean is=FileUtils.writeNoteActonToFile(jsonArray.toString(),note);
//                        Log.e("notename",note.getAbsolutePath()+"  "+is+"  "+soundtrackID);
//                        if(is){
//                            return  note;
//                        }
//                    }
//                    return null;
//                }
//            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<File>() {
//                @Override
//                public void accept(File note) throws Exception {
//                    if(note!=null){
//                        UploadAudioNoteActionTool.getManager(mContext).uploadNoteActon(note,soundtrackID,audiosyncll,meetingConfig);
//                    }
//                }
//            }).subscribe();
//        }
    }

    private void pauseOrStartAudioRecord() {
        if (audioRecorder != null) {
            if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START) {
                audioRecorder.pauseRecord();
                Log.e("syncing---", "false");
            } else {
                audioRecorder.startRecord(null);
                Log.e("syncing---", "true");
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.playstop: //暂停或录音
                if (isPause) {
                    isPause=false;
                    resumeMedia();
                } else {
                    isPause=true;
                    pauseMedia();
                }
                if (isrecordvoice) {
                    pauseOrStartAudioRecord();
                }
                break;
            case R.id.close: //结束录音
                release();
                break;
        }
    }


    public void resume(){
        if (isPause) {
            isPause=false;
            resumeMedia();
        }
        if (isrecordvoice) {
            pauseOrStartAudioRecord();
        }
    }


    public void pause(){
        if(!isPause){
            isPause=true;
            pauseMedia();
        }
        if (isrecordvoice) {
            pauseOrStartAudioRecord();
        }
    }


    public void release(){
        if(audiosyncll!=null&&audiosyncll.getVisibility()==View.VISIBLE){
            closeAudioSync();
            StopMedia();
            stopAudioRecord(soundtrackID);
            isrecordvoice=false;
        }
    }

    private void closeAudioSync() {
        EventSoundSync soundSync=new EventSoundSync();
        soundSync.setSoundtrackID(soundtrackID);
        soundSync.setStatus(0);
        soundSync.setTime(tttime);
        EventBus.getDefault().post(soundSync);
        String url2 = AppConfig.URL_PUBLIC + "Soundtrack/EndSync?soundtrackID=" + soundtrackID + "&syncDuration=" + soundSync.getTime();
        ServiceInterfaceTools.getinstance().endSync(url2, ServiceInterfaceTools.ENDSYNC, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
            }
        });
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
            isPause=false;
            tttime=0;
        }
        audiosyncll.setVisibility(View.GONE);
    }


    private void resumeMedia() {
        playstop.setImageResource(R.drawable.video_stop);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.start();
        }
    }
    private void pauseMedia() {
        playstop.setImageResource(R.drawable.video_play);
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.pause();
        }
    }

    private void StopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.stop();
            mediaPlayer2.reset();
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
    }

    public int  getCurrentTime() {
        return  tttime;
    }


}
