package com.kloudsync.techexcel.personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.nineoldandroids.view.ViewHelper;

public class DrawerLayoutActivity extends Activity  {

    private DrawerLayout mDrawerLayout;
private LinearLayout ly_content;
private LinearLayout menu_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ly_content=findViewById(R.id.ly_content);
        menu_layout=findViewById(R.id.rightmenu);
        ly_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.END);
            }
        });
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float slideOffset) {
                Log.i("---", "滑动中"+slideOffset);
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = menu_layout;
                float scale = 1 - slideOffset;
//                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                ViewHelper.setTranslationX(mContent,
                        -mMenu.getMeasuredWidth() * (1 - scale));
                ViewHelper.setPivotX(mContent, 0);
                ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                Log.i("---", "打开");
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                Log.i("---", "关闭");
            }

            @Override
            public void onDrawerStateChanged(int i) {
                Log.i("---", "状态改变");
            }
        });


    }



}
