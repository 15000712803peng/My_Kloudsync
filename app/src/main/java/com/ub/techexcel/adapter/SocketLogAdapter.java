package com.ub.techexcel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/10/16.
 */

public class SocketLogAdapter extends BaseAdapter {

    private List<String> mList = new ArrayList<>();
    private Context mContext;


    @Override
    public int getCount() {
        return mList.size();
    }

    public SocketLogAdapter(Context context, List<String> list) {
        this.mContext = context;
        mList = list;
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.socketlog_item, null);
            viewHolder.tv = (TextView) view.findViewById(R.id.logcontent);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv.setText(mList.get(i));
        return view;
    }


    class ViewHolder {
        TextView tv;
    }

}
