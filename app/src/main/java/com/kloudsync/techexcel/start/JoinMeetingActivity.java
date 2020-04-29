package com.kloudsync.techexcel.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

public class JoinMeetingActivity extends Activity implements View.OnClickListener {

    private TextView joinmeeting;
    private EditText meetingid;
    private EditText meetingname;
    private ImageView arrowback;
    private TextView meetingidhead,meetingnamehead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinmeeting);
        initView();
    }

    private void initView() {
        meetingid = findViewById(R.id.meetingid);
        meetingname = findViewById(R.id.meetingname);
        joinmeeting = findViewById(R.id.joinmeeting);
        meetingidhead = findViewById(R.id.meetingidhead);
        meetingnamehead = findViewById(R.id.meetingnamehead);
        joinmeeting.setOnClickListener(this);
        arrowback = findViewById(R.id.arrowback);
        arrowback.setOnClickListener(this);
        meetingid.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("输入过程中执行该方法", "文字变化");
                if (!TextUtils.isEmpty(s)&&s.length()>0){
                    meetingidhead.setVisibility(View.VISIBLE);
                }else{
                    meetingidhead.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                Log.e("输入前确认执行该方法", "开始输入");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("输入结束执行该方法", "输入结束");
            }
        });
        meetingname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)&&s.length()>0){
                    meetingnamehead.setVisibility(View.VISIBLE);
                }else{
                    meetingnamehead.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.joinmeeting:
                joinmeeeting();
                break;
            case R.id.arrowback:
                finish();
                break;
        }
    }

    private void joinmeeeting() {

        String meetingId=meetingid.getText().toString();
        String username=meetingname.getText().toString();

        String url= AppConfig.URL_PUBLIC+"User/CreateOrUpdateInstantAccout";
        ServiceInterfaceTools.getinstance().createOrUpdateInstantAccout(url, ServiceInterfaceTools.CREATEORUPDATEINSTANTACCOUT, username, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {


            }
        });

    }
}
