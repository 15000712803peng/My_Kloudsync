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
                            uploadDetailLinstener.uploadFinished("");
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



    public static void addDocumentInDoc(final Activity activity,final File file,final String lessonId ,final DocumentUploadTool.DocUploadDetailLinstener uploadDetailLinstener) {
        final JSONObject jsonobject = null;
        String url = null;

            try {
                url = AppConfig.URL_PUBLIC + "EventAttachment/UploadFileWithHash?LessonID=" + lessonId + "&Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(file.getName()), "UTF-8") + "&Hash=" +
                        Md5Tool.getMd5ByFile(file) + "&IsAddToFavorite=1";

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
                            uploadDetailLinstener.uploadFinished("");
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            JSONObject jsonObject = responsedata.getJSONObject("RetData");
                            String targetFolderKey = jsonObject.getString("Path");
                            int field = jsonObject.getInt("FileID");
                            DocumentUploadTool uploadTool = new DocumentUploadTool(activity);
                            uploadTool.setUploadDetailLinstener(uploadDetailLinstener);
                            uploadTool.uploadFile(activity, targetFolderKey, field, file, lessonId,3);
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
                            uploadDetailLinstener.uploadFinished("");
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


    public static void addDocumentToFavoriteMp3(final Activity activity, String filePath, final DocumentUploadTool.DocUploadDetailLinstener uploadDetailLinstener) {
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
                            uploadDetailLinstener.uploadFinished("");
                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            JSONObject jsonObject = responsedata.getJSONObject("RetData");
                            String targetFolderKey = jsonObject.getString("Path");
                            int field = jsonObject.getInt("FileID");


                            DocumentUploadTool uploadTool = new DocumentUploadTool(activity);
                            uploadTool.setUploadDetailLinstener(uploadDetailLinstener);
                            uploadTool.uploadFileFavoritemp3(activity, targetFolderKey, field, file);


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
















    public static void addLocalNote(final Activity activity, String filePath, final String noteId, final String documentId, final String pageNumber, final int spaceId,final String syncroomId) {
        final JSONObject jsonobject = null;
        String url = null;
        final File file = new File(filePath);
        final String fileHash = Md5Tool.getMd5ByFile(file);

        if (file.exists()) {
            Log.e("addLocalNote","step one :local file exist");
            try {
                int num = (int)(Float.parseFloat(pageNumber));
                url = AppConfig.URL_PUBLIC + "DocumentNote/UploadFileWithHash?localFileID=" + noteId + "&documentItemID=" + documentId + "&pageNumber=" +num + "&title="
                        + URLEncoder.encode(LoginGet.getBase64Password(file.getName()), "UTF-8") +
                        "&hash=" + fileHash + "&syncRoomID=" + syncroomId;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String finalUrl = url;

            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService
                                .submitDataByJson(finalUrl, jsonobject);
                        Log.e("addLocalNote", "step two:" + responsedata.toString() + "   " + finalUrl);
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        final String errorMessage = responsedata
                                .getString("ErrorMessage");
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {

                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            JSONObject jsonObject = responsedata.getJSONObject("RetData");
                            String targetFolderKey = jsonObject.getString("Path");
                            int field = jsonObject.getInt("FileID");
                            DocumentUploadTool uploadTool = new DocumentUploadTool(activity);
                            uploadTool.uploadNote(activity, targetFolderKey, field, file,noteId,documentId,pageNumber,spaceId,syncroomId);
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

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }).start(ThreadManager.getManager());
        } else {
            Toast.makeText(activity, activity.getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

    public static void addLocalNote(final Activity activity, String filePath, final String noteId, final String documentId, final String pageNumber, final int spaceId, final String syncroomId, final String noteLinkProperty) {
        final JSONObject jsonobject = new JSONObject();
        String url = null;
        final File file = new File(filePath);
        final String fileHash = Md5Tool.getMd5ByFile(file);

        if (file.exists()) {
            Log.e("addLocalNote","step one,local file exist :" + file.getAbsolutePath());
            try {
                int num = (int)(Float.parseFloat(pageNumber));
                try {
                    jsonobject.put("LocalFileID",noteId);
                    jsonobject.put("DocumentItemID",documentId);
                    jsonobject.put("PageNumber",num);
                    jsonobject.put("Title",URLEncoder.encode(LoginGet.getBase64Password(file.getName()), "UTF-8"));
                    jsonobject.put("Hash",fileHash);
                    jsonobject.put("SyncRoomID",syncroomId);
                    jsonobject.put("LinkProperty",noteLinkProperty);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                url = AppConfig.URL_PUBLIC + "DocumentNote/UploadFileWithHash";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String finalUrl = url;

            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject responsedata = com.kloudsync.techexcel.service.ConnectService
                                .submitDataByJson(finalUrl, jsonobject);
                        Log.e("addLocalNote", "step two:" + responsedata.toString() + "   " + finalUrl);
                        String retcode = responsedata.getString("RetCode");
                        Message msg = new Message();
                        final String errorMessage = responsedata
                                .getString("ErrorMessage");
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {

                        } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                            JSONObject jsonObject = responsedata.getJSONObject("RetData");
                            String targetFolderKey = jsonObject.getString("Path");
                            int field = jsonObject.getInt("FileID");
                            DocumentUploadTool uploadTool = new DocumentUploadTool(activity);
                            uploadTool.uploadNote(activity, targetFolderKey, field, file,noteId,documentId,pageNumber,spaceId,syncroomId,noteLinkProperty);
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

                        }

                    } catch (JSONException e) {

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
