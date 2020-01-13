package com.kloudsync.techexcel.frgment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.ContactAdapter;
import com.kloudsync.techexcel.bean.EventSearchContact;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.contact.ContactMap;
import com.kloudsync.techexcel.contact.MyFriendsActivity;
import com.kloudsync.techexcel.docment.InviteNewActivity;
import com.kloudsync.techexcel.help.ContactHelpInterface;
import com.kloudsync.techexcel.help.PopContactHAHA;
import com.kloudsync.techexcel.help.SideBar;
import com.kloudsync.techexcel.help.SideBar.OnTouchingLetterChangedListener;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.search.ui.ContactSearchActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.friends.activity.NewFriendsActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.service.activity.NotifyActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

public class ContactFragment extends Fragment implements ContactHelpInterface {

    private View view;
    private TextView tv_ns;
    private TextView tv_red;
    private TextView tv_title;
    private ImageView img_addCustomer;
    private ImageView img_add;
    private ImageView img_notice;
    private ListView list;
    private LinearLayout lin_new;
    private LinearLayout lin_none;
    private LinearLayout lin_my_friend;
    private SideBar sidebar;
    private ContactAdapter cAdapter;
    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    private ArrayList<Customer> sclist = new ArrayList<Customer>();
    private boolean isFirst = true;
    private boolean isCustomer = true;
    private BroadcastReceiver broadcastReceiver;
    public PopupWindow mPopupWindow;
    private boolean isFragmentVisible = false;
    private boolean flagRf;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.contact_fragment, container, false);
            initView();
        }

        return view;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }


    private void initView() {
        Fresco.initialize(getActivity());
        img_addCustomer = (ImageView) view.findViewById(R.id.img_addCustomer);
        img_add = (ImageView) view.findViewById(R.id.img_add);
        img_notice = (ImageView) view.findViewById(R.id.img_notice);
        tv_ns = (TextView) view.findViewById(R.id.tv_ns);
        tv_red = (TextView) view.findViewById(R.id.tv_red);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        lin_none = (LinearLayout) view.findViewById(R.id.lin_none);
        sidebar = (SideBar) view.findViewById(R.id.sidebar);
        list = (ListView) view.findViewById(R.id.lv_contact);
        list.addHeaderView(initHeader());
        cAdapter = new ContactAdapter(getActivity(), cuslist);
        list.setAdapter(cAdapter);
        list.setOnItemClickListener(new MyOnitem());

    }

    private View initHeader() {
        View header = getActivity().getLayoutInflater().inflate(R.layout.contact_header, null);
        lin_my_friend = (LinearLayout) header.findViewById(R.id.lin_my_friend);
        lin_my_friend.setOnClickListener(new myOnClick());
        lin_new = (LinearLayout) header.findViewById(R.id.lin_new);
        lin_new.setOnClickListener(new myOnClick());
        return header;
    }

    LocalBroadcastManager localBroadcastManager;

    private void GetCourseBroad() {
        RefreshNotify();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RefreshNotify();
            }
        };
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.Receive_Course));
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    private void RefreshNotify() {
        int sum = 0;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (!AppConfig.progressCourse.get(i).isStatus()) {
                sum++;
            }
        }
        tv_ns.setText(sum + "");
        tv_ns.setVisibility(sum == 0 ? View.GONE : View.VISIBLE);
    }

    private void getData() {
        final LoginGet loginget = new LoginGet();

        loginget.setSchoolContactListener(new LoginGet.SchoolContactListener() {
            @Override
            public void getContact(ArrayList<Customer> list) {
                cuslist = new ArrayList<Customer>();
                cuslist.addAll(list);
                cAdapter.SortCustomers(cuslist);
                cAdapter.updateListView2(cuslist);
                VisibleGoneList(cuslist);
            }
        });
        loginget.setTeamSpaceGetListener(new LoginGet.TeamSpaceGetListener() {
            @Override
            public void getTS(ArrayList<Customer> list) {
                cuslist.addAll(0, list);
                cAdapter.updateListView2(cuslist);
                VisibleGoneList(cuslist);

            }
        });
        loginget.GetSchoolContact(getActivity());

    }

    private void getSide() {
        sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position;
                position = SideBarSortHelp.getPositionForSection(cuslist,
                        s.charAt(0));

                if (position != -1) {
                    list.setSelection(position);
                } else {
                    list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }

            }
        });

    }


    @Override
    public void RefreshRed(boolean flag_r) {
        tv_red.setVisibility(flag_r ? View.VISIBLE : View.GONE);
    }

    private class MyOnitem implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            if (position == 0) {
                return;
            }
            Customer cus = isCustomer ? cuslist.get(position - 1) : sclist
                    .get(position - 1);
            if (cus.isTeam()) {
            } else {
                if (cus.isEnableChat()) {
                    AppConfig.Name = cus.getName();
                    AppConfig.isUpdateDialogue = true;
                    RongIM.getInstance().startPrivateChat(getActivity(),
                            cus.getUBAOUserID(), cus.getName());
                }
            }

        }

    }

    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_addCustomer:
                    GoToInviteNew();
                    break;
                case R.id.lin_new:
                    AddNewUM();
                    break;
                case R.id.lin_my_friend:
                    Intent i = new Intent(getActivity(), MyFriendsActivity.class);
                    startActivity(i);
                    break;
                case R.id.img_add:
                    ShowPop();
                    break;
                case R.id.img_notice:
                    GoToNotice();
                    break;
                default:
                    break;
            }
        }

        private void GoToNotice() {
            startActivity(new Intent(getActivity(), NotifyActivity.class));
        }


        private void AddNewUM() {
            Intent i = new Intent(getActivity(), NewFriendsActivity.class);
            i.putExtra("currentposition", isCustomer ? 0 : 1);
            startActivity(i);
        }

    }

    private void GoToInviteNew() {
        Intent intent = new Intent(getActivity(), InviteNewActivity.class);
        intent.putExtra("flag_c", true);
        startActivity(intent);
    }

    private void ShowPop() {
        PopContactHAHA haha = new PopContactHAHA();
        haha.getPopwindow(getActivity());
        haha.StartPop(img_add);
    }

    private void VisibleGoneList(ArrayList<Customer> list) {
        // TODO Auto-generated method stub
        if (0 == list.size()) {
            lin_none.setVisibility(View.VISIBLE);
            this.list.setVisibility(View.GONE);
            sidebar.setVisibility(View.GONE);
        } else {
            lin_none.setVisibility(View.GONE);
            this.list.setVisibility(View.VISIBLE);
            sidebar.setVisibility(View.VISIBLE);
        }

    }

    public void SideVG(boolean isShow) {
        sidebar.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    int SchoolIds;

    private void GetSchoolInfo() {
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                getActivity().MODE_PRIVATE);
        int SchoolId = sharedPreferences.getInt("SchoolID", -1);
        String schoolName = sharedPreferences.getString("SchoolName", null);
        if (SchoolIds != SchoolId && SchoolIds != 0) {
            getData();
        }
        SchoolIds = SchoolId;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisible = isVisibleToUser;
        if (isFirst && isVisibleToUser) {
            isFirst = false;
            initFunction();
        }
        RefreshInfo();
    }

    private void RefreshInfo() {

        if (AppConfig.isUpdateCustomer) {
            getData();
        }
        AppConfig.isUpdateCustomer = false;
    }

    private void initFunction() {
        EventBus.getDefault().register(this);
        GetSchoolInfo();
        getData();
        getSide();
        GetCourseBroad();
        img_addCustomer.setOnClickListener(new myOnClick());
        img_add.setOnClickListener(new myOnClick());
        img_notice.setOnClickListener(new myOnClick());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(TeamSpaceBean teamSpaceBean) {
        flagRf = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goToSearch(EventSearchContact eventSearchContact) {
        Intent intent = new Intent(getActivity(), ContactSearchActivity.class);
        Bundle bundle = new Bundle();
        if (cuslist != null && cuslist.size() > 0) {
            bundle.putSerializable("customer_list", (Serializable) cuslist);
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isFragmentVisible) {
            RefreshInfo();
        }
        if (tv_title != null) {
            GetSchoolInfo();
        }
        if (flagRf) {
            getData();
            flagRf = false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null && getActivity() != null) {
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
