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

import com.kloudsync.techexcel.R;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.AgoraBean;
import com.ub.techexcel.bean.AgoraMember;

import java.util.ArrayList;
import java.util.List;

import io.agora.openlive.ui.VideoViewEventListener;

/**
 * Created by wang on 2018/8/8.
 */

public class FullAgoraCameraAdapter extends RecyclerView.Adapter<FullAgoraCameraAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<AgoraMember> members;
    private Context mContext;
    public VideoViewEventListener mListener;

    public List<AgoraMember> getUsers() {
        return members;
    }


    public FullAgoraCameraAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        members = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_view_container2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder myHolder = holder;
        final AgoraMember member = members.get(position);
        final FrameLayout holderView = (FrameLayout) myHolder.itemView;
        holderView.removeAllViews();
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        Rect frame = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int notifiheight = frame.top;
        height -= notifiheight;
        int framelayoutHeight;
        if (members.size() <= 2) {
            framelayoutHeight = height;
        } else if (members.size() > 2 && members.size() <= 10) {
            framelayoutHeight = height / 2;
        } else {
            int line = ((members.size() % 5) == 0 ? 0 : 1);
            framelayoutHeight = height / (members.size() / 5 + line);
        }
        if (holderView.getChildCount() == 0) {
            SurfaceView target = member.getSurfaceView();
            stripSurfaceView(target);
            if (!member.isMuteVideo()) {
                target.setVisibility(View.VISIBLE);
                stripSurfaceView(target);
                holderView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, framelayoutHeight));
                Log.e("FullAgoraCameraAdapter", "add_surface_view");
                holderView.addView(target, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, framelayoutHeight));
            } else {
                target.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        int sizeLimit = members.size();
        return sizeLimit;
    }

    public AgoraMember getItem(int position) {
        return members.get(position);
    }

    public void setMembers(List<AgoraMember> datas) {
        if (datas != null) {
            members.clear();
            for (AgoraMember mData : datas) {
                Log.e("bigsetData", mData + "        ");
                SurfaceView surfaceView = mData.getSurfaceView();
                members.add(mData);
            }
        }
        notifyDataSetChanged();
    }

    public void muteOrOpenCamera(int myId, boolean isMute) {
        int index = members.indexOf(new AgoraMember(myId));
        if (index >= 0 && index < members.size()) {
            members.get(index).setMuteVideo(isMute);
            Log.e("muteOrOpenCamera","isMute:" + isMute);
            notifyDataSetChanged();
        }
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

    public void reset() {
        for (AgoraMember member : this.members) {
            SurfaceView surfaceView = member.getSurfaceView();
            stripSurfaceView(surfaceView);
        }
        this.members.clear();
        notifyDataSetChanged();
    }


}
