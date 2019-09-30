package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateNewSpaceActivity extends Activity implements View.OnClickListener {

    private ImageView back;
    private EditText inputname;
    private TextView createbtn;
    private int teamid = 0;
    private TextView tv_title;
    private TextView teamtypecontent;

    private boolean isSync;
    private TeamSpaceBean teamSpaceBean;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.CreateTeamTopic:
                    String result = (String) msg.obj;
                    finish();
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnewspace);
        teamid = getIntent().getIntExtra("ItemID", 0);
        isSync = getIntent().getBooleanExtra("isSync", false);
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        inputname = (EditText) findViewById(R.id.inputname);
        createbtn = (TextView) findViewById(R.id.createbtn);
        createbtn.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        teamtypecontent = (TextView) findViewById(R.id.teamtypecontent);
        tv_title.setText(getResources().getString(R.string.Create_Space));
        createbtn.setText(getResources().getString(R.string.Create_Space));
        if (isSync) {
            teamSpaceBean = new TeamSpaceBean();
            teamtypecontent.setText("I don't link to document team");
            teamSpaceBean.setItemID(-1);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.createbtn:
                CreateNew();
                break;

        }
    }


    private void CreateNew() {
        if (isSync) {
            CreateTeamTopic();
        } else {
            TeamSpaceInterfaceTools.getinstance().createTeamSpace(AppConfig.URL_PUBLIC + "TeamSpace/CreateTeamSpace", TeamSpaceInterfaceTools.CREATETEAMSPACE,
                    AppConfig.SchoolID, 2, inputname.getText().toString(), teamid, 0, new TeamSpaceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            EventBus.getDefault().post(new TeamSpaceBean());
                            finish();
                        }
                    }
            );
        }
    }

    private void CreateTeamTopic() {
        final JSONObject jsonObject = format();
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "Topic/CreateTeamTopic", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.CreateTeamTopic;
                        msg.obj = responsedata.toString();
                    } else {
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
            jsonObject.put("LinkedDocTeamID", teamSpaceBean.getItemID());
            jsonObject.put("ParentID", 0);
            jsonObject.put("Name", inputname.getText().toString());
            jsonObject.put("Type", 1);
            jsonObject.put("CompanyID", AppConfig.SchoolID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }


}
