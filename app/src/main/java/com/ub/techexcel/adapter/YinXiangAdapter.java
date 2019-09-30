package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.Tools;
import com.ub.techexcel.tools.YinxiangOperatorPopup;

import java.util.List;


public class YinXiangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SoundtrackBean> mlist;
    private Context mContext;
    private Uri defaultImageUri;
    private View outView;

    public YinXiangAdapter(Context context, List<SoundtrackBean> mlist) {
        this.mContext = context;
        this.mlist = mlist;
        defaultImageUri = Tools.getUriFromDrawableRes(context, R.drawable.hello);
    }

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void editYinxiang(SoundtrackBean soundtrackBean);

        void deleteYinxiang(SoundtrackBean soundtrackBean);

        void playYinxiang(SoundtrackBean soundtrackBean);

        void shareYinxiang(SoundtrackBean soundtrackBean);

        void copyUrl(SoundtrackBean soundtrackBean);

        void shareInApp(SoundtrackBean soundtrackBean);

        void sharePopup(SoundtrackBean soundtrackBean);
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void setView(View outView) {
        this.outView = outView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yinxiang_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final SoundtrackBean soundtrackBean = mlist.get(position);
        holder.title.setText(soundtrackBean.getTitle());
        holder.username.setText(soundtrackBean.getUserName());
        holder.duration.setText(soundtrackBean.getDuration());
        if (position == mlist.size() - 1) {
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.GONE);
        }
        holder.operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YinxiangOperatorPopup yinxiangOperatorPopup = new YinxiangOperatorPopup();
                yinxiangOperatorPopup.getPopwindow(mContext);
                yinxiangOperatorPopup.setFavoritePoPListener(new YinxiangOperatorPopup.FavoritePoPListener() {
                    @Override
                    public void editYinxiang() {
                        mFavoritePoPListener.editYinxiang(soundtrackBean);
                    }

                    @Override
                    public void deleteYinxiang() {
                        mFavoritePoPListener.deleteYinxiang(soundtrackBean);
                    }

                    @Override
                    public void playYinxiang() {
                        mFavoritePoPListener.playYinxiang(soundtrackBean);
                    }

                    @Override
                    public void shareYinxiang() {
                        mFavoritePoPListener.shareYinxiang(soundtrackBean);
                    }

                    @Override
                    public void copyUrl() {
                        mFavoritePoPListener.copyUrl(soundtrackBean);
                    }

                    @Override
                    public void shareInApp() {
                        mFavoritePoPListener.shareInApp(soundtrackBean);
                    }

                    @Override
                    public void sharePopup() {

                        mFavoritePoPListener.sharePopup(soundtrackBean);


                    }
                });
                yinxiangOperatorPopup.StartPop(holder.operationmore, soundtrackBean);
            }
        });
        String url2 = soundtrackBean.getAvatarUrl();
        Uri imageUri2;
        if (!TextUtils.isEmpty(url2)) {
            imageUri2 = Uri.parse(url2);
        } else {
            imageUri2 = defaultImageUri;
        }
        holder.image.setImageURI(imageUri2);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RelativeLayout operation;
        TextView username;
        TextView duration;
        SimpleDraweeView image;
        View divider;
        ImageView operationmore;

        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            username = (TextView) view.findViewById(R.id.username);
            duration = (TextView) view.findViewById(R.id.duration);
            operation = (RelativeLayout) view.findViewById(R.id.operation);
            image = (SimpleDraweeView) view.findViewById(R.id.image);
            operationmore = view.findViewById(R.id.operationmore);
            divider = view.findViewById(R.id.divider);
        }
    }
}
