package com.ub.techexcel.adapter;

import java.util.List;

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

public class ServiceAdapter extends BaseAdapter {
    private Context context;
    private List<ServiceBean> serviceList;
    public ImageLoader imageLoader;
    private boolean isPublic;

    //isPublic 判断是否公开
    public ServiceAdapter(Context context, List<ServiceBean> serviceList, boolean isPublic) {
        this.context = context;
        this.serviceList = serviceList;
        imageLoader = new ImageLoader(context.getApplicationContext());
        this.isPublic = isPublic;
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;

    }


    private OnModifyServiceListener onModifyServiceListener;

    public void setOnModifyServiceListener(
            OnModifyServiceListener onModifyServiceListener) {
        this.onModifyServiceListener = onModifyServiceListener;
    }

    public interface OnModifyServiceListener {
        void onBeginStudy(int position);

        void viewCourse(int position);

        void enterCustomerDetail(int position);

        void sendSMS(int position);

    }


}
class ViewHolder {
    TextView name, status, concern, num, confirmfinish, modifyservice, kename;
    LinearLayout lineItems, entercustomer, sendsms, issmslinear;
    LinearLayout ifclose;
    ImageView image;
    ImageView smsicon;
    TextView sendcourse;

}


