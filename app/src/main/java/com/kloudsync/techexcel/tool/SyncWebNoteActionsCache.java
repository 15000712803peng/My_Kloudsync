package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.bean.NoteDotBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncWebNoteActionsCache {
	private final SharedPreferences cachePreference;
	private static SyncWebNoteActionsCache instance;
	Gson gson;

	private SyncWebNoteActionsCache(Context context) {
		cachePreference = context.getSharedPreferences(AppConfig.KLOUDWEBNOTEACTIONSCACHE, Context.MODE_PRIVATE);
		gson = new Gson();
	}

	public static synchronized SyncWebNoteActionsCache getInstance(Context context) {
		if (instance == null) {
			instance = new SyncWebNoteActionsCache(context);
		}
		return instance;
	}

    /*public void cacheActions(NoteDotBean bean) {
        if (bean == null) {
            return;
        }
        Map map = getPageMap();
        if (map == null) {
            return;
        }
        map.put(bean.getDotId(), bean);
        cachePreference.edit().putString("dot_actions_map", new Gson().toJson(map)).commit();
    }*/

	public void cacheMapActions(List<NoteDotBean> noteDotList) {
		if (noteDotList == null) {
			return;
		}
		Map map = getPageMap();
		if (map == null) {
			return;
		}
		for (NoteDotBean noteDotBean : noteDotList) {
			map.put(noteDotBean.getDotId(), noteDotBean);
		}

		cachePreference.edit().putString("dot_actions_map", new Gson().toJson(map)).commit();
	}

	public void removeListActions(List<String> uuids) {
		if (uuids == null) {
			return;
		}
		Map<String, NoteDotBean> map = getPageMap();
		if (map == null) {
			return;
		} else if (map.size() == 0) {
			return;
		}
		for (int i = 0; i < uuids.size(); i++) {
			Log.e("SyncWebNoteActionsCache", "i=" + i + "--mapsize=" + map.size());
			String key = uuids.get(i);
			if (map.containsKey(key)) {
				map.remove(key);
			}
		}
		cachePreference.edit().putString("dot_actions_map", new Gson().toJson(map)).commit();
	}

	private Map<String, NoteDotBean> getPageMap() {
		String json = cachePreference.getString("dot_actions_map", "");
		if (TextUtil.isEmpty(json)) {
			return new HashMap<>();
		}
		return gson.fromJson(json, new TypeToken<Map<String, NoteDotBean>>() {
		}.getType());
	}

	public Map<String, NoteDotBean> getPartWebActions() {
		Map<String, NoteDotBean> map = getPageMap();
		Log.e("SyncWebNoteActionsCache", "getPageCache, map:" + map);
		if (map != null) {
			return map;
		}
		return null;
	}

  /*  public PartWebActions getPartWebActions(long playTime,int recordId){
        Map<String,PartWebActions> map = getPageMap(dot.getDotId());
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

    }*/


	public boolean containPartWebActions(String dotId) {
		return getPageMap().containsKey(dotId);
	}


	public void clear() {
		cachePreference.edit().putString("dot_actions_map", "").commit();
	}


}
