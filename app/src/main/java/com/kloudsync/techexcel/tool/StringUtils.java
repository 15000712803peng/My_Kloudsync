//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kloudsync.techexcel.tool;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static final String UTF16LE = "UTF-16LE";
    public static final String UTF16BE = "UTF-16BE";
    public static final String UTF16 = "UTF-16";
    public static String punctuation = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    public static String END_WIDTH_NUMBER_REGEX = "\\d+$";

    public StringUtils() {
    }

    public static String toStringSafely(Object obj) {
        return obj == null?"":obj.toString();
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().length() <= 0;
    }

    public static boolean isNotBlank(String string) {
        return string != null && string.trim().length() > 0;
    }

    public static boolean isBlank(String string) {
        return !isNotBlank(string);
    }

    public static boolean isInteger(String string) {
        if(isNullOrEmpty(string)) {
            return false;
        } else {
            String var1 = string;
            if(string.charAt(0) == 45) {
                if(string.length() <= 1) {
                    return false;
                }

                var1 = string.substring(1, string.length() - 1);
            }

            for(int var2 = 0; var2 < var1.length(); ++var2) {
                if(!Character.isDigit(var1.charAt(var2))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String utf16le(byte[] data) {
        String var1 = "";
        if(data == null) {
            return var1;
        } else {
            try {
                var1 = new String(data, "UTF-16LE");
            } catch (Exception var3) {
                Log.w("", var3);
            }

            return var1;
        }
    }

    public static String utf16(byte[] data) {
        String var1 = "";

        try {
            var1 = new String(data, "UTF-16");
        } catch (Exception var3) {
            ;
        }

        return var1;
    }

    public static byte[] utf16leBuffer(String text) {
        byte[] var1 = null;

        try {
            var1 = text.getBytes("UTF-16LE");
        } catch (Exception var3) {
            ;
        }

        return var1;
    }

    public static String join(Iterable<?> elements, String delimiter) {
        StringBuilder var2 = new StringBuilder();

        Object var4;
        for(Iterator var3 = elements.iterator(); var3.hasNext(); var2.append(var4)) {
            var4 = var3.next();
            if(var2.length() > 0) {
                var2.append(delimiter);
            }
        }

        return var2.toString();
    }

    public static List<String> split(String string, String delimiter) {
        if(isNullOrEmpty(string)) {
            return new ArrayList();
        } else {
            String[] var2 = string.split(delimiter);
            return Arrays.asList(var2);
        }
    }

    public static String deleteNewlineSymbol(String content) {
        if(!isNullOrEmpty(content)) {
            content = content.replaceAll("\r\n", " ").replaceAll("\n", " ");
        }

        return content;
    }

    public static String leftTrim(String content) {
        int var1 = 0;

        int var2;
        for(var2 = content.length() - 1; var1 <= var2 && content.charAt(var1) <= 32; ++var1) {
            ;
        }

        return var1 == 0?content:content.substring(var1, var2 + 1);
    }

    public static String rightTrim(String content) {
        byte var1 = 0;
        int var2 = content.length() - 1;

        int var3;
        for(var3 = var2; var3 >= var1 && content.charAt(var3) <= 32; --var3) {
            ;
        }

        return var3 == var2?content:content.substring(var1, var3 + 1);
    }

    public static String substring(String content, int beginIndex, int endIndex) {
        if(isNullOrEmpty(content)) {
            return "";
        } else {
            int var3 = content.codePointCount(0, content.length());
            if(endIndex > var3) {
                return "";
            } else {
                StringBuilder var4 = new StringBuilder();

                for(int var5 = beginIndex; var5 < endIndex; ++var5) {
                    var4.appendCodePoint(content.codePointAt(var5));
                }

                return var4.toString();
            }
        }
    }

    public static String trim(String input) {
        if(isNotBlank(input)) {
            input = input.trim();
            input = input.replace("\u0000", "");
            input = input.replace("\\u0000", "");
            input = input.replaceAll("\\u0000", "");
            input = input.replaceAll("\\\\u0000", "");
        }

        return input;
    }

    public static String trimPunctuation(String input) {
        input = trim(input);
        if(isNullOrEmpty(input)) {
            return input;
        } else {
            int var1;
            for(var1 = 0; var1 < input.length() && punctuation.contains(String.valueOf(input.charAt(var1))); ++var1) {
                ;
            }

            if(var1 > input.length() - 1) {
                return "";
            } else {
                int var2;
                for(var2 = input.length() - 1; var2 > var1 && punctuation.contains(String.valueOf(input.charAt(var2))); --var2) {
                    ;
                }

                input = input.substring(var1, var2 + 1);
                return input;
            }
        }
    }

    public static boolean isAlpha(char ch) {
        return 65 <= ch && ch <= 122 || 192 <= ch && ch <= 214 || 216 <= ch && ch <= 246 || 248 <= ch && ch <= 255 || 256 <= ch && ch <= 383 || 384 <= ch && ch <= 591 || 902 == ch || 904 <= ch && ch <= 1023 || 1024 <= ch && ch <= 1153 || 1162 <= ch && ch <= 1279 || 1280 <= ch && ch <= 1327 || 7680 <= ch && ch <= 7935;
    }

    public static boolean isUrl(String url) {
        return !isNullOrEmpty(url) && Patterns.WEB_URL.matcher(url).matches();
    }

    public static String safelyGetStr(String origin) {
        return isNullOrEmpty(origin)?"":origin;
    }

    public static boolean safelyEquals(String firstStr, String secondStr) {
        return firstStr == null && secondStr == null?true:(firstStr != null && secondStr != null?firstStr.equals(secondStr):false);
    }

    public static boolean safelyContains(String src, String pattern) {
        return isNullOrEmpty(src)?false:src.contains(pattern);
    }

    public static int getLength(String origin) {
        return isNullOrEmpty(origin)?0:origin.length();
    }

    public static int getTextWidth(Paint paint, String str) {
        int var2 = 0;
        if(str != null && str.length() > 0) {
            int var3 = str.length();
            float[] var4 = new float[var3];
            paint.getTextWidths(str, var4);

            for(int var5 = 0; var5 < var3; ++var5) {
                var2 += (int)Math.ceil((double)var4[var5]);
            }
        }

        return var2;
    }

    public static boolean isChinese(char c) {
        UnicodeBlock var1 = UnicodeBlock.of(c);
        return var1 == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || var1 == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || var1 == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || var1 == UnicodeBlock.GENERAL_PUNCTUATION || var1 == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || var1 == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static boolean isChinese(String s) {
        if(isNullOrEmpty(s)) {
            return false;
        } else {
            for(int var1 = 0; var1 < s.length(); ++var1) {
                if(isChinese(s.charAt(var1))) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isAlpha(String s) {
        return isNullOrEmpty(s)?false:isAlpha(s.charAt(0));
    }

    public static boolean isEquals(String s1, String s2) {
        return !isNullOrEmpty(s1) && !isNullOrEmpty(s2)?s1.equals(s2):false;
    }

    public static String getBlankStr(String origin) {
        return isNullOrEmpty(origin)?"":origin;
    }

    public static String getHtmlFormatString(String content) {
        return content == null?null:content.replaceAll("\\<.*?>|\\n", "");
    }

    public static boolean isMatchCaseInsensitive(@NonNull String string, @NonNull String pattern) {
        String var2 = "(?i)" + pattern;
        return Pattern.compile(var2).matcher(string).find();
    }

    public static String readLine(String filename) throws IOException {
        BufferedReader var1 = new BufferedReader(new FileReader(filename), 256);

        String var2;
        try {
            var2 = var1.readLine();
        } finally {
            var1.close();
        }

        return var2;
    }

    public static int calculateSpaceNumForString(String str) {
        if(str == null) {
            return 0;
        } else if(str.contains(" ")) {
            String[] var1 = str.split(" ");
            return var1.length > 0?var1.length - 1:0;
        } else {
            return 0;
        }
    }

    public static String nonBlankValue(String value, String fallbackValue) {
        return isNotBlank(value)?value:fallbackValue;
    }

    public static boolean isPhoneNumber(String phone) {
        if(isNullOrEmpty(phone)) {
            return false;
        } else {
            String var1 = "^[0-9]{7,11}$";
            Pattern var2 = Pattern.compile(var1);
            Matcher var3 = var2.matcher(phone);
            return var3.matches();
        }
    }

    public static boolean isEmail(String email) {
        if(isNullOrEmpty(email)) {
            return false;
        } else {
            String var1 = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
            Pattern var2 = Pattern.compile(var1);
            Matcher var3 = var2.matcher(email);
            return var3.matches();
        }
    }

    public static String hidePartPhone(String phone) {
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String hidePartEmail(String email) {
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }

    public static int lastNumberOffset(String str) {
        if(isNullOrEmpty(str)) {
            return -1;
        } else {
            Pattern var1 = Pattern.compile(END_WIDTH_NUMBER_REGEX);
            Matcher var2 = var1.matcher(str);
            return !var2.find()?-1:var2.start();
        }
    }



    public static boolean parseBoolean(String s) {
        try {
            return Boolean.parseBoolean(s);
        } catch (Exception var2) {
            return false;
        }
    }

    public static String removeNewlineSymbol(String content) {
        if(isNotBlank(content)) {
            content = content.replaceAll("\r\n", "").replaceAll("\n", "");
        }

        return content;
    }
}
