package com.kloudsync.techexcel.tool;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by tonyan on 2020/4/12.
 */

public class CameraRecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {

    private int startY;
    private int startX;
    private int endX, endY;
    private RelativeLayout cameraLayout;
    private FollowSpearkerTouchListener followSpearkerTouchListener;

    public void setFollowSpearkerTouchListener(FollowSpearkerTouchListener followSpearkerTouchListener) {
        this.followSpearkerTouchListener = followSpearkerTouchListener;
    }

    public void setCameraLayout(RelativeLayout cameraLayout) {
        this.cameraLayout = cameraLayout;
    }

    boolean isMove;


    public void layoutCamera(int left ,int top){
        if(cameraLayout != null){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = cameraLayout.getLeft();
            params.topMargin = cameraLayout.getTop();
            params.setMargins(left, top, 0, 0);
            cameraLayout.setLayoutParams(params);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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

                if (Math.abs(move_bigX) > 10 || Math.abs(move_bigY) > 10) {
                    //拿到当前控件未移动的坐标
                    int left = cameraLayout.getLeft();
                    int top = cameraLayout.getTop();
                    left += move_bigX;
                    top += move_bigY;
                    int right = left + cameraLayout.getWidth();
                    int bottom = top + cameraLayout.getHeight();
                    cameraLayout.layout(left, top, right, bottom);
                    isMove = true;
                }
                startX = moveX;
                startY = moveY;

                break;
            case MotionEvent.ACTION_UP:
                if(isMove){
                    isMove = false;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = cameraLayout.getLeft();
                    params.topMargin = cameraLayout.getTop();
                    params.setMargins(cameraLayout.getLeft(), cameraLayout.getTop(), 0, 0);
                    cameraLayout.setLayoutParams(params);
                    if(followSpearkerTouchListener != null){
                        followSpearkerTouchListener.layoutSpeaker(cameraLayout.getLeft(),cameraLayout.getTop());
                    }
                    return true;
                }else {
                    return false;
                }

        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
