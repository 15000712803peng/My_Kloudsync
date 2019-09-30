package com.kloudsync.techexcel.personal;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateOrganizationActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout backLayout;
    private EditText et_name;
    private TextView tv_submit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView titleText;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.CreateOrganization:
                    String result = (String) msg.obj;
                    COjson(result);
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_submit.setEnabled(true);
                        }
                    }, 1000);
                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    private void COjson(String result) {
        JSONObject obj = null;
        Toast.makeText(this, R.string.create_success, Toast.LENGTH_SHORT).show();
        try {

            obj = new JSONObject(result);
            int RetData = obj.getInt("RetData");
            sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                    MODE_PRIVATE);
            editor = sharedPreferences.edit();
            AppConfig.SchoolID = RetData;
            editor.putString("SchoolID", RetData + "");
            editor.putInt("teamid", -1);
            editor.putString("SchoolName",et_name.getText().toString());
            editor.putString("teamname","");
            editor.commit();
            EventBus.getDefault().post(new TeamSpaceBean());
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);
        initView();

    }

    private void initView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        et_name= (EditText) findViewById(R.id.et_name);
        tv_submit= (TextView) findViewById(R.id.tv_submit);
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText(R.string.create_organization);
        backLayout.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.tv_submit:
                tv_submit.setEnabled(false);
                SubmitNewOrganizaion();
                break;
            default:
                break;
        }
    }


    Runnable enableTextTask = new Runnable() {
        @Override
        public void run() {
            tv_submit.setEnabled(true);
        }
    };
    private void SubmitNewOrganizaion() {

        String name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Organization name can`t be empty", Toast.LENGTH_SHORT).show();
            tv_submit.setEnabled(true);
            return;
        }
        handler.removeCallbacks(enableTextTask);
        handler.postDelayed(enableTextTask, 3000);
        final JSONObject jsonObject = format();
        Log.e("-------", "create");
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "School/CreateSchool", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.CreateOrganization;
                        msg.obj = responsedata.toString();
                    }else{
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
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
            jsonObject.put("SchoolName", et_name.getText().toString().trim());
            jsonObject.put("Category1", 2);
            jsonObject.put("Category2", 0);
            jsonObject.put("OwnerID", AppConfig.UserID);
            jsonObject.put("AdminID", AppConfig.UserID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }
}
