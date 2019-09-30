package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.TeamMember;
import com.ub.techexcel.tools.Tools;

import java.util.Collections;
import java.util.List;

public class SpaceMembersAdapter extends HeaderRecyclerAdapter<TeamMember> {

    private Context context;
    private static final int TYPE_MEMBER = 0;
    private static final int TYPE_ADMIN = 1;
    private static final int TYPE_OWNER = 2;
    //--------------

    public SpaceMembersAdapter(Context context) {
        this.context = context;
    }

    public interface MoreOptionsClickListener {
        void moreOptionsClick(TeamMember member);
    }

    private MoreOptionsClickListener moreOptionsClickListener;


    public void setMoreOptionsClickListener(MoreOptionsClickListener moreOptionsClickListener) {
        this.moreOptionsClickListener = moreOptionsClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.team_member, parent, false));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int position, final TeamMember member) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.tv_name.setText(member.getMemberName());
        String url2 = member.getMemberAvatar();
        Uri imageUri2;
        if (!TextUtils.isEmpty(url2)) {
            imageUri2 = Uri.parse(url2);
        } else {
            imageUri2 = Tools.getUriFromDrawableRes(context, R.drawable.hello);
        }
        holder.memberImage.setImageURI(imageUri2);
        showMemberType(member.getMemberType(), holder.typeText);
        holder.moreOpation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreOptionsClickListener != null) {
                    moreOptionsClickListener.moreOptionsClick(member);
                }
            }
        });
    }

    @Override
    public void setDatas(List<TeamMember> datas) {
        Collections.sort(datas);
        super.setDatas(datas);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        SimpleDraweeView memberImage;
        ImageView moreOpation;
        TextView typeText;

        ViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.txt_name);
            moreOpation = (ImageView) view.findViewById(R.id.moreOpation);
            memberImage = (SimpleDraweeView) view.findViewById(R.id.image_member);
            typeText = view.findViewById(R.id.txt_type);
        }
    }

    private void showMemberType(int type, TextView typeText) {
        switch (type) {
            case TYPE_MEMBER:
                typeText.setVisibility(View.GONE);
                break;
            case TYPE_ADMIN:
                typeText.setVisibility(View.VISIBLE);
                typeText.setText("Admin");
                typeText.setTextColor(context.getResources().getColor(R.color.txt_admin_color));
                typeText.setBackground(context.getResources().getDrawable(R.drawable.member_admin_bg));
                break;
            case TYPE_OWNER:
                typeText.setVisibility(View.VISIBLE);
                typeText.setText("Owner");
                typeText.setTextColor(context.getResources().getColor(R.color.txt_owner_color));
                typeText.setBackground(context.getResources().getDrawable(R.drawable.member_owner_bg));
                break;
            default:
                typeText.setVisibility(View.GONE);
                break;
        }
    }

}
