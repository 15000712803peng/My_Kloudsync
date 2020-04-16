package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventClose;
import com.kloudsync.techexcel.bean.MeetingConfig;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wang on 2017/9/18.
 */

public class MeetingWarningDialog implements View.OnClickListener {

    private Context context;
    private Dialog settingDialog;
    private View view;
    private TextView startText;

    private TextView closeText;
    private MeetingConfig meetingConfig;

    public interface OnUserOptionsListener {
        void onUserStart();
    }

    private OnUserOptionsListener onUserOptionsListener;

    public void setOnUserOptionsListener(OnUserOptionsListener onUserOptionsListener) {
        this.onUserOptionsListener = onUserOptionsListener;
    }

    public MeetingWarningDialog(Context context) {
        this.context = context;
        getPopupWindowInstance(context);
    }


    public void getPopupWindowInstance(Context host) {
        if (null != settingDialog) {
            settingDialog.dismiss();
            return;
        } else {
            initPopuptWindow(host);
        }
    }

    public void initPopuptWindow(Context host) {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.dialog_meeting_warning, null);
        closeText = view.findViewById(R.id.txt_cancel);
        closeText.setOnClickListener(this);
        startText = view.findViewById(R.id.txt_start);
        startText.setOnClickListener(this);
        settingDialog = new Dialog(host, R.style.my_dialog);
        settingDialog.setContentView(view);
        settingDialog.setCancelable(false);

        Window window = settingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = host.getResources().getDimensionPixelSize(R.dimen.meeting_setting_dialog_width);
        settingDialog.getWindow().setAttributes(lp);
    }


    @SuppressLint("NewApi")
    public void show(Context host, MeetingConfig meetingConfig) {
        this.context = host;
        this.meetingConfig = meetingConfig;
        if (settingDialog != null && !settingDialog.isShowing()) {
            settingDialog.show();
        }

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
            case R.id.txt_start:
                dismiss();
                onUserOptionsListener.onUserStart();
                break;
            case R.id.txt_cancel:
                dismiss();
//                if(meetingConfig.isFromMeeting() && meetingConfig.getRole() == MeetingConfig.MeetingRole.HOST){
                    EventBus.getDefault().post(new EventClose());
//                }
                break;
        }
    }


}
