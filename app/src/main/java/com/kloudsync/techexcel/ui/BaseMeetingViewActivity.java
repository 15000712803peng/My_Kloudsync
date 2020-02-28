package com.kloudsync.techexcel.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.dialog.loading.KloudLoadingView;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.ub.techexcel.bean.AgoraMember;

import org.greenrobot.eventbus.EventBus;
import org.xwalk.core.XWalkView;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseMeetingViewActivity extends FragmentActivity {

    @Bind(R.id.layout_enter_loading)
    protected LinearLayout enterLoading;

    @Bind(R.id.enter_loading)
    protected KloudLoadingView loadingView;

    @Bind(R.id.web)
    protected XWalkView web;


    @Bind(R.id.layout_note)
    RelativeLayout noteLayout;

    @Bind(R.id.layout_note_users)
    LinearLayout noteUsersLayout;

    @Bind(R.id.layout_vedio)
    RelativeLayout vedioLayout;

    @Bind(R.id.image_vedio_close)
    ImageView closeVedioImage;



    @Bind(R.id.web_note)
    protected XWalkView noteWeb;

    @Bind(R.id.menu)
    protected ImageView menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("BaseMeetingViewActivity", "on_create");
        setContentView(R.layout.activity_view_meeting);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        showEnterLoading();
        initData();
    }

    public void showEnterLoading() {
        enterLoading.setVisibility(View.VISIBLE);
        loadingView.smoothToShow();
    }

    public void hideEnterLoading() {
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
