package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.kloudsync.techexcel.tool.Md5Tool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AddDocumentTool {

    public static void addDocumentToSpace(final Activity activity, String filePath, final int spaceId, final DocumentUploadTool.DocUploadDetailLinstener uploadDetailLinstener) {
        final JSONObject jsonobject = null;
        String url = null;
        final File file = new File(filePath);
        final String fileHash = Md5Tool.getMd5ByFile(file);

        if (file.exists()) {
            try {
                url = AppConfig.URL_PUBLIC + "SpaceAttachment/UploadFileWithHash?spaceID=" + spaceId + "&folderID=-1&Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(file.getName()), "UTF-8") +
                        "&Hash=" + fileHash;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String finalUrl = url;

            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        uploadDetailLinstener.uploadStart();
                        JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService
                                .submitDataByJson(finalUrl, jsonobject);
                        Log.e("UploadFileWithHash", responsedata.toString() + "   " + finalUrl);
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        final String errorMessage = responsedata
                                .getString("ErrorMessage");
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                            uploadDetailLinstener.uploadFinished();
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            JSONObject jsonObject = responsedata.getJSONObject("RetData");
                            String targetFolderKey = jsonObject.getString("Path");
                            int field = jsonObject.getInt("FileID");
                            DocumentUploadTool uploadTool = new DocumentUploadTool(activity);
                            uploadTool.setUploadDetailLinstener(uploadDetailLinstener);
                            uploadTool.uploadFileV2(activity, targetFolderKey, field, file, spaceId);
                        } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                            msg.what = AppConfig.FAILED;
                            msg.obj = errorMessage;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            uploadDetailLinstener.uploadError(errorMessage);
                        }

                    } catch (JSONException e) {
                        uploadDetailLinstener.uploadError("data exception,add failed");
                        e.printStackTrace();
                    }
                }
            }).start(ThreadManager.getManager());
        } else {
            Toast.makeText(activity, activity.getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }


    public static void addDocumentToFavorite(final Activity activity, String filePath, final DocumentUploadTool.DocUploadDetailLinstener uploadDetailLinstener) {
        final JSONObject jsonobject = null;
        String url = null;
        final File file = new File(filePath);
        final String fileHash = Md5Tool.getMd5ByFile(file);
        if (file.exists()) {
            try {
                url = AppConfig.URL_PUBLIC + "FavoriteAttachment/UploadFileWithHash?Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(file.getName()), "UTF-8") +
                        "&Hash=" + fileHash;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String finalUrl = url;

            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        uploadDetailLinstener.uploadStart();
                        JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService
                                .submitDataByJson(finalUrl, jsonobject);
                        Log.e("UploadFileWithHash", responsedata.toString() + "   " + finalUrl);
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        final String ErrorMessage = responsedata
                                .getString("ErrorMessage");
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                            uploadDetailLinstener.uploadFinished();
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            JSONObject jsonObject = responsedata.getJSONObject("RetData");
                            String targetFolderKey = jsonObject.getString("Path");
                            int field = jsonObject.getInt("FileID");
//                            uploadFile2(attachmentBean);
                            DocumentUploadTool uploadTool = new DocumentUploadTool(activity);
                            uploadTool.setUploadDetailLinstener(uploadDetailLinstener);
                            uploadTool.uploadFileFavorite2(activity, targetFolderKey, field, file);
                        } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                            msg.what = AppConfig.FAILED;
                            msg.obj = ErrorMessage;
                            uploadDetailLinstener.uploadError(ErrorMessage);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, ErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            uploadDetailLinstener.uploadError(ErrorMessage);
                        }

                    } catch (JSONException e) {
                        uploadDetailLinstener.uploadError("data exception,add failed");
                        e.printStackTrace();
                    }
                }
            }).start(ThreadManager.getManager());
        } else {
            Toast.makeText(activity, activity.getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

}
