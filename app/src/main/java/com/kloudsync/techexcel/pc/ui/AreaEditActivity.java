package com.kloudsync.techexcel.pc.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.user.techexcel.pi.tools.AdressAdapter;
import com.kloudsync.user.techexcel.pi.tools.ProvinceBean;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AreaEditActivity extends Activity {
	private TextView city, province;
	private EditText tv_editstreet;
	private ListView chosewhichcity;
	private String inprovince;
	private String incity;
	private String instreet;
	private ProvinceBean pbean,cbean;
	AdressAdapter provinceAdapter = null;
	AdressAdapter cityAdapter = null;
	static int provincePosition = 3;
	List<ProvinceBean> provincelist = new ArrayList<ProvinceBean>();
	List<ProvinceBean> citylist = new ArrayList<ProvinceBean>();
	private TextView tv_topname, tv_save;
	private String settvname;
	private ImageView img;
	private ProvinceBean provincebean = new ProvinceBean();
	private ProvinceBean citybean = new ProvinceBean();
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x11:
				for (int i = 0; i < provincelist.size(); i++) {
					if (provincelist.get(i).getName().equals(inprovince)) {
						getCity(i + 1);
					}
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pi_areaedit);
		inprovince = AppConfig.PROVINCE;
		incity = AppConfig.CITY;
		instreet = AppConfig.STREET;
		pbean = AppConfig.STATEBEAN;
		cbean = AppConfig.CITYBEAN;
		getProvince();
		initview();
	}

	private void initview() {
		tv_topname = (TextView) findViewById(R.id.topname);
		settvname = getString(R.string.area) + "";
		tv_topname.setText(settvname);
		tv_editstreet = (EditText) findViewById(R.id.editstreet);
		if (instreet != null) {
			tv_editstreet.setText(instreet);
		}
		province = (TextView) findViewById(R.id.spin_province);
		if (inprovince != null) {
			province.setText(inprovince);
		}
		province.setOnClickListener(new MyOnClick());
		city = (TextView) findViewById(R.id.spin_city);
		if (incity != null) {
			city.setText(incity);
		}
		city.setOnClickListener(new MyOnClick());
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AppConfig.STREET = tv_editstreet.getText().toString() + "";
				if (AppConfig.PROVINCE == null || AppConfig.PROVINCE == ""
						|| AppConfig.CITY == null || AppConfig.CITY == ""
						|| AppConfig.STREET == null || AppConfig.STREET == "") {
					Toast.makeText(AreaEditActivity.this, "输入完整的地址信息",
							Toast.LENGTH_SHORT).show();
				} else {
					finish();
				}
			}
		});
		img = (ImageView) findViewById(R.id.imgback);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AppConfig.PROVINCE = inprovince;
				AppConfig.CITY = incity;
				AppConfig.STREET = instreet;
				AppConfig.STATEBEAN = pbean;
				AppConfig.CITYBEAN = cbean;
				if (AppConfig.PROVINCE == null) {
					AppConfig.PROVINCE = "";
				}
				if (AppConfig.CITY == null) {
					AppConfig.CITY = "";
				}
				
				finish();
			}
		});

	}

	private class MyOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.spin_city:
				setCity(AreaEditActivity.this);
				break;
			case R.id.spin_province:
				setProvince(AreaEditActivity.this);
				break;
			default:
				break;
			}
		}

	}

	private AlertDialog builder;

	private void setProvince(Context context) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View windov = inflater.inflate(R.layout.pi_chosecity, null);
		chosewhichcity = (ListView) windov.findViewById(R.id.tv_chosewitchcity);
		chosewhichcity.setAdapter(new AdressAdapter(context, provincelist));
		chosewhichcity.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				province.setText(provincelist.get(arg2).getName());
				AppConfig.PROVINCE = provincelist.get(arg2).getName() + "";
				ProvinceBean bean = new ProvinceBean();
				bean.setID(provincelist.get(arg2).getID());
				bean.setName(provincelist.get(arg2).getName());
				AppConfig.STATEBEAN = bean;
				getCity(arg2 + 1);
				city.setText("");
				tv_editstreet.setText("");
				AppConfig.CITY = "";
				AppConfig.CITYBEAN = new ProvinceBean();
				AppConfig.STREET = "";
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

	private void setCity(Context context) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View windov = inflater.inflate(R.layout.pi_chosecity, null);
		chosewhichcity = (ListView) windov.findViewById(R.id.tv_chosewitchcity);
		chosewhichcity.setAdapter(new AdressAdapter(context, citylist));
		chosewhichcity.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				city.setText(citylist.get(arg2).getName());
				AppConfig.CITY = citylist.get(arg2).getName();
				ProvinceBean bean = new ProvinceBean();
				bean.setID(citylist.get(arg2).getID());
				bean.setName(citylist.get(arg2).getName());
				AppConfig.CITYBEAN = bean;
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

	private void getProvince() {
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = ConnectService
						.getIncidentbyHttpGet(AppConfig.URL_PUBLIC
								+ AppConfig.WHO_DO_WHAT + "?" + "ChoiceTypeID="
								+ AppConfig.GTEPROVINCE);
				Log.e("zhang", jsonObject.toString() + "");
				JSONArray jsonarray;
				try {
					jsonarray = jsonObject.getJSONArray("RetData");
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject object = jsonarray.getJSONObject(i);
						ProvinceBean bean = new ProvinceBean();
						bean.setID(object.getInt("ID"));
						bean.setName(object.getString("Name").substring(3));
						provincelist.add(bean);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = 0x11;
				handler.sendEmptyMessage(msg.what);
			}
		}).start(ThreadManager.getManager());
	}

	private void getCity(final int i) {
		citylist.clear();
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = ConnectService
						.getIncidentbyHttpGet(AppConfig.URL_PUBLIC
								+ AppConfig.WHO_DO_WHAT + "?" + "ChoiceTypeID="
								+ AppConfig.GTECITY + "&" + "ParentChoiceID="
								+ i);
				Log.e("zhang", jsonObject.toString() + "");
				JSONArray jsonarray;
				try {
					jsonarray = jsonObject.getJSONArray("RetData");
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject object = jsonarray.getJSONObject(i);
						ProvinceBean bean = new ProvinceBean();
						bean.setID(object.getInt("ID"));
						bean.setName(object.getString("Name").substring(5));
						citylist.add(bean);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start(ThreadManager.getManager());
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("AreaEditActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("AreaEditActivity");
		MobclickAgent.onPause(this);
	}
}
