package com.kloudsync.techexcel.docment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

public class EditSpaceActivity extends AppCompatActivity {

    private RelativeLayout backLayout;
    private TextView createbtn;
    private EditText spaceNameEdit;
    private int itemID;
    private TextView titleText;
    String spaceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editteam);
        itemID = getIntent().getIntExtra("space_id", 0);
        spaceName = getIntent().getStringExtra("space_name");
        initView();

    }

    private void initView() {
        createbtn = (TextView) findViewById(R.id.createbtn);
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        spaceNameEdit = (EditText) findViewById(R.id.edit_team_name);
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText("Edit space");
        if (!TextUtils.isEmpty(spaceName)) {
            spaceNameEdit.setText(spaceName);
        }
        createbtn.setOnClickListener(new MyOnClick());
        backLayout.setOnClickListener(new MyOnClick());
    }


    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_back:
                    finish();
                    break;
                case R.id.createbtn:
//                    finish();
                    updateSpace();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateSpace() {
        final String editName = spaceNameEdit.getText().toString();
        if (TextUtils.isEmpty(editName)) {
            Toast.makeText(getApplication(), "please edit space name", Toast.LENGTH_SHORT).show();
            return;
        }
        final JSONObject params = getParams(editName);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "TeamSpace/UpdateTeamSpace", params);
                    Log.e("jsonObject", params.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        updateSuccess(editName);
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private JSONObject getParams(String editName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", itemID);
            jsonObject.put("Name", editName);
            jsonObject.put("Note", editName);
//            jsonObject.put("Note", Base64.encodeToString(editName.getBytes(),Base64.DEFAULT));
//            jsonObject.put("Name", Base64.encodeToString(editName.getBytes(),Base64.DEFAULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void updateSuccess(final String newName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.operate_success, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
