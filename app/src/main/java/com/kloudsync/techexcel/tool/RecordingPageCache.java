package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.RecordingPage;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.HashMap;
import java.util.Map;

public class RecordingPageCache {

    private final SharedPreferences cachePreference;
    private static RecordingPageCache instance;
    Gson gson;

    private RecordingPageCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_recording_page_cache", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized RecordingPageCache getInstance(Context context) {
        if (instance == null) {
            instance = new RecordingPageCache(context);
        }
        return instance;
    }


    public void cachePageFile(RecordingPage page) {

        if (page == null || TextUtils.isEmpty(page.getPageUrl())) {
            return;
        }
        Map map = getPageMap();
        if(map == null){
            return;
        }
        map.put(page.getPageUrl(),page);
        cachePreference.edit().putString("page_map", new Gson().toJson(map)).commit();
    }

    public void cachePreloadFile(String url,String localFilePath) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = getPreloadPageMap();
        if(map == null){
            return;
        }
        map.put(url,localFilePath);
        cachePreference.edit().putString("preload_page_map", new Gson().toJson(map)).commit();
    }

    public void removeFile(String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = getPageMap();
        if(map == null){
            return;
        }
        map.remove(url);
        cachePreference.edit().putString("page_map", new Gson().toJson(map)).commit();
    }

    public void removePreloadFile(String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = getPreloadPageMap();
        if(map == null){
            return;
        }
        map.remove(url);
        cachePreference.edit().putString("preload_page_map", new Gson().toJson(map)).commit();
    }

    private Map<String,RecordingPage> getPageMap() {
        String json = cachePreference.getString("page_map", "");
        if (TextUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        return gson.fromJson(json, new TypeToken<Map<String,RecordingPage>>() {
        }.getType());
    }

    private Map<String,String> getPreloadPageMap() {
        String json = cachePreference.getString("preload_page_map", "");
        if (TextUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        return gson.fromJson(json, new TypeToken<Map<String,String>>() {
        }.getType());
    }

    public RecordingPage getPageCache(String url){
        Map<String,RecordingPage> map = getPageMap();
        Log.e("RecordingPageCache","getPageCache, map:" + map);
        if(map != null){
            return map.get(url);
        }
        return null;
    }

    public String getPreloadCache(String url){
        Map<String,String> map = getPreloadPageMap();
        Log.e("RecordingPageCache","getPageCache, map:" + map);
        if(map != null){
            return map.get(url);
        }
        return null;
    }

    public boolean containFile(String url){
        return getPageMap().containsKey(url);
    }


    public void clear() {
        cachePreference.edit().putString("page_map", "").commit();
        cachePreference.edit().putString("preload_page_map", "").commit();
    }


}
