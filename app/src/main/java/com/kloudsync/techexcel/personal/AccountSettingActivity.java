package com.kloudsync.techexcel.personal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.AccountSettingAdminUserBean;
import com.kloudsync.techexcel.bean.AccountSettingBean;
import com.kloudsync.techexcel.bean.AccountSettingContactBean;
import com.kloudsync.techexcel.bean.AccountSettingImageBean;
import com.kloudsync.techexcel.bean.EventRefreshTab;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.ContainsEmojiEditText;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.AccountSettingTakePhotoPopup;
import com.ub.techexcel.tools.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class AccountSettingActivity extends Activity {


    private RelativeLayout as_rl_logo, as_rl_admin, as_rl_contact;
    private ContainsEmojiEditText as_et_name, as__et_webaddress, as_et_email;
    private ImageView img_notice;
    private TextView as_tv_save;
    private AccountSettingBean asbean = new AccountSettingBean();
    private AccountSettingImageBean imagebean = new AccountSettingImageBean();


    private List<AccountSettingAdminUserBean> aslist = new ArrayList<AccountSettingAdminUserBean>();
    private List<AccountSettingContactBean> ascontactlist = new ArrayList<AccountSettingContactBean>();


    private SimpleDraweeView as_img_logo,as_img_admin_one,as_img_admin_two,as_img_admin_three,as_img_contact_one,as_img_contact_two,as_img_contact_three;

    public static String path = "";
    public static String pathname = "";
    public static File cache, localFile, file;

    private String imageurl = "";
    private String name;
    private AccountSettingTakePhotoPopup accountSettingTakePhotoPopup;
    private SharedPreferences userPreferences;
    private Switch syncSwitch;
    // public PopupWindow mPopupWindow;
    private Intent intent;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x22:
                    //Toast.makeText(AccountSettingActivity.this,"upload success", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x00:
                    name = asbean.getSchoolName();
                    if (name != null) {
                        as_et_name.setText(name);
                    }
                    break;
                case 0x01:
                    if(ascontactlist.size() >= 3) {
                        as_img_contact_one.setImageURI(Uri.parse(ascontactlist.get(0).getAvatarUrl()));
                        as_img_contact_two.setImageURI(Uri.parse(ascontactlist.get(1).getAvatarUrl()));
                        as_img_contact_three.setImageURI(Uri.parse(ascontactlist.get(2).getAvatarUrl()));
                    }else if (ascontactlist.size() >= 2){
                        as_img_contact_one.setImageURI(Uri.parse(ascontactlist.get(0).getAvatarUrl()));
                        as_img_contact_two.setImageURI(Uri.parse(ascontactlist.get(1).getAvatarUrl()));
                        as_img_contact_three.setVisibility(View.GONE);
                    }else if (ascontactlist.size() >= 1){
                        as_img_contact_one.setImageURI(Uri.parse(ascontactlist.get(0).getAvatarUrl()));
                        as_img_contact_two.setVisibility(View.GONE);
                        as_img_contact_three.setVisibility(View.GONE);
                    }else {
                        as_img_contact_one.setVisibility(View.GONE);
                        as_img_contact_two.setVisibility(View.GONE);
                        as_img_contact_three.setVisibility(View.GONE);
                    }
                    break;
                case 0x02:
                    if(aslist.size() >= 3) {
                        as_img_admin_one.setImageURI(Uri.parse(aslist.get(0).getAvatarUrl()));
                        as_img_admin_two.setImageURI(Uri.parse(aslist.get(1).getAvatarUrl()));
                        as_img_admin_three.setImageURI(Uri.parse(aslist.get(2).getAvatarUrl()));
                    }else if (aslist.size() >= 2){
                        as_img_admin_one.setImageURI(Uri.parse(aslist.get(0).getAvatarUrl()));
                        as_img_admin_two.setImageURI(Uri.parse(aslist.get(1).getAvatarUrl()));
                        as_img_admin_three.setVisibility(View.GONE);
                    }else if (aslist.size() >= 1){
                        as_img_admin_one.setImageURI(Uri.parse(aslist.get(0).getAvatarUrl()));
                        as_img_admin_two.setVisibility(View.GONE);
                        as_img_admin_three.setVisibility(View.GONE);
                    }else {
                        as_img_admin_one.setVisibility(View.GONE);
                        as_img_admin_two.setVisibility(View.GONE);
                        as_img_admin_three.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stubim
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting);
        userPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        initView();
        getSchoolImage();
        confirmSex();
        GetAdminInfo();
        GetContactInfo();
    }

    public void initView() {
        img_notice = findViewById(R.id.img_notice);

        as_img_logo = findViewById(R.id.as_img_logo);
        as_img_admin_one = findViewById(R.id.as_img_admin_one);
        as_img_admin_two = findViewById(R.id.as_img_admin_two);
        as_img_admin_three = findViewById(R.id.as_img_admin_three);
        as_img_contact_one = findViewById(R.id.as_img_contact_one);
        as_img_contact_two = findViewById(R.id.as_img_contact_two);
        as_img_contact_three = findViewById(R.id.as_img_contact_three);


        as_rl_logo = findViewById(R.id.as_rl_logo);
        as_et_name = findViewById(R.id.as_et_name);
        as__et_webaddress = findViewById(R.id.as__et_webaddress);
        as_et_email = findViewById(R.id.as_et_email);
        as_rl_admin = findViewById(R.id.as_rl_admin);
        as_rl_contact = findViewById(R.id.as_rl_contact);
        as_tv_save = findViewById(R.id.as_tv_save);

        cache = new File(Environment.getExternalStorageDirectory(), "Image");
        if (!cache.exists()) {
            cache.mkdirs();
        }

        img_notice.setOnClickListener(new MyOnClick());
        as_rl_logo.setOnClickListener(new MyOnClick());
        as_img_logo.setOnClickListener(new MyOnClick());
        as_rl_admin.setOnClickListener(new MyOnClick());
        as_rl_contact.setOnClickListener(new MyOnClick());
        as_tv_save.setOnClickListener(new MyOnClick());
        syncSwitch = findViewById(R.id.switch_sync);
        syncSwitch.setChecked(userPreferences.getBoolean("enable_sync", false));
        syncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPreferences.edit().putBoolean("enable_sync", isChecked).commit();
                EventBus.getDefault().post(new EventRefreshTab());
                Log.e("AccountSettingActivity", "post event");
            }
        });
    }

    /**
     * 获得AdminUser前三个头像
     *
     * @param
     */
    private void GetAdminInfo() {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService
                            .getIncidentData(
                                    AppConfig.URL_PUBLIC
                                            + "SchoolContact/List?schoolID=" + AppConfig.SchoolID+"&roleType=7,8&searchText=&pageIndex=0");
                    //Log.e("老余AdminUser", jsonObject + "");
                    aslist = formatAdminjson(jsonObject);
                    //Log.e("老余AdminUserSize", aslist.size() + "");
                    if (aslist.size() > 0 && aslist != null) {
                        Message msg = new Message();
                        msg.what = 0x02;
                        msg.obj = aslist;
                        handler.sendMessage(msg);
                    }else {
                        as_img_admin_one.setVisibility(View.GONE);
                        as_img_admin_two.setVisibility(View.GONE);
                        as_img_admin_three.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }
    private List<AccountSettingAdminUserBean> formatAdminjson(JSONObject jsonObject) {
        List<AccountSettingAdminUserBean> list = new ArrayList<AccountSettingAdminUserBean>();
        JSONArray jsonarray;
        try {
            jsonarray = jsonObject.getJSONArray("RetData");
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject object = jsonarray.getJSONObject(i);
                AccountSettingAdminUserBean bean = new AccountSettingAdminUserBean();
                bean.setUserName(object.getString("UserName"));
                bean.setPhone(object.getString("Phone"));
                bean.setAvatarUrl(object.getString("AvatarUrl"));
                list.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获得Contact前三个头像
     *
     * @param
     */
    private void GetContactInfo() {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject jsonObject = ConnectService
                            .getIncidentData(
                                    AppConfig.URL_PUBLIC
                                            + "SchoolContact/List?schoolID=" + AppConfig.SchoolID + "&roleType=0,1,2,3,4,5,6,7,8,9&searchText=&pageIndex=0");
                  //  Log.e("老余Contact", jsonObject + "");
                    ascontactlist = formatCantactjson(jsonObject);
                  //  Log.e("老余ContactSize", ascontactlist.size() + "");
                    if (ascontactlist.size() > 0 && ascontactlist != null) {
                        Message msg = new Message();
                        msg.what = 0x01;
                        msg.obj = ascontactlist;
                        handler.sendMessage(msg);
                    }else {
                        as_img_contact_one.setVisibility(View.GONE);
                        as_img_contact_two.setVisibility(View.GONE);
                        as_img_contact_three.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private List<AccountSettingContactBean> formatCantactjson(JSONObject jsonObject) {
        List<AccountSettingContactBean> list = new ArrayList<AccountSettingContactBean>();
        JSONArray jsonarray;
        try {
            jsonarray = jsonObject.getJSONArray("RetData");
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject object = jsonarray.getJSONObject(i);
                AccountSettingContactBean bean = new AccountSettingContactBean();
                bean.setUserName(object.getString("UserName"));
                bean.setPhone(object.getString("Phone"));
                bean.setAvatarUrl(object.getString("AvatarUrl"));
                list.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取头像
     */
    private void getSchoolImage() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService
                        .getIncidentData(AppConfig.URL_PUBLIC
                                + "School/GetCompanyAvatar?companyID=" + AppConfig.SchoolID);
                try {
                    if (jsonObject.getInt("RetCode") != 200
                            && jsonObject.getInt("RetCode") != 0) {
                        return;
                    }
                    imagebean.setSchoolImage(jsonObject.getString("RetData"));
                    Log.e("嘿嘿嘿", imagebean.getSchoolImage() + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imagebean != null) {
                            imageurl = imagebean.getSchoolImage();
                            Uri imageUri = Uri.parse(imageurl);
                            as_img_logo.setImageURI(imageUri);
                        }
                    }
                });

            }
        }).start(((App) getApplication()).getThreadMgr());
    }

/*    private AccountSettingImageBean formatimagejson(JSONObject jsonObject) {
        AccountSettingImageBean ibean = new AccountSettingImageBean();
        try {

            JSONObject object=   jsonObject.getJSONObject("RetData");
            ibean.setSchoolImage(object.getString("RetData"));
            Log.e("嘿嘿嘿",ibean.getSchoolImage()+ "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ibean;
    }*/

    /**
     * 获取组织个人信息
     */
    private void confirmSex() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService
                        .getIncidentData(AppConfig.URL_PUBLIC
                                + "School/SchoolInfo?schoolID=" + AppConfig.SchoolID);
                try {
                    if (jsonObject.getInt("RetCode") != 200
                            && jsonObject.getInt("RetCode") != 0) {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                asbean = formatjson(jsonObject);
                if (asbean != null) {
                    Message msg = new Message();
                    msg.what = 0x00;
                    msg.obj = asbean;
                    handler.sendMessage(msg);
                }
            }
        }).start(((App) getApplication()).getThreadMgr());
    }

    private AccountSettingBean formatjson(JSONObject jsonObject) {
        AccountSettingBean bean = new AccountSettingBean();
        try {

            JSONObject object = jsonObject.getJSONObject("RetData");
            bean.setSchoolName(object.getString("SchoolName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }


    /**
     * 头像上传
     */
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
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String url = AppConfig.URL_PUBLIC + "School/UploadCompanyAvatar?companyID="+AppConfig.SchoolID;

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

    /**
     * 跳至拍照界面
     */
    public void GotoPhoto() {
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

    /**
     * 跳至相册选择界面
     */
    public void GetGallery() {
        // 跳转至相册界面
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);

    }

    /**
     * 当从照片界面或者相册界面返回时
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 系统相册返回
        if (requestCode == 0 && resultCode == Activity.RESULT_OK
                && data != null) {

            path = FileUtils.getPath(this, data.getData());
            pathname = path.substring(path.lastIndexOf("/") + 1);
            Bitmap bm = BitmapFactory.decodeFile(path);
            SetAvCircle();
            as_img_logo.setImageURI(data.getData());
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
            pathname = path.substring(path.lastIndexOf("/") + 1);
            as_img_logo.setImageURI(Uri.fromFile(localFile));

        }
        // 截图后返回
/*        if (requestCode == 2 && data != null) {
            Bundle bundle = data.getExtras();
            Log.e("duang", (bundle != null) + "   ");
            if (bundle != null) {

                Bitmap bitmap = bundle.getParcelable("data");
                Log.e("duang", bitmap + "   ");
                // 创建助手类的实例
                int size = bitmap.getWidth() * bitmap.getHeight() * 4;
                //创建一个字节数组输出流,流的大小为size
                ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
                //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                //将字节数组输出流转化为字节数组byte[]
                byte[] imagedata1 = baos.toByteArray();

//				SetAvCircle();
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                as_img_logo.setImageURI(uri);
//				tv_head.setImageBitmap(bitmap);
                //关闭字节数组输出流
                try {
                    baos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                file = new File(path);
*//*                saveBitmap2file(
                        bitmap,
                        file.getName().toString());*//*
            }
        }*/
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

    private void SetAvCircle() {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        RoundingParams parames = RoundingParams.asCircle();
        GenericDraweeHierarchy hierarchy = builder.setRoundingParams(parames).build();
        as_img_logo.setHierarchy(hierarchy);
    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_notice:
                    finish();
                    break;
                case R.id.as_img_logo:
                case R.id.as_rl_logo:
                    // mPopupWindow.showAtLocation(as_img_logo, Gravity.CENTER, 0, 0);
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
                        public void dismiss() {

                            Log.e("哈哈哈哈哈", "sdoafjiasdfasdfasdf");
                            getWindow().getDecorView().setAlpha(1.0f);
                        }

                        @Override
                        public void open() {
                            getWindow().getDecorView().setAlpha(0.5f);
                        }
                    });
                    accountSettingTakePhotoPopup.StartPop(as_img_logo);
                    break;

                case R.id.as_rl_admin:
                    intent = new Intent(getApplicationContext(), AccountSettingAdminUserActivity.class);
                    startActivity(intent);
                    break;
                case R.id.as_rl_contact:
                    intent = new Intent(getApplicationContext(), AccountSettingContactActivity.class);
                    startActivity(intent);
                    break;
                case R.id.as_tv_save:
                    if(path != null && path.length() > 0){
                        uploadhead();
                    } else {
                        //save();
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
