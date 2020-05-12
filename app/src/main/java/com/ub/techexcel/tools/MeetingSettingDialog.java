package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventClose;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.help.PopMeetingMenu;
import com.kloudsync.techexcel.tool.MeetingSettingCache;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wang on 2017/9/18.
 */

public class MeetingSettingDialog implements View.OnClickListener {

    private Activity host;
    private Dialog settingDialog;
    private View view;
    private TextView startText;
    private ImageView microImage, cameraImage;
    private TextView microText, cameraText;
    private boolean isStartMeeting;
    private TextView closeText;
    private MeetingConfig meetingConfig;

    private LinearLayout tabTitlesLayout;
    private LinearLayout recordingLayout;
    private CheckBox isOpenRecord;
    private LinearLayout systemaudioll,thirdaudioll;
    private TextView systemtv,systemtvline,thirdaudiotv,thirdaudiotvline;
    private LinearLayout openwenxin;
    private boolean mIsCheck=false;
    private ImageView iv_dms_check,iv_dms_speaker;
    private PopMeetingMenu.MeetingMenuOperationsListener operationsListener;

    public boolean isStartMeeting() {
        return isStartMeeting;
    }

    public void setStartMeeting(boolean startMeeting) {
        isStartMeeting = startMeeting;
    }

    public interface OnUserOptionsListener {
        void onUserStart(boolean isrecord);

        void onUserJoin(boolean isrecord);
    }

    private OnUserOptionsListener onUserOptionsListener;

    public void setOnUserOptionsListener(OnUserOptionsListener onUserOptionsListener) {
        this.onUserOptionsListener = onUserOptionsListener;
    }

    public MeetingSettingDialog(Activity host,PopMeetingMenu.MeetingMenuOperationsListener operationsListener) {
        this.host = host;
        this.operationsListener=operationsListener;
        getPopupWindowInstance(host);
    }


    public void getPopupWindowInstance(Activity host) {
        if (null != settingDialog) {
            settingDialog.dismiss();
            return;
        } else {
            initPopuptWindow(host);
        }
    }

    public void initPopuptWindow(Activity host) {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.dialog_meeting_setting, null);
        iv_dms_check = view.findViewById(R.id.iv_dms_check);
        iv_dms_speaker = view.findViewById(R.id.iv_dms_speaker);
        iv_dms_speaker.setOnClickListener(this);
        microImage = view.findViewById(R.id.image_micro);
        microImage.setOnClickListener(this);
        closeText = view.findViewById(R.id.txt_cancel);
        closeText.setOnClickListener(this);
        tabTitlesLayout = view.findViewById(R.id.layout_tab_titles);
        recordingLayout = view.findViewById(R.id.layout_recording);
        isOpenRecord = view.findViewById(R.id.isOpenRecord);
        microText = view.findViewById(R.id.txt_micro);
        cameraImage = view.findViewById(R.id.image_camera);
        cameraImage.setOnClickListener(this);
        cameraText = view.findViewById(R.id.txt_camera);
        startText = view.findViewById(R.id.txt_start);
        startText.setOnClickListener(this);
        settingDialog = new Dialog(host, R.style.my_dialog);
        settingDialog.setContentView(view);
        settingDialog.setCancelable(false);
        systemaudioll=view.findViewById(R.id.systemaudioll);
        thirdaudioll=view.findViewById(R.id.thirdaudioll);
        systemtv=view.findViewById(R.id.systemtv);
        systemtv.setOnClickListener(this);
        systemtvline=view.findViewById(R.id.systemtvline);
        thirdaudiotv=view.findViewById(R.id.thirdaudiotv);
        thirdaudiotv.setOnClickListener(this);
        thirdaudiotvline=view.findViewById(R.id.thirdaudiotvline);
        openwenxin=view.findViewById(R.id.openwenxin);
        openwenxin.setOnClickListener(this);

        Window window = settingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = host.getResources().getDimensionPixelSize(R.dimen.meeting_setting_dialog_width);
        settingDialog.getWindow().setAttributes(lp);

        recordingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mIsCheck){
                    mIsCheck=true;
                    iv_dms_check.setImageResource(R.drawable.icon_check);
                }else {
                    mIsCheck=false;
                    iv_dms_check.setImageResource(R.drawable.icon_uncheck);
                }
            }
        });


    }

    private void init(MeetingSettingCache settingCache) {

        if (settingCache.getMeetingSetting().isMicroOn()) {
            microImage.setImageResource(R.drawable.icon_micro_on);
            //microText.setText(R.string.satOn);
        } else {
            microImage.setImageResource(R.drawable.icon_micro_off);
            //microText.setText(R.string.satOff);
        }

        if (settingCache.getMeetingSetting().isCameraOn()) {
            cameraImage.setImageResource(R.drawable.icon_camera_on);
            //cameraText.setText(R.string.satOn);
        } else {
            cameraImage.setImageResource(R.drawable.icon_camera_off);
            //cameraText.setText(R.string.satOff);
        }

        if (meetingConfig.isFromMeeting() && !meetingConfig.isHost()) {
            recordingLayout.setVisibility(View.GONE);
//            tabTitlesLayout.setVisibility(View.GONE);
            closeText.setVisibility(View.GONE);
        } else {
            recordingLayout.setVisibility(View.VISIBLE);
//            tabTitlesLayout.setVisibility(View.VISIBLE);
            closeText.setVisibility(View.VISIBLE);
        }

        int voiceStatus = getSettingCache(host).getMeetingSetting().getVoiceStatus();
        Log.e("PopMeetingMenu", "voice_status:" + voiceStatus);
        if (voiceStatus == 0) {
            iv_dms_speaker.setImageResource(R.drawable.icon_ear_on);
        } else if (voiceStatus == 1) {
            iv_dms_speaker.setImageResource(R.drawable.icon_audio_off);
        } else if (voiceStatus == 2) {
            iv_dms_speaker.setImageResource(R.drawable.icon_audio_on);
        }

    }

    @SuppressLint("NewApi")
    public void show(Activity host, MeetingConfig meetingConfig) {
        this.host = host;
        this.meetingConfig = meetingConfig;
        if (settingDialog != null && !settingDialog.isShowing()) {
            settingDialog.show();
        }
        settingCache = getSettingCache(host);
        init(settingCache);
    }

    public boolean isShowing() {
        if (settingDialog != null) {
            return settingDialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (settingDialog != null) {
            settingDialog.dismiss();
            settingDialog = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_dms_speaker:
//                if (meetingConfig.getMeetingStatus() == 0) {
//                    return;
//                }
                int voiceStatus = getSettingCache(host).getMeetingSetting().getVoiceStatus();
                Log.e("PopMeetingMenu", "voice_status:" + voiceStatus);
                if (voiceStatus == 0) {
                    iv_dms_speaker.setImageResource(R.drawable.icon_ear_on);
                } else if (voiceStatus == 1) {
                    iv_dms_speaker.setImageResource(R.drawable.icon_audio_off);
                } else if (voiceStatus == 2) {
                    iv_dms_speaker.setImageResource(R.drawable.icon_audio_on);
                }
                if (operationsListener != null) {
                    operationsListener.menuChangeVoiceStatus(voiceStatus);
                }
                break;
            case R.id.txt_start:
                if (isStartMeeting) {
                    if (onUserOptionsListener != null) {
                        //boolean isCheck=isOpenRecord.isChecked();
                        onUserOptionsListener.onUserStart(mIsCheck);
                    }
                } else {
                    if (onUserOptionsListener != null) {
                        //boolean isCheck=isOpenRecord.isChecked();
                        onUserOptionsListener.onUserJoin(mIsCheck);
                    }
                }
                dismiss();
                break;
            case R.id.image_micro:
                boolean isMicroOn = getSettingCache(host).getMeetingSetting().isMicroOn();
                if (!isMicroOn) {
                    microImage.setImageResource(R.drawable.icon_micro_on);
                    //microText.setText(R.string.satOn);
                } else {
                    microImage.setImageResource(R.drawable.icon_micro_off);
                    //microText.setText(R.string.satOff);
                }
                getSettingCache(host).setMicroOn(!isMicroOn);

                break;
            case R.id.image_camera:
                boolean isCameraOn = getSettingCache(host).getMeetingSetting().isCameraOn();
                if (!isCameraOn) {
                    cameraImage.setImageResource(R.drawable.icon_camera_on);
                    //cameraText.setText(R.string.satOn);
                } else {
                    cameraImage.setImageResource(R.drawable.icon_camera_off);
                    //cameraText.setText(R.string.satOff);
                }
                getSettingCache(host).setCameraOn(!isCameraOn);
                break;
            case R.id.txt_cancel:
                if (meetingConfig.isFromMeeting() && meetingConfig.getRole() == MeetingConfig.MeetingRole.HOST) {
                    EventBus.getDefault().post(new EventClose());
                }
                dismiss();
                break;

            case R.id.systemtv:
                systemaudioll.setVisibility(View.VISIBLE);
                systemtv.setTextColor(host.getResources().getColor(R.color.skyblue));
                systemtvline.setVisibility(View.VISIBLE);

                thirdaudioll.setVisibility(View.GONE);
                thirdaudiotv.setTextColor(host.getResources().getColor(R.color.black));
                thirdaudiotvline.setVisibility(View.GONE);
                break;
            case R.id.thirdaudiotv:
                systemaudioll.setVisibility(View.GONE);
                systemtv.setTextColor(host.getResources().getColor(R.color.black));
                systemtvline.setVisibility(View.GONE);

                thirdaudioll.setVisibility(View.VISIBLE);
                thirdaudiotv.setTextColor(host.getResources().getColor(R.color.skyblue));
                thirdaudiotvline.setVisibility(View.VISIBLE);
                break;
            case R.id.openwenxin:
                if (isStartMeeting) {
                    if (onUserOptionsListener != null) {
                        meetingConfig.setThirdAudio(true);
                        onUserOptionsListener.onUserStart(isOpenRecord.isChecked());
                    }
                } else {
                    if (onUserOptionsListener != null) {
                        meetingConfig.setThirdAudio(true);
                        onUserOptionsListener.onUserJoin(isOpenRecord.isChecked());
                    }
                }
                dismiss();
                break;
        }
    }

    private MeetingSettingCache settingCache;

    private MeetingSettingCache getSettingCache(Activity host) {
        if (settingCache == null) {
            settingCache = MeetingSettingCache.getInstance(host);
        }
        return settingCache;
    }

}
