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
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.view.CircleImageView;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.bean.SyncRoomMember;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class AudienceMemberPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private RecyclerView recycleview;
    private AudienceListAdapter audienceListAdapter;
    public ImageLoader imageLoader;

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
        view = layoutInflater.inflate(R.layout.audience_member_popup, null);

        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        ImageView back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(this);


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
    private String meetingId;

    @SuppressLint("NewApi")
    public void StartPop(View v, String meetingId) {
        this.outView = v;
        this.meetingId = meetingId;
        if (mPopupWindow != null) {
            webCamPopupListener.open();
            mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
            getAllMembers();
        }
    }

    private final void getAllMembers() {
        TeamSpaceInterfaceTools.getinstance().getAudienceList(AppConfig.URL_PUBLIC_AUDIENCE + "MeetingServer/member/audience_list?meetingId=" + meetingId,
                TeamSpaceInterfaceTools.GETAUDIENCELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<Customer> list = new ArrayList<>();
                        list.addAll((Collection<? extends Customer>) object);
                        audienceListAdapter = new AudienceListAdapter(mContext, list);
                        recycleview.setAdapter(audienceListAdapter);
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
            default:
                break;
        }
    }


    public class AudienceListAdapter extends RecyclerView.Adapter<AudienceListAdapter.RecycleHolder2> {

        private Context context;

        private List<Customer> list = new ArrayList<>();

        public AudienceListAdapter(Context context, List<Customer> list) {
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
            final Customer customer = list.get(position);

            holder.name.setText(customer.getName());
            String url = customer.getUrl();
            if (null == url || url.length() < 1) {
                holder.icon.setImageResource(R.drawable.hello);
            } else {
                imageLoader.DisplayImage(url, holder.icon);
            }
            if (customer.isOnline()) {
                holder.name.setTextColor(mContext.getResources().getColor(R.color.black));
            } else {
                holder.name.setTextColor(mContext.getResources().getColor(R.color.darkgrey));
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
