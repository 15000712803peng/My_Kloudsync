package com.kloudsync.techexcel.contact;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by pingfan on 2018/3/30.
 */

public class PurchasedCoursesActivity extends SwipeBackActivity {
    private TextView tv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new MyonClick());
    }

    protected class MyonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_back:
                    finish();
                    break;
            }
        }
    }
}
