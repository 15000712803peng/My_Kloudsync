package com.ub.techexcel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ub.techexcel.bean.ServiceBean;
import com.kloudsync.techexcel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/6/19.
 */

public class SelectCourseAdapter extends BaseAdapter {

    private Context context;
    private List<ServiceBean> list = new ArrayList<>();

    public SelectCourseAdapter(Context context, List<ServiceBean> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.service_item, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }


    class ViewHolder {
        TextView name, status, concern, kename;
        ImageView image;
        RelativeLayout rl;

    }
}
