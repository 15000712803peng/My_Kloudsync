package com.ub.friends.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.AddFriend;
import com.ub.techexcel.database.CustomerDao;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class AddVerificationActivity extends Activity implements
		OnClickListener {

	private TextView send;
	private EditText editText;
	private LinearLayout back;
	private AddFriend addfriend = new AddFriend();
	private static CustomerDao customerDao;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConfig.LOAD_FINISH:
				Toast.makeText(getApplicationContext(), "好友请求已发出", 1).show();
				if (getIntent().getBooleanExtra("isdetail", false)) {
					FriendsDetailActivity.instance.finish();
				}
				addfriend.setType("3");
				addfriend.setSourceID(AppConfig.RongUserID);
				Log.e("好友请求已发出",
						addfriend.getSourceID() + "   "
								+ addfriend.getTargetID());
				// 将词条记录插入数据库
				customerDao.insert(addfriend, false);
				AppConfig.isonresuce = true;
				finish();
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
		setContentView(R.layout.addverification);
		customerDao = new CustomerDao(this);
		addfriend = (AddFriend) getIntent().getSerializableExtra("addfriends");
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		send = (TextView) findViewById(R.id.send);
		send.setOnClickListener(this);
		back = (LinearLayout) findViewById(R.id.backll);
		back.setOnClickListener(this);
		editText = (EditText) findViewById(R.id.vercontent);
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("AddVerificationActivity"  ); 
	    MobclickAgent.onResume(this);       //统计时长 
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("AddVerificationActivity");
	    MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.backll:
			finish();
			break;
		case R.id.send:
			sendApplyFriend();
			break;

		default:
			break;
		}
	}

	private void sendApplyFriend() {
		// TODO Auto-generated method stub
        new ApiTask(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				JSONObject json = format();
				Log.e("aaaa", json.toString());
				JSONObject jsonObject = ConnectService.submitDataByJson(
						AppConfig.URL_PUBLIC + "Friend/ApplyFriend", json);
				Log.e("dddd", jsonObject.toString());
				formatReturnjson(jsonObject);

			}

        }).start(ThreadManager.getManager());
	}

	private void formatReturnjson(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			int retCode = jsonObject.getInt("RetCode");
			switch (retCode) {
			case AppConfig.RETCODE_SUCCESS:
				handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
				break;

			default:
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private JSONObject format() {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("TargetID", addfriend.getTargetID());
			jsonObject.put("UserID", addfriend.getUserID());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
	}
}
