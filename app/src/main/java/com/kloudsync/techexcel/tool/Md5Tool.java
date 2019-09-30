package com.kloudsync.techexcel.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.UUID;

public class Md5Tool {

    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(
                    FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    public static String transformMD5(String inputStr) {

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
        char[] charArray = inputStr.toCharArray(); //将字符串转换为字符数组
        byte[] byteArray = new byte[charArray.length]; //创建字节数组

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }

        //将得到的字节数组进行MD5运算
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer md5Str = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++) {
            if (Integer.toHexString(0xFF & md5Bytes[i]).length() == 1)
                md5Str.append("0").append(Integer.toHexString(0xFF & md5Bytes[i]));
            else
                md5Str.append(Integer.toHexString(0xFF & md5Bytes[i]));
        }

        return md5Str.toString();
    }

    /**
     * 获得一个UUID
     *
     * @return String UUID
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public static boolean checkType(String houzui) {
        if (houzui.equals("pdf")) {
            return true;
        } else if (houzui.equals("ppt")) {
            return true;
        } else if (houzui.equals("pptx")) {
            return true;
        } else if (houzui.equals("docx")) {
            return true;
        } else if (houzui.equals("doc")) {
            return true;
        }

        return false;
    }
}
