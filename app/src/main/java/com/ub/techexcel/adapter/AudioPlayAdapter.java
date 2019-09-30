package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2018/8/8.
 */

public class AudioPlayAdapter extends RecyclerView.Adapter<AudioPlayAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private int[] mDatas;
    private Context mContext;
    public AudioPlayListener mListener;

    int selectPosition = -1;

    public interface AudioPlayListener {
        void select(int value);
    }

    public void setAudioPlayListener(AudioPlayListener listener) {
        this.mListener = listener;
    }

    public AudioPlayAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        mDatas = new int[]{5, 6, 7, 8, 9, 10};
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.audioplat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final int second = mDatas[position];
        holder.tv.setText(second + "秒");
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.select(second);
                selectPosition = position;
                notifyDataSetChanged();
            }
        });
        if (selectPosition == position) {
            holder.imageview.setImageResource(R.drawable.finish_a);
        } else {
            holder.imageview.setImageResource(R.drawable.finish_d);
        }
    }

    @Override
    public int getItemCount() {
        int sizeLimit = mDatas.length;
        return sizeLimit;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        private ImageView imageview;

        public ViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv);
            imageview = (ImageView) view.findViewById(R.id.imageview);
        }
    }
}
