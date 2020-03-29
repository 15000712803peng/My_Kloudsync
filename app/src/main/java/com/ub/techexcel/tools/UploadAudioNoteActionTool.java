package com.ub.techexcel.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
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
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSyncSucc;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.Popupdate;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.bean.LineItem;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class UploadAudioNoteActionTool {

    static volatile UploadAudioNoteActionTool instance;
    private Context mContext;

    public UploadAudioNoteActionTool(Context context){
        mContext=context;
    }

    public static UploadAudioNoteActionTool getManager(Context context) {
        if (instance == null) {
            synchronized (UploadAudioNoteActionTool.class) {
                if (instance == null) {
                    instance = new UploadAudioNoteActionTool(context);
                }
            }
        }
        return instance;
    }

    private int soundtrackID;
    private MeetingConfig meetingConfig;


    public void uploadNoteActon(File file, int soundtrackID, final LinearLayout audiosyncll, MeetingConfig meetingConfig){
            this.soundtrackID=soundtrackID;
            this.meetingConfig=meetingConfig;
            final LineItem attachmentBean = new LineItem();
            attachmentBean.setUrl(file.getAbsolutePath()); // 文件的路径
            attachmentBean.setFileName(file.getName()); // 文件名
            LoginGet lg = new LoginGet();
            lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
                @Override
                public void getUD(Uploadao ud) {
                    if (1 == ud.getServiceProviderId()) {
                        uploadWithTransferUtility(attachmentBean, ud);
                    } else if (2 == ud.getServiceProviderId()) {
                        initOSS(attachmentBean, ud);
                    }
                }
            });
            lg.GetprepareUploading(mContext);
    }




    public void uploadWithTransferUtility(final LineItem attachmentBean, final Uploadao ud) {
        mfile = new File(attachmentBean.getUrl());
        fileName = mfile.getName();
        objectkey = "NoteControlAction" + "/" +soundtrackID+ "/channel_1.json";
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                        ud.getAccessKeyId(),
                        ud.getAccessKeySecret(),
                        ud.getSecurityToken());
                AmazonS3Client s3 = new AmazonS3Client(sessionCredentials);
                s3.setRegion(com.amazonaws.regions.Region.getRegion(ud.getRegionName()));
                com.amazonaws.services.s3.model.PutObjectRequest request = new com.amazonaws.services.s3.model.PutObjectRequest(ud.getBucketName(), objectkey, mfile);
                TransferManager tm = new TransferManager(s3);
                com.amazonaws.services.s3.model.ObjectMetadata objectMetadata=new com.amazonaws.services.s3.model.ObjectMetadata();
                objectMetadata.setContentType("application/json");
                request.setMetadata(objectMetadata);
                request.setCannedAcl(CannedAccessControlList.PublicRead);
                request.setGeneralProgressListener(new ProgressListener() {
                    @Override
                    public void progressChanged(final ProgressEvent progressEvent) {

                    }
                });
                Upload upload = tm.upload(request);
                try {
                    upload.waitForCompletion();
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,"笔记控制文件上传成功",Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
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
                oss = new OSSClient(mContext, ud.getRegionName() + ".aliyuncs.com", credentialProvider, conf);
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile3(attachmentBean, ud);
                    }
                });
            }
        }).start(ThreadManager.getManager());
    }

    private File mfile;
    private String objectkey;
    private String fileName;

    private void uploadFile3(final LineItem attachmentBean, final Uploadao ud) {
        Log.e("syncing---", attachmentBean.getUrl() + "   " + attachmentBean.getFileName());
        String path = attachmentBean.getUrl();
        mfile = new File(path);
        fileName = mfile.getName();
        objectkey = "NoteControlAction" + "/" +soundtrackID+ "/channel_1.json";
//        NoteControlAction/4016/channel_1.json
        Log.e("notename",objectkey+"  "+"  "+ud.getBucketName()+ " "+ud.getRegionName());
        String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record/";
        File recordDir = new File(recordDirectory);
        // 要保证目录存在，如果不存在则主动创建
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        // 创建断点上传请求，参数中给出断点记录文件的保存位置，需是一个文件夹的绝对路径
        ResumableUploadRequest request = new ResumableUploadRequest(ud.getBucketName(),
                objectkey, path, recordDirectory);
        //设置false,取消时，不删除断点记录文件，如果不进行设置，默认true，是会删除断点记录文件，下次再进行上传时会重新上传。
        request.setDeleteUploadOnCancelling(false);
        ObjectMetadata objectMetadata=new ObjectMetadata();
        objectMetadata.setContentType("application/json");
        request.setMetadata(objectMetadata);
        request.setPartSize(1 * 1024 * 1024);
        // 设置上传过程回调
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, final long currentSize, final long totalSize) {
            }
        });
        oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      Toast.makeText(mContext,"笔记控制文件上传成功",Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {

            }
        });

    }




}
