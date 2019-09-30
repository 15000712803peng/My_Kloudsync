package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.ub.kloudsync.activity.Document;

import org.json.JSONObject;

public class EditDocumentDialog {

    public Context mContext;

    private static PopEditDocumentListener popEditDocumentListener;

    Document lesson;
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
                case AppConfig.RenameAttachment:
                    popEditDocumentListener.popEditSuccess();
                    mPopupWindow.dismiss();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            mContext,
                            mContext.getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();
                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            mContext,
                            mContext.getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }
        }
    };


    public interface PopEditDocumentListener {
        void popEditSuccess();
    }

    public void setPopEditDocumentListener(PopEditDocumentListener popEditDocumentListener) {
        EditDocumentDialog.popEditDocumentListener = popEditDocumentListener;
    }

    public void getPopwindow(Context context, Document lesson) {
        this.mContext = context;
        this.lesson = lesson;
        getPopupWindowInstance();
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation3);
    }

    public Dialog mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private TextView cancel, ok;
    private EditText et_title;
    private View popupWindow;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.document_edit_popup, null);
        cancel = (TextView) popupWindow.findViewById(R.id.cancel);
        ok = (TextView) popupWindow.findViewById(R.id.ok);
        et_title = (EditText) popupWindow.findViewById(R.id.et_title);
        et_title.setText(lesson.getTitle());
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(popupWindow);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8f);
        mPopupWindow.getWindow().setAttributes(lp);
        cancel.setOnClickListener(new myOnClick());
        ok.setOnClickListener(new myOnClick());

    }

    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancel:
                    mPopupWindow.dismiss();
                    break;
                case R.id.ok:
                    ChangeDuang();
                    break;

                default:
                    break;
            }
        }

    }

    private void ChangeDuang() {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SpaceAttachment/RenameAttachment?itemID=" + lesson.getItemID()
                                    + "&title=" + LoginGet.getBase64Password(et_title.getText().toString()), jsonObject);
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.RenameAttachment;
                        msg.obj = responsedata.getString("RetData");
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(mContext)) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start(ThreadManager.getManager());
    }

    public void StartPop() {
        mPopupWindow.show();
    }


}
