package com.kloudsync.techexcel.personal;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.help.EverPenManger;
import com.kloudsync.techexcel.help.PenDotTool;
import com.kloudsync.techexcel.mvp.BaseActivity;
import com.kloudsync.techexcel.mvp.presenter.CurrentNotePresenter;
import com.kloudsync.techexcel.mvp.view.CurrentNoteView;
import com.kloudsync.techexcel.ui.DrawView;
import com.tqltech.tqlpencomm.Dot;
import com.ub.techexcel.tools.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.Bind;

import static com.kloudsync.techexcel.help.DotConstants.BG_REAL_HEIGHT;
import static com.kloudsync.techexcel.help.DotConstants.BG_REAL_WIDTH;

public class CurrentNoteActivity extends BaseActivity<CurrentNotePresenter> implements CurrentNoteView {
	private static final String TAG = CurrentNoteActivity.class.getSimpleName();
	public static final String CURRENTPAGE = "currentpage";
	@Bind(R.id.iv_titlebar_back)
	ImageView mIvTitlebarBack;
	@Bind(R.id.tv_titlebar_title)
	TextView mTvTitlebarTitle;
	@Bind(R.id.rly_current_note_draw)
	RelativeLayout mRlyCurrentNoteDraw;
	@Bind(R.id.iv_current_note_draw)
	ImageView mIvCurrentNoteDraw;
	private EverPenManger mEverPenManger;
	private DrawView mCanvasImage;
	private boolean mHasMeasured;
	private int BG_WIDTH;                                    //显示背景图宽
	private int BG_HEIGHT;                                   //显示背景图高
	private int gcontentLeft;                                //内容显示区域left坐标
	private int gcontentTop;                                 //内容显示区域top坐标
	private int A5_X_OFFSET;                                 //笔迹X轴偏移量
	private int A5_Y_OFFSET;                                 //笔迹Y轴偏移量
	public static float mWidth;                              //屏幕宽
	public static float mHeight;                             //屏幕高
	private boolean isNotchScreen = false;     //刘海屏标记
	private static int notchscreenWidth;        //刘海宽
	private static int notchscreenHeight;       //刘海高

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_current_note;
	}

	@Override
	protected void initPresenter() {
		mPresenter = new CurrentNotePresenter();
	}

	@Override
	protected void initView() {
		isNotchScreen = hasNotchScreen(this);
		mCanvasImage = new DrawView(this);
		mEverPenManger = EverPenManger.getInstance(this);
		mEverPenManger.addListener(mPresenter);
		String title = getIntent().getStringExtra(CURRENTPAGE);
		mTvTitlebarTitle.setText(title);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;
		ViewGroup.LayoutParams param = mRlyCurrentNoteDraw.getLayoutParams();
		mRlyCurrentNoteDraw.addView(mCanvasImage, param);
		mIvCurrentNoteDraw.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (mHasMeasured == false) {
					mHasMeasured = true;
					//计算
					float ratio = 0.95f;
					ratio = (ratio * mWidth) / BG_REAL_WIDTH;
					BG_HEIGHT = (int) (BG_REAL_HEIGHT * ratio);
					if (BG_HEIGHT > (mHeight - 100)) {
						ratio = 0.90f;
						ratio = (ratio * mHeight) / BG_REAL_HEIGHT;

						BG_HEIGHT = (int) (BG_REAL_HEIGHT * ratio);
					}
					BG_WIDTH = (int) (BG_REAL_WIDTH * ratio);

					ViewGroup.LayoutParams para = mIvCurrentNoteDraw.getLayoutParams();
					para.width = BG_WIDTH;
					para.height = BG_HEIGHT;
					mIvCurrentNoteDraw.setLayoutParams(para);

					gcontentLeft = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getLeft();
					gcontentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

					int statusHeight = getStatusBarHeight();
					int statusHeight2 = getStatusBarHeight(CurrentNoteActivity.this);

					Log.i(TAG, "onGlobalLayout: statusHeight=" + statusHeight + ",statusHeight2=" + statusHeight2);
					Log.i(TAG, "onGlobalLayout: mHeight=" + mHeight + ",mWidth=" + mWidth);
					Log.i(TAG, "onGlobalLayout: gcontentTop=" + gcontentTop + ",gcontentLeft=" + gcontentLeft);
					Log.i(TAG, "onGlobalLayout: BG_HEIGHT=" + BG_HEIGHT + ",BG_WIDTH=" + BG_WIDTH);
					A5_X_OFFSET = (int) (mWidth - gcontentLeft - BG_WIDTH) / 2;
					if (isNotchScreen) {
						A5_Y_OFFSET = (int) (mHeight - gcontentTop - BG_HEIGHT + notchscreenHeight) / 2 /*- 12*/;
					} else {
						A5_Y_OFFSET = (int) (mHeight - gcontentTop - BG_HEIGHT) / 2;
					}
					PenDotTool.setData(BG_WIDTH, BG_HEIGHT, A5_X_OFFSET, A5_Y_OFFSET);
				}
			}
		});
	}

	@Override
	protected void initListener() {
		mIvTitlebarBack.setOnClickListener(this);
	}

	public static int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	private int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			Log.i(TAG, "get status bar height fail");
			e1.printStackTrace();
			return 75;
		}

	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_titlebar_back:
				finish();
				break;
		}
	}

	@Override
	public void onReceiveDot(Dot dot) {
		PenDotTool.processEachDot(dot, mCanvasImage);
	}

	@Override
	public void onReceiveOfflineStrokes(Dot dot) {
		PenDotTool.processEachDot(dot, mCanvasImage);
	}

	@Override
	public void toast(String msg) {
		super.toast(msg);
	}

	@Override
	public void showLoading() {
		super.showLoading();
	}

	@Override
	public void dismissLoading() {
		super.dismissLoading();
	}

	@Override
	protected void onDestroy() {
		mHasMeasured = false;
		mEverPenManger.removeListener(mPresenter);
		mCanvasImage.DrawDestroy();
		super.onDestroy();
	}

	/**
	 * 判断是否是刘海屏
	 *
	 * @return
	 */
	public static boolean hasNotchScreen(Activity activity) {
		boolean isHW = hasNotchAtHuawei(activity);
		boolean isOPPO = hasNotchAtOPPO(activity);
		boolean isVIVO = hasNotchAtVivo(activity);
		if (getInt("ro.miui.notch", activity) == 1
				|| isHW
				|| isOPPO
				|| isVIVO) {
			//TODO 各种品牌
			Log.i(TAG, "hasNotchScreen: 刘海屏系统");
			//华为
			if (isHW) {
				int[] huawei = getNotchSizeAtHuawei(activity);
				notchscreenWidth = huawei[0];
				notchscreenHeight = huawei[1];
			} else if (isOPPO) {
				notchscreenWidth = Tools.dip2px(activity, 100);
				notchscreenHeight = Tools.dip2px(activity, 27);
			} else if (isVIVO) {
				notchscreenWidth = 324;
				notchscreenHeight = 80;
			} else {

			}
			//vivo不提供接口获取刘海尺寸，目前vivo的刘海宽为100dp,高为27dp。
			//OPPO不提供接口获取刘海尺寸，目前其有刘海屏的机型尺寸规格都是统一的。不排除以后机型会有变化。
			//其显示屏宽度为1080px，高度为2280px。刘海区域则都是宽度为324px, 高度为80px。
			//小米的状态栏高度会略高于刘海屏的高度，因此可以通过获取状态栏的高度来间接避开刘海屏

			Log.i(TAG, "hasNotchScreen:  width=" + notchscreenWidth + ",height=" + notchscreenHeight);
			return true;
		}
		Log.i(TAG, "hasNotchScreen: 非刘海屏系统");
		return false;
	}

	/**
	 * 小米刘海屏判断.
	 *
	 * @return 0 if it is not notch ; return 1 means notch * @throws IllegalArgumentException if the key exceeds 32 characters
	 */
	public static int getInt(String key, Activity activity) {
		int result = 0;
		if (isXiaomi()) {
			try {
				ClassLoader classLoader = activity.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
				@SuppressWarnings("rawtypes")
				Class[] paramTypes = new Class[2];
				paramTypes[0] = String.class;
				paramTypes[1] = int.class;
				Method getInt = SystemProperties.getMethod("getInt", paramTypes);
				//参数
				Object[] params = new Object[2];
				params[0] = new String(key);
				params[1] = new Integer(0);
				result = (Integer) getInt.invoke(SystemProperties, params);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return result;
	}


	// 是否是小米手机
	public static boolean isXiaomi() {
		return "Xiaomi".equals(Build.MANUFACTURER);
	}

	/**
	 * 华为刘海屏判断
	 *
	 * @return
	 */
	public static boolean hasNotchAtHuawei(Context context) {
		boolean ret = false;
		try {
			ClassLoader classLoader = context.getClassLoader();
			Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
			Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
			ret = (boolean) get.invoke(HwNotchSizeUtil);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "hasNotchAtHuawei ClassNotFoundException");
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "hasNotchAtHuawei NoSuchMethodException");
		} catch (Exception e) {
			Log.e(TAG, "hasNotchAtHuawei Exception");
		} finally {
			return ret;
		}
	}

	public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
	public static final int VIVO_FILLET = 0x00000008;//是否有圆角

	/**
	 * VIVO刘海屏判断
	 *
	 * @return
	 */
	public static boolean hasNotchAtVivo(Context context) {
		boolean ret = false;
		try {
			ClassLoader classLoader = context.getClassLoader();
			Class FtFeature = classLoader.loadClass("android.util.FtFeature");
			Method method = FtFeature.getMethod("isFeatureSupport", int.class);
			ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "hasNotchAtVivo ClassNotFoundException");
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "hasNotchAtVivo NoSuchMethodException");
		} catch (Exception e) {
			Log.e(TAG, "hasNotchAtVivo Exception");
		} finally {
			return ret;
		}
	}

	/**
	 * OPPO刘海屏判断
	 *
	 * @return
	 */
	public static boolean hasNotchAtOPPO(Context context) {
		return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
	}


	//获取刘海尺寸：width、height
	//int[0]值为刘海宽度 int[1]值为刘海高度
	public static int[] getNotchSizeAtHuawei(Context context) {
		int[] ret = new int[]{0, 0};
		try {
			ClassLoader cl = context.getClassLoader();
			Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
			Method get = HwNotchSizeUtil.getMethod("getNotchSize");
			ret = (int[]) get.invoke(HwNotchSizeUtil);
		} catch (ClassNotFoundException e) {
			Log.e("Notch", "getNotchSizeAtHuawei ClassNotFoundException");
		} catch (NoSuchMethodException e) {
			Log.e("Notch", "getNotchSizeAtHuawei NoSuchMethodException");
		} catch (Exception e) {
			Log.e("Notch", "getNotchSizeAtHuawei Exception");
		} finally {
			return ret;
		}
	}
}
