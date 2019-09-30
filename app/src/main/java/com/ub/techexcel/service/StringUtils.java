package com.ub.techexcel.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;

public class StringUtils {

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


	/**
	 * 将httpentity转化为string
	 * 
	 * @param httpEntity
	 * @return
	 */
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
