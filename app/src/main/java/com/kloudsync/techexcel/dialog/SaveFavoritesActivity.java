package com.kloudsync.techexcel.dialog;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.SendFileMessage;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.ub.kloudsync.activity.Document;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class SaveFavoritesActivity extends SwipeBackActivity {


    private PDFView pdf_view;
    private TextView tv_back;
    private TextView tv_save;
    private TextView tv_name;
    private RelativeLayout rl_update;


    private SendFileMessage sf;
    private boolean isSend;
    String url;
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            rl_update.setVisibility(View.GONE);

            switch (msg.what) {
                case AppConfig.SAVESUCCESS:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.Save_Success),
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),
                            result,
                            Toast.LENGTH_LONG).show();
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

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_favourites);

        InitializationSF();
        initView();
    }

    private void InitializationSF() {
        sf = (SendFileMessage) getIntent().getSerializableExtra("sendFileMessage");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_name = (TextView) findViewById(R.id.tv_name);
        pdf_view = (PDFView) findViewById(R.id.pdf_view);
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);

        tv_name.setText(sf.getFileName());
        ShowSave();

        GetShowUrl();
        doDownload();

        tv_back.setOnClickListener(new myOnClick());
        tv_save.setOnClickListener(new myOnClick());
    }

    private void ShowSave() {
        LoginGet lg = new LoginGet();
        lg.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Document> list) {
                for (int i = 0; i < list.size(); i++) {
                    if (sf.getAttachmentID().equals(list.get(i).getAttachmentID() + "")) {
                        isSend = true;
                        break;
                    }
                }
                tv_save.setVisibility(isSend ? View.GONE : View.VISIBLE);
            }
        });
        lg.MyFavoriteRequest(getApplicationContext(),0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Rect frame = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int notifiheight = frame.top;
                int pheight = (getResources().getDisplayMetrics().heightPixels
                        - DensityUtil.dp2px(getApplicationContext(), 50) - notifiheight + 5);

                FrameLayout.LayoutParams pdfparams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, pheight);
                pdf_view.setLayoutParams(pdfparams);
            }
        },100);
    }

    private void displayFromFile(File file) {
        pdf_view.setSwipeVertical(true);//pdf放大的时候，是否在屏幕的右上角生成小地图
        pdf_view.fromFile(file)   //设置pdf文件地址
                .enableSwipe(true)   //是否允许翻页，默认是允许翻
                .load();
    }

    public void doDownload() {



        final File file = new File(Environment.getExternalStorageDirectory(), "peertime/" + sf.getFileName());
        Log.e("hahaha", file.getPath() + ":" + file.exists() + ":" + url);
        if (file.exists()) {
            displayFromFile(file);
            return;
        }
        String path = file.getPath();
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(path);
        Callback.Cancelable cc = x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                Log.e("haha", result.length() + ":onSuccess");
                displayFromFile(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("haha", "onError");

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("haha", "onCancelled");

            }

            @Override
            public void onFinished() {
                Log.e("haha", "onFinished");

            }

            @Override
            public void onWaiting() {
                Log.e("haha", "onWaiting");

            }

            @Override
            public void onStarted() {
                Log.e("haha", "onStarted");

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.e("haha", "onLoading");

            }

        });
    }

    private void GetShowUrl() {
        url = sf.getFileDownloadURL();
        int s1 = url.lastIndexOf("_");
        int s2 = url.lastIndexOf(">");
        if (s1 != -1) {
            url = url.substring(0, s1) + url.substring(s2 + 1, url.length());
        }
    }


    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.tv_save:
                    SaveMyFavor();
                    break;

                default:
                    break;
            }
        }

    }

    private void SaveMyFavor() {
        rl_update.setVisibility(View.VISIBLE);
        final JSONObject jsonObject = format(sf);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC + "EventAttachment/SaveAsFavorite?attachmentID=" + sf.getAttachmentID(), jsonObject);
                    Log.e("SaveAsFavoriteresponse", responsedata.toString());
                    int retcode = (Integer) responsedata.get("RetCode");
                    msg = new Message();
                    if (0 == retcode) {
                        msg.what = AppConfig.SAVESUCCESS;
                        String result = responsedata.toString();
                        msg.obj = result;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
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

    private static JSONObject format(SendFileMessage sf) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("attachmentID", sf.getAttachmentID());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
