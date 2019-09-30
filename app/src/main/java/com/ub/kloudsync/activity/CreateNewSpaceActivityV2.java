package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

import org.greenrobot.eventbus.EventBus;

public class CreateNewSpaceActivityV2 extends Activity implements View.OnClickListener {

    private ImageView back;
    private EditText inputname;
    private TextView createbtn;
    private int teamid = 0;
    private TextView tv_title;
    RelativeLayout backLayout;
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
        setContentView(R.layout.activity_create_new_space);
        teamid = getIntent().getIntExtra("ItemID", 0);
        initView();
    }

    private void initView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        inputname = (EditText) findViewById(R.id.inputname);
        createbtn = (TextView) findViewById(R.id.createbtn);
        createbtn.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.Create_Space));
        createbtn.setText(getResources().getString(R.string.Create_Space));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.createbtn:
                CreateNew();
                break;

        }
    }

    private void CreateNew() {
        TeamSpaceInterfaceTools.getinstance().createTeamSpace(AppConfig.URL_PUBLIC + "TeamSpace/CreateTeamSpace", TeamSpaceInterfaceTools.CREATETEAMSPACE,
                AppConfig.SchoolID, 2, inputname.getText().toString().trim(), teamid, 0, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        EventBus.getDefault().post(new TeamSpaceBean());
                        setResult(RESULT_OK);
                        finish();
                    }
                }
        );
    }


}
