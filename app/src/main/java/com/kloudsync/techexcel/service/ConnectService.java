package com.kloudsync.techexcel.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.kloudsync.techexcel.config.AppConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
     * 通過httppost带参获取网络数据
     *
     * @param url
     * @return
     */
    public static JSONObject postIncidentData(String url, int pageIndex,
                                              int pageSize, int pjId) {
        JSONObject responsejson = new JSONObject();
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
                5000); // 超时设置
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("UserToken", AppConfig.UserToken);
        /*
         * httppost.setHeader("UserToken", AppConfig.UserToken);
		 * httppost.setHeader("LanguageID", AppConfig.LANGUAGEID + "");
		 */
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); // 存放请求参数
            nameValuePairs.add(new BasicNameValuePair("ProjectID", pjId + ""));
            nameValuePairs.add(new BasicNameValuePair("FolderIDs", "0"));
            nameValuePairs.add(new BasicNameValuePair("ShowAll", 0 + ""));
            nameValuePairs.add(new BasicNameValuePair("PageIndex", pageIndex
                    + ""));
            nameValuePairs
                    .add(new BasicNameValuePair("PageSize", pageSize + ""));
            nameValuePairs.add(new BasicNameValuePair("KeyWords", ""));
            nameValuePairs.add(new BasicNameValuePair("OwnerIDs", ""));
            nameValuePairs.add(new BasicNameValuePair("StatusIDs", ""));
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(
                    nameValuePairs);
            httppost.setEntity(encodedFormEntity);

            HttpResponse response = httpclient.execute(httppost);
            // 处理返回的httpResponse信息
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = StringUtils.retrieveInputStream(response
                        .getEntity());

                // String
                // result1=EntityUtils.toString(encodedFormEntity,"UTF-8");
                try {
                    responsejson = new JSONObject(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responsejson;
    }

    /**
     * 通過httppost带参获取网络数据
     *
     * @param url
     * @return
     */
    public static JSONObject postEventData(String url, int projectId,
                                           int folderId, int showall, int pageIndex, int pageSize) {
        JSONObject responsejson = new JSONObject();
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
                5000); // 超时设置
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("UserToken", AppConfig.UserToken);
        /*
         * httppost.setHeader("UserToken", AppConfig.UserToken);
		 * httppost.setHeader("LanguageID", AppConfig.LANGUAGEID + "");
		 */
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); // 存放请求参数
            nameValuePairs.add(new BasicNameValuePair("ProjectID", projectId
                    + ""));
            nameValuePairs.add(new BasicNameValuePair("FolderIDs", folderId
                    + ""));
            nameValuePairs.add(new BasicNameValuePair("ShowAll", showall + ""));
            nameValuePairs.add(new BasicNameValuePair("PageIndex", pageIndex
                    + ""));
            nameValuePairs
                    .add(new BasicNameValuePair("PageSize", pageSize + ""));
            nameValuePairs.add(new BasicNameValuePair("KeyWords", ""));
            nameValuePairs.add(new BasicNameValuePair("OwnerIDs", ""));
            nameValuePairs.add(new BasicNameValuePair("StatusIDs", ""));
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(
                    nameValuePairs);
            httppost.setEntity(encodedFormEntity);

            HttpResponse response = httpclient.execute(httppost);
            // 处理返回的httpResponse信息
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = StringUtils.retrieveInputStream(response
                        .getEntity());
                // String
                // result1=EntityUtils.toString(encodedFormEntity,"UTF-8");
                try {
                    responsejson = new JSONObject(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responsejson;
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

                jsonObject = new JSONObject(json);
                is.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public static JSONObject getIncidentDataattachment(String path) {
        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.addRequestProperty("UserToken", AppConfig.UserToken);
            /*
             * conn.addRequestProperty("UserToken", AppConfig.UserToken);
			 * conn.addRequestProperty("LanguageID", AppConfig.LANGUAGEID + "");
			 */
            conn.setRequestMethod("DELETE");
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

    /**
     * 通過httpget获取网络数据
     *
     * @param url
     * @return
     */
    public static JSONObject getIncidentbyHttpGet(String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            get.setHeader("UserToken", AppConfig.UserToken);
            /*
             * get.setHeader("UserToken", AppConfig.UserToken);
			 * get.setHeader("LanguageID", AppConfig.LANGUAGEID + "");
			 */
            HttpResponse httpResponse = client.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                String result = StringUtils.retrieveInputStream(entity);
                jsonObject = new JSONObject(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // 利用http发送数据到服务器（addincident）
    public static JSONObject submitDataByJson(String path, JSONObject jsonObject) {
        JSONObject responsejson = new JSONObject();
        /* 把JSON数据转换成String类型使用输出流向服务器写 */
        try {
            URL url2 = new URL(path);
            String content = String.valueOf(jsonObject);
            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("UserToken", AppConfig.UserToken);
            connection.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash," +
                    " application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();
            int code = connection.getResponseCode();
            Log.e("code", code + "");
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String str = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(str);
                is.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;

    }
    // 利用http发送数据到服务器（addincident）
    public static JSONObject submitDataByJson4(String path, String content) {
        JSONObject responsejson = new JSONObject();
        /* 把JSON数据转换成String类型使用输出流向服务器写 */
        try {
            URL url2 = new URL(path);

            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("UserToken", AppConfig.UserToken);
            connection.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash," +
                    " application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();
            int code = connection.getResponseCode();
            Log.e("code", code + "");
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String str = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(str);
                is.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;

    }







    // 利用http发送数据到服务器（addincident）
    public static JSONObject submitDataByJsonLive(String path, JSONObject jsonObject) {
        JSONObject responsejson = new JSONObject();
        /* 把JSON数据转换成String类型使用输出流向服务器写 */
        try {
            URL url2 = new URL(path);
            String content = String.valueOf(jsonObject);
            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("Authorization", "Bearer " +AppConfig.liveToken);
            connection.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash," +
                    " application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();
            int code = connection.getResponseCode();
            Log.e("code", code + "");
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String str = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(str);
                is.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;

    }


    // 利用http发送数据到服务器（addincident）
    public static JSONObject submitDataByJsonArray(String path, JSONArray jsonObject) {
        JSONObject responsejson = new JSONObject();
        /* 把JSON数据转换成String类型使用输出流向服务器写 */
        try {
            URL url2 = new URL(path);
            String content = String.valueOf(jsonObject);
            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("UserToken", AppConfig.UserToken);
            connection.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash," +
                    " application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();
            int code = connection.getResponseCode();
            Log.e("code", code + "");
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String str = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(str);
                is.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;
    }


    // 利用http发送数据到服务器（addincident）
    public static JSONObject submitDataByJson2(String path, String jsonObject) {
        JSONObject responsejson = new JSONObject();
        try {
            URL url2 = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
//          connection.addRequestProperty("UserToken", AppConfig.UserToken);
            connection.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash," +
                    " application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(jsonObject.getBytes());
            os.close();
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String str = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(str);
                is.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;
    }

}
