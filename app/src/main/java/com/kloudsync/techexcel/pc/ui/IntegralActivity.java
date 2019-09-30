package com.kloudsync.techexcel.pc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.pc.adapter.IntegralAdapter;
import com.kloudsync.techexcel.pc.bean.IntegralDetails;
import com.kloudsync.techexcel.view.CountView;
import com.kloudsync.techexcel.view.RoundProgressBar;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IntegralActivity extends Activity {

	private RoundProgressBar mRoundProgressBar;
	private int progress = 0;
	private int integralNumber;
	private CountView tv_pc_integral_number;
	private TextView tv_name;
	private TextView tv_back;
	private ListView lv_pc_integral;

	private String memberPoints;
	
	long count;
	int maxValue = 10000;
	int maxtime = 1500;
	private List<IntegralDetails> integralList;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConfig.PASSWORDSUCCESS:
				Log.e("size", integralList.size()+"yy");
				IntegralAdapter adapter = new IntegralAdapter(getApplicationContext(), integralList);
				lv_pc_integral.setAdapter(adapter);
				break;
			default:
				break;
			}
		};
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_integral);
		
		initView();
		getIntegral();
		LoadIntegral();


	}
	private void getIntegral() {
		// TODO Auto-generated method stub
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				JSONObject jsonObject = ConnectService
						.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "User/UserPointList");
				Log.e("dk", jsonObject.toString());
				formatIntegral(jsonObject);
			}

		}).start(ThreadManager.getManager());
	}
	

	private void formatIntegral(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			int retCode = jsonObject.getInt("RetCode");
			String error=jsonObject.getString("ErrorMessage");
			JSONArray  dIntegral = jsonObject.getJSONArray("RetData");
			switch (retCode) {
			case 0:
				integralList = new ArrayList<IntegralDetails>();
				for (int i = 0; i < dIntegral.length(); i++) {
					IntegralDetails integralDetails = new IntegralDetails();
					String changeType = dIntegral.getJSONObject(i).getString("ChangeType");
					String changeTypeName = dIntegral.getJSONObject(i).getString("ChangeTypeName");
					String pointValue = dIntegral.getJSONObject(i).getString("PointValue");
					String changeDate = dIntegral.getJSONObject(i).getString("ChangeDate");
					String changeLog = dIntegral.getJSONObject(i).getString("ChangeLog");
					
					integralDetails.setChangeType(changeType);
					integralDetails.setChangeTypeName(changeTypeName);
					integralDetails.setPointValue(pointValue);
					integralDetails.setChangeDate(changeDate);
					integralDetails.setChangeLog(changeLog);
					
					integralList.add(integralDetails);
				}
				Message message1 = handler
						.obtainMessage(AppConfig.PASSWORDSUCCESS);
				message1.sendToTarget();
				break;
			case -1500:
				Toast.makeText(getApplicationContext(), error, 100).show();
				break;
			case -1401:
				Toast.makeText(getApplicationContext(), error, 100).show();
				break;	
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	 
	private void LoadIntegral() {
		// TODO Auto-generated method stub
		

			final int step = integralNumber / maxtime + 1;
		new ApiTask(new Runnable() {
				@Override
				public void run() {
					while (progress <= integralNumber && progress <= maxValue) {
						mRoundProgressBar.setProgress(progress);
						progress += step;
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
		}).start(ThreadManager.getManager());

		
	}

	private void initView() {
		mRoundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
		mRoundProgressBar.setMax(maxValue);
		tv_pc_integral_number = (CountView) findViewById(R.id.tv_pc_integral_number);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_back = (TextView) findViewById(R.id.tv_back);
		lv_pc_integral = (ListView) findViewById(R.id.lv_pc_integral);

		tv_name.setText(R.string.integral_title);
		
		memberPoints = getIntent().getStringExtra("memberPoints");
		//Log.e("dk", memberPoints);
		integralNumber = Integer.parseInt(memberPoints);
		
		//数字滚动控件
		tv_pc_integral_number.showNumberWithAnimation(integralNumber);

		tv_back.setOnClickListener(new myOnClick());
	}

	private class myOnClick implements OnClickListener {
		Intent intent = new Intent();

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			default:
				break;
			}

		}

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("IntegralActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("IntegralActivity");
		MobclickAgent.onPause(this);
	}
}