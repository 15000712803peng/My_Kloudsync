package com.kloudsync.techexcel.pc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.ConditionBean;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.contact.UserDetail;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.pc.adapter.AllListFilterAdapter;
import com.kloudsync.techexcel.pc.help.NumericWheelAdapter;
import com.kloudsync.techexcel.pc.help.OnWheelScrollListener;
import com.kloudsync.techexcel.pc.help.WheelView;
import com.kloudsync.techexcel.tool.ContainsEmojiEditText;
import com.kloudsync.techexcel.view.CircleImageView;
import com.kloudsync.user.techexcel.pi.tools.FileUtils;
import com.kloudsync.user.techexcel.pi.tools.MemberBean;
import com.kloudsync.user.techexcel.pi.tools.UserGet;
import com.kloudsync.user.techexcel.pi.tools.UserGet.DetailListener;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditUserInfoActivity extends Activity {
	private TextView im_imgback;
	private TextView tv_topname, tv_area, tv_sex, tv_height, tv_birthday,
			tv_save, tv_weight;
	private RelativeLayout tv_img;
	private CircleImageView tv_head;
	private LinearLayout ll_edit_area, ll_sex, ll_height, ll_birthday,
			ll_weight_in;
	private String name, State, City, Address, BirthDay, Gender, Height,
			Weight, AvatarUrl;
	private int StateId = -1, CityId = -1;
	private AllListFilterAdapter adapter;
	private int selectposition = -1;
	private ScrollView scrollView;
	private ListView listView;
	private static final int SUCCESSGETSEX = 0X110;
	private String filter = "";
	private LinearLayout sexchose;
	private List<ConditionBean> list = new ArrayList<ConditionBean>();
	private WheelView tv_heightEdit, tv_weightEdit;
	private int height = 170;
	private int weight = 50;
	private WheelView year, month, day;
	PopupWindow menuWindow;
	private int myear, mmonth, mday;
	private LinearLayout pi_ll_birth_cancel, pi_ll_birth_save;
	private ContainsEmojiEditText editText;
	private String UserID;
	private static File cache;
	public ImageLoader imageLoader;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
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
			case 0x22:
				AppConfig.isUpdateCustomer = true;
				save();
				break;
			case 0x10:
				Toast.makeText(EditUserInfoActivity.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
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
				if (Weight != null && !Weight.equals("null")) {
					tv_weight.setText(Weight);
				}
				for (int i = 0; i < list.size(); i++) {
					if (Gender.equals(list.get(i).getFilterValueID())) {
						tv_sex.setText(list.get(i).getFilterValue());
					}
				}
				String photo[] = AvatarUrl.split("/");
				String filename = photo[photo.length - 1];
				final String fileLocalUrl = Environment
						.getExternalStorageDirectory().getPath()
						+ "/Image"
						+ File.separator + filename;
				AppConfig.IMAGEURL = fileLocalUrl;
				downloadAttachment();
				break;
			case AppConfig.SUCCESS:
				AppConfig.isUpdateCustomer = true;
				UserDetail.instance.finish();
				ShowUserInfoActivity.instance.finish();
				Toast.makeText(EditUserInfoActivity.this, "用户资料修改成功",
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent(EditUserInfoActivity.this,
						UserDetail.class);
				i.putExtra("UserID", UserID);
				startActivity(i);
				Intent ii = new Intent();
				ii.setAction("com.ubao.techexcel.frgment");
				sendBroadcast(ii);
				finish();
				break;
			case AppConfig.FAILED:
				Toast.makeText(EditUserInfoActivity.this, msg.obj.toString(),
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
		setContentView(R.layout.pi_edituser);
		UserID = getIntent().getStringExtra("UserID");
		initview();
		getPersonInfo();
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

	private void getPersonInfo() {
		UserGet userget = new UserGet();
		userget.setDetailListener(new DetailListener() {
			@Override
			public void getUser(Customer user) {
				AvatarUrl = user.getUrl() + "";
				name = user.getName() + "";
				Gender = user.getSex() + "";
				Address = user.getAddress() + "";
				BirthDay = user.getBirthday() + "";
				Height = user.getHeight() + "";
				Weight = user.getWeight() + "";
				Log.e("zhang", "personal:" + name + "," + Gender + "," + State
						+ "," + City + "," + Address + "," + BirthDay + ","
						+ Height + "," + Weight + "," + AvatarUrl);
				confirmSex();
			}

			@Override
			public void getMember(MemberBean member) {

			}
		});
		userget.CustomerDetailRequest(getApplicationContext(), UserID);
	}

	private void initview() {
		im_imgback = (TextView) findViewById(R.id.imgback_in_e);
		im_imgback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_save = (TextView) findViewById(R.id.tv_save_in_e);
		tv_save.setOnClickListener(new MyOnClick());
		tv_topname = (TextView) findViewById(R.id.topname_in_e);
		tv_topname.setText("编辑个人资料");
		tv_img = (RelativeLayout) findViewById(R.id.editimghead_in_e);
		tv_img.setOnClickListener(new MyOnClick());
		tv_head = (CircleImageView) findViewById(R.id.edithead_in_e);
		tv_head.setOnClickListener(new MyOnClick());
		ll_edit_area = (LinearLayout) findViewById(R.id.ll_edit_area_in_e);
		ll_edit_area.setOnClickListener(new MyOnClick());
		ll_sex = (LinearLayout) findViewById(R.id.ll_sex_in_e);
		ll_sex.setOnClickListener(new MyOnClick());
		ll_height = (LinearLayout) findViewById(R.id.ll_height_in_e);
		ll_height.setOnClickListener(new MyOnClick());
		ll_birthday = (LinearLayout) findViewById(R.id.ll_birthday_in_e);
		ll_birthday.setOnClickListener(new MyOnClick());
		ll_weight_in = (LinearLayout) findViewById(R.id.ll_weight_in_e);
		ll_weight_in.setOnClickListener(new MyOnClick());
		tv_weight = (TextView) findViewById(R.id.tv_weight_in_e);
		tv_area = (TextView) findViewById(R.id.edit_area_in_e);
		tv_sex = (TextView) findViewById(R.id.tv_sex_in_e);
		tv_height = (TextView) findViewById(R.id.tv_height_in_e);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday_in_e);
		editText = (ContainsEmojiEditText) findViewById(R.id.editnamecontent_in_e);

	}

	private class MyOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			case R.id.ll_weight_in_e:
				setWeight(EditUserInfoActivity.this);
				break;
			case R.id.editimghead_in_e:
			case R.id.edithead_in_e:
				intent = new Intent(EditUserInfoActivity.this,
						UpLoadHeadActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_edit_area_in_e:
				intent = new Intent(EditUserInfoActivity.this,
						AreaEditActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_birthday_in_e:
				setbirthday(EditUserInfoActivity.this);
				break;
			case R.id.ll_height_in_e:
				setHight(EditUserInfoActivity.this);
				break;
			case R.id.ll_sex_in_e:
				selectSex(EditUserInfoActivity.this);
				getSex();
				break;
			case R.id.editnamecontent_in_e:
				// 弹出软键盘
				@SuppressWarnings("static-access")
				final InputMethodManager inputManager = (InputMethodManager) editText
						.getContext().getSystemService(
								EditUserInfoActivity.this.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(editText, 0);
				break;
			case R.id.tv_save_in_e:
				if (AppConfig.UPLOADSTATIC == true) {
					AppConfig.UPLOADSTATIC = false;
					uploadhead();
				} else {
					save();
				}
				break;
			default:
				break;
			}
		}

	}

	private void save() {
		name = editText.getText().toString();
		final JSONObject jsonobject = format();
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC + "User/UpdateCustomerInfo",
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

	private void uploadhead() {
		RequestParams params = new RequestParams();
		params = AppConfig.PARAMS;
		String url = AppConfig.URL_PUBLIC
				+ "Avatar?Uploadtype=1&UserID4Customer=" + UserID;
		Log.e("url", url);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("UTF-8");
		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {

					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						FileUtils ss = new FileUtils(getApplicationContext());
						ss.deleteFile();
						cache = new File(Environment
								.getExternalStorageDirectory(), "Image");
						if (!cache.exists()) {
							cache.mkdirs();
						}
						Message msg = new Message();
						msg.what = 0x22;
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

	private JSONObject format() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("UserID", UserID);
			if (name != null) {
				jsonObject.put("name", name);
			}

			if ((StateId + "") != null) {
				jsonObject.put("State", StateId + "");
			}
			if ((CityId + "") != null) {
				jsonObject.put("City", CityId + "");
			}

			jsonObject.put("Address", Address);
			if (BirthDay != null) {
				jsonObject.put("Birthday", BirthDay);
			}

			if (Gender != null) {
				jsonObject.put("Sex", Gender + "");
			}

			if (Height != null) {
				jsonObject.put("Height", Height);
			}
			if (Weight != null) {
				jsonObject.put("Weight", Weight);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
	private void setHight(Context context) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View windov = inflater.inflate(R.layout.pi_editheight, null);
		tv_heightEdit = (WheelView) windov.findViewById(R.id.tv_heightEdit);
		tv_heightEdit.setAdapter(new NumericWheelAdapter(100, 300));
		tv_heightEdit.setCyclic(true);
		tv_heightEdit.addScrollingListener(scrollListener);
		tv_heightEdit.setCurrentItem(height - 100);
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
						Height = height + "";
						tv_height.setText(Height);
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

	private void setWeight(Context context) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View windov = inflater.inflate(R.layout.pi_editweight, null);
		tv_weightEdit = (WheelView) windov.findViewById(R.id.tv_weightEdit);
		tv_weightEdit.setAdapter(new NumericWheelAdapter(30, 150));
		tv_weightEdit.setCyclic(true);
		tv_weightEdit.addScrollingListener(scrollListenerw);
		tv_weightEdit.setCurrentItem(weight - 30);
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
						Weight = weight + "";
						tv_weight.setText(Weight);
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

	OnWheelScrollListener scrollListenerw = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			weight = tv_weightEdit.getCurrentItem() + 30;
		}
	};

	private void getSex() {
		if (list.size() > 0 && list != null) {
			Message msg = new Message();
			msg.what = SUCCESSGETSEX;
			msg.obj = list;
			handler.sendMessage(msg);
		}
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

	@Override
	protected void onResume() {
		if (AppConfig.PROVINCE != null || AppConfig.CITY != null
				|| AppConfig.STREET != null) {
			String spro = "", scity = "", str = "";
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
			StateId = AppConfig.STATEBEAN.getID();
			CityId = AppConfig.CITYBEAN.getID();
			Address = AppConfig.STREET;
		}
		if (AppConfig.IMAGEURL != null) {
			ImageLoaderMy.getInstance(3, Type.LIFO).loadImage(
					AppConfig.IMAGEURL, tv_head);

		}
		super.onResume();
		MobclickAgent.onPageStart("EditUserInfoActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("EditUserInfoActivity");
		MobclickAgent.onPause(this);
	}
}
