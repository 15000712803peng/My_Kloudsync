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
    //    private TextView tv_customer, tv_healthcl, tv_map_mode;
//    private TextView tv_cancel;
    private TextView tv_new;
    private TextView tv_ns;
    private TextView tv_red;
    private TextView tv_title;
    private TextView tv_mfriend;
    //    private TextView tv_myc;
//    private TextView tv_sc;
    private ImageView img_addCustomer;
    private ImageView img_add;
    private ImageView img_notice;
    private ListView list;
    //    private LinearLayout lin_search, lin_edit;
    private LinearLayout lin_new;
    private LinearLayout lin_none;
    private LinearLayout lin_my_friend;
    //    private ViewPager vp_contact;
//    private View v_linc;
    private ClearEditText et_search;
    private SideBar sidebar;
    private ContactAdapter cAdapter;
    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    private ArrayList<Customer> sclist = new ArrayList<Customer>();
    private ArrayList<Customer> healthlist = new ArrayList<Customer>();
    ArrayList<Customer> eList = new ArrayList<Customer>();
    ArrayList<Customer> mList = new ArrayList<Customer>();

    private boolean isFirst = true;
    private boolean isCustomer = true;
//    private int mySelect = 0;

    BroadcastReceiver broadcastReceiver;

    private InputMethodManager inputManager;

    public PopupWindow mPopupWindow;

    private boolean isFragmentVisible = false;

    private boolean flagRf;

    private SharedPreferences sharedPreferences;

    private double lats[] = {31.199105, 31.199344, 31.198661, 31.198437,
            31.199425, 31.198939, 31.199089, 31.198757};
    private double longs[] = {121.438247, 121.437556, 121.437556, 121.438378,
            121.438337, 121.43902, 121.438454, 121.438189};

    private List<Fragment> mTabs = new ArrayList<Fragment>();

    private FragmentPagerAdapter mAdapter;

