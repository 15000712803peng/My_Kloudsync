package com.kloudsync.techexcel.search.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.DocumentDetail;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.MoveDocumentActivity;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogDeleteDocument;
import com.kloudsync.techexcel.help.DocumentOperationsDialog;
import com.kloudsync.techexcel.help.DocumentShareDialog;
import com.kloudsync.techexcel.help.EditDocumentDialog;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.response.NResponse;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.search.view.VDocumentSearch;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.adapter.DocumentAdapter;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class DocumentSearchActivity extends BaseActivity implements VDocumentSearch, View.OnClickListener, TextWatcher {

    private RecyclerView documentList;
    private TextView cancelText;
    String searchStr;
    EditText searchEdit;
    String teamName;
    private ImageView clearEditImage;
    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    int spaceId;
    private TextView messageText;
    int currentTeamId;
    SharedPreferences userPreferences;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    editCompleted();
                    break;
                case AppConfig.AddTempLesson:
                    watchCourse((Document) msg.obj);
                    break;
                case AppConfig.FAILED:
                    String errorMsg = (String) msg.obj;
                    if (TextUtils.isEmpty(errorMsg)) {
                        errorMsg = getResources().getString(R.string.operate_failure);
                    }
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void watchCourse(Document document) {
        Intent intent = new Intent(this, WatchCourseActivity3.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", document.getLessonId() + "," + AppConfig.UserID);
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", document.getLessonId());
        intent.putExtra("isInstantMeeting", 1);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        currentTeamId = userPreferences.getInt("teamid", 0);
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        spaceId = getIntent().getIntExtra("space_id", 0);
        teamName = getIntent().getStringExtra("team_name");
        documentList = findViewById(R.id.list);
        documentList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cancelText = findViewById(R.id.tv_cancel);
        cancelText.setOnClickListener(this);
        searchEdit = findViewById(R.id.et_search);
        searchEdit.addTextChangedListener(this);
        clearEditImage = findViewById(R.id.img_clear_edit);
        clearEditImage.setOnClickListener(this);
        noDataLayout = findViewById(R.id.no_data_lay);
        loadingBar = findViewById(R.id.loading_progress);
        messageText = findViewById(R.id.txt_msg);
    }

    @Override
    public void showLoading() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty(String message) {
        noDataLayout.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.INVISIBLE);
        documentList.setVisibility(View.GONE);
        messageText.setText(message);
    }

    private void deleteDocument(final Document document) {
        DialogDeleteDocument ddd = new DialogDeleteDocument();
        ddd.setDelDocListener(new DialogDeleteDocument.DialogDelDocListener() {
            @Override
            public void delDoc() {
                requestDelete(document);
            }
        });
        ddd.EditCancel(DocumentSearchActivity.this);
    }

    private void requestDelete(final Document lesson) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "SpaceAttachment/RemoveDocument?itemIDs=" +
                                    lesson.getItemID());
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
                    if (!NetWorkHelp.checkNetWorkStatus(getApplicationContext())) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start(ThreadManager.getManager());
    }


    private void editDocument(Document lesson) {
        EditDocumentDialog ped = new EditDocumentDialog();
        ped.setPopEditDocumentListener(new EditDocumentDialog.PopEditDocumentListener() {
            @Override
            public void popEditSuccess() {
                editCompleted();
                EventBus.getDefault().post(new TeamSpaceBean());
            }
        });
        ped.getPopwindow(this, lesson);
        ped.StartPop();
    }

    private static final int REQUEST_MOVE_DOCUMENT = 1;

    private void moveDocument(Document document) {
        Intent intent = new Intent(this, MoveDocumentActivity.class);
        intent.putExtra("team_id", currentTeamId);
        intent.putExtra("space_id", spaceId);
        intent.putExtra("doc_id", Integer.parseInt(document.getItemID()));
        intent.putExtra("team_name", teamName);
        startActivityForResult(intent, REQUEST_MOVE_DOCUMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_MOVE_DOCUMENT) {
                editCompleted();
                EventBus.getDefault().post(new TeamSpaceBean());
            }
        }
    }

    @Override
    public void showDocuments(List<Document> document, String keyword) {
        loadingBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        documentList.setVisibility(View.VISIBLE);
        documentAdapter = new DocumentAdapter(DocumentSearchActivity.this, document);
        documentList.setAdapter(documentAdapter);
        documentAdapter.setOnItemLectureListener(new DocumentAdapter.OnItemLectureListener() {
            @Override
            public void onItem(final Document lesson, View view) {
                DocumentOperationsDialog pd = new DocumentOperationsDialog();
                pd.getPopwindow(DocumentSearchActivity.this, lesson);
                pd.setPoPMoreListener(new DocumentOperationsDialog.PopDocumentListener() {
                    boolean flags;

                    @Override
                    public void PopView() {
//                                        getTempLesson(lesson);
//                                        GoToVIew(lesson);
                    }

                    @Override
                    public void PopDelete() {
                        deleteDocument(lesson);
                    }

                    @Override
                    public void PopEdit() {
                        flags = true;
                        editDocument(lesson);
                    }

                    @Override
                    public void PopShare() {
                        flags = true;
                        ShareKloudSync(lesson, -1);
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
                getTempLesson(lesson);

            }

            @Override
            public void share(int s, Document teamSpaceBeanFile) {
                ShareKloudSync(teamSpaceBeanFile, s);

            }

            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }

            @Override
            public void deleteRefresh() {
                editCompleted();
            }
        });

    }

    private void MoveDocument(final Document lesson) {

        LoginGet loginget = new LoginGet();
        loginget.setTeamSpaceGetListener(new LoginGet.TeamSpaceGetListener() {
            @Override
            public void getTS(ArrayList<Customer> list) {

            }
        });
        loginget.GetTeamSpace(this);
    }

    private void ShareKloudSync(final Document lesson, final int id) {
        final DocumentShareDialog psk = new DocumentShareDialog();
        psk.getPopwindow(this, lesson, id);
        psk.setPoPDismissListener(new DocumentShareDialog.PopShareKloudSyncDismissListener() {
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

    }

    @Override
    public void afterTextChanged(Editable s) {
        handler.removeCallbacks(editRunnable);
        handler.postDelayed(editRunnable, 600);
    }


    private Runnable editRunnable = new Runnable() {
        @Override
        public void run() {
            editCompleted();
        }
    };

    private void editCompleted() {
        searchStr = searchEdit.getText().toString().trim();
        if (TextUtils.isEmpty(searchStr)) {
            showEmpty("");
            return;
        }
        search(searchStr);
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

    private void search(final String searchStr) {
        showLoading();
        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, NResponse<NetworkResponse<DocumentDetail>>>() {
            @Override
            public NResponse<NetworkResponse<DocumentDetail>> apply(String searchStr) throws Exception {
                NResponse<NetworkResponse<DocumentDetail>> response = new NResponse<>();
                try {
                    response.setResponse(ServiceInterfaceTools.getinstance().searchDocumentsInSpace(spaceId + "", searchStr).execute());
                } catch (UnknownHostException e) {
                    return response.setNull(true);
                } catch (SocketTimeoutException exception) {
                    return response.setNull(true);
                }
                return response;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<NResponse<NetworkResponse<DocumentDetail>>>() {
            @Override
            public void accept(NResponse<NetworkResponse<DocumentDetail>> teamSearchResponseResponse) throws Exception {
                handleResponse(teamSearchResponseResponse, searchStr);
            }
        }).subscribe();

    }

    private DocumentAdapter documentAdapter;

    private void handleResponse(NResponse<NetworkResponse<DocumentDetail>> res, String keyword) {
        if (res == null || res.isNull()) {
            showEmpty(getString(R.string.rc_network_error));
            return;
        }
        Response<NetworkResponse<DocumentDetail>> response = res.getResponse();
        if (response.isSuccessful()) {
            int errorCode = response.body().getRetCode();
            if (errorCode == 0) {
                List<Document> doces = response.body().getRetData().getDocumentList();
                if (doces != null && doces.size() > 0) {
                    showDocuments(doces, keyword);
                } else {
                    showEmpty(getString(R.string.no_data));
                }
            } else {
                showEmpty(response.body().getErrorMessage());
            }

        }

    }


    private void getTempLesson(final Document fa) {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "Lesson/AddTempLessonWithOriginalDocument?attachmentID=" + fa.getAttachmentID()
                                    + "&Title=" + URLEncoder.encode(LoginGet.getBase64Password(fa.getTitle()), "UTF-8"), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "  " + responsedata.toString());
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddTempLesson;
                        JSONObject jsonObject1 = responsedata.getJSONObject("RetData");
                        msg.obj = fa;
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
}
