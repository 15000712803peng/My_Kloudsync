package com.kloudsync.techexcel.frgment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.service.activity.NotifyActivity;

public class UpgradeFragment extends Fragment {

    private View view;

    private ImageView img_notice;
    private ImageView img_add;
    private TextView tv_ns;
    private TextView tv_title;
    private TextView tv_upgrade;

    BroadcastReceiver broadcastReceiver;

    private boolean isFragmentVisible = false;
    private boolean isFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.upgrade_fragment, container, false);
            initView();
        }

        return view;
    }

    private void initView() {
        img_add = (ImageView) view.findViewById(R.id.img_add);
        img_notice = (ImageView) view.findViewById(R.id.img_notice);
        tv_ns = (TextView) view.findViewById(R.id.tv_ns);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_upgrade = (TextView) view.findViewById(R.id.tv_upgrade);

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
    }

    private void initFunction() {
        GetCourseBroad();
        img_add.setOnClickListener(new myOnClick());
        img_notice.setOnClickListener(new myOnClick());
        tv_upgrade.setOnClickListener(new myOnClick());

    }

    protected class myOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_add:

                    break;
                case R.id.img_notice:
                    GoToNotice();
                    break;
                case R.id.tv_upgrade:

                    break;
                default:
                    break;
            }
        }
    }

    private void GoToNotice() {
        startActivity(new Intent(getActivity(), NotifyActivity.class));
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
