package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.GroupAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.bean.SyncRoomBean;

import java.util.ArrayList;
import java.util.List;


public class CreateSyncRoomPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView closebnt;
    private ImageView selectimage;
    private TextView teamname;
    private EditText inputsyncroomname;
    private TextView createtv;

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
        view = layoutInflater.inflate(R.layout.createnewsyncroompopup, null);
        closebnt = (ImageView) view.findViewById(R.id.closebnt);
        teamname = (TextView) view.findViewById(R.id.teamname);
        createtv = (TextView) view.findViewById(R.id.createtv);
        lv_group = (ListView) view.findViewById(R.id.listview);
        createtv.setText("Create new SyncRoom & share");
        inputsyncroomname = (EditText) view.findViewById(R.id.inputsyncroomname);
        closebnt.setOnClickListener(this);
        selectimage = (ImageView) view.findViewById(R.id.selectimage);
        selectimage.setOnClickListener(this);
        createtv.setOnClickListener(this);

        GetJiuCai();

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

    private ArrayList<Customer> mlist = new ArrayList<Customer>();
    private GroupAdapter gadapter;
    private ListView lv_group;

    private void GetJiuCai() {
        LoginGet loginget = new LoginGet();
        loginget.setLoginGetListener(new LoginGet.LoginGetListener() {

            @Override
            public void getMember(ArrayList<Customer> list) {
            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                mlist = new ArrayList<Customer>();
                mlist.addAll(list);
                gadapter = new GroupAdapter(mContext, mlist);
                lv_group.setAdapter(gadapter);
                lv_group.setOnItemClickListener(new myOnItem());

            }
        });
        loginget.CustomerRequest(mContext);
    }

    private class myOnItem implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            gadapter.SetSelected(true);
            Customer cus;
            cus = mlist.get(position);
            if (!cus.isHasSelected()) {
                if (cus.isSelected()) {
                    mlist.get(position).setSelected(false);
                } else {
                    mlist.get(position).setSelected(true);
                }
            }
            gadapter.updateListView(mlist);
        }

    }

    private View view2;
    private String attachmentid2;
    int spaceid;

    @SuppressLint("NewApi")
    public void StartPop(View v, TeamSpaceBean teamSpaceBean, int spaceid, String attachmentid) {
        view2 = v;
        attachmentid2 = attachmentid;
        this.spaceid = spaceid;

        if (mPopupWindow != null) {
            if (teamSpaceBean != null) {
                teamSpaceBean2 = teamSpaceBean;
                teamname.setText(teamSpaceBean.getName());
            }
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


    private TeamSpaceBean teamSpaceBean2 = new TeamSpaceBean();

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
                    public void select(TeamSpaceBean teamSpaceBean) {
                        teamname.setText(teamSpaceBean.getName());
                        teamSpaceBean2 = teamSpaceBean;
                    }
                });
                switchTeamPopup.StartPop(view2, teamSpaceBean2.getItemID());
                break;
            case R.id.createtv:
                createnewsyncroom();
                break;
            default:
                break;
        }
    }

    private void createnewsyncroom() {
        if (teamSpaceBean2 == null || teamSpaceBean2.getItemID() == 0) {
            Toast.makeText(mContext, "please select team first", Toast.LENGTH_LONG).show();
            return;
        }
        TeamSpaceInterfaceTools.getinstance().createSyncRoom(AppConfig.URL_PUBLIC + "SyncRoom/CreateSyncRoom",
                TeamSpaceInterfaceTools.CREATESYNCROOM, AppConfig.SchoolID, teamSpaceBean2.getItemID(), spaceid, inputsyncroomname.getText().toString(),
                attachmentid2, "", mlist, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        final int syncroomId = (int) object;
                        TeamSpaceInterfaceTools.getinstance().getSyncRoomList(AppConfig.URL_PUBLIC + "SyncRoom/List?companyID=" + AppConfig.SchoolID + "&teamID=" + teamSpaceBean2.getItemID() + "&spaceID=" + spaceid,
                                TeamSpaceInterfaceTools.GETSYNCROOMLIST, new TeamSpaceInterfaceListener() {
                                    @Override
                                    public void getServiceReturnData(Object object) {
                                        List<SyncRoomBean> syncRoomBeanList = (List<SyncRoomBean>) object;
                                        for (int i = 0; i < syncRoomBeanList.size(); i++) {
                                            if (syncRoomBeanList.get(i).getItemID() == syncroomId) {
                                                webCamPopupListener.enter(syncRoomBeanList.get(i));
                                                dismiss();
                                                break;
                                            }
                                        }
                                    }
                                });
                    }
                });
    }


    public interface WebCamPopupListener {
        void enter(SyncRoomBean syncRoomBean);
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;


}
