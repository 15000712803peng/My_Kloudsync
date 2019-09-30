package com.kloudsync.techexcel.pc.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.kloudsync.techexcel.R;

public class AboutActivity extends AppCompatActivity {

    private ImageView img_notice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        img_notice = (ImageView) findViewById(R.id.img_notice);
        img_notice.setOnClickListener(new MyOnClick());
    }

    protected class MyOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_notice:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
