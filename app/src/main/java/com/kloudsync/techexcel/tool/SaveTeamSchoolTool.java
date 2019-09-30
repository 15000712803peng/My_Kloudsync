package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

public class SaveTeamSchoolTool {

    public Context mContext;

    private SharedPreferences sharedPreferences;

    public void SaveToServe(Context mConteaxt) {
        this.mContext = mConteaxt;
        UpdateAUU();
    }

    private void UpdateAUU() {
        sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO,
                mContext.MODE_PRIVATE);
        AUUserInfo();
    }

    private void AUUserInfo() {

        final JSONObject jsonObject = format();
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "User/AddOrUpdateUserPreference", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddOrUpdateUserPreference;
                        msg.obj = responsedata.toString();
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
//                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FieldID", 10001);
//            jsonObject.put("PreferenceValue", 0);
            jsonObject.put("PreferenceText", format2() + "");
//            jsonObject.put("PreferenceMemo", "");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    private JSONObject format2() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("TeamID", sharedPreferences.getInt("SchoolID", -1));
            jsonObject.put("TeamName", sharedPreferences.getString("SchoolName", null));
            jsonObject.put("SchoolID", sharedPreferences.getInt("SchoolID", -1));
            jsonObject.put("SchoolName", sharedPreferences.getString("SchoolName", null));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }
}
