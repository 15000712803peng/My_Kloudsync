package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.ub.techexcel.bean.PartWebActions;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SyncWebActionsCache {
    private final SharedPreferences cachePreference;
    private static SyncWebActionsCache instance;
    Gson gson;

    private SyncWebActionsCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_web_actions_cache", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized SyncWebActionsCache getInstance(Context context) {
        if (instance == null) {
            instance = new SyncWebActionsCache(context);
        }
        return instance;
    }


    public void cacheActions(PartWebActions partWebActions) {

        if (partWebActions == null || TextUtils.isEmpty(partWebActions.getUrl())) {
            return;
        }
        Map map = getPageMap();
        if(map == null){
            return;
        }
        map.put(partWebActions.getUrl(),partWebActions);
        cachePreference.edit().putString("web_actions_map", new Gson().toJson(map)).commit();
    }

    public void removeActions(String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = getPageMap();
        if(map == null){
            return;
        }
        map.remove(url);
        cachePreference.edit().putString("web_actions_map", new Gson().toJson(map)).commit();
    }

    private Map<String,PartWebActions> getPageMap() {
        String json = cachePreference.getString("web_actions_map", "");
        if (TextUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        return gson.fromJson(json, new TypeToken<Map<String,PartWebActions>>() {
        }.getType());
    }

    public PartWebActions getPartWebActions(String url){
        Map<String,PartWebActions> map = getPageMap();
        Log.e("SyncWebActionsCache","getPageCache, map:" + map);
        if(map != null){
            return map.get(url);
        }
        return null;
    }

    public PartWebActions getPartWebActions(long playTime,int recordId){
        Map<String,PartWebActions> map = getPageMap();
        if(map != null){
            Set<String> urlSet = map.keySet();
            Iterator<String> iterator = urlSet.iterator();
            while (iterator.hasNext()){
                String url = iterator.next();
                Log.e("SoundtrackActionsManager","check_cache_url:" + url.substring(url.indexOf("__time__separator__") + "__time__separator__".length(),url.length()));
                String[] times = url.substring(url.indexOf("__time__separator__") + "__time__separator__".length(),url.length()).split("__");
                if(playTime >= Long.parseLong(times[0]) && playTime <= Long.parseLong(times[1]) && recordId == Integer.parseInt(times[2])){
                    return map.get(url);
                }
            }
        }
        return null;

    }



    public boolean containPartWebActions(String url){
        return getPageMap().containsKey(url);
    }


    public void clear() {
        cachePreference.edit().putString("web_actions_map", "").commit();
    }


}
