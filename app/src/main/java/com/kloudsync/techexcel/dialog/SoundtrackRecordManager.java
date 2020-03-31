package com.kloudsync.techexcel.dialog;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.kloudsync.techexcel.help.UploadAudioPopupdate;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.GZipUtil;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.ub.kloudsync.activity.Document;
import com.ub.service.audiorecord.AudioRecorder;
import com.ub.service.audiorecord.FileUtils;
import com.ub.service.audiorecord.RecordEndListener;
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
     * @param isrecordvoice 是否录制声音
     * @param soundtrackBean
     * @param audiosyncll
     */
    public void setInitParams(boolean isrecordvoice, SoundtrackBean soundtrackBean, LinearLayout audiosyncll, TextView timeshow, MeetingConfig meetingConfig) {
//        if(Tools.isOrientationPortrait((Activity) mContext)){
//            Log.e("henshupng","竖屏");
//            Tools.setPortrait((Activity) mContext);
//        }else{
//            Log.e("henshupng","横屏");
//            Tools.setLandscape((Activity) mContext);
//        }
	    this.timeShow = timeshow;
	    timeShow.setOnClickListener(this);
        this.audiosyncll=audiosyncll;
        this.isrecordvoice=isrecordvoice;
        this.meetingConfig=meetingConfig;
        this.soundtrackBean=soundtrackBean;
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
    public void  recordDocumentAction(String action){
        try {
            JSONObject actionjson=new JSONObject(action);
            documentActionList.add(actionjson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void  recordDocumentAction2(String action){

        try {
            JSONArray jsonArray=new JSONArray("[{\"type\":2,\"page\":1,\"time\":1},{\"type\":32,\"page\":1,\"CW\":1872,\"CH\":2422,\"VW\":1882,\"VH\":503,\"ST\":0,\"SL\":0,\"time\":2}," +
                    "{\"type\":34,\"show\":1,\"sleep\":10,\"bd\":0,\"poz\":[[1877,26]],\"delay\":1,\"page\":1,\"VW\":1882,\"CW\":1872,\"ST\":0,\"SL\":0,\"time\":154}," +
                    "{\"type\":34,\"show\":1,\"sleep\":10,\"bd\":0,\"poz\":[[1816,17]],\"delay\":1,\"page\":1,\"VW\":1882,\"CW\":1872,\"ST\":0,\"SL\":0,\"time\":192}," +
                    "{\"type\":34,\"show\":1,\"sleep\":10,\"bd\":0,\"poz\":[[1751,9]],\"delay\":1,\"page\":1,\"VW\":1882,\"CW\":1872,\"ST\":0,\"SL\":0,\"time\":224}," +
                    "{\"type\":22,\"page\":1,\"CW\":1872,\"CH\":2422,\"VW\":1882,\"VH\":553,\"id\":\"bd149d3b-7808-4432-0b6a-48e6c5b2c62f\",\"w\":\"3\",\"color\":\"#458df3\",\"d\":[[1713,19]],\"tar\":\"\"," +
                    "\"time\":608},{\"type\":34,\"show\":1,\"sleep\":10,\"bd\":0,\"poz\":[[1711,30]],\"delay\":1,\"page\":1,\"VW\":1882,\"CW\":1872,\"ST\":0,\"SL\":0,\"time\":655},{\"type\":21,\"page\"" +
                    ":1,\"CW\":1872,\"CH\":2422,\"VW\":1882,\"VH\":553,\"id\":\"bd149d3b-7808-4432-0b6a-48e6c5b2c62f\",\"d\":\"M1713,19 L1712,21 L1712,23 L1712,25 L1712,26 L1711,27 L1711,30 L1711," +
                    "31 L1711,33 L1711,36 L1711,38 L1710,40 L1710,41 L1710,42\",\"w\":\"3\",\"color\":\"#458df3\",\"save\":1,\"tar\":\"\",\"time\":902}," +
                    "{\"type\":35,\"poz\":[1710,42],\"bd\":0,\"page\":1,\"VW\":1882,\"CW\":1872,\"ST\":0,\"SL\":0,\"time\":902},{\"type\":34,\"show\":0,\"page\":1,\"time\":1094}]");
            action=jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("sync---原始数据",action);
        try {
            String  compressdata=GZipUtil.compress(action);
            Log.e("sync---GZIP压缩",compressdata);
            String base64 = LoginGet.getBase64Password(compressdata);
            Log.e("sync---Base64编码",base64);

            String baseee=LoginGet.DecodeBase64Password(base64);
            Log.e("sync---Base64解码码",baseee);
            String  uncompressdata=GZipUtil.unCompress(baseee);
            Log.e("sync---GZIP解压",uncompressdata);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }





    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer2;

    private void  initPlayMusic(final boolean isrecordvoice, String url,String url2){
        Log.e("syncing---", isrecordvoice+"  "+url+"  "+url2);
	    if (isrecordvoice) {
		    //启动录音程序
		    startAudioRecord();
	    }
	    //显示进度条
	    displayLayout();
        initAction();

        EventSoundSync soundSync=new EventSoundSync();
        soundSync.setSoundtrackID(soundtrackBean.getSoundtrackID());
        soundSync.setStatus(1);
        soundSync.setTime(tttime);
        EventBus.getDefault().post(soundSync);

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
	private ImageView timeHidden;
    private SeekBar mSeekBar;
    private boolean isPause=false;

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
                Log.e("syncing---docu","分片大小  "+subAryList.size());
                uploaddocumentActionsList=GZipUtil.getDocumentactionList(subAryList,soundtrackBean);
                if(uploaddocumentActionsList.size()>0){
                    executeUploadDocument(uploaddocumentActionsList.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void  executeUploadDocument(final DocumentAction documentAction){
//        String url="https://testapi.peertime.cn/MeetingServer/sync_action/upload_all_actions";
        String url=AppConfig.URL_MEETING_BASE+"sync_action/upload_all_actions";
        ServiceInterfaceTools.getinstance().uploadAllactions(url, ServiceInterfaceTools.UPLOADALLACTIONS, documentAction, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                if(isrecordvoice){
                    uploadAudioPopupdate.setProgress1(documentAction.getTotal(), documentAction.getIndex());
                }else{
                    uploadAudioPopupdate.setProgress(documentAction.getTotal(), documentAction.getIndex());
                }
                if(documentAction.getIndex()==documentActionList.size()){ //  上传文档动作成功
                    if (isrecordvoice) {    // 完成录音
                        Log.e("syncing---docu","开始上传录音");
                        stopAudioRecord();
                    }else{
                        Log.e("syncing---docu","笔记动作");
                        stopRecordNoteAction(); //笔记动作
                    }
                }else{
                   DocumentAction nextdocumentAction=uploaddocumentActionsList.get(documentAction.getIndex());
                    executeUploadDocument(nextdocumentAction);
                }

            }
        });
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
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
            isPause=false;
            tttime=0;
        }
        audiosyncll.setVisibility(View.GONE);
	    timeShow.setVisibility(View.GONE);
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
