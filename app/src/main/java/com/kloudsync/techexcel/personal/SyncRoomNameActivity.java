
package com.kloudsync.techexcel.personal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
import com.kloudsync.techexcel.school.SwitchOrganizationActivity;
import com.kloudsync.techexcel.tool.CustomSyncRoomTool;
import com.kloudsync.techexcel.tool.PingYinUtil;
import com.kloudsync.techexcel.ui.MainActivity;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.service.activity.SyncRoomActivity;
import com.ub.techexcel.adapter.AccountSettingAdminUserAdapter;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SyncRoomNameActivity extends AppCompatActivity {

    private ImageView tv_back;
    private RelativeLayout save;
    private EditText syncroomedit, syncroomteammember, syncroomcustomer;
    private EditText syncroomediten, syncroomteammemberen, syncroomcustomeren;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting_syncroom_name);
        initView();
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void initView() {
        tv_back = findViewById(R.id.tv_back);
        save = findViewById(R.id.save);
        syncroomedit = findViewById(R.id.syncroomedit);
        syncroomteammember = findViewById(R.id.syncroomteammember);
        syncroomcustomer = findViewById(R.id.syncroomcustomer);
        syncroomediten = findViewById(R.id.syncroomediten);
        syncroomteammemberen = findViewById(R.id.syncroomteammemberen);
        syncroomcustomeren = findViewById(R.id.syncroomcustomeren);
        tv_back.setOnClickListener(new MyOnClick());
        save.setOnClickListener(new MyOnClick());
        getCompanyNameList();
    }


    private void updateSyncroomName() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("companyId", AppConfig.SchoolID);
            JSONArray jsonArray = new JSONArray();
            final String syncroomeditcontent = syncroomedit.getText() + "";
            final String syncroomteammembercontent = syncroomteammember.getText() + "";
            final String syncroomcustomercontent = syncroomcustomer.getText() + "";

            if (!TextUtils.isEmpty(syncroomeditcontent)) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("fieldId", 1001);
                jsonObject1.put("fieldName", syncroomeditcontent);
                jsonObject1.put("languageId", 0);
                jsonArray.put(jsonObject1);
            }
            if (!TextUtils.isEmpty(syncroomteammembercontent)) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("fieldId", 1002);
                jsonObject1.put("fieldName", syncroomteammembercontent);
                jsonObject1.put("languageId", 0);
                jsonArray.put(jsonObject1);
            }
            if (!TextUtils.isEmpty(syncroomcustomercontent)) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("fieldId", 1003);
                jsonObject1.put("fieldName", syncroomcustomercontent);
                jsonObject1.put("languageId", 0);
                jsonArray.put(jsonObject1);
            }

            final String syncroomeditcontenten = syncroomediten.getText() + "";
            final String syncroomteammembercontenten = syncroomteammemberen.getText() + "";
            final String syncroomcustomercontenten = syncroomcustomeren.getText() + "";

            if (!TextUtils.isEmpty(syncroomeditcontenten)) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("fieldId", 1001);
                jsonObject1.put("fieldName", syncroomeditcontenten);
                jsonObject1.put("languageId", 1);
                jsonArray.put(jsonObject1);
            }
            if (!TextUtils.isEmpty(syncroomteammembercontenten)) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("fieldId", 1002);
                jsonObject1.put("fieldName", syncroomteammembercontenten);
                jsonObject1.put("languageId", 1);
                jsonArray.put(jsonObject1);
            }
            if (!TextUtils.isEmpty(syncroomcustomercontenten)) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("fieldId", 1003);
                jsonObject1.put("fieldName", syncroomcustomercontenten);
                jsonObject1.put("languageId", 1);
                jsonArray.put(jsonObject1);
            }

            jsonObject.put("displayNameList", jsonArray);
            String url = AppConfig.URL_MEETING_BASE + "company_custom_display_name/insert_or_update_custom_display_name";
            ServiceInterfaceTools.getinstance().updateCustomDisplayName(url, ServiceInterfaceTools.UPDATECUSTOMDISPLAYNAME, jsonObject, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    editor.putString("customyinxiang", syncroomeditcontent);
                    editor.putString("customteammember", syncroomteammembercontent);
                    editor.putString("customcustomer", syncroomcustomercontent);
                    editor.putString("customyinxiangen", syncroomeditcontenten);
                    editor.putString("customteammemberen", syncroomteammembercontenten);
                    editor.putString("customcustomeren", syncroomcustomercontenten);
                    editor.commit();
                    MainActivity.RESUME = true;
                    finish();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getCompanyNameList() {
        String url = AppConfig.URL_MEETING_BASE + "company_custom_display_name/name_list?companyId=" + AppConfig.SchoolID;
        ServiceInterfaceTools.getinstance().getCompanyDisplayNameList(url, ServiceInterfaceTools.GETCOMPANYDISPLAYNAMELIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                JSONObject jsonObject = (JSONObject) object;
                CustomSyncRoomTool.getInstance(SyncRoomNameActivity.this).setCustomSyncRoomContent(jsonObject);
                String first = CustomSyncRoomTool.getInstance(SyncRoomNameActivity.this).getCustomyinxiang2();
                syncroomedit.setText(first);
                if (!TextUtils.isEmpty(first)) {
                    syncroomedit.setSelection(first.length());
                }
                syncroomediten.setText(CustomSyncRoomTool.getInstance(SyncRoomNameActivity.this).getCustomyinxiang1());
                syncroomteammember.setText(CustomSyncRoomTool.getInstance(SyncRoomNameActivity.this).getCustomteammember2());
                syncroomteammemberen.setText(CustomSyncRoomTool.getInstance(SyncRoomNameActivity.this).getCustomteammember1());
                syncroomcustomer.setText(CustomSyncRoomTool.getInstance(SyncRoomNameActivity.this).getCustomcustomer2());
                syncroomcustomeren.setText(CustomSyncRoomTool.getInstance(SyncRoomNameActivity.this).getCustomcustomer1());
            }
        });
    }


    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.save:
                    updateSyncroomName();
                    break;
                default:
                    break;
            }
        }
    }


}
