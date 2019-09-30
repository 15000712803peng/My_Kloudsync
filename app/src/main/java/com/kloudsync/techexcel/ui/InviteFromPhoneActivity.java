package com.kloudsync.techexcel.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.response.InviteResponse;
import com.kloudsync.techexcel.start.ChangeCountryCode;
import com.kloudsync.techexcel.tool.ContactsTool;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteFromPhoneActivity extends Activity implements View.OnClickListener {

    private EditText phoneNumEdit;
    private Button inviteBtn;
    private RelativeLayout backLayout;
    private Switch sw_rc;
    private TextView tv_cphone;
    private TextView tv_title;
    private int itemID;
    ContactsTool contactsTool;
    private RelativeLayout openContactLayout;
    int inviteType = -1;
    int inviteTo = -1;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.InviteToCompany:
                    String result = (String) msg.obj;
                    EventBus.getDefault().post(new TeamSpaceBean());
                    finish();
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(InviteFromPhoneActivity.this, result,
                            Toast.LENGTH_LONG).show();
                    break;

                case AppConfig.INVITE_FAIL:
                    String failMessage = (String) msg.obj == null ? "邀请失败" : (String) msg.obj;
                    Toast.makeText(getApplicationContext(), failMessage, Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        itemID = getIntent().getIntExtra("team_id", 0);
        inviteType = getIntent().getIntExtra("invite_type", 0);
        contactsTool = new ContactsTool();
        initView();
    }

    private void initView() {
        phoneNumEdit = findViewById(R.id.edit_phone);
        inviteBtn = findViewById(R.id.btn_invite);
        backLayout = findViewById(R.id.layout_back);
        sw_rc = findViewById(R.id.sw_rc);
        tv_cphone = findViewById(R.id.tv_cphone);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("Invite new");
        inviteBtn.setOnClickListener(this);
        backLayout.setOnClickListener(this);
        tv_cphone.setOnClickListener(this);
        openContactLayout = findViewById(R.id.layout_open_contact);
        openContactLayout.setOnClickListener(this);
        ShowET();
    }

    private void ShowET() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        AppConfig.COUNTRY_CODE = sharedPreferences.getInt("countrycode", 86);
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
    }

    public void ShowCode() {
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cphone:
                GotoChangeCode();
                break;
            case R.id.layout_back:
                finish();
                break;
            case R.id.layout_open_contact:
                contactsTool.getContact(this);
                break;
            case R.id.btn_invite:
                invite(phoneNumEdit.getText().toString().trim());

//                if (0 == itemID) {
//                    finish();
//                } else {
//                    InviteToCompany();
//                }
                break;
        }
    }

    private void invite(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(getApplicationContext(), "请输入或者从通讯录中选择手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (inviteType == 0) {
            inviteTo = AppConfig.SchoolID;
        } else if (inviteType == 3) {
            inviteTo = itemID;
        }

        ServiceInterfaceTools.getinstance().inviteNewToCompany(mobile, inviteType, inviteTo, sw_rc.isChecked() ? 1 : 0).enqueue(new Callback<InviteResponse>() {
            @Override
            public void onResponse(Call<InviteResponse> call, Response<InviteResponse> response) {
                if (response != null && response.isSuccessful()) {
                    Log.e("duang123", response.body().toString() + "   :");

                    if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                        Toast.makeText(getApplicationContext(), "邀请成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String msg = getString(R.string.operate_failure);
                        if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                            msg = response.body().getErrorMessage();
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        Message message = handler.obtainMessage(AppConfig.INVITE_FAIL);
                        message.obj = response.body().getErrorMessage();
                        message.sendToTarget();
                    }
                }
            }

            @Override
            public void onFailure(Call<InviteResponse> call, Throwable t) {
                Message message = handler.obtainMessage(AppConfig.INVITE_FAIL);
                message.obj = "网络异常，邀请失败";
                message.sendToTarget();
            }
        });
    }

    public void GotoChangeCode() {
        Intent intent = new Intent(this, ChangeCountryCode.class);
        String code = tv_cphone.getText().toString();
        code = code.replaceAll("\\+", "");
        AppConfig.COUNTRY_CODE = Integer.parseInt(code);
        startActivityForResult(intent, com.kloudsync.techexcel.start.RegisterActivity.CHANGE_COUNTRY_CODE);
        overridePendingTransition(R.anim.tran_in4, R.anim.tran_out4);
    }

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            String PhoneNumber = tv_cphone.getText().toString() + phoneNumEdit.getText().toString();
            jsonObject.put("CompanyID", AppConfig.SchoolID);
            jsonObject.put("PhoneNumber", PhoneNumber);
            jsonObject.put("TeamSpaceID", itemID);
//            jsonObject.put("RequestToChat", cb_rc.isChecked() ? 1 : 0);
            jsonObject.put("RequestToChat", sw_rc.isChecked() ? 1 : 0);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case com.kloudsync.techexcel.start.RegisterActivity.CHANGE_COUNTRY_CODE:
                    ShowCode();
                    break;
                case ContactsTool.REQUEST_CONTACTS:
                    String phoneNume = contactsTool.contactResponse(this, data);
                    if (!TextUtils.isEmpty(phoneNume)) {
                        phoneNumEdit.setText(phoneNume);
                    }
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ContactsTool.REQUEST_CONTACTS_PERMISSION) {
            contactsTool.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }

    }
}
