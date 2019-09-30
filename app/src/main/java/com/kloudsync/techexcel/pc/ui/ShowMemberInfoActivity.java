package com.kloudsync.techexcel.pc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
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

public class ShowMemberInfoActivity extends Activity {
	private ImageView im_imgback;
	private TextView tv_topname, tv_area, tv_sex, tv_height, tv_birthday,
			tv_weight, tv_userNo;
	private RelativeLayout tv_img;
	private CircleImageView tv_head;
	private LinearLayout ll_edit_area, ll_sex, ll_height, ll_birthday;
	private String name, State, City, Address, BirthDay, Gender, Height, Phone,
			Weight, AvatarUrl;
	private List<ConditionBean> list = new ArrayList<ConditionBean>();
	PopupWindow menuWindow;
	private TextView editText;
	private LinearLayout layout;
	private String UserID;
	public ImageLoader imageLoader;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x10:
				Toast.makeText(ShowMemberInfoActivity.this, msg.obj.toString(),
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
				for (int i = 0; i < list.size(); i++) {
					if (Gender.equals(list.get(i).getFilterValueID())) {
						tv_sex.setText(list.get(i).getFilterValue() + "");
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
		setContentView(R.layout.pi_showmember);
		UserID = getIntent().getStringExtra("UserID");
		initview();
		getphoto();
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
				com.tencent.mm.sdk.platformtools.Log.e("dkjfoisajfoiws",
						jsonObject.toString() + "");
				list = formatjson(jsonObject);
				if (list.size() > 0 && list != null) {
					Message msg = new Message();
					msg.what = 0x00;
					msg.obj = list;
					handler.sendMessage(msg);
				}
			}
		}).start(((App) getApplication()).getThreadMgr());
	}

	private void getphoto() {
		/*UserGet userget = new UserGet();
		userget.setDetailListener(new DetailListener() {

			@Override
			public void getUser(UserBean user) {

			}

			@Override
			public void getMember(MemberBean member) {
				AvatarUrl = member.getAvatarUrl() + "";
				getPersonInfo();
			}
		});
		userget.MemberDetailRequest(getApplicationContext(), UserID);*/


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
								+ AppConfig.GETCUSTOMERINFO + "?UserID=" + UserID);
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
							if (responsejson.has("Mobile")) {
								Phone = responsejson.getString("Mobile") + "";
							} else {
								Phone = "";
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
		tv_userNo = (TextView) findViewById(R.id.userNo_me);
		im_imgback = (ImageView) findViewById(R.id.imgback_me);
		im_imgback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_topname = (TextView) findViewById(R.id.topname_me);
		tv_topname.setText("个人资料");
		tv_img = (RelativeLayout) findViewById(R.id.editimghead_me);
		tv_img.setOnClickListener(new MyOnClick());
		tv_head = (CircleImageView) findViewById(R.id.edithead_me);
		tv_head.setOnClickListener(new MyOnClick());
		ll_edit_area = (LinearLayout) findViewById(R.id.ll_edit_area_me);
		ll_edit_area.setOnClickListener(new MyOnClick());
		ll_sex = (LinearLayout) findViewById(R.id.ll_sex_me);
		ll_sex.setOnClickListener(new MyOnClick());
		ll_height = (LinearLayout) findViewById(R.id.ll_height_me);
		ll_height.setOnClickListener(new MyOnClick());
		ll_birthday = (LinearLayout) findViewById(R.id.ll_birthday_me);
		ll_birthday.setOnClickListener(new MyOnClick());
		tv_area = (TextView) findViewById(R.id.edit_area_me);
		tv_sex = (TextView) findViewById(R.id.tv_sex_me);
		tv_height = (TextView) findViewById(R.id.tv_height_me);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday_me);
		editText = (TextView) findViewById(R.id.editnamecontent_me);
		layout = (LinearLayout) findViewById(R.id.editname_me);
		layout.setOnClickListener(new MyOnClick());
		tv_weight = (TextView) findViewById(R.id.tv_weight_me);
		tv_weight.setOnClickListener(new MyOnClick());
	}

	private class MyOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.edithead_me:
			case R.id.ll_edit_area_me:
			case R.id.ll_birthday_me:
			case R.id.ll_height_me:
			case R.id.ll_sex_me:
			case R.id.editnamecontent_me:
			case R.id.tv_weight_me:
				Toast.makeText(ShowMemberInfoActivity.this, "非本人会员不可编辑",
						Toast.LENGTH_SHORT).show();
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
		MobclickAgent.onPageStart("ShowMemberInfoActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ShowMemberInfoActivity");
		MobclickAgent.onPause(this);
	}
}
