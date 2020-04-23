package com.kloudsync.techexcel.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.Attendee;
import com.ub.techexcel.bean.ServiceBean;
import java.util.List;
public class AttendeeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ServiceBean serviceBean;
    private Context mContext;
    private List<Attendee> attendees;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new ViewHolder(view);
    }

    public AttendeeAdapter(Context context,List<Attendee> attendees){
        this.attendees=attendees;
        this.mContext=context;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tv_item_attendee.setText(attendees.get(position).getMemberName());
        Glide.with(mContext).load(attendees.get(position).getAvatarUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.iv_item_attendee);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return attendees.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_attendee;
        ImageView iv_item_attendee;
        ViewHolder(View view) {
            super(view);
            tv_item_attendee = (TextView) view.findViewById(R.id.tv_item_attendee);
            iv_item_attendee = (ImageView) view.findViewById(R.id.iv_item_attendee);
        }
    }
}
