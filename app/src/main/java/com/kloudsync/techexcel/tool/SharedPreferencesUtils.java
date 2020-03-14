package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.app.App;

import java.util.ArrayList;
import java.util.List;


public class SharedPreferencesUtils {


	private static SharedPreferences getSharedPreference(String fileName) {
		return App.getAppContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
	}

	public static <T> void putString(String fileName, String tag, T value) {
		SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
		String strJson;
		if (null == value) {
			editor.putString(tag, (String) value);
		} else {
			//转换成json数据，再保存
			strJson = new Gson().toJson(value);
			editor.putString(tag, strJson);
		}
		editor.commit();
	}

	public static <T> void putList(String fileName, String tag, List<T> datalist) {
		if (null == datalist)
			return;
		SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
		//转换成json数据，再保存
		String strJson = new Gson().toJson(datalist);
		editor.putString(tag, strJson);
		editor.commit();
	}

	public static <T> T getString(String fileName, String tag, Class<T> classOfT) {
		SharedPreferences sp = getSharedPreference(fileName);
		T value;
		String strJson = sp.getString(tag, null);
		if (strJson == null) {
			return null;
		} else {
			value = new Gson().fromJson(strJson, classOfT);
			return value;
		}
	}

	public static <T> List<T> getList(String fileName, String key, TypeToken<List<T>> typeToken) {
		SharedPreferences sp = getSharedPreference(fileName);
		String strJson = sp.getString(key, null);
		List<T> datalist = new ArrayList<>();
		if (strJson == null) {
			return datalist;
		}
		datalist = new Gson().fromJson(strJson, typeToken.getType());
		return datalist;
	}
}
