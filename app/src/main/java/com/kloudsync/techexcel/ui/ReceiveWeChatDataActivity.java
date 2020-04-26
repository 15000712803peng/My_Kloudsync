package com.kloudsync.techexcel.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EventWxFilePath;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AddDocToSpaceDialog;
import com.kloudsync.techexcel.dialog.AddWxDocDialog;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.UploadFileDialog;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.personal.PersonalCollectionActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ReceiveWeChatDataActivity extends Activity implements AddDocToSpaceDialog.OnSpaceSelectedListener {

    private SharedPreferences sharedPreferences;
    public static ReceiveWeChatDataActivity instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadnull);
        Log.e("WeChatActivity", "on create");
        instance = this;
        getUri();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("WeChatActivity", "on new intent");
    }

    private void getUri() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        String filePath = "";
        if (uri == null) {
            Toast.makeText(getApplicationContext(), "data error ,open failed", Toast.LENGTH_SHORT).show();
        }
        if (uri != null) {
            filePath = FileUtils.getPath(this, uri);
            if (TextUtils.isEmpty(filePath) || !(new File(filePath).exists())) {
                Toast.makeText(getApplicationContext(), "data error ,open failed", Toast.LENGTH_SHORT).show();
            }
        }
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        final boolean isLogIn = sharedPreferences.getBoolean("isLogIn", false);
        final Intent lanchIntent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName());
        lanchIntent.putExtra("wechat_data_path", filePath);
        AppConfig.wechatFilePath=filePath;

        if (!isLogIn) {
            Log.e("check_dialog", "one  "+filePath);
            startActivity(lanchIntent);
            finish();
        } else {
            if (((App) getApplication()).getMainActivityInstance() == null) {
                Log.e("check_dialog", "two  "+filePath);
                startActivity(lanchIntent);
                finish();
            } else {
                Log.e("check_dialog", "three  "+ filePath);
//                EventWxFilePath path = new EventWxFilePath();
//                path.setPath(filePath);
//                EventBus.getDefault().post(path);
                if(DocAndMeetingActivity.instance!=null|| PersonalCollectionActivity.instance!=null){
                      finish();
                }else {
                    Log.e("check_dialog", "three2  "+ filePath);
                    isUploadweixinFile();
                }
            }
        }
    }


    private AddWxDocDialog addWxDocDialog;
    private AddDocToSpaceDialog addDocToSpaceDialog;
    private String filePath;

    private void isUploadweixinFile(){
        if (!TextUtils.isEmpty(AppConfig.wechatFilePath)) {
            if (addWxDocDialog != null) {
                addWxDocDialog.dismiss();
                addWxDocDialog = null;
            }
            addWxDocDialog = new AddWxDocDialog(this, AppConfig.wechatFilePath);
            addWxDocDialog.setSavedListener(new AddWxDocDialog.OnDocSavedListener() {
                @Override
                public void onSaveSpace(String path) {
                    AppConfig.wechatFilePath="";
                    filePath = path;
                    if (addDocToSpaceDialog != null) {
                        addDocToSpaceDialog.dismiss();
                    }
                    addDocToSpaceDialog = new AddDocToSpaceDialog(ReceiveWeChatDataActivity.this);
                    addDocToSpaceDialog.setOnSpaceSelectedListener(ReceiveWeChatDataActivity.this);
                    addDocToSpaceDialog.show();
                }

                @Override
                public void onSaveFavorite(String path) {
                    Intent intent = new Intent(ReceiveWeChatDataActivity.this, PersonalCollectionActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancel() {
                    AppConfig.wechatFilePath="";
                    finish();
                }
            });
            addWxDocDialog.show();
        }
    }

    private UploadFileDialog  uploadFileDialog;

    @Override
    public void onSpaceSelected(int spaceId) {
        AddDocumentTool.addDocumentToSpace(this, filePath, spaceId, new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog == null) {
                            uploadFileDialog = new UploadFileDialog(ReceiveWeChatDataActivity.this);
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
            public void uploadFile(final int progress) {  //上传进度更新
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
            public void convertFile(final int progress) { //转化进度更新
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
            public void uploadFinished(final Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                        EventBus.getDefault().post(new TeamSpaceBean());
                        //打开document
                        if(result!=null){
                            Document document= (Document) result;
                            requestDocumentDetail(document);
                        }else{
                            finish();
                        }
                    }
                });

            }

            @Override
            public void uploadError(String message) {  // 重复上传
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "add favorite success", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }



    //生成一个lessonId
    private void requestDocumentDetail(final Document  document) {
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
                            Intent intent = new Intent(ReceiveWeChatDataActivity.this, DocAndMeetingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("isStartCourse", true);
                            intent.putExtra("meeting_id", Integer.parseInt(document.getLessonId()) + "," + AppConfig.UserID);
                            intent.putExtra("document_id", document.getTempItemId());
                            intent.putExtra("meeting_type", 2);
                            intent.putExtra("lession_id", Integer.parseInt(document.getLessonId()));
                            startActivity(intent);
                            finish();
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("WeChatActivity", "on destroy");
    }


}
