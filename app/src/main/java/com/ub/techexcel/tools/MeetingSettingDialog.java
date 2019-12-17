package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.tool.MeetingSettingCache;

/**
 * Created by wang on 2017/9/18.
 */

public class MeetingSettingDialog implements View.OnClickListener{

    private Activity host;
    private Dialog settingDialog;
    private View view;
    private TextView startText;
    private ImageView microImage,cameraImage;
    private TextView microText,cameraText;
    private boolean isStartMeeting;

    public boolean isStartMeeting() {
        return isStartMeeting;
    }

    public void setStartMeeting(boolean startMeeting) {
        isStartMeeting = startMeeting;
    }

    public interface OnUserOptionsListener{
        void onUserStart();
        void onUserJoin();
    }

    private OnUserOptionsListener onUserOptionsListener;

    public void setOnUserOptionsListener(OnUserOptionsListener onUserOptionsListener) {
        this.onUserOptionsListener = onUserOptionsListener;
    }

    public  MeetingSettingDialog(Activity host) {
        this.host = host;
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
        microImage = view.findViewById(R.id.image_micro);
        microImage.setOnClickListener(this);
        microText = view.findViewById(R.id.txt_micro);
        cameraImage = view.findViewById(R.id.image_camera);
        cameraImage.setOnClickListener(this);
        cameraText = view.findViewById(R.id.txt_camera);
        startText = view.findViewById(R.id.txt_start);
        startText.setOnClickListener(this);
        settingDialog = new Dialog(host, R.style.my_dialog);
        settingDialog.setContentView(view);

    }

    private void init(MeetingSettingCache settingCache){

        if(settingCache.getMeetingSetting().isMicroOn()){
            microImage.setImageResource(R.drawable.sound_on1);
            microText.setText(R.string.satOn);
        }else {
            microImage.setImageResource(R.drawable.sound_off1);
            microText.setText(R.string.satOff);
        }

        if(settingCache.getMeetingSetting().isCameraOn()){
            cameraImage.setImageResource(R.drawable.cam_on2);
            cameraText.setText(R.string.satOn);
        }else {
            cameraImage.setImageResource(R.drawable.cam_off2);
            cameraText.setText(R.string.satOff);
        }
    }

    @SuppressLint("NewApi")
    public void show(Activity host) {
        this.host = host;
        if (settingDialog != null && !settingDialog.isShowing()) {
            settingDialog.show();
        }
        settingCache = getSettingCache(host);
        init(settingCache);
    }

    public boolean isShowing() {
        if(settingDialog != null){
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
            case R.id.txt_start:
                if(isStartMeeting){
                    if(onUserOptionsListener != null){
                        onUserOptionsListener.onUserStart();
                    }
                }else {
                    if(onUserOptionsListener != null){
                        onUserOptionsListener.onUserJoin();
                    }
                }

                dismiss();
                break;
            case R.id.image_micro:
                boolean isMicroOn = getSettingCache(host).getMeetingSetting().isMicroOn();
                if(!isMicroOn){
                    microImage.setImageResource(R.drawable.sound_on1);
                    microText.setText(R.string.satOn);
                }else {
                    microImage.setImageResource(R.drawable.sound_off1);
                    microText.setText(R.string.satOff);
                }
                getSettingCache(host).setMicroOn(!isMicroOn);

                break;
            case R.id.image_camera:
                boolean isCameraOn = getSettingCache(host).getMeetingSetting().isCameraOn();
                if(!isCameraOn){
                    cameraImage.setImageResource(R.drawable.cam_on2);
                    cameraText.setText(R.string.satOn);
                }else {
                    cameraImage.setImageResource(R.drawable.cam_off2);
                    cameraText.setText(R.string.satOff);
                }
                getSettingCache(host).setCameraOn(!isCameraOn);
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
