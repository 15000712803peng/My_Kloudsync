package com.ub.techexcel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.tool.TextTool;
import com.ub.techexcel.bean.ServiceBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2018/3/23.
 */

public class CourseAdapter extends BaseAdapter {

    private Context context;
    private List<ServiceBean> serviceList;
    public ImageLoader imageLoader;
    private int isShow;

    boolean fromSearch;
    String keyword;

    public CourseAdapter(Context context, List<ServiceBean> serviceList, boolean isPublic, int isShow) {
        this.context = context;
        this.serviceList = serviceList;
        this.isShow = isShow;
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    public OnModifyServiceListener onModifyServiceListener;

    public void setFromSearch(boolean fromSearch, String keyword) {

        this.fromSearch = fromSearch;
        this.keyword = keyword;

    }

    public interface OnModifyServiceListener {
        void select(ServiceBean bean);
    }

    public void setOnModifyServiceListener(OnModifyServiceListener onModifyServiceListener) {
        this.onModifyServiceListener = onModifyServiceListener;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return serviceList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return serviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.course_item, null);
            holder.day = (TextView) convertView.findViewById(R.id.day);
            holder.month = (TextView) convertView.findViewById(R.id.month);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.host = (TextView) convertView.findViewById(R.id.host);
            holder.attendee = (TextView) convertView.findViewById(R.id.attendee);
            holder.moreoperation = (ImageView) convertView.findViewById(R.id.moreoperation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ServiceBean bean = serviceList.get(position);
        final ViewHolder finalHolder = holder;
        holder.moreoperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onModifyServiceListener.select(bean);
            }
        });

        String start = "0:00", end = "0:00";
        String month = "", day = "";
        long number = 0;

        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat formatter2 = new SimpleDateFormat("MM");
        final SimpleDateFormat formatter3 = new SimpleDateFormat("dd");

        if (!TextUtil.isEmpty(bean.getPlanedStartDate())) {
            Date curDate = new Date(Long.parseLong(bean.getPlanedStartDate()));
            start = formatter.format(curDate);
            month = formatter2.format(curDate);
            day = formatter3.format(curDate);
        }
        if (!TextUtil.isEmpty(bean.getPlanedEndDate())) {
            Date curDate = new Date(Long.parseLong(bean.getPlanedEndDate()));
            end = formatter.format(curDate);
        }
        number =  Long.parseLong(bean.getPlanedEndDate()) - Long.parseLong(bean.getPlanedStartDate());
        long hour= number  / 3600000;               //以小时为单位取整
        long min = number % 86400000 % 3600000 / 60000;       //以分钟为单位取整
        long seconds = number % 86400000 % 3600000 % 60000 / 1000;   //以秒为单位取整
        Log.e("laoyu", "day"+day);
        String dd=hour+":"+min+":"+seconds;

        holder.title.setText(bean.getName());

        if (fromSearch) {
            if (!TextUtil.isEmpty(keyword)) {
                holder.title.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), bean.getName(), keyword));
            }
        } else {
            holder.title.setText(bean.getName());
        }

        holder.day.setText(day);
        holder.month.setText(getMonth(month));
        holder.date.setText("Meeting time: "+start + " - " + end + "(" +dd+")" );
        holder.host.setText("Host: " + bean.getTeacherName());
        holder.attendee.setText(bean.getUserName());
        return convertView;

    }


    public String getMonth(String monthnumber) {
        if (monthnumber.equals("01")) {
            return context.getResources().getString(R.string.mtJan);
        } else if (monthnumber.equals("02")) {
            return context.getResources().getString(R.string.mtFeb);
        } else if (monthnumber.equals("03")) {
            return context.getResources().getString(R.string.mtMar);
        } else if (monthnumber.equals("04")) {
            return context.getResources().getString(R.string.mtApr);
        } else if (monthnumber.equals("05")) {
            return context.getResources().getString(R.string.mtMay);
        } else if (monthnumber.equals("06")) {
            return context.getResources().getString(R.string.mtJun);
        } else if (monthnumber.equals("07")) {
            return context.getResources().getString(R.string.mtJul);
        } else if (monthnumber.equals("08")) {
            return context.getResources().getString(R.string.mtAug);
        } else if (monthnumber.equals("09")) {
            return context.getResources().getString(R.string.mtSept);
        } else if (monthnumber.equals("10")) {
            return context.getResources().getString(R.string.mtOct);
        } else if (monthnumber.equals("11")) {
            return context.getResources().getString(R.string.mtNov);
        } else if (monthnumber.equals("12")) {
            return context.getResources().getString(R.string.mtDec);
        }
        return "";
    }


    class ViewHolder {

        TextView day, month, title, date, host, attendee;
        ImageView moreoperation;

    }

}

