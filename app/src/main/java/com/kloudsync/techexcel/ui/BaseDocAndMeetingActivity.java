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
import android.webkit.WebView;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseDocAndMeetingActivity extends FragmentActivity {

    @Bind(R.id.layout_enter_loading)
    protected LinearLayout enterLoading;

    @Bind(R.id.enter_loading)
    protected KloudLoadingView loadingView;

    @Bind(R.id.web)
    protected WebView web;

    @Bind(R.id.web_note)
    protected WebView noteWeb;

    @Bind(R.id.menu)
    protected ImageView menuIcon;

    @Bind(R.id.layout_note_users)
    LinearLayout noteUsersLayout;

    @Bind(R.id.single_layout_item_full_screen)
    RelativeLayout itemFullScreenLayout;

    @Bind(R.id.layout_fullsceen_vedio)
    FrameLayout fullScreenVedio;

    @Bind(R.id.single_txt_name)
    TextView singleTextName;

    @Bind(R.id.icon_back_single_full_screen)
    ImageView singleFullScreenImage;

    @Bind(R.id.single_image_audio_status)
    ImageView singleAudioStatusImage;

    @Bind(R.id.single_image_vedio_status)
    ImageView singleVedioStatusImage;

    @Bind(R.id.single_member_icon)
    ImageView singleMemberIcon;

    protected AgoraMember currentAgoraMember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("BaseDocAndMeetingActivity", "on_create");
        setContentView(R.layout.activity_doc_and_meeting);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        showEnterLoading();
        initData();
//        Toast.makeText(this,"on_create",Toast.LENGTH_SHORT).show();
        singleFullScreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAgoraFull();
            }
        });
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

    protected void hideAgoraFull() {
        fullScreenVedio.removeAllViews();
        itemFullScreenLayout.setVisibility(View.GONE);

    }

    protected void showAgoraFull(AgoraMember agoraMember) {
        currentAgoraMember = agoraMember;
        itemFullScreenLayout.setVisibility(View.VISIBLE);
        SurfaceView target = agoraMember.getSurfaceView();
        if (!agoraMember.isMuteVideo() && target != null) {
            Log.e("showAgoraFull", "show");
            target.setVisibility(View.VISIBLE);
            stripSurfaceView(target);
            fullScreenVedio.addView(target, 0, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            Log.e("showAgoraFull", "surface_view_gone");
            stripSurfaceView(target);
            target.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(agoraMember.getUserName())) {
            singleTextName.setVisibility(View.GONE);
            singleTextName.setText("");
        } else {
            singleTextName.setVisibility(View.VISIBLE);
            singleTextName.setText(agoraMember.getUserName());
        }

        if (agoraMember.isMuteAudio()) {
            singleAudioStatusImage.setImageResource(R.drawable.icon_command_mic_disable);
        } else {
            singleAudioStatusImage.setImageResource(R.drawable.icon_command_mic_enabel);
        }
//
        if (TextUtils.isEmpty(agoraMember.getIconUrl())) {
            singleMemberIcon.setImageResource(R.drawable.hello);
        } else {
            new ImageLoader(this).DisplayImage(agoraMember.getIconUrl(), singleMemberIcon);
        }

        if (agoraMember.isMuteVideo()) {
            singleMemberIcon.setVisibility(View.VISIBLE);
            singleVedioStatusImage.setImageResource(R.drawable.icon_command_webcam_disable);

        } else {
            singleMemberIcon.setVisibility(View.GONE);
            singleVedioStatusImage.setImageResource(R.drawable.icon_command_webcam_enable);
        }
    }

    protected final void stripSurfaceView(SurfaceView view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }
}
