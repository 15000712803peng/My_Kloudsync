package com.kloudsync.techexcel.pc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.AccountSettingBean;
import com.kloudsync.techexcel.bean.ConditionBean;
import com.kloudsync.techexcel.bean.params.EventChangeAccout;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.CustomerYu;
import com.kloudsync.techexcel.pc.adapter.AllListFilterAdapter;
import com.kloudsync.techexcel.pc.help.NumericWheelAdapter;
import com.kloudsync.techexcel.pc.help.OnWheelScrollListener;
import com.kloudsync.techexcel.pc.help.WheelView;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.ContainsEmojiEditText;
import com.kloudsync.user.techexcel.pi.tools.ProvinceBean;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.AccountSettingTakePhotoPopup;
import com.ub.techexcel.tools.CalListviewHeight;
import com.ub.techexcel.tools.FileUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static io.rong.imkit.utilities.RongUtils.density;

public class PersonalInfoActivity extends AppCompatActivity {
	private ImageView im_imgback;
	private TextView tv_topname, tv_area, tv_sex, tv_birthday, tv_save,
			tv_phone,pi_tv_secondaryphone,pi_tv_save;

	private EditText pi_et_description;
	private ContainsEmojiEditText pi_tv_email;

	private RelativeLayout tv_img;
	private SimpleDraweeView tv_head;
	private LinearLayout ll_edit_area, ll_sex, ll_birthday;
	private String name, State, City, Address, BirthDay, Gender, Mobile,
			AvatarUrl;

	private String FullName,FirstName,MiddleName,newAvatarUrl,SecondaryPhone,Description,PrimaryPhone,Email,RongCloudID,Nickname,LastName,LoginName;
	private CustomerYu customerYu = new CustomerYu();
	private RelativeLayout rl_pi_name,rl_pi_phone,rl_pi_secondary_phone;

	private AccountSettingTakePhotoPopup accountSettingTakePhotoPopup;
	private int StateId = -1, CityId = -1;
	private AllListFilterAdapter adapter;
	private int selectposition = -1;
	private ScrollView scrollView;
	private ListView listView;
	private static final int SUCCESSGETSEX = 0X110;
	private String filter = "";
	private LinearLayout sexchose;
	private List<ConditionBean> list = new ArrayList<ConditionBean>();
	// private WheelView tv_heightEdit;

	private WheelView year, month, day;
	PopupWindow menuWindow;
	private int myear, mmonth, mday;
	private LinearLayout pi_ll_birth_cancel, pi_ll_birth_save;
	private TextView tv_pi_name;
	private LinearLayout layout;
	private String UserID;
	private SharedPreferences sharedPreferences;
	List<ProvinceBean> provincelist = new ArrayList<ProvinceBean>();
	List<ProvinceBean> citylist = new ArrayList<ProvinceBean>();

	public PopupWindow mPopupWindow2;
	public static String path = "";
	public static String pathname = "";
	public static File cache, localFile, file;
	private static Bitmap.CompressFormat format;
	private static FileOutputStream stream;

