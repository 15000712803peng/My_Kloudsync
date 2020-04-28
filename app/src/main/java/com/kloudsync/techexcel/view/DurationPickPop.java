package com.kloudsync.techexcel.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.view.loopview.LoopView;
import com.kloudsync.techexcel.view.loopview.OnItemSelectedListener;
import com.ub.techexcel.tools.Tools;
import java.util.ArrayList;
public class DurationPickPop implements View.OnClickListener, DialogInterface.OnDismissListener{

    public Activity host;
    public Dialog dialog;
    private View view;
    private LoopView lv_pick_duration;
    private TextView tv_pick_cancel,tv_pick_confirm;
    private String show="";
    private long value;
    String[] durations;
    int[] times;
    ArrayList<String> listDuration = new ArrayList<>();
    ArrayList<Long> valueList = new ArrayList<>();
    private void init() {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public DurationPickPop(Activity host) {
        this.host = host;
        init();
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        durations = host.getResources().getStringArray(R.array.time_duration);
        times = host.getResources().getIntArray(R.array.time_millsecends);
        for(int i = 0 ; i < durations.length; ++i){
            valueList.add((long)(times[i] * 1000 * 60));

        }
        view = layoutInflater.inflate(R.layout.pick_looper_duration, null);
        tv_pick_cancel=(TextView) view.findViewById(R.id.tv_pick_cancel);
        tv_pick_confirm=(TextView) view.findViewById(R.id.tv_pick_confirm);
        lv_pick_duration=(LoopView) view.findViewById(R.id.lv_pick_duration);

        dialog = new Dialog(host, R.style.my_dialog);
        dialog.setContentView(view);
        dialog.setOnDismissListener(this);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        View root = host.getWindow().getDecorView();
        if (Tools.isOrientationPortrait(host)) {
            dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            params.width = host.getResources().getDisplayMetrics().widthPixels;
            params.height = Tools.dip2px(host, 220);
        } else {
            dialog.getWindow().setGravity(Gravity.RIGHT);
            params.height = root.getMeasuredHeight();
            params.width = Tools.dip2px(host, 130);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setWindowAnimations(R.style.anination3);
        }

        tv_pick_cancel.setOnClickListener(this);
        tv_pick_confirm.setOnClickListener(this);
        setDurations();
        dialog.getWindow().setAttributes(params);
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pick_confirm:
                if(onDurationCallBackListener!=null){
                    onDurationCallBackListener.onDurationCallBack(show,value);
                }
                dismiss();
            case R.id.tv_pick_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }
    private void setDurations(){
        for (int i = 0; i < durations.length;i++) {
            listDuration.add(durations[i]);
        }
        // 滚动监听
        lv_pick_duration.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                show=listDuration.get(index);
                value=valueList.get(index);
            }
        });
        // 设置原始数据
        lv_pick_duration.setItems(listDuration);
        lv_pick_duration.setNotLoop();
        show=listDuration.get(2);
        value=valueList.get(2);
    }
    public interface OnDurationCallBackListener{
        void onDurationCallBack(String show,long value);
    }
    OnDurationCallBackListener onDurationCallBackListener;
    public void setOnDurationCallBackListener(OnDurationCallBackListener onTimeCallBackListener) {
        this.onDurationCallBackListener = onTimeCallBackListener;
    }

    public void setInitPosition(int index){
        lv_pick_duration.setInitPosition(index);
    }
}
