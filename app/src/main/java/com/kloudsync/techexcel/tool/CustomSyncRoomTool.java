package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;

import com.kloudsync.techexcel.config.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomSyncRoomTool {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static CustomSyncRoomTool customSyncRoomTool;

    public synchronized static CustomSyncRoomTool getInstance(Context context) {
        if (customSyncRoomTool == null) {
            synchronized (CustomSyncRoomTool.class) {
                if (customSyncRoomTool == null) {
                    customSyncRoomTool = new CustomSyncRoomTool(context);
                }
            }
        }
        return customSyncRoomTool;
    }


    public CustomSyncRoomTool(Context context) {
        sharedPreferences = context.getSharedPreferences(AppConfig.LOGININFO,
                context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setCustomSyncRoomContent(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray == null || jsonArray.length() == 0) {
                editor.putString("customyinxiang", "音想房间");
                editor.putString("customyinxiangen", "SyncRoom");
                editor.putString("customteammember", "团队成员");
                editor.putString("customteammemberen", "Team member");
                editor.putString("customcustomer", "用户");
                editor.putString("customcustomeren", "Customer");
                editor.commit();
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    int field = jsonObject1.getInt("fieldId");
                    String fieldName = jsonObject1.getString("fieldName");
                    switch (field) {
                        case 1001:
                            if (jsonObject1.getInt("languageId") == 0) {  //中文
                                editor.putString("customyinxiang", fieldName);
                            } else {
                                editor.putString("customyinxiangen", fieldName);
                            }
                            editor.commit();
                            break;
                        case 1002:
                            if (jsonObject1.getInt("languageId") == 0) {
                                editor.putString("customteammember", fieldName);
                            } else {
                                editor.putString("customteammemberen", fieldName);
                            }
                            editor.commit();
                            break;
                        case 1003:
                            if (jsonObject1.getInt("languageId") == 0) {
                                editor.putString("customcustomer", fieldName);
                            } else {
                                editor.putString("customcustomeren", fieldName);
                            }
                            editor.commit();
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (JSONException e) {
            editor.putString("customyinxiang", "音想房间");
            editor.putString("customyinxiangen", "SyncRoom");
            editor.putString("customteammember", "团队成员");
            editor.putString("customteammemberen", "Team member");
            editor.putString("customcustomer", "用户");
            editor.putString("customcustomeren", "Customer");
            editor.commit();
            e.printStackTrace();
        }
    }


    public String getCustomyinxiang() {
        if (AppConfig.LANGUAGEID == 1) {
            return sharedPreferences.getString("customyinxiangen", "SyncRoom");
        } else if (AppConfig.LANGUAGEID == 2) {
            return sharedPreferences.getString("customyinxiang", "音想房间");
        }
        return "";
    }

    public String getCustomteammember() {
        if (AppConfig.LANGUAGEID == 1) {
            return sharedPreferences.getString("customteammemberen", "Team member");
        } else if (AppConfig.LANGUAGEID == 2) {
            return sharedPreferences.getString("customteammember", "团队成员");
        }
        return "";
    }

    public String getCustomcustomer() {
        if (AppConfig.LANGUAGEID == 1) {
            return sharedPreferences.getString("customcustomeren", "Customer");
        } else if (AppConfig.LANGUAGEID == 2) {
            return sharedPreferences.getString("customcustomer", "用户");
        }
        return "";
    }


    public String getCustomyinxiang1() {
        return sharedPreferences.getString("customyinxiangen", "SyncRoom");
    }

    public String getCustomyinxiang2() {
        return sharedPreferences.getString("customyinxiang", "音想房间");
    }

    public String getCustomteammember1() {
        return sharedPreferences.getString("customteammemberen", "Team member");
    }

    public String getCustomteammember2() {
        return sharedPreferences.getString("customteammember", "团队成员");
    }

    public String getCustomcustomer1() {
        return sharedPreferences.getString("customcustomeren", "Customer");
    }

    public String getCustomcustomer2() {
        return sharedPreferences.getString("customcustomer", "用户");
    }


}
