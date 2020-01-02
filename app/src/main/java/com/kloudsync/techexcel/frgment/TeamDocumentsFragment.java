package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.DocumentData;
import com.kloudsync.techexcel.bean.EventSpaceFragment;
import com.kloudsync.techexcel.bean.EventSyncSucc;
import com.kloudsync.techexcel.bean.MessageDocList;
import com.kloudsync.techexcel.bean.MessageSpaceList;
import com.kloudsync.techexcel.bean.UserInCompany;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.AddDocumentActivity;
import com.kloudsync.techexcel.docment.EditTeamActivity;
import com.kloudsync.techexcel.docment.MoveDocumentActivity;
import com.kloudsync.techexcel.docment.RenameActivity;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogDeleteDocument;
import com.kloudsync.techexcel.help.FilterSpaceDialog;
import com.kloudsync.techexcel.help.PopDeleteDocument;
import com.kloudsync.techexcel.help.PopDocument;
import com.kloudsync.techexcel.help.PopEditDocument;
import com.kloudsync.techexcel.help.PopShareKloudSync;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.help.UserInfoHelper;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.school.SwitchOrganizationActivity;
import com.kloudsync.techexcel.search.ui.TeamAndDocSearchActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.KloudCache;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.kloudsync.activity.CreateNewSpaceActivityV2;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.SpaceDeletePopup;
import com.ub.kloudsync.activity.SpaceDocumentsActivity;
import com.ub.kloudsync.activity.SwitchTeamActivity;
import com.ub.kloudsync.activity.TeamMorePopup;
import com.ub.kloudsync.activity.TeamPropertyActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.SocketService;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.adapter.HomeDocumentAdapter;
import com.ub.techexcel.adapter.SpaceAdapter;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamDocumentsFragment extends MyFragment implements View.OnClickListener, SpaceAdapter.OnItemLectureListener, KloudCache.OnUserInfoChangedListener, FilterSpaceDialog.SpaceOptionsLinstener {

    private RecyclerView mCurrentTeamRecyclerView;
    private RelativeLayout teamRl;
    private RelativeLayout createNewSpace;
    private ImageView switchTeam;
    private ImageView addService;
    private ImageView moreOpation;
    private TextView teamSpacename;
    private RecyclerView spaceRecycleView;
    private List<TeamSpaceBean> spacesList = new ArrayList<>();
    private SpaceAdapter spaceAdapter;
    private TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
    private HomeDocumentAdapter documentAdapter;
    private SharedPreferences sharedPreferences;
    private RelativeLayout searchLayout;
    private static final int REQUEST_UPDATE_TEAM = 1;
    private ImageView filterSpaceImage;
    private View view;
    private ImageView switchCompanyImage;
    private TextView searchPromptText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.documentfragment, container, false);
            EventBus.getDefault().register(this);
            initView(view);
        }
        load();
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(TeamSpaceBean teamSpaceBean) {
        Log.e("event_bus", "document fragment refresh");
        getTeam();
        getUserDetail();
        getSpaceList();
        getAllDocumentList();
    }

    @Subscribe
    public void refreshSync(EventSyncSucc eventSyncSucc) {
        getAllDocumentList();
    }

    private void getUserDetail() {
        UserInfoHelper.getUserInfoInCompany(getActivity(), AppConfig.SchoolID + "", AppConfig.UserID,
                sharedPreferences.getInt("teamid", 0) + "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventSpaceList(MessageSpaceList spaceList) {
        KloudCache.getInstance(getActivity()).asyncCacheSpaceList(spaceList.getSpaceList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventDocList(MessageDocList docList) {
        KloudCache.getInstance(getActivity()).asyncCacheDocList(docList.getDocList());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        KloudCache.getInstance(getActivity()).clear();
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
        getUserDetail();
        getSpaceList();
        getAllDocumentList();
        Intent intent = new Intent(getActivity(), SocketService.class);
        getActivity().startService(intent);
    }


    private void getSpaceList() {
        Log.e("event_bus", "get space list");
        String url = AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamSpaceBean.getItemID();
        int showAll = sharedPreferences.getInt("show_all_spaces", 0);
        if (showAll == 1) {
            url += "&showAll=1";
        }

        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(url,
                TeamSpaceInterfaceTools.GET_SPACE_LIST_IN_HOME_PAGE, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        spacesList.clear();
                        spacesList.addAll(list);
                        spaceAdapter.notifyDataSetChanged();
                        Log.e("event_bus", "space adapter notify data set changed");
                        EventBus.getDefault().post(new MessageSpaceList(list));
                    }
                });
    }

    private void getAllDocumentList() {
        ServiceInterfaceTools.getinstance().getAllDocumentList(teamSpaceBean.getItemID() + "").enqueue(new Callback<NetworkResponse<DocumentData>>() {
            @Override
            public void onResponse(Call<NetworkResponse<DocumentData>> call, Response<NetworkResponse<DocumentData>> response) {
                if (response != null && response.isSuccessful() && response.body() != null && response.body().getRetData() != null) {
                    List<Document> documents = response.body().getRetData().getDocumentList();
                    if (documents == null) {
                        documents = new ArrayList<>();
                    }
                    EventBus.getDefault().post(new MessageDocList(documents));
                    if (mCurrentTeamRecyclerView.getAdapter() == null) {
                        documentAdapter = new HomeDocumentAdapter(getActivity(), documents);
                        mCurrentTeamRecyclerView.setAdapter(documentAdapter);
                        documentAdapter.setOnItemLectureListener(new HomeDocumentAdapter.OnItemLectureListener() {
                            @Override
                            public void onItem(final Document document, View view) {
                                PopDocument pd = new PopDocument();
                                pd.getPopwindow(getActivity(), document);
                                pd.setPoPMoreListener(new PopDocument.PopDocumentListener() {
                                    boolean flags;

                                    @Override
                                    public void PopView() {
//                                        getTempLesson(lesson);
//                                        GoToVIew(lesson);
                                    }

                                    @Override
                                    public void PopDelete() {
                                        delDocumentDialog(document);
                                    }

                                    @Override
                                    public void PopEdit() {
                                        flags = true;
                                        editDocumentDialog(document);
                                    }

                                    @Override
                                    public void PopShare() {
                                        flags = true;
                                        shareDocumentDialog(document, -1);
                                    }

                                    @Override
                                    public void PopMove() {
//                                        MoveDocument(lesson);
                                        moveDocument(document);
                                    }

                                    @Override
                                    public void PopBack() {

                                    }

                                });
                                pd.StartPop(view);
                            }

                            @Override
                            public void onRealItem(Document document, View view) {
//                                GoToVIew(lesson);
                                if (!Tools.isFastClick()) {
                                    requestDocumentDetail(document);
                                }
                            }

                            @Override
                            public void share(int position, Document document) {
                                shareDocumentDialog(document, position);
                            }

                            @Override
                            public void dismiss() {
                            }

                            @Override
                            public void open() {
                            }

                            @Override
                            public void deleteRefresh() {
                                getAllDocumentList();
                            }
                        });

                    } else {
                        documentAdapter.setDocuments(documents);
                        Log.e("HomeDocumentAdapter", "set documents");
                    }

                }
            }

            @Override
            public void onFailure(Call<NetworkResponse<DocumentData>> call, Throwable t) {

            }
        });

    }

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();

    private void MoveDocument(final Document lesson) {
        LoginGet loginget = new LoginGet();
        loginget.setTeamSpaceGetListener(new LoginGet.TeamSpaceGetListener() {
            @Override
            public void getTS(ArrayList<Customer> list) {
                cuslist = new ArrayList<Customer>();
                cuslist.addAll(list);
                for (int i = 0; i < cuslist.size(); i++) {
                    Customer customer = cuslist.get(i);
                    ArrayList<Space> sl = customer.getSpaceList();
                    if (0 == sl.size()) {
                        cuslist.remove(i--);
                    }
                }
                SpaceDeletePopup spaceDeletePopup = new SpaceDeletePopup();
                spaceDeletePopup.getPopwindow(getActivity());
                spaceDeletePopup.setSP(cuslist);
                spaceDeletePopup.ChangeMove(lesson);
                spaceDeletePopup.SendTeam(teamSpaceBean.getItemID(), teamSpaceBean.getName());
                spaceDeletePopup.setFavoritePoPListener(new SpaceDeletePopup.FavoritePoPListener() {
                    @Override
                    public void dismiss() {
                    }

                    @Override
                    public void open() {

                    }

                    @Override
                    public void delete(int spaceid) {

                    }

                    @Override
                    public void refresh() {
                        getSpaceList();
                        getAllDocumentList();
                    }
                });
                spaceDeletePopup.StartPop(spaceRecycleView);

            }
        });
        loginget.GetTeamSpace(getActivity());

    }


    private void shareDocumentDialog(final Document document, final int id) {
        final PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(getActivity(), document, id);
        psk.setPoPDismissListener(new PopShareKloudSync.PopShareKloudSyncDismissListener() {
            @Override
            public void CopyLink() {

            }

            @Override
            public void Wechat() {

            }

            @Override
            public void Moment() {

            }

            @Override
            public void Scan() {

            }

            @Override
            public void PopBack() {

            }
        });
        psk.startPop();

    }

    private void editDocumentDialog(Document document) {
        PopEditDocument ped = new PopEditDocument();
        ped.setPopEditDocumentListener(new PopEditDocument.PopEditDocumentListener() {
            @Override
            public void popEditSuccess() {
                getAllDocumentList();
            }
        });
        ped.getPopwindow(getActivity(), document);
        ped.StartPop();
    }

    private void delDocumentDialog(final Document document) {
        DialogDeleteDocument ddd = new DialogDeleteDocument();
        ddd.setDelDocListener(new DialogDeleteDocument.DialogDelDocListener() {
            @Override
            public void delDoc() {
                deleteDocumentRequest(document);
            }
        });
        ddd.EditCancel(getActivity());
    }

    private void deleteDocumentRequest(final Document document) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "SpaceAttachment/RemoveDocument?itemIDs=" +
                                    document.getItemID());
                    Log.e("RemoveDocument", responsedata.toString());
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
                    // TODO Auto-generated catch block
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

    private void initView(View view) {

        mCurrentTeamRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
        spaceRecycleView = (RecyclerView) view.findViewById(R.id.spacerecycleview);
        searchLayout = view.findViewById(R.id.search_layout);
        mCurrentTeamRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        spaceRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        switchCompanyImage = view.findViewById(R.id.image_switch_company);
        switchCompanyImage.setOnClickListener(this);
        spaceAdapter = new SpaceAdapter(getActivity(), spacesList, false, false);
        spaceRecycleView.setAdapter(spaceAdapter);
        spaceAdapter.setOnItemLectureListener(this);
        searchPromptText = view.findViewById(R.id.txt_promp_search);
        searchPromptText.setText(getString(R.string.Search_Contact) + "  "+sharedPreferences.getString("SchoolName", ""));
        mCurrentTeamRecyclerView.setNestedScrollingEnabled(false);
        spaceRecycleView.setNestedScrollingEnabled(false);

        teamRl = (RelativeLayout) view.findViewById(R.id.teamrl);
        createNewSpace = (RelativeLayout) view.findViewById(R.id.createnewspace);
        switchTeam = (ImageView) view.findViewById(R.id.switchteam);
        addService = (ImageView) view.findViewById(R.id.addService);
        teamSpacename = (TextView) view.findViewById(R.id.teamspacename);
        moreOpation = (ImageView) view.findViewById(R.id.moreOpation);
        teamRl.setOnClickListener(this);
        switchTeam.setOnClickListener(this);
        addService.setOnClickListener(this);
        createNewSpace.setOnClickListener(this);
        moreOpation.setOnClickListener(this);
        teamSpacename.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        filterSpaceImage = view.findViewById(R.id.image_filter_space);
        filterSpaceImage.setOnClickListener(this);
        getTeam();
    }

    private void getTeam() {

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
//                GoToTeamp();
                GoToSwitch();
                break;
            case R.id.switchteam:
                GoToSwitch();
                break;
            case R.id.teamspacename:
                GoToSwitch();
                break;
            case R.id.addService:
//                AddBiuBiu();
                Intent addDocIntent = new Intent(getActivity(), AddDocumentActivity.class);
                if (teamSpaceBean != null) {
                    addDocIntent.putExtra("team_name", teamSpaceBean.getName());
                    addDocIntent.putExtra("team_id", teamSpaceBean.getItemID());
                }
                startActivity(addDocIntent);
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
            case R.id.moreOpation:
                MoreForTeam();
                break;
            case R.id.search_layout:
                Intent searchIntent = new Intent(getActivity(), TeamAndDocSearchActivity.class);
                searchIntent.putExtra("company_id", AppConfig.SchoolID);
                searchIntent.putExtra("team_id", teamSpaceBean.getItemID());
                searchIntent.putExtra("team_name", teamSpaceBean.getName());
                startActivity(searchIntent);
                break;
            case R.id.image_filter_space:
                dialog = new FilterSpaceDialog(getActivity());
                dialog.setOptionsLinstener(this);
                dialog.setSelectedOption(sharedPreferences.getInt("show_all_spaces", 0));
                dialog.show();
                break;
            case R.id.image_switch_company:
                goToSwitchCompany();
                break;
        }
    }

    FilterSpaceDialog dialog;

    private void GoToSwitch() {
        Intent intent2;
        intent2 = new Intent(getActivity(), SwitchTeamActivity.class);
        if (user != null) {
            intent2.putExtra("role", user.getRole());
        }
        startActivity(intent2);
    }

    private void GoToTeamp() {
        Intent intent = new Intent(getActivity(), TeamPropertyActivity.class);
        if (teamSpaceBean.getItemID() > 0) {
            intent.putExtra("ItemID", teamSpaceBean.getItemID());
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "请先选择Team", Toast.LENGTH_LONG).show();
        }
    }


    private void MoreForTeam() {
        TeamMorePopup teamMorePopup = new TeamMorePopup();
        teamMorePopup.setIsTeam(true);
        teamMorePopup.setTSid(teamSpaceBean.getItemID());
        teamMorePopup.setTName(teamSpaceBean.getName());
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
                PopDeleteDocument pdd = new PopDeleteDocument();
                pdd.getPopwindow(getActivity());
                pdd.setPoPDismissListener(new PopDeleteDocument.PopDeleteDismissListener() {
                    @Override
                    public void PopDelete() {

                        LoginGet lg = new LoginGet();
                        lg.setBeforeDeleteTeamListener(new LoginGet.BeforeDeleteTeamListener() {
                            @Override
                            public void getBDT(int retdata) {
                                if (retdata > 0) {
                                    Toast.makeText(getActivity(), "Please delete space first", Toast.LENGTH_LONG).show();
                                } else {
                                    DeleteTeam();
                                }
                            }
                        });
                        lg.GetBeforeDeleteTeam(getActivity(), teamSpaceBean.getItemID() + "");
                    }

                    @Override
                    public void Open() {

                    }

                    @Override
                    public void Close() {

                    }
                });
                pdd.StartPop(moreOpation);


            }

            @Override
            public void rename() {
//                GoToRename();

                /*DialogRename dr = new DialogRename();
                dr.EditCancel(getActivity(), teamSpaceBean.getItemID(), true);*/
                GoToTeamp();


            }

            @Override
            public void quit() {

            }

            @Override
            public void edit() {
                Intent intent = new Intent(getActivity(), EditTeamActivity.class);
                intent.putExtra("team_id", teamSpaceBean.getItemID());
                intent.putExtra("team_name", teamSpaceBean.getName());
                startActivityForResult(intent, REQUEST_UPDATE_TEAM);

            }
        });
        teamMorePopup.StartPop(moreOpation);
    }

    private void GoToRename() {
        Intent intent = new Intent(getActivity(), RenameActivity.class);
        intent.putExtra("itemID", teamSpaceBean.getItemID());
        intent.putExtra("isteam", true);
        startActivity(intent);
    }

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
                    // TODO Auto-generated catch block
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

    private int itemID;

    private void AddFavorite(final Document fa) {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SpaceAttachment/UploadFromFavorite?spaceID=" + itemID
                                    + "&itemIDs=" + fa.getItemID(), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        EventBus.getDefault().post(new TeamSpaceBean());
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    //生成一个lessonId
    private void requestDocumentDetail(final Document document) {
        try {
            String url = AppConfig.URL_PUBLIC
                    + "Lesson/AddTempLessonWithOriginalDocument?attachmentID=" + document.getAttachmentID()
                    + "&Title=" + URLEncoder.encode(LoginGet.getBase64Password(document.getTitle()), "UTF-8");
            ServiceInterfaceTools.getinstance().addTempLessonWithOriginalDocument(url, ServiceInterfaceTools.ADDTEMPLESSONWITHORIGINALDOCUMENT,
                    new ServiceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            String data = (String) object;
                            String[] datas = data.split("-");
                            document.setLessonId(datas[0]);
                            document.setTempItemId(Integer.parseInt(datas[1]));
                            Message msg = Message.obtain();
                            msg.what = AppConfig.AddTempLesson;
                            msg.obj = document;
                            handler.sendMessage(msg);
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


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
                case AppConfig.AddTempLesson:
                    GoToVIew((Document) msg.obj);
                    break;
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    break;
                default:
                    break;
            }
        }
    };

    private void GoToVIew(Document lesson) {
//        Intent intent = new Intent(getActivity(), DocAndMeetingActivity.class);
        Intent intent = new Intent(getActivity(), WatchCourseActivity3.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", "Doc-" + AppConfig.UserID);

        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("spaceId", itemID);
        intent.putExtra("lessionId", lesson.getLessonId());
        intent.putExtra("type", 2);
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("documentId", lesson.getTempItemId());

        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        //-----
        intent.putExtra("meeting_id", "Doc-" + AppConfig.UserID);
        intent.putExtra("document_id", lesson.getTempItemId());
        intent.putExtra("meeting_type", 2);
        intent.putExtra("space_id", itemID);
        intent.putExtra("lession_id", Integer.parseInt(lesson.getLessonId()));
        startActivity(intent);
    }



    private ServiceBean bean = new ServiceBean();


    @Override
    public void onItem(TeamSpaceBean teamSpaceBean) {
//        Intent intent = new Intent(getActivity(), SpaceDocumentsActivity.class);
//        intent.putExtra("ItemID", teamSpaceBean.getItemID());
//        intent.putExtra("space_name", teamSpaceBean.getName());
//        intent.putExtra("team_id", sharedPreferences.getInt("teamid", 0));
//        startActivity(intent);
        EventSpaceFragment eventSpaceFragment = new EventSpaceFragment();
        eventSpaceFragment.setItemID(teamSpaceBean.getItemID());
        eventSpaceFragment.setSpaceName(teamSpaceBean.getName());
        eventSpaceFragment.setSpaceId(teamSpaceBean.getItemID());
        eventSpaceFragment.setType(1);
        eventSpaceFragment.setTeamName(sharedPreferences.getString("teamname",""));
        eventSpaceFragment.setTeamId(sharedPreferences.getInt("teamid",0));
        EventBus.getDefault().post(eventSpaceFragment);
    }

    @Override
    public void select(TeamSpaceBean teamSpaceBean) {

    }

    private void moveDocument(Document document) {
        Intent intent = new Intent(getActivity(), MoveDocumentActivity.class);
        intent.putExtra("team_id", teamSpaceBean.getItemID());
        intent.putExtra("space_id", document.getSpaceID());
        intent.putExtra("doc_id", document.getItemID());
        intent.putExtra("team_name", teamSpaceBean.getName());
        startActivity(intent);
    }

    @Override
    public void onUserInfoChanged(UserInCompany user) {
        handleRolePemission(user);
    }

    @Override
    public void onStart() {
        super.onStart();
        KloudCache.getInstance(getActivity()).registerUserInfoChangedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        KloudCache.getInstance(getActivity()).unregisterUserInfoChangedListener();
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


    @Override
    public void showAllOption() {
        sharedPreferences.edit().putInt("show_all_spaces", 1).commit();
        getSpaceList();
    }

    @Override
    public void showOnlyMineOption() {
        sharedPreferences.edit().putInt("show_all_spaces", 0).commit();
        getSpaceList();

    }

    private void goToSwitchCompany() {
        Intent intent = new Intent(getActivity(), SwitchOrganizationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
