package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.LruCache;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.WeiXinApi;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.ub.techexcel.adapter.RecordingAdapter;
import com.ub.techexcel.adapter.YinXiangAdapter;
import com.ub.techexcel.adapter.YinXiangAdapter2;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.RecordingBean;
import com.ub.techexcel.bean.SoundtrackBean;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class RecordingPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private ImageView close;
    private RecyclerView recycleview;


    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void playYinxiang(RecordingBean soundtrackBean);
    }

    private FavoritePoPListener favoritePoPListener;

    public void setFavoritePoPListener(FavoritePoPListener favoritePoPListener) {
        this.favoritePoPListener = favoritePoPListener;
    }


    public void getPopwindow(Context context) {
        this.mContext = context;
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


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.recording_popup, null);
        close = (ImageView) view.findViewById(R.id.close);
        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

        close.setOnClickListener(this);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.RIGHT);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
//        DisplayMetrics dm = new DisplayMetrics();
//        (((Activity)mContext).getWindowManager()).getDefaultDisplay().getRealMetrics(dm);
        View root = ((Activity) mContext).getWindow().getDecorView();
        params.height = root.getMeasuredHeight();
        mPopupWindow.getWindow().setAttributes(params);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.getWindow().setWindowAnimations(R.style.anination3);

    }


    @SuppressLint("NewApi")
    public void StartPop(View v, String lessonId) {
        if (mPopupWindow != null) {
            mPopupWindow.show();
            String recordListurl = AppConfig.URL_PUBLIC_AUDIENCE + "MeetingServer/recording/recording_list?lessonId=" + lessonId;
            ServiceInterfaceTools.getinstance().getRecordingList(recordListurl, ServiceInterfaceTools.GETRECORDINGLIST, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    List<RecordingBean> recordingBeanList = new ArrayList<>();
                    recordingBeanList.addAll((List<RecordingBean>) object);
                    RecordingAdapter recordingAdapter = new RecordingAdapter(mContext, recordingBeanList);
                    recycleview.setAdapter(recordingAdapter);
                    recordingAdapter.setFavoritePoPListener(new RecordingAdapter.FavoritePoPListener2() {
                        @Override
                        public void playYinxiang(RecordingBean recordingBean) {
                            dismiss();
                            favoritePoPListener.playYinxiang(recordingBean);
                        }
                    });
                }
            });
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;
            default:
                break;
        }
    }

}
