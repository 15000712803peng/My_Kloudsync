package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSpaceFragment;
import com.kloudsync.techexcel.bean.UserInCompany;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.AddSyncRoomActivity;
import com.kloudsync.techexcel.docment.RenameActivity;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.KloudCache;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.ub.kloudsync.activity.CreateNewSpaceActivityV2;
import com.ub.kloudsync.activity.SpaceSyncRoomActivity;
import com.ub.kloudsync.activity.SwitchTeamActivity;
import com.ub.kloudsync.activity.TeamMorePopup;
import com.ub.kloudsync.activity.TeamPropertyActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.SyncBookActivity;
import com.ub.service.activity.SyncRoomActivity;
import com.ub.techexcel.adapter.SpaceAdapter;
import com.ub.techexcel.adapter.SyncRoomAdapter;
import com.ub.techexcel.bean.SyncRoomBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TopicFragment extends MyFragment implements View.OnClickListener, SpaceAdapter.OnItemLectureListener {

    private RecyclerView syncroomRecyclerView;
    private RelativeLayout teamRl;
    private RelativeLayout createNewSpace;
    private ImageView switchTeam;
    private TextView teamSpacename;
    private RecyclerView spaceRecycleView;
    private List<TeamSpaceBean> spacesList = new ArrayList<>();
    private SpaceAdapter spaceAdapter;
    private TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
    private SharedPreferences sharedPreferences;
    private SyncRoomAdapter syncRoomAdapter;
    private ImageView moreOpation;
    RelativeLayout addSyncRoomLayout;
    View view ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.topicfragment, container, false);
            EventBus.getDefault().register(this);
            initView(view);
        }
        load();
        handleRolePemission(KloudCache.getInstance(getActivity()).getUserInfo());
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(TeamSpaceBean teamSpaceBean) {
        Log.e("event_bus", "topic fragment refresh");
        getTeamhaha();
        getSpaceList();
    }

    private void GoToSwitch() {
        Intent intent2;
        intent2 = new Intent(getActivity(), SwitchTeamActivity.class);
        if (user != null) {
            intent2.putExtra("role", user.getRole());
        }
        startActivity(intent2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(List<TeamSpaceBean> list) {
        Log.e("duang", "biubiu");
        getTeamhaha();
        spacesList.clear();
        spacesList.addAll(list);
        spaceAdapter.notifyDataSetChanged();
        getSyncRoomList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible) {  //isPrepared 可见在onCreate之前执行
            if (!isLoadDataFinish) {
                isLoadDataFinish = true;


            }
        }
    }

    private void load(){
        getSpaceList();
    }


    List<TeamSpaceBean> spaceList;

    private  void getSpaceList() {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamSpaceBean.getItemID(),
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        spaceList = (List<TeamSpaceBean>) object;
                        spacesList.clear();
                        spacesList.addAll(spaceList);
                        spaceAdapter.notifyDataSetChanged();
                        getSyncRoomList();
                    }
                });
    }

    private void getSyncRoomList() {

        TeamSpaceInterfaceTools.getinstance().getSyncRoomList(AppConfig.URL_PUBLIC + "SyncRoom/List?companyID=" + AppConfig.SchoolID + "&teamID=" + teamSpaceBean.getItemID() + "&spaceID=0",
                TeamSpaceInterfaceTools.GETSYNCROOMLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SyncRoomBean> list = (List<SyncRoomBean>) object;
                        if (spaceList != null) {
                            for (SyncRoomBean syncRoom : list) {
                                for (TeamSpaceBean space : spaceList) {
                                    if (syncRoom.getParentID() == space.getItemID()) {
                                        syncRoom.setPath(teamName + "/" + space.getName());
                                        break;
                                    }
                                }
                            }
                        }
                        syncRoomAdapter = new SyncRoomAdapter(getActivity(), list);
                        syncroomRecyclerView.setAdapter(syncRoomAdapter);
                        syncRoomAdapter.setOnItemLectureListener(new SyncRoomAdapter.OnItemLectureListener() {

                            @Override
                            public void view(SyncRoomBean syncRoomBean) {
                                enterSyncroom(syncRoomBean);
                            }

                            @Override
                            public void deleteSuccess() {
                                getSpaceList();
                                getSyncRoomList();
                            }

                            @Override
                            public void switchSuccess() {
                                getSpaceList();
                                getSyncRoomList();
                            }


                            @Override
                            public void item(SyncRoomBean syncRoomBean) {
//                                Intent intent=new Intent(getActivity(), LinshiActivity.class);
//                                intent.putExtra("syncRoomBean", syncRoomBean);
//                                intent.putExtra("teamId",teamSpaceBean.getItemID());
//                                startActivity(intent);
                                enterSyncroom(syncRoomBean);
                            }

                            @Override
                            public void dismiss() {
                            }

                            @Override
                            public void open() {
                            }
                        });
                    }
                });
    }

    private void enterSyncroom(SyncRoomBean syncRoomBean) {
        if(syncRoomBean.getTopicType() == 7){
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
            intent.putExtra("spaceId", teamSpaceBean.getItemID());
            intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
            intent.putExtra("isStartCourse", true);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getActivity(), SyncRoomActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("userid", AppConfig.UserID);
            intent.putExtra("meetingId", syncRoomBean.getItemID() + "," + AppConfig.UserID);
            intent.putExtra("isTeamspace", true);
            intent.putExtra("yinxiangmode", 0);
            intent.putExtra("identity", 2);
            intent.putExtra("lessionId", syncRoomBean.getItemID() + "");
            intent.putExtra("syncRoomname", syncRoomBean.getName() + "");
            intent.putExtra("spaceId", teamSpaceBean.getItemID());
            intent.putExtra("isInstantMeeting", 0);
            intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
            intent.putExtra("isStartCourse", true);
            startActivity(intent);
        }

    }


    private void initView(View view) {
        syncroomRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
        spaceRecycleView = (RecyclerView) view.findViewById(R.id.spacerecycleview);
        syncroomRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        spaceRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        spaceAdapter = new SpaceAdapter(getActivity(), spacesList, true,false);
        spaceRecycleView.setAdapter(spaceAdapter);
        spaceAdapter.setOnItemLectureListener(this);
        syncroomRecyclerView.setNestedScrollingEnabled(false);
        spaceRecycleView.setNestedScrollingEnabled(false);
        teamRl = (RelativeLayout) view.findViewById(R.id.teamrl);
        createNewSpace = (RelativeLayout) view.findViewById(R.id.createnewspace);
        switchTeam = (ImageView) view.findViewById(R.id.switchteam);
        teamSpacename = (TextView) view.findViewById(R.id.teamspacename);
        moreOpation = (ImageView) view.findViewById(R.id.moreOpation);
        moreOpation.setOnClickListener(this);
        addSyncRoomLayout = (RelativeLayout) view.findViewById(R.id.layout_add);
        teamRl.setOnClickListener(this);
        addSyncRoomLayout.setOnClickListener(this);
        switchTeam.setOnClickListener(this);
        createNewSpace.setOnClickListener(this);
        getTeamhaha();
    }

    private UserInCompany user;

    private void handleRolePemission(UserInCompany user) {
        this.user = user;
        if (sharedPreferences.getInt("teamid", -1) > 0) {
            if (user == null) {
                return;
            }
            if (user.getRole() == 7 || user.getRole() == 8) {
                createNewSpace.setVisibility(View.VISIBLE);
            } else {
                if (user.getRoleInTeam() == null) {
                    return;
                }
                if (user.getRoleInTeam().getTeamRole() == 0) {
                    createNewSpace.setVisibility(View.GONE);
                } else if (user.getRoleInTeam().getTeamRole() > 0) {
                    createNewSpace.setVisibility(View.VISIBLE);
                }
            }
        } else {
            createNewSpace.setVisibility(View.GONE);
        }

    }


    String teamName;
    private void getTeamhaha() {

        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                Context.MODE_PRIVATE);
        teamName = sharedPreferences.getString("teamname", "");
        teamSpaceBean.setName(sharedPreferences.getString("teamname", ""));
        teamSpacename.setText(teamSpaceBean.getName());
        int teamId = sharedPreferences.getInt("teamid", 0);
        teamSpaceBean.setItemID(teamId);
        if (teamId == 0) {
            createNewSpace.setVisibility(View.GONE);
        } else {
            createNewSpace.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.teamrl:
                GoToSwitch();
                break;
            case R.id.switchteam:
                Intent intent2 = new Intent(getActivity(), SwitchTeamActivity.class);
                startActivity(intent2);
                break;
            case R.id.moreOpation:
                MoreForTeam();
                break;
            case R.id.createnewspace:
                Intent intent3 = new Intent(getActivity(), CreateNewSpaceActivityV2.class);
                if (teamSpaceBean.getItemID() > 0) {
                    intent3.putExtra("ItemID", teamSpaceBean.getItemID());
                    startActivity(intent3);
                } else {
                    Toast.makeText(getActivity(), "请先选择Team", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.layout_add:
                Intent addDocIntent = new Intent(getActivity(), AddSyncRoomActivity.class);
                if (teamSpaceBean != null) {
                    addDocIntent.putExtra("team_name", teamSpaceBean.getName());
                    addDocIntent.putExtra("team_id", teamSpaceBean.getItemID());
                }
                startActivity(addDocIntent);
                break;
        }
    }

    private void MoreForTeam() {
        TeamMorePopup teamMorePopup=new TeamMorePopup();
        teamMorePopup.setIsTeam(true);
        teamMorePopup.setTSid(teamSpaceBean.getItemID());
        teamMorePopup.setTName(sharedPreferences.getString("teamname", ""));
        teamMorePopup.getPopwindow(getActivity());
        teamMorePopup.setFavoritePoPListener(new TeamMorePopup.FavoritePoPListener() {
            @Override
            public void dismiss() {

            }

            @Override
            public void open() {

            }

            @Override
            public void delete() {
                LoginGet lg = new LoginGet();
                lg.setBeforeDeleteTeamListener(new LoginGet.BeforeDeleteTeamListener() {
                    @Override
                    public void getBDT(int retdata) {
                        if(retdata > 0){
                            Toast.makeText(getActivity(), "Please delete space first", Toast.LENGTH_LONG).show();
                        } else {
                            DeleteTeam();
                        }
                    }
                });
                lg.GetBeforeDeleteTeam(getActivity(), teamSpaceBean.getItemID() + "");
            }

            @Override
            public void rename() {
                GoToRename();
            }

            @Override
            public void quit() {

            }

            @Override
            public void edit() {
                GoToTeamp();
            }
        });

        teamMorePopup.StartPop(moreOpation);
    }

    private void GoToRename() {
        Intent intent = new Intent(getActivity(), RenameActivity.class);
        intent.putExtra("itemID",teamSpaceBean.getItemID());
        intent.putExtra("isteam", true);
        startActivity(intent);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getActivity(),
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    break;
                default:
                    break;
            }
        }
    };


    private void DeleteTeam() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "TeamSpace/DeleteTeam?teamID=" +
                                    teamSpaceBean.getItemID());
                    Log.e("DeleteTeam", responsedata.toString());
                    int retcode = (Integer) responsedata.get("RetCode");
                    msg = new Message();
                    if (0 == retcode) {
                        msg.what = AppConfig.DELETESUCCESS;
                        String result = responsedata.toString();
                        msg.obj = result;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("errorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(getActivity())) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start(ThreadManager.getManager());
    }

    private void GoToTeamp() {
        Intent intent = new Intent(getActivity(), TeamPropertyActivity.class);
        if (teamSpaceBean.getItemID() != 0) {
            intent.putExtra("ItemID", teamSpaceBean.getItemID());
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "请先选择Team", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItem(TeamSpaceBean teamSpaceBean2) {
//        Intent intent = new Intent(getActivity(), SpaceSyncRoomActivity.class);
//        intent.putExtra("teamid", teamSpaceBean.getItemID());
//        intent.putExtra("spaceid", teamSpaceBean2.getItemID());
//        intent.putExtra("spaceName", teamSpaceBean2.getName());
//        startActivity(intent);
        EventSpaceFragment eventSpaceFragment = new EventSpaceFragment();
        eventSpaceFragment.setItemID(teamSpaceBean2.getItemID());
        eventSpaceFragment.setSpaceId(teamSpaceBean2.getItemID());
        eventSpaceFragment.setSpaceName(teamSpaceBean2.getName());
        eventSpaceFragment.setType(2);
        eventSpaceFragment.setTeamName(sharedPreferences.getString("teamname",""));
        eventSpaceFragment.setTeamId(sharedPreferences.getInt("teamid",0));
        EventBus.getDefault().post(eventSpaceFragment);
    }

    @Override
    public void select(TeamSpaceBean teamSpaceBean) {


    }


}