	public static final String 亲 = "（づ￣3￣）づ╭❤～";

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
//				AppConfig.isUpdateCustomer = true;
					save();
					break;
				case 0x10:
					Toast.makeText(PersonalInfoActivity.this, msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
					break;
				case 0x00:
					/*if (name != null) {
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
					if (BirthDay != null && BirthDay.length() > 0) {
						tv_birthday.setText(BirthDay);
						String str[] = BirthDay.split("-");
						myear = Integer.parseInt(str[0]);
						mmonth = Integer.parseInt(str[1]);
						mday = Integer.parseInt(str[2]);
					}
					if (Mobile != null) {
						tv_phone.setText(Mobile.substring(0, 3) + "-"
								+ Mobile.substring(3, 7) + "-"
								+ Mobile.substring(7));
					}
					for (int i = 0; i < list.size(); i++) {
						if (Gender.equals(list.get(i).getFilterValueID())) {
							tv_sex.setText(list.get(i).getFilterValue());
							filter = list.get(i).getFilterValue();
						}
					}*/
					if (FullName != null) {
						tv_pi_name.setText(FullName);
					}
					if (PrimaryPhone != null) {
						tv_phone.setText(PrimaryPhone.substring(0, 3) + "-"
								+ PrimaryPhone.substring(3, 7) + "-"
								+ PrimaryPhone.substring(7));
					}
					if (newAvatarUrl != null) {
						Uri imageUri = Uri.parse(newAvatarUrl);
						tv_head.setImageURI(imageUri);
					}
					if (Email != null) {
						pi_tv_email.setText(Email);
					}
					if (Description != null) {
						pi_et_description.setText(Description);
					}
					if (SecondaryPhone != null) {
						pi_tv_secondaryphone.setText(SecondaryPhone);
					}
					//downloadAttachment();
					break;
				case AppConfig.SUCCESS:
				    Toast.makeText(PersonalInfoActivity.this, "个人资料修改成功",
							Toast.LENGTH_SHORT).show();
//				AppConfig.isUpdateCustomer = true;
					AppConfig.isUpdateDialogue = true;
					AppConfig.HASUPDATAINFO = true;
					sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
							MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("Name", name);
					editor.commit();

					EventBus.getDefault().post(new EventChangeAccout());
					finish();
					break;
				case AppConfig.FAILED:
					Toast.makeText(PersonalInfoActivity.this, msg.obj.toString(),
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
		setContentView(R.layout.pc_personal_info);
		sharedPreferences = PersonalInfoActivity.this.getSharedPreferences(
				AppConfig.LOGININFO, Context.MODE_PRIVATE);
//		UserID = sharedPreferences.getInt("UserID", 0) + "";
		UserID = AppConfig.UserID;
		initview();
		//getPersonInfo();
		getPersonInfo2();
	}

	private void confirmSex() {
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = ConnectService
						.getIncidentData(AppConfig.URL_PUBLIC
								+ AppConfig.WHO_DO_WHAT + "?" + "ChoiceTypeID="
								+ AppConfig.GETSEX);
				Log.e("User/Choices",jsonObject.toString() + "");
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
		}).start(((App) getApplication()).getThreadMgr());
	}

	private void getPersonInfo() {

		LoginGet loginget = new LoginGet();
		loginget.setDetailGetListener(new LoginGet.DetailGetListener() {

			@Override
			public void getUser(Customer user) {
				AvatarUrl = user.getUrl() + "";
				name = user.getName() + "";
				Gender = user.getSex() + "";
				Address = user.getAddress() + "";
				BirthDay = user.getBirthday() + "";
				Mobile = user.getPhone() + "";
				Log.e("老余", "personal:" + name + "," + Gender + "," + State
						+ "," + City + "," + Address + "," + BirthDay + ","
						+ Mobile + "," + AvatarUrl);
//				confirmSex();
				Message msg = new Message();
				msg.what = 0x00;
				msg.obj = list;
				handler.sendMessage(msg);

			}

			@Override
			public void getMember(Customer member) {
				// TODO Auto-generated method stub

			}
		});
		loginget.CustomerDetailRequest(getApplicationContext(), UserID);

	}


	/**
	 * 获取组织个人信息
	 */
	private void getPersonInfo2() {
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = ConnectService
						.getIncidentData(AppConfig.URL_PUBLIC
								+ "User/UserProfile");

				try {
					if (jsonObject.getInt("RetCode") != 200
							&& jsonObject.getInt("RetCode") != 0) {
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}


				customerYu = formatjsonYu(jsonObject);


				if (customerYu != null) {
					Message msg = new Message();
					msg.what = 0x00;
					msg.obj = customerYu;
					handler.sendMessage(msg);
				}
			}
		}).start(((App) getApplication()).getThreadMgr());
	}

	private CustomerYu formatjsonYu(JSONObject jsonObject) {
		CustomerYu bean = new CustomerYu();
		try {

			JSONObject object = jsonObject.getJSONObject("RetData");

			newAvatarUrl = object.getString("AvatarUrl");
			FullName = object.getString("FullName");
			FirstName = object.getString("FirstName");
			MiddleName = object.getString("MiddleName");
			PrimaryPhone = object.getString("PrimaryPhone");
			Email = object.getString("Email");
			Description = object.getString("Description");
			SecondaryPhone = object.getString("SecondaryPhone");
			UserID = object.getString("UserID");
			RongCloudID = object.getString("RongCloudID");
			Nickname = object.getString("Nickname");
			LastName = object.getString("LastName");
			LoginName = object.getString("LoginName");





			bean.setAvatarUrl(newAvatarUrl);
			bean.setFullName(FullName);
			bean.setFirstName(FirstName);
			bean.setMiddleName(MiddleName);
			bean.setPrimaryPhone(PrimaryPhone);
			bean.setEmail(Email);
			bean.setDescription(Description);
			bean.setSecondaryPhone(SecondaryPhone);
			bean.setLastName(LastName);
			Log.e("老余LG", "personal:" + FullName + "," + FirstName + "," + MiddleName
					+ "," + newAvatarUrl + "," + PrimaryPhone + "," + SecondaryPhone + ","
					+ Description + "," + Description);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bean;
	}


	//拿下链接设置头像
	public void downloadAttachment() {
		Log.e("zhang", "AvatarUrl:" + AvatarUrl);
		Uri imageUri = Uri.parse(AvatarUrl);
		tv_head.setImageURI(imageUri);
		/*RequestQueue requestQueue = Volley.newRequestQueue(PersonalInfoActivity.this);
		final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
				20);
		ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
			@Override
			public void putBitmap(String key, Bitmap value) {
				lruCache.put(key, value);
			}

			@Override
			public Bitmap getBitmap(String key) {
				return lruCache.get(key);
			}
		};
		ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(tv_head,
				R.drawable.hello, R.drawable.hello);

		imageLoader.get(AvatarUrl, listener);*/
	}

	private void initview() {
		im_imgback = (ImageView) findViewById(R.id.imgback);
		im_imgback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		pi_tv_email = (ContainsEmojiEditText) findViewById(R.id.pi_tv_email);
		pi_tv_secondaryphone = (TextView) findViewById(R.id.pi_tv_secondaryphone);
		pi_tv_email = (ContainsEmojiEditText) findViewById(R.id.pi_tv_email);
		pi_tv_secondaryphone = (TextView) findViewById(R.id.pi_tv_secondaryphone);
		pi_et_description = (EditText) findViewById(R.id.pi_et_description);



		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_save.setOnClickListener(new MyOnClick());
		tv_save.setVisibility(View.GONE);
		pi_tv_save = (TextView) findViewById(R.id.pi_tv_save);
		pi_tv_save.setOnClickListener(new MyOnClick());


		rl_pi_name = (RelativeLayout) findViewById(R.id.rl_pi_name);
		rl_pi_phone = (RelativeLayout) findViewById(R.id.rl_pi_phone);
		rl_pi_secondary_phone = (RelativeLayout) findViewById(R.id.rl_pi_secondary_phone);

		rl_pi_name.setOnClickListener(new MyOnClick());
		rl_pi_phone.setOnClickListener(new MyOnClick());
		rl_pi_secondary_phone.setOnClickListener(new MyOnClick());

		tv_topname = (TextView) findViewById(R.id.topname);
		tv_topname.setText(getResources().getString(R.string.personInfo));
		tv_img = (RelativeLayout) findViewById(R.id.editimghead);
		tv_img.setOnClickListener(new MyOnClick());
		tv_head = (SimpleDraweeView) findViewById(R.id.edithead);
		tv_head.setOnClickListener(new MyOnClick());
		ll_edit_area = (LinearLayout) findViewById(R.id.ll_edit_area_in);
		ll_edit_area.setOnClickListener(new MyOnClick());
		ll_sex = (LinearLayout) findViewById(R.id.ll_sex_in);
		ll_sex.setOnClickListener(new MyOnClick());
		// ll_phone_in = (LinearLayout) findViewById(R.id.ll_phone_in);
		// ll_phone_in.setOnClickListener(new MyOnClick());
		ll_birthday = (LinearLayout) findViewById(R.id.ll_birthday_in);
		ll_birthday.setOnClickListener(new MyOnClick());
		tv_area = (TextView) findViewById(R.id.edit_area_in);
		tv_sex = (TextView) findViewById(R.id.tv_sex_in);
		tv_phone = (TextView) findViewById(R.id.tv_phone_in);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday_in);
		tv_pi_name = (TextView) findViewById(R.id.tv_pi_name);


		ViewCompat.setTransitionName(tv_head, 亲);

		cache = new File(Environment.getExternalStorageDirectory(), "Image");
		if (!cache.exists()) {
			cache.mkdirs();
		}
		//getPopupWindowInstance2();
	}

	private void getPopupWindowInstance2() {
		if (null != mPopupWindow2) {
			mPopupWindow2.dismiss();
			return;
		} else {
			initPopuptWindow2();
		}
	}private void initPopuptWindow2() {
		LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		View popupWindow = layoutInflater
				.inflate(R.layout.pop_photo, null);
		TextView tv_gallery = (TextView) popupWindow
				.findViewById(R.id.tv_gallery);
		TextView tv_photo = (TextView) popupWindow
				.findViewById(R.id.tv_photo);

		tv_gallery.setOnClickListener(new MypopClick());
		tv_photo.setOnClickListener(new MypopClick());

		int width = getResources().getDisplayMetrics().widthPixels;
		// 创建一个PopupWindow
		// 参数1：contentView 指定PopupWindow的内容
		// 参数2：width 指定PopupWindow的width
		// 参数3：height 指定PopupWindow的height
		mPopupWindow2 = new PopupWindow(popupWindow, width - 100, (int) (85 * density),
				false);

		// getWindowManager().getDefaultDisplay().getWidth();
		// getWindowManager().getDefaultDisplay().getHeight();
		mPopupWindow2.getWidth();
		mPopupWindow2.getHeight();

		// 使其聚焦
		mPopupWindow2.setFocusable(true);
		// 设置允许在外点击消失
		mPopupWindow2.setOutsideTouchable(true);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		mPopupWindow2.setBackgroundDrawable(new BitmapDrawable());
	}


	private class MypopClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_gallery:
					GetGallery();
					break;
				case R.id.tv_photo:
					GotoPhoto();
					break;

				default:

			}

		}

	}
