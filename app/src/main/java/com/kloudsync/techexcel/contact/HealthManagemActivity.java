package com.kloudsync.techexcel.contact;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.user.techexcel.pi.tools.HealthListAdapter;
import com.kloudsync.user.techexcel.pi.tools.HealthListAdapter.OnTaskStateChangedListener;
import com.kloudsync.user.techexcel.pi.tools.TaskBean;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HealthManagemActivity extends Activity {

	private LinearLayout img_back;
	private TextView tv_name;
	private ListView pi_health_tasklist;
	private List<TaskBean> list = new ArrayList<TaskBean>();
	private HealthListAdapter adapter;
	private String UserID;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x11:
				adapter = new HealthListAdapter(HealthManagemActivity.this,
						list);
				pi_health_tasklist.setAdapter(adapter);
				adapter.setOnTaskStateChangedListener(new OnTaskStateChangedListener() {

					@Override
					public void onTaskStateChanged(int position) {
						addAndDeleteTask(position);
					}

				});
				break;
			case AppConfig.SUCCESS:
				AppConfig.CHANGETASK = true;
				getTask();
				break;
			case AppConfig.FAILED:
				Toast.makeText(HealthManagemActivity.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pi_health_management);
		initView();
		UserID = getIntent().getStringExtra("UserID");
		getTask();
	}

	private void getTask() {
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = ConnectService
						.getIncidentbyHttpGet(AppConfig.URL_PUBLIC
								+ "HealthSchedule/TaskTemplateList?CusUBAOID="
								+ UserID);
				try {
					if (jsonObject.getInt("RetCode") != 200
							&& jsonObject.getInt("RetCode") != 0) {
						return;
					} else {
						list = formatjson(jsonObject);
						if (list.size() > 0) {
							Message msg = new Message();
							msg.what = 0x11;
							handler.sendEmptyMessage(msg.what);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start(ThreadManager.getManager());
	}

	private List<TaskBean> formatjson(JSONObject jsonObject) {
		List<TaskBean> list = new ArrayList<TaskBean>();
		JSONObject RetData;
		JSONArray jsonarray;
		try {
			RetData = jsonObject.getJSONObject("RetData");
			jsonarray = RetData.getJSONArray("TaskTemplates");
			Log.e("zhang task", jsonarray.toString());
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = jsonarray.getJSONObject(i);
				TaskBean bean = new TaskBean();
				bean.setTaskCount(object.getString("TaskCount") + "");
				bean.setID(object.getString("ID") + "");
				bean.setName(object.getString("Name") + "");
				bean.setIconURL(object.getString("IconURL") + "");
				bean.setTaskType(object.getString("TaskType") + "");
				bean.setDescription(object.getString("Description"));
				list.add(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	private void addAndDeleteTask(int position) {
		if (list.get(position).getTaskCount().equals("0")) {
			add(position);
		} else {
			delete(position);
		}
	}

	private void delete(int position) {
		final JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("TaskTemplateID", list.get(position).getID());
			jsonObject.put("DeleteTask", "1");
			jsonObject.put("UBAOUserID", UserID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC + "HealthSchedule/CancelTask",
							jsonObject);
					String retcode = responsedata.getString("RetCode");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						list.clear();
						msg.what = AppConfig.SUCCESS;
						msg.obj = responsedata.getString("RetData");
					} else {
						msg.what = AppConfig.FAILED;
						msg.obj = responsedata.getString("ErrorMessage");
					}
					handler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start(ThreadManager.getManager());
	}

	private void add(int position) {
		final JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("TaskTemplateID", list.get(position).getID());
			jsonObject.put("UBAOUserID", UserID);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC + "HealthSchedule/AddTask",
							jsonObject);
					String retcode = responsedata.getString("RetCode");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						list.clear();
						msg.what = AppConfig.SUCCESS;
						msg.obj = responsedata.getString("RetData");
					} else {
						msg.what = AppConfig.FAILED;
						msg.obj = responsedata.getString("ErrorMessage");
					}
					handler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start(ThreadManager.getManager());
	}

	private void initView() {
		tv_name = (TextView) findViewById(R.id.tv_name);
		img_back = (LinearLayout) findViewById(R.id.img_back);
		tv_name.setText(R.string.health_title);
		img_back.setOnClickListener(new myOnClick());
		pi_health_tasklist = (ListView) findViewById(R.id.pi_health_tasklist);
	}

	private class myOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.img_back:
				finish();
				break;
			default:
				break;
			}
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("HealthManagemActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("HealthManagemActivity");
		MobclickAgent.onPause(this);
	}
}
