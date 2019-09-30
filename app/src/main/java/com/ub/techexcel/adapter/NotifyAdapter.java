package com.ub.techexcel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ub.techexcel.bean.ServiceBean;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;

import java.util.List;

public class NotifyAdapter extends BaseAdapter {
    private Context context;
    private List<ServiceBean> serviceList;
    public ImageLoader imageLoader;

    //isPublic 判断是否公开
    public NotifyAdapter(Context context, List<ServiceBean> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        imageLoader = new ImageLoader(context.getApplicationContext());
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
                    R.layout.service_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.lineItems = (LinearLayout) convertView
                    .findViewById(R.id.lineitems);
            holder.concern = (TextView) convertView
                    .findViewById(R.id.concerValue);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;

    }

    private OnModifyCourseListener onModifyServiceListener;

    public void setOnModifyCourseListener(
            OnModifyCourseListener onModifyServiceListener) {
        this.onModifyServiceListener = onModifyServiceListener;
    }

    public interface OnModifyCourseListener {

        void join(int position);

        void leave(int position);

    }


}

