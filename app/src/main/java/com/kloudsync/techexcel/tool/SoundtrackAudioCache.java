package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.HashMap;
import java.util.Map;

public class SoundtrackAudioCache {

    private final SharedPreferences cachePreference;
    private static SoundtrackAudioCache instance;
    Gson gson;

    private SoundtrackAudioCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_soundtrack_audio_cache", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized SoundtrackAudioCache getInstance(Context context) {
        if (instance == null) {
            instance = new SoundtrackAudioCache(context);
        }
        return instance;
    }


    public void cacheAudio(String vedioUrl,String filePath) {

        if (TextUtils.isEmpty(vedioUrl)) {
            return;
        }
        Map map = getPageMap();
        if (map == null) {
            return;
        }
        map.put(vedioUrl, filePath);
        cachePreference.edit().putString("soundtrack_audio_map", new Gson().toJson(map)).commit();
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
        cachePreference.edit().putString("soundtrack_audio_map", new Gson().toJson(map)).commit();
    }

    private Map<String, String> getPageMap() {
        String json = cachePreference.getString("soundtrack_audio_map", "");
        if (TextUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        return gson.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    public String getAudioPath(String url) {
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
        cachePreference.edit().putString("soundtrack_audio_map", "").commit();
    }


}
