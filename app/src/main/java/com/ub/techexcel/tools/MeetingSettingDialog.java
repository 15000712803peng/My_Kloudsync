package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2017/9/18.
 */

public class MeetingSettingDialog implements View.OnClickListener{

    private Context mContext;
    private Dialog settingDialog;
    private View view;
    private TextView startText;
    public interface OnUserOptionsListener{
        void onUserStart();
    }

    private OnUserOptionsListener onUserOptionsListener;

    public void setOnUserOptionsListener(OnUserOptionsListener onUserOptionsListener) {
        this.onUserOptionsListener = onUserOptionsListener;
    }

    public  MeetingSettingDialog(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
    }


    public void getPopupWindowInstance() {
        if (null != settingDialog) {
            settingDialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_meeting_setting, null);
        startText = view.findViewById(R.id.txt_start);
        startText.setOnClickListener(this);
        settingDialog = new Dialog(mContext, R.style.my_dialog);
        settingDialog.setContentView(view);
    }


    @SuppressLint("NewApi")
    public void show() {
        if (settingDialog != null && !settingDialog.isShowing()) {
            settingDialog.show();
        }
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
                if(onUserOptionsListener != null){
                    onUserOptionsListener.onUserStart();
                }
                dismiss();
                break;
        }
    }


}
