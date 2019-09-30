package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.TvDevice;

import java.util.ArrayList;
import java.util.List;

public class TvDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private List<TvDevice> mlist = new ArrayList<>();

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(final View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public TvDeviceAdapter(List<TvDevice> devices) {
        this.mlist = mlist;
    }

    public void setDevices(List<TvDevice> devices) {
        this.mlist.clear();
        this.mlist.addAll(devices);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final TvDevice device = mlist.get(position);
        holder.nameText.setText(device.getDeviceName());
        holder.timeText.setText(device.getLoginTime());

    }


    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameText;
        TextView timeText;

        ViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.txt_name);
            timeText = (TextView) view.findViewById(R.id.txt_time);
        }
    }
}
