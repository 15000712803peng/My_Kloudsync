package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.tool.PopupWindowUtil;
import com.ub.kloudsync.activity.Document;

public class PopMoreFavorite {

    public Context mContext;

    private static PopMoreFavoriteListener popMoreFavoriteListener;

    private boolean flag;

    private Document fav;


    public interface PopMoreFavoriteListener {
        void PopView();
        void PopDelete();
        void PopShare();
    }

    public void setPoPMoreListener(PopMoreFavoriteListener popMoreFavoriteListener) {
        PopMoreFavorite.popMoreFavoriteListener = popMoreFavoriteListener;
    }

    public void getPopwindow(Context context, Document fav) {
        this.mContext = context;
        this.fav = fav;

        getPopupWindowInstance();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation3);
    }


    public PopupWindow mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private TextView tv_view, tv_share;
    private TextView tv_delete;
    private View popupWindow;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.pop_favormore, null);

        tv_view = (TextView) popupWindow.findViewById(R.id.tv_view);
        tv_share = (TextView) popupWindow.findViewById(R.id.tv_share);
        tv_delete = (TextView) popupWindow.findViewById(R.id.tv_delete);

        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        tv_view.setOnClickListener(new myOnClick());
        tv_share.setOnClickListener(new myOnClick());
        tv_delete.setOnClickListener(new myOnClick());


        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_view:
                    popMoreFavoriteListener.PopView();
                    mPopupWindow.dismiss();
                    break;
                case R.id.tv_share:
                    popMoreFavoriteListener.PopShare();
                    mPopupWindow.dismiss();
                    break;
                case R.id.tv_delete:
                    popMoreFavoriteListener.PopDelete();
                    mPopupWindow.dismiss();
                    break;

                default:
                    break;
            }

        }

    }

    public void StartPop(View v) {
//        mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, popupWindow , 100);
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        Log.e("duang", height + ":" + windowPos[1]);
        int xOff = 20; // 可以自己调整偏移
        windowPos[0] -= xOff;
        mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
        //如果指定高度的话用这个也能达到效果,不过会把列表往上移
//        mPopupWindow.showAsDropDown(v);
//        mPopupWindow.showAsDropDown(v,windowPos[0], windowPos[1]);
    }


}
