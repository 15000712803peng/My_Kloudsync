package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.adapter.TeamAdapter;
import com.ub.techexcel.bean.SyncRoomBean;

import java.util.ArrayList;
import java.util.List;


public class SwitchTeamPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView closebnt;
    private RecyclerView recycleview;
    private TeamAdapter mTeamAdapter;
    private List<TeamSpaceBean> mCurrentTeamData = new ArrayList<>();


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

    public interface WebCamPopupListener {
        void select(TeamSpaceBean teamSpaceBean);
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;


    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.switchteampopup, null);
        closebnt = (ImageView) view.findViewById(R.id.closebnt);
        closebnt.setOnClickListener(this);

        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

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


        mTeamAdapter = new TeamAdapter(mContext, mCurrentTeamData);
        mTeamAdapter.setOnItemLectureListener(new TeamAdapter.OnItemLectureListener() {
            @Override
            public void onItem(TeamSpaceBean teamSpaceBean) {
                for (int i = 0; i < mCurrentTeamData.size(); i++) {
                    TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
                    if (teamSpaceBean1.getItemID() == teamSpaceBean.getItemID()) {
                        teamSpaceBean1.setSelect(true);
                    } else {
                        teamSpaceBean1.setSelect(false);
                    }
                }
                mTeamAdapter.notifyDataSetChanged();
                webCamPopupListener.select(teamSpaceBean);
                dismiss();
            }
        });
        recycleview.setAdapter(mTeamAdapter);

    }


    @SuppressLint("NewApi")
    public void StartPop(View v, final int itemid) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
            TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=1&parentID=0",
                    TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                            mCurrentTeamData.clear();
                            mCurrentTeamData.addAll(list);
                            for (int i = 0; i < mCurrentTeamData.size(); i++) {
                                TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
                                if (teamSpaceBean1.getItemID() == itemid) {
                                    teamSpaceBean1.setSelect(true);
                                } else {
                                    teamSpaceBean1.setSelect(false);
                                }
                            }
                            mTeamAdapter.notifyDataSetChanged();
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
            case R.id.closebnt:
                mPopupWindow.dismiss();
                break;
            default:
                break;
        }
    }


}
