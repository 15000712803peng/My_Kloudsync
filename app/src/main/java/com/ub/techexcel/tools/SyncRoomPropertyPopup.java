package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.bean.SyncRoomBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class SyncRoomPropertyPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    private TextView cancel, save;
    private EditText edittext;
    private RelativeLayout selectpurpose;
    private TextView customerservicevalue;

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
        view = layoutInflater.inflate(R.layout.syncroom_property_popup, null);

        cancel = (TextView) view.findViewById(R.id.cancel);
        edittext = (EditText) view.findViewById(R.id.edittext);
        cancel.setOnClickListener(this);
        save = (TextView) view.findViewById(R.id.save);
        save.setOnClickListener(this);
        customerservicevalue = (TextView) view.findViewById(R.id.customerservicevalue);
        selectpurpose = (RelativeLayout) view.findViewById(R.id.selectpurpose);
        selectpurpose.setOnClickListener(this);


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

    private View outView;

    private String topicId;
    private String topicName;

    @SuppressLint("NewApi")
    public void StartPop(View v, String lessonId, String syncRoomname) {
        outView = v;
        topicId = lessonId;
        topicName=syncRoomname;
        edittext.setText(syncRoomname);
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
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

        void changeOptions(SyncRoomBean syncRoomBean, TeamSpaceBean teamSpaceBean);

    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.save:
                updateTeamTopic();
                break;
            case R.id.selectpurpose:
                openTeamTypePopup();
                break;
            default:
                break;
        }
    }

    private void updateTeamTopic() {
        TeamSpaceInterfaceTools.getinstance().updateTeamTopic(AppConfig.URL_PUBLIC + "Topic/UpdateTeamTopic", TeamSpaceInterfaceTools.UPDATETEAMTOPIC, topicId
                , edittext.getText().toString(), teamType, "", new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        dismiss();
                        EventBus.getDefault().post(new TeamSpaceBean());
                    }
                });
    }


    private int teamType = 0;

    private void openTeamTypePopup() {
        NewSyncRoomTypePopup newSyncRoomTypePopup = new NewSyncRoomTypePopup();
        newSyncRoomTypePopup.getPopwindow(mContext);
        newSyncRoomTypePopup.setFavoritePoPListener(new NewSyncRoomTypePopup.FavoritePoPListener() {
            @Override
            public void select(int type) {
                teamType = type;
                if (type == 1) {
                    customerservicevalue.setText("Customer Service");
                } else if (type == 2) {
                    customerservicevalue.setText("Customer Requirement Discussion");
                } else if (type == 3) {
                    customerservicevalue.setText("Customer Leads");
                }
            }

            @Override
            public void dismiss() {
            }

            @Override
            public void open() {
            }

        });
        newSyncRoomTypePopup.StartPop(outView, teamType,true);
    }

}
