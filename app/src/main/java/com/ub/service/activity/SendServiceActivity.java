package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.CustomizeMessage;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.CommonUse;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.DialogGetListener;
import com.kloudsync.techexcel.start.LoginGet.LoginGetListener;
import com.ub.techexcel.adapter.SendServiceAdapter;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation.ConversationType;

public class SendServiceActivity extends Activity implements OnClickListener,
        OnItemClickListener, TextWatcher {
    private TextView cancel;
    private TextView sendServiceTv;
    private EditText editText;
    private LinearLayout defaultPage;
    private ListView listView;
    private List<ServiceBean> seriviceList = new ArrayList<ServiceBean>();
    private SendServiceAdapter sendServiceAdapter;
    private ArrayList<CommonUse> main = new ArrayList<CommonUse>();
    private ArrayList<Customer> custometList = new ArrayList<Customer>();
    private String UBAOUserID;

    private int conversationtype = 1;
    private String mTargetId;
    private String useid;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    if (seriviceList.size() > 0) {
                        defaultPage.setVisibility(View.GONE);
                        sendServiceAdapter = new SendServiceAdapter(
                                SendServiceActivity.this, seriviceList);
                        listView.setAdapter(sendServiceAdapter);
                    }
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
        setContentView(R.layout.sendservice);
        UBAOUserID = getIntent().getStringExtra("mTargetId");
        conversationtype = getIntent().getIntExtra("conversationtype", 1);
        mTargetId = getIntent().getStringExtra("mTargetId");
        useid = getIntent().getStringExtra("userId");
        initView();
        getConcernList();

    }

    private void getConcernList() {
        // TODO Auto-generated method stub
        LoginGet loginGet = new LoginGet();
        loginGet.setDialogGetListener(new DialogGetListener() {

            @Override
            public void getUseful(ArrayList<CommonUse> list) {
                // TODO Auto-generated method stub

            }

            @Override
            public void getCH(ArrayList<CommonUse> list) {
                // TODO Auto-generated method stub
                main = new ArrayList<CommonUse>();
                main.addAll(list);
                getCustomerList();
            }

        });
        loginGet.ConcernHierarchyRequest(SendServiceActivity.this);
    }

    private void getCustomerList() {
        final LoginGet loginGet = new LoginGet();
        loginGet.setLoginGetListener(new LoginGetListener() {
            @Override
            public void getMember(ArrayList<Customer> list) {

            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                custometList = list;
                loadData();
            }
        });
        loginGet.CustomerRequest(SendServiceActivity.this);
    }

    /**
     * get service list
     */
    private void loadData() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnJson = ConnectService
                            .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                    + "Service/List?PageIndex=0&PageSize=20&UserID=" + useid + "&isPublish=1");
                    formatServiceData(returnJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private void formatServiceData(JSONObject returnJson) {
        seriviceList.clear();
        Log.e("发课程单", returnJson.toString());
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = returnJson.getJSONArray("RetData");
                    for (int i = 0; i < retdata.length(); i++) {
                        JSONObject service = retdata.getJSONObject(i);
                        ServiceBean bean = new ServiceBean();
                        String name = service.getString("Name");
                        bean.setName((name == null || name.equals("null")) ? ""
                                : name);
                        int concernid = service.getInt("ConcernID");
                        bean.setConcernID(concernid);
                        if (main.size() > 0) { // 关注点
                            for (CommonUse commuse : main) {
                                if (commuse.getID() == concernid) {
                                    String conName = commuse.getName();
                                    bean.setConcernName(conName);
                                    break;
                                }
                            }
                        }
                        bean.setCategoryID(service.getInt("CategoryID"));
                        bean.setSubCategoryID(service.getInt("SubCategoryID"));
                        int statusID = service.getInt("StatusID");
                        bean.setStatusID(statusID);
                        bean.setId(service.getInt("ID"));
                        bean.setIfClose(service.getInt("IfClosed"));
                        Customer customer = new Customer();
                        customer.setUserID(service.getInt("UserID") + "");

                        if (custometList.size() > 0) { // 设置用户的详细信息
                            for (Customer customer2 : custometList) {
                                if (customer.getUserID().equals(
                                        customer2.getUserID())) {
                                    customer.setUrl(customer2.getUrl());
                                    customer.setUBAOUserID(customer2
                                            .getUBAOUserID());
                                    customer.setName(customer2.getName());
                                }
                            }
                        }
                        bean.setCustomer(customer);
                        JSONArray lineitems = service.getJSONArray("LineItems");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setEventID(lineitem.getInt("EventID"));
                            String linename = lineitem.getString("EventName");
                            item.setEventName((linename == null || linename
                                    .equals("null")) ? "" : linename);
                            items.add(item);
                        }
                        bean.setLineItems(items);

                        if (bean.getStatusID() == 322) {
                            seriviceList.add(bean);
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
    }

    private void initView() {
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        sendServiceTv = (TextView) findViewById(R.id.sendservice);
        sendServiceTv.setOnClickListener(this);
        defaultPage = (LinearLayout) findViewById(R.id.defaultpage);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        editText = (EditText) findViewById(R.id.searchuser);
        editText.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.sendservice:
                boolean isselect = false;
                for (ServiceBean bean : seriviceList) {
                    if (bean.isSelect()) {
                        isselect = true;
                        sendServiceToUser(bean);
                    }
                }
                if (isselect) {
                    AppConfig.isUpdateDialogue = true;
                    finish();
                } else {
                    Toast.makeText(SendServiceActivity.this, "至少选择一项服务", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }

    }

    /**
     * 給用戶发送服务单
     */
    private void sendServiceToUser(ServiceBean sb) {
        if (conversationtype == 1) {

            CustomizeMessage msg = new CustomizeMessage(sb.getId() + "",
                    sb.getName(), sb.getConcernName(), "800");
            RongIM.getInstance()
                    .getRongIMClient()
                    .sendMessage(ConversationType.PRIVATE,
                            UBAOUserID, msg, "", "",
                            new RongIMClient.SendMessageCallback() {
                                @Override
                                public void onError(Integer messageId,
                                                    RongIMClient.ErrorCode e) {
                                    Toast.makeText(SendServiceActivity.this,
                                            "发送失败", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onSuccess(Integer integer) {

                                }
                            });

        } else if (conversationtype == 2) {
            CustomizeMessage msg = new CustomizeMessage(sb.getId() + "",
                    sb.getName(), sb.getConcernName(), "800");
            RongIM.getInstance()
                    .getRongIMClient()
                    .sendMessage(ConversationType.GROUP,
                            mTargetId, msg, "", "",
                            new RongIMClient.SendMessageCallback() {
                                @Override
                                public void onError(Integer messageId,
                                                    RongIMClient.ErrorCode e) {
                                    Toast.makeText(SendServiceActivity.this,
                                            "发送失败", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onSuccess(Integer integer) {

                                }
                            });

        }
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ServiceBean bean = seriviceList.get(arg2);
        if (bean.isSelect()) {
            bean.setSelect(false);
        } else {
            bean.setSelect(true);
        }
        sendServiceAdapter.notifyDataSetChanged();
        for (ServiceBean bean1 : seriviceList) {
            if (bean1.isSelect()) {
                sendServiceTv.setTextColor(getResources().getColor(R.color.c7));
                break;
            } else {
                sendServiceTv.setTextColor(getResources().getColor(
                        R.color.qiangrey));
            }
        }

    }

    @Override
    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        List<ServiceBean> newServiceBean = new ArrayList<ServiceBean>();
        for (ServiceBean bean1 : seriviceList) {
            if (bean1.getCustomer().getName().contains(arg0.toString())) {
                newServiceBean.add(bean1);
            }
        }
        sendServiceAdapter = new SendServiceAdapter(SendServiceActivity.this,
                newServiceBean);
        listView.setAdapter(sendServiceAdapter);

    }

}
