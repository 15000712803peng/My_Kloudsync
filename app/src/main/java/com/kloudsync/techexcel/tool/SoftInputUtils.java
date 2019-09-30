package com.kloudsync.techexcel.tool;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by pingfan on 2018/2/27.
 */

public class SoftInputUtils {
    /**
     * 显示软键盘，Dialog使用
     *
     * @param activity 当前Activity
     */
    public static void showSoftInput(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    /**
     * 显示软键盘，Dialog使用
     *
     * @param activity 当前Activity
     */
    public static void showSoftInput(Activity activity, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view,InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏软键盘
     *
     * @param activity 当前Activity
     */
    public static void hideSoftInput(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getWindow().getDecorView().getWindowToken(), 0);
    }
}
