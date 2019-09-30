package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;

import org.greenrobot.eventbus.EventBus;

public class SelectLessonTypeActivity extends Activity implements View.OnClickListener {


    private RelativeLayout select1, select2, select3;
    private ImageView image1, image2, image3;
    private LinearLayout backll;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lesson_type);
        initView();
    }

    private void initView() {
        select1 = (RelativeLayout) findViewById(R.id.select1);
        select2 = (RelativeLayout) findViewById(R.id.select2);
        select3 = (RelativeLayout) findViewById(R.id.select3);
        select1.setOnClickListener(this);
        select2.setOnClickListener(this);
        select3.setOnClickListener(this);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        setInvisible();

        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);
        type = getIntent().getIntExtra("lessontype", -1);
        switch (type) {
            case 2:
                image1.setVisibility(View.VISIBLE);
                break;
            case 0:
                image2.setVisibility(View.VISIBLE);
                break;
            case 1:
                image3.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select1:
                setInvisible();
                image1.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(2);
                finish();
                break;
            case R.id.select2:
                setInvisible();
                image2.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(0);
                finish();
                break;
            case R.id.select3:
                setInvisible();
                image3.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(1);
                finish();
                break;
            case R.id.backll:
                finish();
                break;
        }
    }

    private void setInvisible() {
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
    }


}
