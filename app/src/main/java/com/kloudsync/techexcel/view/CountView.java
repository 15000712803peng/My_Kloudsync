package com.kloudsync.techexcel.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;


@SuppressLint("NewApi")
public class CountView extends TextView{
    //����ʱ�� ms
    int duration = 1500;
    int number;
    public CountView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void showNumberWithAnimation(int number) {
        ObjectAnimator objectAnimator=ObjectAnimator.ofInt(this,"number",0,number);
        objectAnimator.setDuration(duration);
        //���������������쵽�ٵ���
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }
 
    public float getNumber() {
        return number;
    }
 
    public void setNumber(int number) {
        this.number = number;
        setText(number+"");
    }
}