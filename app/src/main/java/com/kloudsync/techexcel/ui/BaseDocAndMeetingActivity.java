package com.kloudsync.techexcel.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.dialog.loading.KloudLoadingView;

import org.greenrobot.eventbus.EventBus;
import org.xwalk.core.XWalkView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tonyan on 2019/11/19.
 */

public abstract class BaseDocAndMeetingActivity extends FragmentActivity{

    @Bind(R.id.layout_enter_loading)
    protected LinearLayout enterLoading;

    @Bind(R.id.enter_loading)
    protected KloudLoadingView loadingView;

    @Bind(R.id.web)
    protected XWalkView web;

    @Bind(R.id.web_note)
    protected XWalkView noteWeb;

    @Bind(R.id.menu)
    protected ImageView menuIcon;

    @Bind(R.id.layout_note_users)
    LinearLayout noteUsersLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("BaseDocAndMeetingActivity","on_create");
        setContentView(R.layout.activity_doc_and_meeting);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        showEnterLoading();
        initData();
    }

    public void showEnterLoading(){
        enterLoading.setVisibility(View.VISIBLE);
        loadingView.smoothToShow();
    }

    public  void hideEnterLoading(){
        loadingView.hide();
        enterLoading.setVisibility(View.GONE);
    }

    public abstract void showErrorPage();

    public abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
