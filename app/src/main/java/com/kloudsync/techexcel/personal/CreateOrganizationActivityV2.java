package com.kloudsync.techexcel.personal;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.SelectCompanyLogoDialog;
import com.kloudsync.techexcel.dialog.UploadFileDialog;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.tool.UploadFileTool;
import com.kloudsync.techexcel.ui.MainActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CreateOrganizationActivityV2 extends AppCompatActivity implements View.OnClickListener, SelectCompanyLogoDialog.LogoOptionsListener {

    private RelativeLayout backLayout,rl_organization_upload;
    private EditText et_name;
    private TextView tv_submit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView titleText;
    private ImageView selectLogoImage,iv_organization_upload;
    private TextView enterText;
    private boolean fromAppSetting;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.CreateOrganization:
                    String result = (String) msg.obj;
                    COjson(result);
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_submit.setEnabled(true);
                        }
                    }, 1000);
                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                    tv_submit.setEnabled(true);
                    break;
                case AppConfig.AddOrUpdateUserPreference:
                    editor = sharedPreferences.edit();
                    editor.putInt("SchoolID", AppConfig.SchoolID);
                    editor.putString("SchoolName", et_name.getText().toString());
                    editor.putString("teamname", defaultTeam.getName());
                    editor.putInt("teamid", defaultTeam.getItemID());
                    editor.commit();
                    EventBus.getDefault().post(new TeamSpaceBean());
                    if (pictureUri != null && logoFile != null) {
                        UploadFileTool.uploadCompanyLogo(AppConfig.SchoolID + "", logoFile, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                if (uploadFileDialog != null) {
                                    uploadFileDialog.cancel();
                                }
                                tv_submit.setEnabled(true);
                                getToInvite();
                                finish();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                if (uploadFileDialog != null) {
                                    uploadFileDialog.cancel();
                                }
                                tv_submit.setEnabled(true);
                                getToInvite();
                                finish();
                            }

                            @Override
                            public void onStart() {
                                super.onStart();
                                uploadFileDialog = new UploadFileDialog(CreateOrganizationActivityV2.this);
                                uploadFileDialog.setTile("uploding logo");
                                uploadFileDialog.show();
                            }

                            @Override
                            public void onLoading(long total, long current, boolean isUploading) {
                                super.onLoading(total, current, isUploading);
//                                Log.e("onloading","current:" + current + ",total:" + total);
                                if (current == 0 || total == -1 || current / total <= 0) {

                                } else {
                                    uploadFileDialog.setProgress(total, current);
                                }

                            }
                        });
                    } else {
                        getToInvite();
                        finish();
                    }
                default:
                    break;
            }
        }

    };

    private void getToInvite() {
        Intent intent = new Intent(this, CreateOrganizationInviteActivity.class);
        intent.putExtra("space_id", firstSpaceId);
        startActivity(intent);

    }


    UploadFileDialog uploadFileDialog;
    int firstSpaceId;
    private void COjson(String result) {
        JSONObject obj = null;
        Toast.makeText(this, R.string.create_success, Toast.LENGTH_SHORT).show();
        try {
            obj = new JSONObject(result);
            int shoolId = obj.getJSONObject("RetData").getInt("SchoolID");
            int teamId = obj.getJSONObject("RetData").getInt("FirstTeamID");
            firstSpaceId = obj.getJSONObject("RetData").getInt("FirstSpaceID");
            sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                    MODE_PRIVATE);
            editor = sharedPreferences.edit();
            AppConfig.SchoolID = shoolId;
            editor.putInt("SchoolID", shoolId);
            editor.putInt("teamid", teamId);
            editor.putString("SchoolName", et_name.getText().toString());
            editor.commit();
//
            requestDefaultTeam();
//            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    TeamSpaceBean defaultTeam;

    private void requestDefaultTeam() {

        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=1&parentID=0",
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        if (list.size() > 0) {
                            defaultTeam = list.get(0);
                        }
                        if (defaultTeam != null) {
                            editor.putString("teamname", defaultTeam.getName());
                            requestUpdateUser();
                        } else {
                            Toast.makeText(getApplicationContext(), "创建异常", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void requestUpdateUser() {
        final JSONObject jsonObject = getUpdateUserParms();
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "User/AddOrUpdateUserPreference", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddOrUpdateUserPreference;
                        msg.obj = responsedata.toString();
                    } else {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_organization);
        fromAppSetting = getIntent().getBooleanExtra("from_app_setting",false);
        initView();

    }

    private void initView() {
        rl_organization_upload= (RelativeLayout) findViewById(R.id.rl_organization_upload);
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        et_name = (EditText) findViewById(R.id.et_name);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        titleText = (TextView) findViewById(R.id.tv_title);
        enterText = findViewById(R.id.txt_enter);
        enterText.setOnClickListener(this);
        titleText.setText(R.string.create_organization);
        backLayout.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        rl_organization_upload.setOnClickListener(this);

        iv_organization_upload = (ImageView) findViewById(R.id.iv_organization_upload);
        selectLogoImage = (ImageView) findViewById(R.id.image_select_logo);
        selectLogoImage.setOnClickListener(this);
//        if(fromAppSetting){
//            tv_submit.setText(R.string.create_and_switch);
//            enterText.setVisibility(View.GONE);
//        }else {
//            tv_submit.setText(R.string.Submit);
//            enterText.setVisibility(View.VISIBLE);
//        }
    }

    SelectCompanyLogoDialog logoDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.tv_submit:
                requestCreateNewCompanyAndEnterOrInvite(true);
                break;
            //case R.id.image_select_logo:
            case R.id.rl_organization_upload:
                logoDialog = new SelectCompanyLogoDialog(this);
                logoDialog.setLogoOptionsListener(this);
                logoDialog.show();
                break;
            case R.id.txt_enter:
                requestCreateNewCompanyAndEnterOrInvite(false);
                break;
            default:
                break;
        }
    }


    Runnable enableTextTask = new Runnable() {
        @Override
        public void run() {
            tv_submit.setEnabled(true);
        }
    };


    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SchoolName", et_name.getText().toString().trim());
            jsonObject.put("Category1", 2);
            jsonObject.put("Category2", 0);
            jsonObject.put("OwnerID", AppConfig.UserID);
            jsonObject.put("AdminID", AppConfig.UserID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }


    private JSONObject getUpdateUserParms() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FieldID", 10001);
//            jsonObject.put("PreferenceValue", 0);
            jsonObject.put("PreferenceText", getParmsObj() + "");
//            jsonObject.put("PreferenceMemo", "");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    private JSONObject getParmsObj() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SchoolID", AppConfig.SchoolID);
            jsonObject.put("TeamID", defaultTeam.getItemID());
            jsonObject.put("SchoolName", et_name.getText().toString());
            jsonObject.put("TeamName", TextUtils.isEmpty(defaultTeam.getName()) ? "" : defaultTeam.getName());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void fromPhoto() {
        takePicture();
    }

    private static final int REQUEST_SELECTED_IMAGE = 2;

    @Override
    public void fromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECTED_IMAGE);
    }


    File logoFile;

    public void takePicture() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            Toast.makeText(this, "请插入SD卡", Toast.LENGTH_SHORT).show();
            return;
        }
        File cache = new File(Environment.getExternalStorageDirectory(), "Image");
        if (!cache.exists()) {
            cache.mkdirs();
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 文件名
        String fileName = DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";
        logoFile = new File(cache, fileName);
        Log.e("take_picture", "path:" + logoFile.getAbsolutePath());
        //Android7.0文件保存方式改变了
        if (Build.VERSION.SDK_INT < 24) {
            Uri uri = Uri.fromFile(logoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, logoFile.getAbsolutePath());
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, REQUEST_TAKE_PICTURE);
    }

    private static final int REQUEST_TAKE_PICTURE = 1;

    Uri pictureUri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PICTURE) {

                if (data != null) {
                    pictureUri = data.getData();
                }

                if (null == pictureUri) {
                    if (logoFile != null) {
                        pictureUri = Uri.fromFile(logoFile);
                    }
                }

                if (pictureUri != null) {
                    //selectLogoImage.setImageURI(pictureUri);
                    iv_organization_upload.setImageURI(pictureUri);
                }
            } else if (requestCode == REQUEST_SELECTED_IMAGE) {
                String path = FileUtils.getPath(this, data.getData());
                logoFile = new File(path);
                if (logoFile.exists()) {
                    pictureUri = Uri.fromFile(logoFile);
                    //selectLogoImage.setImageURI(pictureUri);
                    iv_organization_upload.setImageURI(pictureUri);
                }
            }
        }
    }

    private void requestCreateNewCompanyAndEnterOrInvite(final boolean isInvite) {
        String name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), R.string.tips_orginzaiton_name_not_empty, Toast.LENGTH_SHORT).show();
            tv_submit.setEnabled(true);
            return;
        }
        tv_submit.setEnabled(false);
        handler.removeCallbacks(enableTextTask);
        handler.postDelayed(enableTextTask, 3000);
        final JSONObject jsonObject = format();
        Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String s) throws Exception {
                JSONObject response = ConnectService.submitDataByJson(
                        AppConfig.URL_PUBLIC
                                + "School/CreateSchool", jsonObject);
                return response;
            }
        }).map(new Function<JSONObject, JSONObject>() {
            @Override
            public JSONObject apply(JSONObject jsonObject) throws Exception {
                JSONObject result = new JSONObject();
                if(jsonObject.has("RetCode")){
                    if(jsonObject.getString("RetCode").equals("0")){
                        // 创建公司成功
                        int shoolId = jsonObject.getJSONObject("RetData").getInt("SchoolID");
                        int teamId = jsonObject.getJSONObject("RetData").getInt("FirstTeamID");
                        firstSpaceId = jsonObject.getJSONObject("RetData").getInt("FirstSpaceID");
                        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                                MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        AppConfig.SchoolID = shoolId;
                        editor.putInt("SchoolID", shoolId);
                        editor.putInt("teamid", teamId);
                        editor.putString("SchoolName", et_name.getText().toString());
                        editor.commit();
                        result = ServiceInterfaceTools.getinstance().syncRequestDefaultTeamForOrganiztion(AppConfig.SchoolID);
                    }
                }
                return result;
            }
        }).map(new Function<JSONObject, JSONObject>() {
            @Override
            public JSONObject apply(JSONObject jsonObject) throws Exception {
                JSONObject result = new JSONObject();
                if(jsonObject.has("RetCode")){
                    if(jsonObject.getInt("RetCode") == 0){
                        JSONArray jsonArray = jsonObject.getJSONArray("RetData");
                        if(jsonArray != null && jsonArray.length() > 0){
                            JSONObject item = jsonArray.getJSONObject(0);
                            TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
                            teamSpaceBean.setItemID(item.getInt("ItemID"));
                            teamSpaceBean.setName(item.getString("Name"));
                            teamSpaceBean.setCompanyID(item.getInt("CompanyID"));
                            teamSpaceBean.setType(item.getInt("Type"));
                            teamSpaceBean.setParentID(item.getInt("ParentID"));
                            teamSpaceBean.setCreatedDate(item.getString("CreatedDate"));
                            teamSpaceBean.setCreatedByName(item.getString("CreatedByName"));
                            teamSpaceBean.setAttachmentCount(item.getInt("AttachmentCount"));
                            teamSpaceBean.setMemberCount(item.getInt("MemberCount"));
                            teamSpaceBean.setSyncRoomCount(item.getInt("SyncRoomCount"));
//                            result.put("default_team",jsonArray.getJSONObject(0));
                            defaultTeam = teamSpaceBean;
                            result = ServiceInterfaceTools.getinstance().syncAddOrUpdateUserPreference(getUpdateUserParms());
                        }
                    }
                }
                return result;
            }
        }).map(new Function<JSONObject, JSONObject>() {
            @Override
            public JSONObject apply(JSONObject jsonObject) throws Exception {
                JSONObject result = new JSONObject();
                if(jsonObject.has("RetCode")){
                    if(jsonObject.getString("RetCode").equals(AppConfig.RIGHT_RETCODE)){
                        result.put("create_succ",true);
                        editor = sharedPreferences.edit();
                        editor.putInt("SchoolID", AppConfig.SchoolID);
                        editor.putString("SchoolName", et_name.getText().toString());
                        editor.putString("teamname", defaultTeam.getName());
                        editor.putInt("teamid", defaultTeam.getItemID());
                        editor.commit();
                        EventBus.getDefault().post(new TeamSpaceBean());
                        if (pictureUri != null && logoFile != null) {
                            UploadFileTool.uploadCompanyLogo(AppConfig.SchoolID + "", logoFile, new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    if (uploadFileDialog != null) {
                                        uploadFileDialog.cancel();
                                    }
                                    tv_submit.setEnabled(true);
                                    if(isInvite){
                                        getToInvite();
                                    }else {
                                        goToMainActivity();
                                    }
                                    finish();
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    if (uploadFileDialog != null) {
                                        uploadFileDialog.cancel();
                                    }
                                    tv_submit.setEnabled(true);
                                    if(isInvite){
                                        getToInvite();
                                    }else {
                                        goToMainActivity();
                                    }
                                    finish();

                                }

                                @Override
                                public void onStart() {
                                    super.onStart();
                                    uploadFileDialog = new UploadFileDialog(CreateOrganizationActivityV2.this);
                                    uploadFileDialog.setTile("uploding logo");
                                    uploadFileDialog.show();
                                }

                                @Override
                                public void onLoading(long total, long current, boolean isUploading) {
                                    super.onLoading(total, current, isUploading);
//                                Log.e("onloading","current:" + current + ",total:" + total);
                                    if (current == 0 || total == -1 || current / total <= 0) {

                                    } else {
                                        uploadFileDialog.setProgress(total, current);
                                    }

                                }
                            });
                        } else {
                            if(isInvite){
                                getToInvite();
                            }else {
                                goToMainActivity();
                            }
                        }
                    }
                }
                return result;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if(!jsonObject.has("create_succ")){
                    Toast.makeText(getApplicationContext(),R.string.create_Fail,Toast.LENGTH_SHORT).show();
                }
            }
        }).subscribe();

    }

    private void goToMainActivity() {
        Observable.just("go_to_main").observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Toast.makeText(getApplicationContext(),R.string.create_success,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateOrganizationActivityV2.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }).subscribe();

    }



}
