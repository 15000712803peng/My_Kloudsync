package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.TvDeviceAdapter;
import com.kloudsync.techexcel.bean.TvDevice;
import com.kloudsync.techexcel.view.UISwitchButton;

import java.util.List;


public class TvDevicesListPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private RecyclerView deviceList;
    private TextView scantv;
    private TvDeviceAdapter adapter;
    private LinearLayout devicesLayout;
    private TextView noDeviceText;
    private UISwitchButton isChangeStatus;
    private  LinearLayout nodataprompt;
    private RelativeLayout hah1,hah3;
    private TextView hah2;

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

        devicesLayout = (LinearLayout) view.findViewById(R.id.layout_devices);
        noDeviceText = (TextView) view.findViewById(R.id.txt_no_devices);
        isChangeStatus = (UISwitchButton) view.findViewById(R.id.switch_sync);
        nodataprompt = view.findViewById(R.id.nodataprompt);
        hah1 = view.findViewById(R.id.hah1);
        hah2= view.findViewById(R.id.hah2);
        hah3 = view.findViewById(R.id.hah3);


        ImageView back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(this);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.RIGHT);
        View root = ((Activity) mContext).getWindow().getDecorView();
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        params.height = root.getMeasuredHeight();
        mPopupWindow.getWindow().setAttributes(params);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.getWindow().setWindowAnimations(R.style.anination3);
    }


    @SuppressLint("NewApi")
    public void StartPop(View v, List<TvDevice> devices, boolean enable, boolean isbeike) {
        if (mPopupWindow != null) {
            getBindTvs(devices, enable, isbeike);
            isChangeStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    webCamPopupListener.changeBindStatus(isChecked);
                }
            });
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

    public void setTvDevices(List<TvDevice> devices) {
        if (adapter != null) {
            adapter.setDevices(devices);
        }
    }

    public void Hidden() {
        nodataprompt.setVisibility(View.GONE);
        hah1.setVisibility(View.VISIBLE);
        hah2.setVisibility(View.VISIBLE);
        hah3.setVisibility(View.VISIBLE);
    }

    public void setVisible() {
        nodataprompt.setVisibility(View.VISIBLE);
        hah1.setVisibility(View.GONE);
        hah2.setVisibility(View.GONE);
        hah3.setVisibility(View.GONE);
    }


    public interface WebCamPopupListener {
        void scanTv();

        void changeBindStatus(boolean isCheck);

        void openTransfer(TvDevice tvDevice);

        void closeTransfer(TvDevice tvDevice);

        void logout(TvDevice tvDevice);

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

    private void getBindTvs(List<TvDevice> devices, boolean enable, boolean isbeike) {
        isChangeStatus.setChecked(enable);
        if (devices != null && devices.size() > 0) {
            devicesLayout.setVisibility(View.VISIBLE);
            noDeviceText.setVisibility(View.GONE);
            adapter = new TvDeviceAdapter(mContext, devices, isbeike);
            deviceList.setAdapter(adapter);
            adapter.setOnItemClickListener(new TvDeviceAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void openTransfer(TvDevice tvDevice) {
                    webCamPopupListener.openTransfer(tvDevice);

                }

                @Override
                public void closeTransfer(TvDevice tvDevice) {
                    webCamPopupListener.closeTransfer(tvDevice);
                }

                @Override
                public void logout(TvDevice tvDevice) {
                    webCamPopupListener.logout(tvDevice);
                }
            });
        } else {
            devicesLayout.setVisibility(View.GONE);
            noDeviceText.setVisibility(View.VISIBLE);
        }
    }


}
