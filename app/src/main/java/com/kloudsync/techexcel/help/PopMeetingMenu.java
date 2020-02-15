package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.ETC1Util;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.MeetingSettingCache;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class PopMeetingMenu implements PopupWindow.OnDismissListener, OnClickListener {

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_camera:
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
                if(meetingConfig.getRole() == MeetingConfig.MeetingRole.AUDIENCE){
                    Toast.makeText(host,"没有权限操作",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (operationsListener != null) {
                    operationsListener.menuSwitchCamera();
                }
                break;
            case R.id.meeting_menu_end:
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
                if (operationsListener != null) {
                    operationsListener.menuInviteClicked();
                }
                hide();
                break;
            case R.id.meeting_menu_more:
                if (operationsListener != null) {
                    operationsListener.menuMoreClicked();
                }
                break;
        }
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

    public void show(Activity host, ImageView menu, MeetingConfig meetingConfig, PopMeetingMenu.MeetingMenuOperationsListener operationsListener) {
        this.host = host;
        this.menuIcon = menu;
        this.meetingConfig = meetingConfig;
        this.operationsListener = operationsListener;
        initBySetting();
        Log.e("PopMeetingMenu", "show_menu");
        window.showAtLocation(menu, Gravity.NO_GRAVITY,
                width - host.getResources().getDimensionPixelSize(R.dimen.meeting_menu_margin_left),
                host.getResources().getDimensionPixelSize(R.dimen.menu_top_margin));
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


}
