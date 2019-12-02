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
import com.kloudsync.techexcel.view.CircleImageView;
import com.kloudsync.techexcel.view.RoundProgressBar;
import com.ub.techexcel.bean.LineItem;

import java.util.List;


public class MyRecyclerAdapter2 extends RecyclerView.Adapter<MyRecyclerAdapter2.ViewHolder> {


    private LayoutInflater inflater;
    private List<LineItem> mDatas;
    public ImageLoader imageLoader;
    private MyItemClickListener mListener;
    public Context context;


    public interface MyItemClickListener {
        void onItemClick(int position);
    }

    public void setMyItemClickListener(MyItemClickListener mListener) {
        this.mListener = mListener;
    }

    public MyRecyclerAdapter2(Context context, List<LineItem> datas) {
        inflater = LayoutInflater.from(context);
        mDatas = datas;
        imageLoader = new ImageLoader(context);
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }

        CircleImageView icon;
        TextView name, identlyTv;
        LinearLayout headll, bgisshow, bgisshow2;
        RoundProgressBar rpb_update;
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
        viewHolder.identlyTv = (TextView) view.findViewById(R.id.identlyTv);
        viewHolder.headll = (LinearLayout) view.findViewById(R.id.headll);
        viewHolder.bgisshow = (LinearLayout) view.findViewById(R.id.bgisshow);
        viewHolder.bgisshow2 = (LinearLayout) view.findViewById(R.id.bgisshow2);
        viewHolder.rpb_update = (RoundProgressBar) view.findViewById(R.id.rpb_update);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        LineItem attachment = mDatas.get(position);
        holder.identlyTv.setText("");
        if (attachment.getFlag() == 0) {
            holder.bgisshow2.setVisibility(View.GONE);
            holder.rpb_update.setVisibility(View.GONE);
            holder.bgisshow.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.VISIBLE);

            holder.name.setText((position + 1) + "");
            holder.icon.setImageResource(R.drawable.documento);
            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("---------","ddddddddddd");
                    mListener.onItemClick(position);
                }
            });
            if (attachment.isSelect()) {
                holder.bgisshow.setBackgroundResource(R.drawable.course_bg1);
            } else {
                holder.bgisshow.setBackgroundResource(R.drawable.course_bg2);
            }
        } else if (attachment.getFlag() == 1) {
            holder.bgisshow2.setVisibility(View.VISIBLE);
            holder.rpb_update.setVisibility(View.VISIBLE);
            holder.bgisshow.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);

            holder.name.setTextColor(context.getResources().getColor(R.color.darkblack2));
            holder.rpb_update.setCricleProgressColor(context.getResources().getColor(R.color.qiangrey));
            holder.name.setText("Uploading");
            holder.rpb_update.setProgress(attachment.getProgress());
        } else {
            holder.bgisshow2.setVisibility(View.VISIBLE);
            holder.rpb_update.setVisibility(View.VISIBLE);
            holder.bgisshow.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);

            holder.name.setTextColor(context.getResources().getColor(R.color.back_main));
            holder.rpb_update.setCricleProgressColor(context.getResources().getColor(R.color.back_main));
            holder.name.setText("Converting");
            holder.rpb_update.setProgress(attachment.getProgress());

        }

    }


    public void setProgress(long total, long current, LineItem att) {
        int pb = (int) (current * 100 / total);
        att.setProgress(pb);
        notifyDataSetChanged();
    }

}
