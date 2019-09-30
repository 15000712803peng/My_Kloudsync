package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.view.CircleImageView;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.bean.SyncRoomMember;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SyncRoomMemberPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private RecyclerView recycleview;
    private SyncRoomTeamAdapter syncRoomTeamAdapter;
    public ImageLoader imageLoader;
    private CircleImageView iconciv;
    private TextView ownername;
    private LinearLayout morell;
    private RelativeLayout invitenew;
    private ImageView invitenewimg;
    private RelativeLayout selectfromcontact;

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        imageLoader = new ImageLoader(context);
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
        view = layoutInflater.inflate(R.layout.syncroom_member_popup, null);

        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        iconciv = (CircleImageView) view.findViewById(R.id.iconciv);
        ownername = (TextView) view.findViewById(R.id.ownername);
        iconciv.setImageResource(R.drawable.hello);
        ImageView back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(this);

        morell = (LinearLayout) view.findViewById(R.id.morell);
        invitenew = (RelativeLayout) view.findViewById(R.id.invitenew);
        invitenewimg = (ImageView) view.findViewById(R.id.invitenewimg);
        invitenewimg.setOnClickListener(this);
        invitenew.setOnClickListener(this);
        selectfromcontact = (RelativeLayout) view.findViewById(R.id.selectfromcontact);
        selectfromcontact.setOnClickListener(this);

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


    private View outView;
    private String topicId;

    @SuppressLint("NewApi")
    public void StartPop(View v, String topicId) {
        this.outView = v;
        this.topicId = topicId;
        if (mPopupWindow != null) {
            webCamPopupListener.open();
            mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
            getAllMembers();
        }
    }

    private final void getAllMembers() {
        TeamSpaceInterfaceTools.getinstance().getMemberList(AppConfig.URL_PUBLIC + "Topic/MemberList?TeamTopicID=" + topicId,
                TeamSpaceInterfaceTools.GETMEMBERLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SyncRoomMember> list = new ArrayList<>();
                        list = (List<SyncRoomMember>) object;
                        List<SyncRoomMember> list2 = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            SyncRoomMember syncRoomMember = list.get(i);
                            if (syncRoomMember.getMemberType() == 2) {
                                ownername.setText(syncRoomMember.getMemberName());
                                String url = syncRoomMember.getMemberAvatar();
                                if (null == url || url.length() < 1) {
                                    iconciv.setImageResource(R.drawable.hello);
                                } else {
                                    imageLoader.DisplayImage(url, iconciv);
                                }
                                list2.add(syncRoomMember);
                            } else {
                                list2.add(syncRoomMember);
                            }
                        }
                        syncRoomTeamAdapter = new SyncRoomTeamAdapter(mContext, list2);
                        recycleview.setAdapter(syncRoomTeamAdapter);
                    }
                });
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

        void inviteNew();

        void dismiss();

        void open();
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;

    String mm = "";

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                mPopupWindow.dismiss();
                break;
            case R.id.invitenew:
                webCamPopupListener.inviteNew();
                dismiss();
                break;
            case R.id.invitenewimg:
                if (morell.getVisibility() == View.VISIBLE) {
                    morell.setVisibility(View.GONE);
                } else {
                    morell.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.selectfromcontact:
                morell.setVisibility(View.GONE);
                InviteOthersPopup inviteOthersPopup = new InviteOthersPopup();
                inviteOthersPopup.getPopwindow(mContext);
                inviteOthersPopup.setFavoritePoPListener(new InviteOthersPopup.FavoritePoPListener() {
                    @Override
                    public void select(List<Customer> list) {
                        mm = "";
                        for (int i = 0; i < list.size(); i++) {
                            Customer customer = list.get(i);
                            if (customer.isSelected()) {
                                mm = mm + customer.getUserID() + ",";
                            }
                        }
                        if (mm.length() > 0) {
                            mm = mm.substring(0, mm.length() - 1);
                        }
                        new ApiTask(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject jsonObject = ConnectService.submitDataByJsonArray(AppConfig.URL_PUBLIC + "Topic/AddMember?CompanyID=" + AppConfig.SchoolID + "" +
                                        "&TeamTopicID=" + topicId + "&MemberType=0&MemberList=" + mm, null);
                                try {
                                    Log.e("fffff", AppConfig.URL_PUBLIC + "Topic/AddMember?CompanyID=" + AppConfig.SchoolID + "" +
                                            "&TeamTopicID=" + topicId + "&MemberType=0&MemberList=" + mm + "  " + jsonObject.toString());
                                    if (jsonObject.getInt("RetCode") == 0) {
                                        getAllMembers();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start(ThreadManager.getManager());

                    }
                });
                inviteOthersPopup.StartPop(outView);
                break;

            default:
                break;
        }
    }


    public class SyncRoomTeamAdapter extends RecyclerView.Adapter<SyncRoomTeamAdapter.RecycleHolder2> {

        private Context context;

        private List<SyncRoomMember> list = new ArrayList<>();

        public SyncRoomTeamAdapter(Context context, List<SyncRoomMember> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecycleHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.syncroom_member_popup_item, parent, false);
            RecycleHolder2 holder = new RecycleHolder2(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecycleHolder2 holder, int position) {
            final SyncRoomMember customer = list.get(position);

            holder.name.setText(customer.getMemberName());
            String url = customer.getMemberAvatar();
            if (null == url || url.length() < 1) {
                holder.icon.setImageResource(R.drawable.hello);
            } else {
                imageLoader.DisplayImage(url, holder.icon);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RecycleHolder2 extends RecyclerView.ViewHolder {
            TextView name;
            CircleImageView icon;

            public RecycleHolder2(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                icon = (CircleImageView) itemView.findViewById(R.id.icon);
            }
        }
    }


}
