package com.kloudsync.techexcel.tool;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
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
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManagerConfiguration;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kloudsync.techexcel.adapter.FavoriteAdapter;
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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imlib.MD5;

public class DocumentUploadUtil {

    private Context mContext;
    private String targetFolderKey;
    private int field;
    private Uploadao uploadao = new Uploadao();
    private int itemID;

    private File mfile;
    private String MD5Hash;
    private String fileName;

    private Timer timer1;
    private TimerTask timerTask1;

    private Popupdate puo;
    private View mTeamRecyclerView;
    private FavoriteAdapter fAdapter;
    private ArrayList<Document> mlist = new ArrayList<Document>();
    private boolean flags = true;
    private int type;

    private Document favorite = new Document();


    private static UpdateGetListener updateGetListener;

    public interface UpdateGetListener {
        void Update();
    }

    public void setUpdateGetListener(UpdateGetListener updateGetListener) {
        this.updateGetListener = updateGetListener;
    }

    /**
     * Document上传
     *
     * @param context
     * @param targetFolderKey1
     * @param field1
     * @param attachmentBean
     * @param view
     * @param itemID1
     */
    public void uploadFile2(final Context context, String targetFolderKey1, int field1, final LineItem attachmentBean, View view,
                            int itemID1) {
        this.mContext = context;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.mTeamRecyclerView = view;
        this.itemID = itemID1;
        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {
                puo = new Popupdate();
                puo.getPopwindow(mContext, attachmentBean.getFileName());
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
        lg.GetprepareUploading(mContext);
    }

    /**
     * Favorite上传
     *
     * @param context
     * @param targetFolderKey1
     * @param field1
     * @param attachmentBean
     * @param view
     * @param fAdapter1
     * @param mlist1
     */
    public void uploadFileFavorite(final Context context, String targetFolderKey1, int field1, final LineItem attachmentBean, View view,
                                   FavoriteAdapter fAdapter1, ArrayList<Document> mlist1) {
        this.mContext = context;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.mTeamRecyclerView = view;
        this.fAdapter = fAdapter1;
        this.mlist = mlist1;
        flags = false;
        type = 1;
        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {

                if (flags) {
                    puo = new Popupdate();
                    puo.getPopwindow(mContext, attachmentBean.getFileName());
                    puo.setPopCancelListener(new Popupdate.PopCancelListener() {
                        @Override
                        public void Cancel() {
                        }
                    });
                    puo.StartPop(mTeamRecyclerView);
                }
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(attachmentBean, ud);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(attachmentBean, ud);
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
     * @param attachmentBean
     * @param view
     */
    public void uploadFileFavorite2(final Context context, String targetFolderKey1, int field1, final LineItem attachmentBean, View view) {
        this.mContext = context;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.mTeamRecyclerView = view;
        type = 1;

        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {

                if (flags) {
                    puo = new Popupdate();
                    puo.getPopwindow(mContext, attachmentBean.getFileName());
                    puo.setPopCancelListener(new Popupdate.PopCancelListener() {
                        @Override
                        public void Cancel() {
                        }
                    });
                    puo.StartPop(mTeamRecyclerView);
                }
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(attachmentBean, ud);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(attachmentBean, ud);
                }
            }
        });
        lg.GetprepareUploading(mContext);
    }


    /**
     * Favorite上传视频文件 不需要转化
     *
     * @param context
     * @param targetFolderKey1
     * @param field1
     * @param attachmentBean
     * @param view
     */
    private boolean isNeedConvert = true;

