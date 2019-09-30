package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.School;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class SchoolAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private List<School> mlist = new ArrayList<>();
    private int SchoolId;


    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }


    public SchoolAdapter(List<School> mlist, int SchoolId) {
        this.mlist = mlist;
        this.SchoolId = SchoolId;
    }

    public void UpdateRV(List<School> mlist, int SchoolId) {
        this.mlist = mlist;
        this.SchoolId = SchoolId;
        notifyDataSetChanged();
    }

    public void UpdateRV2(List<School> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organization, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        School school = mlist.get(position);
        holder.tv_school.setText(school.getSchoolName());
        holder.img_choosen.setVisibility((SchoolId != school.getSchoolID()) ? View.GONE : View.VISIBLE);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_school;
        ImageView img_choosen;

        ViewHolder(View view) {
            super(view);
            tv_school = (TextView) view.findViewById(R.id.tv_school);
            img_choosen = (ImageView) view.findViewById(R.id.img_choosen);
        }
    }
}
