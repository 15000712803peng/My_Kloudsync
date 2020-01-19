package com.kloudsync.techexcel.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.CustomerAdapter;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.ShareMessage;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.SideBar;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.MessageTool;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.friends.activity.AddFriendsActivity;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class MyFriendsActivity extends Activity implements View.OnClickListener {


    private ListView lv_contact;
    private LinearLayout lin_none;
    private ClearEditText et_search;
    private SideBar sidebar;
    private ImageView tv_back;
    private TextView titleText;
    private ImageView img_add;
    private RelativeLayout backLayout;
    private CustomerAdapter cAdapter;
    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    ArrayList<Customer> eList = new ArrayList<Customer>();
    private boolean flag_s;
    private Document document;
    private int Syncid;
    private RelativeLayout titleRightLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfriends);
        flag_s = getIntent().getBooleanExtra("isShare", false);
        if (flag_s) {
            Syncid = getIntent().getIntExtra("Syncid", -1);
            document = (Document) getIntent().getSerializableExtra("document");
        }

        initView();
        getData();
    }

    private void initView() {
        lin_none = (LinearLayout) findViewById(R.id.lin_none);
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        tv_back = (ImageView) findViewById(R.id.tv_back);
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText(R.string.My_Friends
        );
        et_search = (ClearEditText) findViewById(R.id.et_search);
        sidebar = (SideBar) findViewById(R.id.sidebar);
        img_add = (ImageView) findViewById(R.id.img_add);
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        titleRightLayout = findViewById(R.id.layout_title_right);
        titleRightLayout.setVisibility(View.VISIBLE);
        titleRightLayout.setOnClickListener(this);
//        editCustomers();
        getSide();
        cAdapter = new CustomerAdapter(this, cuslist);
        lv_contact.setAdapter(cAdapter);
        lv_contact.setOnItemClickListener(new MyOnitem());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.layout_title_right:
                GoToAddFriend();
                break;
            default:
                break;
        }
    }


    private void GoToAddFriend() {
        Intent i = new Intent(MyFriendsActivity.this, AddFriendsActivity.class);
        startActivity(i);
    }

    private void getSide() {
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
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
        });

    }

    //搜索
    private void editCustomers() {
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
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
        loginget.CustomerRequest(MyFriendsActivity.this);

    }

    private void VisibleGoneList(ArrayList<Customer> list) {
        // TODO Auto-generated method stub
        if (0 == list.size()) {
            lin_none.setVisibility(View.VISIBLE);
            lv_contact.setVisibility(View.GONE);
            sidebar.setVisibility(View.GONE);
        } else {
            lin_none.setVisibility(View.GONE);
            lv_contact.setVisibility(View.VISIBLE);
            sidebar.setVisibility(View.VISIBLE);
        }
    }

    private class MyOnitem implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Customer cus = cuslist.get(position);
            if (et_search.length() != 0) {
                cus = eList.get(position);
            }

            if (flag_s) {
                ShareToMyFriend(cus);
            } else if (0 == position && cus.getName().equals(AppConfig.RobotName)) {
                AppConfig.Name = AppConfig.RobotName;
                AppConfig.isUpdateDialogue = true;
                RongIM.getInstance().refreshUserInfoCache(new UserInfo(AppConfig.DEVICE_ID + AppConfig.RongUserID,
                        AppConfig.Name,
                        null));
                RongIM.getInstance().startPrivateChat(MyFriendsActivity.this,
                        AppConfig.DEVICE_ID + AppConfig.RongUserID,
                        AppConfig.Name);
            } else {
                Intent intent = new Intent(MyFriendsActivity.this, UserDetail.class);
                intent.putExtra("UserID", cus.getUserID());
                startActivity(intent);
            }

        }

    }


    private void ShareToMyFriend(final Customer cus) {
        String url = AppConfig.URL_PUBLIC + "User/UserProfile";
        ServiceInterfaceTools.getinstance().getLoginUserInfo(url, ServiceInterfaceTools.GETLOGINUSERINFO, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                String loginavatarurl = (String) object;
                ShareMessage sm = new ShareMessage();
                sm.setShareDocTitle(document.getTitle());
                sm.setAttachmentID(document.getAttachmentID() + "");
                String url = AppConfig.SHARE_ATTACHMENT + document.getAttachmentID();
                String thumurl = document.getAttachmentUrl();
                Log.e("thumurl", thumurl + "  " + loginavatarurl);

                if(!TextUtils.isEmpty(thumurl)){
                    if (thumurl.contains("<") && thumurl.contains(">")) {
                        thumurl = thumurl.substring(0, thumurl.lastIndexOf("<")) + "1"
                                + thumurl.substring(thumurl.lastIndexOf("."), thumurl.length());
                    }
                    Log.e("thumurl", thumurl);
                }

                sm.setShareDocThumbnailUrl(thumurl);
                sm.setShareDocUrl(url);
                sm.setShareDocAvatarUrl(loginavatarurl);
                String date = (String) DateFormat.format("yyyy_MM_dd", System.currentTimeMillis());
                sm.setShareDocTime(date);
                sm.setShareDocUsername(AppConfig.UserName);
                MessageTool.sendMessage(sm, cus.getUBAOUserID(), Conversation.ConversationType.PRIVATE);
                Toast.makeText(MyFriendsActivity.this, getString(R.string.share_success), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

}
