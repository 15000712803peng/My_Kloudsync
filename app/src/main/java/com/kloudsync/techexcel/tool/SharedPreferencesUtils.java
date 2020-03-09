package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;


public class SharedPreferencesUtils {


	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Gson mGson;


	public SharedPreferencesUtils(Context context, String name) {
		sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		editor = sp.edit();
		mGson = new Gson();
	}

	public <T> void setString(String tag, T value) {
		String strJson;
		if (null == value) {
			editor.putString(tag, (String) value);
		} else {
			//转换成json数据，再保存
			strJson = mGson.toJson(value);
			editor.putString(tag, strJson);
		}
		editor.commit();

	}

	public <T> T getString(String tag, Class<T> classOfT) {
		T value;
		String strJson = sp.getString(tag, null);
		if (strJson == null) {
			return null;
		} else {
			value = mGson.fromJson(strJson, classOfT);
			return value;
		}
	}
}
