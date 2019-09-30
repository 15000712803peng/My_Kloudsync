package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddDocToSpaceDialog implements View.OnClickListener, HeaderRecyclerAdapter.OnItemClickListener {
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private ViewFlipper viewFlipper;
    private View selectTeamView;
    private ImageView closeImage;
    private RecyclerView teamList;
    private SingleTeamAdapter adapter;
    private View selectSpaceView;
    private ImageView backImage;
    private TextView teamNameText;
    private RecyclerView spaceList;
    private SpaceAdapter spaceAdapter;
    private ImageView spaceCloseImage;

    public interface OnSpaceSelectedListener {
        void onSpaceSelected(int spaceId);
    }

    OnSpaceSelectedListener onSpaceSelectedListener;


    public void setOnSpaceSelectedListener(OnSpaceSelectedListener onSpaceSelectedListener) {
        this.onSpaceSelectedListener = onSpaceSelectedListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;

    }

    public AddDocToSpaceDialog(Context context) {
        mContext = context;
        initDialog();
        getTeamList();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_add_doc_space, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        selectTeamView = layoutInflater.inflate(R.layout.view_select_team, null);
        selectSpaceView = layoutInflater.inflate(R.layout.view_select_space, null);
        viewFlipper = view.findViewById(R.id.view_flipper);
        teamList = selectTeamView.findViewById(R.id.list_team);
        teamList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        spaceList = selectSpaceView.findViewById(R.id.list_space);
        spaceList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        adapter = new SingleTeamAdapter();
        spaceAdapter = new SpaceAdapter();
        adapter.setOnItemClickListener(this);
        spaceAdapter.setOnItemClickListener(this);
        teamList.setAdapter(adapter);
        spaceList.setAdapter(spaceAdapter);
        spaceCloseImage = selectSpaceView.findViewById(R.id.img_space_close);
        spaceCloseImage.setOnClickListener(this);
        teamNameText = selectSpaceView.findViewById(R.id.txt_team_name);
        closeImage = selectTeamView.findViewById(R.id.img_close);
        backImage = selectSpaceView.findViewById(R.id.image_back);
        backImage.setOnClickListener(this);
        closeImage.setOnClickListener(this);
        viewFlipper.addView(selectTeamView);
        viewFlipper.addView(selectSpaceView);
        width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * (0.8f));
        heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (0.7f));
        viewFlipper = view.findViewById(R.id.view_flipper);
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = width;
        lp.height = heigth;
        dialog.getWindow().setAttributes(lp);

    }


    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                dismiss();
                break;
            case R.id.image_back:
                if (viewFlipper != null) {
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_out));
                    viewFlipper.showPrevious();
                }
                break;
            case R.id.img_space_close:
                dismiss();
                break;
        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void getTeamList() {
        ServiceInterfaceTools.getinstance().getCompanyTeams(AppConfig.SchoolID + "").enqueue(new Callback<TeamsResponse>() {
            @Override
            public void onResponse(Call<TeamsResponse> call, Response<TeamsResponse> response) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO,
                        Context.MODE_PRIVATE);
                int itemid = sharedPreferences.getInt("teamid", 0);
                if (response != null && response.isSuccessful()) {
                    List<Team> list = response.body().getRetData();
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    adapter.setDatas(list);
                }

            }

            @Override
            public void onFailure(Call<TeamsResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onItemClick(int position, Object data) {
        if (data instanceof Team) {
            Team team = (Team) data;
            if (viewFlipper != null) {
                teamNameText.setText(team.getName());
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                        R.anim.flipper_left_in));
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                        R.anim.flipper_left_out));
                viewFlipper.showNext();
                spaceAdapter.clear();
                getSpaceList(team.getItemID() + "");
            }
        } else if (data instanceof TeamSpaceBean) {
            TeamSpaceBean space = (TeamSpaceBean) data;
            spaceAdapter.setSelected(space);
            dismiss();
            if (onSpaceSelectedListener != null) {
                onSpaceSelectedListener.onSpaceSelected(space.getItemID());
            }
        }

    }

    public class SingleTeamAdapter extends HeaderRecyclerAdapter<Team> {

        @Override
        public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_team_item, parent, false));
        }

        @Override
        public void onBind(RecyclerView.ViewHolder viewHolder, final int realPosition, Team data) {
            final Team item = mDatas.get(realPosition);
            ItemHolder holder = (ItemHolder) viewHolder;
            holder.teamNameText.setText(item.getName());

        }

        class ItemHolder extends RecyclerView.ViewHolder {
            TextView teamNameText;
            LinearLayout itemLayout;

            public ItemHolder(View itemView) {
                super(itemView);
                teamNameText = itemView.findViewById(R.id.txt_team_name);
                itemLayout = itemView.findViewById(R.id.lin_favour);

            }
        }
    }

    public class SpaceAdapter extends HeaderRecyclerAdapter<TeamSpaceBean> {

        @Override
        public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_space_item, parent, false));
        }

        @Override
        public void onBind(RecyclerView.ViewHolder viewHolder, final int realPosition, TeamSpaceBean data) {
            final TeamSpaceBean item = mDatas.get(realPosition);
            ItemHolder holder = (ItemHolder) viewHolder;
            holder.spaceNameText.setText(item.getName());
            if (item.isSelect()) {
                holder.selectedImage.setVisibility(View.VISIBLE);
            } else {
                holder.selectedImage.setVisibility(View.INVISIBLE);
            }

        }

        class ItemHolder extends RecyclerView.ViewHolder {
            TextView spaceNameText;
            ImageView selectedImage;

            public ItemHolder(View itemView) {
                super(itemView);
                spaceNameText = itemView.findViewById(R.id.txt_space_name);
                selectedImage = itemView.findViewById(R.id.image_selected);
            }
        }

        public void clear() {
            mDatas.clear();
            notifyDataSetChanged();
        }

        public void clearSelected() {
            for (TeamSpaceBean space : mDatas) {
                space.setSelect(false);
            }
        }

        public void setSelected(TeamSpaceBean space) {
            clearSelected();
            space.setSelect(true);
            notifyDataSetChanged();
        }


    }


    private void getSpaceList(String teamId) {
        String url = AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamId;
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(url,
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        spaceAdapter.setDatas(list);
                    }
                });
    }
}
