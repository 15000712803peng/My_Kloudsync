package com.ub.techexcel.tools;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class XWalkViewTouchListener implements View.OnTouchListener {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("XWalkView_DOWN","触摸点在当前View的X轴坐标 "+event.getX()+" 触摸点在整个屏幕的X轴坐标 "+event.getRawX()+"  当前View的Y轴坐标 "+event.getRawY()+"  压力值 "+event.getPressure()+"   面积 "+event.getSize());

                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("XWalkView_Move",event.getRawX()+"  "+event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
                Log.e("XWalkView_up",event.getRawX()+"  "+event.getRawY());
                break;
        }
        return false;
    }
}
