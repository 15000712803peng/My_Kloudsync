package com.kloudsync.techexcel.personal;

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
	public static float mWidth;                              //屏幕宽
	public static float mHeight;                             //屏幕高
	private boolean isNotchScreen = false;     //刘海屏标记


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
		isNotchScreen = PenDotTool.hasNotchScreen(this);
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
					int BG_HEIGHT = (int) (BG_REAL_HEIGHT * ratio);//显示背景图高
					if (BG_HEIGHT > (mHeight - 100)) {
						ratio = 0.90f;
						ratio = (ratio * mHeight) / BG_REAL_HEIGHT;

						BG_HEIGHT = (int) (BG_REAL_HEIGHT * ratio);
					}
					int BG_WIDTH = (int) (BG_REAL_WIDTH * ratio);//显示背景图宽

					ViewGroup.LayoutParams para = mIvCurrentNoteDraw.getLayoutParams();
					para.width = BG_WIDTH;
					para.height = BG_HEIGHT;
					mIvCurrentNoteDraw.setLayoutParams(para);

					int gcontentLeft = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getLeft(); //内容显示区域left坐标
					int gcontentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();//内容显示区域top坐标

					int statusHeight = PenDotTool.getStatusBarHeightClass(CurrentNoteActivity.this);
					int statusHeight2 = PenDotTool.getStatusBarHeight(CurrentNoteActivity.this);

					Log.i(TAG, "onGlobalLayout: statusHeight=" + statusHeight + ",statusHeight2=" + statusHeight2);
					Log.i(TAG, "onGlobalLayout: mHeight=" + mHeight + ",mWidth=" + mWidth);
					Log.i(TAG, "onGlobalLayout: gcontentTop=" + gcontentTop + ",gcontentLeft=" + gcontentLeft);
					Log.i(TAG, "onGlobalLayout: BG_HEIGHT=" + BG_HEIGHT + ",BG_WIDTH=" + BG_WIDTH);
					int A5_X_OFFSET = (int) (mWidth - gcontentLeft - BG_WIDTH) / 2;//笔迹X轴偏移量
					int A5_Y_OFFSET; //笔迹Y轴偏移量
					if (isNotchScreen) {
						A5_Y_OFFSET = (int) (mHeight - gcontentTop - BG_HEIGHT + PenDotTool.getNotchscreenHeight()) / 2 /*- 12*/;
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


}
