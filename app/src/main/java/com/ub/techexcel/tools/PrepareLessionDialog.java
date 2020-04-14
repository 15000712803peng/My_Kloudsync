package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.bean.SoundtrackBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class PrepareLessionDialog implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private ImageView close;
    private TextView addaudio, addrecord;
    private EditText edittext;
    private ImageView delete1, delete2;
    private TextView recordsync, cancel;
    private Document favorite = new Document();
    private Document recordfavorite = new Document();
    private CheckBox checkBox;
    private String attachmentId;
    private TextView recordname, recordtime;
    private TextView bgname, bgtime;
    private LinearLayout backgroundAudioLayout;
    private LinearLayout recordMyVoiceLayout;
    private RelativeLayout voiceItemLayout;
    private LinearLayout addVoiceLayout;
    private CheckBox isPublic;
    public PrepareLessionDialog(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.cancel();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_course_prepare, null);

//        recordsync.setText("Sync");
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
        View root = ((Activity) mContext).getWindow().getDecorView();
        params.height = root.getMeasuredHeight() * 4 / 5 + 30;
        mPopupWindow.getWindow().setAttributes(params);

    }





    @SuppressLint("NewApi")
    public void show() {
        if (mPopupWindow != null) {
            this.attachmentId = attachmentId;
            mPopupWindow.show();
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
            case R.id.close:
                dismiss();
                break;
            case R.id.addaudio:
                showSelectAudioDialog();
                break;
            case R.id.addrecord:
                showSelectAudioDialog();
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.recordsync:
                Toast.makeText(mContext,"开发中",Toast.LENGTH_SHORT).show();
//                dismiss();
//                createSoundtrack();

                break;
            case R.id.delete1:
                favorite = new Document();
                backgroundAudioLayout.setVisibility(View.INVISIBLE);
                delete1.setVisibility(View.INVISIBLE);
                addaudio.setVisibility(View.VISIBLE);
                bgname.setVisibility(View.INVISIBLE);
                bgtime.setVisibility(View.INVISIBLE);

                break;
            case R.id.delete2:
                recordfavorite = new Document();
                voiceItemLayout.setVisibility(View.INVISIBLE);
                addVoiceLayout.setVisibility(View.VISIBLE);
                delete2.setVisibility(View.INVISIBLE);
                if (checkBox.isChecked()) {
                    addrecord.setVisibility(View.INVISIBLE);
                } else {
                    addrecord.setVisibility(View.VISIBLE);
                }
//                addrecord.setVisibility(View.VISIBLE);
//                recordname.setVisibility(View.INVISIBLE);
//                recordtime.setVisibility(View.INVISIBLE);
//                recordsync.setText("Sync");
                break;
            default:
                break;
        }
    }

    SelectAudioForCreatingSyncDialog selectAudioDialog;
    private void showSelectAudioDialog(){
        if(selectAudioDialog != null){
            selectAudioDialog.cancel();
        }
        selectAudioDialog = new SelectAudioForCreatingSyncDialog(mContext);
        selectAudioDialog.show();
    }

    class BackgroudAudioInfo{
        public String audioId;
        public String audioTitle;
    }

    private BackgroudAudioInfo backgroudAudioInfo;

}
