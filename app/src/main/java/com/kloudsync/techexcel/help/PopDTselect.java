package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.adapter.TeamAdapter;

import java.util.ArrayList;
import java.util.List;

public class PopDTselect {

    public Context mContext;
    private int height;

    private static PopDTselectDismissListener popDTselectDismissListener;

    public interface PopDTselectDismissListener {
        void PopDismiss(TeamSpaceBean teamSpaceBean);
    }

    public void setPoPDismissListener(PopDTselectDismissListener popDTselectDismissListener) {
        this.popDTselectDismissListener = popDTselectDismissListener;
    }

    public void getPopwindow(Context context, TeamSpaceBean tb) {
        this.mContext = context;
        this.tb = tb;

        height = mContext.getResources().getDisplayMetrics().heightPixels;

        getPopupWindowInstance();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation4);
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

    private RecyclerView mTeamRecyclerView;
    private TeamAdapter mTeamAdapter;
    private List<TeamSpaceBean> mCurrentTeamData = new ArrayList<>();
    TeamSpaceBean tb = new TeamSpaceBean();

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_dt, null);

        mTeamRecyclerView = (RecyclerView) popupWindow.findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mPopupWindow = new PopupWindow(popupWindow, ConstraintLayout.LayoutParams.MATCH_PARENT,
                height * 2 / 5, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popDTselectDismissListener != null) {
                    popDTselectDismissListener.PopDismiss(tb);
                }
            }
        });
        getAllTeamList();



        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }



    public void getAllTeamList() {
        mTeamAdapter = new TeamAdapter(mContext, mCurrentTeamData);
        mTeamRecyclerView.setAdapter(mTeamAdapter);
        mTeamAdapter.setOnItemLectureListener(new TeamAdapter.OnItemLectureListener() {
            @Override
            public void onItem(TeamSpaceBean teamSpaceBean) {
                tb = teamSpaceBean;
                for (int i = 0; i < mCurrentTeamData.size(); i++) {
                    TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
                    if (teamSpaceBean1.getItemID() == teamSpaceBean.getItemID()) {
                        teamSpaceBean1.setSelect(true);
                    } else {
                        teamSpaceBean1.setSelect(false);
                    }
                }
                mTeamAdapter.notifyDataSetChanged();
                mPopupWindow.dismiss();
            }
        });
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=1&parentID=0",
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        Log.e("ddddddd", list.size() + "");
                        mCurrentTeamData.clear();
                        mCurrentTeamData.addAll(list);
                        for (int i = 0; i < mCurrentTeamData.size(); i++) {
                            TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
                            if (teamSpaceBean1.getItemID() == tb.getItemID()) {
                                teamSpaceBean1.setSelect(true);
                            } else {
                                teamSpaceBean1.setSelect(false);
                            }
                        }
                        mTeamAdapter.notifyDataSetChanged();
                    }
                });

    }


    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                default:
                    break;
            }

        }


    }


    public void StartPop(View v) {
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }


}
