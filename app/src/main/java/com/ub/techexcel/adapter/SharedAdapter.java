package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.view.CircleImageView;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.bean.SoundtrackBean;

import java.util.List;

/**
 * Created by wang on 2017/8/18.
 */

public class SharedAdapter extends RecyclerView.Adapter<SharedAdapter.ViewHolder> {


    private LayoutInflater inflater;
    private List<SoundtrackBean> mDatas;
    public ImageLoader imageLoader;

    public SharedAdapter(Context context, List<SoundtrackBean> datas) {
        inflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    public interface OnItemClickListener3 {
        void onClick(SoundtrackBean soundtrackBean);
    }

    public OnItemClickListener3 onItemClickListener3;

    public void setOnItemClickListener3(OnItemClickListener3 onItemClickListener3) {
        this.onItemClickListener3 = onItemClickListener3;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }

        private TextView title;
        private TextView duration;
        private LinearLayout share;
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.yinxiang_share_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.title = (TextView) view.findViewById(R.id.title);
        viewHolder.duration = (TextView) view.findViewById(R.id.duration);
        viewHolder.share = (LinearLayout) view.findViewById(R.id.share);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        final SoundtrackBean soundtrackBean = mDatas.get(position);

        holder.title.setText(soundtrackBean.getTitle());
        holder.duration.setText(soundtrackBean.getDuration());


        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener3.onClick(soundtrackBean);
            }
        });


    }


}
