package com.kloudsync.techexcel.adapter;

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
import com.ub.kloudsync.activity.TeamUser;
import com.ub.techexcel.tools.Tools;

import java.util.ArrayList;
import java.util.List;

public class SpacePropertyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private List<TeamUser> mlist = new ArrayList<>();
    private Context context;
    private static final int TYPE_MEMBER = 0;
    private static final int TYPE_ADMIN = 1;
    private static final int TYPE_OWNER = 2;
    //--------------
    public static final int VIEW_TYPE_HEADER = 1;
    public static final int VIEW_TYPE_MEMBER = 2;

    public SpacePropertyAdapter(Context context, List<TeamUser> mlist) {
        this.mlist = mlist;
        this.context = context;
    }

    private static OnItemClickListner onItemClickListner;

    public static void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        SpacePropertyAdapter.onItemClickListner = onItemClickListner;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_item:
                if (onItemClickListner != null) {
                    onItemClickListner.onSpaceMemberClick((TeamUser) view.getTag());
                }
                break;
            case R.id.layout_add_header:
                if (onItemClickListner != null) {
                    onItemClickListner.onHeaderClick();
                }
                break;
        }
    }

    public interface OnItemClickListner {
        void onHeaderClick();

        void onSpaceMemberClick(TeamUser member);
    }

    public void UpdateRV(List<TeamUser> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_header, parent, false);
            return new HeaderHolder(view);

        } else if (viewType == VIEW_TYPE_MEMBER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_space_member, parent, false);
            return new ItemHolder(view);
        }
        return new ItemHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER: {
                HeaderHolder holder = (HeaderHolder) viewHolder;
                holder.topText.setText("Space members");
                holder.nameText.setText("Invite new");
                holder.addLayout.setOnClickListener(this);

            }
            break;
            case VIEW_TYPE_MEMBER: {
                final ItemHolder holder = (ItemHolder) viewHolder;
                final TeamUser teamUser = mlist.get(position);
                holder.tv_name.setText(teamUser.getMemberName());

                String url2 = teamUser.getMemberAvatar();
                Uri imageUri2;
                if (!TextUtils.isEmpty(url2)) {
                    imageUri2 = Uri.parse(url2);
                } else {
                    imageUri2 = Tools.getUriFromDrawableRes(context, R.drawable.hello);
                }
                holder.simpledraweeview.setImageURI(imageUri2);
                holder.itemLayout.setTag(teamUser);
                holder.itemLayout.setOnClickListener(this);
                showMemberType(teamUser.getMemberType(), holder.typeText);
            }
            break;
        }


    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        SimpleDraweeView simpledraweeview;
        ImageView moreOpation;
        TextView typeText;
        RelativeLayout itemLayout;

        ItemHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            moreOpation = (ImageView) view.findViewById(R.id.moreOpation);
            simpledraweeview = (SimpleDraweeView) view.findViewById(R.id.simpledraweeview);
            typeText = view.findViewById(R.id.txt_type);
            itemLayout = view.findViewById(R.id.layout_item);
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView topText;
        TextView nameText;
        RelativeLayout addLayout;

        HeaderHolder(View view) {
            super(view);
            topText = (TextView) view.findViewById(R.id.txt_top);
            nameText = (TextView) view.findViewById(R.id.txt_header_name);
            addLayout = view.findViewById(R.id.layout_add_header);
        }
    }

    private void showMemberType(int type, TextView typeText) {
        switch (type) {
            case TYPE_MEMBER:
                typeText.setVisibility(View.GONE);
                break;
            case TYPE_ADMIN:
                typeText.setText("Admin");
                typeText.setTextColor(context.getResources().getColor(R.color.txt_admin_color));
                typeText.setBackground(context.getResources().getDrawable(R.drawable.member_admin_bg));
                break;
            case TYPE_OWNER:
                typeText.setText("Owner");
                typeText.setTextColor(context.getResources().getColor(R.color.txt_owner_color));
                typeText.setBackground(context.getResources().getDrawable(R.drawable.member_owner_bg));
                break;
            default:
                typeText.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_MEMBER;
    }
}
