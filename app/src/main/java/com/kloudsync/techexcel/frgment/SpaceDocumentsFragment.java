package com.kloudsync.techexcel.frgment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSyncSucc;
import com.kloudsync.techexcel.bean.EventViewDocInSpacePermissionGranted;
import com.kloudsync.techexcel.bean.RoleInTeam;
import com.kloudsync.techexcel.bean.params.EventCamera;
import com.kloudsync.techexcel.bean.params.EventTeamFragment;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.UploadFileDialog;
import com.kloudsync.techexcel.docment.EditSpaceActivity;
import com.kloudsync.techexcel.docment.FavoriteDocumentsActivity;
import com.kloudsync.techexcel.docment.MoveDocumentActivity;
import com.kloudsync.techexcel.docment.RenameActivity;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogDeleteDocument;
import com.kloudsync.techexcel.help.DocChooseDialog;
import com.kloudsync.techexcel.help.KloudPerssionManger;
import com.kloudsync.techexcel.help.PopDocument;
import com.kloudsync.techexcel.help.PopEditDocument;
import com.kloudsync.techexcel.help.PopShareKloudSync;
import com.kloudsync.techexcel.help.Popupdate;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.ConvertingResult;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Space;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.search.ui.DocumentSearchActivity;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.kloudsync.techexcel.tool.FileGetTool;
import com.kloudsync.techexcel.tool.KloudCache;
import com.kloudsync.techexcel.tool.Md5Tool;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.SpaceDeletePopup;
import com.ub.kloudsync.activity.SpacePropertyActivity;
import com.ub.kloudsync.activity.SwitchSpaceActivity;
import com.ub.kloudsync.activity.TeamMorePopup;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.adapter.HomeDocumentAdapter;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_UPLOADFILE;
import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE_FOR_VIWE_DOC_IN_SPACE;

public class SpaceDocumentsFragment extends Fragment implements View.OnClickListener, DocChooseDialog.SelectedOptionListener, DocumentUploadTool.DocUploadDetailLinstener {

    private RecyclerView mTeamRecyclerView;
    private int spaceId;
    private int teamId;
    private TextView teamspacename;
    //    private TextView tv_fs;
    private RelativeLayout backLayout;
    private RelativeLayout addDocImage;
    private ImageView moreOptionsImage;
    private RelativeLayout teamRl;
    private HomeDocumentAdapter teamSpaceDocumentAdapter;
    private TeamSpaceBean selectSpace;
    String targetFolderKey;
    int field;
    private Uploadao uploadao = new Uploadao();
    private File mfile;
    private String MD5Hash;
    private String fileName;
    private Timer timer1;
    private TimerTask timerTask1;
    private static final int REQUEST_SELECTED_IMAGE = 1;
    private static final int REQUEST_SELECT_DOC = 2;
    private static final int REQUEST_CODE_CHANGESPACE = 3;
    private static final int REQUEST_EDIT_SPACE = 4;
    private static final int REQUEST_MOVE_DOCUMENT = 5;

    private TextView dirText;
    private TextView projectText;
    private String projectName;


    UploadFileDialog uploadFileDialog;

