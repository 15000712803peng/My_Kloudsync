package com.kloudsync.techexcel.pc.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.user.techexcel.pi.tools.MemberBean;
import com.kloudsync.user.techexcel.pi.tools.UserGet;
import com.kloudsync.user.techexcel.pi.tools.UserGet.DetailListener;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfessionalFieldActivity extends Activity {
	EditText selfdescription_in, edit_selfgoodat;
	private TextView tv_name, tv_save;
	private ImageView imgback;
	String goodat, description;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConfig.SUCCESS:
				AppConfig.HASUPDATESUMMERY = true;
				finish();
				break;
			case AppConfig.FAILED:
				Toast.makeText(ProfessionalFieldActivity.this,
						msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pc_professionalstate);
		initview();
		getinformation();
	}

	private void initview() {
		tv_name = (TextView) findViewById(R.id.topname);
		tv_name.setText(getString(R.string.professional));
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				goodat = edit_selfgoodat.getText().toString() + "";
				description = selfdescription_in.getText().toString() + "";
				save();
			}
		});
		imgback = (ImageView) findViewById(R.id.imgback);
		imgback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		edit_selfgoodat = (EditText) findViewById(R.id.edit_selfgoodat);
		selfdescription_in = (EditText) findViewById(R.id.selfdescription_in);
	}

	private void getinformation() {
		UserGet userget = new UserGet();
		userget.setDetailListener(new DetailListener() {

			@Override
			public void getUser(Customer user) {
				goodat = user.getSkilledFields();
				edit_selfgoodat.setText(goodat);
				description = user.getSummary();
				selfdescription_in.setText(description);

			}

			@Override
			public void getMember(MemberBean member) {
				/*goodat = member.getSkilledFields();
				edit_selfgoodat.setText(goodat);
				description = member.getSummary();
				selfdescription_in.setText(description);*/
			}
		});
		userget.CustomerDetailRequest(getApplicationContext(), AppConfig.UserID);
	}

	private void save() {
		final JSONObject jsonobject = format();
        new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC + "User/UpdateMemberSummary",
							jsonobject);
					String retcode = responsedata.getString("RetCode");
					Log.e("sbsbsbs", jsonobject.toString() + "");
					Log.e("sbsbsbs", responsedata.toString() + "");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						msg.what = AppConfig.SUCCESS;
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

	private JSONObject format() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("UserID", AppConfig.UserID);
			jsonObject.put("SkilledFields", goodat);
			jsonObject.put("Summary", description);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ProfessionalFieldActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ProfessionalFieldActivity");
		MobclickAgent.onPause(this);
	}
}
