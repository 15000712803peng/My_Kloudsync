package com.kloudsync.techexcel.pc.help;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.kloudsync.techexcel.config.AppConfig;


import android.util.Log;

public class SendPostAndGet {
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
				String json = inputStreamTString(is);
				responsejson = new JSONObject(json);
				is.close();
				connection.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responsejson;
	}

	public static String inputStreamTString(InputStream in) {

		// 构造输出流对象 写数据
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;// 表示成功读取字节数的个数
		try {
			// 将输入流读到字节数组中，每次最多读取多少个
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			byte[] result = out.toByteArray();
			in.close();
			out.close();
			return new String(result, "utf-8");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getIncidentbyHttpGet(String url) {
		JSONObject jsonObject = new JSONObject();
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			get.setHeader("UserToken", AppConfig.UserToken);
			HttpResponse httpResponse = client.execute(get);
			Log.e("zhang", httpResponse.getStatusLine().getStatusCode()+"");
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = httpResponse.getEntity();
				String result = retrieveInputStream(entity);
				jsonObject = new JSONObject(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public static String retrieveInputStream(HttpEntity httpEntity) {
		int length = (int) httpEntity.getContentLength();
		if (length < 0)
			length = 10000;
		StringBuffer stringBuffer = new StringBuffer(length);
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					httpEntity.getContent(), HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
}
