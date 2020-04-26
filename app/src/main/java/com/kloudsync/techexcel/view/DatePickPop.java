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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
public class DatePickPop implements View.OnClickListener, DialogInterface.OnDismissListener{
    public Activity host;
    public Dialog dialog;
    private View view;
    private LoopView lv_pick_date,lv_pick_one,lv_pick_hour,lv_pick_duration;
    private TextView tv_pick_cancel,tv_pick_confirm;
    private String curDateInfo="",curDayInfo="",curHour="", curDuration="",curDate="";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<String> dateList=new ArrayList<String>();
    private void init() {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public DatePickPop(Activity host) {
        this.host = host;
        init();
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.pick_looper_date, null);
        tv_pick_cancel=(TextView) view.findViewById(R.id.tv_pick_cancel);
        tv_pick_confirm=(TextView) view.findViewById(R.id.tv_pick_confirm);
        lv_pick_date=(LoopView) view.findViewById(R.id.lv_pick_date);
        lv_pick_one=(LoopView) view.findViewById(R.id.lv_pick_one);
        lv_pick_hour=(LoopView) view.findViewById(R.id.lv_pick_hour);
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
        setDates();
        setDurations();
        setHours();
        setOneDays();
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
                String value="",show=curDateInfo+curDayInfo+curHour+":"+curDuration;
                if(curDayInfo.equals("下午")){
                    int time=Integer.parseInt(curHour)+12;
                    value=curDate+" "+time+":"+curDuration;
                }else {
                    if(curHour.length()==1){
                        curHour=0+curHour;
                    }
                    value=curDate+" "+curHour+":"+curDuration;
                }
                if(onTimeCallBackListener!=null){
                    onTimeCallBackListener.onTimeCallBack(show,value);
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

    private void setDates(){
        final ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<=30;i++){
            Date date=new Date();//取时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE,i);//把日期往后增加一天.整数往后推,负数往前移动
            date=calendar.getTime(); //这个时间就是日期往后推一天的结果
            SimpleDateFormat formatter = new SimpleDateFormat("M-d");
            String dateString = formatter.format(date);
            String[] dates=dateString.split("-");
            String dateInfo=dates[0]+"月"+dates[1]+"日"+" "+getWeekOfDate(date);
            list.add(dateInfo);
            dateList.add(simpleDateFormat.format(date));
        }
        lv_pick_date.setItems(list);
        lv_pick_date.setNotLoop();
        lv_pick_date.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                curDateInfo=list.get(index);
                curDate=dateList.get(index);
            }
        });
        curDateInfo=list.get(0);
        curDate=dateList.get(0);
    }

    private void setDurations(){
        final ArrayList<String> list = new ArrayList<>();
        list.add("00");
        for (int i = 15; i < 60;) {
            list.add(String.valueOf(i));
            i=i+15;
        }
        // 滚动监听
        lv_pick_duration.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                curDuration=list.get(index);
            }
        });
        // 设置原始数据
        lv_pick_duration.setItems(list);
        lv_pick_duration.setNotLoop();
        curDuration=list.get(0);
    }

    private void setHours(){
        final ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <=12;i++) {
            list.add(String.valueOf(i));
        }
        // 滚动监听
        lv_pick_hour.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                curHour=list.get(index);
            }
        });
        // 设置原始数据
        lv_pick_hour.setItems(list);
        lv_pick_hour.setNotLoop();
        curHour=list.get(0);
    }

    private void setOneDays(){
        final ArrayList<String> list = new ArrayList<>();
        list.add("上午");
        list.add("下午");
        // 滚动监听
        lv_pick_one.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                curDayInfo=list.get(index);
            }
        });
        // 设置原始数据
        lv_pick_one.setItems(list);
        lv_pick_one.setNotLoop();
        curDayInfo=list.get(0);
    }


    /**
     * 获取当前日期是星期几
     * @param date
     * @return 当前日期是星期几
     */
    public String getWeekOfDate(Date date) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public interface OnTimeCallBackListener{
        void onTimeCallBack(String showTime,String valueTime);
    }
    OnTimeCallBackListener onTimeCallBackListener;

    public void setOnTimeCallBackListener(OnTimeCallBackListener onTimeCallBackListener) {
        this.onTimeCallBackListener = onTimeCallBackListener;
    }
}
