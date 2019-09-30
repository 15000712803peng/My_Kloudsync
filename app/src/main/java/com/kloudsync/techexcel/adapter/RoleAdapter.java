package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class RoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private List<String> mlist = new ArrayList<>();

    private int role;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(String name, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            int s = (Integer) v.getTag();
            UpdateRV(s);
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(mlist.get(s), (Integer) v.getTag());
        }
    }

    public RoleAdapter(List<String> mlist, int role) {
        this.mlist = mlist;
        this.role = role;
    }

    public void UpdateRV(int role) {
        this.role = role;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_role, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tv_name.setText(mlist.get(position));
        holder.tv_name.setTextColor(holder.tv_name.getResources().getColor(position == role ? R.color.green : R.color.darkgrey));
        holder.lin_role.setOnClickListener(this);
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        LinearLayout lin_role;

        ViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            lin_role = (LinearLayout) view.findViewById(R.id.lin_role);
        }
    }
}
