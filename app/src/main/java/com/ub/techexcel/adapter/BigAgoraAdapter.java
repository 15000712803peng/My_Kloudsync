package com.ub.techexcel.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.AgoraBean;
import com.kloudsync.techexcel.R;

import java.util.ArrayList;
import java.util.List;

import io.agora.openlive.ui.VideoViewEventListener;

/**
 * Created by wang on 2018/8/8.
 */

public class BigAgoraAdapter extends RecyclerView.Adapter<BigAgoraAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<AgoraBean> mDatas;
    private Context mContext;
    public VideoViewEventListener mListener;

    public void setItemEventHandler(VideoViewEventListener listener) {
        this.mListener = listener;
    }

    public BigAgoraAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        mDatas = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_view_container2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder myHolder = holder;
        final AgoraBean agoraBean = mDatas.get(position);
        final FrameLayout holderView = (FrameLayout) myHolder.itemView;
        holderView.removeAllViews();
        Log.e("------------big--", agoraBean.getuId() + "    " + holderView.getChildCount());
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        Rect frame = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int notifiheight = frame.top;
        height -= notifiheight;
        int framelayoutHeight;
        if (mDatas.size() <= 2) {
            framelayoutHeight = height;
        } else if (mDatas.size() > 2 && mDatas.size() <= 10) {
            framelayoutHeight = height / 2;
        } else {
            int line = ((mDatas.size() % 5) == 0 ? 0 : 1);
            framelayoutHeight = height / (mDatas.size() / 5 + line);
        }
        if (holderView.getChildCount() == 0) {
            if (agoraBean.isMuteVideo() == false) {
                SurfaceView target = agoraBean.getSurfaceView();
                stripSurfaceView(target);
                holderView.addView(target, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, framelayoutHeight));
            }
            holderView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, framelayoutHeight));
            View d = inflater.inflate(R.layout.framelayout_head, null);
            TextView videoname = (TextView) d.findViewById(R.id.videoname);
            videoname.setText(agoraBean.getUserName());
            ViewParent parent = videoname.getParent();
            if (parent != null) {
                ((RelativeLayout) parent).removeView(videoname);
            }
            holderView.addView(videoname);
        }
        holderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!agoraBean.isSelect()) {
                    agoraBean.setSelect(true);
                    View d = inflater.inflate(R.layout.framelayout_head, null);
                    RelativeLayout relativeLayout = (RelativeLayout) d.findViewById(R.id.item_rl);
                    final ImageView audioIv = (ImageView) relativeLayout.findViewById(R.id.iv1);
                    final ImageView videoIv = (ImageView) relativeLayout.findViewById(R.id.iv2);
                    final ImageView enlargeIv = (ImageView) relativeLayout.findViewById(R.id.iv3);
                    audioIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (agoraBean.isMuteAudio() == false) {  //学生的audio没被禁止
                                mListener.closeOtherAudio(agoraBean);
                            } else {
                                mListener.openMyAudio(agoraBean);
                            }
                        }
                    });
                    videoIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (agoraBean.isMuteVideo() == false) {  //学生的Video没被禁止
                                mListener.closeOtherVideo(agoraBean);
                            } else {
                                mListener.openMyVideo(agoraBean);
                            }
                        }
                    });
                    enlargeIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.isEnlarge(agoraBean);
                        }
                    });
                    if (WatchCourseActivity2.watch2instance) {
                        if (agoraBean.isMuteAudio()) {
                            audioIv.setImageResource(R.drawable.icon_command_mic_disable);
                        } else {
                            audioIv.setImageResource(R.drawable.icon_command_mic_enabel);
                        }
                        if (agoraBean.isMuteVideo()) {
                            videoIv.setImageResource(R.drawable.icon_command_webcam_disable);
                        } else {
                            videoIv.setImageResource(R.drawable.icon_command_webcam_enable);
                        }
                        if (WatchCourseActivity2.mViewType == 1) {  //当前处于放大模式
                            enlargeIv.setImageResource(R.drawable.icon_fullscreen);
                        } else if (WatchCourseActivity2.mViewType == 2) {  //当前处于 全屏 模式
                            enlargeIv.setImageResource(R.drawable.icon_restore);
                        }
                    } else if (WatchCourseActivity3.watch3instance) {
                        if (agoraBean.isMuteAudio()) {
                            audioIv.setImageResource(R.drawable.icon_command_mic_disable);
                        } else {
                            audioIv.setImageResource(R.drawable.icon_command_mic_enabel);
                        }
                        if (agoraBean.isMuteVideo()) {
                            videoIv.setImageResource(R.drawable.icon_command_webcam_disable);
                        } else {
                            videoIv.setImageResource(R.drawable.icon_command_webcam_enable);
                        }
                        if (WatchCourseActivity3.mViewType == 1) {  //当前处于放大模式
                            enlargeIv.setImageResource(R.drawable.icon_fullscreen);
                        } else if (WatchCourseActivity3.mViewType == 2) {  //当前处于 全屏 模式
                            enlargeIv.setImageResource(R.drawable.icon_restore);
                        }
                    }
                    ViewGroup parent = (ViewGroup) relativeLayout.getParent();
                    if (parent != null) {
                        parent.removeView(relativeLayout);
                    }
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.BOTTOM;
                    holderView.addView(relativeLayout, lp);
                } else {
                    agoraBean.setSelect(false);
                    RelativeLayout relativeLayout = (RelativeLayout) holderView.findViewById(R.id.item_rl);
                    if (relativeLayout != null) {
                        holderView.removeView(relativeLayout);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int sizeLimit = mDatas.size();
        return sizeLimit;
    }

    public AgoraBean getItem(int position) {
        return mDatas.get(position);
    }

    public void setData(List<AgoraBean> datas, String teacherId) {
        if (datas != null) {
            mDatas.clear();
            for (AgoraBean mData : datas) {
                Log.e("bigsetData", mData.getUserName() + "        ");
                SurfaceView surfaceView = mData.getSurfaceView();
                surfaceView.setVisibility(View.VISIBLE);
                surfaceView.setZOrderOnTop(true);
                surfaceView.setZOrderMediaOverlay(true);
                AgoraBean agoraBean = new AgoraBean();
                agoraBean.setuId(mData.getuId());
                agoraBean.setSurfaceView(surfaceView);
                agoraBean.setMuteAudio(mData.isMuteAudio());
                agoraBean.setMuteVideo(mData.isMuteVideo());
                agoraBean.setUserName(mData.getUserName());

                if (TextUtils.isEmpty(teacherId)) {
                    mDatas.add(agoraBean);
                } else {
                    if (teacherId.equals(agoraBean.getuId() + "")) {
                        mDatas.add(0, agoraBean);
                    } else {
                        mDatas.add(agoraBean);
                    }
                }
            }
        } else {
            Log.e("bigsetData", mDatas.size() + "        no");
            for (AgoraBean mData : mDatas) {
                SurfaceView surfaceView = mData.getSurfaceView();   // SurfaceView单独在一个Window之上，不和父控件在一个View树中
                stripSurfaceView(surfaceView);
            }
            mDatas.clear();
        }
        notifyDataSetChanged();
    }

    protected final void stripSurfaceView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }
    }
}
