package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/8/18.
 */

public class TeacherRecyclerAdapter extends RecyclerView.Adapter<TeacherRecyclerAdapter.ViewHolder> {


    private LayoutInflater inflater;
    private List<Customer> mDatas = new ArrayList<>();
    public ImageLoader imageLoader;

    public TeacherRecyclerAdapter(Context context, List<Customer> datas) {
        inflater = LayoutInflater.from(context);
        mDatas.clear();
        mDatas.addAll(datas);
        if(mDatas.size()>0){
            Customer customer2=new Customer();
            for (Customer customer : mDatas) {
                if(customer.getRole()==2){
                    customer2=customer;
                    mDatas.remove(customer);
                    mDatas.add(0,customer2);
                    break;
                }
            }
        }
        imageLoader = new ImageLoader(context);
    }

    public List<Customer> getmDatas() {
        return mDatas;
    }

    public synchronized void Update(List<Customer> datas) {
        Log.e("UpdateUpdateUpdate",datas.size()+"");
        mDatas.clear();
        mDatas.addAll(datas);
        if(mDatas.size()>0){
            Customer customer2=new Customer();
            for (Customer customer : mDatas) {
                if(customer.getRole()==2){
                    customer2=customer;
                    mDatas.remove(customer);
                    mDatas.add(0,customer2);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }

        CircleImageView icon;
        TextView name;
        TextView identlytv;
        LinearLayout bgisshow;
        LinearLayout headll;
    }


    public interface OnItemClickListener2 {
        void onClick(Customer position);
    }

    private OnItemClickListener2 onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener2 onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.icon = (CircleImageView) view.findViewById(R.id.studenticon);
        viewHolder.name = (TextView) view.findViewById(R.id.studentname);
        viewHolder.identlytv = (TextView) view.findViewById(R.id.identlyTv);
        viewHolder.bgisshow = (LinearLayout) view.findViewById(R.id.bgisshow);
        viewHolder.headll = (LinearLayout) view.findViewById(R.id.headll);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
      final  Customer customer = mDatas.get(position);
        holder.name.setText(customer.getName());

        String url = customer.getUrl();
        if (null == url || url.length() < 1) {
            holder.icon.setImageResource(R.drawable.hello);
        } else {
            imageLoader.DisplayImage(url, holder.icon);
        }
        holder.identlytv.setVisibility(View.VISIBLE);
        if (customer.getRole() == 2) {
            holder.identlytv.setText("Teacher");
        } else if (customer.getRole() == 1) {
            holder.identlytv.setText("Student");
        }
        if (customer.isPresenter()) {
            holder.bgisshow.setBackgroundResource(R.drawable.course_bg1);
        } else {
            holder.bgisshow.setBackgroundResource(R.drawable.course_bg2);
        }
        if (customer.isEnterMeeting()) {
            holder.headll.setAlpha(1.0f);
        } else {
            holder.headll.setAlpha(0.5f);
        }
        if (customer.isOnline()) {
            holder.headll.setAlpha(1.0f);
            holder.headll.setClickable(true);
        } else {
            holder.headll.setAlpha(0.5f);
            holder.headll.setClickable(false);
        }

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(customer);
            }
        });


    }
}
