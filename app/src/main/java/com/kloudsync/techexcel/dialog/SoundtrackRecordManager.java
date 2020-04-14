package com.kloudsync.techexcel.dialog;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.params.EventSoundSync;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.UploadAudioPopupdate;
import com.kloudsync.techexcel.tool.GZipUtil;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.ub.kloudsync.activity.Document;
import com.ub.service.audiorecord.AudioRecorder;
import com.ub.service.audiorecord.FileUtils;
import com.ub.service.audiorecord.RecordEndListener;
import com.ub.service.audiorecord.RecordStreamListener;
import com.ub.techexcel.bean.AudioActionBean;
import com.ub.techexcel.bean.DocumentAction;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class SoundtrackRecordManager implements View.OnClickListener,UploadAudioListener{

    private Context mContext;
    private static Handler recordHandler;
    private  UploadAudioPopupdate uploadAudioPopupdate=new UploadAudioPopupdate();
    private WebView webView;

    private SoundtrackRecordManager(Context context) {
        this.mContext = context;
        if(uploadAudioPopupdate==null){
            uploadAudioPopupdate=new UploadAudioPopupdate();
        }
        uploadAudioPopupdate.getPopwindow(mContext);
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
                timeShow.setText(time);
                newAudioActionTime(tttime);
                break;
        }
    }

    public  static  SoundtrackRecordManager instance;

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
    private MeetingConfig meetingConfig;
    private TextView timeShow;
    private SoundtrackBean soundtrackBean;


    /**
     *
     * //        if(Tools.isOrientationPortrait((Activity) mContext)){
     * //            Log.e("henshupng","竖屏");
     * //            Tools.setPortrait((Activity) mContext);
     * //        }else{
     * //            Log.e("henshupng","横屏");
     * //            Tools.setLandscape((Activity) mContext);
     * //        }
     * @param isrecordvoice 是否录制声音
     * @param soundtrackBean
     * @param audiosyncll
     */
    public void setInitParams(boolean isrecordvoice, SoundtrackBean soundtrackBean, LinearLayout audiosyncll, WebView webView, TextView timeshow, MeetingConfig meetingConfig) {
        this.webView = webView;
        this.audiosyncll=audiosyncll;
        this.timeShow = timeshow;
        timeShow.setOnClickListener(this);
        this.isrecordvoice=isrecordvoice;
        this.meetingConfig=meetingConfig;
        this.soundtrackBean=soundtrackBean;
        //显示进度条
        displayLayout();
        initAction();
        // 通知笔画开始录制
        EventSoundSync soundSync=new EventSoundSync();
        soundSync.setSoundtrackID(soundtrackBean.getSoundtrackID());
        soundSync.setStatus(1);
        soundSync.setTime(tttime);
        EventBus.getDefault().post(soundSync);

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


    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer2;

    private void  initPlayMusic(final boolean isrecordvoice, String url,String url2){
        Log.e("syncing---", isrecordvoice+"   background music "+url+"  "+url2);
        if (isrecordvoice) {
            //启动录音程序
            initAudioRecord();
        }
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
//                mediaPlayer.prepare();
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    if(isrecordvoice){
                        while (true){
                            if(audioRecorder!=null){
                                if(audioRecorder.getInitStatus()== AudioRecord.STATE_INITIALIZED){
                                    audioRecorder.startRecord(null);
                                    mp.start();
                                    refreshRecord();
                                    break;
                                }
                            }
                        }
                    }else{
                        mp.start();
                        refreshRecord();
                    }



                }
            });
        }else{

            if(isrecordvoice){
                while (true){
                    if(audioRecorder!=null){
                        if(audioRecorder.getInitStatus()== AudioRecord.STATE_INITIALIZED){
                            audioRecorder.startRecord(null);
                            refreshRecord();
                            break;
                        }
                    }
                }
            }else{
                refreshRecord();
            }

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
                mediaPlayer2.prepareAsync();
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
    private ImageView timeHidden;
    private SeekBar mSeekBar;
    private boolean isPause=false;
    private ProgressBar sondtrack_record_load_bar;

    public void displayLayout() {
        audiosyncll.setVisibility(View.VISIBLE);
        playstop = audiosyncll.findViewById(R.id.playstop);
        timeHidden = audiosyncll.findViewById(R.id.timehidden);
        timeHidden.setOnClickListener(this);
        playstop.setOnClickListener(this);
        playstop.setImageResource(R.drawable.video_stop);
        syncicon =  audiosyncll.findViewById(R.id.syncicon);
        close = audiosyncll.findViewById(R.id.close);
        close.setOnClickListener(this);
        isStatus =  audiosyncll.findViewById(R.id.isStatus);
        sondtrack_record_load_bar =  audiosyncll.findViewById(R.id.sondtrack_record_load_bar);
        sondtrack_record_load_bar.setVisibility(View.VISIBLE);
        isStatus.setVisibility(View.INVISIBLE);
        audiotime =  audiosyncll.findViewById(R.id.audiotime);
        audiotime.setText("00:00");
        mSeekBar = audiosyncll.findViewById(R.id.seekBar);
        mSeekBar.setVisibility(View.GONE  );
        if (isrecordvoice) {
            isStatus.setText("Recording");
            syncicon.setVisibility(View.VISIBLE);
        } else {
            isStatus.setText("Syncing");  //只同步  不錄音
            syncicon.setVisibility(View.GONE);
        }
    }




    private List<JSONObject> noteActionList=new ArrayList<>();

    /**
     * 记录笔记动作
     * @param noteRecordType
     * @param data
     */
    public void recordNoteAction(NoteRecordType noteRecordType, JSONObject data){
        if (audiosyncll != null && (audiosyncll.getVisibility() == View.VISIBLE || timeShow.getVisibility() == View.VISIBLE)) {
            int acitontype=noteRecordType.getActiontype();
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("actionType",acitontype);
                jsonObject.put("time",tttime);
//                jsonObject.put("page",meetingConfig.getPageNumber());
                jsonObject.put("page",1);
                jsonObject.put("data",data);
                Log.e("notename",jsonObject.toString());
                noteActionList.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private List<JSONObject> documentActionList=new ArrayList<>();
    /**
     * 记录文档上动作
     */
    public void  recordDocumentAction(String actions){
        Log.e("syncing---", tttime + "");
        try {
            JSONObject jsonObject = new JSONObject(actions);
            jsonObject.put("time", tttime);
            documentActionList.add(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private  void initAction(){
        noteActionList.clear();
        documentActionList.clear();
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("type",2);
            jsonObject.put("page",meetingConfig.getPageNumber());
            jsonObject.put("time",1);
            documentActionList.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int tttime=0;
    private Timer audioplaytimer;
    /**
     * 每隔100毫秒拿录制进度
     */
    private void refreshRecord() {
        getAudioAction(soundtrackBean.getActionBaseSoundtrackID(), 0);
        sondtrack_record_load_bar.setVisibility(View.INVISIBLE);
        isStatus.setVisibility(View.VISIBLE);
        tttime = 0;
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
        }
        audioplaytimer = new Timer();
        audioplaytimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isPause) {
                    tttime = tttime + 100;
                    if (audiotime != null) {
                        final String time = new SimpleDateFormat("mm:ss").format(tttime);
                        Message ms=Message.obtain();
                        ms.what=MESSAGE_PLAY_TIME_REFRESHED;
                        ms.obj=time;
                        recordHandler.sendMessage(ms);
                    }
                    Log.e("refreshTime", " " + tttime);
                }
            }
        }, 0, 100);
    }


    /**
     * 拿随 某时刻后面 20秒  音向 的Actions
     *
     * @param
     */
    private List<AudioActionBean> audioActionBeanList = new ArrayList<>();
    private int startTimee;

    private void getAudioAction(final int soundtrackID, int startTime) {
        if (soundtrackID != 0) {
            startTimee = startTime;
            String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + soundtrackID + "&startTime=" + startTime + "&endTime=" + (startTime + 20000);
            ServiceInterfaceTools.getinstance().getSoundtrackActions(url, ServiceInterfaceTools.GETSOUNDTRACKACTIONS, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    List<AudioActionBean> audioActionBeanList2 = (List<AudioActionBean>) object;
                    audioActionBeanList.clear();
                    if (audioActionBeanList2.size() > 0) {
                        audioActionBeanList.addAll(audioActionBeanList2);
                    }
                }
            });
        }
    }

    private void newAudioActionTime(int locateTime) {
        if (soundtrackBean.getActionBaseSoundtrackID() != 0) {
            Log.e("newAudioActionTime", audioActionBeanList.size() + "    当前播放器的位置 " + locateTime);
            for (int i = 0; i < audioActionBeanList.size(); i++) {
                AudioActionBean audioActionBean = audioActionBeanList.get(i);
                Log.e("newAudioActionTime", locateTime + "   " + audioActionBean.getTime() + "      " + audioActionBean.getData());
                if (locateTime >= audioActionBean.getTime()) {
                    String data = audioActionBean.getData();
                    if (doVideoAction(data)) { //存在  视频文件播放

                    } else {  // 不存在 视频文件播放
                        if (webView != null) {
                            recordDocumentAction(data);
                            webView.loadUrl("javascript:PlayActionByTxt('" + data + "')", null);
                            webView.loadUrl("javascript:Record()", null);
                        }
                    }
                    audioActionBeanList.remove(i);
                    i--;
                } else {
                    break;
                }
            }
            if (locateTime > (startTimee + 10000)) {
                getAudioAction(soundtrackBean.getActionBaseSoundtrackID(), locateTime);
            }
        }
    }


    private boolean doVideoAction(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int actionType = jsonObject.getInt("actionType");
            if (actionType == 19) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private AudioRecorder audioRecorder;

    private void initAudioRecord() {
        if (audioRecorder != null) {
            audioRecorder.canel();  // 取消录音
        }
        audioRecorder = AudioRecorder.getInstance();
        try {
            if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY) {
                String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                audioRecorder.createDefaultAudio(fileName);
            }
        } catch (IllegalStateException e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void stopAudioRecord() {
        if (audioRecorder != null) {
            audioRecorder.stopRecord(new RecordEndListener() {
                @Override
                public void endRecord(String fileName) {
                    Log.e("syncing---", "录音结束，开始上传 " + fileName);
                    File file = com.ub.service.audiorecord.FileUtils.getWavFile(fileName);
                    if (file != null) {
                        Log.e("syncing---", file.getAbsolutePath() + "   " + file.getName());
                        UploadAudioTool.getManager(mContext).uploadAudio(file,soundtrackBean,uploadAudioPopupdate,meetingConfig,SoundtrackRecordManager.this);
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
        uploadAudioPopupdate.setCanChangTitle();
        if(noteActionList.size()>0){
            final JSONArray jsonArray=new JSONArray();
            for (int i = 0; i < noteActionList.size(); i++) {
                jsonArray.put(noteActionList.get(i));
            }
            String noteactionname=  new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            Observable.just(noteactionname).observeOn(Schedulers.io()).map(new Function<String, File>() {
                @Override
                public File apply(String name) throws Exception {
                    File note=FileUtils.createNoteFile(name);
                    if(note!=null){
                        boolean is=FileUtils.writeNoteActonToFile(jsonArray.toString(),note);
                        Log.e("notename",note.getAbsolutePath()+"  "+is+"  "+soundtrackBean.getSoundtrackID());
                        if(is){
                            return  note;
                        }
                    }
                    return null;
                }
            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<File>() {
                @Override
                public void accept(File note) throws Exception {
                    if(note!=null){
                        UploadAudioNoteActionTool.getManager(mContext).uploadNoteActon(note,soundtrackBean.getSoundtrackID(),audiosyncll,meetingConfig);
                    }
                }
            }).subscribe();
        }
    }



    private List<DocumentAction> uploaddocumentActionsList=new ArrayList<>();

    private void stopRecordDocumentAction(){
        uploadAudioPopupdate.StartPop(soundtrackBean);
        if(documentActionList.size()>0){
            try {
                final List<List<JSONObject>> subAryList = GZipUtil.fetchList(documentActionList);
                uploaddocumentActionsList=GZipUtil.getDocumentactionList(subAryList,soundtrackBean);
                Log.e("syncing---docu","分片大小  "+subAryList.size()+"   分组大小 "+uploaddocumentActionsList.size());
                if(uploaddocumentActionsList.size()>0){
                    executeUploadDocument(uploaddocumentActionsList.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void   executeUploadDocument(DocumentAction documentAction){
        Observable.just(documentAction).observeOn(Schedulers.io()).map(new Function<DocumentAction, DocumentAction>() {
            @Override
            public DocumentAction apply(DocumentAction documentAction) throws Exception {
                String url=AppConfig.URL_MEETING_BASE+"sync_action/upload_all_actions";
                int retcode=  ServiceInterfaceTools.getinstance().uploadAllactions2(url, documentAction);
                Log.e("syncing---docu","返回值  "+retcode);
                if(retcode==0){
                    return documentAction;
                }
                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DocumentAction>() {
            @Override
            public void accept(DocumentAction documentAction) throws Exception {
                if(documentAction!=null){
                    if(isrecordvoice){
                        uploadAudioPopupdate.setProgress1(documentAction.getTotal(), documentAction.getIndex());
                    }else{
                        uploadAudioPopupdate.setProgress(documentAction.getTotal(), documentAction.getIndex());
                    }
                    if(documentAction.getIndex()==uploaddocumentActionsList.size()){ //  上传文档动作成功
                        if (isrecordvoice) {   // 完成录音
                            Log.e("syncing---docu","开始上传录音");
                            stopAudioRecord();
                        }else{
                            Log.e("syncing---docu","笔记动作");
                            stopRecordNoteAction(); //笔记动作
                        }
                    }else{
                        Log.e("syncing---docu","开始上传下一个音想动作"+documentAction.getIndex());
                        if(documentAction.getIndex()<uploaddocumentActionsList.size()){
                            DocumentAction nextdocumentAction=uploaddocumentActionsList.get(documentAction.getIndex());
                            executeUploadDocument(nextdocumentAction);
                        }
                    }
                }
            }
        }).subscribe();
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
            case R.id.timehidden:
                timeShow.setVisibility(View.VISIBLE);
                audiosyncll.setVisibility(View.GONE);
                break;
            case R.id.timeshow:
                timeShow.setVisibility(View.GONE);
                audiosyncll.setVisibility(View.VISIBLE);
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
            if (isrecordvoice) {
                pauseOrStartAudioRecord();
            }
            stopRecordDocumentAction();
            instance=null;
        }
    }

    private void closeAudioSync() {
        EventSoundSync soundSync=new EventSoundSync();
        soundSync.setSoundtrackID(soundtrackBean.getSoundtrackID());
        soundSync.setStatus(0);
        soundSync.setTime(tttime);
        EventBus.getDefault().post(soundSync);
        String url2 = AppConfig.URL_PUBLIC + "Soundtrack/EndSync?soundtrackID=" + soundtrackBean.getSoundtrackID() + "&syncDuration=" + soundSync.getTime();
        ServiceInterfaceTools.getinstance().endSync(url2, ServiceInterfaceTools.ENDSYNC, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {

            }
        });

        if (meetingConfig.isMeetingPause()) {  //是否暂停会议
            addsoundtolesson(soundtrackBean.getSoundtrackID() + "");
        }
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
            isPause=false;
            tttime=0;
        }
        audiosyncll.setVisibility(View.GONE);
        timeShow.setVisibility(View.GONE);
    }


    private void addsoundtolesson(final String soundtrackIDs) {
        String url = AppConfig.URL_PUBLIC + "LessonSoundtrack?lessonID=" + meetingConfig.getLessionId() + "&soundtrackIDs=" + soundtrackIDs;
        ServiceInterfaceTools.getinstance().addSoundToLesson(url, ServiceInterfaceTools.ADDSOUNDTOLESSON,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {

                    }
                });
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
