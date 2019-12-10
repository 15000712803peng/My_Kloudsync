package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.RecordingPage;
import com.kloudsync.techexcel.config.RealMeetingSetting;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.HashMap;
import java.util.Map;

public class MeetingSettingCache {

    private final SharedPreferences cachePreference;
    private static MeetingSettingCache instance;
    Gson gson;

    private MeetingSettingCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_meeting_setting", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized MeetingSettingCache getInstance(Context context) {
        if (instance == null) {
            instance = new MeetingSettingCache(context);
        }
        return instance;
    }

    public void setMicroOn(boolean isOn){
        RealMeetingSetting setting = getMeetingSetting();
        setting.setMicroOn(isOn);
        cachePreference.edit().putString("meeting_setting",gson.toJson(setting)).commit();
    }

    public void setCameraOn(boolean isOn){
        RealMeetingSetting setting = getMeetingSetting();
        setting.setCameraOn(isOn);
        cachePreference.edit().putString("meeting_setting",gson.toJson(setting)).commit();

    }

    public void setVoiceStatus(int status){
        RealMeetingSetting setting = getMeetingSetting();
        setting.setVoiceStatus(status);
        cachePreference.edit().putString("meeting_setting",gson.toJson(setting)).commit();
    }


    public void setRecordOn(boolean isOn){
        RealMeetingSetting setting = getMeetingSetting();
        setting.setRecordOn(isOn);
        cachePreference.edit().putString("meeting_setting",gson.toJson(setting)).commit();

    }

    public RealMeetingSetting getMeetingSetting(){
        String setting = cachePreference.getString("meeting_setting","");
        if(TextUtils.isEmpty(setting)){
            return new RealMeetingSetting();
        }else {
            return gson.fromJson(setting,RealMeetingSetting.class);
        }
    }



    public void clear() {
        cachePreference.edit().putString("kloud_meeting_setting", "").commit();
    }


}