//    private static ContactInfoInterface cii;
//    private static ContactInfoInterface cii2;

    private int width;

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

    /*@Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if (childFragment.getClass().equals(ListVFragment.class)) {
            cii = (ContactInfoInterface) childFragment;
        }else
        if (childFragment.getClass().equals(ListVsFragment.class)) {
            cii2 = (ContactInfoInterface) childFragment;
        }
    }*/

    private void initView() {
        Fresco.initialize(getActivity());
        img_addCustomer = (ImageView) view.findViewById(R.id.img_addCustomer);
        img_add = (ImageView) view.findViewById(R.id.img_add);
        img_notice = (ImageView) view.findViewById(R.id.img_notice);
//        img_newuser = (ImageView) view.findViewById(R.id.img_newuser);
//        tv_map_mode = (TextView) view.findViewById(tv_map_mode);
//        tv_customer = (TextView) view.findViewById(tv_customer);
//        tv_healthcl = (TextView) view.findViewById(tv_healthcl);
//        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_new = (TextView) view.findViewById(R.id.tv_new);
        tv_ns = (TextView) view.findViewById(R.id.tv_ns);
        tv_red = (TextView) view.findViewById(R.id.tv_red);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_mfriend = (TextView) view.findViewById(R.id.tv_mfriend);
//        tv_myc = (TextView) view.findViewById(R.id.tv_myc);
//        tv_sc = (TextView) view.findViewById(R.id.tv_sc);
//        tv_add = (TextView) view.findViewById(tv_add);
//        lin_search = (LinearLayout) view.findViewById(R.id.lin_search);
//        lin_edit = (LinearLayout) view.findViewById(R.id.lin_edit);

        lin_none = (LinearLayout) view.findViewById(R.id.lin_none);

//        v_linc = view.findViewById(R.id.v_linc);
        et_search = (ClearEditText) view.findViewById(R.id.et_search);
        sidebar = (SideBar) view.findViewById(R.id.sidebar);
        list = (ListView) view.findViewById(R.id.lv_contact);
        list.addHeaderView(initHeader());
//        vp_contact = (ViewPager) view.findViewById(R.id.vp_contact);
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
//        getActivity().registerReceiver(broadcastReceiver, filter);
        //LocalBroadcastManager 是基于Handler实现的，拥有更高的效率与安全性。安全性主要体现在数据仅限于应用内部传输，避免广播被拦截、伪造、篡改的风险
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
                /*if (isCustomer) {
                    if (cii != null) {
                        cii.TouchSide(s);
                    }
                } else {
                    if (cii2 != null) {
                        cii2.TouchSide(s);
                    }
                }*/
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

    private void editCustomers() {
        inputManager = (InputMethodManager) et_search
                .getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        et_search.setHint(getActivity().getResources().getString(R.string.contact));
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /*if (cii != null) {
                    cii.EditSearch(et_search.getText().toString());
                }
                if (cii2 != null) {
                    cii2.EditSearch(et_search.getText().toString());
                }*/
                eList.clear();
                for (int i = 0; i < cuslist.size(); i++) {
                    Customer cus = cuslist.get(i);
                    String name = et_search.getText().toString();
                    String getName = cus.getName().toLowerCase();//转小写
                    String nameb = name.toLowerCase();//转小写
                    if (getName.contains(nameb.toString())
                            && name.length() > 0) {
                        eList.add(cus);
                    }
                }
                if (et_search.length() != 0) {
                    cAdapter.updateListView2(eList);
                } else {
                    cAdapter.updateListView2(cuslist);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

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
//            if (et_search.length() != 0) {
//                cus = eList.get(position);
//            }
            if (cus.isTeam()) {

            } else {
//                Intent intent = new Intent(getActivity(), isCustomer ? UserDetail.class : MemberDetail.class);
                /*Intent intent = new Intent(getActivity(), UserDetail.class);
//			intent.putExtra("Customer", cus);
                intent.putExtra("UserID", cus.getUserID());
                startActivity(intent);*/

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
//                    mPopupWindow.showAsDropDown(v);
                    GoToInviteNew();
                    break;
                /*case R.id.tv_myc:
                    ChangeList(0);
                    break;
                case R.id.tv_sc:
                    ChangeList(1);
                    break;*/
                /*case tv_map_mode:
                    ChangeToMap();
                    break;
                case tv_customer:
                    ChangeList(0);
                    break;
                case tv_healthcl:
                    ChangeList(1);
                    break;*/
                /*case R.id.tv_cancel:
                    CancelSearch();
                    break;*/
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
                /*case R.id.lin_search:
                    GetSearch();
                    break;*/
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

    /*private void CancelSearch() {
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        et_search.setText("");
        lin_search.setVisibility(View.VISIBLE);
        lin_edit.setVisibility(View.GONE);
    }
*/

    public void ChangeToMap() {
        Intent intent = new Intent(getActivity(), ContactMap.class);
        if (isCustomer) {
            intent.putExtra("mList", cuslist);
        } else {
            intent.putExtra("mList", healthlist);
        }
        intent.putExtra("isCustomer", isCustomer);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.tran_in, R.anim.tran_out);


    }

    /*private void GetSearch() {
        et_search.setFocusable(true);
        et_search.setFocusableInTouchMode(true);
        et_search.requestFocus();
        inputManager.showSoftInput(et_search, 0);
        lin_search.setVisibility(View.GONE);
        lin_edit.setVisibility(View.VISIBLE);
    }*/

    /*@SuppressLint("NewApi")
    public void ChangeList(int i) {
        switch (i) {
            case 0:
                isCustomer = true;
                tv_myc.setTextColor(getResources().getColor(R.color.green));
                tv_sc.setTextColor(getResources().getColor(R.color.darkgrey));
                break;
            case 1:
                isCustomer = false;
                tv_myc.setTextColor(getResources().getColor(R.color.darkgrey));
                tv_sc.setTextColor(getResources().getColor(R.color.green));
                break;

            default:
                break;
        }

        if (isCustomer) {
            if (cii != null) {
                cii.SideVg();
            }
        } else {
            if (cii2 != null) {
                cii2.SideVg();
            }
        }
        vp_contact.setCurrentItem(i);

        *//*if (mySelect != i) {

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(v_linc.getLayoutParams());
            lp.width = width /2;
            lp.setMargins((0 == i) ? 0 : width / 2, 0, 0, 0);
            v_linc.setLayoutParams(lp);

            LayoutAnimationController lac = new LayoutAnimationController(
                    AnimationUtils.loadAnimation(getActivity(),
                            0 == i ? R.anim.contact_left : R.anim.contact_right));
            lac.setInterpolator(new AccelerateInterpolator());
            lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
            lv_contact.setLayoutAnimation(lac);
            lv_contact.startLayoutAnimation();
        }*//*
//        mySelect = i;

    }*/

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
        /*if (-1 == SchoolId || SchoolId == AppConfig.SchoolID) {
            tv_title.setText(getResources().getString(R.string.My_School));
        } else {
            tv_title.setText(schoolName);
        }*/
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
            /*if (cii != null) {
                cii.Refresh();
            }
            if (cii2 != null) {
                cii2.Refresh();
            }*/

        }
        AppConfig.isUpdateCustomer = false;
    }

    private void initFunction() {
        EventBus.getDefault().register(this);
        GetSchoolInfo();
        getData();
//        initVP();
//        editCustomers();
        getSide();
        GetCourseBroad();
        img_addCustomer.setOnClickListener(new myOnClick());
//        tv_map_mode.setOnClickListener(new myOnClick());
//        tv_customer.setOnClickListener(new myOnClick());
//        tv_healthcl.setOnClickListener(new myOnClick());
//        tv_cancel.setOnClickListener(new myOnClick());
        img_add.setOnClickListener(new myOnClick());
//        lin_search.setOnClickListener(new myOnClick());
        img_notice.setOnClickListener(new myOnClick());
//        tv_myc.setOnClickListener(new myOnClick());
//        tv_sc.setOnClickListener(new myOnClick());
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
//            getActivity().unregisterReceiver(broadcastReceiver);
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
