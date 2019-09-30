package com.ub.friends.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.info.AddFriend;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.LoginGetListener;
import com.ub.techexcel.adapter.MyNewCustomersAdapter;
import com.ub.techexcel.adapter.MyNewFriendsAdapter;
import com.ub.techexcel.adapter.MyNewFriendsAdapter.OnHealthChangedListener;
import com.ub.techexcel.database.CustomerDao;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewFriendsActivity extends Activity implements OnClickListener,
        OnItemClickListener {
    private static final int FRIENDS_NAME = 0X001;
    private LinearLayout back;
    private TextView addFriends;
    private List<AddFriend> memberlist = new ArrayList<AddFriend>(); // 新的会员
    private List<Customer> customerList = new ArrayList<Customer>(); // 新的用户
    private ListView listView;
    private MyNewFriendsAdapter myNewFriendsAdapter;
    private MyNewCustomersAdapter myNewCustomersAdapter;
    private EditText editText;
    private CustomerDao customerDao;
    private TextView title;

    private LinearLayout ll, defaultPage;
    private ImageView imageView;
    private TextView tv1, tv2;
    private TextView titleText;
    private RelativeLayout backLayout;
    /**
     * O USER 1 FRIEND
     */
    private int currentPosition = 1;
    private InputMethodManager inputManager;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    if (memberlist.size() > 0) {
                        defaultPage.setVisibility(View.GONE);
                        ll.setVisibility(View.VISIBLE);

                        myNewFriendsAdapter = new MyNewFriendsAdapter(
                                NewFriendsActivity.this, memberlist);
                        listView.setAdapter(myNewFriendsAdapter);
                        myNewFriendsAdapter
                                .setOnHealthChangedListener(new OnHealthChangedListener() {
                                    @Override
                                    public void onTouchingLetterChanged(int position) {
                                        // TODO Auto-generated method stub
                                        AddFriend addFriend = memberlist
                                                .get(position);
                                        String ss = addFriend.getType();
                                        if (ss.equals("1")) {// 接受
                                            Log.e("",
                                                    addFriend.getSourceID()
                                                            + "   "
                                                            + addFriend
                                                            .getTargetID());
                                            acceptRequest(position);
                                        } else if (ss.equals("2")) {// 已添加

                                        } else if (ss.equals("3")) {// 等待验证

                                        } else {// 添加

                                            Intent intent = new Intent(
                                                    NewFriendsActivity.this,
                                                    AddVerificationActivity.class);
                                            intent.putExtra("addfriends",
                                                    memberlist.get(position));
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                    break;
                case FRIENDS_NAME:
                    getUserNameTel(memberlist);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newfriends);
//        currentPosition = getIntent().getIntExtra("currentposition", 0);
        AppConfig.isRefreshRed = true;
        initView();
        AppConfig.isRefreshRed = true;

        if (currentPosition == 0) {
//            getCustomer(); // 获的新的用户
            getFriendsFromDB();
        } else if (currentPosition == 1) {
            getFriendsFromDB();
        }
        listView.setOnItemClickListener(this);
    }

    private void getCustomer() {
        // TODO Auto-generated method stub
        final LoginGet loginget = new LoginGet();
        loginget.setLoginGetListener(new LoginGetListener() {

            @Override
            public void getMember(ArrayList<Customer> list) {

            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                customerList = new ArrayList<Customer>();
                customerList.addAll(list);
                if (customerList.size() > 0) {
                    defaultPage.setVisibility(View.GONE);
                    ll.setVisibility(View.VISIBLE);
                    myNewCustomersAdapter = new MyNewCustomersAdapter(
                            NewFriendsActivity.this, customerList);
                    listView.setAdapter(myNewCustomersAdapter);
                } else {
                    tv2.setText("You haven't friends for a long time");
                }

            }
        });
        loginget.CustomerRequest(NewFriendsActivity.this);

    }

    @Override
    protected void onResume() {
        MobclickAgent.onPageStart("NewFriendsActivity");
        MobclickAgent.onResume(this); // 统计时长
        super.onResume();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NewFriendsActivity");
        MobclickAgent.onPause(this);
    }

    /**
     * 通过rongid 查找数据库里所有记录好友的详细信息
     *
     * @param list
     */
    private void getUserNameTel(List<AddFriend> list) {
        if (list == null || list.size() <= 0) {
            handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
        } else {
            StringBuilder builder = new StringBuilder();
            for (AddFriend addFriend : list) {
                String rongid = "";
                if (addFriend.getTargetID().equals(AppConfig.RongUserID)) {
                    rongid = addFriend.getSourceID();
                }
                if (addFriend.getSourceID().equals(AppConfig.RongUserID)) {
                    rongid = addFriend.getTargetID();
                }
                builder.append(rongid + ",");
            }
            final String newbuilder = builder
                    .substring(0, builder.length() - 1);
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    JSONObject ronfinfo = ConnectService
                            .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                    + "Friend/ToApproveList?rongCloudIDs="
                                    + newbuilder);
                    Log.e("url", AppConfig.URL_PUBLIC
                            + "Friend/ToApproveList?rongCloudIDs=" + newbuilder);
                    List<AddFriend> addinfo = formatFrinds(ronfinfo);
                    if (addinfo.size() > 0) {
                        setFriendsNameTel(addinfo);
                    }
                    handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
                }

            }).start(((App) getApplication()).getThreadMgr());
        }

    }

    private void setFriendsNameTel(List<AddFriend> addinfo) {
        // TODO Auto-generated method stub
        for (AddFriend addFriend : memberlist) { // 查找数据库
            String rongid = "";
            if (addFriend.getTargetID().equals(AppConfig.RongUserID)) { // source
                rongid = addFriend.getSourceID();
            } else if (addFriend.getSourceID().equals(AppConfig.RongUserID)) {
                rongid = addFriend.getTargetID();
            }
            for (AddFriend addFriend2 : addinfo) {
                if (rongid.equals(addFriend2.getTargetID())) { // addFriend2.getTargetID()
                    // 指好友的rongyunid
                    addFriend.setName(addFriend2.getName());
                    addFriend.setPhone(addFriend2.getPhone());
                    addFriend.setUrl(addFriend2.getUrl());
                    addFriend.setUserID(addFriend2.getUserID());
                    break;
                }
            }
        }
    }

    private void initView() {
        customerDao = new CustomerDao(this);
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        addFriends = (TextView) findViewById(R.id.txt_right_title);
        addFriends.setText(R.string.add);
        addFriends.setOnClickListener(this);
        title = (TextView) findViewById(R.id.tv_title);
        listView = (ListView) findViewById(R.id.listview);
        title.setText(getString(R.string.newcontact));
        ll = (LinearLayout) findViewById(R.id.ll);
        defaultPage = (LinearLayout) findViewById(R.id.defaultpage);

        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageResource(R.drawable.contacts_d);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv1.setText("");
        tv2 = (TextView) findViewById(R.id.tv2);
        tv2.setText("You haven't friends for a long time");

        editText = (EditText) findViewById(R.id.searchfriends);
        inputManager = (InputMethodManager) editText.getContext()
                .getSystemService(this.INPUT_METHOD_SERVICE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
                String tel = arg0.toString();
                if (currentPosition == 0) { // customer
                    List<Customer> list = new ArrayList<Customer>();
                    for (Customer customer : customerList) {
                        if (customer.getPhone().contains(tel)) {
                            list.add(customer);
                        }
                    }
                    myNewCustomersAdapter = new MyNewCustomersAdapter(
                            NewFriendsActivity.this, list);
                    listView.setAdapter(myNewCustomersAdapter);
                } else if (currentPosition == 1) {
                    List<AddFriend> list2 = new ArrayList<AddFriend>();
                    for (AddFriend friends : memberlist) {
                        if (friends.getPhone().contains(tel)) {
                            list2.add(friends);
                        }
                    }
                    myNewFriendsAdapter = new MyNewFriendsAdapter(
                            NewFriendsActivity.this, list2);
                    myNewFriendsAdapter
                            .setOnHealthChangedListener(new OnHealthChangedListener() {
                                @Override
                                public void onTouchingLetterChanged(int position) {
                                    // TODO Auto-generated method stub
                                    AddFriend addFriend = memberlist
                                            .get(position);
                                    String ss = addFriend.getType();
                                    if (ss.equals("1")) {
                                        Log.e("接受",
                                                addFriend.getSourceID()
                                                        + "   "
                                                        + addFriend
                                                        .getTargetID());
                                        acceptRequest(position);
                                    } else if (ss.equals("2")) {
                                    } else if (ss.equals("3")) {
                                    } else {
                                        Intent intent = new Intent(
                                                NewFriendsActivity.this,
                                                AddVerificationActivity.class);
                                        intent.putExtra("addfriends",
                                                memberlist.get(position));
                                        startActivity(intent);
                                    }
                                }
                            });
                    listView.setAdapter(myNewFriendsAdapter);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

    }

    private void getFriendsFromDB() {
        List<AddFriend> list2 = customerDao.queryAll();
        memberlist.clear();
        memberlist.addAll(list2);
        handler.obtainMessage(FRIENDS_NAME).sendToTarget();
    }

    /**
     * 不是登录账号的好友
     *
     * @param jsonObject
     * @return
     */
    private List<AddFriend> formatFrinds(JSONObject jsonObject) {
        Log.e("rongyuninfo", jsonObject.toString());
        List<AddFriend> list = new ArrayList<AddFriend>();
        try {
            int retCode = jsonObject.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retData = jsonObject.getJSONArray("RetData");
                    list = new ArrayList<AddFriend>();
                    for (int i = 0; i < retData.length(); i++) {
                        JSONObject object = retData.getJSONObject(i);
                        AddFriend addfriend = new AddFriend();
                        addfriend.setName(object.getString("Name"));
                        addfriend.setUserID(object.getInt("UserID") + "");
                        String telephone = object.getString("Phone");
                        addfriend.setPhone(telephone);
                        addfriend.setTargetID(object.getString("RongCloudUserID"));
                        boolean IsFriend = object.getBoolean("IsFriend");
                        addfriend.setUrl(object.getString("AvatarUrl"));
                        if (IsFriend) { // 是好友
                            addfriend.setType("2");
                        } else { // 不是好友
                            addfriend.setType("0");
                        }
                        list.add(addfriend);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 接受好友的申请
     *
     * @param position
     */
    private void acceptRequest(final int position) {
        // TODO Auto-generated method stub
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("TargetID", memberlist.get(position)
                            .getSourceID());
                    JSONObject jsonObject2 = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC + "Friend/ApproveFriend",
                            jsonObject);
                    if (jsonObject2.getInt("RetCode") == 0) {
                        memberlist.get(position).setType("2"); // 已添加
                        customerDao.update(memberlist.get(position));
                        handler.obtainMessage(AppConfig.LOAD_FINISH)
                                .sendToTarget();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(((App) getApplication()).getThreadMgr());
    }


    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.layout_back:
                inputManager.hideSoftInputFromWindow(getCurrentFocus()
                                .getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
                break;
            case R.id.txt_right_title:
                // customerDao.delete();
                inputManager.hideSoftInputFromWindow(getCurrentFocus()
                                .getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                Intent intent = new Intent(NewFriendsActivity.this,
                        AddFriendsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        switch (currentPosition) {
            case 0:
//                Intent intent = new Intent(NewFriendsActivity.this,
//                        UserDetail.class);
//                intent.putExtra("UserID", customerList.get(arg2).getUserID());
//                startActivity(intent);
                break;
            case 1:
//                Intent intent2 = new Intent(NewFriendsActivity.this,
//                        UserDetail.class);
//                intent2.putExtra("UserID", memberlist.get(arg2).getUserID());
//                startActivity(intent2);
                break;
            default:
                break;
        }
    }

}
