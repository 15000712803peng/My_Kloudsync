package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

public class DensityUtil {

    private DensityUtil() {
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 获取屏幕宽高
     */
    public static int[] getWindowMsg(Context context) {
        int[] wh = new int[2];
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wh[0] = wm.getDefaultDisplay().getWidth();
        wh[1] = wm.getDefaultDisplay().getHeight();
        Log.d("vivi", "屏幕宽 =" + wh[0]);
        Log.d("vivi", "屏幕高 =" + wh[1]);
        return wh;
    }


}
