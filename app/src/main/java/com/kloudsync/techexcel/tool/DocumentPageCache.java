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

public class DocumentPageCache {
    private final SharedPreferences cachePreference;
    private static DocumentPageCache instance;
    Gson gson;

    private DocumentPageCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_document_page_cache", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized DocumentPageCache getInstance(Context context) {
        if (instance == null) {
            instance = new DocumentPageCache(context);
        }
        return instance;
    }


    public void cacheFile(DocumentPage page) {

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

    private Map<String,DocumentPage> getPageMap() {
        String json = cachePreference.getString("page_map", "");
        if (TextUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        return gson.fromJson(json, new TypeToken<Map<String,DocumentPage>>() {
        }.getType());
    }

    public DocumentPage getPageCache(String url){
        Map<String,DocumentPage> map = getPageMap();
        Log.e("TvFileCache","getPageCache, map:" + map);
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
    }


}
