package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EventDeleteDocs;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class DocAndMeetingFileListPopupwindow implements View.OnClickListener {

	private Context mContext;
	private PopupWindow mPpw;
	private View mView;
	private MeetingDocument mMeetingDocument;
	private Dialog mDialog;
	private MeetingConfig mMeetingConfig;
	private RelativeLayout mDocMoreDelete;
	private TextView mDocMoreDivider;

	public DocAndMeetingFileListPopupwindow(Context context) {
		mContext = context;
		initPopupwindow();
	}

	private void initPopupwindow() {
		mView = LayoutInflater.from(mContext).inflate(R.layout.ppw_doc_file_more, null);
		RelativeLayout docMoreSaveTeamSpace = mView.findViewById(R.id.ppw_doc_more_save_team_space);
		mDocMoreDelete = mView.findViewById(R.id.ppw_doc_more_delete);
		mDocMoreDivider = mView.findViewById(R.id.tv_ppw_doc_more_divider);
		docMoreSaveTeamSpace.setOnClickListener(this);
		mDocMoreDelete.setOnClickListener(this);
		mPpw = new PopupWindow(mView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mPpw.setBackgroundDrawable(new ColorDrawable());
		mPpw.setOutsideTouchable(true);
		mPpw.setFocusable(true);
	}

	public void show(final View view, MeetingDocument document, MeetingConfig meetingConfig) {
		if (mPpw != null) {
			mMeetingDocument = document;
			mMeetingConfig = meetingConfig;
			if (!mMeetingConfig.getPresenterId().equals(AppConfig.UserID)) {
				mDocMoreDelete.setVisibility(View.GONE);
				mDocMoreDivider.setVisibility(View.GONE);
			} else {
				mDocMoreDelete.setVisibility(View.VISIBLE);
				mDocMoreDivider.setVisibility(View.VISIBLE);
			}
			mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			int popupWidth = mView.getMeasuredWidth();    //  获取测量后的宽度
			int popupHeight = mView.getMeasuredHeight();  //获取测量后的高度
			int viewWidth = view.getMeasuredWidth();    //  获取测量后的宽度
			int viewHeight = view.getMeasuredHeight();  //获取测量后的高度
			mPpw.showAsDropDown(view, -popupWidth, -popupHeight + viewHeight / 2);

		}
	}


	public void dismissPpw() {
		if (mPpw == null) return;
		mPpw.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ppw_doc_more_save_team_space://保存至团队空间
				break;
			case R.id.ppw_doc_more_delete://删除
				deleteDocument(mMeetingDocument);
				break;
		}
		dismissPpw();
	}

	public void deleteDocument(final MeetingDocument meetingDocument) {
		final LayoutInflater inflater = LayoutInflater
				.from(mContext);
		View windov = inflater.inflate(R.layout.deletedocument_dialog, null);
		windov.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
			}
		});
		windov.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new ApiTask(new Runnable() {
					@Override
					public void run() {
						JSONObject jsonObject = ConnectService.getIncidentDataattachment(AppConfig.URL_PUBLIC + "EventAttachment/RemoveAttachments?itemIDs=" + meetingDocument.getItemID());
						try {
							Log.e("dddddd", AppConfig.URL_PUBLIC + "EventAttachment/RemoveAttachments?itemIDs=" + meetingDocument.getItemID() + jsonObject.toString());
							int retcode = jsonObject.getInt("RetCode");
							switch (retcode) {
								case 0:
									EventDeleteDocs eventDeleteDocs = new EventDeleteDocs();
									eventDeleteDocs.setItemId(meetingDocument.getItemID());
									EventBus.getDefault().post(eventDeleteDocs);
									break;

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}).start(((App) ((Activity) mContext).getApplication()).getThreadMgr());
				mDialog.dismiss();
			}
		});
		mDialog = new Dialog(mContext, R.style.my_dialog);
		mDialog.setContentView(windov);
		mDialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		p.width = LinearLayout.LayoutParams.MATCH_PARENT;
		dialogWindow.setAttributes(p);
		mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mDialog.show();
	}

}