//去调用系统拍照界面
	public void GotoPhoto() {
		accountSettingTakePhotoPopup.dismiss();
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			Toast.makeText(this, "请插入SD卡", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		path = Environment.getExternalStorageDirectory().getPath();
		// 文件名
		path = DateFormat.format("yyyyMMdd_hhmmss",
				Calendar.getInstance(Locale.CHINA))
				+ ".jpg";
		localFile = new File(cache, path);
		//Android7.0文件保存方式改变了
		if (Build.VERSION.SDK_INT < 24) {
			Uri uri = Uri.fromFile(new File(cache, path));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		} else {
			ContentValues contentValues = new ContentValues(1);
			contentValues.put(MediaStore.Images.Media.DATA, localFile.getAbsolutePath());
			Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		}


		startActivityForResult(intent, 1);

	}

	public void GetGallery() {
		accountSettingTakePhotoPopup.dismiss();
		// 跳转至相册界面
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 0);

	}

	private class MyOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
				case R.id.edithead:
				case R.id.editimghead:
					//mPopupWindow2.showAtLocation(tv_head, Gravity.CENTER, 0, 0);
					accountSettingTakePhotoPopup = new AccountSettingTakePhotoPopup();
					accountSettingTakePhotoPopup.getPopwindow(getApplicationContext());
					accountSettingTakePhotoPopup.setFavoritePoPListener(new AccountSettingTakePhotoPopup.FavoritePoPListener() {

						@Override
						public void takePhoto() {
							GotoPhoto();
						}

						@Override
						public void filePhoto() {
							GetGallery();
						}

						@Override
						public void fileDeletePhoto() {

						}

						@Override
						public void dismiss() {

							Log.e("哈哈哈哈哈", "sdoafjiasdfasdfasdf");
							getWindow().getDecorView().setAlpha(1.0f);
						}

						@Override
						public void open() {
							getWindow().getDecorView().setAlpha(0.5f);
						}
					});
					accountSettingTakePhotoPopup.StartPop(tv_head);
					break;
				case R.id.ll_edit_area_in:
					intent = new Intent(PersonalInfoActivity.this,
							AreaEditActivity.class);
					startActivity(intent);
					break;
					//老余
				case R.id.rl_pi_name:
					intent = new Intent(PersonalInfoActivity.this,
                            ChangeNameActivity.class);
					//通过intent传入对象
					intent.putExtra("customerYu",customerYu);
					//设置标示
					startActivityForResult(intent,0x001);
					break;
				case R.id.rl_pi_phone:
					intent = new Intent(PersonalInfoActivity.this,
							ChangePhoneActivity.class);
					startActivity(intent);
					break;
				case R.id.rl_pi_secondary_phone:
					/*intent = new Intent(PersonalInfoActivity.this,
							ChangePhoneActivity.class);
					startActivity(intent);*/
					break;
				case R.id.ll_birthday_in:
					setbirthday(PersonalInfoActivity.this);
					break;
				case R.id.ll_sex_in:
					selectSex(PersonalInfoActivity.this);
					getSex();
					break;
