package com.kloudsync.techexcel.pc.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.http.RequestParams;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

public class UpLoadHeadActivity extends Activity {
	private ImageView imgback;
	private TextView topname, tv_save;
	private static File cache, localFile, file;
	private static ContentResolver contentResolver;
	private static String path;
	private static CircleImageView mFace;
	private static Bitmap bitmap;
	private static Bundle bundle;
	private static Cursor cursor;
	private static Uri uri;
	private static String sdStatus;
	private static String name;
	private static FileOutputStream b;
	private static CompressFormat format;
	private static FileOutputStream stream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pc_uploadhead);
		intview();
	}

	private void intview() {
		topname = (TextView) findViewById(R.id.topname);
		topname.setText(getString(R.string.head));
		imgback = (ImageView) findViewById(R.id.imgback);
		imgback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				save();
			}
		});
		mFace = (CircleImageView) findViewById(R.id.uploadhead);
		// editText = (EditText) findViewById(R.id.edit);
		contentResolver = getContentResolver();
		// client = new AsyncHttpClient();
		// 图片缓存 目录
		cache = new File(Environment.getExternalStorageDirectory(), "Image");
		if (!cache.exists()) {
			cache.mkdirs();
		}
	}

	public void save() {
		if (localFile == null) {
			finish();
		} else {
			RequestParams params = new RequestParams();
			params.setHeader("UserToken", AppConfig.UserToken);
			params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
			if (localFile.exists()) {
				params.addBodyParameter(localFile.getName(), localFile);
				try {
					String name = localFile.getName();
					String baseurl = LoginGet.getBase64Password(name);
					String fileNamebase = URLEncoder.encode(baseurl, "UTF-8");
					params.addBodyParameter("fileNamebase", fileNamebase);
					AppConfig.PARAMS = params;
					AppConfig.UPLOADSTATIC = true;
					finish();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						getString(R.string.nofile), Toast.LENGTH_LONG).show();
			}
		}

	}

	public void selectimage(View view) {
		// 跳转至相册界面
		Intent intent = new Intent(Intent.ACTION_PICK,
				Media.EXTERNAL_CONTENT_URI);
		intent.setDataAndType(Media.EXTERNAL_CONTENT_URI,
				"image/*");
		this.startActivityForResult(intent, 0);
	}

	public void selectcamera(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == Activity.RESULT_OK
				&& data != null) {
			uri = data.getData();
			String lastname = uri.getPath().substring(
					uri.getPath().length() - 4);
			Log.e("zhang", lastname);
			if (lastname.equals(".png") || lastname.equals(".PNG")
					|| lastname.equals(".jpg") || lastname.equals(".JPG")) {
				ContentResolver cr = this.getContentResolver();
				try {
					InputStream is = cr.openInputStream(uri);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = false;
					options.inSampleSize = 10;
					Bitmap btp = BitmapFactory.decodeStream(is, null, options);
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					CircleImageView imageView = (CircleImageView) findViewById(R.id.uploadhead);
					imageView.setImageBitmap(btp);
					localFile = new File(uri.getPath());
					AppConfig.IMAGEURL = uri.getPath();
				} catch (FileNotFoundException e) {
					Log.e("Exception", e.getMessage(), e);
				}
			} else {
				cursor = contentResolver.query(uri, new String[] { "_data" },
						null, null, null);
				cursor.moveToFirst();
				path = cursor.getString(0);
				startPhotoZoom(data.getData(), 100); // 截图
			}
		}
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.i("TestFile",
						"SD card is not avaiable/writeable right now.");
				return;
			}
			new DateFormat();
			name = DateFormat.format("yyyyMMdd_hhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".png";
			bundle = data.getExtras();
			bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
			b = null;
			localFile = new File(cache, name);
			path = localFile.getPath();
			try {
				b = new FileOutputStream(localFile.getPath());
				bitmap.compress(CompressFormat.JPEG, 100, b);// 把数据写入文件
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
			startPhotoZoom(Uri.fromFile(file), 100); // 截图
		}
		// 截图后返回
		if (requestCode == 2 && data != null) {
			bundle = data.getExtras();
			if (bundle != null) {
				bitmap = bundle.getParcelable("data");
				mFace.setImageBitmap(bitmap);
				file = new File(path);
				saveBitmap2file(
						bitmap,
						file.getName().toString()
								.substring(0, file.getName().length() - 4)
								+ ".png"); // 以文件形式保存在手机上
				AppConfig.IMAGEURL = localFile.getPath();
			}
		}
	}

	// bitmap 转化为文件 将截取的图片 保存在本地
	private boolean saveBitmap2file(Bitmap bmp, String filename) {
		format = CompressFormat.PNG;
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

	// 截图
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

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("UpLoadHeadActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("UpLoadHeadActivity");
		MobclickAgent.onPause(this);
	}
}
