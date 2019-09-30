package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class DeleteSpaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Customer> mlist = new ArrayList<>();


    public DeleteSpaceAdapter(List<Customer> mlist) {
        this.mlist = mlist;
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public void UpdateRV(List<Customer> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deletesp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Customer cus = mlist.get(position);
        holder.tv_name.setText(cus.getSpace().getName());
        holder.tv_space.setText(cus.getSpaceList().size() + " spaces");
        switch (position % 3){
            case 0:
                holder.img_avatar.setImageResource(R.drawable.avtar_1);
                break;
            case 1:
                holder.img_avatar.setImageResource(R.drawable.avtar_2);
                break;
            case 2:
                holder.img_avatar.setImageResource(R.drawable.avtar_6);
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_space;
        ImageView img_avatar;
        ViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_space = (TextView) view.findViewById(R.id.tv_space);
            img_avatar = (ImageView) view.findViewById(R.id.img_avatar);
        }
    }
}
