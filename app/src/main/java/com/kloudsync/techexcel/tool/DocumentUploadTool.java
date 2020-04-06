package com.kloudsync.techexcel.tool;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
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
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kloudsync.techexcel.adapter.FavoriteAdapter;
import com.kloudsync.techexcel.bean.NoteId;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.Popupdate;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.ConvertingResult;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DocumentUploadTool {
    private Context mContext;
    private String targetFolderKey;
    private int field;
    private Uploadao uploadao = new Uploadao();
    private int spaceID;
    private File mfile;
    private String MD5Hash;
    private String fileHash;
    private String fileName;
    private Timer timer1;
    private TimerTask timerTask1;
    private Popupdate puo;
    private int type;
    private static UpdateGetListener updateGetListener;

    public interface UpdateGetListener {
        void Update();
    }

    public DocumentUploadTool(Context context) {

    }

    public void setUpdateGetListener(UpdateGetListener updateGetListener) {
        this.updateGetListener = updateGetListener;
    }

    public interface DocUploadDetailLinstener {
        void uploadStart();

        void uploadFile(int progress);

        void convertFile(int progress);

        void uploadFinished(Object result);

        void uploadError(String message);
    }


    private DocUploadDetailLinstener uploadDetailLinstener;


    public void setUploadDetailLinstener(DocUploadDetailLinstener uploadDetailLinstener) {
        this.uploadDetailLinstener = uploadDetailLinstener;
    }


    /**
     * Document上传
     *
     * @param context
     * @param targetFolderKey1
     * @param field1
     * @param attachmentBean
     * @param spaceID
     */
    public void uploadFile2(final Context context, String targetFolderKey1, int field1, final LineItem attachmentBean,
                            int spaceID, String fileHash) {
        this.mContext = context;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.spaceID = spaceID;
        this.MD5Hash = fileHash;
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

    public void uploadFileV2(final Context context, String targetFolderKey1, int field1, final LineItem attachmentBean,
                             int spaceID) {
        this.mContext = context;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.spaceID = spaceID;
        this.MD5Hash = Md5Tool.transformMD5(AppConfig.UserID + mfile.getName())+ System.currentTimeMillis();
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


    public void uploadFileV2(final Context context, String targetFolderKey1, int field1,
                             File file, int spaceID) {
        this.mContext = context;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.spaceID = spaceID;
        this.mfile = file;
        fileHash = Md5Tool.getMd5ByFile(mfile);
        this.type = 0;
        this.fileName = file.getName();
        this.MD5Hash = Md5Tool.transformMD5(AppConfig.UserID + mfile.getName())+ System.currentTimeMillis();
        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(ud);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(ud);
                }
            }
        });
        lg.GetprepareUploading(mContext);
    }

    private String lessionId;

    public void uploadFile(final Context context, String targetFolderKey1, int field1,
                           File file, String lessionId, int type) {
        this.mContext = context;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.lessionId = lessionId;
        this.mfile = file;
        fileHash = Md5Tool.getMd5ByFile(mfile);
        this.type = type;
        this.fileName = file.getName();
        this.MD5Hash = Md5Tool.transformMD5(AppConfig.UserID + mfile.getName())+ System.currentTimeMillis();
        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(ud);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(ud);
                }
            }
        });
        lg.GetprepareUploading(mContext);
    }


    /**
     * Favorite上传
     *
     * @param context
     * @param targetFolderKey1
     * @param field1
     */
    public void uploadFileFavorite2(final Context context, String targetFolderKey1, int field1, File file) {
        this.mContext = context;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.mfile = file;
        type = 1;
        this.MD5Hash = Md5Tool.transformMD5(AppConfig.UserID + mfile.getName())+ System.currentTimeMillis();
        fileHash = Md5Tool.getMd5ByFile(mfile);
        this.fileName = file.getName();
        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(ud);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(ud);
                }
            }
        });
        lg.GetprepareUploading(mContext);
    }

    public void uploadWithTransferUtility(final LineItem attachmentBean, final Uploadao ud) {
        mfile = new File(attachmentBean.getUrl());
        fileHash = Md5Tool.getMd5ByFile(mfile);
        fileName = mfile.getName();
        String name2 = AppConfig.UserID + mfile.getName();
        MD5Hash = Md5Tool.transformMD5(name2);
        new ApiTask(new Runnable() {
            @Override
            public void run() {

                BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                        ud.getAccessKeyId(),
                        ud.getAccessKeySecret(),
                        ud.getSecurityToken());
                AmazonS3Client s3 = new AmazonS3Client(sessionCredentials);
                s3.setRegion(com.amazonaws.regions.Region.getRegion(ud.getRegionName()));

                PutObjectRequest request = new PutObjectRequest(ud.getBucketName(), MD5Hash, mfile);

                TransferManager tm = new TransferManager(s3);
                request.setCannedAcl(CannedAccessControlList.PublicRead);
                request.setGeneralProgressListener(new ProgressListener() {
                    @Override
                    public void progressChanged(final ProgressEvent progressEvent) {
                        Log.e("Transferred", mfile.length() + " : " + progressEvent.getBytesTransferred());
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (uploadDetailLinstener != null) {
                                    uploadDetailLinstener.uploadFile((int) (progressEvent.getBytesTransferred() * 100 / mfile.length()));
                                }
                            }
                        });

                    }
                });

                Upload upload = tm.upload(request);
                Log.e("Transferred", "upload");

                // Optionally, you can wait for the upload to finish before continuing.
                try {
                    upload.waitForCompletion();
                    Log.e("Transferred", "Completion");
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            attachmentBean.setFlag(2);
                            startConverting(ud, attachmentBean);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start(ThreadManager.getManager());


    }


    public void uploadWithTransferUtility(final Uploadao ud) {
        fileName = mfile.getName();
        String name2 = AppConfig.UserID + mfile.getName();
        MD5Hash = Md5Tool.transformMD5(name2);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                        ud.getAccessKeyId(),
                        ud.getAccessKeySecret(),
                        ud.getSecurityToken());
                AmazonS3Client s3 = new AmazonS3Client(sessionCredentials);
                s3.setRegion(com.amazonaws.regions.Region.getRegion(ud.getRegionName()));

                PutObjectRequest request = new PutObjectRequest(ud.getBucketName(), MD5Hash, mfile);

                TransferManager tm = new TransferManager(s3);
                request.setCannedAcl(CannedAccessControlList.PublicRead);
                request.setGeneralProgressListener(new ProgressListener() {
                    @Override
                    public void progressChanged(final ProgressEvent progressEvent) {
                        Log.e("Transferred", mfile.length() + " : " + progressEvent.getBytesTransferred());
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (uploadDetailLinstener != null) {
                                    uploadDetailLinstener.uploadFile((int) (progressEvent.getBytesTransferred() * 100 / mfile.length()));
                                }
                            }
                        });

                    }
                });

                Upload upload = tm.upload(request);
                Log.e("Transferred", "upload");

                // Optionally, you can wait for the upload to finish before continuing.
                try {
                    upload.waitForCompletion();
                    Log.e("Transferred", "Completion");
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startConverting(ud);
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
                UpdateVideo3(attachmentBean, ud);
            }
        }).start(ThreadManager.getManager());
    }

    private void initOSS(final Uploadao ud) {
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
                String endpoint = ud.getRegionName() + ".aliyuncs.com";
                oss = new OSSClient(mContext, endpoint, credentialProvider, conf);
                UpdateVideo3(ud);
            }
        }).start(ThreadManager.getManager());
    }

    private void UpdateVideo3(final LineItem attachmentBean, final Uploadao ud) {

        String path = attachmentBean.getUrl();
        mfile = new File(path);
        fileName = mfile.getName();
        String name2 = AppConfig.UserID + mfile.getName();
        fileHash = Md5Tool.getMd5ByFile(mfile);
        MD5Hash = Md5Tool.transformMD5(name2);
       /* PutObjectRequest put = new PutObjectRequest(ud.getBucketName(),
                MD5Hash, path);
        put.setCRC64(OSSRequest.CRC64Config.YES);*/
        //开始下载
        attachmentBean.setAttachmentID(-1 + "");
        attachmentBean.setFlag(1);
        String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record/";
        File recordDir = new File(recordDirectory);
        // 要保证目录存在，如果不存在则主动创建
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        // 创建断点上传请求，参数中给出断点记录文件的保存位置，需是一个文件夹的绝对路径
        ResumableUploadRequest request = new ResumableUploadRequest(ud.getBucketName(),
                MD5Hash, path, recordDirectory);
        //设置false,取消时，不删除断点记录文件，如果不进行设置，默认true，是会删除断点记录文件，下次再进行上传时会重新上传。
        request.setDeleteUploadOnCancelling(false);
        request.setPartSize(1 * 1024 * 1024);
        // 设置上传过程回调
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, final long currentSize, final long totalSize) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = (int) (currentSize * 100 / totalSize);

                        if (uploadDetailLinstener != null) {
                            uploadDetailLinstener.uploadFile(progress);
                        }
                    }
                });
            }
        });


        oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startConverting(ud, attachmentBean);
                    }
                });
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
                Log.e("biang", "onFailure");
            }
        });

    }

    private void UpdateVideo3(final Uploadao ud) {
//        MD5Hash = Md5Tool.transformMD5(name2);
       /* PutObjectRequest put = new PutObjectRequest(ud.getBucketName(),
                MD5Hash, path);
        put.setCRC64(OSSRequest.CRC64Config.YES);*/
        //开始下载

        String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record/";
        File recordDir = new File(recordDirectory);
        // 要保证目录存在，如果不存在则主动创建
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }

        // 创建断点上传请求，参数中给出断点记录文件的保存位置，需是一个文件夹的绝对路径
        ResumableUploadRequest request = new ResumableUploadRequest(ud.getBucketName(), MD5Hash, mfile.getAbsolutePath(), recordDirectory);
        //设置false,取消时，不删除断点记录文件，如果不进行设置，默认true，是会删除断点记录文件，下次再进行上传时会重新上传。
        request.setDeleteUploadOnCancelling(false);
        request.setPartSize(1 * 1024 * 1024);
