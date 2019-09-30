package com.kloudsync.techexcel.app;

import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class BaseActivity extends ActivityWrapper {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        initView();
    }

    protected abstract int setLayout();

    protected abstract void initView();

}
