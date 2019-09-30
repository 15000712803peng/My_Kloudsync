package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.adapter.SpaceAdapter;
import com.ub.techexcel.adapter.SpaceAdapter2;
import com.ub.techexcel.bean.SyncRoomBean;

import java.util.ArrayList;
import java.util.List;


public class SyncRoomPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView closebnt;
    private ImageView selectimage;
    private RecyclerView recycleview;
    private List<SyncRoomBean> list = new ArrayList<>();
    private SyncRoomTeamAdapter syncRoomTeamAdapter;
    private TextView teamname;

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
        view = layoutInflater.inflate(R.layout.syncroom_popup, null);
        closebnt = (ImageView) view.findViewById(R.id.closebnt);
        teamname = (TextView) view.findViewById(R.id.teamname);
        closebnt.setOnClickListener(this);
        selectimage = (ImageView) view.findViewById(R.id.selectimage);
        selectimage.setOnClickListener(this);
        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new GridLayoutManager(mContext, 3));

        spaceRecycleView = (RecyclerView) view.findViewById(R.id.spacerecycleview);
        spaceRecycleView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));


        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                webCamPopupListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.anination3);

    }

    private View view2;

    @SuppressLint("NewApi")
    public void StartPop(View v) {
        view2 = v;
        if (mPopupWindow != null) {
            webCamPopupListener.open();
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

        void changeOptions(SyncRoomBean syncRoomBean, TeamSpaceBean teamSpaceBean, int spaceid);

        void dismiss();

        void open();
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;

    private int teamID = -1;
    private TeamSpaceBean teamSpaceBean2;


    private SpaceAdapter2 spaceAdapter;
    private List<TeamSpaceBean> spacesList = new ArrayList<>();
    private RecyclerView spaceRecycleView;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closebnt:
                mPopupWindow.dismiss();
                break;
            case R.id.selectimage:
                SwitchTeamPopup switchTeamPopup = new SwitchTeamPopup();
                switchTeamPopup.getPopwindow(mContext);
                switchTeamPopup.setWebCamPopupListener(new SwitchTeamPopup.WebCamPopupListener() {
                    @Override
                    public void select(TeamSpaceBean teamSpaceBean) {   //拿space

                        teamID = teamSpaceBean.getItemID();
                        teamname.setText(teamSpaceBean.getName());
                        teamSpaceBean2 = teamSpaceBean;

                        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamID,
                                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                                    @Override
                                    public void getServiceReturnData(Object object) {
                                        spaceRecycleView.setVisibility(View.VISIBLE);
                                        recycleview.setVisibility(View.GONE);

                                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                                        spacesList.clear();
                                        spacesList.addAll(list);
                                        spaceAdapter = new SpaceAdapter2(mContext, spacesList, true);
                                        spaceAdapter.setOnItemLectureListener(new SpaceAdapter2.OnItemLectureListener() {
                                            @Override
                                            public void onItem(TeamSpaceBean teamSpaceBean) {
                                                onItem2(teamSpaceBean);
                                            }
                                        });
                                        spaceRecycleView.setAdapter(spaceAdapter);
                                    }
                                });


                    }
                });
                switchTeamPopup.StartPop(view2, teamID);
                break;
            default:
                break;
        }
    }

    private int spaceid = -1;

    public void onItem2(TeamSpaceBean teamSpaceBean) { //space

        teamname.setText(teamSpaceBean2.getName() + "/" + teamSpaceBean.getName());
        spaceid = teamSpaceBean.getItemID();
        TeamSpaceInterfaceTools.getinstance().getSyncRoomList(AppConfig.URL_PUBLIC + "SyncRoom/List?companyID=" + AppConfig.SchoolID + "&teamID=" + teamID + "&spaceID=" + teamSpaceBean.getItemID(),
                TeamSpaceInterfaceTools.GETSYNCROOMLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        spaceRecycleView.setVisibility(View.GONE);
                        recycleview.setVisibility(View.VISIBLE);
                        list.clear();
                        SyncRoomBean syncRoomBean = new SyncRoomBean();
                        syncRoomBean.setName("Create new");
                        syncRoomBean.setItemID(-1);
                        list.add(syncRoomBean);
                        List<SyncRoomBean> syncRoomBeanList = (List<SyncRoomBean>) object;
                        list.addAll(syncRoomBeanList);
                        syncRoomTeamAdapter = new SyncRoomTeamAdapter(mContext, list);
                        recycleview.setAdapter(syncRoomTeamAdapter);
                    }
                });
    }


    public class SyncRoomTeamAdapter extends RecyclerView.Adapter<SyncRoomTeamAdapter.RecycleHolder2> {

        private Context context;

        private List<SyncRoomBean> list = new ArrayList<>();

        public SyncRoomTeamAdapter(Context context, List<SyncRoomBean> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecycleHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.syncroom_popp_item, parent, false);
            RecycleHolder2 holder = new RecycleHolder2(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecycleHolder2 holder, int position) {
            final SyncRoomBean syncRoomBean = list.get(position);

            holder.title.setText(syncRoomBean.getName());
            if (!TextUtils.isEmpty(syncRoomBean.getName())) {
                holder.tv1.setText(syncRoomBean.getName().substring(0, 1));
            }
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webCamPopupListener.changeOptions(syncRoomBean, teamSpaceBean2, spaceid);
                }
            });

            if (syncRoomBean.getItemID() == -1) {
                holder.im1.setVisibility(View.VISIBLE);
                holder.tv1.setVisibility(View.GONE);
            } else {
                holder.im1.setVisibility(View.GONE);
                holder.tv1.setVisibility(View.VISIBLE);
            }

            int ii = position % 3;
            if (ii == 1) {
                holder.tv1.setBackgroundColor(Color.parseColor("#34AA44"));
            } else if (ii == 2) {
                holder.tv1.setBackgroundColor(Color.parseColor("#1665D8"));
            } else {
                holder.tv1.setBackgroundColor(Color.parseColor("#F6AB2F"));
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RecycleHolder2 extends RecyclerView.ViewHolder {
            TextView title;
            TextView tv1;
            ImageView im1;
            LinearLayout ll;

            public RecycleHolder2(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.tv11);
                tv1 = (TextView) itemView.findViewById(R.id.tv1);
                im1 = (ImageView) itemView.findViewById(R.id.img1);
                ll = (LinearLayout) itemView.findViewById(R.id.ll);
            }
        }
    }


}
