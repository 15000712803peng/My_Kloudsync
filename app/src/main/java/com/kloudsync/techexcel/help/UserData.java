package com.kloudsync.techexcel.help;

import android.content.Context;
import android.text.TextUtils;

import com.kloudsync.techexcel.config.AppConfig;

/**
 * Created by tonyan on 2019/11/18.
 */

public class UserData {

    public static String getUserToken(Context context){
        String userToken = AppConfig.UserToken;
        if(TextUtils.isEmpty(userToken)){
            userToken = context.getSharedPreferences(AppConfig.LOGININFO,
                    Context.MODE_PRIVATE).getString("UserToken","");
        }
        return userToken;
    }
}
