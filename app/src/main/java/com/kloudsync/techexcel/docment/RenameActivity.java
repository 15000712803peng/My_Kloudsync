package com.kloudsync.techexcel.docment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class RenameActivity extends Activity {

    private ImageView img_notice;
    private Button btn_cancel;
    private Button btn_ok;
    private EditText et_heihei;
    private TextView tv_title;
    private TextView tv_fs;

    private int itemID;
    private boolean isteam;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(RenameActivity.this,
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.Rename:
                    result = (String) msg.obj;
                    if(isteam) {
                        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                                MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putInt("teamid", itemID);
                        editor.putString("teamname", et_heihei.getText().toString());
                        editor.commit();
                    }
                    EventBus.getDefault().post(new TeamSpaceBean());
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename);
        itemID = getIntent().getIntExtra("itemID", 0);
        isteam = getIntent().getBooleanExtra("isteam", false);
        initView();
    }

    private void initView() {
        img_notice = (ImageView) findViewById(R.id.img_notice);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        et_heihei = (EditText) findViewById(R.id.et_heihei);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_fs = (TextView) findViewById(R.id.tv_fs);

        tv_title.setText(isteam ? "Team Property" : "Space Property");

        img_notice.setOnClickListener(new MyOnClick());
        btn_cancel.setOnClickListener(new MyOnClick());
        btn_ok.setOnClickListener(new MyOnClick());

        getTeamItem();
    }

    public void getTeamItem() {

        TeamSpaceInterfaceTools.getinstance().getTeamItem(AppConfig.URL_PUBLIC + "TeamSpace/Item?itemID=" + itemID,
                TeamSpaceInterfaceTools.GETTEAMITEM, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        TeamSpaceBean teamSpaceBean = (TeamSpaceBean) object;
                        et_heihei.setText(teamSpaceBean.getName());
                        if(teamSpaceBean.getName().length() > 0) {
                            tv_fs.setText(teamSpaceBean.getName().substring(0, 1));
                        }else{
                            tv_fs.setText("");
                        }
                    }
                });

    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_notice:
                    finish();
                    break;
                case R.id.btn_cancel:
                    finish();
                    break;
                case R.id.btn_ok:
                    AskRename();
                    break;

                default:
                    break;
            }
        }
    }


    private void AskRename() {
        final JSONObject jsonObject = format();
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "TeamSpace/UpdateTeamSpace", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.Rename;
                        msg.obj = responsedata.toString();
                    }else{
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
        }).start(((App) getApplication()).getThreadMgr());
    }

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();

        String name = et_heihei.getText().toString();
        try {
            jsonObject.put("ID", itemID);
            jsonObject.put("Name", name);
            jsonObject.put("Note", name);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }


}
