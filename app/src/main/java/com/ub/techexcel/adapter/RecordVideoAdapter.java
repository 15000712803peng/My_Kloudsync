package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.AgoraBean;
import com.ub.techexcel.bean.SectionVO;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.view.CustomVideoView;

import java.util.ArrayList;
import java.util.List;

import io.agora.openlive.ui.VideoViewEventListener;

/**
 * Created by wang on 2018/8/8.
 */

public class RecordVideoAdapter extends RecyclerView.Adapter<RecordVideoAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<SectionVO> mDatas;
    private Context context;

    public RecordVideoAdapter(Context context, List<SectionVO> datas) {
        inflater = LayoutInflater.from(context);
        mDatas = datas;
        this.context = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }
        CustomVideoView customVideoView;
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recordvideo_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.customVideoView = view.findViewById(R.id.recordscreenvideo);

        viewHolder.customVideoView.setMediaController(new MediaController(context));
        viewHolder.customVideoView.setZOrderOnTop(true);
        viewHolder.customVideoView.setZOrderMediaOverlay(true);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final SectionVO sectionVO = mDatas.get(position);
        Uri uri = Uri.parse(sectionVO.getFileUrl());
        holder.customVideoView.setVideoURI(uri);
        if (!holder.customVideoView.isPlaying()) {
            holder.customVideoView.start();
        }
        MediaController mc = new MediaController(context);
        mc.setVisibility(View.INVISIBLE);
        holder.customVideoView.setMediaController(mc);

    }


}
