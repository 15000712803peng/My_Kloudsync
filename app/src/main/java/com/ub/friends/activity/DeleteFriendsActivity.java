package com.ub.friends.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.ub.techexcel.database.CustomerDao;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;

public class DeleteFriendsActivity extends Activity implements OnClickListener {
	private TextView deleteFriendsTv;
	private RelativeLayout setbeizhu;
	private LinearLayout backall;
	private String friendsId;
	private CustomerDao customerDao;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConfig.LOAD_FINISH:
				// 删除聊天记录
				RongIMClient.getInstance().removeConversation(
						ConversationType.PRIVATE, friendsId,
						new ResultCallback<Boolean>() {
							@Override
							public void onError(ErrorCode arg0) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(Boolean arg0) {
//								Toast.makeText(DeleteFriendsActivity.this,
//										"好友刪除成功", Toast.LENGTH_LONG).show();
								AppConfig.isUpdateDialogue = true;
								AppConfig.isUpdateCustomer = true;
								customerDao.deleteFriends(friendsId);
//								MemberDetail.instance.finish();
								finish();
							}
						});

				break;
			case AppConfig.FAILED:
//				Toast.makeText(DeleteFriendsActivity.this, "好友刪除失败",
//						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deletefriend);
		friendsId = getIntent().getStringExtra("RongID");
		customerDao = new CustomerDao(DeleteFriendsActivity.this);
		initView();

//		17829752114
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("DeleteFriendsActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("DeleteFriendsActivity");
		MobclickAgent.onPause(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		setbeizhu = (RelativeLayout) findViewById(R.id.setbeizhu);
		deleteFriendsTv = (TextView) findViewById(R.id.deletefriends);
		backall = (LinearLayout) findViewById(R.id.backll);
		setbeizhu.setOnClickListener(this);
		deleteFriendsTv.setOnClickListener(this);
		backall.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setbeizhu:

			break;
		case R.id.deletefriends:
			Dialog(DeleteFriendsActivity.this);
			break;
		case R.id.backll:
			finish();
			break;

		default:
			break;
		}

	}

	private AlertDialog dialog;

	public void Dialog(Context context) {

		final LayoutInflater inflater = LayoutInflater.from(context);
		View windov = inflater.inflate(R.layout.delete_dialog, null);
		windov.findViewById(R.id.no).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		windov.findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                new ApiTask(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = ConnectService
									.getIncidentbyHttpGet(AppConfig.URL_PUBLIC
											+ "Friend/DeleteFriend?friendID="
											+ friendsId);
							Log.e("deletefriends", jsonObject.toString());
							if (jsonObject.getInt("RetCode") == 0) {
								handler.obtainMessage(AppConfig.LOAD_FINISH)
										.sendToTarget();
							} else {
								handler.obtainMessage(AppConfig.FALSE)
										.sendToTarget();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
                }).start(((App) getApplication()).getThreadMgr());
				dialog.dismiss();
			}
		});
		dialog = new AlertDialog.Builder(context).show();
		Window dialogWindow = dialog.getWindow();
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		p.width = (int) (d.getWidth() * 0.8);
		dialogWindow.setAttributes(p);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(windov);
	}

}
