package com.kloudsync.user.techexcel.pi.tools;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

public class FileUtils {
	/**
	 * sd卡的根目录
	 */
	private static String mSdRootPath = Environment
			.getExternalStorageDirectory().getPath();
	/**
	 * 手机的缓存根目录
	 */
	private static String mDataRootPath = null;
	/**
	 * 保存Image的目录名
	 */
	private final static String FOLDER_NAME = "/Image";

	/**
	 * getCacheDir()方法用于获取/data/data//cache目录
	 * 
	 * getFilesDir()方法用于获取/data/data//files目录
	 * 
	 * @param context
	 */
	public FileUtils(Context context) {
		mDataRootPath = context.getCacheDir().getPath();
	}

	/**
	 * 获取储存Image的目录 Environment.getExtemalStorageState()
	 * 
	 * Environment.MEDIA_MOUNTED手机装有SDCard,并且可以进行读写
	 * 
	 * 获取SDCard的目录:Environment.getExtemalStorageDirectory()
	 * 
	 * @return
	 */
	public String getStorageDirectory() {
		// 判断sdcard是否存在
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
				: mDataRootPath + FOLDER_NAME;
	}

	

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExists(String fileurl) {
		return new File(fileurl).exists();
	}

	/**
	 * 获取文件的大小
	 * 
	 * @param fileName
	 * @return
	 */
	public long getFileSize(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName
				+ ".png").length();
	}

	/**
	 * 删除SD卡或者手机的缓存图片和目录
	 */
	public void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if (!dirFile.exists()) {
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
		dirFile.delete();
	}
	
	public void deleteFile(String fileurl) {
		File dirFile = new File(fileurl);
		dirFile.delete();
	}
	
	
	 
    public static  String getPath(Context context, Uri uri) { 
  
        if ("content".equalsIgnoreCase(uri.getScheme())) { 
            String[] projection = { "_data" }; 
            Cursor cursor = null; 
  
            try { 
                cursor = context.getContentResolver().query(uri, projection,null, null, null); 
                int column_index = cursor.getColumnIndexOrThrow("_data"); 
                if (cursor.moveToFirst()) { 
                    return cursor.getString(column_index); 
                } 
            } catch (Exception e) { 
                // Eat it 
            } 
        } 
  
        else if ("file".equalsIgnoreCase(uri.getScheme())) { 
            return uri.getPath(); 
        } 
  
        return null; 
    } 

}
