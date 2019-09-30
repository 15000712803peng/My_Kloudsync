package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.kloudsync.techexcel.R;

public class NewPublicLessonActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_public_lesson);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
