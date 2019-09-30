package com.kloudsync.techexcel.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

import com.ub.techexcel.bean.LineItem;

import java.io.File;
import java.io.IOException;

public class FileGetTool {

    public static File GetFile(LineItem attachmentBean){
        File file = new File(attachmentBean.getUrl());

        String houzui = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
        String path = file.getAbsolutePath();
        if (houzui.equals("jpg") || houzui.equals("png") || houzui.equals("jpeg")) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Log.e("haha3", bitmap.getByteCount() + ":" + bitmap.getWidth()
                    + ":" + bitmap.getHeight() + ":" + houzui);
            if (bitmap.getWidth() > 2000 || bitmap.getHeight() > 2000) {
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    bitmap = BitmapChangeTool.getMyBitmap(bitmap, (float) bitmap.getWidth() / 2000);
                } else {
                    bitmap = BitmapChangeTool.getMyBitmap(bitmap, (float) bitmap.getHeight() / 2000);
                }
            Log.e("haha3", bitmap.getByteCount() + ":" + bitmap.getWidth()
                    + ":" + bitmap.getHeight());

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            bitmap = BitmapChangeTool.rotateBitmap(bitmap, orientation);
            Log.e("haha3", bitmap.getByteCount() + ":" + bitmap.getWidth()
                    + ":" + bitmap.getHeight() + ":" + orientation);

            Log.e("haha4", file.getAbsolutePath() + ":");
            BitmapChangeTool.saveImageToGallery(bitmap, file);
            file = new File(attachmentBean.getUrl());
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Log.e("haha3", file.getAbsolutePath() + ":" + bitmap.getByteCount() + ":" + bitmap.getWidth()
                    + ":" + bitmap.getHeight());
            }

        }
        return file;
    }
}
