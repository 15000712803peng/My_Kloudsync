package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddFileFromDocumentDialog implements View.OnClickListener, HeaderRecyclerAdapter.OnItemClickListener {
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private ViewFlipper viewFlipper;
    private View selectTeamView;
    private View selectDocumentView;
    private ImageView closeImage;
    private RecyclerView teamList;
    private SingleTeamAdapter adapter;
    private View selectSpaceView;
    private LinearLayout selectTeamLayout;
    private LinearLayout selectSpaceLayout;
    private LinearLayout selectDocumentLayout;
    private TextView teamNameText;
    private RecyclerView spaceList;
    private SpaceAdapter spaceAdapter;
    private ImageView spaceCloseImage;
    private TextView docTeamName;
    private TextView docSpaceName;
    private LinearLayout docSelectTeamLayout;
    private LinearLayout docSelectSpaceLayout;
    private ImageView docCloseImage;
    private TextView docSaveText;
    private TextView docCancelText;

    public interface OnDocSelectedListener {
        void onDocSelected(String docId);
    }

    OnDocSelectedListener onDocSelectedListener;


    public void setOnSpaceSelectedListener(OnDocSelectedListener onDocSelectedListener) {
        this.onDocSelectedListener = onDocSelectedListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;

    }

    public AddFileFromDocumentDialog(Context context) {
        mContext = context;
        initDialog();
        getTeamList();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_add_file_from_document, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        selectTeamView = layoutInflater.inflate(R.layout.file_select_team, null);
        selectSpaceView = layoutInflater.inflate(R.layout.file_select_space, null);
        selectDocumentView = layoutInflater.inflate(R.layout.file_select_document, null);
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
        docTeamName = selectDocumentView.findViewById(R.id.txt_doc_team_name);
        docSpaceName = selectDocumentView.findViewById(R.id.txt_doc_space_name);
        spaceCloseImage = selectSpaceView.findViewById(R.id.img_close);
        spaceCloseImage = selectSpaceView.findViewById(R.id.img_close);
        docCloseImage = selectDocumentView.findViewById(R.id.doc_img_close);
        docCloseImage.setOnClickListener(this);
        spaceCloseImage.setOnClickListener(this);
        teamNameText = selectSpaceView.findViewById(R.id.txt_team_name);
        closeImage = selectTeamView.findViewById(R.id.img_close);
        selectTeamLayout = selectSpaceView.findViewById(R.id.layout_select_team);
        selectSpaceLayout = selectSpaceView.findViewById(R.id.layout_select_team);
        docSelectSpaceLayout = selectDocumentView.findViewById(R.id.layout_doc_select_space);
        docSelectTeamLayout = selectDocumentView.findViewById(R.id.layout_doc_select_team);
        docSelectSpaceLayout.setOnClickListener(this);
        docSelectTeamLayout.setOnClickListener(this);
        documentList = selectDocumentView.findViewById(R.id.list_document);
        docSaveText = selectDocumentView.findViewById(R.id.save);
        docSaveText.setOnClickListener(this);
        docCancelText = selectDocumentView.findViewById(R.id.cancel);
        docCancelText.setOnClickListener(this);
        documentList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        selectTeamLayout.setOnClickListener(this);
        closeImage.setOnClickListener(this);
        viewFlipper.addView(selectTeamView);
        viewFlipper.addView(selectSpaceView);
        viewFlipper.addView(selectDocumentView);
        width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * (0.85f));
        heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (0.88f));
        viewFlipper = view.findViewById(R.id.view_flipper);
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
            case R.id.layout_select_team:
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

            case R.id.layout_doc_select_team:
                if (viewFlipper != null) {
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_out));
                    viewFlipper.setDisplayedChild(0);
                }
                break;
            case R.id.layout_doc_select_space:
                if (viewFlipper != null) {
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_out));
                    viewFlipper.showPrevious();
                }
                break;

            case R.id.doc_img_close:
                dismiss();
                break;

            case R.id.cancel:
                dismiss();
                break;
            case R.id.save:
                if (onDocSelectedListener != null) {
                    if (documentAdapter != null) {
                        Document document = documentAdapter.getSelectFile();
                        if (document != null) {
                            onDocSelectedListener.onDocSelected(document.getItemID());
                            dismiss();
                        } else {
                            Toast.makeText(mContext, "no file selected", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

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

    DocumentAdapter documentAdapter;
    RecyclerView documentList;

    private void getDocumentList(String spaceId) {
        TeamSpaceInterfaceTools.getinstance().getSpaceDocumentList(AppConfig.URL_PUBLIC + "SpaceAttachment/List?spaceID=" + spaceId + "&type=1&pageIndex=0&pageSize=20&searchText=",
                TeamSpaceInterfaceTools.GETSPACEDOCUMENTLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<Document> list = (List<Document>) object;
                        documentAdapter = new DocumentAdapter(mContext, list);
                        documentList.setAdapter(documentAdapter);
                    }
                });
    }

    Team currentTeam;
    TeamSpaceBean currentSpace;

    @Override
    public void onItemClick(int position, Object data) {
        if (data instanceof Team) {
            Team team = (Team) data;
            currentTeam = team;
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
            currentSpace = space;
            spaceAdapter.setSelected(space);

            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.flipper_left_in));
            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.flipper_left_out));
            if (currentTeam != null) {
                docTeamName.setText(currentTeam.getName());
            }
            if (currentSpace != null) {
                docSpaceName.setText(currentSpace.getName());
            }
            viewFlipper.showNext();
//            getSpaceList(currentSpace.getItemID()+"");
            getDocumentList(currentSpace.getItemID() + "");
//            dismiss();
//            if (onSpaceSelectedListener != null) {
//                onSpaceSelectedListener.onSpaceSelected(space.getItemID());
//            }
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


    class DocHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView size;
        ImageView imageview;

        public DocHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            size = (TextView) itemView
                    .findViewById(R.id.filesize);
            time = (TextView) itemView
                    .findViewById(R.id.totalTime);
            imageview = (ImageView) itemView
                    .findViewById(R.id.imageview);
        }
    }


    public class DocumentAdapter extends RecyclerView.Adapter<DocHolder> {
        private Context mContext;
        private List<Document> mDatas;
        int selectPosition = -1;


        public DocumentAdapter(Context context, List<Document> mDatas) {
            this.mContext = context;
            this.mDatas = mDatas;

        }


        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public Document getSelectFile() {
            if (selectPosition == -1) {
                return null;
            }
            return mDatas.get(selectPosition);
        }


        @Override
        public DocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DocHolder(LayoutInflater.from(mContext).inflate(R.layout.popup_video_item, parent, false));
        }

        @Override
        public void onBindViewHolder(DocHolder holder, final int position) {
            holder.name.setText(mDatas.get(position).getTitle());
            holder.time.setText("123kb");
            String create = mDatas.get(position).getCreatedDate();
            String createdate = "";
            if (!TextUtils.isEmpty(create)) {
                long dd = Long.parseLong(create);
                createdate = new SimpleDateFormat("yyyy.MM.dd").format(dd);
            }
            holder.size.setText(createdate);
            if (selectPosition == position) {
                holder.imageview.setImageResource(R.drawable.finish_a);
            } else {
                holder.imageview.setImageResource(R.drawable.finish_d);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition = position;
                    notifyDataSetChanged();
                }
            });

        }


    }
}
