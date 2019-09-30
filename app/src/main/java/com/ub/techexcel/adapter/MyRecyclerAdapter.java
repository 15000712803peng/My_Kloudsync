package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.view.CircleImageView;

import java.util.List;

/**
 * Created by wang on 2017/8/18.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {


    private LayoutInflater inflater;
    private List<Customer> mDatas;
    public ImageLoader imageLoader;

    public MyRecyclerAdapter(Context context, List<Customer> datas) {
        inflater = LayoutInflater.from(context);
        mDatas = datas;
        imageLoader = new ImageLoader(context);
    }



    public interface OnItemClickListener3{
        void  onClick(int position);
    }

    OnItemClickListener3 onItemClickListener3;

    public void setOnItemClickListener3(OnItemClickListener3 onItemClickListener3){
        this.onItemClickListener3=onItemClickListener3;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }

        CircleImageView icon;
        TextView name;
        LinearLayout headll;
        TextView identlytv;
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
        viewHolder.headll = (LinearLayout) view.findViewById(R.id.headll);
        viewHolder.identlytv = (TextView) view.findViewById(R.id.identlyTv);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Customer customer = mDatas.get(position);
        holder.name.setText(customer.getName());
        String url = customer.getUrl();
        if (null == url || url.length() < 1) {
            holder.icon.setImageResource(R.drawable.hello);
        } else {
            imageLoader.DisplayImage(url, holder.icon);
        }

        holder.identlytv.setVisibility(View.VISIBLE);
        holder.identlytv.setText("Sit On");
        if (customer.isEnterMeeting()) {
            holder.headll.setAlpha(1.0f);
        } else {
            holder.headll.setAlpha(0.5f);
        }
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener3.onClick(position);
            }
        });


    }
}
