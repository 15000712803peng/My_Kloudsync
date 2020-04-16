package com.ub.service.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.ui.MeetingViewActivity;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceDetailActivity extends Activity implements OnClickListener {
    private TextView back;
    private TextView studentname, tuturname;

    private int itemId;
    private ServiceBean bean = new ServiceBean();
    public static ServiceDetailActivity serviceDetailActivityInstabce;
    private int conversationtype;
    private String mTargetId;
    private LinearLayout opencourse, startcourse, endcourse;
    private TextView startvalue;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    if (bean.getRoleinlesson() == 2) {
                        startvalue.setText(getResources().getString(R.string.start));
                    } else {
                        startvalue.setText(getResources().getString(R.string.join));
                    }
                    studentname.setText(bean.getUserName());
                    tuturname.setText(bean.getTeacherName());
                    break;
                case AppConfig.CONFIRM_SERVICE:
                    Intent ii = new Intent();
                    ii.setAction("com.ubao.techexcel.frgment");
                    sendBroadcast(ii);
                    finish();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicedetail);

        serviceDetailActivityInstabce = this;
        itemId = getIntent().getIntExtra("id", 0);
        conversationtype = getIntent().getIntExtra("conversationtype", 1);
        mTargetId = getIntent().getStringExtra("mTargetId");
        initView();
        getServiceDetail();

        if (getIntent().getBooleanExtra("ismodifyservice", false)) {
            dialog(ServiceDetailActivity.this);
        }
    }

    private AlertDialog dialog2;

    public void dialog(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View windov = inflater.inflate(R.layout.servicrmodify_dialog, null);
        windov.findViewById(R.id.smsinvate).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog2.dismiss();
                    }
                });
        dialog2 = new AlertDialog.Builder(context).show();
        Window dialogWindow = dialog2.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(p);
        dialog2.setCanceledOnTouchOutside(true);
        dialog2.setContentView(windov);

    }

    private void getServiceDetail() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnjson = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Lesson/Item?lessonID=" + itemId);
                formatServiceData(returnjson);
            }
        }).start(((App) getApplication()).getThreadMgr());
    }


    private void formatServiceData(JSONObject returnJson) {
        Log.e("returnJson", returnJson.toString());
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONObject service = returnJson.getJSONObject("RetData");
                    bean = new ServiceBean();
                    bean.setId(service.getInt("LessonID"));
                    String des = service.getString("Description");
                    bean.setDescription(des);
                    int statusID = service.getInt("StatusID");
                    bean.setStatusID(statusID);
                    bean.setRoleinlesson(service.getInt("RoleInLesson"));
                    JSONArray memberlist = service.getJSONArray("MemberInfoList");
                    for (int i = 0; i < memberlist.length(); i++) {
                        JSONObject jsonObject = memberlist.getJSONObject(i);
                        int role = jsonObject.getInt("Role");
                        if (role == 2) { //teacher
                            bean.setTeacherName(jsonObject.getString("MemberName"));
                            bean.setTeacherId(jsonObject.getString("MemberID"));
                        } else if (role == 1) {
                            bean.setUserName(jsonObject.getString("MemberName"));
                            bean.setUserId(jsonObject.getString("MemberID"));
                        }
                    }
                    handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        studentname = (TextView) findViewById(R.id.studentname);
        tuturname = (TextView) findViewById(R.id.tuturname);

        opencourse = (LinearLayout) findViewById(R.id.opencourse);
        opencourse.setOnClickListener(this);
        startcourse = (LinearLayout) findViewById(R.id.startcourse);
        endcourse = (LinearLayout) findViewById(R.id.endcourse);
        startcourse.setOnClickListener(this);
        endcourse.setOnClickListener(this);
        startvalue = (TextView) findViewById(R.id.startvalue);

    }


    private Intent intent;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                if (AppConfig.ISMODIFY_SERVICE) {
                    AppConfig.ISMODIFY_SERVICE = false;
                    finish();
                    Intent ii = new Intent();
                    ii.setAction("com.ubao.techexcel.frgment");
                    sendBroadcast(ii);
                } else {
                    finish();
                    if (AppConfig.isNewService) {
                        AppConfig.isNewService = false;
                    }
                }
                break;
            case R.id.opencourse:
                intent = new Intent(ServiceDetailActivity.this, MeetingViewActivity.class);
                intent.putExtra("userid", bean.getUserId());
                intent.putExtra("meetingId", bean.getId() + "");
                intent.putExtra("teacherid", bean.getTeacherId());
                intent.putExtra("identity", bean.getRoleinlesson());
                intent.putExtra("isStartCourse", true);
                intent.putExtra("isPrepare", true);
                intent.putExtra("isInstantMeeting", 0);
                startActivity(intent);
                break;
            case R.id.startcourse:
                intent = new Intent(ServiceDetailActivity.this, MeetingViewActivity.class);
                intent.putExtra("userid", bean.getUserId());
                intent.putExtra("meetingId", bean.getId() + "");
                intent.putExtra("teacherid", bean.getTeacherId());
                intent.putExtra("identity", bean.getRoleinlesson());
                intent.putExtra("isStartCourse", true);
                intent.putExtra("isInstantMeeting", 0);
                startActivity(intent);
                break;
            case R.id.endcourse:
                confirmFinish(bean);
                break;
            default:
                break;
        }

    }

    /**
     * 确认结束课程
     */
    private AlertDialog dialog;

    private void confirmFinish(final ServiceBean serviceBean) {
        final LayoutInflater inflater = LayoutInflater
                .from(ServiceDetailActivity.this);
        View windov = inflater.inflate(R.layout.confirmservice, null);
        windov.findViewById(R.id.no).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        windov.findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new ApiTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject submitjson = new JSONObject();
                            submitjson.put("ID", serviceBean.getId());
                            submitjson.put("StatusID", 1);
                            JSONObject jsonObject = ConnectService
                                    .submitDataByJson(AppConfig.URL_PUBLIC
                                            + "/Lesson/Forward", submitjson);
                            Log.e("returnJson", jsonObject.toString());
                            if (jsonObject.getInt("RetCode") == 0) {
                                handler.obtainMessage(AppConfig.CONFIRM_SERVICE)
                                        .sendToTarget();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start(((App) getApplication()).getThreadMgr());
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(this).show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(p);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(windov);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (AppConfig.ISMODIFY_SERVICE) {
                AppConfig.ISMODIFY_SERVICE = false;
                finish();
                AppConfig.ISONRESUME = true;
            } else {
                finish();
                if (AppConfig.isNewService) {
                    AppConfig.isNewService = false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
