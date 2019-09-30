package com.kloudsync.techexcel.linshi;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogAddFavorite;
import com.kloudsync.techexcel.help.PopAlbums;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.Md5Tool;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.SyncRoomBean;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ViewAllDocumentActivity extends Activity implements View.OnClickListener {

    private RecyclerView recycleview;
    private RelativeLayout back;
    private int teamId;
    private SyncRoomBean syncRoomBean;
    private List<LineItem> items = new ArrayList<>();
    private ViewAllDocAdapter viewAllDocAdapter;
    private TextView addnewdoc;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewalldocumentactivity);
        initView();
        syncRoomBean = (SyncRoomBean) getIntent().getSerializableExtra("syncRoomBean");
        teamId = getIntent().getIntExtra("teamId", 0);
        getAllDocumentList();





    }



    class MyTask extends AsyncTask<Integer,Integer,String>{

        @Override
        protected String doInBackground(Integer... integers) {
            return null;
        }
    }

    private void initView() {
        recycleview = (RecyclerView) findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        back = (RelativeLayout) findViewById(R.id.back);
        viewAllDocAdapter = new ViewAllDocAdapter(this, items);
        recycleview.setAdapter(viewAllDocAdapter);
        addnewdoc = (TextView) findViewById(R.id.addnewdoc);
        addnewdoc.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    public void getAllDocumentList() {
        ServiceInterfaceTools.getinstance().getViewAllDocuments(syncRoomBean.getItemID(), ServiceInterfaceTools.GETVIEWALLDOCUMENTS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {


                List<LineItem> data = (List<LineItem>) object;
                items.clear();
                items.addAll(data);
                viewAllDocAdapter.notifyDataSetChanged();


            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.addnewdoc:
                addDocument();
                break;
        }
    }


    private void addDocument() {
        PopAlbums pa = new PopAlbums();
        pa.getPopwindow(getApplicationContext());
        pa.BulinBulin();
        pa.setPoPDismissListener(new PopAlbums.PopAlbumsDismissListener() {
            @Override
            public void PopDismiss(boolean isAdd) {
                DialogAddFavorite daf = new DialogAddFavorite();
                daf.setPoPDismissListener(new DialogAddFavorite.DialogDismissListener() {
                    @Override
                    public void DialogDismiss(final Document favorite) {
                        new ApiTask(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = new JSONObject();
                                    js.put("lessonID", syncRoomBean.getItemID());
                                    js.put("itemIDs", favorite.getItemID());
                                    JSONObject jsonObject = ConnectService.submitDataByJson
                                            (AppConfig.URL_PUBLIC + "TopicAttachment/UploadFromFavorite?TopicID=" + syncRoomBean.getItemID() + "&itemIDs=" + favorite.getItemID(), js);
                                    Log.e("save_file", js.toString() + "   " + jsonObject.toString());
                                    if (jsonObject.getInt("RetCode") == 0) {
                                        getAllDocumentList();
                                        isupdate=true;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start(ThreadManager.getManager());
                    }
                });
                daf.EditCancel(ViewAllDocumentActivity.this);
            }

            @Override
            public void PopDismissPhoto(boolean isAdd) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_ALBUM);
            }

            @Override
            public void PopBack() {
                getWindow().getDecorView().setAlpha(1.0f);
            }
        });
        pa.StartPop(addnewdoc);
        getWindow().getDecorView().setAlpha(0.5f);
    }

    public static final int REQUEST_CODE_CAPTURE_ALBUM = 0;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE_ALBUM && resultCode == Activity.RESULT_OK
                && data != null) {
            String path = FileUtils.getPath(this, data.getData());
            String pathname = path.substring(path.lastIndexOf("/") + 1);
            LineItem attachmentBean = new LineItem();
            attachmentBean.setUrl(path);
            attachmentBean.setFileName(pathname);
            UploadFileWithHash(attachmentBean);
        }
    }

    private boolean isupdate;

    private void UploadFileWithHash(final LineItem attachmentBean) {
        final JSONObject jsonobject = null;
        String url = null;
        File file = new File(attachmentBean.getUrl());
        String title = attachmentBean.getFileName();
        if (file.exists()) {
            try {
                url = AppConfig.URL_PUBLIC + "TopicAttachment/UploadFileWithHash?topicID=" + syncRoomBean.getItemID() + "&folderID=-1&Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") +
                        "&Description=&Hash=" +
                        Md5Tool.getMd5ByFile(file);
                Log.e("UploadFileWithHash", url + "");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();


            }
            final String finalUrl = url;
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject responsedata = ConnectService
                                .submitDataByJson(finalUrl, jsonobject);
                        String retcode = responsedata.getString("RetCode");
                        Log.e("UploadFileWithHash", responsedata.toString() + "");
                        Message msg = new Message();
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {  //刷新
                            getAllDocumentList();
                            isupdate = true;
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            uploadFile2(attachmentBean);
                        } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                            msg.what = AppConfig.FAILED;
                            final String ErrorMessage = responsedata
                                    .getString("ErrorMessage");
                            msg.obj = ErrorMessage;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ViewAllDocumentActivity.this, ErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start(ThreadManager.getManager());
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String fileNamebase;
    private HttpHandler httpHandler;

    public void uploadFile2(final LineItem attachmentBean) {
        String fileName = attachmentBean.getFileName();
        attachmentBean.setFileName(fileName.replace(" ", "_"));
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);
        params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
        File file = new File(attachmentBean.getUrl());
        if (file.exists()) {
            String name = attachmentBean.getFileName();
            Log.e("iiiiiiiiiifilename----",
                    name + "      文件大小 " + file.length());
            params.addBodyParameter(name, file);
            String url = null;
            try {
                String baseurl = LoginGet.getBase64Password(name);
                fileNamebase = URLEncoder.encode(baseurl, "UTF-8");
                url = AppConfig.URL_PUBLIC + "TopicAttachment/AddNewTopicDocumentMultipart?Description=description&folderID=-1&topicID=" + syncRoomBean.getItemID() +
                        "&Title=" + fileNamebase + "&Hash=" + Md5Tool.getMd5ByFile(file) + "&Guid=" + Md5Tool.getUUID() + "&Total=1&Index=1";
                Log.e("iiiiiiiiii", url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            httpHandler = http.send(HttpRequest.HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {
                            Log.e("iiiiiiiiii", total + "  " + current);
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {   // converting
                            Log.e("iiiiiiiiii", responseInfo.result);
                            try {
                                JSONObject jsonObject = new JSONObject(responseInfo.result);
                                if (jsonObject.getInt("RetCode") == 0) {
                                    getAllDocumentList();
                                    isupdate = true;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Toast.makeText(getApplicationContext(),
                                    msg,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isupdate) {
            EventBus.getDefault().post(new TeamSpaceBean());
        }
    }

}
