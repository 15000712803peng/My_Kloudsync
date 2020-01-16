package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;

import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;

public class VideoAudioManager {


    private static VideoAudioManager mVideoAudioManager;
    private Context context;

    public static synchronized VideoAudioManager getMgr(Context context) {

        if (mVideoAudioManager == null) {
            mVideoAudioManager = new VideoAudioManager(context);
        }
        return mVideoAudioManager;
    }


    public VideoAudioManager(Context context) {
        this.context = context;
    }


    private String targetFolderKey;
    private int field;

    public void uploadLocalVideo(final String path1, final String title, final View view) {

        final LineItem attachmentBean = new LineItem();
        attachmentBean.setUrl(path1);
        attachmentBean.setFileName(title);
        File file = FileGetTool.GetFile(attachmentBean);
        if (file.exists()) {
            try {
                String url = AppConfig.URL_PUBLIC + "FavoriteAttachment/UploadFileWithHash?Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") + "&Hash=" + Md5Tool.getMd5ByFile(file);

                MeetingServiceTools.getInstance().uploadFileWithHash(url, MeetingServiceTools.UPLOADFILEWITHHASH, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        try {
                            JSONObject responsedata = (JSONObject) object;
                            String retcode = responsedata.getString("RetCode");
                            if (retcode.equals(AppConfig.RIGHT_RETCODE)) {  // 刷新

                            } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                                JSONObject jsonObject = responsedata.getJSONObject("RetData");
                                targetFolderKey = jsonObject.getString("Path");
                                field = jsonObject.getInt("FileID");
                                DocumentUploadUtil duu = new DocumentUploadUtil();
                                duu.setUpdateGetListener(new DocumentUploadUtil.UpdateGetListener() {
                                    @Override
                                    public void Update() {
                                        Toast.makeText(context, "video upload success", Toast.LENGTH_LONG).show();
                                    }
                                });
                                duu.uploadVideoFavorite(context, targetFolderKey, field,
                                        attachmentBean, view);
                            } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                                String errorMessage = responsedata
                                        .getString("ErrorMessage");
                                Toast.makeText(context, errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }


//https://api.peertime.cn/peertime/V1/FavoriteAttachment/UploadFileWithHash?Title=YXBwbGUubXA0&Description=&Hash=3682bdf6fce14e971da2f4b6c7f57591
//https://api.peertime.cn/peertime/V1/FavoriteAttachment/UploadFileWithHash?Title=VklEXzIwMTkxMjIyXzEyMjAxOS5tcDQ%3D%0A&Hash=53a62198e4ad6012f5b5709837400823














}
