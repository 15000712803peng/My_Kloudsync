package com.kloudsync.techexcel.dialog;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;

/**
 * Created by tonyan on 2019/12/20.
 */

public class PopMeetingMemberSetting extends PopupWindow implements View.OnClickListener {

    private Context context;

    private MeetingMember meetingMember;
    private TextView setPresenter,setAuditor,setSpeakMember;
    private MeetingConfig meetingConfig;

    public interface OnMemberSettingChanged{
        void setPresenter(MeetingMember meetingMember);
        void setAuditor(MeetingMember meetingMember);
        void setSpeakMember(MeetingMember meetingMember);
    }

    private OnMemberSettingChanged onMemberSettingChanged;

    public void setOnMemberSettingChanged(OnMemberSettingChanged onMemberSettingChanged) {
        this.onMemberSettingChanged = onMemberSettingChanged;
    }

    public PopMeetingMemberSetting(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_meeting_member_options, null);
        setPresenter = view.findViewById(R.id.txt_setting_presenter);
        setAuditor = view.findViewById(R.id.txt_setting_auditor);
        setSpeakMember = view.findViewById(R.id.txt_setting_speak_member);
        setSpeakMember.setOnClickListener(this);
        setPresenter.setOnClickListener(this);
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
        if(meetingMember.getPresenter() == 1){
            setPresenter.setVisibility(View.GONE);
        }
        if(meetingMember.getPresenter() == 1 || meetingMember.getRole() == 2){
            setAuditor.setVisibility(View.GONE);
        }
        showAsDropDown(view, -context.getResources().getDimensionPixelOffset(R.dimen.meeting_members_setting_width) + context.getResources().getDimensionPixelOffset(R.dimen.pop_setting_left_margin), 10);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_setting_presenter:
                if(meetingMember != null && onMemberSettingChanged != null){
                    onMemberSettingChanged.setPresenter(meetingMember);
                }
                dismiss();
                break;
            case R.id.txt_setting_auditor:
                if(meetingMember != null && onMemberSettingChanged != null){
                    onMemberSettingChanged.setAuditor(meetingMember);
                }
                dismiss();
                break;
            case R.id.txt_setting_speak_member:
                if(meetingMember != null && onMemberSettingChanged != null){
                    onMemberSettingChanged.setSpeakMember(meetingMember);
                }
                dismiss();
                break;
            default:
                break;
        }
    }


}
