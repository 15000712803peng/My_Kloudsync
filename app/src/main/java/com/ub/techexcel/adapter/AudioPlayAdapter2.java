package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.Document;

import java.util.List;

/**
 * Created by wang on 2018/8/8.
 */

public class AudioPlayAdapter2 extends RecyclerView.Adapter<AudioPlayAdapter2.ViewHolder> {

    private LayoutInflater inflater;
    private List<Document> mDatas;
    private Context mContext;
    public AudioPlayListener2 mListener;

    int selectPosition = -1;

    public interface AudioPlayListener2 {
        void select(Document value);
    }

    public void setAudioPlay2Listener(AudioPlayListener2 listener) {
        this.mListener = listener;
    }

    public AudioPlayAdapter2(Context context, List<Document> mDatas) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.audioplat_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Document favorite = mDatas.get(position);

        holder.tv.setText(favorite.getTitle());
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPosition = position;
                mListener.select(favorite);
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
        int sizeLimit = mDatas.size();
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
