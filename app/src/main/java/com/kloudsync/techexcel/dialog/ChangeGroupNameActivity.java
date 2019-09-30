package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.GroupMessage;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.GroupInfo;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;

public class ChangeGroupNameActivity extends Activity {
	
	private TextView tv_back, tv_show;
	private EditText et_name;
	
	private GroupInfo gi;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case AppConfig.UPDATE_GROUPNAME:
				String result = (String) msg.obj;
				UpdateGroupNameSuccess();
				break;
			case AppConfig.NO_NETWORK:
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(R.string.No_networking),
						1000).show();
				
				break;
			case AppConfig.NETERROR:
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(R.string.NETWORK_ERROR),
						1000).show();
				
				break;
			case AppConfig.FAILED:
				result = (String) msg.obj;
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_LONG).show();
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
		setContentView(R.layout.activity_changegroupname);

		gi = (GroupInfo) getIntent().getSerializableExtra("groupinfo");
		
		initView();
	}

	private void initView() {
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_show = (TextView) findViewById(R.id.tv_show);
		et_name = (EditText) findViewById(R.id.et_name);
		
		if(null == gi.getGroupName() || gi.getGroupName().length() < 1 || gi.getGroupName().equals("null")){
			
		}else{
			et_name.setText(gi.getGroupName());
		}
		
		tv_back.setOnClickListener(new MyOnClick());
		tv_show.setOnClickListener(new MyOnClick());
		
	}
	
	protected class MyOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_show:
				JudgeIsUpdate();
				break;

			default:
				break;
			}
			
		}

		
	}

	private void JudgeIsUpdate() {
		String name = et_name.getText().toString();
		if(null == name || name.length() < 1){
			Toast.makeText(getApplicationContext(), "群名称不能为空",
					Toast.LENGTH_SHORT).show();
		} else {
			UpdateGroupName();
		}
	}
	public void UpdateGroupName() {
		final JSONObject jsonObject = formate();
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC
									+ "ChatGroup/UpdateGroup", jsonObject);
					Log.e("返回的jsonObject", jsonObject.toString() + "");
					Log.e("返回的responsedata", responsedata.toString() + "");
					String retcode = responsedata.getString("RetCode");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						msg.what = AppConfig.UPDATE_GROUPNAME;	
						msg.obj = responsedata.toString();
					}else{
						msg.what = AppConfig.FAILED;
						String ErrorMessage = responsedata.getString("ErrorMessage");
						msg.obj = ErrorMessage;
					}
					handler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start(ThreadManager.getManager());
		
	}

	private JSONObject formate() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("GroupID", gi.getGroupID());
			jsonObject.put("GroupName", et_name.getText().toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject;
	}
	

	private void UpdateGroupNameSuccess() {
		ConversationActivity.instance.finish();
		ChatDetailActivity.instance.finish();
		
		if (gi.getGroupAdminID().equals(AppConfig.RongUserID)) {
			AppConfig.isChangeGroupName = true;
			AppConfig.UPDATEGROUP = new Group(gi.getGroupID(), et_name
					.getText().toString(), null);
		}
		
		GroupMessage gmsg = new GroupMessage(AppConfig.UserName + "修改了群名称");
		RongIM.getInstance()
		.getRongIMClient()
		.sendMessage(Conversation.ConversationType.GROUP, gi.getGroupID(),
				gmsg, "", "",
				new RongIMClient.SendMessageCallback() {
					@Override
					public void onError(Integer messageId,
							RongIMClient.ErrorCode e) {
						Log.e("lalala", "sendMessage onError");
					}

					@Override
					public void onSuccess(Integer integer) {
						Log.e("lalala", "sendMessage onSuccess");

					}
				});
		
		
		RongIM.getInstance().startGroupChat(
				ChangeGroupNameActivity.this, gi.getGroupID(),
				et_name.getText().toString());
		finish();
	}

}
