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

public class NoteImageCache {

    private final SharedPreferences cachePreference;
    private static NoteImageCache instance;
    Gson gson;
    private NoteImageCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_note_image_cache", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized NoteImageCache getInstance(Context context) {
        if (instance == null) {
            instance = new NoteImageCache(context);
        }
        return instance;
    }


    public void cacheNoteImage(String url,String localPath) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = getNoteMap();
        if(map == null){
            return;
        }
        map.put(url,localPath);
        cachePreference.edit().putString("note_images", new Gson().toJson(map)).commit();
    }

    public void removeNoteImage(String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = getNoteMap();
        if(map == null){
            return;
        }
        map.remove(url);
        cachePreference.edit().putString("note_images", new Gson().toJson(map)).commit();
    }

    private Map<String,String> getNoteMap() {
        String json = cachePreference.getString("note_images", "");
        if (TextUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        return gson.fromJson(json, new TypeToken<Map<String,String>>() {
        }.getType());
    }

    public String getNoteImage(String url){
        Map<String,String> map = getNoteMap();
        Log.e("NoteImageCache","getPageCache, map:" + map);
        if(map != null){
            return map.get(url);
        }
        return null;
    }

    public boolean containFile(String url){
        return getNoteMap().containsKey(url);
    }


    public void clear() {
        cachePreference.edit().putString("note_images", "").commit();
    }


}
