package com.kloudsync.techexcel.dialog;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventKickOffMember;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.linshi.LinshiActivity;
import com.kloudsync.techexcel.tool.DensityUtil;

import org.greenrobot.eventbus.EventBus;

import static com.kloudsync.techexcel.bean.MeetingMember.TYPE_ITEM_HANDSUP_MEMBER;
import static com.kloudsync.techexcel.bean.MeetingMember.TYPE_ITEM_MAIN_SPEAKER;
import static com.kloudsync.techexcel.bean.MeetingMember.TYPE_ITEM_SPEAKING_SPEAKER;

/**
 * Created by tonyan on 2019/12/20.
 */

public class PopMeetingMemberSetting extends PopupWindow implements View.OnClickListener {

    private Context context;

    private MeetingMember meetingMember;
    private TextView setPresenter, setAuditor, setSpeakMember, kickOffMember;
    private MeetingConfig meetingConfig;
    View view;

    public interface OnMemberSettingChanged {
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
        view = inflater.inflate(R.layout.pop_meeting_member_options, null);
        setPresenter = view.findViewById(R.id.txt_setting_presenter);
        setAuditor = view.findViewById(R.id.txt_setting_auditor);
        setSpeakMember = view.findViewById(R.id.txt_setting_speak_member);
        kickOffMember = view.findViewById(R.id.txt_kick_off);
        kickOffMember.setOnClickListener(this);
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

    public void showAtBottom(MeetingMember meetingMember, View view, MeetingConfig meetingConfig) {
        this.meetingMember = meetingMember;
        this.meetingConfig = meetingConfig;
        if (meetingMember.getPresenter() == 1) {
            setPresenter.setVisibility(View.GONE);
        }
        if (meetingMember.getPresenter() == 1 || meetingMember.getRole() == 2) {
            setAuditor.setVisibility(View.GONE);
        }
        if (meetingConfig.getMeetingHostId().equals(meetingMember.getUserId() + "")) {
            // 操作的成员是HOST
            kickOffMember.setVisibility(View.GONE);
        } else {
            // 不是HOST，如果自己是HOST
            if (AppConfig.UserID.equals(meetingConfig.getMeetingHostId())) {
                kickOffMember.setVisibility(View.VISIBLE);
            } else {
                kickOffMember.setVisibility(View.GONE);
            }
        }
        showAsDropDown(view, -context.getResources().getDimensionPixelOffset(R.dimen.meeting_members_setting_width) + context.getResources().getDimensionPixelOffset(R.dimen.pop_setting_left_margin), 10);
    }

    public void showAtLeft(MeetingMember meetingMember, View view, MeetingConfig meetingConfig) {
        this.meetingMember = meetingMember;
        this.meetingConfig = meetingConfig;
//        if (meetingMember.getPresenter() == 1) {
//            setPresenter.setVisibility(View.GONE);
//        }
//        if (meetingMember.getPresenter() == 1 || meetingMember.getRole() == 2) {
//            setAuditor.setVisibility(View.GONE);
//        }
//        if (meetingConfig.getMeetingHostId().equals(meetingMember.getUserId() + "")) {
//            // 操作的成员是HOST
//            kickOffMember.setVisibility(View.GONE);
//        } else {
//            // 不是HOST，如果自己是HOST
//            if (AppConfig.UserID.equals(meetingConfig.getMeetingHostId())) {
//                kickOffMember.setVisibility(View.VISIBLE);
//            } else {
//                kickOffMember.setVisibility(View.GONE);
//            }
//        }

        //判断自己的身份
        if(meetingConfig.getMeetingHostId().equals(AppConfig.UserID)){  // 主持人身份
            if((meetingMember.getUserId()+"").equals(AppConfig.UserID)){
                setPresenter.setVisibility(View.VISIBLE); //设为演示人
            }else{
                setPresenter.setVisibility(View.VISIBLE); //设为演示人
                setSpeakMember.setVisibility(View.VISIBLE); // 设为临时发言人
                setAuditor.setVisibility(View.VISIBLE);  // 设为参会者
                kickOffMember.setVisibility(View.VISIBLE); //请他离开会议
            }
        }else if(meetingConfig.getPresenterId().equals(AppConfig.UserID)){  //演示者身份

            if((meetingMember.getUserId()+"").equals(meetingConfig.getMeetingHostId())){
                setPresenter.setVisibility(View.VISIBLE); //设为演示人
            }else{
                setPresenter.setVisibility(View.VISIBLE); //设为演示人
                setSpeakMember.setVisibility(View.VISIBLE); // 设为临时发言人
                setAuditor.setVisibility(View.VISIBLE);  // 设为参会者
            }

        }else if(meetingConfig.getViewType()==TYPE_ITEM_MAIN_SPEAKER){ //发言人身份

            if((meetingMember.getUserId()+"").equals(AppConfig.UserID)){
                setPresenter.setVisibility(View.VISIBLE); //设为演示人
                setSpeakMember.setVisibility(View.VISIBLE); // 设为临时发言人
                setAuditor.setVisibility(View.VISIBLE);  // 设为参会者
            }else{

                setSpeakMember.setVisibility(View.VISIBLE); // 设为临时发言人
                setAuditor.setVisibility(View.VISIBLE);  // 设为参会者
            }

        }else if(meetingConfig.getViewType()==TYPE_ITEM_SPEAKING_SPEAKER){ //临时发言人

        }else if(meetingConfig.getViewType()==TYPE_ITEM_HANDSUP_MEMBER){ //允许发言

        }else {

        }


        this.view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = this.view.getMeasuredWidth();
        int popupHeight = this.view.getMeasuredHeight();
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        showAtLocation(view, Gravity.NO_GRAVITY, location[0] - popupWidth - DensityUtil.dp2px(context, 40), location[1] + view.getHeight() / 2 - popupHeight / 2);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_setting_presenter://设置为主持人
                if (meetingMember != null && onMemberSettingChanged != null) {
                    onMemberSettingChanged.setPresenter(meetingMember);
                }
                dismiss();
                break;
            case R.id.txt_setting_auditor://设置为参会者
                if (meetingMember != null && onMemberSettingChanged != null) {
                    onMemberSettingChanged.setAuditor(meetingMember);
                }
                dismiss();
                break;
            case R.id.txt_setting_speak_member://设置为可讲话参会者
                if (meetingMember != null && onMemberSettingChanged != null) {
                    onMemberSettingChanged.setSpeakMember(meetingMember);
                }
                dismiss();
                break;
            case R.id.txt_kick_off:
                if (meetingMember != null) {
                    Log.e("check_post_kick_off", "post_4");
                    EventKickOffMember kickOffMember = new EventKickOffMember();
                    kickOffMember.setMeetingMember(meetingMember);
                    EventBus.getDefault().post(kickOffMember);
                }
                dismiss();
                break;
            default:
                break;
        }
    }


}
