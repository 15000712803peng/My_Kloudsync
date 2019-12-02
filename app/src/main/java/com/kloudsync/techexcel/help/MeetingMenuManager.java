package com.kloudsync.techexcel.help;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;

/**
 * Created by tonyan on 2019/11/21.
 */

public class MeetingMenuManager implements View.OnClickListener {


    private Context context;
    private static MeetingMenuManager instance;
    //
    ImageView menuIcon;
    PopBottomMenu bottomMenuPop;
    PopBottomMenu.BottomMenuOperationsListener bottomMenuOperationsListener;
    private MeetingConfig meetingConfig;

    public void setBottomMenuOperationsListener(PopBottomMenu.BottomMenuOperationsListener bottomMenuOperationsListener) {
        this.bottomMenuOperationsListener = bottomMenuOperationsListener;
    }

    public void setMenuIcon(ImageView menuIcon) {
        this.menuIcon = menuIcon;
        this.menuIcon.setOnClickListener(this);
    }

    private MeetingMenuManager(Context context, MeetingConfig meetingConfig) {
        this.context = context;
        this.meetingConfig = meetingConfig;
        bottomMenuPop = new PopBottomMenu(context,meetingConfig);

    }

    public static MeetingMenuManager getInstance(Context context, MeetingConfig meetingConfig) {
        if (instance == null) {
            synchronized (MeetingMenuManager.class) {
                if (instance == null) {
                    instance = new MeetingMenuManager(context,meetingConfig);
                }
            }
        }
        return instance;
    }

    public void release() {
        if(bottomMenuPop != null){
            bottomMenuPop.hide();
        }
        instance = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu:
                handleMenuClicked();
                break;
        }
    }

    private void handleMenuClicked() {
        if (bottomMenuPop != null && bottomMenuPop.isShowing()) {
            bottomMenuPop.hide();
            menuIcon.setImageResource(R.drawable.icon_menu);
        } else {
            bottomMenuPop = null;
            bottomMenuPop = new PopBottomMenu(context,meetingConfig);
            bottomMenuPop.show(menuIcon,bottomMenuOperationsListener);
            menuIcon.setImageResource(R.drawable.icon_menu_active);
        }
    }

    public void totalHideMenu(){
        if (bottomMenuPop != null && bottomMenuPop.isShowing()) {
            bottomMenuPop.hide();
            menuIcon.setImageResource(R.drawable.icon_menu);
        }

        if(menuIcon != null){
            menuIcon.setVisibility(View.INVISIBLE);
        }
    }


}
