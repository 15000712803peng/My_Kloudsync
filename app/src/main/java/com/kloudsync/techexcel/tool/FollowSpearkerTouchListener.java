package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

import static android.content.Context.MODE_PRIVATE;


public class FollowSpearkerTouchListener implements View.OnTouchListener {

    private int startY;
    private int startX;
    private int endX, endY;
    private RelativeLayout spearkerLayout;
    private RelativeLayout.LayoutParams layoutParams;
    Context context;
    int width, height;
    private FrameLayout speakerViewLayout;
    int screenWidth, screenHeight;
    private CameraTouchListener cameraTouchListener;



    public void refreshBySetting() {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = spearkerLayout.getLeft();
        params.topMargin = spearkerLayout.getTop();
        params.setMargins(spearkerLayout.getLeft(), spearkerLayout.getTop(), 0, 0);
        spearkerLayout.setLayoutParams(params);

    }

    public void setSpeakerLayout(RelativeLayout spearkerLayout) {
        this.spearkerLayout = spearkerLayout;
    }

    boolean isMove;

    public FollowSpearkerTouchListener(Context context,CameraTouchListener cameraTouchListener) {
        this.context = context;
        this.cameraTouchListener = cameraTouchListener;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    private void getSize() {
        String modeSetting = context.getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE).getString("speaker_size_mode", "small");
        Log.e("getSize", "mode_setting:" + modeSetting);
        if (modeSetting.equals("small")) {
            width = context.getResources().getDimensionPixelSize(R.dimen.speaker_normal_width);
            height = context.getResources().getDimensionPixelSize(R.dimen.speaker_normal);
        } else if (modeSetting.equals("big")) {
            width = context.getResources().getDimensionPixelSize(R.dimen.speaker_big_width);
            height = context.getResources().getDimensionPixelSize(R.dimen.speaker_big);
        } else if (modeSetting.equals("large")) {
            width = context.getResources().getDimensionPixelSize(R.dimen.speaker_large_wdth);
            height = context.getResources().getDimensionPixelSize(R.dimen.speaker_large);
        }
    }

    public int getLeft() {
        if (spearkerLayout != null) {
            return spearkerLayout.getLeft();
        }
        return 0;
    }

    public int getTop() {
        if (spearkerLayout != null) {
            return spearkerLayout.getTop();
        }
        return 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e("FollowSpearkerTouchListener", "onTouch:" + event.getRawX());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getSize();
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                endX = startX;
                endY = startY;
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                int moveY = (int) event.getRawY();


                int move_bigX = moveX - startX;
                int move_bigY = moveY - startY;

                if (Math.abs(move_bigX) > 0 || Math.abs(move_bigY) > 0) {
                    //拿到当前控件未移动的坐标
                    int left = spearkerLayout.getLeft();
                    int top = spearkerLayout.getTop();
                    left += move_bigX;
                    top += move_bigY;
                    int right = left + spearkerLayout.getWidth();
                    int bottom = top + spearkerLayout.getHeight();
                    if (left > 0 && top > 0 && right < screenWidth && bottom < screenHeight) {
                        spearkerLayout.layout(left, top, right, bottom);
                        isMove = true;
                    }

                }
                startX = moveX;
                startY = moveY;

                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    isMove = false;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                    params.leftMargin = spearkerLayout.getLeft();
                    params.topMargin = spearkerLayout.getTop();
                    params.setMargins(spearkerLayout.getLeft(), spearkerLayout.getTop(), 0, 0);
                    spearkerLayout.setLayoutParams(params);
                    if(cameraTouchListener != null){
                        cameraTouchListener.layoutCamera(spearkerLayout.getLeft(),spearkerLayout.getTop());
                    }
                    return true;
                }


                break;

        }

        return false;
    }

    public void layoutSpeaker(int left ,int top){
        if(spearkerLayout != null){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
            params.leftMargin = spearkerLayout.getLeft();
            params.topMargin = spearkerLayout.getTop();
            params.setMargins(left, top, 0, 0);
            spearkerLayout.setLayoutParams(params);
        }

    }
}
