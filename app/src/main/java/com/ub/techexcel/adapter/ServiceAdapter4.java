package com.ub.techexcel.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.ub.service.activity.MeetingPropertyActivity;
import com.ub.service.activity.MeetingShareActivity;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.MeetingMoreOperationPopup;
import com.ub.techexcel.tools.Tools;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2018/3/23.
 */

public class ServiceAdapter4 extends BaseAdapter {

    private Context context;
    private List<ServiceBean> serviceList;

    public ServiceAdapter4(Context context, List<ServiceBean> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
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
                    R.layout.service_item4, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.tv = (TextView) convertView.findViewById(R.id.tv);
            holder.count = (TextView) convertView.findViewById(R.id.count);
            holder.more = (ImageView) convertView.findViewById(R.id.more);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ServiceBean bean = serviceList.get(position);
        holder.title.setText(bean.getName());
        if (!TextUtils.isEmpty(bean.getName())) {
            holder.tv.setText(bean.getName().substring(0, 1));
        }
        String start = "0:00", end = "0:00";
        String year = "";
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        if (!TextUtil.isEmpty(bean.getPlanedStartDate())) {
            Date curDate = new Date(Long.parseLong(bean.getPlanedStartDate()));
            start = formatter.format(curDate);
            year = formatter2.format(curDate);
        }
        if (!TextUtil.isEmpty(bean.getPlanedEndDate())) {
            Date curDate = new Date(Long.parseLong(bean.getPlanedEndDate()));
            end = formatter.format(curDate);
        }
        if (bean.getDateType() == 3 || bean.getDateType() == 4) {
            holder.date.setText(year + " " + start + " ~ " + end);
        } else {
            holder.date.setText(start + " ~ " + end);
        }

        int count=bean.getTeacherCount()+bean.getStudentCount();
        if(count!=0){

            holder.count.setText("| "+count+" attendees");
        }

        return convertView;

    }


    class ViewHolder {
        TextView date;
        TextView title;
        TextView tv;
        TextView count;
        ImageView more;
    }

}

