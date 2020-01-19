package com.kloudsync.techexcel.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tonyan on 2020/1/16.
 */

public class DigitalPensActivity extends Activity{

    @Bind(R.id.tv_title)
    TextView titleText;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_pen);
        ButterKnife.bind(this);
        titleText.setText(R.string.title_select_digital_pen);
    }
}
