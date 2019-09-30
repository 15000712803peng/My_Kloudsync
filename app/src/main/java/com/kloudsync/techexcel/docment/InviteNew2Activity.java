package com.kloudsync.techexcel.docment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.InviteNew2Adapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.kloudsync.activity.TeamUser;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InviteNew2Activity extends AppCompatActivity {

    private ImageView img_notice;
    private Button btn_ok;
    private RecyclerView rv_in;
    private ClearEditText et_search;
    private TextView tv_title;

    private int itemID;
    private List<TeamUser> mTeamUserData = new ArrayList<>();
    private List<Customer> mlist = new ArrayList<>();
    ArrayList<Customer> eList = new ArrayList<Customer>();

    private InviteNew2Adapter iadapter;

    public static final String 嘛米嘛米哄 = "凸(艹皿艹 )";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.AddMember:
                    String result = (String) msg.obj;
                    EventBus.getDefault().post(new Document());
                    EventBus.getDefault().post(new TeamSpaceBean());
                    finish();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_LONG).show();

                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_LONG).show();

                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitenew2);
        initView();
        itemID = getIntent().getIntExtra("itemID", 0);
        editCustomers();
        GetData();
    }

    private void GetData() {
        final LoginGet loginget = new LoginGet();
        loginget.setLoginGetListener(new LoginGet.LoginGetListener() {
            @Override
            public void getCustomer(ArrayList<Customer> list) {
                mlist = new ArrayList<Customer>();
                mlist.addAll(list);
                for (int i = 0; i < mlist.size(); i++) {
                    Customer cus = mlist.get(i);
                    for (int j = 0; j < mTeamUserData.size(); j++) {
                        TeamUser tu = mTeamUserData.get(j);
                        if (cus.getUserID().equals(tu.getMemberID() + "")) {
                            mlist.remove(i);
                            i--;
                            break;
                        }
                    }
                }
                iadapter.UpdateRV(mlist);

            }

            @Override
            public void getMember(ArrayList<Customer> list) {

            }
        });
        TeamSpaceInterfaceTools.getinstance().getTeamItem(AppConfig.URL_PUBLIC + "TeamSpace/Item?itemID=" + itemID,
                TeamSpaceInterfaceTools.GETTEAMITEM, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        TeamSpaceBean teamSpaceBean = (TeamSpaceBean) object;
                        mTeamUserData = teamSpaceBean.getMemberList();

                        loginget.CustomerRequest(getApplicationContext());
                    }
                });
    }

    private void initView() {
        img_notice = (ImageView) findViewById(R.id.img_notice);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        rv_in = (RecyclerView) findViewById(R.id.rv_in);
        et_search = (ClearEditText) findViewById(R.id.et_search);
        tv_title = (TextView) findViewById(R.id.tv_title);

        img_notice.setOnClickListener(new MyOnClick());
        btn_ok.setOnClickListener(new MyOnClick());

        ViewCompat.setTransitionName(tv_title, 嘛米嘛米哄);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_in.setLayoutManager(manager);
        iadapter = new InviteNew2Adapter(mlist);
        iadapter.setOnItemClickListener(new InviteNew2Adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Customer customer = mlist.get(position);

                if (et_search.length() != 0) {
                    customer = eList.get(position);
                }
                customer.setSelected(!customer.isSelected());
                iadapter.UpdateRV((et_search.length() != 0) ? eList : mlist);
            }
        });
        rv_in.setAdapter(iadapter);
    }

    protected class MyOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_notice:
                    ActivityCompat.finishAfterTransition(InviteNew2Activity.this);
                    break;
                case R.id.btn_ok:
                    AddContact();
                    break;
                default:
                    break;
            }

        }
    }

    private void editCustomers() {
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                eList = new ArrayList<Customer>();
                for (int i = 0; i < mlist.size(); i++) {
                    Customer cus = mlist.get(i);
                    String name = et_search.getText().toString();
                    String getName = cus.getName().toLowerCase();//转小写
                    String nameb = name.toLowerCase();//转小写
                    if (getName.contains(nameb.toString())
                            && name.length() > 0) {
                        Customer customer;
                        customer = cus;
                        eList.add(customer);
                    }
                }
                if (et_search.length() != 0) {
                    iadapter.UpdateRV(eList);
                } else {
                    iadapter.UpdateRV(mlist);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void AddContact() {
        String mems = "";
        for (int i = 0; i < mlist.size(); i++) {
            Customer cus = mlist.get(i);
            if (cus.isSelected()) {
                if (0 == mems.length()) {
                    mems += cus.getUserID();
                } else {
                    mems += "," + cus.getUserID();
                }
            }
        }
        if (0 == mems.length()) {
            Toast.makeText(this, "please select new contact first", Toast.LENGTH_LONG).show();
            return;
        } else {
            final String finalMems = mems;
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject responsedata = ConnectService.submitDataByJson(
                                AppConfig.URL_PUBLIC
                                        + "TeamSpace/AddMember?TeamSpaceID=" + itemID + "&MemberList=" + finalMems, null);
                        Log.e("duang", AppConfig.URL_PUBLIC
                                + "TeamSpace/AddMember?TeamSpaceID=" + itemID + "&MemberList=" + finalMems);
                        Log.e("返回的responsedata", responsedata.toString() + "");
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                            msg.what = AppConfig.AddMember;
                            msg.obj = responsedata.toString();
                        } else {
                            msg.what = AppConfig.FAILED;
                            String ErrorMessage = responsedata.getString("ErrorMessage");
                            msg.obj = ErrorMessage;
                        }
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start(ThreadManager.getManager());
        }

    }

}
