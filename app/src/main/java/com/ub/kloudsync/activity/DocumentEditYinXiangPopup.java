package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.bean.SoundtrackBean;

import org.json.JSONObject;


public class DocumentEditYinXiangPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private TextView cancel, ok;
    private SoundtrackBean soundtrackBean;
    private EditText edittext;

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void open();

        void dismiss();

        void editSuccess();

    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }


    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.document_yinxiang_popup, null);
        cancel = (TextView) view.findViewById(R.id.cancel);
        edittext = (EditText) view.findViewById(R.id.edittext);
        ok = (TextView) view.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
//        mPopupWindow = new Dialog(view, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);


    }


    @SuppressLint("NewApi")
    public void StartPop(View v, SoundtrackBean soundtrackBean) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            this.soundtrackBean = soundtrackBean;
            mPopupWindow.show();
            edittext.setText(soundtrackBean.getTitle() + "");
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                dismiss();
                editSoundtrack();
                break;
            default:
                break;
        }
    }


    private void editSoundtrack() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("SoundtrackID", soundtrackBean.getSoundtrackID());
                    jsonObject.put("Title", edittext.getText().toString());
                    jsonObject.put("IsPublic", 1);
                    JSONObject returnjson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Soundtrack/UpdateTitleAndVisibility", jsonObject);
                    Log.e("hhh", jsonObject.toString() + "      " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        mFavoritePoPListener.editSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

}
