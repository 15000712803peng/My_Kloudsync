package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class PopAlbums {

    public Context mContext;

    private static PopAlbumsDismissListener popAlbumsDismissListener;

    public interface PopAlbumsDismissListener {
        void PopDismiss(boolean isAdd);
        void PopDismissPhoto(boolean isAdd);
        void PopBack();
    }

    public void setPoPDismissListener(PopAlbumsDismissListener popAlbumsDismissListener) {
        this.popAlbumsDismissListener = popAlbumsDismissListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation5);
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

    public void BulinBulin(){
        tv_album2.setText("From favourite");
    }

    private TextView tv_album,tv_album2;
    private LinearLayout lin_album1,
            lin_album2,
            lin_album3,
            lin_album4;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_album, null);

        tv_album = (TextView) popupWindow.findViewById(R.id.tv_album);
        tv_album2 = (TextView) popupWindow.findViewById(R.id.tv_album2);
        lin_album1 = (LinearLayout) popupWindow.findViewById(R.id.lin_album1);
        lin_album2 = (LinearLayout) popupWindow.findViewById(R.id.lin_album2);
        lin_album3 = (LinearLayout) popupWindow.findViewById(R.id.lin_album3);
        lin_album4 = (LinearLayout) popupWindow.findViewById(R.id.lin_album4);

        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popAlbumsDismissListener != null) {
                    popAlbumsDismissListener.PopBack();
                }
            }
        });

        lin_album1.setOnClickListener(new myOnClick());
        lin_album2.setOnClickListener(new myOnClick());
        lin_album3.setOnClickListener(new myOnClick());
        lin_album4.setOnClickListener(new myOnClick());


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
                case R.id.lin_album1:
                    DissmissPhotoPop(true);
                    break;
                case R.id.lin_album2:
                    DissmissVideoPop(true);
                    break;
                case R.id.lin_album3:
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_album4:
                    mPopupWindow.dismiss();
                    break;

                default:
                    break;
            }

        }


    }

    private void DissmissPhotoPop(boolean isupdate) {
        if (popAlbumsDismissListener != null) {
            popAlbumsDismissListener.PopDismissPhoto(isupdate);
        }
        mPopupWindow.dismiss();
    }

    private void DissmissVideoPop(boolean isupdate) {
        if (popAlbumsDismissListener != null) {
            popAlbumsDismissListener.PopDismiss(isupdate);
        }
        mPopupWindow.dismiss();
    }

    public void StartPop(View v) {
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }


}
