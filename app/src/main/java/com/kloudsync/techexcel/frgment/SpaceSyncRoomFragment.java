package com.kloudsync.techexcel.frgment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.params.EventTeamFragment;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DialogRename;
import com.kloudsync.techexcel.tool.CustomSyncRoomTool;
import com.ub.kloudsync.activity.CreateNewSyncRoomActivity;
import com.ub.kloudsync.activity.SpacePropertyActivity;
import com.ub.kloudsync.activity.SwitchSpaceActivity;
import com.ub.kloudsync.activity.TeamMorePopup;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.SyncBookActivity;
import com.ub.service.activity.SyncRoomActivity;
import com.ub.techexcel.adapter.SyncRoomAdapter;
import com.ub.techexcel.bean.SyncRoomBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SpaceSyncRoomFragment extends Fragment implements View.OnClickListener {

    private RecyclerView syncroomRecyclerView;
    private int teamId, spaceId;
    private String spaceName;
    private TextView teamspacename;

    private ImageView img_back;
    private RelativeLayout teamRl;
    private SyncRoomAdapter syncRoomAdapter;
    private RelativeLayout createnewsyncroom;
    private ImageView addImage;

    private RelativeLayout backLayout;

    private TextView dirText;
    private TextView projectText;
    private TextView tv_title;
    private TextView currentspacetv;
    private String projectName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);


    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.spacesyncroomteam, container, false);
            initView(view);
        }
        load();
        return view;
    }

    private void load() {
        spaceId = getArguments().getInt("ItemID", 0);
        spaceName = getArguments().getString("space_name");
        teamId = getArguments().getInt("team_id", 0);
        teamspacename.setText(spaceName);
        projectName = getArguments().getString("project_name", "");
        projectText.setText(projectName);
        if (!TextUtils.isEmpty(spaceName)) {
            dirText.setText(spaceName.substring(0, 1).toUpperCase());
        }
        getSyncRoomList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(SyncRoomBean syncRoomBean) {
        getSyncRoomList();
        isRefresh = true;
    }


    private void initView(View view) {
        syncroomRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
        syncroomRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        teamspacename = (TextView) view.findViewById(R.id.teamspacename);
        projectText = view.findViewById(R.id.txt_project_name);
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(CustomSyncRoomTool.getInstance(getActivity()).getCustomyinxiang());
        addImage = view.findViewById(R.id.image_add);
        currentspacetv = view.findViewById(R.id.currentspacetv);
        if (AppConfig.LANGUAGEID == 1) {
            currentspacetv.setText(CustomSyncRoomTool.getInstance(getActivity()).getCustomyinxiang() + " in current space");
        } else if (AppConfig.LANGUAGEID == 2) {
            currentspacetv.setText("当前空间的" + CustomSyncRoomTool.getInstance(getActivity()).getCustomyinxiang());
        }
        addImage.setOnClickListener(this);
        img_back = (ImageView) view.findViewById(R.id.img_notice);
        teamRl = (RelativeLayout) view.findViewById(R.id.teamrl);
        dirText = view.findViewById(R.id.switch_dir);
        backLayout = view.findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        teamRl.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    private boolean isRefresh;

    private void getSyncRoomList() {
        TeamSpaceInterfaceTools.getinstance().getSyncRoomList(AppConfig.URL_PUBLIC + "SyncRoom/List?companyID=" + AppConfig.SchoolID + "&teamID=" + teamId + "&spaceID=" + spaceId,
                TeamSpaceInterfaceTools.GETSYNCROOMLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SyncRoomBean> list = (List<SyncRoomBean>) object;
                        syncRoomAdapter = new SyncRoomAdapter(getActivity(), list);
                        syncroomRecyclerView.setAdapter(syncRoomAdapter);
                        syncRoomAdapter.setOnItemLectureListener(new SyncRoomAdapter.OnItemLectureListener() {
                            @Override
                            public void view(SyncRoomBean syncRoomBean) {
                                enterSyncroom(syncRoomBean);
                            }

                            @Override
                            public void deleteSuccess() {
                                getSyncRoomList();
                                isRefresh = true;
                            }


                            @Override
                            public void switchSuccess() {  //move
                                getSyncRoomList();
                                isRefresh = true;
                            }

                            @Override
                            public void item(SyncRoomBean syncRoomBean) {
                                if (syncRoomBean.getTopicType() == 7) {
                                    //syncbook
                                    Intent intent = new Intent(getActivity(), SyncBookActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("userid", AppConfig.UserID);
                                    intent.putExtra("meetingId", syncRoomBean.getItemID() + "," + AppConfig.UserID);
                                    intent.putExtra("isTeamspace", true);
                                    intent.putExtra("yinxiangmode", 0);
                                    intent.putExtra("identity", 2);
                                    intent.putExtra("lessionId", syncRoomBean.getItemID() + "");
                                    intent.putExtra("syncRoomname", syncRoomBean.getName() + "");
                                    intent.putExtra("isInstantMeeting", 0);
                                    intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
                                    intent.putExtra("isStartCourse", true);
                                    intent.putExtra("spaceId", spaceId);
                                    intent.putExtra("isStartCourse", true);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getActivity(), SyncRoomActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("userid", AppConfig.UserID);
                                    intent.putExtra("meetingId", syncRoomBean.getItemID() + "," + AppConfig.UserID);
                                    intent.putExtra("isTeamspace", true);
                                    intent.putExtra("yinxiangmode", 0);
                                    intent.putExtra("identity", 2);
                                    intent.putExtra("spaceId", spaceId);
                                    intent.putExtra("lessionId", syncRoomBean.getItemID() + "");
                                    intent.putExtra("syncRoomname", syncRoomBean.getName() + "");
                                    intent.putExtra("isInstantMeeting", 0);
                                    intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
                                    intent.putExtra("isStartCourse", true);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void dismiss() {
                                getActivity().getWindow().getDecorView().setAlpha(1.0f);
                            }

                            @Override
                            public void open() {
                                getActivity().getWindow().getDecorView().setAlpha(0.5f);
                            }
                        });
                    }
                });
    }

    private void enterSyncroom(SyncRoomBean syncRoomBean) {

        Intent intent = new Intent(getActivity(), SyncRoomActivity.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", syncRoomBean.getItemID() + "");
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", syncRoomBean.getItemID() + "");
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        startActivity(intent);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_add:
                Intent intent = new Intent(getActivity(), CreateNewSyncRoomActivity.class);
                intent.putExtra("spaceid", spaceId);
                intent.putExtra("teamid", teamId);
                startActivity(intent);
                break;
            case R.id.teamrl:
                Intent intent2 = new Intent(getActivity(), SwitchSpaceActivity.class);
                intent2.putExtra("ItemID", spaceId);
                intent2.putExtra("team_id", teamId);
                intent2.putExtra("isSyncRoom", true);
                intent2.putExtra("project_name", projectName);
                startActivityForResult(intent2, REQUEST_CODE_CHANGESPACE);
                break;
            case R.id.img_notice:
//                finish();
                break;
            case R.id.switchteam:
                ShowMorePop();
                break;
            case R.id.layout_back:
                EventTeamFragment teamFragment = new EventTeamFragment();
                teamFragment.setType(2);
                EventBus.getDefault().post(teamFragment);
//                finish();
                break;
        }
    }


    public static final int REQUEST_CODE_CHANGESPACE = 1;
    private TeamSpaceBean selectSpace;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHANGESPACE) {
            if (resultCode == RESULT_OK) {
//                selectSpace = (TeamSpaceBean) data.getSerializableExtra("selectSpace");
//                if (spaceId != selectSpace.getItemID()) {
//                    spaceId = selectSpace.getItemID();
//                    teamspacename.setText(selectSpace.getName());
//
//                }

                selectSpace = (TeamSpaceBean) data.getSerializableExtra("selectSpace");
                projectName = data.getStringExtra("teamname");
                if (!TextUtils.isEmpty(projectName)) {
                    projectText.setText(projectName);
                }

                if (data.hasExtra("teamid")) {
                    teamId = data.getIntExtra("teamid", 0);
                }

                spaceId = selectSpace.getItemID();
                spaceName = selectSpace.getName();
                if (!TextUtils.isEmpty(spaceName)) {
                    dirText.setText(spaceName.substring(0, 1).toUpperCase());
                }
                teamspacename.setText(selectSpace.getName());
                getSyncRoomList();
            }

        }
    }


    private void ShowMorePop() {

        TeamMorePopup teamMorePopup = new TeamMorePopup();
        teamMorePopup.setIsTeam(false);
        teamMorePopup.setTSid(spaceId);
        teamMorePopup.getPopwindow(getActivity());
        teamMorePopup.setFavoritePoPListener(new TeamMorePopup.FavoritePoPListener() {
            @Override
            public void dismiss() {
                getActivity().getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getActivity().getWindow().getDecorView().setAlpha(0.5f);
            }

            @Override
            public void delete() {
//                DeleteSpace();
            }

            @Override
            public void rename() {
//                GotoRename();
                DialogRename dr = new DialogRename();
                dr.EditCancel(getActivity(), spaceId, false);
            }

            @Override
            public void quit() {
//                finish();
            }

            @Override
            public void edit() {
                Intent intent2 = new Intent(getActivity(), SpacePropertyActivity.class);
                intent2.putExtra("ItemID", spaceId);
                startActivity(intent2);
            }
        });

//        teamMorePopup.StartPop(switchteam);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (isRefresh) {
            EventBus.getDefault().post(new TeamSpaceBean());
        }
    }
}
