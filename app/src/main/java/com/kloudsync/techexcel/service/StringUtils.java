package com.kloudsync.techexcel.service;

import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;

public class StringUtils {

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


	public static String getStrFromInsByCode(InputStream is, String code){
		StringBuilder builder=new StringBuilder();
		BufferedReader reader=null;
		try {
			reader = new BufferedReader(new InputStreamReader(is,code));
			String line;
			while((line=reader.readLine())!=null){
				builder.append(line+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return builder.toString();
	}



	public static String Inputstr2Str_Reader(InputStream in, String encode)
	{

		String str = "";
		try
		{
			if (encode == null || encode.equals(""))
			{
				// 默认以utf-8形式
				encode = "utf-8";
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, encode));
			StringBuffer sb = new StringBuffer();
			while ((str = reader.readLine()) != null)
			{
				sb.append(str).append("\n");
			}
			return sb.toString();
		}
		catch (UnsupportedEncodingException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 利用byte数组转换InputStream------->String <功能详细描述>
	 *
	 * @param in
	 * @return
	 * @see [类、类#方法、类#成员]
	 */

	public static String Inputstr2Str_byteArr(InputStream in, String encode)
	{
		StringBuffer sb = new StringBuffer();
		byte[] b = new byte[1024];
		int len = 0;
		try
		{

			if (encode == null || encode.equals(""))
			{
				// 默认以utf-8形式
				encode = "utf-8";
			}

			while ((len = in.read(b)) != -1)
			{
				sb.append(new String(b, 0, len, encode));
			}

			return sb.toString();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * 利用ByteArrayOutputStream：Inputstream------------>String <功能详细描述>
	 *
	 * @param in
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String Inputstr2Str_ByteArrayOutputStream(InputStream in,String encode)
	{

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		try
		{
			if (encode == null || encode.equals(""))
			{
				// 默认以utf-8形式
				encode = "utf-8";
			}
			while ((len = in.read(b)) > 0)
			{
				out.write(b, 0, len);
			}
			return out.toString(encode);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 利用ByteArrayInputStream：String------------------>InputStream <功能详细描述>
	 *
	 * @param inStr
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static InputStream Str2Inputstr(String inStr)
	{
		try
		{
			// return new ByteArrayInputStream(inStr.getBytes());
			// return new ByteArrayInputStream(inStr.getBytes("UTF-8"));
			return new StringBufferInputStream(inStr);
		}
		catch (Exception e)
		{
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
