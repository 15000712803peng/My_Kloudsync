package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingType;

public class PopBottomMenu implements PopupWindow.OnDismissListener, OnClickListener {

    int width;
    private Context mContext;
    private PopupWindow bottomMenuWindow;
    //--
    private ImageView menuIcon;

    private RelativeLayout menuClose;
    private RelativeLayout menuFile;
    private RelativeLayout menuStartMeeting;
    private RelativeLayout menuNote;
    private RelativeLayout menuMember;
    private RelativeLayout menuChat;
    private RelativeLayout menuTv;
    private RelativeLayout menuSync;
    private RelativeLayout menuSetting;
    private RelativeLayout menuShare;
    //----
    private MeetingConfig meetingConfig;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_menu_close:
                hide();
                Log.e("PopBottomMenu", "menu_close_clicked:" + bottomMenuOperationsListener);
                if (bottomMenuOperationsListener != null) {
                    bottomMenuOperationsListener.menuClosedClicked();
                }
                break;
            case R.id.bottom_menu_file:
                hide();
                if (bottomMenuOperationsListener != null) {
                    bottomMenuOperationsListener.menuFileClicked();
                }
                break;
            case R.id.bottom_menu_start_meeting:
                hide();
                if (bottomMenuOperationsListener != null) {
                    bottomMenuOperationsListener.menuStartMeetingClicked();
                }
                break;
            case R.id.bottom_menu_share:
                hide();
                if(bottomMenuOperationsListener != null){
                    bottomMenuOperationsListener.menuShareDocClicked();
                }
                break;
            case R.id.bottom_menu_notes:
                hide();
                if(bottomMenuOperationsListener != null){
                    bottomMenuOperationsListener.menuNoteClicked();
                }
                break;
            case R.id.bottom_menu_scan:
                hide();
                if(bottomMenuOperationsListener != null){
                    bottomMenuOperationsListener.menuScanTvClicked();
                }
                break;

            case R.id.bottom_menu_members:
                hide();
                if(bottomMenuOperationsListener != null){
                    bottomMenuOperationsListener.menuMeetingMembersClicked();
                }
                break;
            case R.id.bottom_menu_sync:
                hide();
                if(bottomMenuOperationsListener != null){
                    bottomMenuOperationsListener.menuSyncClicked();
                }
                break;
        }
    }

    public interface BottomMenuOperationsListener {
        void menuClosedClicked();
        void menuFileClicked();
        void menuStartMeetingClicked();
        void menuShareDocClicked();
        void menuNoteClicked();
        void menuScanTvClicked();
        void menuMeetingMembersClicked();
        void menuSyncClicked();

    }

    private BottomMenuOperationsListener bottomMenuOperationsListener;

    public PopBottomMenu(Context context, MeetingConfig meetingConfig) {
        this.mContext = context;
        this.meetingConfig = meetingConfig;
        getPopupWindow();
//        bottomMenuWindow.setAnimationStyle(R.style.PopupAnimation5);
    }


    public void getPopupWindow() {
        if (null != bottomMenuWindow) {
            bottomMenuWindow.dismiss();
            return;
        } else {
            init();
        }
    }

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_bottom_menu, null);
        menuClose = popupWindow.findViewById(R.id.bottom_menu_close);
        menuClose.setOnClickListener(this);
        menuFile = popupWindow.findViewById(R.id.bottom_menu_file);
        menuFile.setOnClickListener(this);
        menuStartMeeting = popupWindow.findViewById(R.id.bottom_menu_start_meeting);
        menuStartMeeting.setOnClickListener(this);
        menuNote = popupWindow.findViewById(R.id.bottom_menu_notes);
        menuNote.setOnClickListener(this);
        menuMember = popupWindow.findViewById(R.id.bottom_menu_members);
        menuMember.setOnClickListener(this);
        menuChat = popupWindow.findViewById(R.id.bottom_menu_chat);
        menuChat.setOnClickListener(this);
        menuTv = popupWindow.findViewById(R.id.bottom_menu_scan);
        menuTv.setOnClickListener(this);
        menuSync = popupWindow.findViewById(R.id.bottom_menu_sync);
        menuSync.setOnClickListener(this);
        menuSetting = popupWindow.findViewById(R.id.bottom_menu_setting);
        menuSetting.setOnClickListener(this);
        menuShare = popupWindow.findViewById(R.id.bottom_menu_share);
        menuShare.setOnClickListener(this);
        width = (int) (mContext.getResources().getDisplayMetrics().widthPixels);
        bottomMenuWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);
        initMenu(meetingConfig.getType());
        bottomMenuWindow.getWidth();
        bottomMenuWindow.getHeight();
        bottomMenuWindow.setFocusable(true);
        bottomMenuWindow.setOnDismissListener(this);
        // 设置允许在外点击消失
        bottomMenuWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        bottomMenuWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void initMenu(int meetingType) {
        switch (meetingType) {
            case MeetingType.DOC:
                menuFile.setVisibility(View.VISIBLE);
                menuNote.setVisibility(View.VISIBLE);
                menuSync.setVisibility(View.VISIBLE);
                menuTv.setVisibility(View.VISIBLE);
                menuStartMeeting.setVisibility(View.VISIBLE);
                menuShare.setVisibility(View.VISIBLE);
                menuClose.setVisibility(View.VISIBLE);
                //------
                menuMember.setVisibility(View.GONE);
                menuChat.setVisibility(View.GONE);
                menuSetting.setVisibility(View.GONE);

                break;
            case MeetingType.MEETING:
                menuFile.setVisibility(View.VISIBLE);
                menuNote.setVisibility(View.VISIBLE);
                menuSync.setVisibility(View.VISIBLE);
                menuTv.setVisibility(View.VISIBLE);
                menuStartMeeting.setVisibility(View.GONE);
                menuShare.setVisibility(View.GONE);
                menuClose.setVisibility(View.GONE);
                //------
                menuMember.setVisibility(View.VISIBLE);
                menuChat.setVisibility(View.VISIBLE);
                menuSetting.setVisibility(View.VISIBLE);
                break;
            case MeetingType.SYNCBOOK:
                break;
            case MeetingType.SYNCROOM:
                break;
        }
    }


    public void show(ImageView menu,PopBottomMenu.BottomMenuOperationsListener bottomMenuOperationsListener) {
        this.menuIcon = menu;
        this.bottomMenuOperationsListener = bottomMenuOperationsListener;
        bottomMenuWindow.showAtLocation(menu, Gravity.BOTTOM | Gravity.LEFT,
                width - mContext.getResources().getDimensionPixelSize(R.dimen.fab_margin),
                mContext.getResources().getDimensionPixelSize(R.dimen.menu_bottom_margin));
    }

    public boolean isShowing() {
        if (bottomMenuWindow != null) {
            return bottomMenuWindow.isShowing();
        }
        return false;
    }

    public void hide() {
        if (bottomMenuWindow != null) {
            bottomMenuWindow.dismiss();
        }
        bottomMenuWindow = null;
    }

    @Override
    public void onDismiss() {
        Log.e("PopBottomMenu", "on_dismiss");
        if (menuIcon != null) {
            Log.e("PopBottomMenu", "on_dismiss_menu_icon");
            menuIcon.setImageResource(R.drawable.icon_menu);
        }
        bottomMenuWindow = null;
    }
}
