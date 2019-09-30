package com.ub.techexcel.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

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
    private final static String FOLDER_NAME = "/.ubao";
    private final static String FOLDER_NAME2 = "/.ubao2";

    /**
     * getCacheDir()方法用于获取/data/data//cache目录
     * <p>
     * getFilesDir()方法用于获取/data/data//files目录
     *
     * @param context
     */
    public FileUtils(Context context) {
        mDataRootPath = context.getCacheDir().getPath();
    }

    /**
     * 获取储存Image的目录 Environment.getExtemalStorageState()
     * <p>
     * Environment.MEDIA_MOUNTED手机装有SDCard,并且可以进行读写
     * <p>
     * 获取SDCard的目录:Environment.getExtemalStorageDirectory()
     *
     * @return
     */
    public String getStorageDirectory() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
                : mDataRootPath + FOLDER_NAME;
    }
    public String getStorageDirectory2() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME2
                : mDataRootPath + FOLDER_NAME2;
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     *
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public void savaBitmap(String fileName, Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return;
        }
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdir(); // 创建文件目录
        }
        File file = new File(path + File.separator + fileName + ".png");
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file); // 文件输出流 字节流
        // 将图片保存到文件中
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    /**
     * 从手机或者sd卡获取Bitmap
     *
     * @param fileName
     * @return
     */
    public Bitmap getBitmap(String fileName) {
        return BitmapFactory.decodeFile(getStorageDirectory() + File.separator
                + fileName + ".png");
    }

    /**
     * 判断文件是否存在
     *
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

    public void deleteFile2() {
        File dirFile = new File(getStorageDirectory2());
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


    public static String getPath(Context context, Uri uri) {

        String path = "";
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    path = cursor.getString(column_index);

                }
            } catch (Exception e) {
                // Eat it 
            }

            if (TextUtils.isEmpty(path)) {
                path = getFPUriToPath(context, uri);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }


        return path;
    }

    public static String getFPUriToPath(Context context, Uri uri) {
        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs != null) {
                String fileProviderClassName = FileProvider.class.getName();
                for (PackageInfo pack : packs) {
                    ProviderInfo[] providers = pack.providers;
                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            if (uri.getAuthority().equals(provider.authority)) {
                                if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                                    Class<FileProvider> fileProviderClass = FileProvider.class;
                                    try {
                                        Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                                        getPathStrategy.setAccessible(true);
                                        Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
                                        if (invoke != null) {
                                            String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                            Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                            Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                            getFileForUri.setAccessible(true);
                                            Object invoke1 = getFileForUri.invoke(invoke, uri);
                                            if (invoke1 instanceof File) {
                                                String filePath = ((File) invoke1).getAbsolutePath();
                                                return filePath;
                                            }
                                        }
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