    public void uploadVideoFavorite(final Context context, String targetFolderKey1, int field1, final LineItem attachmentBean, View view) {
        isNeedConvert = false;
        this.mContext = context;
        this.targetFolderKey = targetFolderKey1;
        this.field = field1;
        this.mTeamRecyclerView = view;
        type = 1;

        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {

                if (flags) {
                    puo = new Popupdate();
                    puo.getPopwindow(mContext, attachmentBean.getFileName());
                    puo.setPopCancelListener(new Popupdate.PopCancelListener() {
                        @Override
                        public void Cancel() {
                        }
                    });
                    puo.StartPop(mTeamRecyclerView);
                }
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
        String name2 = AppConfig.UserID + mfile.getName();
        MD5Hash = Md5Tool.transformMD5(name2);

        initFavorite(attachmentBean);

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
                TransferManagerConfiguration transferManagerConfiguration=new TransferManagerConfiguration();
                transferManagerConfiguration.setMultipartUploadThreshold(1*100*1024);
                tm.setConfiguration(transferManagerConfiguration);
                request.setCannedAcl(CannedAccessControlList.PublicRead);
                request.setGeneralProgressListener(new ProgressListener() {
                    @Override
                    public void progressChanged(final ProgressEvent progressEvent) {
                        Log.e("Transferred", mfile.length() + " : " + progressEvent.getBytesTransferred());
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (flags) {
                                    if (puo != null) {
                                        puo.setProgress(mfile.length(), progressEvent.getBytesTransferred());
                                    }
                                } else {
                                    fAdapter.SetMyProgress(mfile.length(), progressEvent.getBytesTransferred(), favorite);
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
                            if (flags) {
                                if (puo != null) {
                                    puo.DissmissPop();
                                }
                            } else {
//                        favorite.setAttachmentID(attachmentid);
                                favorite.setFlag(2);
                                favorite.setProgress(0);
                            }
                            startConverting(ud, attachmentBean);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start(ThreadManager.getManager());


    }


    private void initFavorite(LineItem attachmentBean) {
        if (!flags) {
            favorite = new Document();
            favorite.setFlag(1);
            favorite.setTitle(attachmentBean.getFileName());
            mlist.add(favorite);
            fAdapter.UpdateRV(mlist);
        }
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
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateVideo3(attachmentBean, ud);
                    }
                });
            }
        }).start(ThreadManager.getManager());
    }


    private long totalSizee;
    private String fileUuidName;

    private void UpdateVideo3(final LineItem attachmentBean, final Uploadao ud) {

        final String path = attachmentBean.getUrl();
        mfile = new File(path);
        fileName = mfile.getName();
        String name2 = AppConfig.UserID + mfile.getName();

        if (isNeedConvert == false) {
            fileUuidName = Md5Tool.getUUID();
            MD5Hash = targetFolderKey + "/" + fileUuidName + ".mp4";
        } else {
            MD5Hash = Md5Tool.transformMD5(name2);
        }

        //开始下载
        attachmentBean.setAttachmentID(-1 + "");
        attachmentBean.setFlag(1);

        initFavorite(attachmentBean);

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
        request.setPartSize(1 * 100 * 1024);
        // 设置上传过程回调
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, final long currentSize, final long totalSize) {
                Log.e("biang", currentSize + ":" + totalSize + ":" + flags + "     " + MD5Hash + "   " + path);
                totalSizee = totalSize;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (flags) {
                            if (puo != null) {
                                puo.setProgress(totalSize, currentSize);
                            }
                        } else {
                            fAdapter.SetMyProgress(totalSize, currentSize, favorite);
                        }
                    }
                });
            }
        });
        oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                attachmentBean.setFlag(2);
                Log.e("biang", "onSuccess");
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (flags) {
                            if (puo != null) {
                                puo.DissmissPop();
                            }
                        } else {
//                        favorite.setAttachmentID(attachmentid);
                            favorite.setFlag(2);
                            favorite.setProgress(0);
                        }
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

    private void startConverting(final Uploadao ud, final LineItem attachmentBean) {
        uploadao = ud;
        if (isNeedConvert) {
            ServiceInterfaceTools.getinstance().startConverting(AppConfig.URL_LIVEDOC + "startConverting", ServiceInterfaceTools.STARTCONVERTING,
                    uploadao, MD5Hash, fileName, targetFolderKey,
                    new ServiceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            Log.e("hhh", "startConvertingstartConverting");
                            convertingPercentage(attachmentBean);
                        }
                    });
        } else {

            ConvertingResult convertingResult = new ConvertingResult();
            convertingResult.setCount(1);
            convertingResult.setFileName(fileUuidName);
            ServiceInterfaceTools.getinstance().uploadFavoritevideo(AppConfig.URL_PUBLIC + "FavoriteAttachment/UploadNewFile",
                    ServiceInterfaceTools.UPLOADFAVORITENEWFILE,
                    fileName, MD5Hash, totalSizee,
                    convertingResult, field, new ServiceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            Toast.makeText(mContext, "video upload success", Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }

        isNeedConvert = true;


    }

    private void convertingPercentage(final LineItem attachmentBean) {
        if (flags) {
            puo = new Popupdate();
            puo.getPopwindow(mContext, attachmentBean.getFileName());
            puo.ChangeName("Converting");
            puo.StartPop(mTeamRecyclerView);
        }

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


    private void uploadNewFile(final ConvertingResult convertingResult,
                               final LineItem attachmentBean) {
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
            if (type != 1) {
                ServiceInterfaceTools.getinstance().uploadSpaceNewFile(AppConfig.URL_PUBLIC + "SpaceAttachment/UploadNewFile",
                        ServiceInterfaceTools.UPLOADSPACENEWFILE,
                        fileName, itemID, "", MD5Hash,
                        convertingResult, field, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                Log.e("hhh", "SpaceAttachment/UploadNewFile");
                                Toast.makeText(mContext, "upload success", Toast.LENGTH_LONG).show();
                                if (puo != null) {
                                    puo.DissmissPop();
                                }
                                EventBus.getDefault().post(new TeamSpaceBean());
                            }
                        }
                );
            } else {
                ServiceInterfaceTools.getinstance().uploadFavoriteNewFile(AppConfig.URL_PUBLIC + "FavoriteAttachment/UploadNewFile",
                        ServiceInterfaceTools.UPLOADFAVORITENEWFILE,
                        fileName, "", MD5Hash,
                        convertingResult, field, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                Log.e("hhh", "FavoriteAttachment/UploadNewFile");
                                Toast.makeText(mContext, "upload success", Toast.LENGTH_LONG).show();
                                updateGetListener.Update();
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

            if (flags) {
                if (puo != null) {
                    puo.DissmissPop();
                }
            }
            if (updateGetListener != null) {
                updateGetListener.Update();
            }
        }

        if (flags) {
            if (puo != null) {
                puo.setProgress(100, attachmentBean.getProgress());
            }
        } else {
            fAdapter.SetMyProgress(100, attachmentBean.getProgress(), favorite);
        }


    }

}
