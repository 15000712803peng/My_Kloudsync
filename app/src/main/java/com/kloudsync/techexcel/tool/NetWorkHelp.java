package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class NetWorkHelp {

	public static boolean checkNetWorkStatus(Context context) 
    {
        boolean result = false;
		if (null != context) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netinfo = cm.getActiveNetworkInfo();
			if (netinfo != null && netinfo.isConnected()) {
				result = true;
			} else {
				result = false;
			}
		}
        return result;
    }

	/**
	 * 将InputStream转换成String
	 * @param in InputStream
	 * @return String
	 * @throws Exception
	 *
	 */
	public static String InputStreamTOString(InputStream in) throws Exception{
		String json = inputStreamTString(in);
		return json;
	}
	/**
	 * 输入流转化为 字节数组 字符串
	 *
	 * @param in
	 * @return
	 */
	public static String inputStreamTString(InputStream in) {
		//构造输出流对象  写数据
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;//表示成功读取字节数的个数
		try {
			//将输入流读到字节数组中，每次最多读取多少个
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			byte[] result = out.toByteArray();
			in.close();
			out.close();
			return new String(result, "utf-8");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
}
