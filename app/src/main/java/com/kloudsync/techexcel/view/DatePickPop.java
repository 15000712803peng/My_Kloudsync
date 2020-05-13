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
    private String curDateInfo="",curDayInfo="",curHour="", curDuration="",curDate="",curShowDate="";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfShow = new SimpleDateFormat("yyyy/M/d");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-H-m-s");
    String timeInfo[]=null;
    private List<String> dateList=new ArrayList<String>();
    private ArrayList<String> hourList = new ArrayList<>();
    private ArrayList<String> noons = new ArrayList<>();
    private ArrayList<String> durations = new ArrayList<>();
    public boolean mIsOverToday=false;//当前选中日期是否超过今天
    public boolean mIsOverHours=false;//当前选中小时是否超过
    public boolean mIsOverNoon=false;//
    public boolean mIsAfterNoon=false;// false 表示当前是上午 true 表示下午
    public boolean mSelectAfterNoon=false;// false 选中上午  true 选中下午
    private int currentDatePosition,currentNoonPosition,currentHourPosition,currentDurationPosition;
    private void init() {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public DatePickPop(Activity host,int currentDatePosition,int currentNoonPosition,int currentHourPosition,int currentDurationPosition) {
        this.host = host;
        this.currentDatePosition=currentDatePosition;
        this.currentNoonPosition=currentNoonPosition;
        this.currentHourPosition=currentHourPosition;
        this.currentDurationPosition=currentDurationPosition;
        if(currentDatePosition>0) mIsOverToday=true;
        init();
    }

    public void initPopuptWindow() {
        String dateNowStr = sdf.format(new Date());
        timeInfo=dateNowStr.split("-");
        if(Integer.parseInt(timeInfo[3])>12){
            mIsAfterNoon=true;
        }
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
        setCurrentDurations();
        setCurrentHours();
        setCurrentNoons();
        dialog.getWindow().setAttributes(params);
    }

    public void show(int dateIndex,int noonIndex,int hourIndex,int durationIndex) {
        if (dialog != null) {
            lv_pick_date.setInitPosition(dateIndex);
            lv_pick_one.setInitPosition(noonIndex);
            lv_pick_hour.setInitPosition(hourIndex);
            lv_pick_duration.setInitPosition(durationIndex);
            dialog.show();
        }
    }

    public void show() {
        if (dialog != null) {
            lv_pick_date.setInitPosition(currentDatePosition);
            lv_pick_one.setInitPosition(currentNoonPosition);
            lv_pick_hour.setInitPosition(currentHourPosition);
            lv_pick_duration.setInitPosition(currentDurationPosition);
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
                String value="",show=curShowDate+curDayInfo+curHour+":"+curDuration;
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
                    onTimeCallBackListener.onTimeCallBack(show,value,currentDatePosition,currentNoonPosition,currentHourPosition,currentDurationPosition);
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
        final ArrayList<String> showList = new ArrayList<>();
        for(int i=0;i<=30*12*10;i++){
            Date date=new Date();//取时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE,i);//把日期往后增加一天.整数往后推,负数往前移动
            date=calendar.getTime(); //这个时间就是日期往后推一天的结果
            SimpleDateFormat formatter = new SimpleDateFormat("M-d");
            String dateString = formatter.format(date);
            String[] dates=dateString.split("-");
            String dateInfo=dates[0]+"月"+dates[1]+"日"+" "+getWeekOfDate(date);
            if(i==0){
                list.add("今天");
            }else {
                list.add(dateInfo);
            }
            dateList.add(simpleDateFormat.format(date));
            showList.add(sdfShow.format(date));
        }
        lv_pick_date.setItems(list);
        lv_pick_date.setNotLoop();
        lv_pick_date.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                curDateInfo=list.get(index);
                curDate=dateList.get(index);
                curShowDate=showList.get(index);
                currentDatePosition=index;
                if(index>0){
                    mIsOverToday=true;
                    setCurrentHours();
                    setCurrentNoons();
                    setCurrentDurations();
                }else {
                    mIsOverToday=false;
                    currentNoonPosition=0;
                    currentHourPosition=0;
                    currentDurationPosition=0;
                    setCurrentHours();
                    setCurrentNoons();
                    setCurrentDurations();
                    lv_pick_date.setInitPosition(currentDatePosition);
                    lv_pick_one.setInitPosition(currentNoonPosition);
                    lv_pick_hour.setInitPosition(currentHourPosition);
                    lv_pick_duration.setInitPosition(currentDurationPosition);
                }

            }
        });
        curDateInfo=list.get(currentDatePosition);
        curDate=dateList.get(currentDatePosition);
        curShowDate=showList.get(currentDatePosition);
    }

    private void setCurrentDurations(){
        durations.clear();
        int mins=Integer.parseInt(timeInfo[4]);
        if(mIsOverToday||mIsOverHours||mIsOverNoon){
            durations.add("00");
            durations.add("15");
            durations.add("30");
            durations.add("45");
        }else{
            if(mins>45){
                durations.add("00");
                durations.add("15");
                durations.add("30");
                durations.add("45");
            }else if(mins<=45&&mins>30){
                durations.add("45");
            }else if(mins<=30&&mins>15){
                durations.add("30");
                durations.add("45");
            }else if(mins<=15&&mins>0){
                durations.add("15");
                durations.add("30");
                durations.add("45");
            }
        }
        // 滚动监听
        lv_pick_duration.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                curDuration=durations.get(index);
                currentDurationPosition=index;
            }
        });
        // 设置原始数据
        lv_pick_duration.setItems(durations);
        lv_pick_duration.setNotLoop();
        curDuration=durations.get(currentDurationPosition);
    }

    /**
     * 设置当前小时集合
     * */
    private void setCurrentHours(){
        hourList.clear();
        if(mIsOverToday){
            int start=1;
            for (int i = start; i <=12;i++) {
                hourList.add(String.valueOf(i));
            }
        }else {//今天
            if(mIsAfterNoon){//今天下午
                int start=1;
                if(Integer.parseInt(timeInfo[3])>12){
                    start=Integer.parseInt(timeInfo[3])-12;
                    if(Integer.parseInt(timeInfo[4])>45)
                        start+=1;
                }
                for (int i = start; i <=12;i++) {
                    hourList.add(String.valueOf(i));
                }
            }else {//今天上午
                if(mSelectAfterNoon){// 选中下午
                    for (int i = 1; i <=12;i++) {
                        hourList.add(String.valueOf(i));
                    }

                }else {//选中上午
                    int start=Integer.parseInt(timeInfo[3]);
                    if(Integer.parseInt(timeInfo[4])>45)
                        start+=1;
                    for (int i = start; i <=12;i++) {
                        hourList.add(String.valueOf(i));
                    }
                }
            }
        }
        // 滚动监听
        lv_pick_hour.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                curHour=hourList.get(index);
                currentHourPosition=index;
                if(index>0){
                    mIsOverHours=true;
                }else {
                    mIsOverHours=false;
                }
                setCurrentDurations();
                setCurrentHours();
            }
        });
        // 设置原始数据
        lv_pick_hour.setItems(hourList);
        lv_pick_hour.setNotLoop();
        curHour=hourList.get(currentHourPosition);
    }

    /**
     * 设置上午 下午
     * */
    private void setCurrentNoons(){
        noons.clear();
        if(mIsOverToday){
            noons.add("上午");
            noons.add("下午");
        }else {
           if(!mIsAfterNoon){
               noons.add("上午");
               noons.add("下午");
           }else {
               noons.add("下午");
           }
        }
        lv_pick_one.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                curDayInfo=noons.get(index);
                currentNoonPosition=index;
                if(curDayInfo.equals("下午")){
                    mSelectAfterNoon=true;
                }
                if(index>0){
                    mIsOverNoon=true;
                    mSelectAfterNoon=true;
                }else {
                    mIsOverNoon=false;
                    mSelectAfterNoon=false;
                }
                setCurrentHours();
                setCurrentDurations();
            }
        });
        // 设置原始数据
        lv_pick_one.setItems(noons);
        lv_pick_one.setNotLoop();
        curDayInfo=noons.get(currentNoonPosition);
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
        void onTimeCallBack(String showTime,String valueTime,int dataIndex,int noonIndex, int hourIndex,int durationIndex);
    }
    OnTimeCallBackListener onTimeCallBackListener;

    public void setOnTimeCallBackListener(OnTimeCallBackListener onTimeCallBackListener) {
        this.onTimeCallBackListener = onTimeCallBackListener;
    }
}
