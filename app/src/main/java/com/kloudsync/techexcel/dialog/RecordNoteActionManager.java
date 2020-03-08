package com.kloudsync.techexcel.dialog;

import android.content.Context;

import com.ub.techexcel.tools.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecordNoteActionManager {

    static volatile RecordNoteActionManager instance;
    private Context mContext;
    private SoundtrackRecordManager soundtrackRecordManager;

    public RecordNoteActionManager(Context context) {
        this.mContext = context;
        soundtrackRecordManager=SoundtrackRecordManager.getManager(mContext);
    }

    public static RecordNoteActionManager getManager(Context context) {
        if (instance == null) {
            synchronized (RecordNoteActionManager.class) {
                if (instance == null) {
                    instance = new RecordNoteActionManager(context);
                }
            }
        }
        return instance;
    }


    /**
     *
     * @param noteId  笔记id
     * @param noteData 笔记单条数据
     */
    public void sendDrawActions(long noteId, String noteData) {
//        {actionType:304,time:1,page:1,data:{duration:11,strokeId:"3243-2342-24-4"}}
//        time: start time
//        duration:笔画的持续时长
//        strokeId: 线的唯一ID
        String strokeId;
        String linealldata= Tools.getFromBase64(noteData);
        try {
            JSONObject jsonObject=new JSONObject(linealldata);
            JSONArray linesjson=jsonObject.getJSONArray("lines");
            JSONObject submit=new JSONObject();
            for(int i=0;i<linesjson.length();i++){
                JSONObject linedata=linesjson.getJSONObject(i);
                strokeId=linedata.getString("id");
                int duration=0;
                JSONArray points=linedata.getJSONArray("points");
                if(points.length()>0){
                    JSONArray startpoint=points.getJSONArray(0);
                    double starttime=0;
                    if(startpoint.length()>0){
                         starttime=startpoint.getDouble(startpoint.length()-1);
                    }
                    JSONArray endpoint=points.getJSONArray(points.length()-1);
                    double endtime=0;
                    if(endpoint.length()>0){
                         endtime=endpoint.getDouble(endpoint.length()-1);
                    }
                     duration= (int) ((endtime-starttime)*1000);
                }
                submit.put("strokeId",strokeId);
                submit.put("duration",duration);
                break;
            }
            soundtrackRecordManager.recordNoteAction(NoteRecordType.DRAW_LINE,submit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public  void sendDisplayHomePageActions(long noteId, String strokeId){
//      {actionType:308,time:1,page:1,data:{id:123,lastStrokeId:"1-2-3"}}
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",noteId);
            jsonObject.put("lastStrokeId",strokeId);
            soundtrackRecordManager.recordNoteAction(NoteRecordType.DISPALY_HOMEPAGE,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
