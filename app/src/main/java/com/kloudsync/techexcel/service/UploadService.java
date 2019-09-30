package com.kloudsync.techexcel.service;


import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogFDadd;
import com.kloudsync.techexcel.help.DialogSDadd;
import com.kloudsync.techexcel.help.Popupdate;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DocumentUploadUtil;
import com.kloudsync.techexcel.tool.Jianbuderen;
import com.kloudsync.techexcel.tool.Md5Tool;
import com.kloudsync.techexcel.ui.MainActivity;
import com.kloudsync.techexcel.ui.ReceiveWeChatDataActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.bean.LineItem;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class UploadService extends Service {
    public static UploadService instance;
    private boolean flagad;

    private List<TeamSpaceBean> spacesList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showPop();
            }
        }, 1000);

    }


    private void showPop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
        builder.setTitle("")
                .setMessage(getString(R.string.ask_update_out))
                .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        /*if (PersonalCollectionActivity.instance != null && !PersonalCollectionActivity.instance.isFinishing()) {
                            PersonalCollectionActivity.instance.finish();
                        }
                        Intent intent = new Intent(UploadService.this, PersonalCollectionActivity.class);
                        intent.putExtra("path", AppConfig.OUTSIDE_PATH);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/
                        flagad = true;
                        ShowSelect();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.No),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.dismiss();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!flagad) {
                    Jianbuderen.Heihei();
                }
//                stopSelf();
            }
        });

//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                dialog.getWindow().setAttributes(layoutParams);
            }
        }
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void ShowSelect() {
        DialogFDadd dd = new DialogFDadd();
        dd.setDismissListener(new DialogFDadd.DialogDismissListener() {
            @Override
            public void AddDocument() {
                GetSpDuang();
            }
        });
        dd.EditCancel(MainActivity.instance);

    }

    private void GetSpDuang() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID="
                        + sharedPreferences.getInt("SchoolID", -1) + "&type=2&parentID="
                        + sharedPreferences.getInt("teamid", 0),
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        spacesList.clear();
                        spacesList.addAll(list);
                        AddBiuBiu();
                    }
                });
    }

    private int itemID;

    private void AddBiuBiu() {
        if (0 == spacesList.size()) {
            Toast.makeText(getApplicationContext(), "Please create space first. ┏(^ω^)=☞", Toast.LENGTH_LONG).show();
            Jianbuderen.Heihei();
            return;
        }
        DialogSDadd dadd = new DialogSDadd();
        dadd.setPoPDismissListener(new DialogSDadd.DialogDismissListener() {
            @Override
            public void PopSelect(TeamSpaceBean tsb) {
                itemID = tsb.getItemID();
                UploadFileWithHash();
            }
        });
        dadd.EditCancel(MainActivity.instance, spacesList);
    }

    private void UploadFileWithHash() {
        Log.e("UploadFileWithHash", "UploadFileWithHash" + ":" + AppConfig.OUTSIDE_PATH);
        final LineItem attachmentBean = new LineItem();
        final JSONObject jsonobject = null;
        String url = null;

        String path = AppConfig.OUTSIDE_PATH;
        String pathname = path.substring(path.lastIndexOf("/") + 1);
        File file = new File(AppConfig.OUTSIDE_PATH);
        attachmentBean.setUrl(path);
        attachmentBean.setFileName(pathname);
        if (file.exists()) {
            int lastSlash = 0;
            lastSlash = AppConfig.OUTSIDE_PATH.lastIndexOf("/");
            String title = AppConfig.OUTSIDE_PATH.substring(lastSlash + 1, AppConfig.OUTSIDE_PATH.length());
            try {
                url = AppConfig.URL_PUBLIC + "SpaceAttachment/UploadFileWithHash?spaceID=" + itemID + "&folderID=-1&Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") +
                        "&Hash=" +
                        Md5Tool.getMd5ByFile(file);
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
                        Log.e("UploadFileWithHash", responsedata.toString() + "   " + finalUrl);
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {  //刷新
//                            msg.what = AppConfig.DELETESUCCESS;
                            EventBus.getDefault().post(new TeamSpaceBean());
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            JSONObject jsonObject = responsedata.getJSONObject("RetData");
                            String targetFolderKey = jsonObject.getString("Path");
                            int field = jsonObject.getInt("FileID");
//                            uploadFile2();
                            DocumentUploadUtil duu = new DocumentUploadUtil();
                            duu.uploadFile2(MainActivity.instance, targetFolderKey, field, attachmentBean, MainActivity.instance.getCurrentFocus(), itemID);

                        } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                            JSONObject jsonObject = responsedata.getJSONObject("RetData");
                            msg.what = AppConfig.FAILED;
                            final String ErrorMessage = responsedata
                                    .getString("ErrorMessage");
                            msg.obj = ErrorMessage;

                            MainActivity.instance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        MainActivity.instance.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Jianbuderen.Heihei();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start(((App) getApplication()).getThreadMgr());
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String fileNamebase;
    private HttpHandler httpHandler;
    private Popupdate puo;

    public void uploadFile2() {
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);
        params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
        File file = new File(AppConfig.OUTSIDE_PATH);
        if (file.exists()) {
            int lastSlash = 0;
            lastSlash = AppConfig.OUTSIDE_PATH.lastIndexOf("/");
            String name = AppConfig.OUTSIDE_PATH.substring(lastSlash + 1, AppConfig.OUTSIDE_PATH.length());
            Log.e("filename----",
                    name + "      文件大小 " + file.length());
            params.addBodyParameter(name, file);
            String url = null;
            try {
                String baseurl = LoginGet.getBase64Password(name);
                fileNamebase = URLEncoder.encode(baseurl, "UTF-8");
                url = AppConfig.URL_PUBLIC + "SpaceAttachment/AddNewSpaceDocumentMultipart?Description=description&Hash=" + Md5Tool.getMd5ByFile(file) + "&spaceID=" + itemID + "&folderID=-1&Title=" + fileNamebase + "&Guid=" + Md5Tool.getUUID() + "&Total=1&Index=1";
                Log.e("URRRRRRRRRL", url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("url", url);
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            httpHandler = http.send(HttpRequest.HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            Log.e("iiiiiiiiii", "onStart");
                            puo = new Popupdate();
                            puo.setPopCancelListener(new Popupdate.PopCancelListener() {
                                @Override
                                public void Cancel() {
                                    if (httpHandler != null) {
                                        httpHandler.cancel();
                                        httpHandler = null;
                                    }
                                }
                            });
                            puo.setPoPDismissListener(new Popupdate.PopDismissListener() {
                                @Override
                                public void PopDismiss() {
                                }
                            });
                            if (null == ReceiveWeChatDataActivity.instance || ReceiveWeChatDataActivity.instance.isFinishing()) {
                                puo.getPopwindow(MainActivity.instance, "");
                                puo.StartPop(MainActivity.instance.getWindow().getDecorView());
                            } else {
                                puo.getPopwindow(ReceiveWeChatDataActivity.instance, "");
                                puo.StartPop(ReceiveWeChatDataActivity.instance.getWindow().getDecorView());
                            }
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {
                            Log.e("iiiiiiiiii", current + "");
                            if (puo != null) {
                                puo.setProgress(total, current);
                            }
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {   // converting
                            Log.e("iiiiiiiiii", "onSuccess  " + responseInfo.result);
                            EventBus.getDefault().post(new TeamSpaceBean());
                            if (puo != null) {
                                puo.DissmissPop();
                            }
                            Jianbuderen.Heihei();
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Log.e("iiiiiiiiii", "onFailure    " + msg);
                            Jianbuderen.Heihei();
                            if (puo != null) {
                                puo.DissmissPop();
                            }
                            Toast.makeText(MainActivity.instance,
                                    msg,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service onDestroy", "Service be killed! o(TヘTo)");
    }
}
