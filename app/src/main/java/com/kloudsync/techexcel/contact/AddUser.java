package com.kloudsync.techexcel.contact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeightAdapter;
import com.kloudsync.techexcel.adapter.SexAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.PopUbaoMan;
import com.kloudsync.techexcel.help.PopUbaoMan.PoPDismissListener;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Sex;
import com.kloudsync.techexcel.pc.ui.AreaEditActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.UserGetListener;
import com.kloudsync.techexcel.tool.ContainsEmojiEditText;
import com.kloudsync.techexcel.view.CircleImageView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddUser extends Activity {
	
	private RelativeLayout rl_head;
	private LinearLayout lin_phone, lin_name, lin_ub_man, lin_local, lin_sex, lin_height, lin_birth, lin_weight;
	private TextView tv_cancel, tv_care, tv_create, tv_ub_man;
	private TextView tv_local, tv_sex, tv_height, tv_birth;
	private EditText et_phone, et_weight;
	private ContainsEmojiEditText et_name;
	private ListView lv_show;
	private CircleImageView cimg_head;
	
	public static File cache, localFile, file;

	public PopupWindow mPopupWindow;
	public PopupWindow mPopupWindow2;
	
	private SexAdapter sadapter;
	private HeightAdapter hadapter;
	
	LoginGet lg;
	
	private ArrayList<Sex> slist = new ArrayList<Sex>();
	private ArrayList<String> hlist = new ArrayList<String>();
	
	
	float density;
	
	private int flag = -1;
	
	private EditText et_info;

	private String telephone;
	private String name;
	private String weight;
	
	String sexId = "-1";
	String heightId = "-1";
	
	int State;
	int City;
	String Address = "";
	
	public static String path = "";
	public static String pathname = "";
	
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case AppConfig.CREATE_USER:
				String result = (String) msg.obj;
				AppConfig.isUpdateCustomer = true;
				MyJson(result);
				break;
			case AppConfig.UPLOADHEAD:
				Toast.makeText(AddUser.this,
						getString(R.string.uploadsuccess), Toast.LENGTH_SHORT)
						.show();
				AppConfig.isUpdateCustomer = true;
				finish();
				break;
			case AppConfig.NO_NETWORK:
				Toast.makeText(
						AddUser.this,
						getResources().getString(R.string.No_networking),
						1000).show();
				
				break;
			case AppConfig.NETERROR:
				Toast.makeText(
						AddUser.this,
						getResources().getString(R.string.NETWORK_ERROR),
						1000).show();
				
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adduser);
		
		getDen();
		initView();
		getPopupWindowInstance();
		getPopupWindowInstance2();
	}

	protected void MyJson(String result) {
		try {
			JSONObject obj = new JSONObject(result);
			String RetCode = obj.getString("RetCode");
			if(RetCode.equals(AppConfig.RIGHT_RETCODE)){
				JSONObject RetData = obj.getJSONObject("RetData");
				UserID = RetData.getString("UserID");
				IsNew = RetData.getBoolean("IsNew");

				if(path != null && path.length() > 0){
					uploadFile();
				}else{
//					finish();
				}
				UbaoManCheck();
				Log.e("result", UserID + ":" + IsNew);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void UbaoManCheck() {
		if (1 != AppConfig.UserType) {
			PopUbaoMan pum = new PopUbaoMan();
			WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			int width = getResources().getDisplayMetrics().widthPixels;
			int height = getResources().getDisplayMetrics().heightPixels;
			pum.getPopwindow(AddUser.this, width, height,
					getString(R.string.Add_Customer_Pop));
			pum.setPoPDismissListener(new PoPDismissListener() {

				@Override
				public void PopDismiss() {
					finish();

				}
			});
			pum.StartPop(et_name);
		}else {
			finish();
		}
	}
	
	private String UserID;
	private boolean IsNew;
	/**
	 * 文件上传
	 *
	 */
	public void uploadFile() {
		RequestParams params = new RequestParams();
		params.setHeader("UserToken", AppConfig.UserToken);

		params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
		// params.setContentType("application/octet-stream");
		File file = new File(path);
		if (file.exists()) {
			/*
			 * // 对文件名进行编码 try { attachmentBean.setFileName(URLEncoder.encode(
			 * attachmentBean.getFileName(), "UTF-8")); Log.e("urlencodername",
			 * attachmentBean.getFileName() + ""); } catch
			 * (UnsupportedEncodingException e) { e.printStackTrace(); }
			 */
			params.addBodyParameter(pathname, file);
			String url = null;
			try {
				String baseurl = LoginGet.getBase64Password(pathname);
				String fileNamebase = URLEncoder.encode(baseurl, "UTF-8");

				// Log.e("base64", baseurl+"       "+fileNamebase);
				/*
				 * String fileNamebase = URLEncoder.encode(
				 * LoginGet.getBase64Password(nmae.substring(0,
				 * nmae.lastIndexOf("."))),
				 * "UTF-8")+nmae.substring(nmae.lastIndexOf("."));
				 */
				url = AppConfig.URL_PUBLIC + "Avatar?Uploadtype=1&UserID4Customer=" + UserID;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.e("url", url);
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("UTF-8");
			http.send(HttpRequest.HttpMethod.POST, url, params,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
							Toast.makeText(getApplicationContext(),
									getString(R.string.upload),
									Toast.LENGTH_LONG).show();
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {

						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							Message message = new Message();
							message.what = AppConfig.UPLOADHEAD;
							handler.sendEmptyMessage(message.what);
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.e("error", msg.toString());
							Toast.makeText(getApplicationContext(),
									getString(R.string.uploadfailure),
									Toast.LENGTH_SHORT).show();
						}
					});
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.nofile),
					Toast.LENGTH_LONG).show();
		}

	}

	private void getDen() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		
		AppConfig.PROVINCE = null;
		AppConfig.CITY = null;
		AppConfig.STREET = null;
		
	}

	private void initView() {
		AppConfig.UbaoMan = 0;
		rl_head = (RelativeLayout) findViewById(R.id.rl_head);
		lin_phone = (LinearLayout) findViewById(R.id.lin_phone);
		lin_name = (LinearLayout) findViewById(R.id.lin_name);
		lin_local = (LinearLayout) findViewById(R.id.lin_local);
		lin_sex = (LinearLayout) findViewById(R.id.lin_sex);
		lin_height = (LinearLayout) findViewById(R.id.lin_height);
		lin_birth = (LinearLayout) findViewById(R.id.lin_birth);
		lin_weight = (LinearLayout) findViewById(R.id.lin_weight);
		lin_ub_man = (LinearLayout) findViewById(R.id.lin_ub_man);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		tv_care = (TextView) findViewById(R.id.tv_care);
		tv_create = (TextView) findViewById(R.id.tv_create);
		tv_ub_man = (TextView) findViewById(R.id.tv_ub_man);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_name = (ContainsEmojiEditText) findViewById(R.id.et_name);
		et_weight = (EditText) findViewById(R.id.et_weight);
		tv_local = (TextView) findViewById(R.id.tv_local);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		tv_height = (TextView) findViewById(R.id.tv_height);
		tv_birth = (TextView) findViewById(R.id.tv_birth);
		cimg_head = (CircleImageView) findViewById(R.id.cimg_head); 
		
		cache = new File(Environment.getExternalStorageDirectory(), "Image");
		if (!cache.exists()) {
			cache.mkdirs();
		}
		lin_ub_man.setVisibility(1 == AppConfig.UserType ? View.GONE
				: View.VISIBLE);

		SetMyLogintGet();
		
		rl_head.setOnClickListener(new myOnClick());
		lin_local.setOnClickListener(new myOnClick());
		lin_sex.setOnClickListener(new myOnClick());
		lin_height.setOnClickListener(new myOnClick());
		lin_birth.setOnClickListener(new myOnClick());
		lin_weight.setOnClickListener(new myOnClick());
		lin_ub_man.setOnClickListener(new myOnClick());
		tv_cancel.setOnClickListener(new myOnClick());
		tv_create.setOnClickListener(new myOnClick());
	}

	private void SetMyLogintGet() {
		lg = new LoginGet();
		lg.setUserGetListener(new UserGetListener() {
			
			@Override
			public void getSex(ArrayList<Sex> sex_list) {
				slist = new ArrayList<Sex>();
				slist.addAll(sex_list);
				sadapter = new SexAdapter(getApplicationContext(), slist);
				sadapter.SelectedItem(sexId);
				lv_show.setAdapter(sadapter);
				lv_show.setOnItemClickListener(new SexOnitem());
			}

			@Override
			public void getMobile(String retcode, String ErrorMessage) {
				if(retcode.equals("-2004")){
					tv_care.setText(ErrorMessage);
					tv_care.setVisibility(View.VISIBLE);
				}else {
					tv_care.setVisibility(View.GONE);
					SendToCreate();
				}
				
			}
		});
	}

	protected void SendToCreate() {
		final JSONObject jsonObject = format();
        new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC
									+ "User/CreateCustomer", jsonObject);
					Log.e("返回的jsonObject", jsonObject.toString() + "");
					Log.e("返回的responsedata", responsedata.toString() + "");
					String retcode = responsedata.getString("RetCode");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						msg.what = AppConfig.CREATE_USER;		
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

	private JSONObject format() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("Mobile", telephone);
			jsonObject.put("Password", LoginGet.getBase64Password("123456").trim());
			jsonObject.put("Name", name);
			jsonObject.put("State", State);
			weight = et_weight.getText().toString();
			if(weight != null && weight.length() > 0){
				jsonObject.put("Weight", weight);
			}
			if(null != AppConfig.CITY ){
				jsonObject.put("City", City);
			}
			jsonObject.put("Address", Address);
			if(!sexId.equals("-1")){
				jsonObject.put("Gender", sexId);				
			}
			if(!heightId.equals("-1")){
				jsonObject.put("Height", heightId);				
			}
			String birth = tv_birth.getText().toString();
			if(null != birth && birth.length() > 0){
				jsonObject.put("BirthDay", birth);	
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject;
	}

	protected class myOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_head:
				mPopupWindow2.showAtLocation(lin_phone, Gravity.CENTER, 0, 0);				
				break;
			case R.id.lin_phone:
				break;
			case R.id.lin_name:								
				break;
			case R.id.lin_weight:				
				break;
			case R.id.lin_local:
				GoToArea();
				break;
			case R.id.lin_sex:
				getSex();
				break;
			case R.id.lin_height:
				getHeight();
				break;
			case R.id.lin_ub_man:
				getUbMan();
				break;
			case R.id.lin_birth:
				setDay();
				break;
			case R.id.tv_cancel:
				finish();
				break;
			case R.id.tv_create:
				CreateUser();
				break;

			default:
				break;
			}
			
		}

		
	}

	private void GoToArea() {
		Intent intent = new Intent(AddUser.this,
				AreaEditActivity.class);
		startActivity(intent);
	}
	
	public void getUbMan() {
		Intent intent = new Intent(AddUser.this,
				SelectUBMan.class);
		startActivity(intent);
		
	}

	public void CreateUser() {
		if(CheckCanCreate()){
			lg.MobileRequest(AddUser.this, telephone);
		}
		
	}
	
	
	private boolean CheckCanCreate() {
		telephone = et_phone.getText().toString();
		name = et_name.getText().toString();
		if ((null == telephone || telephone.length() <= 0)
				|| (null == name || name.length() <= 0)
				|| (null == Address || Address.length() <= 0)) {
			Toast.makeText(getApplicationContext(), "必填项请填写完整", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	/*
	 * 获取PopupWindow实例
	 */
	private void getPopupWindowInstance() {
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	private void getPopupWindowInstance2() {
		if (null != mPopupWindow2) {
			mPopupWindow2.dismiss();
			return;
		} else {
			initPopuptWindow2();
		}
	}

	public void getHeight() {
		mPopupWindow.showAtLocation(lin_height, Gravity.CENTER, 0, 0);
		hlist = new ArrayList<String>();
		for (int i = 0; i < 150; i++) {
			String height = (100 + i) + "";
			hlist.add(height);
		}
		hadapter = new HeightAdapter(getApplicationContext(), hlist);
		hadapter.SelectedItem(heightId);
		lv_show.setAdapter(hadapter);
		lv_show.setOnItemClickListener(new HeightOnitem());
		lv_show.setSelection(71);
		
	}

	public void getSex() {
		mPopupWindow.showAtLocation(lin_sex, Gravity.CENTER, 0, 0);
		lg.SexRequest(AddUser.this);
		
	}

	/*
	 * 创建PopupWindow
	 */
	@SuppressWarnings("deprecation")
	private void initPopuptWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		View popupWindow = layoutInflater
				.inflate(R.layout.pop_edit, null);
		lv_show = (ListView) popupWindow
				.findViewById(R.id.lv_show);

		
		int width = getResources().getDisplayMetrics().widthPixels;
		// 创建一个PopupWindow
		// 参数1：contentView 指定PopupWindow的内容
		// 参数2：width 指定PopupWindow的width
		// 参数3：height 指定PopupWindow的height
		mPopupWindow = new PopupWindow(popupWindow, width - 100, (int) (200 * density),
				false);

		// getWindowManager().getDefaultDisplay().getWidth();
		// getWindowManager().getDefaultDisplay().getHeight();
		mPopupWindow.getWidth();
		mPopupWindow.getHeight();

		// 使其聚焦
		mPopupWindow.setFocusable(true);
		// 设置允许在外点击消失
		mPopupWindow.setOutsideTouchable(true);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	
	@SuppressWarnings("deprecation")
	private void initPopuptWindow2() {
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
	
	private class SexOnitem implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Sex sex = slist.get(position);
			sexId = sex.getID();
			sadapter.updateListView(slist, sex.getID());
			mPopupWindow.dismiss();
			tv_sex.setText(sex.getName());
		}
		
	}
	
	private class HeightOnitem implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String height = hlist.get(position);
			heightId = height;
			hadapter.updateListView(hlist, height);
			mPopupWindow.dismiss();
			tv_height.setText(height + "cm");
		}
		
	}
	

	public void GotoPhoto() {
		mPopupWindow2.dismiss();
        if (!Environment.MEDIA_MOUNTED.equals(Environment  
                .getExternalStorageState())) {  
            Toast.makeText(this, "请插入SD卡", Toast.LENGTH_SHORT).show();  
            return;  
        }  
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		
        startActivityForResult(intent, 1);  
		
	}

	public void GetGallery() {
		mPopupWindow2.dismiss();
        // 跳转至相册界面  
        Intent intent = new Intent(Intent.ACTION_PICK,  
                Media.EXTERNAL_CONTENT_URI);  
        startActivityForResult(intent, 0); 
		
	}
	
	private boolean flag_ph = false;
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        // 系统相册返回  
        if (requestCode == 0 && resultCode == Activity.RESULT_OK  
                && data != null) { 
        	path = FileUtils.getPath(this, data.getData());
        	pathname = path.substring(path.lastIndexOf("/") + 1);

        	Log.e("path", path + ":" + data.getData());
			/*Bitmap bm = BitmapFactory.decodeFile(path);
			cimg_head.setImageBitmap(bm);*/
//        	cimg_head.setImageURI(data.getData());
        	
            startPhotoZoom(data.getData(), 150);// 截图  
        }  
        // 系统相机返回  
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) { 
        	/*path = FileUtilsType.getPath(this, data.getData());
        	pathname = path.substring(path.lastIndexOf("/") + 1);*/

        	new DateFormat();
			name = DateFormat.format("yyyyMMdd_hhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".png";
			Bundle bundle = data.getExtras();
			Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
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
			// File file = new File(AppConfig.IMAGEURL);
			// if (file.exists()) {
			// file.delete();
			// }
			AppConfig.IMAGEURL = localFile.getPath();
			file = new File(AppConfig.IMAGEURL);
			Log.e("paths", path + "");
        	startPhotoZoom(Uri.fromFile(file), 150);// 截图  
//        	cimg_head.setImageURI(Uri.fromFile(file));
        }  
        // 截图后返回  
        if (requestCode == 2 && data != null) {  
            Bundle bundle = data.getExtras();  
            if (bundle != null) {

        		
                Bitmap bitmap = bundle.getParcelable("data");  

                 // 创建助手类的实例
	             int size = bitmap.getWidth()*bitmap.getHeight()*4;
	             //创建一个字节数组输出流,流的大小为size
		         ByteArrayOutputStream baos=new ByteArrayOutputStream(size);
		         //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
		         bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		         //将字节数组输出流转化为字节数组byte[]
		         byte[] imagedata1=baos.toByteArray();
	
		         cimg_head.setImageBitmap(bitmap);
		         //关闭字节数组输出流
		         try {
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            }  
        }  
    }  
	
	/** 
     * 跳转至系统截图界面进行截图 
     *  
     * @param data 
     * @param size 
     */  
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
	
	public void setDay() {
		initializeViews2();
	}
	
	private static final int SHOW_DATAPICK = 0;
	private static final int DATE_DIALOG_ID = 1;
	private int mYear = 1990;
	private int mMonth = 0;
	private int mDay = 1;

	private void initializeViews2() {
		Message msg = new Message();
		msg.what = SHOW_DATAPICK;
		AddUser.this.dateandtimeHandler.sendMessage(msg);

	}
	
	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDateDisplay();
		}
	};

	protected void updateDateDisplay() {
		tv_birth.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	/**
	 * 处理日期和时间控件的Handler
	 */
	Handler dateandtimeHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;
			}
		}
	};
	


	private void GetUBMAN(int s) {
		switch (s) {
		case 0:
			tv_ub_man.setText("让系统分配");
			break;
		case 1:
			tv_ub_man.setText("预留给自己");
			break;

		default:
			break;
		}
		
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (AppConfig.PROVINCE != null || AppConfig.CITY != null
				|| AppConfig.STREET != null) {
			tv_local.setText(AppConfig.STATEBEAN.getName()
					+ AppConfig.CITYBEAN.getName() + AppConfig.STREET);
			State = AppConfig.STATEBEAN.getID();
			if(null != AppConfig.CITY ){
				City = AppConfig.CITYBEAN.getID();
			}
			Address = AppConfig.STREET;
		}
		
		GetUBMAN(AppConfig.UbaoMan);
	}
	



}
