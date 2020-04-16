package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.DigitalNoteEventInSoundtrack;
import com.ub.techexcel.bean.PartWebActions;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SyncNoteEventsCache {

    private final SharedPreferences cachePreference;
    private static SyncNoteEventsCache instance;
    Gson gson;

    private SyncNoteEventsCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_note_events_cache", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized SyncNoteEventsCache getInstance(Context context) {
        if (instance == null) {
            instance = new SyncNoteEventsCache(context);
        }
        return instance;
    }


    public void cacheNoteEvents(String url,String noteEvents) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if(noteEvents == null){
            noteEvents = "";
        }
        Map map = getPageMap();
        if (map == null) {
            return;
        }
        map.put(url, noteEvents);
        cachePreference.edit().putString("note_events_map", new Gson().toJson(map)).commit();
    }

    public void removeNoteEvents(String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = getPageMap();
        if (map == null) {
            return;
        }
        map.remove(url);
        cachePreference.edit().putString("note_events_map", new Gson().toJson(map)).commit();
    }

    private Map<String, String> getPageMap() {
        String json = cachePreference.getString("note_events_map", "");
        if (TextUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        return gson.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    public String getCacheNoteEvents(String url) {
        Map<String, String> map = getPageMap();
        Log.e("SyncWebActionsCache", "getPageCache, map:" + map);
        if (map != null) {
            return map.get(url);
        }
        return null;
    }


    public boolean containNoteEvents(String url) {
        return getPageMap().containsKey(url);
    }


    public void clear() {
        cachePreference.edit().putString("note_events_map", "").commit();
    }


}
