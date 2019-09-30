package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.SaveTeamSchoolTool;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class DialogRename {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private TextView tv_cancel;
    private TextView tv_ok;
    private EditText et_rename;
    private Context mContext;

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
                    Toast.makeText(mContext,
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.Rename:
                    result = (String) msg.obj;
                    if (isteam) {
                        sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO,
                                mContext.MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putInt("teamid", itemID);
                        editor.putString("teamname", et_rename.getText().toString());
                        editor.commit();
                        SaveTeamSchoolTool st = new SaveTeamSchoolTool();
                        st.SaveToServe(mContext);
                    }
                    EventBus.getDefault().post(new TeamSpaceBean());
                    dlgGetWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    public void EditCancel(Context context, int itemID, boolean isteam) {
        this.mContext = context;
        this.itemID = itemID;
        this.isteam = isteam;

        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.setView(new EditText(mContext));
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation2);
        window.setContentView(R.layout.dialog_rename);

        dlgGetWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
//        layoutParams.width = width * 2 / 3;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        ShowTSInfo();
    }

    private void ShowTSInfo() {
        tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
        tv_ok = (TextView) window.findViewById(R.id.tv_ok);
        et_rename = (EditText) window.findViewById(R.id.et_rename);

        tv_cancel.setOnClickListener(new MyOnClick());
        tv_ok.setOnClickListener(new MyOnClick());

    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    dlgGetWindow.dismiss();
                    break;
                case R.id.tv_ok:
                    AskRename();
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

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        String name = et_rename.getText().toString();
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
