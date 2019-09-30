package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ub.techexcel.adapter.AddAuditorAdapter;
import com.ub.techexcel.tools.CharacterParser;
import com.ub.techexcel.tools.PinyinAddserviceComparator;
import com.ub.techexcel.tools.SiderIndex;
import com.ub.techexcel.view.SideBar;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wang on 2017/8/21.
 */

public class AddAuditorActivity extends Activity implements View.OnClickListener {

    private LinearLayout backll;
    private SideBar sideBar;
    private TextView dialog;
    private PinyinAddserviceComparator pinyinComparator;

    private CharacterParser characterParser;

    private ListView listView;
    private AddAuditorAdapter myAdapter;
    private ProgressBar progressBar;
    private List<Customer> mList = new ArrayList<Customer>();
    private TextView done;
    private List<Customer> existList = new ArrayList<>();
    private Customer teacher, student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addauditor);
        Log.e("existList", existList.size() + "");

        existList = (List<Customer>) getIntent().getSerializableExtra("mAttendesList");
        teacher = (Customer) getIntent().getSerializableExtra("teacher");
        student = (Customer) getIntent().getSerializableExtra("student");

        initView();
        searchcontactThread();
    }


    private void initView() {
        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);
        sideBar = (SideBar) findViewById(R.id.friends_sidrbar);
        listView = (ListView) findViewById(R.id.friends_myfriends);

        dialog = (TextView) findViewById(R.id.friends_dialog);
        progressBar = (ProgressBar) findViewById(R.id.pb_contacts);
        progressBar.setVisibility(View.GONE);
        sideBar.setTextView(dialog);
        sideBar.select(-1);
        done = (TextView) findViewById(R.id.done);
        done.setOnClickListener(this);

        characterParser = new CharacterParser();
        pinyinComparator = new PinyinAddserviceComparator();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Customer bean = mList.get(arg2);
                if (bean.isSelected()) {
                    bean.setSelected(false);
                } else {
                    bean.setSelected(true);
                }
                myAdapter.notifyDataSetChanged();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private String first;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (myAdapter != null) {
                    if (myAdapter.getlist() != null
                            && myAdapter.getlist().size() > 0) {
                        first = myAdapter.getlist().get(firstVisibleItem)
                                .getSortLetters();
                        int index = SiderIndex.stringtoint(first);
                        sideBar.select(index);
                    } else {
                        sideBar.select(-1);
                    }
                }
            }
        });

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (myAdapter != null) {
                    int position = myAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        listView.setSelection(position);
                    }
                }
            }
        });

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SelectUserActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SelectUserActivity");
        MobclickAgent.onPause(this);
    }


    private void searchcontactThread() {
        mList.clear();
        final LoginGet loginGet = new LoginGet();
        loginGet.setLoginGetListener(new LoginGet.LoginGetListener() {

            @Override
            public void getMember(ArrayList<Customer> list) {
                // TODO Auto-generated method stub
            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                // TODO Auto-generated method stub
                mList.clear();
                mList.addAll(list);

                //除去老师 学生信息
                for (int i = 0; i < mList.size(); i++) {
                    Customer c1 = mList.get(i);
                    if (c1.getUserID().equals(teacher.getUserID())) {
                        mList.remove(i);
                        i--;
                    }
                    if (c1.getUserID().equals(student.getUserID())) {
                        mList.remove(i);
                        i--;
                    }
                }

                for (int i = 0; i < mList.size(); i++) {
                    Customer c1 = mList.get(i);
                    for (int j = 0; j < existList.size(); j++) {
                        Customer c2 = existList.get(j);
                        if (c1.getUserID().equals(c2.getUserID())) {
                            if (c2.isEnterMeeting()) {
                                mList.remove(i);
                                i--;
                            }
                            break;
                        }
                    }
                }
                for (Customer model : mList) {
                    String pinyin = characterParser.getPingYin(model.getName());
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    if (sortString.matches("[A-Z]")) {
                        model.setSortLetters(sortString.toUpperCase());
                    } else {
                        model.setSortLetters("#");
                    }
                }
                Collections.sort(mList, pinyinComparator);
                myAdapter = new AddAuditorAdapter(AddAuditorActivity.this, mList);
                listView.setAdapter(myAdapter);
            }
        });
        loginGet.CustomerRequest(getApplicationContext());
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.backll:
                finish();
                break;
            case R.id.done:
                for (int i = 0; i < mList.size(); i++) {
                    Customer customer = mList.get(i);
                    if (customer.isSelected()) {
                        AppConfig.auditorList.add(customer);
                        AppConfig.isUpdateAuditor = true;
                    }
                }
                finish();
                break;
            default:
                break;
        }
    }

}
