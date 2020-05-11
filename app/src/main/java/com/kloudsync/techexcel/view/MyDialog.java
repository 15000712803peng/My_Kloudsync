package com.kloudsync.techexcel.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MyDialog extends Dialog {

	public MyDialog(@NonNull Context context) {
		super(context);
	}

	public MyDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected MyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	public void dismiss() {
		View view = getCurrentFocus();
		InputMethodManager im = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null && im.isActive()) {
			im.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		super.dismiss();
	}
}
