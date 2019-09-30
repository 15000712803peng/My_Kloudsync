package com.kloudsync.techexcel.contact;

import android.app.Activity;
import android.content.Intent;
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
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeRemarkActivity extends Activity {
	
	private TextView tv_back, tv_sure;
	private EditText et_name;
	
	String remark;
	String userId;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case AppConfig.CHANGE_REMARK:
				String result = (String) msg.obj;
				if(result != null){
					ChangeSuccess();
				}else{
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(R.string.No_Data),
							Toast.LENGTH_LONG).show();
				}
				
				break;
			case AppConfig.NO_NETWORK:
				Toast.makeText(
						ChangeRemarkActivity.this,
						getResources().getString(R.string.No_networking),
						Toast.LENGTH_LONG).show();
				
				break;
			case AppConfig.NETERROR:
				Toast.makeText(
						ChangeRemarkActivity.this,
						getResources().getString(R.string.NETWORK_ERROR),
						Toast.LENGTH_LONG).show();
				
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
	

	private void ChangeSuccess() {
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(R.string.Change_Remark_Success),
				Toast.LENGTH_LONG).show();
		UserDetail.instance.finish();
		Intent intent = new Intent(ChangeRemarkActivity.this, UserDetail.class);
		intent.putExtra("UserID", userId);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changeremark);
		remark = getIntent().getStringExtra("remark");
		userId = getIntent().getStringExtra("userId");
		
		initView();
	}
	
	private void initView() {
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_sure = (TextView) findViewById(R.id.tv_sure);
		et_name = (EditText) findViewById(R.id.et_name);
		
		et_name.setText(remark);
		
		
		tv_back.setOnClickListener(new MyOnClick());
		tv_sure.setOnClickListener(new MyOnClick());
		
	}
	
	protected class MyOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_sure:
				ChangeRemark();
				break;

			default:
				break;
			}
			
		}

		
	}

	public void ChangeRemark() {
		final JSONObject jsonObject = formatpc();
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC
									+ "User/UpdateCustomerPersonalComment", jsonObject);
					Log.e("返回的jsonObject", jsonObject.toString() + "");
					Log.e("UpdateCustomerPersonalComment", responsedata.toString() + "");
					String retcode = responsedata.getString("RetCode");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						msg.what = AppConfig.CHANGE_REMARK;
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
	
	private JSONObject formatpc() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("UserID", userId);
			jsonObject.put("PersonalComment", et_name.getText().toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject;
	}
}