    private TextView titleText;

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
                    getTeamItem();
                    EventBus.getDefault().post(new TeamSpaceBean());
                    break;
                case AppConfig.SUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    break;
                case AppConfig.AddTempLesson:
                    GoToVIew((Document) msg.obj);
                    break;
                case AppConfig.LOAD_FINISH:
                    GoToVIew();
                    break;
                default:
                    break;
            }
        }
    };

    private void GoToVIew() {
        Intent intent = new Intent(getActivity(), DocAndMeetingActivity.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", bean.getId() + "," + AppConfig.UserID);
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", "");
        intent.putExtra("isInstantMeeting", 1);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
// -----------------
        intent.putExtra("meeting_id", bean.getId() + "," + AppConfig.UserID);
//        intent.putExtra("meeting_id", "Doc-" + AppConfig.UserID);
        intent.putExtra("document_id", "");
        intent.putExtra("meeting_type", 2);
        intent.putExtra("space_id", spaceId);
        intent.putExtra("lession_id", 0);
        startActivity(intent);
    }

    private void GoToVIew(Document lesson) {
        updateSocket();
        Intent intent = new Intent(getActivity(), DocAndMeetingActivity.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", lesson.getLessonId() + "," + AppConfig.UserID);
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", lesson.getLessonId());
        intent.putExtra("isInstantMeeting", 1);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);

        //----------
        intent.putExtra("meeting_id", Integer.parseInt(lesson.getLessonId()) + "," + AppConfig.UserID);
//        intent.putExtra("meeting_id", "Doc-" + AppConfig.UserID);
        intent.putExtra("document_id", lesson.getTempItemId());
        intent.putExtra("meeting_type", 2);
        intent.putExtra("space_id", spaceId);
        intent.putExtra("lession_id", Integer.parseInt(lesson.getLessonId()));
        startActivity(intent);
    }

    private void ViewdoHaha(final String meetingID) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnjson = com.ub.techexcel.service.ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Lesson/Item?lessonID=" + meetingID);
                formatServiceData(returnjson);
            }
        }).start(ThreadManager.getManager());
    }

    private ServiceBean bean = new ServiceBean();

    private void formatServiceData(JSONObject returnJson) {
        Log.e("returnJson", returnJson.toString());
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONObject service = returnJson.getJSONObject("RetData");
                    bean = new ServiceBean();

                    bean.setId(service.getInt("LessonID"));
                    String des = service.getString("Description");
                    bean.setDescription(des);
                    int statusID = service.getInt("StatusID");
                    bean.setStatusID(statusID);
                    bean.setRoleinlesson(service.getInt("RoleInLesson"));
                    JSONArray memberlist = service.getJSONArray("MemberInfoList");
                    for (int i = 0; i < memberlist.length(); i++) {
                        JSONObject jsonObject = memberlist.getJSONObject(i);
                        int role = jsonObject.getInt("Role");
                        if (role == 2) { //teacher
                            bean.setTeacherName(jsonObject.getString("MemberName"));
                            bean.setTeacherId(jsonObject.getString("MemberID"));
                        } else if (role == 1) {
                            bean.setUserName(jsonObject.getString("MemberName"));
                            bean.setUserId(jsonObject.getString("MemberID"));
                        }
                    }
                    handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String spaceName;
    private LinearLayout searchLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.spacedocumentteam, container, false);
            initView(view);
        }
        load();
        return view;
    }

    private void load() {
        spaceId = getArguments().getInt("ItemID", 0);
        spaceName = getArguments().getString("space_name");
        teamId = getArguments().getInt("team_id", 0);
//        titleText.setText(spaceName);
        teamspacename.setText(spaceName);
        projectName = getArguments().getString("project_name", "");
        projectText.setText(projectName);
        if (!TextUtils.isEmpty(spaceName)) {
            dirText.setText(spaceName.substring(0, 1).toUpperCase());
        }
        getTeamItem();
    }

    private void initView(View view) {
        mTeamRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        teamspacename = (TextView) view.findViewById(R.id.teamspacename);
        titleText = (TextView) view.findViewById(R.id.tv_title);
//        tv_fs = (TextView) findViewById(R.id.tv_fs);
        backLayout = (RelativeLayout) view.findViewById(R.id.layout_back);
        addDocImage = (RelativeLayout) view.findViewById(R.id.image_add);
        moreOptionsImage = (ImageView) view.findViewById(R.id.image_more_options);
        dirText = view.findViewById(R.id.switch_dir);
        projectText = view.findViewById(R.id.txt_project_name);
        searchLayout = view.findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
        teamRl = (RelativeLayout) view.findViewById(R.id.teamrl);
        teamRl.setOnClickListener(this);
        addDocImage.setOnClickListener(this);
        moreOptionsImage.setOnClickListener(this);
        backLayout.setOnClickListener(this);
    }

    String teamName = "";

    public void getTeamItem() {

        TeamSpaceInterfaceTools.getinstance().getTeamItem(AppConfig.URL_PUBLIC + "TeamSpace/Item?itemID=" + spaceId, TeamSpaceInterfaceTools.GETTEAMITEM, new TeamSpaceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                TeamSpaceBean teamSpaceBean = (TeamSpaceBean) object;
//                teamName = teamSpaceBean.getName();
//                teamspacename.setText(teamName);
                getSpaceList();
            }
        });
    }

    private Document tempClickedDocument;

    private void getSpaceList() {
        TeamSpaceInterfaceTools.getinstance().getSpaceDocumentList(AppConfig.URL_PUBLIC + "SpaceAttachment/List?spaceID=" + spaceId + "&type=1&pageIndex=0&pageSize=20&searchText=",
                TeamSpaceInterfaceTools.GETSPACEDOCUMENTLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<Document> list = (List<Document>) object;
                        teamSpaceDocumentAdapter = new HomeDocumentAdapter(getActivity(), list);
                        mTeamRecyclerView.setAdapter(teamSpaceDocumentAdapter);
                        teamSpaceDocumentAdapter.setOnItemLectureListener(new HomeDocumentAdapter.OnItemLectureListener() {
                            @Override
                            public void onItem(final Document lesson, View view) {
                                PopDocument pd = new PopDocument();
                                pd.getPopwindow(getActivity(), lesson);
                                pd.setPoPMoreListener(new PopDocument.PopDocumentListener() {
                                    boolean flags;

                                    @Override
                                    public void PopView() {
//                                        getTempLesson(lesson);
//                                        GoToVIew(lesson);
                                    }

                                    @Override
                                    public void PopDelete() {
                                        int role = KloudCache.getInstance(getActivity()).getUserRole();
                                        int teamRole = KloudCache.getInstance(getActivity()).getTeamRole().getTeamRole();
                                        if (role == 7 || role == 8 || teamRole == RoleInTeam.ROLE_OWENER || teamRole == RoleInTeam.ROLE_ADMIN) {
                                                DialogDelete(lesson);
                                        } else{
                                                Toast.makeText(getActivity(),"你没有权限进行删除操作",Toast.LENGTH_LONG).show();
                                            }
                                    }

                                    @Override
                                    public void PopEdit() {
                                        flags = true;
                                        EditLesson(lesson);
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
                                tempClickedDocument = lesson;
                                viewDocIfPermissionGranted(lesson);
//                                getTempLesson(lesson);

                            }

                            @Override
                            public void share(int s, Document teamSpaceBeanFile, SoundtrackBean soundtrackBean) {
                                ShareKloudSync(teamSpaceBeanFile, s);

                            }

                            @Override
                            public void dismiss() {

                            }

                            @Override
                            public void open() {

                            }

                            @Override
                            public void deleteRefresh() {
                                getSpaceList();
                            }
                        });

                    }
                });
    }

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();


    private void moveDocument(final Document lesson) {
        Intent intent = new Intent(getActivity(), MoveDocumentActivity.class);
        intent.putExtra("team_id", teamId);
        intent.putExtra("space_id", spaceId);
        intent.putExtra("doc_id", lesson.getItemID());
        intent.putExtra("team_name", teamName);
        startActivityForResult(intent, REQUEST_MOVE_DOCUMENT);
    }


    private void ShareKloudSync(final Document lesson, final int id) {
        final PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(getActivity(), lesson, id);
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

    private void EditLesson(Document lesson) {
        PopEditDocument ped = new PopEditDocument();
        ped.setPopEditDocumentListener(new PopEditDocument.PopEditDocumentListener() {
            @Override
            public void popEditSuccess() {
                getSpaceList();
                EventBus.getDefault().post(new TeamSpaceBean());
            }
        });
        ped.getPopwindow(getActivity(), lesson);
        ped.StartPop();
    }

    Document lesson2;

    private void DialogDelete(final Document lesson) {
        DialogDeleteDocument ddd = new DialogDeleteDocument();
        ddd.setDelDocListener(new DialogDeleteDocument.DialogDelDocListener() {
            @Override
            public void delDoc() {
                DeleteLesson(lesson);
            }
        });
        ddd.EditCancel(getActivity());
    }

    private void DeleteLesson(final Document lesson) {
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
                    if (!NetWorkHelp.checkNetWorkStatus(getActivity())) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start(ThreadManager.getManager());
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
                        fa.setLessonId(jsonObject1.getString("LessonID"));
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


    private static final int REQUEST_SELECTED_FILE = 40;
    private static final int REQUEST_SELECTED_CAMERA= 50;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECTED_IMAGE && data != null) {
                String path = FileUtils.getPath(getActivity(), data.getData());
                String pathname = path.substring(path.lastIndexOf("/") + 1);
                LineItem file = new LineItem();
                file.setUrl(path);
                file.setFileName(pathname);
//                uploadFile(file, spaceId);
                AddDocumentTool.addDocumentToSpace(getActivity(), path, spaceId, new DocumentUploadTool.DocUploadDetailLinstener() {
                    @Override
                    public void uploadStart() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (uploadFileDialog == null) {
                                    uploadFileDialog = new UploadFileDialog(getActivity());
                                    uploadFileDialog.setTile("uploading");
                                    uploadFileDialog.show();

                                } else {
                                    if (!uploadFileDialog.isShowing()) {
                                        uploadFileDialog.show();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void uploadFile(final int progress) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                    uploadFileDialog.setProgress(progress);
                                }
                            }
                        });
                    }

                    @Override
                    public void convertFile(final int progress) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                    uploadFileDialog.setTile("Converting");
                                    uploadFileDialog.setProgress(progress);
                                }
                            }
                        });
                    }

                    @Override
                    public void uploadFinished(Object result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addDocSucc();
                            }
                        });

                    }

                    @Override
                    public void uploadError(String message) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "add failed", Toast.LENGTH_SHORT).show();
                                if (uploadFileDialog != null) {
                                    uploadFileDialog.cancel();
                                }
                            }
                        });
                    }
                });
            } else if (requestCode == REQUEST_CODE_CHANGESPACE) {
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
//                titleText.setText(selectSpace.getName());
                teamspacename.setText(selectSpace.getName());
                getTeamItem();

            } else if (requestCode == REQUEST_SELECT_DOC) {
                EventBus.getDefault().post(new TeamSpaceBean());
                getTeamItem();
            } else if (requestCode == REQUEST_EDIT_SPACE) {
                getTeamItem();
                EventBus.getDefault().post(new TeamSpaceBean());
            } else if (requestCode == REQUEST_MOVE_DOCUMENT) {
                getTeamItem();
                EventBus.getDefault().post(new TeamSpaceBean());
            } else if (requestCode == REQUEST_SELECTED_FILE) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOkKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
            } else if (requestCode == REQUEST_SELECTED_CAMERA) {
                if (cameraFile != null && cameraFile.exists()) {
                    Log.e("onActivityResult", "camera_file:" + cameraFile);
                    uploadFile(cameraFile.getAbsolutePath());
                }
            }
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        uploadFile(imagePath);
    }

    public String getImagePath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);   //内容提供器
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));   //获取路径
            }
        }
        cursor.close();
        return path;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOkKitKat(Intent data) {
        String imagePath=null;
        Uri uri = data.getData();
        Log.d("uri=intent.getData :",""+uri);
        if (DocumentsContract.isDocumentUri(getActivity(),uri)){
            String docId = DocumentsContract.getDocumentId(uri);        //数据表里指定的行
            Log.d("getDocumentId(uri) :",""+docId);
            Log.d("uri.getAuthority() :",""+uri.getAuthority());
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }

        }
        else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }
        uploadFile(imagePath);
    }



    private void  uploadFile(String path){
        if(TextUtils.isEmpty(path)){
            Toast.makeText(  getActivity(),"path null",Toast.LENGTH_LONG).show();
            return;
        }
        Log.e("onActivityResult",path);
        String pathname = path.substring(path.lastIndexOf("/") + 1);
        LineItem file = new LineItem();
        file.setUrl(path);
        file.setFileName(pathname);
//                        uploadFile(file, spaceId);
        AddDocumentTool.addDocumentToSpace(getActivity(), path, spaceId, new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart(){
                getActivity(). runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog == null) {
                            uploadFileDialog = new UploadFileDialog(  getActivity());
                            uploadFileDialog.setTile("uploading");
                            uploadFileDialog.show();

                        } else {
                            if (!uploadFileDialog.isShowing()) {
                                uploadFileDialog.show();
                            }
                        }
                    }
                });
            }

            @Override
            public void uploadFile(final int progress) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.setProgress(progress);
                        }
                    }
                });
            }

            @Override
            public void convertFile(final int progress) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.setTile("Converting");
                            uploadFileDialog.setProgress(progress);
                        }
                    }
                });
            }

            @Override
            public void uploadFinished(Object result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addDocSucc();
                    }
                });

            }

            @Override
            public void uploadError(String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "add failed", Toast.LENGTH_SHORT).show();
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                    }
                });
            }
        });
    }




    private Popupdate puo;

    public void uploadFile2(final LineItem attachmentBean) {
        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {

                puo = new Popupdate();
                puo.getPopwindow(getActivity(), attachmentBean.getFileName());
                puo.setPopCancelListener(new Popupdate.PopCancelListener() {
                    @Override
                    public void Cancel() {
                    }
                });
                puo.StartPop(mTeamRecyclerView);
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(attachmentBean, ud);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(attachmentBean, ud);
                }
            }
        });
        lg.GetprepareUploading(getActivity());
    }

    public void uploadWithTransferUtility(final LineItem attachmentBean, final Uploadao ud) {
        mfile = new File(attachmentBean.getUrl());
        fileName = mfile.getName();
        String name2 = AppConfig.UserID + mfile.getName();
        MD5Hash = Md5Tool.transformMD5(name2);

        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                ud.getAccessKeyId(),
                ud.getAccessKeySecret(),
                ud.getSecurityToken());
        AmazonS3Client s3 = new AmazonS3Client(sessionCredentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getActivity())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3)
                        .build();
        TransferObserver uploadObserver =
                transferUtility.upload(
                        ud.getBucketName(),
                        MD5Hash,
                        mfile);
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("YourActivity", "id:" + id + "  state:" + state);
                if (TransferState.COMPLETED == state) {
                    attachmentBean.setFlag(2);
                    if (puo != null) {
                        puo.DissmissPop();
                    }
                    startConverting(ud, attachmentBean);
                }
            }

            @Override
            public void onProgressChanged(int id, final long bytesCurrent, final long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
                if (puo != null) {
                    puo.setProgress(bytesTotal, bytesCurrent);
                }

                Log.e("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("YourActivity", "onError");
            }

        });

    }

    private OSS oss;

    private void initOSS(final LineItem attachmentBean, final Uploadao ud) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ud.getAccessKeyId(),
                        ud.getAccessKeySecret(), ud.getSecurityToken());
                ClientConfiguration conf = new ClientConfiguration();
                conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
                conf.setMaxErrorRetry(2);  // 失败后最大重试次数，默认2次
                OSSLog.enableLog();
                oss = new OSSClient(getActivity(), ud.getRegionName() + ".aliyuncs.com", credentialProvider, conf);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateVideo3(attachmentBean, ud);
                    }
                });
            }
        }).start(ThreadManager.getManager());
    }

    private void UpdateVideo3(final LineItem attachmentBean, final Uploadao ud) {

        String path = attachmentBean.getUrl();
        mfile = new File(path);
        fileName = mfile.getName();
        String name2 = AppConfig.UserID + mfile.getName();
        MD5Hash = Md5Tool.transformMD5(name2);

        PutObjectRequest put = new PutObjectRequest(ud.getBucketName(),
                MD5Hash, path);
        put.setCRC64(OSSRequest.CRC64Config.YES);

        //开始下载
        attachmentBean.setAttachmentID(-1 + "");
        attachmentBean.setFlag(1);

        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, final long currentSize, final long totalSize) {
                Log.e("biang", currentSize + ":" + totalSize);
                if (puo != null) {
                    puo.setProgress(totalSize, currentSize);
                }
            }
        });

        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                attachmentBean.setFlag(2);
                Log.e("biang", "onSuccess");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (puo != null) {
                            puo.DissmissPop();
                        }
                        startConverting(ud, attachmentBean);
                    }
                });
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                Log.e("biang", "onFailure");

            }
        });
    }

    private void startConverting(final Uploadao ud, final LineItem attachmentBean) {
        uploadao = ud;
        ServiceInterfaceTools.getinstance().startConverting(AppConfig.URL_LIVEDOC + "startConverting", ServiceInterfaceTools.STARTCONVERTING,
                uploadao, MD5Hash, fileName, targetFolderKey,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        Log.e("hhh", "startConvertingstartConverting");
                        convertingPercentage(attachmentBean);
                    }
                });
    }

    private void convertingPercentage(final LineItem attachmentBean) {
        puo = new Popupdate();
        puo.getPopwindow(getActivity(), attachmentBean.getFileName());
        puo.ChangeName("Converting");
        puo.StartPop(mTeamRecyclerView);

        timer1 = new Timer();
        timerTask1 = new TimerTask() {
            @Override
            public void run() {
                ServiceInterfaceTools.getinstance().queryConverting(AppConfig.URL_LIVEDOC + "queryConverting", ServiceInterfaceTools.QUERYCONVERTING,
                        uploadao, MD5Hash, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                Log.e("hhh", "queryConvertingqueryConverting");
                                uploadNewFile((ConvertingResult) object, attachmentBean);
                            }
                        });
            }
        };
        timer1.schedule(timerTask1, 100, 1000);
    }


    private void uploadNewFile(final ConvertingResult convertingResult, final LineItem attachmentBean) {
        if (convertingResult.getCurrentStatus() == 0) {  // prepare
            attachmentBean.setProgress(0);
        } else if (convertingResult.getCurrentStatus() == 1) { //Converting
            attachmentBean.setProgress(convertingResult.getFinishPercent());
        } else if (convertingResult.getCurrentStatus() == 5) { //Done
            attachmentBean.setProgress(convertingResult.getFinishPercent());
            if (timer1 != null) {
                timer1.cancel();
                timer1 = null;
            }
            if (timerTask1 != null) {
                timerTask1.cancel();
                timerTask1 = null;
            }
            ServiceInterfaceTools.getinstance().uploadSpaceNewFile(AppConfig.URL_PUBLIC + "SpaceAttachment/UploadNewFile",
                    ServiceInterfaceTools.UPLOADSPACENEWFILE,
                    fileName, spaceId, "", MD5Hash,
                    convertingResult, field, new ServiceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            Log.e("hhh", "SpaceAttachment/UploadNewFile");
                            Toast.makeText(getActivity(), "upload success", Toast.LENGTH_LONG).show();
                            if (puo != null) {
                                puo.DissmissPop();
                            }
                            EventBus.getDefault().post(new TeamSpaceBean());
                        }
                    }
            );
        } else if (convertingResult.getCurrentStatus() == 3) { // Failed
            if (timer1 != null) {
                timer1.cancel();
                timer1 = null;
            }
            if (timerTask1 != null) {
                timerTask1.cancel();
                timerTask1 = null;
            }
        }
        if (puo != null) {
            puo.setProgress(100, attachmentBean.getProgress());
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_add:
                AddDocument();
                break;
            case R.id.image_more_options:
                showMoreDialog();
                break;
            case R.id.teamrl:
                GoToSwitch();
                break;
            case R.id.layout_back:
//                finish();
                EventTeamFragment teamFragment = new EventTeamFragment();
                teamFragment.setType(1);
                EventBus.getDefault().post(teamFragment);
                break;
            case R.id.search_layout:
                Intent intent = new Intent(getActivity(), DocumentSearchActivity.class);
                intent.putExtra("space_id", spaceId);
                intent.putExtra("team_name", teamName);

                startActivity(intent);
                break;
        }

    }

    private void GoToSwitch() {
        Intent intent = new Intent(getActivity(), SwitchSpaceActivity.class);
        intent.putExtra("ItemID", spaceId);
        intent.putExtra("team_id", teamId);
        intent.putExtra("project_name", projectName);
        startActivityForResult(intent, REQUEST_CODE_CHANGESPACE);
    }

    private void showMoreDialog() {
        TeamMorePopup teamMorePopup = new TeamMorePopup();
        teamMorePopup.setIsTeam(false);
        teamMorePopup.setTSid(spaceId);
        if (!TextUtils.isEmpty(spaceName)) {
            teamMorePopup.setTName(spaceName);
        }
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
                DeleteSpace();
            }

            @Override
            public void rename() {
//                GotoRename();
                /*DialogRename dr = new DialogRename();
                dr.EditCancel(SpaceDocumentsActivity.this, itemID, false);*/
                Intent intent2 = new Intent(getActivity(), SpacePropertyActivity.class);
                intent2.putExtra("ItemID", spaceId);
                startActivity(intent2);
            }

            @Override
            public void quit() {
//                finish();
            }

            @Override
            public void edit() {
                Intent intent2 = new Intent(getActivity(), EditSpaceActivity.class);
                intent2.putExtra("space_id", spaceId);
                intent2.putExtra("space_name", spaceName);
                startActivityForResult(intent2, REQUEST_EDIT_SPACE);
            }
        });

        teamMorePopup.StartPop(moreOptionsImage);

    }

    private void GotoRename() {
        Intent intent = new Intent(getActivity(), RenameActivity.class);
        intent.putExtra("itemID", spaceId);
        intent.putExtra("isteam", false);
        startActivity(intent);
    }

    private void DeleteSpace() {
        LoginGet lg = new LoginGet();
        lg.setBeforeDeleteSpaceListener(new LoginGet.BeforeDeleteSpaceListener() {
            @Override
            public void getBDS(int retdata) {
                if (0 == retdata) {
                    MergeSpace(retdata);
                } else {
                    GetDeletePop();
                }
            }
        });
        lg.GetBeforeDeleteSpace(getActivity(), spaceId + "");
    }

    private void GetDeletePop() {
        LoginGet loginget = new LoginGet();
        loginget.setTeamSpaceGetListener(new LoginGet.TeamSpaceGetListener() {
            @Override
            public void getTS(ArrayList<Customer> list) {
                cuslist = new ArrayList<Customer>();
                cuslist.addAll(list);
                for (int i = 0; i < cuslist.size(); i++) {
                    Customer customer = cuslist.get(i);
                    ArrayList<Space> sl = customer.getSpaceList();
                    for (int j = 0; j < sl.size(); j++) {
                        Space sp = sl.get(j);
                        if (sp.getItemID() == spaceId) {
                            sl.remove(j);
                            break;
                        }
                    }
                }

                SpaceDeletePopup spaceDeletePopup = new SpaceDeletePopup();
                spaceDeletePopup.getPopwindow(getActivity());
                spaceDeletePopup.setSP(cuslist);
                spaceDeletePopup.setFavoritePoPListener(new SpaceDeletePopup.FavoritePoPListener() {
                    @Override
                    public void dismiss() {
                        getActivity().getWindow().getDecorView().setAlpha(1.0f);
                    }

                    @Override
                    public void open() {
                        getActivity().getWindow().getDecorView().setAlpha(0.5f);
                    }

                    @Override
                    public void delete(int spaceid) {
                        MergeSpace(spaceid);
                    }

                    @Override
                    public void refresh() {

                    }
                });
                spaceDeletePopup.StartPop(mTeamRecyclerView);

            }
        });
        loginget.GetTeamSpace(getActivity());
    }

    private void MergeSpace(int retdata) {
        int mergeSpaceID = retdata;
        if (0 == mergeSpaceID) {
            mergeSpaceID = 9999;
        }
        final int finalMergeSpaceID = mergeSpaceID;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "TeamSpace/DeleteSpace?spaceID=" +
                                    spaceId + "&mergeSpaceID=" +
                                    finalMergeSpaceID
                    );
                    Log.e("DeleteSpace", responsedata.toString());
                    int retcode = (Integer) responsedata.get("RetCode");
                    msg = new Message();
                    if (0 == retcode) {
                        msg.what = AppConfig.SUCCESS;
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

    DocChooseDialog dialog;

    private void AddDocument() {
        dialog = new DocChooseDialog(getActivity());
        dialog.setSelectedOptionListener(this);
        startRequestPermission();
    }
    private String[] permissions = new String[]{  Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private void startRequestPermission(){
        if (KloudPerssionManger.isPermissionCameraGranted(getActivity()) &&KloudPerssionManger.isPermissionReadExternalStorageGranted(getActivity())
                && KloudPerssionManger.isPermissionExternalStorageGranted(getActivity())) {
            dialog.show();
        }else {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_UPLOADFILE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiver(EventCamera eventCamera){
        if(dialog!=null){
            dialog.show();
        }
    }


    private void AddFavorite(final Document fa) {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "SpaceAttachment/UploadFromFavorite?spaceID=" + spaceId
                                    + "&itemIDs=" + fa.getItemID(), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        getSpaceList();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(TeamSpaceBean teamSpaceBean) {
//        flagr = true;
        getTeamItem();
    }

    @Subscribe
    public void refreshSync(EventSyncSucc eventSyncSucc) {
        getTeamItem();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventFinish(Customer customer) {
//        finish();
    }

    private boolean flagr;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECTED_IMAGE);
    }

    @Override
    public void selectFromDocs() {
        Intent intent = new Intent(getActivity(), FavoriteDocumentsActivity.class);
        intent.putExtra("space_id", spaceId);
        startActivityForResult(intent, REQUEST_SELECT_DOC);
    }

    @Override
    public void selectFromFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SELECTED_FILE);
    }

    /**
     * 拍照上传
     */
    @Override
    public void selectFromCamera() {
        boolean createSuccess = FileUtils.createFileSaveDir(getActivity());
        if (!createSuccess) {
            Toast.makeText(getActivity(), "文件系统异常，打开失败", Toast.LENGTH_SHORT).show();
        }else{
            openCameraForAddDoc();
        }
    }

    private File cameraFile;

    private void openCameraForAddDoc() {
        if (!isCameraCanUse()) {
            Toast.makeText(getActivity(), "相机不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String mFilePath = FileUtils.getBaseDir();
        // 文件名
        String fileName = "Kloud_" + DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";
        cameraFile = new File(mFilePath, fileName);
        //Android7.0文件保存方式改变了
        if (Build.VERSION.SDK_INT < 24) {
            Uri uri = Uri.fromFile(cameraFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, REQUEST_SELECTED_CAMERA);
    }

    private boolean isCameraCanUse() {
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return false;
        } else {
            return true;
        }
    }

    private void uploadFile(final LineItem attachmentBean, final int spaceId) {
        final JSONObject jsonobject = null;
        String url = null;
        File file = FileGetTool.GetFile(attachmentBean);
        String title = attachmentBean.getFileName();
        final String fileHash = Md5Tool.getMd5ByFile(file);
        if (file.exists()) {
            try {
                url = AppConfig.URL_PUBLIC + "SpaceAttachment/UploadFileWithHash?spaceID=" + spaceId + "&folderID=-1&Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") +
                        "&Hash=" + fileHash;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String finalUrl = url;
            uploadStart();
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject responsedata = ConnectService
                                .submitDataByJson(finalUrl, jsonobject);
                        Log.e("UploadFileWithHash", responsedata.toString() + "   " + finalUrl);
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {  //刷新
//                            msg.what = AppConfig.DELETESUCCESS;
                            addDocSucc();
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            JSONObject jsonObject = responsedata.getJSONObject("RetData");
                            String targetFolderKey = jsonObject.getString("Path");
                            int field = jsonObject.getInt("FileID");
//                            uploadFile2(attachmentBean);
                            DocumentUploadTool uploadTool = new DocumentUploadTool(getActivity());
                            uploadTool.setUploadDetailLinstener(SpaceDocumentsFragment.this);
                            uploadTool.uploadFileV2(getActivity(), targetFolderKey, field, attachmentBean, spaceId);
                        } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                            msg.what = AppConfig.FAILED;
                            final String ErrorMessage = responsedata
                                    .getString("ErrorMessage");
                            msg.obj = ErrorMessage;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), ErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start(ThreadManager.getManager());
        } else {
            Toast.makeText(getActivity(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void uploadStart() {
        uploadFileDialog = new UploadFileDialog(getActivity());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploadFileDialog.show();
            }
        });
    }

    @Override
    public void uploadFile(int progress) {

    }

    @Override
    public void convertFile(int progress) {

    }


    @Override
    public void uploadFinished(Object result) {
        addDocSucc();
    }

    @Override
    public void uploadError(String message) {

    }

    private void addDocSucc() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (uploadFileDialog != null) {
                    uploadFileDialog.cancel();
                }
                Toast.makeText(getActivity(), R.string.operate_success, Toast.LENGTH_SHORT).show();
                getTeamItem();
                EventBus.getDefault().post(new TeamSpaceBean());
            }
        });
    }

    private void updateSocket(){
        Intent service = new Intent(getActivity().getApplicationContext(), SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(service);
            getActivity().startService(service);
        } else {
            getActivity().startService(service);
        }
    }

    private void viewDocIfPermissionGranted(Document document){
        if(KloudPerssionManger.isPermissionExternalStorageGranted(getActivity())){
            requestDocumentDetail(document);
        }else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE_FOR_VIWE_DOC_IN_SPACE);
        }
    }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventViewDocAfterPermissionGranted(EventViewDocInSpacePermissionGranted permissionGranted){
        if(tempClickedDocument != null){
            requestDocumentDetail(tempClickedDocument);
            tempClickedDocument = null;
        }
    }

}
