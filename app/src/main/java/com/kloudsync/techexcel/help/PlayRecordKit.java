package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.kloudsync.techexcel.dialog.RecordPlayDialog;
import com.ub.techexcel.bean.Record;
import com.ub.techexcel.bean.RecordDetail;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

/**
 * Created by tonyan on 2019/11/20.
 */

public class PlayRecordKit {

    private Record meetingRecord;

    private boolean playFinished;

    private boolean isStarted;

    private RecordPlayDialog playDialog;

    private Activity host;




    public boolean isPlayFinished() {
        return playFinished;
    }

    public void setPlayFinished(boolean playFinished) {
        this.playFinished = playFinished;
    }


    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void start(){
        isStarted = true;
        Log.e("PlayRecordKit","start");
    }

    public void load(){
        playDialog.show();
    }

    public void pause(){

    }

    public void seek(){

    }

    public PlayRecordKit(Activity host,Record meetingRecord){
        this.host = host;
        this.meetingRecord = meetingRecord;
    }





}
