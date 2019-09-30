package com.ub.techexcel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2018/6/5.
 */

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder2> {


    private LayoutInflater inflater;
    private List<String> mDatas = new ArrayList<>();
    private Context mContext;

    public interface OptionsListener {
        void select(int position);
    }

    private OptionsListener optionsListener;

    public void setOptionsListener(OptionsListener optionsListener) {
        this.optionsListener = optionsListener;
    }

    public OptionsAdapter(Context context, List<String> mDatas) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDatas = mDatas;
    }

    @Override
    public ViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.option_item, parent, false);
        ViewHolder2 viewHolder = new ViewHolder2(view);
        viewHolder.value = (TextView) view.findViewById(R.id.value);
        return viewHolder;
    }

    private int pos = 0;

    public void setPos(int pos) {
        this.pos = pos;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder2 holder, final int position) {
        holder.value.setText(mDatas.get(position));

        if (position == pos) {
            holder.value.setTextColor(mContext.getResources().getColor(R.color.c1));
        } else {
            holder.value.setTextColor(Color.BLACK);
        }

        holder.value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsListener.select(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        public ViewHolder2(View a) {
            super(a);
        }

        TextView value;
    }
}
