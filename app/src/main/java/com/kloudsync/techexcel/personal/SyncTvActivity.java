package com.kloudsync.techexcel.personal;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.TvDeviceAdapterV2;
import com.kloudsync.techexcel.bean.TvDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.help.KloudPerssionManger;
import com.kloudsync.techexcel.response.BindTvStatusResponse;
import com.kloudsync.techexcel.response.DevicesResponse;
import com.kloudsync.techexcel.ui.DocAndMeetingActivityV2;
import com.mining.app.zxing.BindTvCapture;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_UPLOADFILE;
public class SyncTvActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
	private RelativeLayout backLayout;
	private TextView tv_title;
	private RecyclerView deviceList;
	private TvDeviceAdapterV2 adapter;
	ArrayList<TvDevice> mlist = new ArrayList();
	private TextView scanText;
	private LinearLayout devicesLayout;
	private TextView noDeviceText;
	private Switch enableStatusSwitch;
	private final int MESSAGE_TOAST_SUCC = 10001;
	private final int MESSAGE_TOAST_FAIL = 10002;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case AppConfig.FAILED:
					String result = (String) msg.obj;
					Toast.makeText(getApplicationContext(),
							result,
							Toast.LENGTH_LONG).show();
					break;

				case AppConfig.AddTempLesson:
					result = (String) msg.obj;
					GoToVIew(result);
					break;
				case AppConfig.LOAD_FINISH:
//                    GoToVIew();
					break;

				case MESSAGE_TOAST_SUCC:
					new CenterToast.Builder(SyncTvActivity.this).setSuccess(true).setMessage(getString(R.string.operate_success)).create().show();
					break;

				case MESSAGE_TOAST_FAIL:
					new CenterToast.Builder(SyncTvActivity.this).setSuccess(true).setMessage(getString(R.string.operate_failure)).create().show();
					break;

				default:
					break;
			}
		}
	};

	private void GoToVIew(String result) {
		Intent intent = new Intent(SyncTvActivity.this, DocAndMeetingActivityV2.class);
		intent.putExtra("userid", AppConfig.UserID);
		intent.putExtra("meetingId", result);
		intent.putExtra("teacherid", AppConfig.UserID);
		intent.putExtra("isTeamspace", true);
		intent.putExtra("lessionId", result);
		intent.putExtra("identity", 2);
		intent.putExtra("isStartCourse", true);
		intent.putExtra("isPrepare", true);
		intent.putExtra("isInstantMeeting", 0);
		intent.putExtra("yinxiangmode", 0);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync_tv);
		//initSlideTrans();
		findView();
		initView();
		getBindTvs();
	}

	LineItem item;

	private void findView() {
		backLayout = (RelativeLayout) findViewById(R.id.layout_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.sync_tv);
		scanText = (TextView) findViewById(R.id.txt_scan);
		scanText.setOnClickListener(this);
		deviceList = (RecyclerView) findViewById(R.id.list_device);
		final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		deviceList.setLayoutManager(manager);
		adapter = new TvDeviceAdapterV2(this, mlist);
		deviceList.setAdapter(adapter);
		devicesLayout = (LinearLayout) findViewById(R.id.layout_devices);
		noDeviceText = (TextView) findViewById(R.id.txt_no_devices);

	}

	private void getBindTvs() {
		ServiceInterfaceTools.getinstance().getBindTvs().enqueue(new Callback<DevicesResponse>() {

			@Override
			public void onResponse(Call<DevicesResponse> call, Response<DevicesResponse> response) {
				if (response != null && response.isSuccessful() && response.body() != null) {
					if (response.body().getData() != null) {
						List<TvDevice> devices = response.body().getData().getDeviceList();
						enableStatusSwitch.setOnCheckedChangeListener(null);
						enableStatusSwitch.setChecked(response.body().getData().isEnableBind());
						enableStatusSwitch.setOnCheckedChangeListener(SyncTvActivity.this);
						if (devices != null && devices.size() > 0) {
							devicesLayout.setVisibility(View.VISIBLE);
							noDeviceText.setVisibility(View.GONE);
							adapter.setDevices(devices);
						} else {
							devicesLayout.setVisibility(View.GONE);
							noDeviceText.setVisibility(View.VISIBLE);
						}
					} else {
						devicesLayout.setVisibility(View.GONE);
						noDeviceText.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void onFailure(Call<DevicesResponse> call, Throwable t) {

			}
		});

//        LoginGet.GetCurrentUserBindTvInfo(this);
	}

	private void requestChangeBindTvStatus(final int status) {
		ServiceInterfaceTools.getinstance().changeBindTvStatus(status).enqueue(new Callback<BindTvStatusResponse>() {
			@Override
			public void onResponse(Call<BindTvStatusResponse> call, Response<BindTvStatusResponse> response) {
				Log.e("requestChangeBs", "response:" + response);
				if (response != null && response.isSuccessful() && response.body() != null) {
					if (response.body().getCode() == 0) {
						handler.obtainMessage(MESSAGE_TOAST_SUCC).sendToTarget();
						getBindTvs();
					} else {
						handler.obtainMessage(MESSAGE_TOAST_FAIL).sendToTarget();
						enableStatusSwitch.setOnCheckedChangeListener(null);
						enableStatusSwitch.setChecked(!enableStatusSwitch.isChecked());
						enableStatusSwitch.setOnCheckedChangeListener(SyncTvActivity.this);

					}
				} else {
					handler.obtainMessage(MESSAGE_TOAST_FAIL).sendToTarget();
					enableStatusSwitch.setOnCheckedChangeListener(null);
					enableStatusSwitch.setChecked(!enableStatusSwitch.isChecked());
					enableStatusSwitch.setOnCheckedChangeListener(SyncTvActivity.this);
				}
			}

			@Override
			public void onFailure(Call<BindTvStatusResponse> call, Throwable t) {
				handler.obtainMessage(MESSAGE_TOAST_FAIL).sendToTarget();
				enableStatusSwitch.setOnCheckedChangeListener(null);
				enableStatusSwitch.setChecked(!enableStatusSwitch.isChecked());
				enableStatusSwitch.setOnCheckedChangeListener(SyncTvActivity.this);
			}
		});
	}


	private void initView() {
		backLayout.setOnClickListener(this);
		enableStatusSwitch = (Switch) findViewById(R.id.switch_sync_tv);
		enableStatusSwitch.setOnCheckedChangeListener(this);

	}

	private static final int REQUEST_SCAN = 1;

	private String[] permissions = new String[]{Manifest.permission.CAMERA};
	private void startRequestPermission(){
		if (KloudPerssionManger.isPermissionCameraGranted(this) &&KloudPerssionManger.isPermissionReadExternalStorageGranted(this)
				&& KloudPerssionManger.isPermissionExternalStorageGranted(this)) {
			Intent intent = new Intent(this, BindTvCapture.class);
			intent.putExtra("isHorization", false);
			startActivityForResult(intent, REQUEST_SCAN);
		}else {
			ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_UPLOADFILE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.layout_back:
				finish();
				break;
			case R.id.txt_scan:
				startRequestPermission();
				break;
			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_SCAN) {
				getBindTvs();
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		requestChangeBindTvStatus(isChecked ? 1 : 0);
	}
}
