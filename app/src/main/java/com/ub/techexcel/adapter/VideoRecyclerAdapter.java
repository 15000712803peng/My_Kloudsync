package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;

import java.util.List;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

/**
 * Created by wang on 2017/8/18.
 */

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Integer> mDatas;
    public ImageLoader imageLoader;
    private Context mContext;
    private RtcEngine rtcEngine;

    public VideoRecyclerAdapter(Context context, RtcEngine rtcEngine, List<Integer> datas) {
        this.mContext = context;
        this.rtcEngine = rtcEngine;
        inflater = LayoutInflater.from(context);
        mDatas = datas;
        imageLoader = new ImageLoader(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }

        FrameLayout frameLayout;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.frameLayout = (FrameLayout) view.findViewById(R.id.video_view_container);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Integer userid = mDatas.get(position);

        SurfaceView surfaceView = RtcEngine.CreateRendererView(mContext);
        surfaceView.setZOrderMediaOverlay(true);
        holder.frameLayout.addView(surfaceView);

        if ((userid+"").equals(AppConfig.UserID.replace("-", ""))) {
            rtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, Integer.parseInt(userid+"")));
        } else {
            rtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, Integer.parseInt(userid+"")));
        }

    }


}