//        request.setObjectKey(mfile.getName());
        // 设置上传过程回调
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, final long currentSize, final long totalSize) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = (int) (currentSize * 100 / totalSize);

                        if (uploadDetailLinstener != null) {
                            uploadDetailLinstener.uploadFile(progress);
                        }
                    }
                });
            }
        });

        oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startConverting(ud);
                    }
                });
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
                Log.e("biang", "onFailure");
                if (uploadDetailLinstener != null) {
                    uploadDetailLinstener.uploadError("service exception");
                }
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

    private void startConverting(final Uploadao ud) {
        uploadao = ud;
        ServiceInterfaceTools.getinstance().startConverting(AppConfig.URL_LIVEDOC + "startConverting", ServiceInterfaceTools.STARTCONVERTING,
                uploadao, MD5Hash, fileName, targetFolderKey,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        Log.e("hhh", "startConvertingstartConverting");
                        convertingPercentage();
                    }
                });
    }

    private void convertingPercentage(final LineItem attachmentBean) {

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

    private void convertingPercentage() {

        timer1 = new Timer();
        timerTask1 = new TimerTask() {
            @Override
            public void run() {
                ServiceInterfaceTools.getinstance().queryConverting(AppConfig.URL_LIVEDOC + "queryConverting", ServiceInterfaceTools.QUERYCONVERTING,
                        uploadao, MD5Hash, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                Log.e("hhh", "queryConvertingqueryConverting");
                                uploadNewFile((ConvertingResult) object);
                            }
                        });
            }
        };
        timer1.schedule(timerTask1, 100, 1000);
    }

    private void uploadNewFile(final ConvertingResult convertingResult,
                               final LineItem attachmentBean) {
        Log.e("addLocalNote", "step five  uploadNewFile,type:" + type);
        if (convertingResult.getCurrentStatus() == 0) {  // prepare
            attachmentBean.setProgress(0);
            if (uploadDetailLinstener != null) {
                uploadDetailLinstener.convertFile(0);
            }
        } else if (convertingResult.getCurrentStatus() == 1) { //Converting
            attachmentBean.setProgress(convertingResult.getFinishPercent());
            if (uploadDetailLinstener != null) {
                uploadDetailLinstener.convertFile(convertingResult.getFinishPercent());
            }

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
            if (type != 1) {
                if (type == 10) {
                    Log.e("addLocalNote", "step six type:" + type);
                    ServiceInterfaceTools.getinstance().uploadLocalNoteFile(AppConfig.URL_PUBLIC + "DocumentNote/UploadNewFile", ServiceInterfaceTools.UPLOADFAVORITENEWFILE, fileName, "",
                            fileHash, convertingResult, field, this.documentId, this.pageIndex, this.noteId, this.syncroomId, this.noteLinkProperty, new ServiceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {
                                    if (object != null) {
                                        JSONObject response = (JSONObject) object;
                                        try {
                                            if (response.getInt("RetCode") == 0) {
                                                NoteId noteId = new NoteId();
                                                noteId.setLinkID(response.getInt("RetData"));
                                                Log.e("EventBus", "post noteId:" + noteId.getLinkID());
                                                EventBus.getDefault().post(noteId);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                } else if (type == 3) {
                    ServiceInterfaceTools.getinstance().uploadNewFile(AppConfig.URL_PUBLIC + "EventAttachment/UploadNewFile", ServiceInterfaceTools.UPLOADNEWFILE,
                            fileName, uploadao, lessionId, fileHash, convertingResult, true, field, new ServiceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {
                                    Log.e("UploadNewFile", "object:" + object);
                                    if (uploadDetailLinstener != null) {
                                        uploadDetailLinstener.uploadFinished(object);
                                    }

                                }
                            }
                    );
                } else {
                    ServiceInterfaceTools.getinstance().uploadSpaceNewFile(AppConfig.URL_PUBLIC + "SpaceAttachment/UploadNewFile",
                            ServiceInterfaceTools.UPLOADSPACENEWFILE,
                            fileName, spaceID, "", fileHash,
                            convertingResult, field, new ServiceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {
                                    Log.e("hhh", "SpaceAttachment/UploadNewFile");
                                    Toast.makeText(mContext, "upload success", Toast.LENGTH_LONG).show();
                                    EventBus.getDefault().post(new TeamSpaceBean());
                                }
                            }
                    );
                }

            } else {

                ServiceInterfaceTools.getinstance().uploadFavoriteNewFile(AppConfig.URL_PUBLIC + "FavoriteAttachment/UploadNewFile",
                        ServiceInterfaceTools.UPLOADFAVORITENEWFILE,
                        fileName, "", fileHash,
                        convertingResult, field, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                Log.e("hhh", "FavoriteAttachment/UploadNewFile");
//                                Toast.makeText(mActivity, "upload success", Toast.LENGTH_LONG).show();
//                                updateGetListener.Update();
                                if (uploadDetailLinstener != null) {
                                    uploadDetailLinstener.uploadFinished(object);
                                }
                            }
                        }
                );


            }

        } else if (convertingResult.getCurrentStatus() == 3) { // Failed
            if (timer1 != null) {
                timer1.cancel();
                timer1 = null;
            }
            if (timerTask1 != null) {
                timerTask1.cancel();
                timerTask1 = null;
            }

            Toast.makeText(mContext, "Convert failed", Toast.LENGTH_LONG).show();

            if (updateGetListener != null) {
                updateGetListener.Update();
            }
        }

    }

    private void uploadNewFile(final ConvertingResult convertingResult) {
        Log.e("addLocalNote", "step five type:" + type + ",convertingResult,status:" + convertingResult.getCurrentStatus() + ",finish_percent:" + convertingResult.getFinishPercent());
        if (convertingResult.getCurrentStatus() == 0) {  // prepare
            if (uploadDetailLinstener != null) {
                uploadDetailLinstener.convertFile(0);
            }
        } else if (convertingResult.getCurrentStatus() == 1 || convertingResult.getCurrentStatus() == 4) { //Converting
            if (uploadDetailLinstener != null) {
                uploadDetailLinstener.convertFile(convertingResult.getFinishPercent());
            }
        } else if (convertingResult.getCurrentStatus() == 5) { //Done
            if (timer1 != null) {
                timer1.cancel();
                timer1 = null;
            }
            if (timerTask1 != null) {
                timerTask1.cancel();
                timerTask1 = null;
            }
            if (type != 1) {

                if (type == 10) {
                    Log.e("addLocalNote", "step six type:" + type);
                    ServiceInterfaceTools.getinstance().uploadLocalNoteFile(AppConfig.URL_PUBLIC + "DocumentNote/UploadNewFile", ServiceInterfaceTools.UPLOADFAVORITENEWFILE, fileName, "",
                            fileHash, convertingResult, field, this.documentId, this.pageIndex, this.noteId, this.syncroomId, this.noteLinkProperty, new ServiceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {
                                    if (object != null) {
                                        JSONObject response = (JSONObject) object;
                                        try {
                                            if (response.getInt("RetCode") == 0) {
                                                NoteId noteId = new NoteId();
                                                noteId.setLinkID(response.getInt("RetData"));
                                                Log.e("addLocalNote", "post noteId:" + noteId.getLinkID());
                                                EventBus.getDefault().post(noteId);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                } else if (type == 3) {

                    ServiceInterfaceTools.getinstance().uploadNewFile(AppConfig.URL_PUBLIC + "EventAttachment/UploadNewFile", ServiceInterfaceTools.UPLOADNEWFILE,
                            fileName, uploadao, lessionId, fileHash, convertingResult, true, field, new ServiceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {
                                    Log.e("UploadNewFile", "object:" + object);
                                    if (uploadDetailLinstener != null) {
                                        uploadDetailLinstener.uploadFinished(object);
                                    }

                                }
                            }
                    );
                } else {
                    ServiceInterfaceTools.getinstance().uploadSpaceNewFile(AppConfig.URL_PUBLIC + "SpaceAttachment/UploadNewFile",
                            ServiceInterfaceTools.UPLOADSPACENEWFILE,
                            fileName, spaceID, "", fileHash,
                            convertingResult, field, new ServiceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {
                                    Log.e("hhh", "SpaceAttachment/UploadNewFile");
                                    if (uploadDetailLinstener != null) {
                                        uploadDetailLinstener.uploadFinished(object);
                                    }
                                    Toast.makeText(mContext, "upload success", Toast.LENGTH_LONG).show();
                                    EventBus.getDefault().post(new TeamSpaceBean());
                                }
                            }
                    );
                }

            } else {


                ServiceInterfaceTools.getinstance().uploadFavoriteNewFile(AppConfig.URL_PUBLIC + "FavoriteAttachment/UploadNewFile",
                        ServiceInterfaceTools.UPLOADFAVORITENEWFILE,
                        fileName, "", fileHash,
                        convertingResult, field, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                Log.e("hhh", "FavoriteAttachment/UploadNewFile");
//                                Toast.makeText(mActivity, "upload success", Toast.LENGTH_LONG).show();
//                                updateGetListener.Update();
                                if (uploadDetailLinstener != null) {
                                    uploadDetailLinstener.uploadFinished(object);
                                }
                            }
                        }
                );

            }
            if (uploadDetailLinstener != null) {
                uploadDetailLinstener.uploadFinished("");
            }
        } else if (convertingResult.getCurrentStatus() == 3) { // Failed
            if (timer1 != null) {
                timer1.cancel();
                timer1 = null;
            }
            if (timerTask1 != null) {
                timerTask1.cancel();
                timerTask1 = null;
            }

            if (uploadDetailLinstener != null) {
                uploadDetailLinstener.uploadError("Convert failed");
            }
        }

    }

    private String noteId;
    private String documentId;
    private String pageIndex;
    private String syncroomId;
    private String noteLinkProperty;


    public void uploadNote(final Context context, String targetFolderKey1, int field1,
                           File file, String noteId, String documentId, String pageIndex, int spaceID, String syncroomId, String noteLinkProperty) {
        this.mContext = context;
        this.noteId = noteId;
        this.documentId = documentId;
        this.pageIndex = pageIndex;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.mfile = file;
        this.type = 10;
        this.syncroomId = syncroomId;
        this.fileName = file.getName();
        this.spaceID = spaceID;
        this.noteLinkProperty = noteLinkProperty;
        fileHash = Md5Tool.getMd5ByFile(mfile);
        this.MD5Hash = Md5Tool.transformMD5(AppConfig.UserID + mfile.getName()) + System.currentTimeMillis();
        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(ud);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(ud);
                }
            }
        });
        lg.GetprepareUploading(mContext);
    }


    public void uploadNote(final Context context, String targetFolderKey1, int field1,
                           File file, String noteId, String documentId, String pageIndex, int spaceID, String syncroomId) {
        this.mContext = context;
        this.noteId = noteId;
        this.documentId = documentId;
        this.pageIndex = pageIndex;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.mfile = file;
        fileHash = Md5Tool.getMd5ByFile(mfile);
        this.type = 10;
        this.syncroomId = syncroomId;
        this.fileName = file.getName();
        this.spaceID = spaceID;
        this.MD5Hash = Md5Tool.transformMD5(AppConfig.UserID + mfile.getName()) + System.currentTimeMillis();
        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(ud);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(ud);
                }
            }
        });
        lg.GetprepareUploading(mContext);
    }

}
