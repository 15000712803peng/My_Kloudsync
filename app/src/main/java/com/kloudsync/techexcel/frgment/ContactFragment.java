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
import android.util.Log;
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
import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.ContactAdapter;
import com.kloudsync.techexcel.adapter.FriendContactAdapter;
import com.kloudsync.techexcel.bean.ContactDetailData;
import com.kloudsync.techexcel.bean.EventFilterContact;
import com.kloudsync.techexcel.bean.EventRefreshContact;
import com.kloudsync.techexcel.bean.EventSearchContact;
import com.kloudsync.techexcel.bean.FriendContact;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.SameLetterFriends;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.contact.ContactMap;
import com.kloudsync.techexcel.contact.MyFriendsActivity;
import com.kloudsync.techexcel.dialog.PopFilterContact;
import com.kloudsync.techexcel.dialog.PopMeetingMemberSetting;
import com.kloudsync.techexcel.docment.InviteNewActivity;
import com.kloudsync.techexcel.help.ContactHelpInterface;
import com.kloudsync.techexcel.help.PopContactHAHA;
import com.kloudsync.techexcel.help.SideBar;
import com.kloudsync.techexcel.help.SideBar.OnTouchingLetterChangedListener;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.pc.ui.ContactDetailActivity;
import com.kloudsync.techexcel.search.ui.ContactSearchActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.friends.activity.NewFriendsActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.service.activity.NotifyActivity;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;

public class ContactFragment extends Fragment implements ContactHelpInterface, OnClickListener {

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
    private FriendContactAdapter adapter;
    private ImageView filterImage;
    private TextView contactTitle;
    private TextView noContactText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                getActivity().MODE_PRIVATE);

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
        noContactText = view.findViewById(R.id.txt_no_contacts);
        lin_none = (LinearLayout) view.findViewById(R.id.lin_none);
        sidebar = (SideBar) view.findViewById(R.id.sidebar);
        list = (ListView) view.findViewById(R.id.lv_contact);
        filterImage = view.findViewById(R.id.image_filter_contact);
        contactTitle = view.findViewById(R.id.txt_contact_title);
//        list.addHeaderView(initHeader());
        cAdapter = new ContactAdapter(getActivity(), cuslist);
        list.setAdapter(cAdapter);
        list.setOnItemClickListener(new MyOnitem());
        filterImage.setOnClickListener(this);
        showContactByType(sharedPreferences.getInt("contact_type",1));

    }

    private View initHeader() {
        View header = getActivity().getLayoutInflater().inflate(R.layout.contact_header, null);
        lin_my_friend = (LinearLayout) header.findViewById(R.id.lin_my_friend);
        lin_my_friend.setOnClickListener(new myOnClick());
        lin_new = (LinearLayout) header.findViewById(R.id.lin_new);
        lin_new.setOnClickListener(new myOnClick());
        header.setVisibility(View.GONE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_filter_contact:
                showFilterPop(v);
                break;
            default:
                break;
        }

    }

    private class MyOnitem implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if(adapter == null){
                return;
            }

            if(adapter.getItem(position) instanceof FriendContact){
                final FriendContact friendContact = (FriendContact) adapter.getItem(position);
//                AppConfig.Name = friendContact.getUserName();
//                RongIM.getInstance().startPrivateChat(getActivity(),
//                        friendContact.getRongCloudId()+"", friendContact.getUserName());
                Observable.just("Request_Detail").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
                    @Override
                    public JSONObject apply(String s) throws Exception {
                        return ServiceInterfaceTools.getinstance().syncGetContactDetail(AppConfig.SchoolID+"",friendContact.getUserId()+"");
                    }
                }).doOnNext(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        if(jsonObject.has("code")){
                            Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
                            intent.putExtra("contact_detail",jsonObject.getJSONObject("data").toString());
                            intent.putExtra("friend_contact",new Gson().toJson(friendContact));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                }).subscribe();
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
//        isFragmentVisible = isVisibleToUser;
//        if (isFirst && isVisibleToUser) {
//            isFirst = false;
//            initFunction();
//        }
//        RefreshInfo();
    }

    private void RefreshInfo() {

        if (AppConfig.isUpdateCustomer) {
            getData();
        }
        AppConfig.isUpdateCustomer = false;
    }

    private void initFunction() {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void filterContact(EventFilterContact filterContact){
        Log.e("EventBus","filterContact");
        showContactByType(filterContact.getTpye());
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        EventBus.getDefault().register(this);
//        if (isFragmentVisible) {
//            RefreshInfo();
//        }
//        if (tv_title != null) {
//            GetSchoolInfo();
//        }
//        if (flagRf) {
//            getData();
//            flagRf = false;
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null && getActivity() != null) {
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

    }

    List<SameLetterFriends> letterFriendsList = new ArrayList<>();

    private void showContactByType(final int type) {
        Log.e("showContactByType","type:"+ type);
        final int schoolId = sharedPreferences.getInt("SchoolID", -1);

        if (schoolId <= 0) {
            return;
        }

        letterFriendsList.clear();
        final Gson gson = new Gson();
        Observable.just(type).observeOn(Schedulers.io()).map(new Function<Integer, List<SameLetterFriends>>() {
            @Override
            public List<SameLetterFriends> apply(Integer integer) throws Exception {
                JSONObject jsonObject = ServiceInterfaceTools.getinstance().syncGetFriendList(schoolId, type);
                if (jsonObject.has("code")) {
                    if (jsonObject.getInt("code") == 0) {
                        sharedPreferences.edit().putInt("contact_type",type).commit();
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        Log.e("check_contact", "data_array:" + dataArray);
                        if (dataArray != null) {
                            for (int i = 0; i < dataArray.length(); ++i) {
                                SameLetterFriends letterFriends = gson.fromJson(dataArray.getJSONObject(i).toString(),
                                        SameLetterFriends.class);
                                Log.e("check_contact", "letterFriends:" + letterFriends);
                                letterFriendsList.add(letterFriends);
                            }
                        }
                    }
                }
                return letterFriendsList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<List<SameLetterFriends>>() {
            @Override
            public void accept(List<SameLetterFriends> sameLetterFriends) throws Exception {
                if (sameLetterFriends == null || sameLetterFriends.size() <= 0) {
                    sameLetterFriends = new ArrayList<>();
                    noContactText.setVisibility(View.VISIBLE);
                }else {
                    noContactText.setVisibility(View.GONE);
                }
                if(type == 0){
                    filterImage.setImageResource(R.drawable.icon_filter);
                    contactTitle.setText(getString(R.string.contract_company));
                }else if(type == 1){
                    filterImage.setImageResource(R.drawable.filter_red);
                    contactTitle.setText(getString(R.string.contract_all));
                }

                adapter = new FriendContactAdapter(getActivity(), sameLetterFriends);
                Log.e("check_contact", "set_adapter:" + sameLetterFriends.size());
                list.setAdapter(adapter);
            }
        }).subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }



    PopFilterContact filterContactPop;
    private void showFilterPop(View view) {
        if (filterContactPop != null) {
            if (filterContactPop.isShowing()) {
                filterContactPop.dismiss();
            }
            filterContactPop = null;
        }

        filterContactPop = new PopFilterContact(getActivity());
        filterContactPop.showAtBottom(view,sharedPreferences.getInt("contact_type",1));
    }

    @Subscribe
    public void eventRefreshContact(EventRefreshContact eventRefreshContact){
        showContactByType(sharedPreferences.getInt("contact_type",1));

    }

}
