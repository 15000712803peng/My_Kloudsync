
package com.kloudsync.techexcel.personal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.AccountSettingAdminUserBean;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.SideBar;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.tool.PingYinUtil;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.techexcel.adapter.AccountSettingAdminUserAdapter;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AccountSettingAdminUserActivity extends AppCompatActivity {


    private LinearLayout lin_none;
    private RelativeLayout as_rl_add;
    private ClearEditText et_search;
    private ListView as_lv_admin_user;
    private SideBar sidebar;
    private List<AccountSettingAdminUserBean> aslist = new ArrayList<AccountSettingAdminUserBean>();
    private AccountSettingAdminUserAdapter asAdapter;

    ArrayList<AccountSettingAdminUserBean> eList = new ArrayList<AccountSettingAdminUserBean>();

    private ImageView tv_back;
    private TextView tv_title;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x22:
                    //Toast.makeText(AccountSettingActivity.this,"upload success", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x00:
                    if (0 == aslist.size()) {
                        lin_none.setVisibility(View.VISIBLE);
                        as_lv_admin_user.setVisibility(View.GONE);
                        sidebar.setVisibility(View.GONE);
                    } else {
                        lin_none.setVisibility(View.GONE);
                        as_lv_admin_user.setVisibility(View.VISIBLE);
                        sidebar.setVisibility(View.VISIBLE);
                    }

                   // Log.e("老余55521", aslist.size()+ "");
                    asAdapter = new  AccountSettingAdminUserAdapter(AccountSettingAdminUserActivity.this, aslist, true, 0);
                    as_lv_admin_user.setAdapter(asAdapter);

                    asAdapter.updateListView(aslist);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting_admin_user);

        initView();
        GetSchoolInfo();
        getSide();
    }

    private void initView() {

        as_rl_add = (RelativeLayout) findViewById(R.id.as_rl_add);
        lin_none = (LinearLayout) findViewById(R.id.lin_none);
        as_lv_admin_user = (ListView) findViewById(R.id.as_lv_admin_user);
        tv_back = (ImageView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_search = (ClearEditText) findViewById(R.id.et_search);


        sidebar = (SideBar) findViewById(R.id.sidebar);


        tv_back.setOnClickListener(new MyOnClick());
        as_rl_add.setOnClickListener(new MyOnClick());
        et_search.setOnClickListener(new MyOnClick());

    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.as_rl_add:

                    break;
                case R.id.et_search:
                    editCustomers();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获得所有原始数据
     *
     * @param
     */
    private void GetSchoolInfo() {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                     JSONObject jsonObject = ConnectService
                            .getIncidentData(
                            AppConfig.URL_PUBLIC
                                    + "SchoolContact/List?schoolID=" + AppConfig.SchoolID+"&roleType=7,8&searchText=&pageIndex=0");
                   // Log.e("老余", jsonObject + "");
                    //Log.e("老余", responsedata.toString() + "");
                  //  String retcode = responsedata.getString("RetCode");
                    aslist = formatjson(jsonObject);
                  //  Log.e("老余", aslist.size() + "");
                    if (aslist.size() > 0 && aslist != null) {
                        Message msg = new Message();
                        msg.what = 0x00;
                        msg.obj = aslist;
                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }
    private List<AccountSettingAdminUserBean> formatjson(JSONObject jsonObject) {
        List<AccountSettingAdminUserBean> list = new ArrayList<AccountSettingAdminUserBean>();
        JSONArray jsonarray;
        try {
            jsonarray = jsonObject.getJSONArray("RetData");
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject object = jsonarray.getJSONObject(i);
                AccountSettingAdminUserBean bean = new AccountSettingAdminUserBean();
                bean.setUserName(object.getString("UserName"));
                bean.setPhone(object.getString("Phone"));
                bean.setAvatarUrl(object.getString("AvatarUrl"));


                //获取名字首字母
                String Name=bean.getUserName();
                String sortLetter = null;
                if (Name == null
                        || (Name.length() > 0 && Name.substring(0, 1)
                        .equals(" ")) || Name.equals("")) {
                    sortLetter = "";
                } else {
                    sortLetter = PingYinUtil.getPingYin(Name)
                            .substring(0, 1).toUpperCase();
                }
                sortLetter = SideBarSortHelp.getAlpha(sortLetter);
                bean.setSortLetters(sortLetter);
                list.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    //搜索模糊查询
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
                for (int i = 0; i < aslist.size(); i++) {
                    AccountSettingAdminUserBean cus = aslist.get(i);
                    String name = et_search.getText().toString();
                    String getName = cus.getUserName().toLowerCase();//转小写
                    String nameb = name.toLowerCase();//转小写
                    if (getName.contains(nameb.toString())
                            && name.length() > 0) {
                        eList.add(cus);
                    }
                }
                if (et_search.length() != 0) {
                    asAdapter.updateListView2(eList);
                } else {
                    asAdapter.updateListView2(aslist);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

    }

    //右侧字母滑动选择监听
    private void getSide() {
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position;
                position = getPositionForSection(aslist,
                        s.charAt(0));
                if (position != -1) {
                    as_lv_admin_user.setSelection(position);
                } else {
                    as_lv_admin_user
                            .setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
            }
        });
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public static int getPositionForSection(List<AccountSettingAdminUserBean> list, char section) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getSortLetters();
            if (null == sortStr) {
                continue;
            }
            char firstChar = sortStr.charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }
}
