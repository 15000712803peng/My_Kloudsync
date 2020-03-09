package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.kloudsync.techexcel.view.MyDialog;

public class CreateFolderDialog implements View.OnClickListener {
	private static CreateFolderDialog mCreateFolderDialog;
	private Activity mActivity;
	private MyDialog mDialog;
	private EditText mEtFolderName;

	public static CreateFolderDialog instance(Activity activity) {
		if (mCreateFolderDialog == null) {
			synchronized (CreateFolderDialog.class) {
				if (mCreateFolderDialog == null) {
					mCreateFolderDialog = new CreateFolderDialog(activity);
				}
			}
		}
		return mCreateFolderDialog;
	}

	public void destory() {
		mCreateFolderDialog = null;
	}

	public CreateFolderDialog(Activity context) {
		mActivity = context;
	}

	public void showDialog(int titleResId, int hintResId, int maxInputLength) {
		mDialog = new MyDialog(mActivity, R.style.my_dialog);
		View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_create_folder, null);
		TextView dialogTitle = view.findViewById(R.id.tv_dialog_title);
		mEtFolderName = view.findViewById(R.id.et_dialog_folder_name);
		Button btnCancel = view.findViewById(R.id.btn_dialog_cancel);
		Button btnOk = view.findViewById(R.id.btn_dialog_ok);
		dialogTitle.setText(titleResId);
		mEtFolderName.setHint(hintResId);
		if (maxInputLength != 0) {
			mEtFolderName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxInputLength)});
		}
		btnCancel.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		mDialog.setContentView(view);
		mDialog.show();
	}


	private void dismissDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_dialog_cancel:
				dismissDialog();
				break;
			case R.id.btn_dialog_ok:
				String folderName = mEtFolderName.getText().toString().trim();
				if (!TextUtils.isEmpty(folderName)) {
					mCallback.createFolder(folderName);
					dismissDialog();
				} else {
					ToastUtils.show(mActivity, mActivity.getResources().getString(R.string.name_cannot_be_empty));
				}
				break;
		}
	}

	private CreateFolderCallback mCallback;

	public interface CreateFolderCallback {
		void createFolder(String folderName);
	}

	public void setCreateFolderCallback(CreateFolderCallback callback) {
		mCallback = callback;
	}
}
