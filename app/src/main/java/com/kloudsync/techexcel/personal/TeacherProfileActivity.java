package com.kloudsync.techexcel.personal;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class TeacherProfileActivity extends SwipeBackActivity {

    private TextView tv_back;
    private TextView tv_title;
    public static final String 汗 = "Σ( ° △ °|||)︴";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        findView();
        initView();
    }

    private void findView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ViewCompat.setTransitionName(tv_title, 汗);
    }

    private void initView() {
        tv_back.setOnClickListener(new myOnClick());
    }

    protected class myOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    finish();
//                    ActivityCompat.finishAfterTransition(TeacherProfileActivity.this);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
