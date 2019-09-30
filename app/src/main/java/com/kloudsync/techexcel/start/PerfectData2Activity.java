package com.kloudsync.techexcel.start;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.PopPerfactRole;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.view.CircleImageView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.FileUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.view.View.VISIBLE;
import static io.rong.imkit.utilities.RongUtils.density;

public class PerfectData2Activity extends Activity {
    private TextView tv_back;
    private TextView tv_warn;
    private TextView tv_role;
    private TextView tv_save;
    private CircleImageView cimg_head;
    private EditText et_password, et_password2, et_UserID, et_name;
    private RelativeLayout rl_head;
    private LinearLayout lin_role, lin_main;


    private String Mobile, Password, AccessCode, name;
    private int countrycode;
    private int Role;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PopupWindow mPopupWindow2;

    public static String path = "";
    public static String pathname = "";
    public static File cache, localFile, file;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.SUCCESS:
                    LoginActivity.instance.finish();
                    RegisterActivity.instance.finish();
                    Toast.makeText(PerfectData2Activity.this, getResources().getString(R.string.Register_Success),
                            Toast.LENGTH_SHORT).show();
                    sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                            MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putInt("countrycode", countrycode);
                    editor.putString("telephone", Mobile);
                    editor.putString("password", LoginGet.getBase64Password(Password).trim());
                    editor.commit();
                    LoginGet.LoginRequest(PerfectData2Activity.this, "+"
                                    + countrycode + Mobile, Password, 2, sharedPreferences,
                            editor, ((App) getApplication()).getThreadMgr());
                    break;
                case AppConfig.SAVESUCCESS:
                    if (path != null && path.length() > 0) {
                        AppConfig.UserID = msg.obj.toString();
                        uploadFile();
                    } else {
                        Message msg2 = new Message();
                        msg2.what = AppConfig.SUCCESS;
                        handler.sendEmptyMessage(msg2.what);
                    }
                    break;
                case AppConfig.FAILED:
                    Toast.makeText(PerfectData2Activity.this,
                            msg.obj.toString(), Toast.LENGTH_SHORT)
                            .show();
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect_data2);
        initvalue();
        initview();
    }

    private void initvalue() {
        countrycode = Integer.parseInt(getIntent().getStringExtra("countrycode"));
        Mobile = getIntent().getStringExtra("telephone");
        AccessCode = getIntent().getStringExtra("AccessCode");
    }

    private void initview() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_warn = (TextView) findViewById(R.id.tv_warn);
        tv_role = (TextView) findViewById(R.id.tv_role);
        tv_save = (TextView) findViewById(R.id.tv_save);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password2 = (EditText) findViewById(R.id.et_password2);
        et_UserID = (EditText) findViewById(R.id.et_UserID);
        et_name = (EditText) findViewById(R.id.et_name);
        lin_role = (LinearLayout) findViewById(R.id.lin_role);
        lin_main = (LinearLayout) findViewById(R.id.lin_main);
        rl_head = (RelativeLayout) findViewById(R.id.rl_head);
        cimg_head = (CircleImageView) findViewById(R.id.cimg_head);
        tv_back.setOnClickListener(new MyOnClick());
        tv_save.setOnClickListener(new MyOnClick());
        rl_head.setOnClickListener(new MyOnClick());
        lin_role.setOnClickListener(new MyOnClick());

        CheckEtListen();
        getPopupWindowInstance2();
        ReadyThing();
    }

    private void ReadyThing() {
        // 图片缓存 目录
        cache = new File(Environment.getExternalStorageDirectory(), "Image");
        if (!cache.exists()) {
            cache.mkdirs();
        }
    }

    private void CheckEtListen() {
        et_password.addTextChangedListener(new MyTextWatcher());
        et_password2.addTextChangedListener(new MyTextWatcher());
    }

    protected class MyTextWatcher implements TextWatcher{


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String p1 = et_password.getText().toString();
            String p2 = et_password2.getText().toString();
            if(!TextUtils.isEmpty(p1)
                    && !TextUtils.isEmpty(p2)
                    && p1.equals(p2)){
                tv_warn.setVisibility(View.GONE);
            }else {
                tv_warn.setVisibility(VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);

    }

    private class MyOnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.rl_head:
                    mPopupWindow2.showAtLocation(rl_head, Gravity.CENTER, 0, 0);
                    break;
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.lin_role:
                    ChangeRole();
                    break;
                case R.id.tv_save:
                    UpdateData();
                    break;
                default:
                    break;
            }
        }
    }

    private void UpdateData() {
        name = et_name.getText().toString();
        String p1 = et_password.getText().toString();
        String p2 = et_password2.getText().toString();
        if(tv_warn.getVisibility() == VISIBLE && !TextUtils.isEmpty(p1)){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.password_warn),Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name)){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.Name_nn),Toast.LENGTH_SHORT).show();
            return;
        }
        save();
    }

    private void ChangeRole() {
        PopPerfactRole pdf = new PopPerfactRole();
        pdf.getPopwindow(getApplicationContext(), Role);
        pdf.setPoPDismissListener(new PopPerfactRole.PopUpdateOutDismissListener() {
            @Override
            public void PopDismiss(int role, String job) {
                Role = role;
                tv_role.setText(job);
                lin_main.animate().alpha(1.0f);
                lin_main.animate().setDuration(500);
            }
        });
        pdf.StartPop(lin_role);
        lin_main.animate().alpha(0.5f);
        lin_main.animate().setDuration(500);
    }

    /**
     * 文件上传
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
            params.addBodyParameter("UploadType", "0");
            params.addBodyParameter("UserID4Customer", AppConfig.UserID);
            String url = AppConfig.URL_PUBLIC + "Avatar";

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
                            message.what = AppConfig.SUCCESS;
                            handler.sendEmptyMessage(message.what);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Log.e("error", msg.toString());
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.uploadfailure),
                                    Toast.LENGTH_SHORT).show();
                            Message message = new Message();
                            message.what = AppConfig.SUCCESS;
                            handler.sendEmptyMessage(message.what);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 系统相册返回
        if (requestCode == 0 && resultCode == Activity.RESULT_OK
                && data != null) {
            path = FileUtils.getPath(this, data.getData());
            pathname = path.substring(path.lastIndexOf("/") + 1);

//            Log.e("path", path + ":" + data.getData());
			/*Bitmap bm = BitmapFactory.decodeFile(path);
			cimg_head.setImageBitmap(bm);*/
