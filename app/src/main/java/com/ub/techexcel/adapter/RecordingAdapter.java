package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.RecordingBean;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.RecordingPopup;
import com.ub.techexcel.tools.Tools;

import java.util.List;

public class RecordingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RecordingBean> mlist;

    private Context mContext;


    public RecordingAdapter(Context context, List<RecordingBean> mlist) {
        this.mContext = context;
        this.mlist = mlist;

    }

    public interface FavoritePoPListener2 {

        void playYinxiang(RecordingBean soundtrackBean);
    }

    private FavoritePoPListener2 favoritePoPListener;

    public void setFavoritePoPListener(FavoritePoPListener2 favoritePoPListener) {
        this.favoritePoPListener = favoritePoPListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recording_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final RecordingBean recordingBean = mlist.get(position);
        holder.title.setText(recordingBean.getTitle());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritePoPListener.playYinxiang(recordingBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }
}
