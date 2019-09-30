package com.kloudsync.techexcel.pc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.ConditionBean;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.pc.adapter.AllListFilterAdapter;
import com.kloudsync.techexcel.pc.help.NumericWheelAdapter;
import com.kloudsync.techexcel.pc.help.OnWheelScrollListener;
import com.kloudsync.techexcel.pc.help.WheelView;
import com.kloudsync.techexcel.start.LoginActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.RegisterActivity;
import com.kloudsync.techexcel.tool.ContainsEmojiEditText;
import com.kloudsync.techexcel.view.CircleImageView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.CalListviewHeight;
import com.ub.techexcel.tools.ImageLoaderMy;
import com.ub.techexcel.tools.ImageLoaderMy.Type;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PerfectDataActivity extends Activity {
	private ImageView im_imgback;
	private TextView tv_topname, tv_area, tv_sex, tv_height, tv_birthday,
			tv_save;
	private RelativeLayout tv_img;
	private CircleImageView tv_head;
	private LinearLayout ll_edit_area, ll_sex, ll_height, ll_birthday;
	private String Mobile, Password, AccessCode, name, Address, Gender,
			BirthDay;
	private int countrycode;
	private int State = -1, City = -1, Role;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private AllListFilterAdapter adapter;
	private int selectposition = -1;
	private ScrollView scrollView;
	private ListView listView;
	private static final int SUCCESSGETSEX = 0X110;
	private String filter = "";
	private LinearLayout sexchose;
	private List<ConditionBean> list = new ArrayList<ConditionBean>();
	private WheelView tv_heightEdit;
	private int height = -1;
	private WheelView year, month, day;
	PopupWindow menuWindow;
	private int myear, mmonth, mday;
	private LinearLayout pi_ll_birth_cancel, pi_ll_birth_save;
	private ContainsEmojiEditText editText;
	private LinearLayout layout;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConfig.SUCCESS:
				LoginActivity.instance.finish();
				RegisterActivity.instance.finish();
				Toast.makeText(PerfectDataActivity.this, "用户注册成功",
						Toast.LENGTH_SHORT).show();
				sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
						MODE_PRIVATE);
				editor = sharedPreferences.edit();
				editor.putInt("countrycode", countrycode);
				editor.putString("telephone", Mobile);
				editor.putString("password", Password);
				editor.commit();
				LoginGet.LoginRequest(PerfectDataActivity.this, "+"
						+ countrycode + Mobile, Password, 2, sharedPreferences,
                        editor, ((App) getApplication()).getThreadMgr());
				break;
			case 0x22:
				if (AppConfig.UPLOADSTATIC == true) {
					AppConfig.UPLOADSTATIC = false;
					uploadhead(msg.obj.toString());
				} else {
					Message msg2 = new Message();
					msg2.what = AppConfig.SUCCESS;
					handler.sendEmptyMessage(msg2.what);
				}
				break;
			case AppConfig.FALSE:
				Toast.makeText(PerfectDataActivity.this, "用户名和地址不能为空",
						Toast.LENGTH_SHORT).show();
				break;
			case AppConfig.FAILED:
				Toast.makeText(PerfectDataActivity.this,
						"注册失败：" + msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				break;
			case SUCCESSGETSEX:
				sexchose.setVisibility(View.VISIBLE);
				scrollView.setVisibility(View.VISIBLE);
				for (int i = 0; i < list.size(); i++) {
					if (filter.equals(list.get(i).getFilterValue())) {
						selectposition = i;
					}
				}
				adapter = new AllListFilterAdapter(getApplicationContext(),
						list, R.layout.pi_currentstatus_item, selectposition);
				listView.setAdapter(adapter);
				CalListviewHeight.setListViewHeightBasedOnChildren(listView);
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pi_perfect_data);
		initvalue();
		initview();
	}

	private void initvalue() {
		countrycode = Integer.parseInt(getIntent().getStringExtra("countrycode"));
		Mobile = getIntent().getStringExtra("telephone");
		Password = getIntent().getStringExtra("password");
		AccessCode = getIntent().getStringExtra("AccessCode");
		Role = 1;
	}

	private void initview() {
		im_imgback = (ImageView) findViewById(R.id.im_imgback);
		im_imgback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_save.setOnClickListener(new MyOnClick());
		tv_topname = (TextView) findViewById(R.id.tv_topname);
		tv_topname.setText(getResources().getString(R.string.Perfact_info));
		tv_img = (RelativeLayout) findViewById(R.id.img);
		tv_img.setOnClickListener(new MyOnClick());
		tv_head = (CircleImageView) findViewById(R.id.head);
		ll_edit_area = (LinearLayout) findViewById(R.id.ll_edit_area);
		ll_edit_area.setOnClickListener(new MyOnClick());
		ll_sex = (LinearLayout) findViewById(R.id.ll_sex);
		ll_sex.setOnClickListener(new MyOnClick());
		ll_height = (LinearLayout) findViewById(R.id.ll_height);
		ll_height.setOnClickListener(new MyOnClick());
		ll_birthday = (LinearLayout) findViewById(R.id.ll_birthday);
		ll_birthday.setOnClickListener(new MyOnClick());
		tv_area = (TextView) findViewById(R.id.edit_area);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		tv_height = (TextView) findViewById(R.id.tv_height);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		editText = (ContainsEmojiEditText) findViewById(R.id.editnamecontent);
		layout = (LinearLayout) findViewById(R.id.editname);
		layout.setOnClickListener(new MyOnClick());
	}

	private class MyOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			case R.id.img:
				intent = new Intent(PerfectDataActivity.this,
						UpLoadHeadActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_edit_area:
				intent = new Intent(PerfectDataActivity.this,
						AreaEditActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_birthday:
				setbirthday(PerfectDataActivity.this);
				break;
			case R.id.ll_height:
				setHight(PerfectDataActivity.this);

				break;
			case R.id.ll_sex:

				selectSex(PerfectDataActivity.this);
				getSex();
				break;
			case R.id.editnamecontent:
				// 弹出软键盘
				@SuppressWarnings("static-access")
				final InputMethodManager inputManager = (InputMethodManager) editText
						.getContext().getSystemService(
								PerfectDataActivity.this.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(editText, 0);
				break;
			case R.id.tv_save:
				save();
			default:
				break;
			}
		}
	}

	private void setbirthday(Context context) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View windov = inflater.inflate(R.layout.pi_birthday, null);
		year = (WheelView) windov.findViewById(R.id.year);
		month = (WheelView) windov.findViewById(R.id.month);
		day = (WheelView) windov.findViewById(R.id.day);
		pi_ll_birth_cancel = (LinearLayout) windov
				.findViewById(R.id.pi_ll_birth_cancel);
		pi_ll_birth_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				builder.dismiss();
			}
		});
		getDataPick();
		pi_ll_birth_save = (LinearLayout) windov
				.findViewById(R.id.pi_ll_birth_save);
		pi_ll_birth_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				BirthDay = (year.getCurrentItem() + 1950) + "-"
						+ (month.getCurrentItem() + 1) + "-"
						+ (day.getCurrentItem() + 1);
				myear = year.getCurrentItem() + 1950;
				mmonth = month.getCurrentItem() + 1;
				mday = day.getCurrentItem() + 1;
				String strm;
				String strd;
				if (mmonth < 10) {
					strm = "0" + mmonth;
				} else {
					strm = mmonth + "";
				}
				if (mday < 10) {
					strd = "0" + mday;
				} else {
					strd = mday + "";
				}
				tv_birthday.setText(myear + "-" + strm + "-" + strd);
				builder.dismiss();
			}
		});
		builder = new AlertDialog.Builder(context).show();
		Window dialogWindow = builder.getWindow();
		WindowManager m = ((Activity) context).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.8);
		p.height = (int) (d.getHeight() * 0.5);
		dialogWindow.setAttributes(p);
		builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		builder.setContentView(windov);
	}

	private void getDataPick() {
		Calendar c = Calendar.getInstance();
		int curYear = c.get(Calendar.YEAR);
		int curMonth = c.get(Calendar.MONTH) + 1;// 通过Calendar算出的月数要+1

		year.setAdapter(new NumericWheelAdapter(1950, curYear));
		year.setLabel("年");
		year.setCyclic(true);
		year.addScrollingListener(scrollListener1);

		month.setAdapter(new NumericWheelAdapter(1, 12));
		month.setLabel("月");
		month.setCyclic(true);
		month.addScrollingListener(scrollListener1);

		initDay(curYear, curMonth);
		day.setLabel("日");
		day.setCyclic(true);
		day.addScrollingListener(scrollListener1);
		year.setCurrentItem(myear - 1950);
		month.setCurrentItem(mmonth - 1);
		day.setCurrentItem(mday - 1);
	}

	OnWheelScrollListener scrollListener1 = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int n_year = year.getCurrentItem() + 1950;
			int n_month = month.getCurrentItem() + 1;
			initDay(n_year, n_month);
			int n_day = day.getCurrentItem() + 1;
		}
	};

	/**
	 * 
	 * @param year
	 * @param month
	 *            +
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}

	/**
	 */
	private void initDay(int arg1, int arg2) {
		day.setAdapter(new NumericWheelAdapter(1, getDay(arg1, arg2), "%02d"));
	}

	private void setHight(Context context) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View windov = inflater.inflate(R.layout.pi_editheight, null);
		tv_heightEdit = (WheelView) windov.findViewById(R.id.tv_heightEdit);
		tv_heightEdit.setAdapter(new NumericWheelAdapter(100, 300));
		tv_heightEdit.setCyclic(true);
		tv_heightEdit.addScrollingListener(scrollListener);
		if (height != -1) {
			tv_heightEdit.setCurrentItem(height - 100);
		} else {
			tv_heightEdit.setCurrentItem(70);
		}
		windov.findViewById(R.id.pi_ll_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						builder.dismiss();
					}
				});
		windov.findViewById(R.id.pi_ll_save).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						AppConfig.HEIGHT = height + "";
						tv_height.setText(height + "");
						builder.dismiss();
					}
				});
		builder = new AlertDialog.Builder(context).show();
		Window dialogWindow = builder.getWindow();
		WindowManager m = ((Activity) context).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.8);
		p.height = (int) (d.getHeight() * 0.5);
		dialogWindow.setAttributes(p);
		builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		builder.setContentView(windov);

	}

	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			height = tv_heightEdit.getCurrentItem() + 100;
		}
	};

	private void getSex() {
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
					msg.what = SUCCESSGETSEX;
					msg.obj = list;
					handler.sendMessage(msg);
				}
			}
		}).start(ThreadManager.getManager());
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == selectposition) {
					selectposition = -1;
				} else {
					selectposition = position;
				}
				adapter.changePosititon(selectposition);
				adapter.notifyDataSetChanged();
			}
		});
	}

	private AlertDialog builder;

	@SuppressWarnings("deprecation")
	private void selectSex(Context context) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View windov = inflater.inflate(R.layout.pi_sex, null);
		listView = (ListView) windov.findViewById(R.id.pc_selectstatuslv);
		scrollView = (ScrollView) windov.findViewById(R.id.pc_scrollview);
		scrollView.setVisibility(View.GONE);
		sexchose = (LinearLayout) windov.findViewById(R.id.sexconfirm);
		sexchose.setVisibility(View.GONE);
		windov.findViewById(R.id.pi_ll_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						builder.dismiss();
					}
				});
		windov.findViewById(R.id.pi_ll_save).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (selectposition != -1) {
							AppConfig.CURRENT_VALUES = list.get(selectposition)
									.getFilterValue();
							AppConfig.CURRENT_VALUESID = list.get(
									selectposition).getFilterValueID();
							tv_sex.setText(AppConfig.CURRENT_VALUES);
							Gender = AppConfig.CURRENT_VALUESID;
						} else { // 没选择position
							AppConfig.CURRENT_VALUES = null;
							AppConfig.CURRENT_VALUESID = null;
						}
						builder.dismiss();
					}
				});
		builder = new AlertDialog.Builder(context).show();
		Window dialogWindow = builder.getWindow();
		WindowManager m = ((Activity) context).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.8);
		p.height = (int) (d.getHeight() * 0.5);
		dialogWindow.setAttributes(p);
		builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		builder.setContentView(windov);
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

	private void save() {
		Message msg = new Message();
		name = editText.getText().toString();
		if (name == null) {
			msg.what = AppConfig.FALSE;
			handler.sendEmptyMessage(msg.what);
		} else if (State == -1 && City == -1) {
			msg.what = AppConfig.FALSE;
			handler.sendEmptyMessage(msg.what);
		} else {
			final JSONObject jsonobject = format();
			new ApiTask(new Runnable() {
				@Override
				public void run() {
					try {
						JSONObject responsedata = ConnectService
								.submitDataByJsonNoToken(AppConfig.URL_PUBLIC
										+ "User/Register", jsonobject);
						String retcode = responsedata.getString("RetCode");
						JSONObject retdata = responsedata
								.getJSONObject("RetData");
						String UserID = retdata.getString("UserID");
						Log.e("sbsbsbs", jsonobject.toString() + "");
						Log.e("sbsbsbs", responsedata.toString() + "");
						Message msg = new Message();
						if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
							msg.what = 0x22;
							msg.obj = UserID;
						} else {
							msg.what = AppConfig.FAILED;
							String ErrorMessage = responsedata
									.getString("ErrorMessage");
							msg.obj = ErrorMessage;
						}
						handler.sendMessage(msg);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).start(ThreadManager.getManager());
		}

	}

	private void uploadhead(String UserID) {
		RequestParams params = new RequestParams();
		params = AppConfig.PARAMS;
		params.addBodyParameter("UploadType", "0");
		params.addBodyParameter("UserID4Customer", UserID);
		String url = AppConfig.URL_PUBLIC + "Avatar";
		Log.e("url", url);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("UTF-8");
		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						Toast.makeText(getApplicationContext(),
								getString(R.string.upload), Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						Message msg = new Message();
						msg.what = AppConfig.SUCCESS;
						handler.sendEmptyMessage(msg.what);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Log.e("error", msg.toString());
						Toast.makeText(getApplicationContext(),
								getString(R.string.uploadfailure),
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	protected void onResume() {
		if (AppConfig.PROVINCE != null || AppConfig.CITY != null
				|| AppConfig.STREET != null) {
			String spro = "", scity = "", str="";
			if (AppConfig.PROVINCE != null) {
				spro = AppConfig.PROVINCE;
			}
			if (AppConfig.CITY != null) {
				scity = AppConfig.CITY;
			}
			if (AppConfig.STREET != null) {
				str = AppConfig.STREET;
			}
			tv_area.setText(spro + scity + str);
			State = AppConfig.STATEBEAN.getID();
			City = AppConfig.CITYBEAN.getID();
			Address = AppConfig.STREET;
		}
		if (AppConfig.IMAGEURL != null) {
			ImageLoaderMy.getInstance(3, Type.LIFO).loadImage(AppConfig.IMAGEURL,
					tv_head);

		}
		super.onResume();
		MobclickAgent.onPageStart("PerfectDataActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	private JSONObject format() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("Mobile", "+" + countrycode + Mobile);
			jsonObject.put("Password", LoginGet.getBase64Password(Password)
					.trim());
			jsonObject.put("AccessCode", AccessCode);
			jsonObject.put("Role", Role + "");
			jsonObject.put("name", name);
			jsonObject.put("State", State + "");
			jsonObject.put("City", City + "");
			jsonObject.put("Address", Address);
			if (BirthDay != null) {
				jsonObject.put("BirthDay", BirthDay);
			}

			if (Gender != null) {
				jsonObject.put("Gender", Gender + "");
			}
			if (height != -1) {
				jsonObject.put("Height", height + "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("PerfectDataActivity");
		MobclickAgent.onPause(this);
	}
}