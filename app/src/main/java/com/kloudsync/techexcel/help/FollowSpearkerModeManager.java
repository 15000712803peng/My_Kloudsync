package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.CompanySubsystem;
import com.kloudsync.techexcel.bean.EventExpanedUserList;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.PopSpeakerWindowMore;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.tool.FollowSpearkerTouchListener;
import com.kloudsync.techexcel.view.CircleImageView;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tonyan on 2020/4/18.
 */

public class FollowSpearkerModeManager implements View.OnClickListener, PopSpeakerWindowMore.OnSizeSettingChanged {

    private Context context;
    private RelativeLayout speakerLayout;
    private ImageView speakerMoreImage;
    SharedPreferences sharedPreferences;
    FollowSpearkerTouchListener spearkerTouchListener;
    private RelativeLayout speakerContainer;
    private ImageLoader imageLoader;
    private AgoraMember currentAgoraMember;
    private ImageView arrowExpanded;

    public AgoraMember getCurrentAgoraMember() {
        return currentAgoraMember;
    }

    public void setSpeakerContainer(RelativeLayout speakerContainer) {
        this.speakerContainer = speakerContainer;
        imageLoader = new ImageLoader(context);
    }

    public interface OnSpeakerAgoraStatusChanged {

        void onSpeakerAudioStatusChanged(int uid, boolean isMuted);

        void onSpeakerVideoStatusChanged(int uid, boolean isMuted);
    }

    public void changeSizeMode() {
        String sizeMode = sharedPreferences.getString("speaker_size_mode", "small");
        if (sizeMode.equals("small")) {
            sharedPreferences.edit().putString("speaker_size_mode", "big").commit();
            onBigSelected();
        } else if (sizeMode.equals("big")) {
            sharedPreferences.edit().putString("speaker_size_mode", "large").commit();
            onLargeSelected();
        } else if (sizeMode.equals("large")) {
            sharedPreferences.edit().putString("speaker_size_mode", "small").commit();
            onSmallSelected();
        }
    }

