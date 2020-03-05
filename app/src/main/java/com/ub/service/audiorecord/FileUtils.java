package com.ub.service.audiorecord;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HXL on 16/8/11.
 * 管理录音文件的类
 */
public class FileUtils {


    private static String rootPath = "pauseRecordDemo";
    //原始文件(不能播放)
    private final static String AUDIO_PCM_BASEPATH = "/" + rootPath + "/pcm/";
    //可播放的高质量音频文件
    private final static String AUDIO_WAV_BASEPATH = "/" + rootPath + "/wav/";
    // 录制的笔记动作路径
    private final static String NOTE_ACTION_BASEPATH="/"+rootPath+ "/note/";

    private static void setRootPath(String rootPath) {
        FileUtils.rootPath = rootPath;
    }

    public static String getPcmFileAbsolutePath(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("fileName isEmpty");
        }
        if (!isSdcardExit()) {
            throw new IllegalStateException("sd card no found");
        }
        String mAudioRawPath = "";
        if (isSdcardExit()) {
            if (!fileName.endsWith(".pcm")) {
                fileName = fileName + ".pcm";
            }
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;
            File file = new File(fileBasePath);
            //创建目录
            if (!file.exists()) {
                file.mkdirs();
            }
            mAudioRawPath = fileBasePath + fileName;
        }

        return mAudioRawPath;
    }

    public static String getWavFileAbsolutePath(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName can't be null");
        }
        if (!isSdcardExit()) {
            throw new IllegalStateException("sd card no found");
        }

        String mAudioWavPath = "";
        if (isSdcardExit()) {
            if (!fileName.endsWith(".wav")) {
                fileName = fileName + ".wav";
            }
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;
            File file = new File(fileBasePath);
            //创建目录
            if (!file.exists()) {
                file.mkdirs();
            }
            mAudioWavPath = fileBasePath + fileName;
        }
        return mAudioWavPath;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取全部pcm文件列表
     *
     * @return
     */
    public static List<File> getPcmFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {

            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        return list;

    }

    /**
     * 获取全部wav文件列表
     *
     * @return
     */
    public static File getWavFile(String fileName) {
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH + fileName + ".wav";
        File file = new File(fileBasePath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }



    public static File createNoteFile(String notename){
        if (TextUtils.isEmpty(notename)) {
            throw new NullPointerException("fileName can't be null");
        }
        if (!isSdcardExit()) {
            throw new IllegalStateException("sd card no found");
        }
        if (isSdcardExit()) {
            notename = notename + ".txt";
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + NOTE_ACTION_BASEPATH;
            File file = new File(fileBasePath);
            //创建目录
            if (!file.exists()) {
                file.mkdirs();
            }
            String  mAudioWavPath = fileBasePath + notename;
            File note=new File(mAudioWavPath);
            return note;
        }
        return null;
    }

   public static boolean writeNoteActonToFile(List<String> actiondata,File note){
       try {
           if(note==null){
               return false;
           }
           FileOutputStream fos = new FileOutputStream(note);
           for (int i = 0; i < actiondata.size(); i++) {
               String data =actiondata.get(i);
               fos.write(data.getBytes());
           }
           fos.flush();
           fos.close();
           return true;
       } catch (IOException e) {
           e.printStackTrace();
       }
        return false;
    }

}
