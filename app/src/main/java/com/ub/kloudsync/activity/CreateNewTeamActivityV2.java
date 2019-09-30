package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

import org.greenrobot.eventbus.EventBus;

public class CreateNewTeamActivityV2 extends Activity implements View.OnClickListener {

    private RelativeLayout backLayout;
    private EditText inputname;
    private TextView createbtn;
    private TextView tv_title;
    private TextView titleText;
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
        setContentView(R.layout.activity_create_new_team);
//        isSync = getIntent().getBooleanExtra("isSync", false);
        initView();
    }

    private void initView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        inputname = (EditText) findViewById(R.id.inputname);
        createbtn = (TextView) findViewById(R.id.createbtn);
        createbtn.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.Create_team));
        createbtn.setText(getResources().getString(R.string.Create_team));
        titleText = findViewById(R.id.tv_title);
        titleText.setText("Create New Team");
//        if(isSync){
//            teamSpaceBean = new TeamSpaceBean();
//            teamtypecontent.setText("I don't link to document team");
//            teamSpaceBean.setItemID(-1);
//        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.createbtn:
                createNew();
                break;

        }
    }


    private void createNew() {
        if (TextUtils.isEmpty(inputname.getText().toString().trim())) {
            Toast.makeText(this, "please input team name first", Toast.LENGTH_SHORT).show();
            return;
        }
        TeamSpaceInterfaceTools.getinstance().createTeamSpace(AppConfig.URL_PUBLIC + "TeamSpace/CreateTeamSpace", TeamSpaceInterfaceTools.CREATETEAMSPACE,
                AppConfig.SchoolID, 1, inputname.getText().toString().trim(), 0, 0, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        createSuccess();
                    }
                }
        );
    }

    private void createSuccess() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new TeamSpaceBean());
                Toast.makeText(getApplicationContext(), "create successful", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });

    }


}
