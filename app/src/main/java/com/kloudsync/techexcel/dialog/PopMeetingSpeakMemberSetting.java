package com.kloudsync.techexcel.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.config.AppConfig;

/**
 * Created by tonyan on 2019/12/20.
 */

public class PopMeetingSpeakMemberSetting extends PopupWindow implements View.OnClickListener {

    private Context context;

    private MeetingMember meetingMember;
    private TextView setAuditor,setMainMember;
    private MeetingConfig meetingConfig;

    public interface OnSpeakMemberSettingChanged{
        void setSpeakToAuditor(MeetingMember meetingMember);
        void setSpeakToMember(MeetingMember meetingMember);
    }

    private OnSpeakMemberSettingChanged onMemberSettingChanged;

    public void setOnMemberSettingChanged(OnSpeakMemberSettingChanged onMemberSettingChanged) {
        this.onMemberSettingChanged = onMemberSettingChanged;
    }

    public PopMeetingSpeakMemberSetting(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_meeting_speak_member_options, null);
        setAuditor = view.findViewById(R.id.txt_setting_auditor);
        setMainMember = view.findViewById(R.id.txt_setting_main_members);
        setMainMember.setOnClickListener(this);
        setAuditor.setOnClickListener(this);
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        this.setWidth(context.getResources().getDimensionPixelOffset(R.dimen.meeting_members_setting_width));
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.update();

    }

    public void showAtBottom(MeetingMember meetingMember,View view,MeetingConfig meetingConfig) {
        this.meetingMember = meetingMember;
        this.meetingConfig = meetingConfig;
        if((meetingMember.getUserId() +"").equals(AppConfig.UserID)){
            setMainMember.setVisibility(View.GONE);
        }

        showAsDropDown(view, -context.getResources().getDimensionPixelOffset(R.dimen.meeting_members_setting_width) + context.getResources().getDimensionPixelOffset(R.dimen.pop_setting_left_margin), 10);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_setting_main_members:
                if(meetingMember != null && onMemberSettingChanged != null){
                    onMemberSettingChanged.setSpeakToMember(meetingMember);
                }
                dismiss();
                break;
            case R.id.txt_setting_auditor:
                if(meetingMember != null && onMemberSettingChanged != null){
                    onMemberSettingChanged.setSpeakToAuditor(meetingMember);
                }
                dismiss();
                break;
            default:
                break;
        }
    }


}
