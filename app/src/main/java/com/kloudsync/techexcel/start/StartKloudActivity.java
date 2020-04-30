package com.kloudsync.techexcel.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class StartKloudActivity extends Activity implements View.OnClickListener {

    private TextView logintv;
    private TextView registertv;
    private TextView joinmeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startkloud);
        initView();
    }

    private void initView(){
        logintv=findViewById(R.id.logintv);
        logintv.setOnClickListener(this);
        registertv=findViewById(R.id.registertv);
        registertv.setOnClickListener(this);
        joinmeeting=findViewById(R.id.joinmeeting);
        joinmeeting.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logintv:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.registertv:
                Intent reintent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(reintent);
                break;
            case R.id.joinmeeting:
                Intent joinmeetingintent = new Intent(getApplicationContext(), JoinMeetingActivity.class);
                startActivity(joinmeetingintent);
                break;
        }
    }
}
