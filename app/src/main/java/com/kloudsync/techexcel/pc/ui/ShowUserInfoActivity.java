package com.kloudsync.techexcel.pc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.ConditionBean;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.view.CircleImageView;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowUserInfoActivity extends Activity {
	private ImageView im_imgback;
	private CircleImageView tv_head;
	private TextView tv_topname, tv_area, tv_sex, tv_height, tv_birthday,
			tv_weight, tv_userNo, tv_edit, wodeubaoren;
	private String name, State, City, Address, BirthDay, Gender, Height, Phone,
			Weight, AvatarUrl, UBAOPersonName, UBAOPersonID;
	private List<ConditionBean> list = new ArrayList<ConditionBean>();
	PopupWindow menuWindow;
	private TextView editText;
	private String UserID;
	public static ShowUserInfoActivity instance = null;
	public ImageLoader imageLoader;
	public RelativeLayout shifoukebianji;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x10:
				Toast.makeText(ShowUserInfoActivity.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				break;
			case 0x11:
				confirmSex();
				break;
			case 0x00:
				if (name != null) {
					editText.setText(name);
				}
				String myaddress = null;

				if (Address != null && !Address.equals("null")) {
					AppConfig.STREET = Address;
					myaddress = Address;
				} else {
					myaddress = "";
				}
				tv_area.setText(myaddress);

				if (BirthDay != null) {
					tv_birthday.setText(BirthDay);
				}
				if (Height != null) {
					tv_height.setText(Height);
				}
				if (Phone != null) {
					tv_userNo.setText(Phone);
				}
				if (Weight != null && !Weight.equals("null")) {
					tv_weight.setText(Weight);
				}
				if (!UBAOPersonID.equals("0")) {
					wodeubaoren.setText(UBAOPersonName);
				} else {
					wodeubaoren.setText(getResources().getString(R.string.to_be_allocated));
				}
				for (int i = 0; i < list.size(); i++) {
					if (Gender.equals(list.get(i).getFilterValueID())) {
						tv_sex.setText(list.get(i).getFilterValue());
					}
				}
				downloadAttachment();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pi_showuser);
		instance = this;
		UserID = getIntent().getStringExtra("UserID");
		initview();
		getPhoto();
	}

	private void confirmSex() {
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = ConnectService
						.getIncidentbyHttpGet(AppConfig.URL_PUBLIC
								+ AppConfig.WHO_DO_WHAT + "?" + "ChoiceTypeID="
								+ AppConfig.GETSEX);
				try {
					if (jsonObject.getInt("RetCode") != 200
							&& jsonObject.getInt("RetCode") != 0) {
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				list = formatjson(jsonObject);
				if (list.size() > 0 && list != null) {
					Message msg = new Message();
					msg.what = 0x00;
					msg.obj = list;
					handler.sendMessage(msg);
				}
			}
		}).start(ThreadManager.getManager());
	}

	private void getPhoto() {
		/*UserGet userget = new UserGet();
		userget.setDetailListener(new DetailListener() {
			@Override
			public void getUser(UserBean user) {
				AvatarUrl = user.getAvatarUrl() + "";
				getPersonInfo();
			}

			@Override
			public void getMember(MemberBean member) {

			}
		});
		userget.CustomerDetailRequest(getApplicationContext(), UserID);*/


		LoginGet loginget = new LoginGet();
		loginget.setDetailGetListener(new LoginGet.DetailGetListener() {

			@Override
			public void getUser(Customer user) {
				AvatarUrl = user.getUrl() + "";
				getPersonInfo();
			}

			@Override
			public void getMember(Customer member) {
				// TODO Auto-generated method stub

			}
		});
		loginget.CustomerDetailRequest(getApplicationContext(), UserID);
	}

	private void getPersonInfo() {

		new ApiTask(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = ConnectService
						.getIncidentbyHttpGet(AppConfig.URL_PUBLIC
								+ AppConfig.GETCUSTOMERINFO + "?UserID="
								+ UserID);
				Log.e("zhang", jsonObject.toString() + "");
				try {
					if (jsonObject.getInt("RetCode") != 200
							&& jsonObject.getInt("RetCode") != 0) {
						Message msg = new Message();
						msg.what = 0x10;
						msg.obj = jsonObject.getString("ErrorMessage");
						handler.sendMessage(msg);
					} else {
						JSONObject responsejson;
						try {
							responsejson = jsonObject.getJSONObject("RetData");
							name = responsejson.getString("Name") + "";
							if (responsejson.has("Sex")) {
								Gender = responsejson.getString("Sex") + "";
							} else {
								Gender = "";
							}
							if (responsejson.has("Address")) {
								Address = responsejson.getString("Address")
										+ "";
							} else {
								Address = "";
							}
							if (responsejson.has("Birthday")) {
								BirthDay = responsejson.getString("Birthday")
										+ "";
							} else {
								BirthDay = "";
							}
							if (responsejson.has("Height")) {
								Height = responsejson.getString("Height") + "";
							} else {
								Height = "";
							}
							if (responsejson.has("Weight")) {
								Weight = responsejson.getString("Weight") + "";
							} else {
								Weight = "";
							}
							if (responsejson.has("UBAOPersonID")) {
								UBAOPersonID = responsejson
										.getString("UBAOPersonID") + "";
							} else {
								UBAOPersonID = "";
							}
							if (responsejson.has("UBAOPersonName")) {
								UBAOPersonName = responsejson
										.getString("UBAOPersonName") + "";
							} else {
								UBAOPersonName = "";
							}
							Phone = responsejson.getString("Phone") + "";
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Log.e("zhang", "personal:" + name + "," + Gender + ","
								+ State + "," + City + "," + Address + ","
								+ BirthDay + "," + Height + "," + Phone + ","
								+ Weight + "," + AvatarUrl);
						Message msg = new Message();
						msg.what = 0x11;
						handler.sendEmptyMessage(msg.what);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}).start(ThreadManager.getManager());
	}

	public void downloadAttachment() {
		imageLoader = new ImageLoader(getApplicationContext());
		Log.e("zhang", "AvatarUrl:" + AvatarUrl);
		if (null == AvatarUrl || AvatarUrl.length() < 1) {
			Log.e("zhang", "1");
			tv_head.setImageResource(R.drawable.hello);
		} else {
			Log.e("zhang", "2");
			imageLoader.DisplayImage(AvatarUrl, tv_head);
		}
	}

	private void initview() {
		tv_userNo = (TextView) findViewById(R.id.userNo_in);
		im_imgback = (ImageView) findViewById(R.id.imgback_in);
		im_imgback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_edit = (TextView) findViewById(R.id.tv_save_in);
		tv_edit.setOnClickListener(new MyOnClick());
		tv_topname = (TextView) findViewById(R.id.topname_in);
		tv_topname.setText("个人资料");
		tv_area = (TextView) findViewById(R.id.edit_area_in);
		tv_sex = (TextView) findViewById(R.id.tv_sex_in);
		tv_height = (TextView) findViewById(R.id.tv_height_in);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday_in);
		editText = (TextView) findViewById(R.id.editnamecontent_in);
		tv_weight = (TextView) findViewById(R.id.tv_weight_in);
		tv_head = (CircleImageView) findViewById(R.id.edithead_in);
		wodeubaoren = (TextView) findViewById(R.id.wodeyoubaoren);
		shifoukebianji = (RelativeLayout) findViewById(R.id.shifoukebianji);
		if (AppConfig.UserType == 0) {
			tv_edit.setVisibility(View.INVISIBLE);
			shifoukebianji.setVisibility(View.GONE);
		}
	}

	private class MyOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_save_in:
				Intent intent = new Intent(ShowUserInfoActivity.this,
						EditUserInfoActivity.class);
				intent.putExtra("UserID", UserID);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	}

	private List<ConditionBean> formatjson(JSONObject jsonObject) {
		List<ConditionBean> list = new ArrayList<ConditionBean>();
		JSONArray jsonarray;
		try {
			jsonarray = jsonObject.getJSONArray("RetData");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = jsonarray.getJSONObject(i);
				ConditionBean bean = new ConditionBean();
				bean.setFilterValue(object.getString("Name"));
				bean.setFilterValueID(object.getInt("ID") + "");
				list.add(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ShowUserInfoActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ShowUserInfoActivity");
		MobclickAgent.onPause(this);
	}
}
