package com.ub.techexcel.tools;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventNote;
import com.ub.techexcel.bean.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadUtil {


    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final String saveDir, final OnDownloadListener listener) {
        Request request = new Request.Builder().get().url(url).tag(saveDir).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ddddddddsuccess", url + "");
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
//                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(saveDir);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    Log.e("dddddddd", response.code() + "    " + saveDir);
                    listener.onDownloadSuccess(response.code());
                } catch (Exception e) {
                    Log.e("downLoadPage", "onDownloadFailed:"  + e);
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }


    /**
     * 根据tag下载
     *
     * @param tag
     */
    public void cancel(String tag) {
        if (tag == null) return;
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

    }

    public void cancelAll() {
        Log.e("cancel","取消所有下载");
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(int code);

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }

    public void syncDownload(DocumentPage page, final OnDownloadListener listener) {
        Request request = new Request.Builder().get().url(page.getPageUrl()).build();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful() && response.body() != null){

                byte[] buf = new byte[2048];
                int len = 0;
                is = response.body().byteStream();
                long total = response.body().contentLength();
                File file = new File(page.getSavedLocalPath());
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    int progress = (int) (sum * 1.0f / total * 100);
                    // 下载中
                    listener.onDownloading(progress);
                }
                fos.flush();
                // 下载完成
                listener.onDownloadSuccess(response.code());
            }else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }

    }

    public void syncDownload(EventNote note, final OnDownloadListener listener) {
        Request request = new Request.Builder().get().url(note.getNote().getUrl()).build();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful() && response.body() != null){

                byte[] buf = new byte[2048];
                int len = 0;
                is = response.body().byteStream();
                long total = response.body().contentLength();
                File file = new File(note.getNote().getLocalFilePath());
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    int progress = (int) (sum * 1.0f / total * 100);
                    // 下载中
                    listener.onDownloading(progress);
                }
                fos.flush();
                // 下载完成
                listener.onDownloadSuccess(response.code());
            }else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }

    }


}
