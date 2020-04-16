package com.ub.techexcel.tools;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kloudsync.techexcel.config.AppConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by wang on 2018/3/6.
 */

public class SpliteSocket {

    private static final int SOCKET_DIVIDE_LENGTH = 30000;


    public static void sendMesageBySocket(String msg) {
//
//        String dd = compressToString(msg, "ISO-8859-1");
////        Log.e("kkkkkkkkkkkkk1", dd);
//        msg = Base64Coder.encodeString(dd);
////        Log.e("kkkkkkkkkkkkk2", msg);
//        try {
//            int count = msg.getBytes("utf-8").length;
//            if (count > SOCKET_DIVIDE_LENGTH) {
//                int page = (int) Math.ceil((double) count / (double) SOCKET_DIVIDE_LENGTH);
//                String uuid = UUID.randomUUID().toString();
//                for (int i = 1; i <= page; i++) {
//                    String sub;
//                    int num = SOCKET_DIVIDE_LENGTH * (i - 1);
//                    if (i == page) {
//                        sub = msg.substring(num, count);
//                    } else {
//                        sub = msg.substring(num, SOCKET_DIVIDE_LENGTH * i);
//                    }
//                    String mmsg = uuid + ":" + page + ":" + i + "#" + sub;
//                    Log.e("kkkkkkkkkkkkk3", mmsg);
//                    AppConfig.webSocketClient.send(mmsg);
//                }
//            } else {
//                String uuid = UUID.randomUUID().toString();
//                String mmsg = uuid + ":" + 1 + ":" + 1 + "#" + msg;
//                Log.e("kkkkkkkkkkkkk4", mmsg);
//                AppConfig.webSocketClient.send(mmsg);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (AppConfig.webSocketClient != null) {
            try {
                Log.e("SocketService---------",msg);
                AppConfig.webSocketClient.send(msg);
            } catch (Exception e) {
                Log.e("SocketService---------", "socket 异常");

                if(mContext!=null){
                    Intent intent = new Intent();
                    intent.setAction("com.cn.changenumber");
                    mContext.sendBroadcast(intent);
                }

            }
        }

    }


    public static String compressToString(String str, String encoding) {
        try {
            if (str == null || str.length() == 0) {
                return str;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
            return out.toString(encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String uncompressToString(byte[] bytes, String encoding) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (IOException e) {

        }
        return null;
    }

    private static Context mContext;

    public static void init(Context context) {
        mContext=context;

    }




}