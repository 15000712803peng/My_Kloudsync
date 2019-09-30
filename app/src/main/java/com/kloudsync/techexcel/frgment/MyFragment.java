package com.kloudsync.techexcel.frgment;

import android.support.v4.app.Fragment;

public abstract class MyFragment extends Fragment {

	protected boolean isVisible;

	// 表示找控件完成, 给控件们设置数据不会报空指针了
	protected boolean isPrepared = false;
	// 表示是否已经请求过数据
	protected boolean isLoadDataFinish = false;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	protected void onVisible() {
		lazyLoad();
	}

	protected abstract void lazyLoad();

	protected void onInvisible() {
	}

}
