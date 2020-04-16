package com.kloudsync.techexcel.personal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.FavoriteAdapter;
import com.kloudsync.techexcel.bean.EventSyncSucc;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.UploadFileDialog;
import com.kloudsync.techexcel.filepicker.FileEntity;
import com.kloudsync.techexcel.filepicker.FilePickerActivity;
import com.kloudsync.techexcel.filepicker.PickerManager;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.help.AddFavoriteDialog;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogDeleteDocument;
import com.kloudsync.techexcel.help.EditFavoriteDocumentDialog;
import com.kloudsync.techexcel.help.FavoriteDocumentOperationsDialog;
import com.kloudsync.techexcel.help.FavoriteDocumentShareDialog;
import com.kloudsync.techexcel.help.PopAlbums;
import com.kloudsync.techexcel.help.PopDeleteFavorite;
import com.kloudsync.techexcel.help.PopShareKloudSync;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.FavoriteDocumentResponse;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.search.ui.FavoriteDocumentSearchActivity;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.kloudsync.techexcel.tool.DocumentUploadUtil;
import com.kloudsync.techexcel.tool.FileGetTool;
import com.kloudsync.techexcel.tool.Md5Tool;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalCollectionActivity extends Activity implements View.OnClickListener, FavoriteAdapter.OnMoreOptionsClickListener, FavoriteAdapter.OnItemClickListener, AddFavoriteDialog.SelectedOptionListener {

    private RelativeLayout backLayout;
    private RecyclerView rv_pc;
    private RelativeLayout rl_update;
    private LinearLayout lin_main;
    private ArrayList<Document> mlist = new ArrayList<Document>();
    private FavoriteAdapter fAdapter;
    public static PersonalCollectionActivity instance;
    private static final int REQUEST_CODE_CAPTURE_MEDIA = 2;
    private static final int REQUEST_CODE_CAPTURE_PHOTO = 3;
    private String outpath;
    ArrayList<String> ua = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private static int SchoolID;
    private String mPath;
    private String mTitle;
    private ServiceBean bean = new ServiceBean();
    private String name;
    private String Title;
    private File mfile;
    String targetFolderKey;
    int field;
    private TextView titleText;
    private RelativeLayout addFavoriteLayout;
    private LinearLayout searchLayout;
	private SoundtrackBean mTempClickedSoundtrackBean;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            rl_update.setVisibility(View.GONE);
            /*if (puo != null)
                puo.DissmissPop();*/

            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.DELETESUCCESS:
                    new CenterToast.Builder(PersonalCollectionActivity.this).setSuccess(true).setMessage("删除成功").create().show();
                    getData();
                    break;
                case AppConfig.Upload_NoExist:
                    UploadFile(mPath, mTitle);
                    break;
                case AppConfig.AddTempLesson:
//                    result = (String) msg.obj;
//                    ViewdoHaha(result);
                    GoToVIew((Document) msg.obj);
                    break;
                case AppConfig.LOAD_FINISH:
//                    GoToVIew();
                    break;
                case AppConfig.ConvertStatus:
                    double progress = (double) msg.obj;
                    Document favorite = mlist.get(mlist.size() - 1);
                    favorite.setFlag(2);
                    break;
                case AppConfig.AskResult:
                    result = (String) msg.obj;
                    TellmyCompany(result);
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();

                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    private void TellmyCompany(String token) {

        String url = null;
        try {
            url = AppConfig.URL_PUBLIC + "FavoriteAttachment/TransferOrConvertFile?Title="
                    + URLEncoder.encode(LoginGet.getBase64Password(Title), "UTF-8")
                    + "&Hash=" +
                    Md5Tool.getMd5ByFile(mfile)
                    + "&OssObjectName=temp/" + name;
            if (!TextUtil.isEmpty(token)) {
                url += "&Token=" + token;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String finalUrl = url;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService
                            .submitDataByJson(finalUrl, null);
                    Log.e("TransferOrConvertFile", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.DELETESUCCESS;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata
                                .getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                        HttpSend(AppConfig.DELETESUCCESS);
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    private void GoToVIew(Document lesson) {
        Intent intent = new Intent(this, DocAndMeetingActivity.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", lesson.getLessonId() + "," + AppConfig.UserID);
        intent.putExtra("isTeamspace", true);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", lesson.getLessonId());
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        intent.putExtra("document", lesson);
	    intent.putExtra(DocAndMeetingActivity.SUNDTRACKBEAN, mTempClickedSoundtrackBean);
//        Intent intent = new Intent(this, MeetingActivity.class);
//        intent.putExtra("host_id",AppConfig.UserID);
//        intent.putExtra("meeting_id", lesson.getLessonId() + "," + AppConfig.UserID);
//        intent.putExtra("meeting_role", 2);
        startActivity(intent);
	    mTempClickedSoundtrackBean = null;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_collection);
        instance = this;
        outpath = getIntent().getStringExtra("path");
//        initOSS();
        getSchoolID();
        initView();
        getData();
        EventBus.getDefault().register(this);
//        StartTimer();
    }


    private void getSchoolID() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        SchoolID = sharedPreferences.getInt("SchoolID", -1);
        if (-1 == SchoolID) {
            SchoolID = AppConfig.SchoolID;
        }
    }


    private void getData() {
        getFavoriteDocuments();

    }

    private void getFavoriteDocuments() {
        ServiceInterfaceTools.getinstance().getFavoriteDocuments().enqueue(new Callback<NetworkResponse<FavoriteDocumentResponse>>() {
            @Override
            public void onResponse(Call<NetworkResponse<FavoriteDocumentResponse>> call, Response<NetworkResponse<FavoriteDocumentResponse>> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                        List<Document> favoriteList = response.body().getRetData().getList();
                        if (favoriteList == null) {
                            favoriteList = new ArrayList<>();
                        }
                        fAdapter.UpdateRV(favoriteList);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.operate_failure, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.operate_failure, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NetworkResponse<FavoriteDocumentResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.operate_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
        lin_main = (LinearLayout) findViewById(R.id.lin_main);
        rv_pc = (RecyclerView) findViewById(R.id.rv_pc);
        backLayout.setOnClickListener(this);
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText(R.string.saved);
        searchLayout = findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
        addFavoriteLayout = findViewById(R.id.layout_title_right);
        addFavoriteLayout.setVisibility(View.VISIBLE);
        addFavoriteLayout.setOnClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_pc.setLayoutManager(manager);
//        rv_pc.addItemDecoration(new RecyclerViewDivider(
//                PersonalCollectionActivity.this, LinearLayout.HORIZONTAL,
//                DensityUtil.dp2px(PersonalCollectionActivity.this, 1), getResources().getColor(R.color.lightgrey)));
        fAdapter = new FavoriteAdapter(PersonalCollectionActivity.this);
        fAdapter.setOnMoreOptionsClickListener(this);
        fAdapter.setOnItemClickListener(this);

        fAdapter.setDeleteItemClickListener(new FavoriteAdapter.DeleteItemClickListener() {
            @Override
            public void AddTempLesson(int position) {
                Document fa = mlist.get(position);
                GetTempLesson(fa);
            }

            @Override
            public void deleteClick(View view, int position) {
                DeleteFav(view, position);
            }

            @Override
            public void shareLesson(Document lesson, int id) {
                ShareKloudSync(lesson, id);
            }
        });
        fAdapter.setOnItemLectureListener(new FavoriteAdapter.OnItemLectureListener() {
            @Override
            public void onItem(Document favorite, View view) {

            }

            @Override

            public void onRealItem(Document favorite, View view) {

            }

            @Override
            public void share(int s, Document favorite) {
                shareDocumentDialog(favorite, s);
            }

            @Override
            public void open() {

            }

            @Override
            public void dismiss() {

            }

            @Override
            public void deleteRefresh() {
	            runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
			            new CenterToast.Builder(PersonalCollectionActivity.this).setSuccess(true).setMessage(getResources().getString(R.string.operate_success)).create().show();
			            getData();
		            }
	            });

            }

	        @Override
	        public void playDocSoundTrackItem(Document favorite, SoundtrackBean soundtrackBean) {
		        mTempClickedSoundtrackBean = soundtrackBean;
		        GetTempLesson(favorite);
	        }
        });
        rv_pc.setAdapter(fAdapter);
    }

    private void shareDocumentDialog(final Document document, final int id) {
        final PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(this, document, id);
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



    private void ShareKloudSync(final Document document, final int id) {
        final PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(PersonalCollectionActivity.this, document, id);
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

    private void GetTempLesson(final Document document) {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "Lesson/AddTempLessonWithOriginalDocument?attachmentID=" + document.getAttachmentID()
                                    + "&Title=" + URLEncoder.encode(LoginGet.getBase64Password(document.getTitle()), "UTF-8"), jsonObject);
                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddTempLesson;
                        JSONObject RetData = responsedata.getJSONObject("RetData");
                        document.setLessonId(RetData.getString("LessonID"));
                        msg.obj = document;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }


                    handler.sendMessage(msg);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    private void DeleteFav(View view, int position) {
        PopDeleteFavorite pdf = new PopDeleteFavorite();
        final Document fav = mlist.get(position);
        pdf.getPopwindow(getApplicationContext(), fav);
        pdf.setPoPDismissListener(new PopDeleteFavorite.PopUpdateOutDismissListener() {
            @Override
            public void PopDismiss(boolean isDelete) {
                if (isDelete) {
                    deleteFavorite(fav);
                }
                BackChange(1.0f);
            }
        });
        pdf.StartPop(view);
        BackChange(0.5f);
    }

    public void BackChange(float value) {
        lin_main.animate().alpha(value);
        lin_main.animate().setDuration(500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.layout_title_right:
                addDocument();
                break;
            case R.id.search_layout:

                Intent intent = new Intent(this, FavoriteDocumentSearchActivity.class);
                startActivity(intent);

                break;
            default:
                break;
        }
    }

    AddFavoriteDialog dialog;

    private void addDocument() {
        dialog = new AddFavoriteDialog(this);
        dialog.setSelectedOptionListener(this);
        dialog.show();
    }


    // 添加文件
    private void AddAlbum() {
        PopAlbums pa = new PopAlbums();
        pa.getPopwindow(getApplicationContext());
        pa.setPoPDismissListener(new PopAlbums.PopAlbumsDismissListener() {
            @Override
            public void PopDismiss(boolean isAdd) {
                if (isAdd) {
                    GetVideo();
                }
            }

            @Override
            public void PopDismissPhoto(boolean isAdd) {
                if (isAdd) {
                    GetPhoto();
                }
            }

            @Override
            public void PopBack() {
                BackChange(1.0f);
            }
        });
//        pa.StartPop(img_add);
        BackChange(0.5f);

    }

    private void GetVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_MEDIA);
    }

    private void GetPhoto() {
        /*Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_PHOTO);
    }


    private void FinishActivityanim() {
        finish();
    }

    UploadFileDialog uploadFileDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECTED_IMAGE && data != null) {
                String path = FileUtils.getPath(this, data.getData());
                String title = path.substring(path.lastIndexOf("/") + 1);
//                uploadFavoirte(path, title);
                uploadfile(path);
            }else if(requestCode==REQUEST_SELECTED_FILE){

                List<FileEntity>  fff = PickerManager.getInstance().files;
                for (int i = 0; i < fff.size(); i++) {
                    FileEntity fileEntity=fff.get(0);
                    String path=fileEntity.getPath();
                    Log.e("buildversion",path+"");
                    if(!TextUtils.isEmpty(path)){
                        String suff=getSuffix(path);
                        if(suff.equals("mp3")){
                            uploadMp3file(path);
                        }else{
                            uploadfile(path);
                        }
                    }
                }
            }
        }
    }



    private String getSuffix(String fileName){
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }


    private void  uploadfile(String path){
        AddDocumentTool.addDocumentToFavorite(this, path, new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFileDialog = new UploadFileDialog(PersonalCollectionActivity.this);
                        uploadFileDialog.setTile("uploading");
                        uploadFileDialog.show();

                    }
                });
            }

            @Override
            public void uploadFile(final int progress) {
                runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
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
                getData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                        new CenterToast.Builder(getApplicationContext()).setSuccess(true).setMessage(getResources().getString(R.string.create_success)).create().show();

                    }
                }, 600);

            }

            @Override
            public void uploadError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                        Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void  uploadMp3file(String path){
        AddDocumentTool.addDocumentToFavoriteMp3(this, path,0, new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFileDialog = new UploadFileDialog(PersonalCollectionActivity.this);
                        uploadFileDialog.setTile("uploading");
                        uploadFileDialog.show();

                    }
                });
            }

            @Override
            public void uploadFile(final int progress) {
                runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
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
                getData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                        new CenterToast.Builder(getApplicationContext()).setSuccess(true).setMessage(getResources().getString(R.string.create_success)).create().show();
                    }
                }, 600);

            }

            @Override
            public void uploadError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                        Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }




    private void UploadFile(String path, String title) {
        final Document favorite = new Document();
        favorite.setFlag(1);
        favorite.setTitle(title);
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);

//        params.addBodyParameter("Content-Type", "video/mpeg4");// 设定传送的内容类型
        params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
        // params.setContentType("application/octet-stream");
        File file = new File(path);
        if (file.exists()) {
//            rl_update.setVisibility(View.VISIBLE);
            params.addBodyParameter(title, file);
            String url = null;
            try {
                url = AppConfig.URL_PUBLIC + "FavoriteAttachment/AddNewFavorite?Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") +
                        /*"&schoolID=" +
                        SchoolID
                        +*/ "&Hash=" + Md5Tool.getMd5ByFile(file);
                Log.e("hahaha", url + ":" + title);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.e("url", url);
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            final HttpHandler hh = http.send(HttpRequest.HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            mlist.add(favorite);
                            fAdapter.UpdateRV(mlist);
//                            puo.StartPop(img_add);
                        }

                        @Override
                        public void onLoading(final long total, final long current,
                                              boolean isUploading) {
//                            puo.setProgress(total, current);
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(responseInfo.result);
                                Log.e("hahaha", jsonObject.toString() + "");
                                int RetCode = jsonObject.getInt("RetCode");
                                if (0 == RetCode) {
                                    JSONObject js = jsonObject.getJSONObject("RetData");
                                    if (js.getInt("Status") == 10) { // 上传成功  开始转换
                                        int attachmentid = js.getInt("AttachmentID");
                                        favorite.setAttachmentID(attachmentid + "");
                                        favorite.setFlag(2);
                                        favorite.setProgress(0);
                                        ua.add(attachmentid + "");
                                    } else {
                                        HttpSend(AppConfig.DELETESUCCESS);
                                    }
                                } else {
                                    String ErrorMessage = jsonObject.getString("ErrorMessage");
                                    Toast.makeText(PersonalCollectionActivity.this, ErrorMessage, Toast.LENGTH_SHORT).show();
                                    HttpSend(AppConfig.DELETESUCCESS);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException error, String msgs) {
                            Log.e("error", msgs.toString());

                            Message msg = new Message();
                            msg.what = AppConfig.FAILED;
                            msg.obj = msgs.toString();
                            handler.sendMessage(msg);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }


    private void HttpSend(int msgwhat) {
        handler.sendEmptyMessage(msgwhat);
    }


    @Subscribe
    public void refreshSync(EventSyncSucc eventSyncSucc) {
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onMoreOptionsClick(final Document document) {
        FavoriteDocumentOperationsDialog pd = new FavoriteDocumentOperationsDialog();
        pd.getPopwindow(this, document);
        pd.setPoPMoreListener(new FavoriteDocumentOperationsDialog.PopDocumentListener() {
            boolean flags;

            @Override
            public void PopView() {
//                                        getTempLesson(lesson);
//                                        GoToVIew(lesson);
            }

            @Override
            public void PopDelete() {
                deleteDocument(document);
            }

            @Override
            public void PopEdit() {
                editDocument(document);
            }

            @Override
            public void PopShare() {
                shareFavorite(document);
            }

            @Override
            public void PopMove() {

            }

            @Override
            public void PopBack() {

            }
        });
        pd.show();
    }

    private void editDocument(Document lesson) {
        EditFavoriteDocumentDialog ped = new EditFavoriteDocumentDialog();
        ped.setPopEditDocumentListener(new EditFavoriteDocumentDialog.PopEditDocumentListener() {
            @Override
            public void popEditSuccess() {
                getData();
            }
        });
        ped.getPopwindow(this, lesson);
        ped.StartPop();
    }

    private void deleteDocument(final Document document) {
        DialogDeleteDocument ddd = new DialogDeleteDocument();
        ddd.setDelDocListener(new DialogDeleteDocument.DialogDelDocListener() {
            @Override
            public void delDoc() {
                deleteFavorite(document);
            }
        });
        ddd.EditCancel(this);
    }

    private void deleteFavorite(final Document fav) {
        rl_update.setVisibility(View.VISIBLE);


        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();

                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "FavoriteAttachment/RemoveFavorite?" +
                                    "itemIDs=" +
                                    fav.getItemID()
                                    /*+
                                    "&schoolID=" +
                                    SchoolID*/);
                    Log.e("Removeresponse", responsedata.toString());
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

    private void shareFavorite(final Document lesson) {
        final FavoriteDocumentShareDialog dialog = new FavoriteDocumentShareDialog();
        dialog.getPopwindow(this, lesson, -1);
        dialog.startPop();
    }

    private static final int REQUEST_SELECTED_IMAGE = 1;
    private static final int REQUEST_SELECTED_FILE = 4;

    @Override
    public void onItemClick(Document document) {
        GetTempLesson(document);
    }

    @Override
    public void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECTED_IMAGE);

    }


    @Override
    public void selectFromDocs() {
        Intent intent = new Intent(this, FilePickerActivity.class);
        intent.putExtra("fileType",1);
        startActivityForResult(intent,REQUEST_SELECTED_FILE);

    }


}
