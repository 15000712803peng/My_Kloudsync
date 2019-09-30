package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class Favourite2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private List<Document> mlist = new ArrayList<>();

    private DeleteItemClickListener deleteItemClickListener = null;

    public static interface DeleteItemClickListener {
        void AddTempLesson(int position);

        void deleteClick(View view, int position);
    }

    public void setDeleteItemClickListener(DeleteItemClickListener deleteItemClickListener) {
        this.deleteItemClickListener = deleteItemClickListener;
    }

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

    public Favourite2Adapter(List<Document> mlist) {
        this.mlist = mlist;
    }

    public void UpdateRV(List<Document> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favour2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Document favorite = mlist.get(position);
        holder.tv_favour.setText(favorite.getTitle());
        holder.tv_synccount.setText(favorite.getSyncCount() + "");
        holder.lin_sync.setVisibility((0 == favorite.getSyncCount()) ? View.GONE : View.VISIBLE);
        holder.itemView.setOnClickListener(this);

        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_favour;
        TextView tv_synccount;
        RelativeLayout lin_favour;
        LinearLayout lin_sync;

        ViewHolder(View view) {
            super(view);
            tv_favour = (TextView) view.findViewById(R.id.tv_favour);
            tv_synccount = (TextView) view.findViewById(R.id.tv_synccount);
            lin_favour = (RelativeLayout) view.findViewById(R.id.lin_favour);
            lin_sync = (LinearLayout) view.findViewById(R.id.lin_sync);
        }
    }
}
