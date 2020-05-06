package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.personal.AboutActivity2;
import com.kloudsync.techexcel.personal.AboutWebActivity;
import com.kloudsync.techexcel.personal.HelpCenterActivity;
import com.kloudsync.techexcel.personal.LanguageActivity;
import com.nineoldandroids.view.ViewHelper;

public class DrawerLayoutManager implements View.OnClickListener {

    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private ImageView drawerloginmenu;
    private LinearLayout menu_layout;
    private TextView languagetv,abouttv,privacytv,helptv;

    private DrawerLayoutManager(Context context) {
        this.mContext = context;
    }

    static volatile DrawerLayoutManager instance;

    public static DrawerLayoutManager getManager(Context context) {
        if (instance == null) {
            synchronized (SocketMessageManager.class) {
                if (instance == null) {
                    instance = new DrawerLayoutManager(context);
                }
            }
        }
        return instance;
    }

    public void initDrawerLayout(DrawerLayout drawerLayout) {
        this.mDrawerLayout=drawerLayout;
        menu_layout = mDrawerLayout.findViewById(R.id.rightmenu);

        drawerloginmenu = mDrawerLayout.findViewById(R.id.drawerloginmenu);
        languagetv = menu_layout.findViewById(R.id.languagetv);
        abouttv = menu_layout.findViewById(R.id.abouttv);
        privacytv = menu_layout.findViewById(R.id.privacytv);
        helptv = menu_layout.findViewById(R.id.helptv);

        drawerloginmenu.setOnClickListener(this);
        languagetv.setOnClickListener(this);
        abouttv.setOnClickListener(this);
        privacytv.setOnClickListener(this);
        helptv.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        Intent intent=null;
        switch (view.getId()){
            case R.id.drawerloginmenu:
                mDrawerLayout.openDrawer(Gravity.END);
                break;
            case R.id.languagetv:
                mDrawerLayout.closeDrawer(Gravity.END);
                intent = new Intent(mContext, LanguageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                break;
            case R.id.abouttv:
                mDrawerLayout.closeDrawer(Gravity.END);
                intent = new Intent(mContext, AboutActivity2.class);
                mContext.startActivity(intent);
                break;
            case R.id.privacytv:
                mDrawerLayout.closeDrawer(Gravity.END);
                String enUrl = "https://kloudsync.peertime.cn/privacy.html";
                String zhUrl = "https://kloudsync.peertime.cn/privacy-cn.html";
                String tag = mContext.getString(R.string.privacy_statement);
                intent = new Intent(mContext, AboutWebActivity.class);
                intent.putExtra(AboutWebActivity.TAG,tag);
                intent.putExtra(AboutWebActivity.ENURL,enUrl);
                intent.putExtra(AboutWebActivity.ZHURL,zhUrl);
                mContext.startActivity(intent);
                break;
            case R.id.helptv:
                mDrawerLayout.closeDrawer(Gravity.END);
                intent = new Intent(mContext, HelpCenterActivity.class);
                mContext.startActivity(intent);
                break;
        }

    }
}
