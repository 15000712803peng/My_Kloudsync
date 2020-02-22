package com.kloudsync.techexcel.help;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tonyan on 2020/2/22.
 */

public class OkHttpRequest {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();

    public static JSONObject post(String url, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        JSONObject responseJson = new JSONObject();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            Log.e("OkHttpRequest_POST","response:" + response);
            if (response != null && response.body() != null) {
                responseJson = new JSONObject(response.body().string());
            }
        } catch (Exception e) {
            Log.e("OkHttpRequest_POST","Exception:" + e);
            e.printStackTrace();
        }
        return responseJson;
    }

    public static Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
