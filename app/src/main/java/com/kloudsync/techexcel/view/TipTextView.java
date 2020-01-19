package com.kloudsync.techexcel.view;

import android.content.Context;

import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import android.view.animation.TranslateAnimation;

public class TipTextView extends AppCompatTextView {
    private static final int START_TIME = 500;//动画显示时间
    private static final int END_TIME = 500;//动画移出时间
    private static final int SHOW_TIME = 1500;//动画显示时间
    private int titleHeight = 100;//标题栏默认的高度设置成100

    public TipTextView(Context context) {
        super(context);
    }

    public TipTextView(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
    }

    public TipTextView(Context context, AttributeSet paramAttributeSet, int paramInt) {
        super(context, paramAttributeSet, paramInt);
    }

    public void showTips() {

        if (getVisibility() == VISIBLE) {
            return;
        }
        setVisibility(View.VISIBLE);
        //向下移动动画
        TranslateAnimation downTranslateAnimation = new TranslateAnimation(0, 0, 0, titleHeight);
        downTranslateAnimation.setDuration(START_TIME);
        downTranslateAnimation.setFillAfter(true);
        startAnimation(downTranslateAnimation);

        //动画监听
        downTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {//向下移动动画结束
                topTranslateAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void shake(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(END_TIME);
        alphaAnimation.setRepeatCount(3);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                topTranslateAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void topTranslateAnimation() {
        TranslateAnimation topTranslateAnimation = new TranslateAnimation(0, 0, titleHeight, 0);
        topTranslateAnimation.setDuration(START_TIME);
        topTranslateAnimation.setFillAfter(true);
        topTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {//动画结束隐藏提示的TextView
                setVisibility(View.GONE);
            }
        });
    }

    /**
     * 设置标题栏高度
     *
     * @param titleHeight
     */
    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;
    }
}
