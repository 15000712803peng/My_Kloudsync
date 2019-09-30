package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class DialogANContact {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private Context mContext;
    private EditText et_phone, et_email;
    private Button btn_cancel, btn_ok;
    private ImageView img_close;
    private CheckBox cb_rc;
    private TextView tv_cphone;
    private int itemID;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void ChangeOK();
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogANContact.dialogdismissListener = dialogdismissListener;
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.InviteToCompany:
                    String result = (String) msg.obj;
                    dlgGetWindow.dismiss();
                    dialogdismissListener.ChangeOK();
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(mContext, result,
                            Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

    };

    public void EditCancel(Context context, int itemID) {
        this.mContext = context;
        this.itemID = itemID;

        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.setView(new EditText(mContext));
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation3);
        window.setContentView(R.layout.dialog_add_newc);


        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        initView();
    }

    private void initView() {
        et_phone = (EditText) window.findViewById(R.id.et_phone);
        et_email = (EditText) window.findViewById(R.id.et_email);
        btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
        btn_ok = (Button) window.findViewById(R.id.btn_ok);
        img_close = (ImageView) window.findViewById(R.id.img_close);
        cb_rc = (CheckBox) window.findViewById(R.id.cb_rc);
        tv_cphone = (TextView) window.findViewById(R.id.tv_cphone);

        btn_cancel.setOnClickListener(new MyOnClick());
        btn_ok.setOnClickListener(new MyOnClick());
        img_close.setOnClickListener(new MyOnClick());
        tv_cphone.setOnClickListener(new MyOnClick());

        ShowET();
    }

    private void ShowET() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                showKeyboard();
            }
        }, 200);


        SharedPreferences sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO,
                mContext.MODE_PRIVATE);
        AppConfig.COUNTRY_CODE = sharedPreferences.getInt("countrycode", 86);
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
    }

    public void ShowCode() {
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
    }

    public void showKeyboard() {
        if (et_phone != null) {
            //设置可获得焦点
            et_phone.setFocusable(true);
            et_phone.setFocusableInTouchMode(true);
            //请求获得焦点
            et_phone.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) et_phone
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(et_phone, 0);
        }
    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    dlgGetWindow.dismiss();
                    break;
                case R.id.tv_cphone:
                    GotoChangeCode();
                    break;
                case R.id.img_close:
                    dlgGetWindow.dismiss();
                    break;
                case R.id.btn_ok:
                    InviteToCompany();
                    break;
            }
        }
    }


    public void GotoChangeCode() {
        Intent intent = new Intent(mContext, com.kloudsync.techexcel.start.ChangeCountryCode.class);
        String code = tv_cphone.getText().toString();
        code = code.replaceAll("\\+", "");
        AppConfig.COUNTRY_CODE = Integer.parseInt(code);
        ((Activity) mContext).startActivityForResult(intent, com.kloudsync.techexcel.start.RegisterActivity.CHANGE_COUNTRY_CODE);
        ((Activity) mContext).overridePendingTransition(R.anim.tran_in4, R.anim.tran_out4);

    }

    private void InviteToCompany() {
        final JSONObject jsonObject = format();
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SchoolContact/InviteToCompany", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.InviteToCompany;
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
        }).start(ThreadManager.getManager());
    }

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            String PhoneNumber = tv_cphone.getText().toString() + et_phone.getText().toString();
            jsonObject.put("CompanyID", AppConfig.SchoolID);
            jsonObject.put("PhoneNumber", PhoneNumber);
            jsonObject.put("TeamSpaceID", itemID);
            jsonObject.put("RequestToChat", cb_rc.isChecked() ? 1 : 0);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }


}
