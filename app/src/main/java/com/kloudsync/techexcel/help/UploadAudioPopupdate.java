package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.view.RoundProgressBar;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

public class UploadAudioPopupdate implements View.OnClickListener{

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private TextView recordsync,cancel,uploadstaus;
    private EditText edittext;
    private RoundProgressBar roundProgressBar;
    private SoundtrackBean soundtrackBean = new SoundtrackBean();

    public interface UploadFileAbortListener{
        void stopUpload();
    }
    private UploadFileAbortListener uploadFileAbortListener;

    public  void setUploadFileAbortListener(UploadFileAbortListener uploadFileAbortListener){
        this.uploadFileAbortListener=uploadFileAbortListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        initPopuptWindow();
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.uploadaudiopopudata, null);
        recordsync=view.findViewById(R.id.recordsync);
        cancel=view.findViewById(R.id.cancel);
        uploadstaus=view.findViewById(R.id.uploadstaus);
        recordsync.setOnClickListener(this);
        cancel.setOnClickListener(this);
        edittext=view.findViewById(R.id.edittext);
        roundProgressBar=view.findViewById(R.id.rpb_update);
        roundProgressBar.setProgress(0);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        if (Tools.isOrientationPortrait((Activity) mContext)) {
            View root = ((Activity) mContext).getWindow().getDecorView();
            params.width = root.getMeasuredWidth()*9/10;
        }else{
            params.width = mContext.getResources().getDisplayMetrics().widthPixels * 1 / 2;
        }
        mPopupWindow.getWindow().setAttributes(params);

    }



    public void setUploadStaus(int retCode){
        if(retCode==0){
            uploadstaus.setText(mContext.getResources().getText(R.string.uploadsuccess));
        }else if(retCode==1){
            uploadstaus.setText(mContext.getResources().getText(R.string.uploadfailure));
        }
    }


    @SuppressLint("NewApi")
    public void StartPop( SoundtrackBean soundtrackBean) {
        if (mPopupWindow != null) {
            this.soundtrackBean=soundtrackBean;
            recordsync.setEnabled(false);
            if(soundtrackBean!=null){
                String title=soundtrackBean.getTitle();
                edittext.setText(title);
                if(!TextUtils.isEmpty(title)){
                    edittext.setSelection(title.length());
                }
            }
            try {
                mPopupWindow.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isShowing() {
        if (mPopupWindow != null) {
          return mPopupWindow.isShowing();
        }
        return false;
    }

    public void dismiss() {
        hideSoftKeyboard(mContext,edittext);
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow=null;
        }
    }

    /**
     * 隐藏软键盘(有输入框)
     */
    public static void hideSoftKeyboard( Context context, EditText mEditText) {
        InputMethodManager inputmanger = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recordsync:
                soundtrackBean.setTitle(edittext.getText().toString());
                String url= AppConfig.URL_PUBLIC+"Soundtrack/UpdateTitleAndVisibility";
                ServiceInterfaceTools.getinstance().UpdateTitleAndVisibility(url, ServiceInterfaceTools.UPDATETITLEANDVISIBILITY, soundtrackBean, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        dismiss();
                    }
                });
                break;
            case R.id.cancel:
                uploadFileAbortListener.stopUpload();
                roundProgressBar.setProgress(0);
                break;
            default:
                break;
        }
    }

    public void setProgress(long total, long current) {
        if(mPopupWindow!=null){
            if (!mPopupWindow.isShowing()) {
                return;
            }
            int pb = (int) (current * 100 / total);
            roundProgressBar.setProgress(pb);
        }

    }

    public void setProgress1(long total, long current) {
        if(mPopupWindow!=null){
        if (!mPopupWindow.isShowing()) {
            return;
        }
        int pb = (int) (current * 50 / total);
        roundProgressBar.setProgress(pb);
        Log.e("syncing---docu","进度值  "+pb);
    }}


    public void setProgress2(long total, long current) {
        if(mPopupWindow!=null){
        if (!mPopupWindow.isShowing()) {
            return;
        }
        int pb = (int) (current * 50 / total);
        roundProgressBar.setProgress(50+pb);
        Log.e("syncing---docu","进度值  "+(50+pb));
    }}



    public void setCanChangTitle() {
        if(recordsync!=null){
            recordsync.setEnabled(true);
        }
    }
}
