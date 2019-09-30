package com.ub.techexcel.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.kloudsync.techexcel.config.AppConfig;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectService {

    /**
     * 判断网络是否连接
     *
     * @param ctx
     * @return
     */
    public static boolean isNetWorkConnected(Context ctx) {
        if (ctx != null) {
            ConnectivityManager cm = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nwi = cm.getActiveNetworkInfo();
            if (nwi != null) {
                return nwi.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }

    /**
     * 以get方式发送URL请求（httpurlconnection）
     *
     * @param path
     * @return
     */
    public static JSONObject getIncidentData(String path) {
        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.addRequestProperty("UserToken", AppConfig.UserToken);
            // conn.addRequestProperty("LanguageID", AppConfig.LANGUAGEID + "");
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                String json = StringUtils.inputStreamTString(is);
                Log.e("response_json", json);
                jsonObject = new JSONObject(json);
                is.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 通過httpget获取网络数据
     *
     * @param path
     * @return
     */
    public static JSONObject getIncidentbyHttpGet(String path) {
        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.addRequestProperty("UserToken", AppConfig.UserToken);
            // conn.addRequestProperty("LanguageID", AppConfig.LANGUAGEID + "");
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                String json = StringUtils.inputStreamTString(is);

                jsonObject = new JSONObject(json);
                is.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // 利用http发送数据到服务器（addincident）
    public static JSONObject submitDataByJson(String path, JSONObject jsonObject) {
        JSONObject responsejson = new JSONObject();
        // 把JSON数据转换成String类型使用输出流向服务器写
        try {
            URL url2 = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("UserToken", AppConfig.UserToken);
            // connection.addRequestProperty("LanguageID", AppConfig.LANGUAGEID
            // + "");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            if (jsonObject != null) {
                os.write(String.valueOf(jsonObject).getBytes());
            }
            os.close();
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String json = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(json);
                is.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;
    }

    public static JSONObject submitDataByJsonNoToken(String path,
                                                     JSONObject jsonObject) {
        JSONObject responsejson = new JSONObject();
        /* 把JSON数据转换成String类型使用输出流向服务器写 */
        Log.e("path", path);
        try {
            URL url2 = new URL(path);
            String content = String.valueOf(jsonObject);
            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            // connection.addRequestProperty("UserToken", AppConfig.UserToken);
            // connection.addRequestProperty("LanguageID", AppConfig.LANGUAGEID
            // + "");
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String json = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(json);
                is.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;
    }

    public static JSONObject submitDataByJsonRobot(String path,
                                                   JSONObject jsonObject) {
        JSONObject responsejson = new JSONObject();
        /* 把JSON数据转换成String类型使用输出流向服务器写 */
        Log.e("path", path);
        try {
            URL url2 = new URL(path);
            String content = String.valueOf(jsonObject);
            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("Authorization", AppConfig.Authorization);
            // connection.addRequestProperty("UserToken", AppConfig.UserToken);
            // connection.addRequestProperty("LanguageID", AppConfig.LANGUAGEID
            // + "");
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String json = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(json);
                is.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;
    }

    public static JSONObject getRobotHttpGet(String path) {
        Log.e("path", path);
        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.addRequestProperty("Authorization", AppConfig.Authorization);
            // conn.addRequestProperty("LanguageID", AppConfig.LANGUAGEID + "");
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                String json = StringUtils.inputStreamTString(is);

                jsonObject = new JSONObject(json);
                is.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
