package com.kloudsync.techexcel.search.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.MoveDocumentActivity;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogDeleteDocument;
import com.kloudsync.techexcel.help.PopDocument;
import com.kloudsync.techexcel.help.PopEditDocument;
import com.kloudsync.techexcel.help.PopShareKloudSync;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.TeamAndSpaceSearchResponse;
import com.kloudsync.techexcel.search.view.VTeamAndDocSearch;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.KloudCache;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.SpaceDocumentsActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.adapter.HomeDocumentAdapter;
import com.ub.techexcel.adapter.SpaceAdapter;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class TeamAndDocSearchActivity extends BaseActivity implements SpaceAdapter.OnItemLectureListener, VTeamAndDocSearch, View.OnClickListener, TextWatcher {

    private RecyclerView spaceList;
    private RecyclerView docList;
    private SpaceAdapter spaceAdapter;
    private HomeDocumentAdapter docAdapter;
    private DatasForSearch datasForSearch;
    private static final int DATA_FROM_CACHE = 1;
    private static final int DATA_FROM_SERVER = 2;
    private TextView cancelText;
    String searchStr;
    EditText searchEdit;
    private LinearLayout spaceLayout;
    private LinearLayout docLayout;
    private ImageView clearEditImage;
    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    int strategy = DATA_FROM_CACHE;
    int companyID = -1;
    int teamID = -1;
    String teamName;
    private static final int REQUEST_MOVE_DOC = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        companyID = getIntent().getIntExtra("company_id", -1);
        teamID = getIntent().getIntExtra("team_id", -1);
        teamName = getIntent().getStringExtra("team_name");
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_doc_and_team_search;
    }

    @Override
    protected void initView() {
        spaceList = findViewById(R.id.list_space);
        docList = findViewById(R.id.list_doc);
        datasForSearch = new DatasForSearch();
        spaceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        spaceList.setNestedScrollingEnabled(false);
        docList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        docList.setNestedScrollingEnabled(false);
        cancelText = findViewById(R.id.tv_cancel);
        cancelText.setOnClickListener(this);
        searchEdit = findViewById(R.id.et_search);
        searchEdit.addTextChangedListener(this);
        spaceLayout = findViewById(R.id.spaces_layout);
        docLayout = findViewById(R.id.doc_layout);
        clearEditImage = findViewById(R.id.img_clear_edit);
        clearEditImage.setOnClickListener(this);
        noDataLayout = findViewById(R.id.no_data_lay);
        loadingBar = findViewById(R.id.loading_progress);
    }

    @Override
    public void onItem(TeamSpaceBean teamSpaceBean) {
        Intent intent = new Intent(this, SpaceDocumentsActivity.class);
        intent.putExtra("ItemID", teamSpaceBean.getItemID());
        startActivity(intent);
    }

    @Override
    public void select(TeamSpaceBean teamSpaceBean) {

    }

    @Override
    public void showSpacesLoading() {

    }

    @Override
    public void showDocsLoading() {

    }

    @Override
    public void showEmptySpaces() {
        spaceLayout.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyDocs() {
        docLayout.setVisibility(View.GONE);
    }

    @Override
    public void showSpaces(List<TeamSpaceBean> spacesData) {
        spaceLayout.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        spaceAdapter = new SpaceAdapter(this, spacesData, false, false);
        spaceAdapter.setFromSearch(true, searchStr);
        spaceList.setAdapter(spaceAdapter);
        spaceAdapter.setOnItemLectureListener(this);

    }

    @Override
    public void showDoces(List<Document> docsData) {
        docLayout.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        docAdapter = new HomeDocumentAdapter(this, docsData);
        docAdapter.setFromSearch(true, searchStr);
        docList.setAdapter(docAdapter);
        docAdapter.setOnItemLectureListener(new HomeDocumentAdapter.OnItemLectureListener() {
            @Override
            public void onItem(final Document lesson, View view) {
                PopDocument pd = new PopDocument();
                pd.getPopwindow(TeamAndDocSearchActivity.this, lesson);
                pd.setPoPMoreListener(new PopDocument.PopDocumentListener() {
                    @Override
                    public void PopView() {

                    }

                    @Override
                    public void PopDelete() {
                        deleteDoc(lesson);
                    }

                    @Override
                    public void PopEdit() {
                        editLesson(lesson);
                    }

                    @Override
                    public void PopShare() {
                        shareDoc(lesson, -1);
                    }

                    @Override
                    public void PopMove() {
                        moveDocument(lesson);
                    }

                    @Override
                    public void PopBack() {

                    }

                });
                pd.StartPop(view);
            }

            @Override
            public void onRealItem(Document lesson, View view) {
                if (!Tools.isFastClick()) {
                    getTempLesson(lesson);
                }

            }

            @Override
            public void share(int s, Document teamSpaceBeanFile, SoundtrackBean soundtrackBean) {

            }

            @Override
            public void dismiss() {

            }

            @Override
            public void open() {

            }

            @Override
            public void deleteRefresh() {

            }
        });
    }

    @Override
    public void showEmpty() {
        noDataLayout.setVisibility(View.VISIBLE);
        showEmptyDocs();
        showEmptySpaces();
        loadingBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                hideInput();
                finish();
                break;
            case R.id.img_clear_edit:
                searchEdit.setText("");
            default:
                break;
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        handler.removeCallbacks(editRunnable);
        handler.postDelayed(editRunnable, 600);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    public final class DatasForSearch {
        List<TeamSpaceBean> spacesData;
        List<Document> docsData;

        public List<TeamSpaceBean> getSpacesData() {
            return spacesData;
        }

        public DatasForSearch setSpacesData(List<TeamSpaceBean> spacesData) {
            this.spacesData = spacesData;
            return this;
        }

        public List<Document> getDocsData() {
            return docsData;
        }

        public DatasForSearch setDocsData(List<Document> docsData) {
            this.docsData = docsData;
            return this;
        }
    }

    class SpacesTask implements Runnable {
        VTeamAndDocSearch view;
        int stategy;
        String searchStr;

        public SpacesTask(VTeamAndDocSearch view, int strategy, String searchStr) {
            this.view = view;
            this.stategy = strategy;
            this.searchStr = searchStr;
        }

        @Override
        public void run() {
            task();
        }

        private void task() {
            if (stategy == 1) {
                Observable.just(datasForSearch).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DatasForSearch>() {
                    @Override
                    public void accept(DatasForSearch datasForSearch) throws Exception {
                        view.showSpacesLoading();
                        noDataLayout.setVisibility(View.INVISIBLE);
                    }
                }).observeOn(Schedulers.computation()).doOnNext(new Consumer<DatasForSearch>() {
                    @Override
                    public void accept(DatasForSearch datasForSearch) throws Exception {
                        datasForSearch.setSpacesData(KloudCache.getInstance(TeamAndDocSearchActivity.this).getSpaceList() == null ?
                                new ArrayList<TeamSpaceBean>() : KloudCache.getInstance(TeamAndDocSearchActivity.this).getSpaceList());
                    }
                }).map(new Function<DatasForSearch, DatasForSearch>() {

                    @Override
                    public DatasForSearch apply(DatasForSearch datasForSearch) throws Exception {
                        List<TeamSpaceBean> spaces = new ArrayList<>();
                        if (TextUtils.isEmpty(searchStr)) {
                            return datasForSearch.setSpacesData(spaces);
                        }
                        for (TeamSpaceBean space : datasForSearch.getSpacesData()) {
                            if (space.getName().contains(searchStr)) {
                                spaces.add(space);
                            }
                        }
                        return datasForSearch.setSpacesData(spaces);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<DatasForSearch>() {
                    @Override
                    public void accept(DatasForSearch datasForSearch) throws Exception {
                        if (datasForSearch.getSpacesData() == null || datasForSearch.getSpacesData().size() == 0) {
                            view.showEmptySpaces();
                        } else {
                            view.showSpaces(datasForSearch.spacesData);
                        }
                        TeamAndDocSearchActivity.this.datasForSearch.setSpacesData(datasForSearch.getSpacesData());

                    }
                });
            } else if (stategy == 2) {

            }

        }
    }

    class DocsTask implements Runnable {
        VTeamAndDocSearch view;
        int stategy;
        String searchStr;

        public DocsTask(VTeamAndDocSearch view, int strategy, String searchStr) {
            this.view = view;
            this.stategy = strategy;
            this.searchStr = searchStr;
        }

        @Override
        public void run() {
            task();
        }

        private void task() {
            if (stategy == 1) {
                Observable.just(datasForSearch).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DatasForSearch>() {
                    @Override
                    public void accept(DatasForSearch datasForSearch) throws Exception {
                        view.showDocsLoading();
                        noDataLayout.setVisibility(View.INVISIBLE);
                    }
                }).observeOn(Schedulers.computation()).doOnNext(new Consumer<DatasForSearch>() {
                    @Override
                    public void accept(DatasForSearch datasForSearch) throws Exception {
                        datasForSearch.setDocsData(KloudCache.getInstance(TeamAndDocSearchActivity.this).getDocList() == null ?
                                new ArrayList<Document>() : KloudCache.getInstance(TeamAndDocSearchActivity.this).getDocList());
                    }
                }).map(new Function<DatasForSearch, DatasForSearch>() {

                    @Override
                    public DatasForSearch apply(DatasForSearch datasForSearch) throws Exception {
                        List<Document> docs = new ArrayList<>();
                        if (TextUtils.isEmpty(searchStr)) {
                            return datasForSearch.setDocsData(docs);
                        }
                        for (Document file : datasForSearch.getDocsData()) {
                            if (file.getTitle().contains(searchStr)) {
                                docs.add(file);
                            }
                        }
                        return datasForSearch.setDocsData(docs);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<DatasForSearch>() {
                    @Override
                    public void accept(DatasForSearch datasForSearch) throws Exception {
                        Log.e("obserable", "get docs completed");
                        Thread.sleep(1000);
                        if (datasForSearch.getDocsData() == null || datasForSearch.getDocsData().size() == 0) {
                            view.showEmptyDocs();
                        } else {
                            view.showDoces(datasForSearch.docsData);
                        }
                        TeamAndDocSearchActivity.this.datasForSearch.setDocsData(datasForSearch.getDocsData());
                    }

                });
            } else if (stategy == 2) {

            }

        }
    }

    private Runnable editRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("run", "edit runnable run");
            editCompleted(strategy);
        }
    };

    private void editCompleted(int strategy) {
        searchStr = searchEdit.getText().toString();
        if (TextUtils.isEmpty(searchStr)) {
            showEmpty();
            return;
        }
        syncSearch(searchStr, strategy, this);
    }

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideInput();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void getTempLesson(final Document document) {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "Lesson/AddTempLessonWithOriginalDocument?attachmentID=" + document.getAttachmentID()
                                    + "&Title=" + URLEncoder.encode(LoginGet.getBase64Password(document.getTitle()), "UTF-8"), jsonObject);
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddTempLesson;
                        JSONObject jsonObject1 = responsedata.getJSONObject("RetData");
                        document.setLessonId(jsonObject1.getString("LessonID"));
                        msg.obj = document;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(TeamAndDocSearchActivity.this,
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.AddTempLesson:
                    goToCourse((Document) msg.obj);
                    break;
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    Object obj = msg.obj;
                    if (obj instanceof Document) {
                        KloudCache.getInstance(TeamAndDocSearchActivity.this).deleteDoc((Document) obj);
                    } else if (obj instanceof TeamSpaceBean) {
                        KloudCache.getInstance(TeamAndDocSearchActivity.this).deleteSpace((TeamSpaceBean) obj);
                    }
                    refresh(strategy);
                    Toast.makeText(getApplicationContext(), "操作成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void goToCourse(Document document) {
        Intent intent = new Intent(this, DocAndMeetingActivity.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", document.getLessonId() + "," + AppConfig.UserID);
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", document.getLessonId());
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        startActivity(intent);
    }

    private void asyncSearch(String searchStr) {
        ThreadManager.getManager().execute(new SpacesTask(this, DATA_FROM_CACHE, searchStr));
        ThreadManager.getManager().execute(new DocsTask(this, DATA_FROM_CACHE, searchStr));
    }

    private void syncSearch(final String searchStr, int strategy, final VTeamAndDocSearch view) {
        if (strategy == DATA_FROM_CACHE) {
            Observable.just(datasForSearch).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DatasForSearch>() {
                @Override
                public void accept(DatasForSearch datasForSearch) throws Exception {
                    loadingBar.setVisibility(View.VISIBLE);
                    view.showSpacesLoading();
                }
            }).observeOn(Schedulers.computation()).doOnNext(new Consumer<DatasForSearch>() {
                @Override
                public void accept(DatasForSearch datasForSearch) throws Exception {
                    datasForSearch.setSpacesData(KloudCache.getInstance(TeamAndDocSearchActivity.this).getSpaceList() == null ?
                            new ArrayList<TeamSpaceBean>() : KloudCache.getInstance(TeamAndDocSearchActivity.this).getSpaceList());
                }
            }).map(new Function<DatasForSearch, DatasForSearch>() {
                @Override
                public DatasForSearch apply(DatasForSearch datasForSearch) throws Exception {
                    List<TeamSpaceBean> spaces = new ArrayList<>();
                    if (TextUtils.isEmpty(searchStr)) {
                        return datasForSearch.setSpacesData(spaces);
                    }
                    for (TeamSpaceBean space : datasForSearch.getSpacesData()) {
                        if (space.getName().contains(searchStr)) {
                            spaces.add(space);
                        }
                    }
                    return datasForSearch.setSpacesData(spaces);
                }
            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DatasForSearch>() {
                @Override
                public void accept(DatasForSearch datasForSearch) throws Exception {
                    if (datasForSearch.getSpacesData() == null || datasForSearch.getSpacesData().size() == 0) {
                        view.showEmptySpaces();
                    } else {
                        view.showSpaces(datasForSearch.spacesData);
                    }
                }
            }).doOnNext(new Consumer<DatasForSearch>() {
                @Override
                public void accept(DatasForSearch datasForSearch) throws Exception {
                    view.showDocsLoading();
                }
            }).observeOn(Schedulers.computation()).doOnNext(new Consumer<DatasForSearch>() {
                @Override
                public void accept(DatasForSearch datasForSearch) throws Exception {
                    datasForSearch.setDocsData(KloudCache.getInstance(TeamAndDocSearchActivity.this).getDocList() == null ?
                            new ArrayList<Document>() : KloudCache.getInstance(TeamAndDocSearchActivity.this).getDocList());
                }
            }).map(new Function<DatasForSearch, DatasForSearch>() {
                @Override
                public DatasForSearch apply(DatasForSearch datasForSearch) throws Exception {
                    List<Document> docs = new ArrayList<>();
                    if (TextUtils.isEmpty(searchStr)) {
                        return datasForSearch.setDocsData(docs);
                    }

                    for (Document file : datasForSearch.getDocsData()) {
                        if (file.getTitle().contains(searchStr)) {
                            docs.add(file);
                        }
                    }
                    return datasForSearch.setDocsData(docs);
                }
            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DatasForSearch>() {
                @Override
                public void accept(DatasForSearch datasForSearch) throws Exception {
                    if (datasForSearch.getDocsData() == null || datasForSearch.getDocsData().size() == 0) {
                        view.showEmptyDocs();
                    } else {
                        view.showDoces(datasForSearch.docsData);
                    }
                }

            }).subscribe(new Consumer<DatasForSearch>() {
                @Override
                public void accept(DatasForSearch datasForSearch) throws Exception {
                    loadingBar.setVisibility(View.INVISIBLE);
                    if ((datasForSearch.getDocsData() == null || datasForSearch.getDocsData().size() == 0) &&
                            (datasForSearch.getSpacesData() == null || datasForSearch.getSpacesData().size() == 0)) {
                        showEmpty();
                    }
                }
            });
        } else if (strategy == DATA_FROM_SERVER) {
            Observable.just(datasForSearch).observeOn(Schedulers.io()).doOnNext(new Consumer<DatasForSearch>() {
                @Override
                public void accept(DatasForSearch datasForSearch) throws Exception {
                    Response<TeamAndSpaceSearchResponse> response = ServiceInterfaceTools.getinstance().searchSpacesAndDocs(companyID, teamID, searchStr).execute();
                    if (response.isSuccessful()) {
                        if (response.body().getRetCode() == 0) {
                            datasForSearch = (response.body() == null) ? datasForSearch : response.body().getRetData();
                        }
                    } else {

                    }
                }
            }).subscribe();
        }
    }

    private void deleteDoc(final Document lesson) {
        DialogDeleteDocument delDocDialog = new DialogDeleteDocument();
        delDocDialog.setDelDocListener(new DialogDeleteDocument.DialogDelDocListener() {
            @Override
            public void delDoc() {
                deleteLesson(lesson);
            }
        });
        delDocDialog.EditCancel(this);
    }

    private void deleteLesson(final Document lesson) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "SpaceAttachment/RemoveDocument?itemIDs=" +
                                    lesson.getItemID());
                    Log.e("RemoveDocument", responsedata.toString());
                    int retcode = (Integer) responsedata.get("RetCode");
                    msg = new Message();
                    if (0 == retcode) {
                        msg.what = AppConfig.DELETESUCCESS;
                        msg.obj = lesson;
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
                    if (!NetWorkHelp.checkNetWorkStatus(TeamAndDocSearchActivity.this)) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start(ThreadManager.getManager());
    }

    private void refresh(int strategy) {
        editCompleted(strategy);
    }

    private void editLesson(Document lesson) {
        PopEditDocument ped = new PopEditDocument();
        ped.setPopEditDocumentListener(new PopEditDocument.PopEditDocumentListener() {
            @Override
            public void popEditSuccess() {
                EventBus.getDefault().post(new TeamSpaceBean());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh(strategy);
                    }
                }, 1000);
            }
        });
        ped.getPopwindow(this, lesson);
        ped.StartPop();
    }

    private void shareDoc(final Document document, final int id) {
        final PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(this, document, id);
        psk.startPop();
    }

    private void moveDocument(Document document) {
        Intent intent = new Intent(this, MoveDocumentActivity.class);
        intent.putExtra("team_id", teamID);
        intent.putExtra("space_id", document.getSpaceID());
        intent.putExtra("doc_id", document.getItemID());
        intent.putExtra("team_name", teamName);
        startActivityForResult(intent, REQUEST_MOVE_DOC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MOVE_DOC && resultCode == RESULT_OK) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refresh(strategy);
                }
            }, 1000);
        }
    }
}
