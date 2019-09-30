package io.agora.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.AgoraUser;

import java.util.ArrayList;
import java.util.List;

public class AgoraUsersAdapter extends RecyclerView.Adapter<AgoraUsersAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<AgoraUser> users = new ArrayList<>();
    private Context mContext;


    public AgoraUsersAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_view_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        AgoraUser user = getItem(position);
        final AgoraUser user = users.get(position);
        FrameLayout holderView = (FrameLayout) holder.itemView;
        holderView.removeAllViews();
        ;
        holderView.addView(user.getSurfaceView(), 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//
//        holderView.removeAllViews();
//        if (holderView.getChildCount() == 0) {
//            View d = inflater.inflate(R.layout.framelayout_head, null);
//            TextView videoname = (TextView) d.findViewById(R.id.videoname);
//            ViewParent parent = videoname.getParent();
//            if (parent != null) {
//                ((RelativeLayout) parent).removeView(videoname);
//            }
//            holderView.addView(videoname);
//            SurfaceView target = user.getSurfaceView();
//            stripSurfaceView(target);
//            holderView.addView(target, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        }else {
//
//        }
//        holderView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public AgoraUser getItem(int position) {
        return users.get(position);
    }


    public void setUsers(List<AgoraUser> users) {
        this.users.clear();
        this.users.addAll(users);
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

    public List<AgoraUser> getUsers() {
        return users;
    }

    public void addUser(AgoraUser user) {
        this.users.add(user);
        notifyDataSetChanged();
    }
}
