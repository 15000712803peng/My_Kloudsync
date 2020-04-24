package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;


import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;


public class MemberDisplayModeSettingDialog implements View.OnClickListener {
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private ImageView closeImage;
    LinearLayout modeOneLayout;
    LinearLayout modeTwoLayout;
    LinearLayout modeThreeLayout;
    private SharedPreferences sharedPreferences;
    private int displayMode = 1;
    private ImageView oneImage;
    private ImageView twoImage;
    private ImageView threeImage;

    public interface OnFavoriteDocSelectedListener {
        void onFavoriteDocSelected(String docId);
    }

    OnFavoriteDocSelectedListener onFavoriteDocSelectedListener;

    public void setOnFavoriteDocSelectedListener(OnFavoriteDocSelectedListener onFavoriteSelectedListener) {
        this.onFavoriteDocSelectedListener = onFavoriteSelectedListener;
    }

    public MemberDisplayModeSettingDialog(Context context) {
        mContext = context;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_dispaly_mode_setting, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        closeImage = view.findViewById(R.id.image_close);
        closeImage.setOnClickListener(this);
        modeOneLayout = view.findViewById(R.id.layout_mode_one);
        modeOneLayout.setOnClickListener(this);
        modeTwoLayout = view.findViewById(R.id.layout_mode_two);
        modeTwoLayout.setOnClickListener(this);
        modeThreeLayout = view.findViewById(R.id.layout_mode_three);
        modeThreeLayout.setOnClickListener(this);
        oneImage = view.findViewById(R.id.image_one);
        twoImage = view.findViewById(R.id.image_two);
        threeImage = view.findViewById(R.id.image_three);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Tools.isOrientationPortrait((Activity) mContext)) {
            heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (0.30f));
            width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * (0.85f));
        } else {

            heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (0.70f));
            width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * (0.45f));
        }
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = width;
        lp.height = heigth;
        dialog.getWindow().setAttributes(lp);
    }


    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_close:
                dismiss();
                break;
            case R.id.layout_mode_one:
                if (displayMode == 0) {
                    dismiss();
                    return;
                }

                requestChangeDisplayMode(0);
                sharedPreferences.edit().putInt("display_mode", 0).commit();
                dismiss();
                break;
            case R.id.layout_mode_two:
                if (displayMode == 1) {
                    dismiss();
                    return;
                }
                requestChangeDisplayMode(1);
                sharedPreferences.edit().putInt("display_mode", 1).commit();
                dismiss();
                break;
            case R.id.layout_mode_three:
                if (displayMode == 2) {
                    dismiss();
                    return;
                }
                requestChangeDisplayMode(2);
                sharedPreferences.edit().putInt("display_mode", 2).commit();
                dismiss();
                break;

        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO,
                    MODE_PRIVATE);
            displayMode = sharedPreferences.getInt("display_mode", 1);
            init();
            dialog.show();
        }
    }

    private void init() {

        if (displayMode == 0) {
            oneImage.setImageResource(R.drawable.accompany_select);
            twoImage.setImageResource(R.drawable.accompany_unselect);
            threeImage.setImageResource(R.drawable.accompany_unselect);
        } else if (displayMode == 1) {
            oneImage.setImageResource(R.drawable.accompany_unselect);
            twoImage.setImageResource(R.drawable.accompany_select);
            threeImage.setImageResource(R.drawable.accompany_unselect);
        } else if (displayMode == 2) {
            oneImage.setImageResource(R.drawable.accompany_unselect);
            twoImage.setImageResource(R.drawable.accompany_unselect);
            threeImage.setImageResource(R.drawable.accompany_select);
        }
    }

    private void requestChangeDisplayMode(final int mode) {
        Observable.just("change_mode").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String s) throws Exception {
                return ServiceInterfaceTools.getinstance().syncChangeCameraDisplayMode(mode);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if(jsonObject.has("code")){
                    if(jsonObject.getInt("code") == 0){
                        new CenterToast.Builder(mContext).setSuccess(true).setMessage(mContext.getString(R.string.operate_success)).create().show();
                        dismiss();
                    }
                }
            }
        }).subscribe();
    }


}