//        	cimg_head.setImageURI(data.getData());

            startPhotoZoom(data.getData(), 150);// 截图
        }
        // 系统相机返回
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
        	/*path = FileUtils.getPath(this, data.getData());
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
                } catch (Exception e) {
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
                int size = bitmap.getWidth() * bitmap.getHeight() * 4;
                //创建一个字节数组输出流,流的大小为size
                ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
                //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                //将字节数组输出流转化为字节数组byte[]
                byte[] imagedata1 = baos.toByteArray();

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


    private void save() {
        Message msg = new Message();

        final JSONObject jsonobject = format();
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService
                            .submitDataByJsonNoToken(AppConfig.URL_PUBLIC
                                    + "User/Register4Web", jsonobject);
                    String retcode = responsedata.getString("RetCode");
                    JSONObject retdata = responsedata
                            .getJSONObject("RetData");
                    String UserID = retdata.getString("UserID");
                    Log.e("Register1", jsonobject.toString() + "");
                    Log.e("Register2", responsedata.toString() + "");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.SAVESUCCESS;
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PerfectDataActivity");
        MobclickAgent.onResume(this); // 统计时长
    }

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        Password = et_password.getText().toString();
        try {
            jsonObject.put("Mobile", "+" + countrycode + Mobile);
            jsonObject.put("Password", LoginGet.getBase64Password(Password)
                    .trim());
            jsonObject.put("VerificationCode", AccessCode);
            jsonObject.put("Role", Role);
            jsonObject.put("Name", et_name.getText().toString());
            jsonObject.put("LoginName", et_UserID.getText().toString());
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