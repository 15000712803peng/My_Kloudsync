package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2017/9/18.
 */

public class WebCamPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private ImageView tv1;
    private ImageView tv2;
    private ImageView closebnt;
    private TextView bnt, tv11, tv21;
    private boolean isListen = true, isMute = false;
    private TextView systembtn, kloudcallbtn, externalbtn;
    private LinearLayout ll;
    private int identity;
    private String firstStatus;
    private CheckBox isOpenRecord;

    public void getPopwindow(Context context, int identity, String firstStatus) {
        this.mContext = context;
        this.identity = identity;
        this.firstStatus = firstStatus;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public void setDefault(int i) {
//        Toast.makeText(mContext,i+"",Toast.LENGTH_LONG).show();
        switch (i) {
            case 0:
                tv1.setEnabled(true);
                tv2.setEnabled(true);
                break;
            case 1:  // audio 永远 close
                tv1.setEnabled(false);
                isListen = false;
                tv1.setImageResource(R.drawable.sound_off1);
//                tv11.setText("Audio Off");
                tv11.setText(mContext.getResources().getString(R.string.satOff));

                break;
            case 2:  // video 永远 open
                tv2.setEnabled(false);
                isMute = true;
                tv2.setImageResource(R.drawable.cam_on2);
//                tv21.setText("Video On");
                tv21.setText(mContext.getResources().getString(R.string.satOn));
                ll.setVisibility(View.GONE);
                break;
        }

    }

    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.webcam_popup, null);

        tv1 = (ImageView) view.findViewById(R.id.tv1);
        tv2 = (ImageView) view.findViewById(R.id.tv2);
        closebnt = (ImageView) view.findViewById(R.id.closebnt);
        bnt = (TextView) view.findViewById(R.id.bnt);
        tv11 = (TextView) view.findViewById(R.id.tv11);
        tv21 = (TextView) view.findViewById(R.id.tv21);
        ll = (LinearLayout) view.findViewById(R.id.ll);
        isOpenRecord = (CheckBox) view.findViewById(R.id.isOpenRecord);

        systembtn = (TextView) view.findViewById(R.id.systembtn);
        kloudcallbtn = (TextView) view.findViewById(R.id.kloudcallbtn);
        externalbtn = (TextView) view.findViewById(R.id.externalbtn);
        systembtn.setOnClickListener(this);
        kloudcallbtn.setOnClickListener(this);
        externalbtn.setOnClickListener(this);
        closebnt.setOnClickListener(this);


        if (firstStatus.equals("0") && identity == 2) {
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
        }

        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        bnt.setOnClickListener(this);

        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
//        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                webCamPopupListener.dismiss();
//            }
//        });
//        mPopupWindow.setFocusable(false);
//        mPopupWindow.setOutsideTouchable(false);
//        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        mPopupWindow.update();
    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
//            webCamPopupListener.open();
            mPopupWindow.show();
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    public interface WebCamPopupListener {
        void start(boolean isListen, boolean isMute, boolean isRecord);

        void changeOptions(int position);

        void dismiss();

        void open();
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv1:
                if (isListen) {
                    isListen = false;
                    tv1.setImageResource(R.drawable.sound_off1);
                    tv11.setText(mContext.getResources().getString(R.string.satOff));
                } else {
                    isListen = true;
                    tv1.setImageResource(R.drawable.sound_on1);
                    tv11.setText(mContext.getResources().getString(R.string.satOn));
                }
                break;
            case R.id.tv2:
                if (isMute) {
                    isMute = false;
                    tv2.setImageResource(R.drawable.cam_off2);
//                    tv21.setText("Video Off");
                    tv21.setText(mContext.getResources().getString(R.string.satOff));
                } else {
                    isMute = true;
                    tv2.setImageResource(R.drawable.cam_on2);
//                    tv21.setText("Video On");
                    tv21.setText(mContext.getResources().getString(R.string.satOn));
                }
                break;
            case R.id.bnt:
                mPopupWindow.dismiss();
                webCamPopupListener.start(isListen, isMute, isOpenRecord.isChecked());
                break;
            case R.id.closebnt:
                mPopupWindow.dismiss();
                break;
            case R.id.systembtn:
                systembtn.setTextColor(mContext.getResources().getColor(R.color.blue));
                kloudcallbtn.setTextColor(mContext.getResources().getColor(R.color.black));
                externalbtn.setTextColor(mContext.getResources().getColor(R.color.black));
                webCamPopupListener.changeOptions(0);
                break;
            case R.id.kloudcallbtn:
                systembtn.setTextColor(mContext.getResources().getColor(R.color.black));
                kloudcallbtn.setTextColor(mContext.getResources().getColor(R.color.blue));
                externalbtn.setTextColor(mContext.getResources().getColor(R.color.black));
                webCamPopupListener.changeOptions(1);
                break;
            case R.id.externalbtn:
                systembtn.setTextColor(mContext.getResources().getColor(R.color.black));
                kloudcallbtn.setTextColor(mContext.getResources().getColor(R.color.black));
                externalbtn.setTextColor(mContext.getResources().getColor(R.color.blue));
                webCamPopupListener.changeOptions(2);

                break;
            default:
                break;
        }
    }


}
