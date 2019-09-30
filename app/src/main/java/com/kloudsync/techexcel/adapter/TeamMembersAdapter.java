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
import com.ub.kloudsync.activity.InviteNewMorePopup;
import com.ub.kloudsync.activity.TeamUser;
import com.ub.techexcel.tools.Tools;

import java.util.ArrayList;
import java.util.List;

public class TeamMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TeamUser> mlist = new ArrayList<>();
    private Context context;
    private static final int TYPE_MEMBER = 0;
    private static final int TYPE_ADMIN = 1;
    private static final int TYPE_OWNER = 2;
    //--------------
    public static final int VIEW_TYPE_HEADER = 1;
    public static final int VIEW_TYPE_MEMBER = 2;

    public TeamMembersAdapter(Context context, List<TeamUser> mlist) {
        this.mlist = mlist;
        this.context = context;
    }

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void sendMeaage(TeamUser user);

        void setAdmin(TeamUser user);

        void removeTeam(TeamUser user);

    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void UpdateRV(List<TeamUser> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_MEMBER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_header, parent, false);

        } else if (viewType == VIEW_TYPE_MEMBER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_member, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER:
                break;
            case VIEW_TYPE_MEMBER: {
                final ViewHolder holder = (ViewHolder) viewHolder;
                final TeamUser teamUser = mlist.get(position);
                holder.tv_name.setText(teamUser.getMemberName());

                String url2 = teamUser.getMemberAvatar();
                Uri imageUri2;
                if (!TextUtils.isEmpty(url2)) {
                    imageUri2 = Uri.parse(url2);
                } else {
                    imageUri2 = Tools.getUriFromDrawableRes(context, R.drawable.hello);
                }
                holder.memberImage.setImageURI(imageUri2);
                holder.moreOpation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InviteNewMorePopup teamMorePopup = new InviteNewMorePopup();
                        teamMorePopup.getPopwindow(context);
                        teamMorePopup.setFavoritePoPListener(new InviteNewMorePopup.FavoritePoPListener() {

                            @Override
                            public void sendMeaage() {
                                mFavoritePoPListener.sendMeaage(teamUser);
                            }

                            @Override
                            public void setAdmin() {
                                mFavoritePoPListener.setAdmin(teamUser);
                            }

                            @Override
                            public void removeTeam() {
                                mFavoritePoPListener.removeTeam(teamUser);

                            }
                        });
                        teamMorePopup.StartPop(holder.moreOpation);
                    }
                });
                showMemberType(teamUser.getMemberType(), holder.typeText);
            }
            break;
            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mlist.size();
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


}
