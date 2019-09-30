package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.Tools;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2018/3/23.
 */

public class ServiceAdapter3 extends BaseAdapter {

    private Context context;
    private List<ServiceBean> serviceList;
    public ImageLoader imageLoader;
    private boolean isPublic, isShow;
    private Uri defaultImageUri;

    public ServiceAdapter3(Context context, List<ServiceBean> serviceList, boolean isPublic, boolean isShow) {
        this.context = context;
        this.serviceList = serviceList;
        this.isPublic = isPublic;
        this.isShow = isShow;
        imageLoader = new ImageLoader(context.getApplicationContext());
        defaultImageUri = Tools.getUriFromDrawableRes(context, R.drawable.hello);
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
                    R.layout.service_item3, null);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.pdfname = (TextView) convertView.findViewById(R.id.pdfname);
            holder.teachername = (TextView) convertView.findViewById(R.id.teachername);
            holder.studentname = (TextView) convertView.findViewById(R.id.studentname);
            holder.datetype = (TextView) convertView.findViewById(R.id.datetype);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.isPublic = (TextView) convertView.findViewById(R.id.isPublic);
            holder.prepareTv = (TextView) convertView.findViewById(R.id.prepareTv);


            holder.teacherimage = (SimpleDraweeView) convertView.findViewById(R.id.teacherimage);
            holder.studentimage = (SimpleDraweeView) convertView.findViewById(R.id.studentimage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ServiceBean bean = serviceList.get(position);
        holder.name.setText(bean.getName());
        holder.pdfname.setText(bean.getCourseName());
        holder.teachername.setText(bean.getTeacherName() + "");
        holder.studentname.setText(bean.getUserName() + "");

        holder.prepareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onModifyServiceListener.prepareCourse(position);
            }
        });
        if (isPublic) {
            holder.isPublic.setText("PUBLIC");
        } else {
            holder.isPublic.setText("");
        }


        String url = bean.getUserUrl();
        Uri imageUri;
        if (!TextUtils.isEmpty(url)) {
            imageUri = Uri.parse(url);
        } else {
            imageUri = defaultImageUri;
        }
        holder.studentimage.setImageURI(imageUri);

        String url2 = bean.getTeacherUrl();
        Uri imageUri2;
        if (!TextUtils.isEmpty(url2)) {
            imageUri2 = Uri.parse(url2);
        } else {
            imageUri2 = defaultImageUri;
        }
        holder.teacherimage.setImageURI(imageUri2);

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

        if (bean.getDateType() == 1) {
            holder.datetype.setText("Today");
            if (bean.getMins() < 60) {
                holder.status.setText("Starts in " + bean.getMins() + " mins");
            } else {
                holder.status.setText("Starts in " + bean.getMins() / 60 + " hours");
            }
            holder.status.setVisibility(View.VISIBLE);
        } else if (bean.getDateType() == 2) {
            holder.datetype.setText("Tomorrow");
            holder.status.setVisibility(View.GONE);
        } else if (bean.getDateType() == 3) {
            holder.datetype.setText("Later");
            holder.status.setVisibility(View.GONE);
        } else if (bean.getDateType() == 4) {
            holder.datetype.setText("Before");
            holder.status.setVisibility(View.GONE);
        } else {
            holder.status.setVisibility(View.GONE);
        }
        if (bean.isShow()) {
            holder.datetype.setVisibility(View.VISIBLE);
        } else {
            holder.datetype.setVisibility(View.GONE);
        }
        return convertView;

    }


    private ServiceAdapter3.OnModifyServiceListener onModifyServiceListener;

    public void setOnModifyServiceListener(
            ServiceAdapter3.OnModifyServiceListener onModifyServiceListener) {
        this.onModifyServiceListener = onModifyServiceListener;
    }

    public interface OnModifyServiceListener {

        void prepareCourse(int position);

    }

    class ViewHolder {
        TextView date, name, pdfname, teachername, studentname;
        TextView isPublic;
        TextView datetype;
        TextView status;
        TextView prepareTv;
        SimpleDraweeView teacherimage, studentimage;
    }

}

