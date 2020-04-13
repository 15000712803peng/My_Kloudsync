package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingPauseOrResumBean;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.ub.techexcel.bean.EventMuteAll;
import com.ub.techexcel.bean.EventUnmuteAll;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.PopMeetingMore;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PopMeetingMenu implements PopupWindow.OnDismissListener, OnClickListener,PopMeetingMore.OnMoreActionsListener {

    int width;
    private Activity host;
    private MeetingConfig meetingConfig;
    private PopupWindow window;
    //--
    private ImageView menuIcon;
    private RelativeLayout menuEnd;
    private RelativeLayout menuInvite;
    private RelativeLayout menuMore;
    private RelativeLayout menuLeave;
    private ImageView microImage, voiceImage, cameraImage, switchCameraImage;

    //----
    private MeetingSettingCache settingCache;
	private RelativeLayout mMenuPause;
    private ImageView mIvPauseIcon;
    private TextView mTvPauseText;
    private boolean mIsMeetingPause;
    private InputMethodManager mImm;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_camera:

                if(meetingConfig.getMeetingStatus() == 0){
                    return;
                }

                if(meetingConfig.getRole() == MeetingConfig.MeetingRole.AUDIENCE){
                    Toast.makeText(host,"没有权限操作",Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isCameraOn = !getSettingCache(host).getMeetingSetting().isCameraOn();
                cameraImage.setImageResource(isCameraOn ? R.drawable.icon_command_webcam_enable :
                        R.drawable.icon_command_webcam_disable);
//                settingCache.setCameraOn(isCameraOn);
                if (operationsListener != null) {
                    operationsListener.menuCameraClicked(isCameraOn);
                }
                break;

            case R.id.meeting_camera_switch:

                if(meetingConfig.getMeetingStatus() == 0){
                    return;
                }

                if(meetingConfig.getRole() == MeetingConfig.MeetingRole.AUDIENCE){
                    Toast.makeText(host,"没有权限操作",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (operationsListener != null) {
                    operationsListener.menuSwitchCamera();
                }
                break;
            case R.id.meeting_menu_end:
                if(meetingConfig.getMeetingStatus() == 0){
                    return;
                }
                if(operationsListener != null){
                    operationsListener.menuEndClicked();
                }
                hide();
                break;
            case R.id.meeting_menu_leave:
                if(operationsListener != null){
                    operationsListener.menuLeaveClicked();
                }
                hide();

                break;
            case R.id.meeting_mic_enabel: {
                if(meetingConfig.getMeetingStatus() == 0){
                    return;
                }
                if(meetingConfig.getRole() == MeetingConfig.MeetingRole.AUDIENCE){
                    Toast.makeText(host,"没有权限操作",Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isMicroOn = !getSettingCache(host).getMeetingSetting().isMicroOn();
                microImage.setImageResource(isMicroOn ? R.drawable.icon_command_mic_enabel :
                        R.drawable.icon_command_mic_disable);
//                settingCache.setMicroOn(isMicroOn);
                if (operationsListener != null) {
                    operationsListener.menuMicroClicked(isMicroOn);
                }
            }

            break;

            case R.id.meeting_voice: {
                if(meetingConfig.getMeetingStatus() == 0){
                    return;
                }
                int voiceStatus = getSettingCache(host).getMeetingSetting().getVoiceStatus();
                Log.e("PopMeetingMenu", "voice_status:" + voiceStatus);
                if (voiceStatus == 0) {
                    //1
                    voiceImage.setImageResource(R.drawable.icon_ear_active);
                } else if (voiceStatus == 1) {
                    voiceImage.setImageResource(R.drawable.voiceallclose);
                } else if (voiceStatus == 2) {
                    voiceImage.setImageResource(R.drawable.icon_voice_active_1);

                }
                if (operationsListener != null) {
                    operationsListener.menuChangeVoiceStatus(voiceStatus);
                }
            }
            break;

            case R.id.meeting_menu_invite:
                if(meetingConfig.getMeetingStatus() == 0){
                    return;
                }
                if (operationsListener != null) {
                    operationsListener.menuInviteClicked();
                }
                hide();
                break;
	        case R.id.meeting_menu_pause:
                if (mImm.isActive()) mImm.hideSoftInputFromWindow(mMenuPause.getWindowToken(), 0);
                meetingPauseOrResume();
                hide();
                break;
            case R.id.meeting_menu_more:
                if(meetingConfig.getMeetingStatus() == 0){
                    return;
                }
                if (operationsListener != null) {
                    operationsListener.menuMoreClicked();
                }
                showMorePop();
                break;
        }
    }


    @Override
    public void userMuteAll() {
        microImage.setImageResource(R.drawable.icon_command_mic_disable);
        EventMuteAll eventMuteAll = new EventMuteAll();
        EventBus.getDefault().post(eventMuteAll);
    }

    @Override
    public void userUnmuteAll() {
        microImage.setImageResource(R.drawable.icon_command_mic_enabel);
        EventUnmuteAll eventUnmuteAll = new EventUnmuteAll();
        EventBus.getDefault().post(eventUnmuteAll);
    }

    public interface MeetingMenuOperationsListener {

        void menuEndClicked();

        void menuLeaveClicked();

        void menuCameraClicked(boolean isCameraOn);

        void menuSwitchCamera();

        void menuMicroClicked(boolean isMicroOn);

        void menuChangeVoiceStatus(int status);

        void menuInviteClicked();

        void menuMoreClicked();

    }

    private MeetingMenuOperationsListener operationsListener;

    public PopMeetingMenu(Activity host) {
        this.host = host;
        getPopupWindow();
    }


    public void getPopupWindow() {
        if (null != window) {
            window.dismiss();
            return;
        } else {
            init();
        }
    }


    public void init() {
        mImm = (InputMethodManager) host.getSystemService(Context.INPUT_METHOD_SERVICE);
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        View view = layoutInflater.inflate(R.layout.pop_meeting_menu, null);
        width = (int) (host.getResources().getDisplayMetrics().widthPixels);
        window = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);
        voiceImage = view.findViewById(R.id.meeting_voice);
        microImage = view.findViewById(R.id.meeting_mic_enabel);
        microImage.setOnClickListener(this);
        voiceImage.setOnClickListener(this);
        menuInvite = view.findViewById(R.id.meeting_menu_invite);
        menuInvite.setOnClickListener(this);
	    mMenuPause = view.findViewById(R.id.meeting_menu_pause);
        mIvPauseIcon = view.findViewById(R.id.iv_meeting_pause_icon);
        mTvPauseText = view.findViewById(R.id.tv_meeting_pause_text);
        mMenuPause.setOnClickListener(this);
        menuMore = view.findViewById(R.id.meeting_menu_more);
        menuMore.setOnClickListener(this);
        cameraImage = view.findViewById(R.id.meeting_camera);
        switchCameraImage = view.findViewById(R.id.meeting_camera_switch);
        switchCameraImage.setOnClickListener(this);
        menuEnd = view.findViewById(R.id.meeting_menu_end);
        menuEnd.setOnClickListener(this);
        menuLeave = view.findViewById(R.id.meeting_menu_leave);
        menuLeave.setOnClickListener(this);
        cameraImage.setOnClickListener(this);
        window.getWidth();
        window.getHeight();
        window.setFocusable(true);
        window.setOnDismissListener(this);
        // 设置允许在外点击消失
        window.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        window.setBackgroundDrawable(new BitmapDrawable());
    }

    public void show(Activity host, ImageView menu, MeetingConfig meetingConfig, MeetingMenuOperationsListener operationsListener, boolean isMeetingPause) {
        this.host = host;
        this.menuIcon = menu;
        this.meetingConfig = meetingConfig;
        this.operationsListener = operationsListener;
        mIsMeetingPause = isMeetingPause;
        setMeetingPauseOrResumeView();
        initBySetting();
        Log.e("PopMeetingMenu", "show_menu");
        window.showAtLocation(menu, Gravity.NO_GRAVITY,
                width - host.getResources().getDimensionPixelSize(R.dimen.meeting_menu_margin_left),
                host.getResources().getDimensionPixelSize(R.dimen.menu_top_margin));
    }

    /***
     * 设置会议暂停和继续,UI的展示
     */
    private void setMeetingPauseOrResumeView() {
        if (meetingConfig.getSystemType() == AppConfig.COMPANY_MODEL) {
            if (mIsMeetingPause) {
                setMeetingPauseIconAndText(R.drawable.playyinxiangplay, R.string.resume_meeting);
            } else {
                setMeetingPauseIconAndText(R.drawable.icon_command_pause, R.string.meeting_suspended);
            }
        } else {
            if (mIsMeetingPause) {
                setMeetingPauseIconAndText(R.drawable.playyinxiangplay, R.string.continue_class);
            } else {
                setMeetingPauseIconAndText(R.drawable.icon_command_pause, R.string.practice_in_class);
            }
        }
    }

    private void setMeetingPauseIconAndText(int resIconId, int resStrId) {
        mIvPauseIcon.setImageResource(resIconId);
        mTvPauseText.setText(resStrId);
    }

    public boolean isShowing() {
        if (window != null) {
            return window.isShowing();
        }
        return false;
    }

    public void hide() {
        if (window != null) {
            window.dismiss();
        }
        window = null;
    }

    @Override
    public void onDismiss() {
        Log.e("PopBottomMenu", "on_dismiss");
        if (menuIcon != null) {
            Log.e("PopBottomMenu", "on_dismiss_menu_icon");
//            menuIcon.setImageResource(R.drawable.icon_menu);
        }
        window = null;
    }

    public void onAudioRouteChanged(final int routing) {
        Log.e("onAudioRouteChanged", routing + "    语音路由 ");
//        Observable.just(routing).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                switch (integer) {
//                    case AUDIO_ROUTE_HEADSET:
//                        voiceImage.setEnabled(false);
//                        voiceImage.setImageResource(R.drawable.icon_headphone_active);
//                        break;
//                    case AUDIO_ROUTE_EARPIECE:
//                        voiceImage.setEnabled(true);
//                        voiceImage.setImageResource(R.drawable.icon_ear_active);
//                        break;
//                    case AUDIO_ROUTE_SPEAKERPHONE:
//                        voiceImage.setEnabled(true);
//                        voiceImage.setImageResource(R.drawable.icon_voice_active_1);
//                        break;
//                    case AUDIO_ROUTE_HEADSETBLUETOOTH:
//                        voiceImage.setEnabled(false);
//                        voiceImage.setImageResource(R.drawable.icon_headphone_active);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }).subscribe();

    }

    private void initBySetting() {
        cameraImage.setImageResource(getSettingCache(host).getMeetingSetting().isCameraOn() ? R.drawable.icon_command_webcam_enable :
                R.drawable.icon_command_webcam_disable);
        microImage.setImageResource(getSettingCache(host).getMeetingSetting().isMicroOn() ? R.drawable.icon_command_mic_enabel : R.drawable.icon_command_mic_disable);
        if (meetingConfig != null) {
            if (TextUtils.isEmpty(meetingConfig.getMeetingHostId()) || !meetingConfig.getMeetingHostId().equals(AppConfig.UserID)) {
                menuEnd.setVisibility(View.GONE);
            } else {
                menuEnd.setVisibility(View.VISIBLE);

            }
        }

        if(meetingConfig.getMeetingHostId().equals(AppConfig.UserID) || meetingConfig.getPresenterId().equals(AppConfig.UserID)){
            menuMore.setVisibility(View.VISIBLE);
        }else{
            menuMore.setVisibility(View.GONE);
        }
        if (/*meetingConfig.getMeetingHostId().equals(AppConfig.UserID) || */meetingConfig.getPresenterId().equals(AppConfig.UserID)) {
            mMenuPause.setVisibility(View.VISIBLE);
        } else {
            mMenuPause.setVisibility(View.GONE);
        }

        int voiceStatus = getSettingCache(host).getMeetingSetting().getVoiceStatus();
        Log.e("PopMeetingMenu", "voice_status:" + voiceStatus);
        if (voiceStatus == 0) {
            voiceImage.setImageResource(R.drawable.icon_voice_active_1);
        } else if (voiceStatus == 1) {
            voiceImage.setImageResource(R.drawable.icon_ear_active);
        } else if (voiceStatus == 2) {
            voiceImage.setImageResource(R.drawable.voiceallclose);
        }
        MeetingKit.getInstance().initVoice(voiceStatus);

    }

    private MeetingSettingCache getSettingCache(Activity host) {
        if (settingCache == null) {
            settingCache = MeetingSettingCache.getInstance(host);
        }
        return settingCache;
    }

    /**
     * 会议暂停或继续,发送暂停提示消息
     */
    private void meetingPauseOrResume() {
        Observable.just("meeting_pause").observeOn(Schedulers.io()).map(new Function<String, MeetingPauseOrResumBean>() {
            @Override
            public MeetingPauseOrResumBean apply(String s) throws Exception {
                MeetingPauseOrResumBean meetingPauseOrResumBean;
                if (mIsMeetingPause) {
                    meetingPauseOrResumBean = MeetingServiceTools.getInstance().requestMeetingResume();
                } else {
                    meetingPauseOrResumBean = MeetingServiceTools.getInstance().requestMeetingPause();
                }
                return meetingPauseOrResumBean;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingPauseOrResumBean>() {
            @Override
            public void accept(MeetingPauseOrResumBean bean) throws Exception {
                if (bean.getMsg() != null && bean.getMsg().equals("success")) {
                    mIsMeetingPause = !mIsMeetingPause;
                    setMeetingPauseOrResumeView();
                    bean.setPause(mIsMeetingPause);
                    EventBus.getDefault().post(bean);
                } else {
                    ToastUtils.show(host, bean.getMsg());
                    return;
                }
            }
        }).observeOn(Schedulers.io()).map(new Function<MeetingPauseOrResumBean, MeetingPauseOrResumBean>() {
            @Override
            public MeetingPauseOrResumBean apply(MeetingPauseOrResumBean meetingPauseOrResumBean) throws Exception {
                MeetingPauseOrResumBean meetingPauseMessage;
                if (mIsMeetingPause) {
                    String pauseTipsText = MeetingPauseManager.getInstance(host, meetingConfig).getPauseTipsText();
                    meetingPauseMessage = MeetingServiceTools.getInstance().requestMeetingPauseMessage(pauseTipsText);
                } else {
                    return new MeetingPauseOrResumBean();
                }
                if (meetingPauseMessage == null) {
                    meetingPauseMessage = new MeetingPauseOrResumBean();
                }
                return meetingPauseMessage;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingPauseOrResumBean>() {
            @Override
            public void accept(MeetingPauseOrResumBean meetingPauseOrResumBean) throws Exception {
                if (meetingPauseOrResumBean.getMsg() != null && !meetingPauseOrResumBean.getMsg().equals("success")) {
                    ToastUtils.show(host, meetingPauseOrResumBean.getMsg());
                }
            }
        }).subscribe();
    }

    PopMeetingMore popMeetingMore;
    private void showMorePop(){
        if (popMeetingMore != null) {
            if (popMeetingMore.isShowing()) {
                popMeetingMore.dismiss();
                popMeetingMore = null;
            }
        }

        popMeetingMore = new PopMeetingMore(host);
        popMeetingMore.show(menuMore,this);
    }

    public void refreshStatus(){
        if(window != null && window.isShowing()){
            initBySetting();
        }

    }


}
