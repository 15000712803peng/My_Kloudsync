package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.TvDeviceAdapter;
import com.kloudsync.techexcel.bean.TvDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.response.DevicesResponse;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.view.CircleImageView;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.bean.SyncRoomMember;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TvDevicesListPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private RecyclerView deviceList;
    private TextView scantv;
    private TvDeviceAdapter adapter;
    private ArrayList<TvDevice> mlist = new ArrayList();
    private LinearLayout devicesLayout;
    private TextView noDeviceText;
    private CheckBox isChangeStatus;

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
        view = layoutInflater.inflate(R.layout.tv_device_popup, null);
        scantv = (TextView) view.findViewById(R.id.scantv);
        scantv.setOnClickListener(this);

        deviceList = (RecyclerView) view.findViewById(R.id.list_device);
        final LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        deviceList.setLayoutManager(manager);
        adapter = new TvDeviceAdapter(mlist);
        deviceList.setAdapter(adapter);
        devicesLayout = (LinearLayout) view.findViewById(R.id.layout_devices);
        noDeviceText = (TextView) view.findViewById(R.id.txt_no_devices);
        isChangeStatus = (CheckBox) view.findViewById(R.id.isPublic);
        isChangeStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("Tvdeviceslist", isChecked + "");
                webCamPopupListener.changeBindStatus(isChecked);
            }
        });

        ImageView back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(this);

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.anination3);
    }


    @SuppressLint("NewApi")
    public void StartPop(View v, List<TvDevice> devices, boolean enable) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
            getBindTvs(devices, enable);
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
        void scanTv();

        void changeBindStatus(boolean isCheck);
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                mPopupWindow.dismiss();
                break;
            case R.id.scantv:
                webCamPopupListener.scanTv();
                break;
            default:
                break;
        }
    }


    private void getBindTvs(List<TvDevice> devices, boolean enable) {
        isChangeStatus.setChecked(enable);
        if (devices != null && devices.size() > 0) {
            devicesLayout.setVisibility(View.VISIBLE);
            noDeviceText.setVisibility(View.GONE);
            adapter.setDevices(devices);
        } else {
            devicesLayout.setVisibility(View.GONE);
            noDeviceText.setVisibility(View.VISIBLE);
        }
    }


}
