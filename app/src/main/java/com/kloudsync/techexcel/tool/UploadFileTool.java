package com.kloudsync.techexcel.tool;

import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UploadFileTool {

    public static void uploadCompanyLogo(String companyId, File logoFile, RequestCallBack<String> callBack) {
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);
        params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
        // params.setContentType("application/octet-stream");
        if (logoFile.exists()) {
            try {
                String baseurl = LoginGet.getBase64Password(logoFile.getName());
                String fileNamebase = URLEncoder.encode(baseurl, "UTF-8");
                params.addBodyParameter(logoFile.getName(), logoFile);
                params.addBodyParameter("fileNamebase", fileNamebase);
                params.addBodyParameter("UploadType", "0");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = AppConfig.URL_PUBLIC + "School/UploadCompanyAvatar?companyID=" + AppConfig.SchoolID;

            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            http.send(HttpRequest.HttpMethod.POST, url, params,
                    callBack);
        }

    }
}
