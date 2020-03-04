package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.DocumentPage;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.HashMap;
import java.util.Map;

public class MeetingUserVedioCache {

    private final SharedPreferences cachePreference;
    private static MeetingUserVedioCache instance;
    Gson gson;

    private MeetingUserVedioCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_meeting_user_vedio_cache", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized MeetingUserVedioCache getInstance(Context context) {
        if (instance == null) {
            instance = new MeetingUserVedioCache(context);
        }
        return instance;
    }


    public void cacheVedio(String vedioUrl,String filePath) {

        if (TextUtils.isEmpty(vedioUrl)) {
            return;
        }
        Map map = getPageMap();
        if (map == null) {
            return;
        }
        map.put(vedioUrl, filePath);
        cachePreference.edit().putString("user_vedio_map", new Gson().toJson(map)).commit();
    }

    public void removeFile(String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = getPageMap();
        if (map == null) {
            return;
        }
        map.remove(url);
        cachePreference.edit().putString("user_vedio_map", new Gson().toJson(map)).commit();
    }

    private Map<String, String> getPageMap() {
        String json = cachePreference.getString("user_vedio_map", "");
        if (TextUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        return gson.fromJson(json, new TypeToken<Map<String, DocumentPage>>() {
        }.getType());
    }

    public String getVedioPath(String url) {
        Map<String, String> map = getPageMap();
        if (map != null) {
            return map.get(url);
        }
        return null;
    }

    public boolean containFile(String url) {
        return getPageMap().containsKey(url);
    }


    public void clear() {
        cachePreference.edit().putString("user_vedio_map", "").commit();
    }


}
