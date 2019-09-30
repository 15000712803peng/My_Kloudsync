package com.kloudsync.techexcel.contact.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.CustomerAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.contact.UserDetail;
import com.kloudsync.techexcel.frgment.ContactFragment;
import com.kloudsync.techexcel.help.ContactInfoInterface;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class ListVFragment extends Fragment implements ContactInfoInterface {
    private View view;
    private ListView lv_contact;
    private LinearLayout lin_none;

    private CustomerAdapter cAdapter;

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    ArrayList<Customer> eList = new ArrayList<Customer>();

    private String edit = null;

    private boolean isFirst = true;
    private boolean isFragmentVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.contact_page, container, false);

            initView();
        }

        return view;
    }

    private void initView() {
        lin_none = (LinearLayout) view.findViewById(R.id.lin_none);
        lv_contact = (ListView) view.findViewById(R.id.lv_contact);
        cAdapter = new CustomerAdapter(getActivity(), cuslist);
        lv_contact.setAdapter(cAdapter);
        lv_contact.setOnItemClickListener(new MyOnitem());
        getData();
    }


    private void getData() {
        final LoginGet loginget = new LoginGet();
        loginget.setLoginGetListener(new LoginGet.LoginGetListener() {

            @Override
            public void getMember(ArrayList<Customer> list) {
            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                cuslist = new ArrayList<Customer>();
                cuslist.addAll(list);
                cAdapter.updateListView(cuslist);
                VisibleGoneList(cuslist);
            }
        });
        loginget.CustomerRequest(getActivity());

    }

    private void VisibleGoneList(ArrayList<Customer> list) {
        // TODO Auto-generated method stub
        if (0 == list.size()) {
            lin_none.setVisibility(View.VISIBLE);
            lv_contact.setVisibility(View.GONE);
        } else {
            lin_none.setVisibility(View.GONE);
            lv_contact.setVisibility(View.VISIBLE);
        }
        ((ContactFragment) getParentFragment()).SideVG(0 == list.size());

    }

    @Override
    public void EditSearch(String s) {
        edit = s;
        eList = new ArrayList<Customer>();
        for (int i = 0; i < cuslist.size(); i++) {
            Customer cus = cuslist.get(i);
            String name = edit;
            String getName = cus.getName().toLowerCase();//转小写
            String nameb = name.toLowerCase();//转小写
            if (getName.contains(nameb.toString())
                    && name.length() > 0) {
                Customer customer;
                customer = cus;
                eList.add(customer);
            }
        }
        if (edit.length() != 0) {
            cAdapter.updateListView2(eList);
        } else {
            cAdapter.updateListView2(cuslist);
        }
    }

    @Override
    public void TouchSide(String s) {
        int position;
        position = SideBarSortHelp.getPositionForSection(cuslist,
                s.charAt(0));

        if (position != -1) {
            lv_contact.setSelection(position);
        } else {
            lv_contact
                    .setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        }
    }

    @Override
    public void Refresh() {
        if (cAdapter != null)
            getData();
    }

    @Override
    public void SideVg() {
        ((ContactFragment) getParentFragment()).SideVG(0 == cuslist.size());
    }

    private class MyOnitem implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (0 == position) {
                AppConfig.Name = AppConfig.RobotName;
                AppConfig.isUpdateDialogue = true;
                RongIM.getInstance().refreshUserInfoCache(new UserInfo(AppConfig.DEVICE_ID + AppConfig.RongUserID,
                        AppConfig.Name,
                        null));
                RongIM.getInstance().startPrivateChat(getActivity(),
                        AppConfig.DEVICE_ID + AppConfig.RongUserID,
                        AppConfig.Name);
            } else {
                Customer cus = cuslist.get(position);
                if (edit != null && edit.length() != 0) {
                    cus = eList.get(position);
                }
//                Intent intent = new Intent(getActivity(), isCustomer ? UserDetail.class : MemberDetail.class);
                Intent intent = new Intent(getActivity(), UserDetail.class);
                intent.putExtra("UserID", cus.getUserID());
                startActivity(intent);
            }

        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisible = isVisibleToUser;
        if (isFirst && isVisibleToUser) {
            isFirst = false;
//            initFunction();
        }
    }

    private void initFunction() {
        getData();
    }
}