/*				case R.id.editnamecontent_in:
					// 弹出软键盘
					@SuppressWarnings("static-access")
					final InputMethodManager inputManager = (InputMethodManager) editText
							.getContext().getSystemService(
									PersonalInfoActivity.this.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(editText, 0);
					break;*/
				//右上角提交按钮已隐藏
				case R.id.tv_save:
					if(path != null && path.length() > 0){
						uploadhead();
					} else {
						save();
					}
					break;
					//下方按钮提交
				case R.id.pi_tv_save:
					if(path != null && path.length() > 0){
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




	private void uploadhead() {
		RequestParams params = new RequestParams();
		params.setHeader("UserToken", AppConfig.UserToken);
		params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
		// params.setContentType("application/octet-stream");
		if (localFile.exists()){
			try {
				String baseurl = LoginGet.getBase64Password(pathname);
				String fileNamebase = URLEncoder.encode(baseurl, "UTF-8");
				params.addBodyParameter(localFile.getName(), localFile);
				params.addBodyParameter("fileNamebase", fileNamebase);
				params.addBodyParameter("UploadType", "0");
				params.addBodyParameter("UserID4Customer", UserID);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String url = AppConfig.URL_PUBLIC + "Avatar";
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

//保存填写的个人资料
	private void save() {
		name = tv_pi_name.getText().toString();
		final JSONObject jsonobject = format();
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC + "User/UpdateUserProfile",
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
	//保存填写的个人资料
	private JSONObject format() {
		JSONObject jsonObject = new JSONObject();
		//FullName = tv_pi_name.getText().toString();
		Email = pi_tv_email.getText().toString();
		Description = pi_et_description.getText().toString();
		try {
			/*jsonObject.put("UserID", UserID);
			jsonObject.put("name", name);
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
			}*/
			jsonObject.put("UserID", UserID);
			jsonObject.put("Nickname", Nickname);
			jsonObject.put("FirstName", customerYu.getFirstName());
			jsonObject.put("MiddleName", customerYu.getMiddleName());
			jsonObject.put("LastName", customerYu.getLastName());
			jsonObject.put("Description", Description);
			jsonObject.put("PrimaryPhone", PrimaryPhone);
			jsonObject.put("SecondaryPhone", SecondaryPhone);
			jsonObject.put("Email", Email);

			Log.e("老余/Choices",FullName + ","+FirstName+"."+MiddleName);

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
				BirthDay = (year.getCurrentItem() + 1950) + "-" + strm + "-"
						+ strd;
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

	// private void setHight(Context context) {
	// final LayoutInflater inflater = LayoutInflater.from(context);
	// View windov = inflater.inflate(R.layout.pi_editheight, null);
	// tv_heightEdit = (WheelView) windov.findViewById(R.id.tv_heightEdit);
	// tv_heightEdit.setAdapter(new NumericWheelAdapter(100, 300));
	// tv_heightEdit.setCyclic(true);
	// tv_heightEdit.addScrollingListener(scrollListener);
	// if (height != -1) {
	// tv_heightEdit.setCurrentItem(height - 100);
	// } else {
	// tv_heightEdit.setCurrentItem(70);
	// }
	// windov.findViewById(R.id.pi_ll_cancel).setOnClickListener(
	// new OnClickListener() {
	// @Override
	// public void onClick(View arg0) {
	// builder.dismiss();
	// }
	// });
	// windov.findViewById(R.id.pi_ll_save).setOnClickListener(
	// new OnClickListener() {
	// @Override
	// public void onClick(View arg0) {
	// Height = height + "";
	// tv_height.setText(Height);
	// builder.dismiss();
	// }
	// });
	// builder = new AlertDialog.Builder(context).show();
	// Window dialogWindow = builder.getWindow();
	// WindowManager m = ((Activity) context).getWindowManager();
	// Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
	// WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
	// 获取对话框当前的参数值
	// p.width = (int) (d.getWidth() * 0.8);
	// p.height = (int) (d.getHeight() * 0.5);
	// dialogWindow.setAttributes(p);
	// builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
	// builder.setContentView(windov);
	//
	// }

	// OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
	//
	// @Override
	// public void onScrollingStarted(WheelView wheel) {
	//
	// }
	//
	// @Override
	// public void onScrollingFinished(WheelView wheel) {
	// height = tv_heightEdit.getCurrentItem() + 100;
	// tv_height.setText(height + "");
	// }
	// };

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 系统相册返回
		if (requestCode == 0 && resultCode == Activity.RESULT_OK
				&& data != null) {

			path = FileUtils.getPath(this, data.getData());
			pathname = path.substring(path.lastIndexOf("/") + 1);
			try {
//                b = new FileOutputStream(localFile.getPath());
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b); // 把数据写入文件
//                BitmapChangeTool.saveImageToGallery(bitmap,localFile);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
                /*try {
//                    b.flush();
//                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
			}

//			Log.e("path", path + ":" + pathname);
			/*Bitmap bm = BitmapFactory.decodeFile(path);
			cimg_head.setImageBitmap(bm);*/
//        	cimg_head.setImageURI(data.getData());
//			startPhotoZoom(data.getData(), 150);// 截图

			Bitmap bm = BitmapFactory.decodeFile(path);

			SetAvCircle();
			tv_head.setImageURI(data.getData());
			FileOutputStream b = null;
			localFile = new File(cache, pathname);
			try {
				b = new FileOutputStream(localFile.getPath());
				bm.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 系统相机返回
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
        	/*path = FileUtils.getPath(this, data.getData());
        	pathname = path.substring(path.lastIndexOf("/") + 1);*/


			name = path;
			Log.e("duang", name + "   " + path + "   " + (cache != null) + "   " + localFile.getPath() + "   " + localFile.getAbsolutePath());
//            Bundle bundle = data.getExtras();
//            Bitmap bitmap = (Bitmap) bundle.get("data"); // 获取相机返回的数据，并转换为Bitmap图片格式
//            FileOutputStream b = null;
//			File localFile = new File(cache, name);

			pathname = path.substring(path.lastIndexOf("/") + 1);
//			startPhotoZoom(Uri.fromFile(localFile), 150);// 截图
//			startPhotoZoom(Uri.fromFile(new File(cache, path)), 150);// 截图
//			startPhotoZoom(Uri.fromFile(new File(path)), 150);// 截图
			tv_head.setImageURI(Uri.fromFile(localFile));



			/*new DateFormat();
			name = DateFormat.format("yyyyMMdd_hhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".png";
//			Bundle bundle = data.getExtras();
//			Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
			FileOutputStream b = null;
			localFile = new File(cache, name);
			path = localFile.getPath();
			pathname = path.substring(path.lastIndexOf("/") + 1);
			try {
				b = new FileOutputStream(localFile.getPath());
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			startPhotoZoom(Uri.fromFile(localFile), 150);// 截图*/


			// File file = new File(AppConfig.IMAGEURL);
			// if (file.exists()) {
			// file.delete();
			// }
			/*AppConfig.IMAGEURL = localFile.getPath();
			Log.e("paths", path + "");
			startPhotoZoom(Uri.fromFile(localFile), 150);// 截图

			SetAvCircle();
			tv_head.setImageURI(Uri.fromFile(localFile));*/
//        	cimg_head.setImageURI(Uri.fromFile(file));
		}
		// 截图后返回
		if (requestCode == 2 && data != null) {
			Bundle bundle = data.getExtras();
			Log.e("duang", (bundle!=null) + "   ");
			if (bundle != null) {

				Bitmap bitmap = bundle.getParcelable("data");
				Log.e("duang", bitmap + "   ");
				// 创建助手类的实例
				int size = bitmap.getWidth()*bitmap.getHeight()*4;
				//创建一个字节数组输出流,流的大小为size
				ByteArrayOutputStream baos=new ByteArrayOutputStream(size);
				//设置位图的压缩格式，质量为100%，并放入字节数组输出流中
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				//将字节数组输出流转化为字节数组byte[]
				byte[] imagedata1=baos.toByteArray();

//				SetAvCircle();
				Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,null,null));
				tv_head.setImageURI(uri);
//				tv_head.setImageBitmap(bitmap);
				//关闭字节数组输出流
				try {
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				file = new File(path);
				saveBitmap2file(
						bitmap,
						file.getName().toString());
			}
		}

		if(resultCode == 0x001){
			customerYu = (CustomerYu) data.getSerializableExtra("result");
			FullName = customerYu.getFullName();
			tv_pi_name.setText(FullName);
		}


	}

	private void SetAvCircle() {
		GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
		RoundingParams parames = RoundingParams.asCircle();
		GenericDraweeHierarchy hierarchy = builder.setRoundingParams(parames).build();
		tv_head.setHierarchy(hierarchy);
	}

	// bitmap 转化为文件 将截取的图片 保存在本地
	private boolean saveBitmap2file(Bitmap bmp, String filename) {
		format = Bitmap.CompressFormat.PNG;
		localFile = new File(cache, filename);
		int quality = 100;
		stream = null;
		try {
			stream = new FileOutputStream(localFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bmp.compress(format, quality, stream);
	}

	private void startPhotoZoom(Uri data, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		// crop为true时表示显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}
	private void startPhotoZoom2(String path, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");

		Uri uri = Uri.parse(path);
		intent.setDataAndType(uri, "image/*");
		// crop为true时表示显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", false);
		startActivityForResult(intent, 2);
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
		super.onResume();
		MobclickAgent.onPageStart("PersonalInfoActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("PersonalInfoActivity");
		MobclickAgent.onPause(this);
	}
}