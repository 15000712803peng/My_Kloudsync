package com.ub.techexcel.tools;

import android.util.Log;

import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/8/3.
 */

public class ServiceTool implements Runnable {

    private List<ServiceBean> mList = new ArrayList<>();
    private int roleId;
    private int type;
    private String keyWord;


    public ServiceTool(int i, List<ServiceBean> mList,String keyWord) {
        this.mList = mList;
        this.keyWord=keyWord;

        switch (i) {
            case 0:
                roleId = 3;
                type = 1;
                break;
            case 1:
                roleId = 3;
                type = 1;
                break;
            case 2:
                roleId = 3;
                type = 3;
                break;
        }
    }


    @Override
    public void run() {
        String url="";
        if (keyWord == null || keyWord.equals("")) {
            url = AppConfig.URL_PUBLIC
                    + "Lesson/List?roleID=3&isPublish=1&pageIndex=0&pageSize=20&type=" + type;
        } else {
            try {
                url = AppConfig.URL_PUBLIC
                        + "Lesson/List?roleID=3&isPublish=1&pageIndex=0&pageSize=20&type=" + type + "&keyword=" + URLEncoder.encode(LoginGet.getBase64Password(keyWord), "UTF-8") ;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        JSONObject returnJson = ConnectService
                .getIncidentbyHttpGet(url);
        Log.e("Lesson/List", url + "  " + returnJson.toString());
        formatServiceData(returnJson);

    }

    private void formatServiceData(JSONObject returnJson) {
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = returnJson.getJSONArray("RetData");
                    for (int i = 0; i < retdata.length(); i++) {
                        JSONObject service = retdata.getJSONObject(i);
                        ServiceBean bean = new ServiceBean();
                        int statusID = service.getInt("Status");
                        bean.setStatusID(statusID);
                        bean.setId(service.getInt("LessonID"));
                        bean.setRoleinlesson(service.getInt("Role"));
                        bean.setPlanedEndDate(service.getString("PlanedEndDate"));
                        bean.setPlanedStartDate(service.getString("PlanedStartDate"));
                        bean.setCourseName(service.getString("CourseName"));
                        bean.setUserName(service.getString("StudentNames"));
                        bean.setName(service.getString("Title"));
                        bean.setTeacherName(service.getString("TeacherNames"));
                        bean.setFinished(service.getInt("IsFinished") == 1 ? true : false);
                        bean.setStudentCount(service.getInt("StudentCount"));
                        mList.add(bean);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
