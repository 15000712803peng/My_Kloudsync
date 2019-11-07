package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSearchChat;
import com.kloudsync.techexcel.bean.EventSearchContact;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.InviteNewDialog;
import com.kloudsync.techexcel.help.PopContactHAHA;
import com.kloudsync.techexcel.ui.InviteFromPhoneActivity;
import com.kloudsync.techexcel.view.CustomViewPager;
import com.ub.service.activity.NotifyActivity;
import com.ub.service.activity.SelectUserActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class TwoToOneFragment extends Fragment implements ViewPager.OnPageChangeListener, InviteNewDialog.InviteOptionsLinstener, View.OnClickListener {

    private View view;

    private RelativeLayout addLayout;
    private TextView tv_myc;
    private TextView tv_sc;
    private CustomViewPager vp_contact;
    private View view_lin1, view_lin2;
    BroadcastReceiver broadcastReceiver;
    private boolean isFragmentVisible = false;
    private boolean isFirst = true;
    private boolean isContact;
    private int width;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private RelativeLayout searchLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.twotoone_fragment, container, false);
            initView();
        }
        initFunction();
        return view;

    }

    private void initView() {
        addLayout = (RelativeLayout) view.findViewById(R.id.layout_add);
        tv_myc = (TextView) view.findViewById(R.id.tv_myc);
        tv_sc = (TextView) view.findViewById(R.id.tv_sc);
        vp_contact = (CustomViewPager) view.findViewById(R.id.vp_contact);
        vp_contact.addOnPageChangeListener(this);
        view_lin1 = view.findViewById(R.id.view_lin1);
        view_lin2 = view.findViewById(R.id.view_lin2);
        vp_contact.setPagingEnabled(true);
        searchLayout = (RelativeLayout) view.findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisible = isVisibleToUser;
        if (isFirst && isVisibleToUser) {
            isFirst = false;

        }
    }

    private void initFunction() {
        GetCourseBroad();
        initVP();
        addLayout.setOnClickListener(new myOnClick());
        tv_myc.setOnClickListener(new myOnClick());
        tv_sc.setOnClickListener(new myOnClick());

    }

    private void initVP() {
        DialogueFragment dialogueFragment = new DialogueFragment();
        ContactFragment contactFragment = new ContactFragment();
        mTabs = new ArrayList<Fragment>();
        mTabs.add(dialogueFragment);
        mTabs.add(contactFragment);
        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
        vp_contact.setAdapter(mAdapter);
        vp_contact.setOffscreenPageLimit(2);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ChangeList(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void inviteFromContactOption() {

    }

    @Override
    public void inviteNewOption() {
        inviteNew();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_layout) {
            if (!isContact) {
                EventBus.getDefault().post(new EventSearchChat());
            } else {
                EventBus.getDefault().post(new EventSearchContact());
            }
        }
    }

    protected class myOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_add:
//                    ShowPop();
                    ChatOrInvite();
                    break;

                case R.id.tv_myc:
                    ChangeList(0);
                    break;
                case R.id.tv_sc:
                    ChangeList(1);
                    break;
                default:
                    break;
            }
        }
    }

    InviteNewDialog inviteDialog;
    private void ChatOrInvite() {
        if (isContact) {
            if (inviteDialog == null) {
                inviteDialog = new InviteNewDialog(getActivity());
                inviteDialog.setOptionsLinstener(this);
                inviteDialog.setInviteFromContactLayoutGone();
                inviteDialog.show();
            } else {
                inviteDialog.show();
                inviteDialog.setOptionsLinstener(this);
            }
        } else {
            Intent i = new Intent(getActivity(), SelectUserActivity.class);
            i.putExtra("isDialogue", true);
            startActivity(i);

        }
    }

    private void inviteNew() {
        Intent intent = new Intent(getActivity(), InviteFromPhoneActivity.class);
        intent.putExtra("invite_type", 0);
        startActivity(intent);
    }

    private void ShowPop() {
        PopContactHAHA haha = new PopContactHAHA();
        haha.getPopwindow(getActivity());
        haha.StartPop(addLayout);
    }

    @SuppressLint("NewApi")
    public void ChangeList(int i) {
        isContact = (1 == i);
        tv_myc.setTextColor(getResources().getColor(0 == i ? R.color.white : R.color.black));
        tv_sc.setTextColor(getResources().getColor(0 == i ? R.color.black : R.color.white));

        tv_myc.setBackgroundResource(0 == i ?R.drawable.chatcontact1:R.drawable.chatcontact2);
        tv_sc.setBackgroundResource(0 == i ?R.drawable.chatcontact2:R.drawable.chatcontact1);

//        view_lin1.setVisibility(0 == i ? View.VISIBLE: View.INVISIBLE);
//        view_lin2.setVisibility(0 == i ? View.INVISIBLE: View.VISIBLE);
        view_lin1.setVisibility(0 == i ? View.GONE: View.GONE);
        view_lin2.setVisibility(0 == i ? View.GONE: View.GONE);
//        tv_myc.setBackground(getActivity().getDrawable(0 == i ? R.drawable.blue_left_bg : R.drawable.white_left_bg));
//        tv_sc.setBackground(getActivity().getDrawable(0 == i ? R.drawable.white_right_bg : R.drawable.blue_right_bg));
        vp_contact.setCurrentItem(i,false);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null && getActivity() != null) {
//            getActivity().unregisterReceiver(broadcastReceiver);
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }
}
