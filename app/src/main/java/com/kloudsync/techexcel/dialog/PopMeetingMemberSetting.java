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

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingMember;

/**
 * Created by tonyan on 2019/12/20.
 */

public class PopMeetingMemberSetting extends PopupWindow implements View.OnClickListener {

    private Context context;

    private MeetingMember meetingMember;

    public PopMeetingMemberSetting(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_meeting_member_options, null);
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

    public void showAtBottom(MeetingMember meetingMember,View view) {
        this.meetingMember = meetingMember;
        showAsDropDown(view, -context.getResources().getDimensionPixelOffset(R.dimen.meeting_members_setting_width) + context.getResources().getDimensionPixelOffset(R.dimen.pop_setting_left_margin), 10);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            default:
                break;
        }
    }


}