    public void initViews(Context context) {
        this.context = context;
        this.speakerLayout = speakerContainer.findViewById(R.id.layout_follow_speaker);
        sharedPreferences = context.getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        spearkerTouchListener = new FollowSpearkerTouchListener(context);
        spearkerTouchListener.setSpeakerLayout(speakerLayout);
        speakerLayout.setOnTouchListener(spearkerTouchListener);
        speakerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSizeMode();
            }
        });

        speakerMoreImage = speakerLayout.findViewById(R.id.image_speark_more);
        speakerMoreImage.setOnClickListener(this);
        vedioFrame = speakerLayout.findViewById(R.id.speaker_view_container);
        nameText = speakerLayout.findViewById(R.id.txt_name);
        audioStatusImage = speakerLayout.findViewById(R.id.image_audio_status);
        iconImage = speakerLayout.findViewById(R.id.member_icon);
        arrowExpanded = speakerLayout.findViewById(R.id.arrow_expand);
        arrowExpanded.setOnClickListener(this);
        if (currentAgoraMember != null) {
            fillViewByMember(currentAgoraMember);
        }
    }

    public void initViews(Context context, boolean init) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);

        if (init) {
            String sizeMode = sharedPreferences.getString("speaker_size_mode", "small");
            if (sizeMode.equals("small")) {
                onSmallSelected();
            } else if (sizeMode.equals("big")) {
                onBigSelected();
            } else if (sizeMode.equals("large")) {
                onLargeSelected();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_speark_more:
                showMorePopwindow();
                break;
            case R.id.arrow_expand:
                Log.e("check_post", "post_expanded");
                EventExpanedUserList expanedUserList = new EventExpanedUserList();
                EventBus.getDefault().post(expanedUserList);
                break;
        }
    }

    PopSpeakerWindowMore windowMorePop;

    private void showMorePopwindow() {
        if (windowMorePop != null) {
            if (windowMorePop.isShowing()) {
                windowMorePop.dismiss();
            }
            windowMorePop = null;
        }
        windowMorePop = new PopSpeakerWindowMore((Activity) context);
        windowMorePop.setOnSizeSettingChanged(this);
        windowMorePop.showAtBottom(speakerMoreImage);

    }

    @Override
    public void onSmallSelected() {

        Activity activity = (Activity) context;
        int width = 0, height = 0;
        speakerContainer.removeAllViews();
        width = context.getResources().getDimensionPixelSize(R.dimen.speaker_normal_width);
        height = context.getResources().getDimensionPixelSize(R.dimen.speaker_normal);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        if (spearkerTouchListener != null) {
            params.topMargin = spearkerTouchListener.getTop();
            params.leftMargin = spearkerTouchListener.getLeft();
        }
        RelativeLayout speakerLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.speaker_camera_small_item, null);
        speakerContainer.addView(speakerLayout, params);

        initViews(context);
    }

    @Override
    public void onBigSelected() {
        Activity activity = (Activity) context;
        int width = 0, height = 0;
        speakerContainer.removeAllViews();
        width = context.getResources().getDimensionPixelSize(R.dimen.speaker_big_width);
        height = context.getResources().getDimensionPixelSize(R.dimen.speaker_big);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        RelativeLayout speakerLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.speaker_camera_big_item, null);
        if (spearkerTouchListener != null) {
            params.topMargin = spearkerTouchListener.getTop();
            params.leftMargin = spearkerTouchListener.getLeft();
        }

        speakerContainer.addView(speakerLayout, params);
        initViews(context);
    }

    @Override
    public void onLargeSelected() {
        Activity activity = (Activity) context;
        int width = 0, height = 0;
        speakerContainer.removeAllViews();
        width = context.getResources().getDimensionPixelSize(R.dimen.speaker_large_wdth);
        height = context.getResources().getDimensionPixelSize(R.dimen.speaker_large);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        RelativeLayout speakerLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.speaker_camera_large_item, null);
        if (spearkerTouchListener != null) {
            params.topMargin = spearkerTouchListener.getTop();
            params.leftMargin = spearkerTouchListener.getLeft();
        }

        speakerContainer.addView(speakerLayout, params);
        initViews(context);

    }

    public void showHostView(AgoraMember agoraMember) {
        Log.e("check_speaker_show", "speaker:" + agoraMember);
        if (TextUtils.isEmpty(agoraMember.getUserName())) {
            Observable.just(agoraMember).observeOn(Schedulers.io()).doOnNext(new Consumer<AgoraMember>() {
                @Override
                public void accept(AgoraMember agoraMember) throws Exception {
                    JSONObject jsonObject = ServiceInterfaceTools.getinstance().syncGetUserDetail(agoraMember.getUserId() + "");
                    if (jsonObject.has("RetCode")) {
                        if (jsonObject.getInt("RetCode") == 0) {
                            JSONObject data = jsonObject.getJSONObject("RetData");
                            agoraMember.setUserName(data.optString("Name"));
                            agoraMember.setIconUrl(data.optString("AvatarUrl"));
                        }
                    }
                }
            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<AgoraMember>() {
                @Override
                public void accept(AgoraMember agoraMember) throws Exception {
                    currentAgoraMember = agoraMember;
                    fillViewByMember(agoraMember);
                }
            }).subscribe();

        } else {
            this.currentAgoraMember = agoraMember;
            fillViewByMember(agoraMember);
        }

//        if (meetingConfig != null && meetingConfig.getMeetingHostId().equals(agoraMember.getUserId() + "")) {
//
//        }

    }

    public void showSpeakerView(AgoraMember agoraMember) {

        Log.e("check_speaker_show", "speaker:" + agoraMember);
        agoraMember.setMuteAudio(false);
        this.currentAgoraMember = agoraMember;
        fillViewByMember(agoraMember);
//        if (meetingConfig != null && meetingConfig.getMeetingHostId().equals(agoraMember.getUserId() + "")) {
//
//        }
    }

    private void fillViewByMember(AgoraMember member) {

        Activity activity = (Activity) context;
        vedioFrame.removeAllViews();
        Log.e("fillViewByMember", "is_mute_video:" + member.isMuteVideo());
        if (!member.isMuteVideo()) {
            iconImage.setVisibility(View.INVISIBLE);
            View d = activity.getLayoutInflater().inflate(R.layout.framelayout_head, null);
            TextView videoname = (TextView) d.findViewById(R.id.videoname);
            ViewParent parent = videoname.getParent();
            if (parent != null) {
                ((RelativeLayout) parent).removeView(videoname);
            }

            vedioFrame.addView(videoname);
            SurfaceView target = member.getSurfaceView();
            if (target != null) {
                member.setSurfaceShowing(true);
                target.setVisibility(View.VISIBLE);
                stripSurfaceView(target);
                vedioFrame.addView(target, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            iconImage.setVisibility(View.VISIBLE);
            vedioFrame.removeAllViews();
        }

        if (!TextUtils.isEmpty(member.getUserName())) {
            nameText.setText(member.getUserName());
        } else {
            nameText.setText("");
        }

        if (member.isMuteAudio()) {
            audioStatusImage.setImageResource(R.drawable.microphone);
        } else {
            audioStatusImage.setImageResource(R.drawable.icon_command_mic_enabel);
        }

        if (TextUtils.isEmpty(member.getIconUrl())) {
            iconImage.setImageResource(R.drawable.hello);
        } else {
            imageLoader.DisplayImage(member.getIconUrl(), iconImage);
        }

    }

    protected final void stripSurfaceView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    private FrameLayout vedioFrame;
    private TextView nameText;
    private ImageView audioStatusImage;
    private CircleImageView iconImage;

    public void onSpeakerAudioChanged(boolean isMuted) {
        if (this.currentAgoraMember != null) {
            this.currentAgoraMember.setMuteAudio(isMuted);
            if (currentAgoraMember.isMuteAudio()) {
                audioStatusImage.setImageResource(R.drawable.icon_command_mic_disable);
            } else {
                audioStatusImage.setImageResource(R.drawable.icon_command_mic_enabel);
                currentAgoraMember.setHaveShowUnMuteAudioImage(true);
            }
        }
    }

    public void onSpeakerVideoChanged(boolean isMuted, SurfaceView surfaceView) {
        if (this.currentAgoraMember != null) {
            this.currentAgoraMember.setMuteVideo(isMuted);
            this.currentAgoraMember.setSurfaceView(surfaceView);
            fillViewByMember(currentAgoraMember);
        }
    }

    public void hideArrowExpanded() {
        if (arrowExpanded != null) {
            arrowExpanded.setVisibility(View.INVISIBLE);
        }
    }

    public void showArrowExpanded() {
        if (arrowExpanded != null) {
            arrowExpanded.setVisibility(View.VISIBLE);
        }
    }
}
