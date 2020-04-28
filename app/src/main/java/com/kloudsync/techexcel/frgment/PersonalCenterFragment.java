package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.bean.params.EventChangeAccout;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.ModifyMeetingIdDialog;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.EverPenManger;
import com.kloudsync.techexcel.help.PopShareKloudSync;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.CustomerYu;
import com.kloudsync.techexcel.pc.ui.ChangePasswordActivity;
import com.kloudsync.techexcel.pc.ui.EffectiveActivity;
import com.kloudsync.techexcel.pc.ui.PersonalInfoActivity;
import com.kloudsync.techexcel.pc.ui.ProfessionalFieldActivity;
import com.kloudsync.techexcel.pc.ui.SelfDescriptionActivity;
import com.kloudsync.techexcel.personal.AboutActivity2;
import com.kloudsync.techexcel.personal.AccountSettingActivity;
import com.kloudsync.techexcel.personal.CreateOrganizationActivityV2;
import com.kloudsync.techexcel.personal.DrawerLayoutActivity;
import com.kloudsync.techexcel.personal.HelpCenterActivity;
import com.kloudsync.techexcel.personal.LanguageActivity;
import com.kloudsync.techexcel.personal.MyCourseTemplateActivity;
import com.kloudsync.techexcel.personal.MyNoteActivity;
import com.kloudsync.techexcel.personal.PersonalCollectionActivity;
import com.kloudsync.techexcel.personal.SchoolProfileActivity;
import com.kloudsync.techexcel.personal.SyncTvActivity;
import com.kloudsync.techexcel.personal.TeacherProfileActivity;
import com.kloudsync.techexcel.school.SwitchOrganizationActivity;
import com.kloudsync.techexcel.start.LoginActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.kloudsync.techexcel.tool.StringUtils;
import com.kloudsync.techexcel.ui.CurrentPenStatusActivity;
import com.kloudsync.techexcel.ui.DigitalPensActivity;
import com.kloudsync.techexcel.ui.GuideActivity;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.kloudsync.activity.Document;
import com.ub.service.activity.FinishedCourseActivity;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;


public class PersonalCenterFragment extends Fragment implements OnClickListener, ModifyMeetingIdDialog.OnModifyClickListner {

    private View view;
    private RelativeLayout rl_pc_portrait,
            rl_pc_professionalField, rl_pc_effective, rl_pc_password,
            rl_pc_loginout, rl_pc_language, rl_pc_synctv,ll_digital_note;

    private RelativeLayout rl_pc_klassroomID;
    private RelativeLayout rl_cn_account_setting;
    private RelativeLayout rl_pc_about;
    private RelativeLayout guideLayout;
    private RelativeLayout rl_pc_hc;
    private RelativeLayout rl_pc_sk;
    private RelativeLayout rl_contacts_portrait;
    private RelativeLayout rl_cn_account;
    private RelativeLayout ll_pc_integral, ll_pc_collection,ll_pc_note,
            ll_pc_publish_article;
    private RelativeLayout rl_school_name, rl_teacher_profile;
    private RelativeLayout lin_switch;
    private TextView tv_pc_account_name, tv_pc_account_level,
            pi_goodatfield, pc_tv_date, pc_tv_language;
    private TextView tv_roomid;
    private TextView tv_switch;
    private TextView tv_sname;
    private TextView tv_teacher_profile;
    private TextView tv_pc_hc;
    private TextView tv_pc_about;
    private SharedPreferences sharedPreferences;
    private String account_name;
    private String account_number;
    private String AvatarUrl = "";
    private String memberPoints = "0";
    private String expirationDate = "";
    private String SkilledFields;
    private String ArticleCount;
    public ImageLoader imageLoader;
    private SimpleDraweeView contacts_portrait;
    private AlertDialog dialog = null;
    private String updateClassroomId;

    private CustomerYu customerYu = new CustomerYu();

    private String FullName;
    private String  PrimaryPhone;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1006:
                    int retcode = (int) msg.obj;
                    if (retcode == -1) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.classroomid_occupied), Toast.LENGTH_LONG).show();
                    } else {
                        getRoomID();
                    }
                    break;
                case 0x1007:
                    String retdate = (String) msg.obj;
                    if (retdate.equals(AppConfig.ClassRoomID)) {
                    } else {
                        AppConfig.ClassRoomID = retdate;
                        tv_roomid.setText(AppConfig.ClassRoomID.replaceAll("-", "").toUpperCase());
                    }
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;case 0x00:
                    downloadAttachment();
                    tv_pc_account_name.setText(account_name);
                    tv_pc_account_level.setText(PrimaryPhone);
                    break;
            }
        }
    };
    private TextView mPenStatus;
    private TextView mPenSource;
    private TextView mCurrentPenStatus;
	private EverPen mCurrentPen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        if(view == null){
            view = inflater.inflate(R.layout.personal_center, container, false);
            initView();
            EventBus.getDefault().register(this);
        }

        ShowLanguage();
        GetSchoolInfo();
        getPersonInfo2();
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverEventChangeAccout(EventChangeAccout eventChangeAccout){
        Log.e("receiverEventChangeAccout","receiverEventChangeAccout");
        getPersonInfo2();
    }


    //获取组织信息
    private void GetSchoolInfo() {
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                getActivity().MODE_PRIVATE);
        int SchoolId = sharedPreferences.getInt("SchoolID", -1);
        String schoolName = sharedPreferences.getString("SchoolName", null);
        /*if (-1 == SchoolId || SchoolId == AppConfig.SchoolID) {
            tv_sname.setText(getResources().getString(R.string.My_School));
        } else {
            tv_sname.setText(schoolName);
        }*/
        tv_sname.setText(schoolName);
    }

    boolean isFirst = true;

    private boolean isKVisibleToUser;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isFirst && isVisibleToUser) {
            isKVisibleToUser = isVisibleToUser;
            isFirst = false;

        }
        if (isKVisibleToUser) {
            if (AppConfig.HASUPDATAINFO == true) {
                account_name = sharedPreferences.getString("Name", "");

                // account_number = sharedPreferences.getString("telephone",
                // "");

                tv_pc_account_name.setText(account_name);
                // tv_pc_account_number.setText(account_number);
                getPersonInfo2();
                AppConfig.HASUPDATAINFO = false;
            }
        }
    }

    @Override
    public void onResume() {
        Log.e("qqqqqqq", AppConfig.isUpdateDialogue + "");
        if (isKVisibleToUser) {
            if (AppConfig.HASUPDATAINFO == true) {
                account_name = sharedPreferences.getString("Name", "");

                // account_number = sharedPreferences.getString("telephone",
                // "");
                tv_pc_account_name.setText(account_name);
                // tv_pc_account_number.setText(account_number);
                getPersonInfo2();
                AppConfig.HASUPDATAINFO = false;
            }
            if (AppConfig.HASUPDATESUMMERY) {
                getPersonInfo2();
                AppConfig.HASUPDATESUMMERY = false;
            }
        }
        if (tv_sname != null) {
            GetSchoolInfo();
        }

        EverPen autoPen = EverPenManger.getInstance(getActivity()).getAutoPen();
        if (autoPen != null) {
            mPenSource.setText(autoPen.getPenType() + autoPen.getSimilaPenSource() + autoPen.getPenName());
            if (mCurrentPen != null && mCurrentPen.isConnected()) {
                mPenStatus.setText(R.string.the_connected);
            } else {
                mPenStatus.setText(R.string.not_connected);
			    /*if (mCurrentPen.isClick()) {
                    mCurrentPenStatus.setText(R.string.connecting);
                }else {
                    mCurrentPenStatus.setText(R.string.is_scanning);
                }*/
                mCurrentPenStatus.setText(R.string.is_scanning);
            }
        } else {
            mPenStatus.setVisibility(View.GONE);
            mPenSource.setVisibility(View.GONE);
            mCurrentPenStatus.setVisibility(View.GONE);
        }
        super.onResume();
    }

    private void initView() {
        rl_pc_portrait = (RelativeLayout) view
                .findViewById(R.id.rl_pc_portrait);
        ll_pc_publish_article = (RelativeLayout) view
                .findViewById(R.id.ll_pc_publish_article);
        ll_pc_collection = (RelativeLayout) view
                .findViewById(R.id.ll_pc_collection);
        ll_pc_note = (RelativeLayout) view
                .findViewById(R.id.ll_pc_note);
        // rl_pc_description = (RelativeLayout) view
        // .findViewById(R.id.rl_pc_description);
        ll_pc_integral = (RelativeLayout) view.findViewById(R.id.ll_pc_integral);
        rl_school_name = (RelativeLayout) view.findViewById(R.id.rl_school_name);
        rl_teacher_profile = (RelativeLayout) view.findViewById(R.id.rl_teacher_profile);
        rl_cn_account = (RelativeLayout) view.findViewById(R.id.rl_cn_account);
        ll_pc_integral = (RelativeLayout) view.findViewById(R.id.ll_pc_integral);
        pi_goodatfield = (TextView) view.findViewById(R.id.pi_goodatfield);
        rl_pc_professionalField = (RelativeLayout) view
                .findViewById(R.id.rl_pc_professionalField);
        guideLayout = view.findViewById(R.id.rl_guide);
        guideLayout.setOnClickListener(this);
        rl_pc_effective = (RelativeLayout) view
                .findViewById(R.id.rl_pc_effective);
        rl_pc_password = (RelativeLayout) view
                .findViewById(R.id.rl_pc_password);
        rl_pc_synctv = (RelativeLayout) view.findViewById(R.id.rl_pc_synctv);
        rl_pc_loginout = (RelativeLayout) view
                .findViewById(R.id.rl_pc_loginout);
        ll_digital_note = (RelativeLayout)view.findViewById(R.id.ll_digital_note);
        mPenStatus = view.findViewById(R.id.tv_personal_pen_status);
        mPenSource = view.findViewById(R.id.tv_personal_pen_source);
        mCurrentPenStatus = view.findViewById(R.id.tv_personal_pen_current_status);

        rl_pc_language = (RelativeLayout) view
                .findViewById(R.id.rl_pc_language);
        rl_pc_klassroomID = (RelativeLayout) view
                .findViewById(R.id.rl_pc_klassroomID);
        rl_pc_about = (RelativeLayout) view
                .findViewById(R.id.rl_pc_about);
        rl_cn_account_setting = (RelativeLayout) view
                .findViewById(R.id.rl_cn_account_setting);
        rl_pc_sk = (RelativeLayout) view
                .findViewById(R.id.rl_pc_sk);
        rl_pc_hc = (RelativeLayout) view
                .findViewById(R.id.rl_pc_hc);
        rl_contacts_portrait = (RelativeLayout) view
                .findViewById(R.id.rl_contacts_portrait);
        lin_switch = view.findViewById(R.id.lin_switch);
        pc_tv_date = (TextView) view.findViewById(R.id.pc_tv_date);
        pc_tv_language = (TextView) view.findViewById(R.id.pc_tv_language);

        contacts_portrait = (SimpleDraweeView) view
                .findViewById(R.id.contacts_portrait);

        tv_pc_account_name = (TextView) view
                .findViewById(R.id.tv_pc_account_name);
        tv_pc_account_level = (TextView) view
                .findViewById(R.id.tv_pc_account_level);
        tv_roomid = (TextView) view
                .findViewById(R.id.tv_roomid);
        tv_sname = (TextView) view.findViewById(R.id.tv_sname);
//        tv_switch = (TextView) view.findViewById(R.id.tv_switch);
        tv_teacher_profile = (TextView) view.findViewById(R.id.tv_teacher_profile);
        tv_pc_hc = (TextView) view.findViewById(R.id.tv_pc_hc);
        tv_pc_about = view.findViewById(R.id.tv_pc_about);
        if (!TextUtils.isEmpty(AppConfig.ClassRoomID)) {
            tv_roomid.setText(AppConfig.ClassRoomID.replaceAll("-", "").toUpperCase());
        }else {
            getRoomID();
        }
        sharedPreferences = getActivity().getSharedPreferences(
                AppConfig.LOGININFO, Context.MODE_PRIVATE);

        account_name = sharedPreferences.getString("Name", "");
        // account_number = sharedPreferences.getString("telephone", "");

        tv_pc_account_name.setText(account_name);
        // tv_pc_account_number.setText(account_number);
        mCurrentPen = EverPenManger.getInstance(getActivity()).getCurrentPen();

        ll_pc_publish_article.setOnClickListener(this);
        ll_pc_collection.setOnClickListener(this);
        ll_pc_note.setOnClickListener(this);
        ll_pc_integral.setOnClickListener(this);
        rl_pc_portrait.setOnClickListener(this);
        rl_pc_professionalField.setOnClickListener(this);
        rl_pc_effective.setOnClickListener(this);
        rl_pc_password.setOnClickListener(this);
        rl_pc_synctv.setOnClickListener(this);
        rl_cn_account_setting.setOnClickListener(this);
        rl_pc_loginout.setOnClickListener(this);
        rl_pc_language.setOnClickListener(this);
        rl_pc_klassroomID.setOnClickListener(this);
        rl_school_name.setOnClickListener(this);
        rl_teacher_profile.setOnClickListener(this);
        rl_pc_about.setOnClickListener(this);
        rl_pc_hc.setOnClickListener(this);
        rl_pc_sk.setOnClickListener(this);
        rl_contacts_portrait.setOnClickListener(this);
        rl_cn_account.setOnClickListener(this);
        contacts_portrait.setOnClickListener(this);
        lin_switch.setOnClickListener(this);
        ll_digital_note.setOnClickListener(this);
    }

    private void ShowLanguage() {
        if (AppConfig.LANGUAGEID == 1) {
            pc_tv_language.setText(getResources().getString(R.string.English));
        } else if (AppConfig.LANGUAGEID == 2) {
            pc_tv_language.setText(getResources().getString(R.string.Chinese));
        }
    }

    private void getToSwitchCompany() {
//        Intent intent = new Intent(getActivity(), SelectSchoolActivity.class);
        Intent intent = new Intent(getActivity(), SwitchOrganizationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void ShareKloudsync() {
        Document document = new Document();
        document.setMe(true);
        document.setSourceFileUrl("aaa");
        document.setTitle("KloudSync - 让你的每个文档“动”起来");
        PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(getActivity(), document, -1);
        psk.setQrcodeVisiable();
        psk.setPoPDismissListener(new PopShareKloudSync.PopShareKloudSyncDismissListener() {
            @Override
            public void CopyLink() {

            }

            @Override
            public void Wechat() {

            }

            @Override
            public void Moment() {

            }

            @Override
            public void Scan() {

            }

            @Override
            public void PopBack() {
            }
        });
        psk.startPop();
    }

    private void GoToHC() {

        /*intent = new Intent(getActivity(), HelpCenterActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                new Pair(tv_pc_hc, HelpCenterActivity.汗));
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());*/
        intent = new Intent(getActivity(), HelpCenterActivity.class);
        startActivity(intent);
    }

    private void GoToAbout() {
        intent = new Intent(getActivity(), AboutActivity2.class);
//        intent = new Intent(getActivity(), DrawerLayoutActivity.class);
        startActivity(intent);
        /*intent = new Intent(getActivity(), AboutActivity2.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                new Pair(tv_pc_about, AboutActivity2.棒));
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());*/
    }


    ModifyMeetingIdDialog modifyMeetingIdDialog;
    private void modifyKlassRoomID() {
        if (modifyMeetingIdDialog == null) {
            modifyMeetingIdDialog = new ModifyMeetingIdDialog(getActivity());
            modifyMeetingIdDialog.setModifyClickListner(this);
        }
        modifyMeetingIdDialog.setCurrentMeetingId(tv_roomid.getText().toString().trim());
        modifyMeetingIdDialog.show();
    }

    @NonNull
    private LinearLayout getModifyEt(final EditText et) {
        LinearLayout lin = new LinearLayout(getActivity());
        lin.setOrientation(LinearLayout.VERTICAL);
        lin.setBackgroundColor(getResources().getColor(R.color.white));
        lin.addView(et);

        LinearLayout lin2 = new LinearLayout(getActivity());
        lin2.setOrientation(LinearLayout.HORIZONTAL);

        int pad = DensityUtil.sp2px(getActivity(), 12);
        LinearLayout.LayoutParams lpe = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpe.setMargins(pad, pad, pad, pad);

        TextView tv1 = new TextView(getActivity());
        tv1.setText(getResources().getString(R.string.Cancel));
        tv1.setTextColor(getResources().getColor(R.color.black));
        tv1.setPadding(pad, pad, pad, pad);
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(12);

        TextView tv2 = new TextView(getActivity());
        tv2.setText(getResources().getString(R.string.OK));
        tv2.setTextColor(getResources().getColor(R.color.black));
        tv2.setPadding(pad, pad, pad, pad);
        tv2.setGravity(Gravity.CENTER);
        tv2.setTextSize(12);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        tv1.setLayoutParams(lp);
        tv2.setLayoutParams(lp);

        tv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                updateClassroomId = et.getText().toString();

                if (updateClassroomId.equals(AppConfig.ClassRoomID)) {
                    dialog.dismiss();
                } else {
                    UpdateClassRoomID(updateClassroomId);
                }
            }
        });
        lin2.addView(tv1);
        lin2.addView(tv2);
        lin.addView(lin2);
        return lin;
    }

    private void UpdateClassRoomID(final String classRoomId) {

        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{3,12}$";

        if(TextUtils.isEmpty(classRoomId)){
            Toast.makeText(getActivity(),"会议id不能是空",Toast.LENGTH_SHORT).show();
            return;
        }

        if((classRoomId.length() < 3)){
            Toast.makeText(getActivity(),"会议id的长度需要大于等于3",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!StringUtils.hasChar(classRoomId)){
            Toast.makeText(getActivity(),"会议id至少包含一个字母",Toast.LENGTH_SHORT).show();
            return;
        }

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("classroomID", classRoomId);
                    JSONObject jsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/UpdateClassRoomID?classRoomID=" + classRoomId, js);
                    Log.e("getClassRoomLessonID2", jsonObject.toString()); //{"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":2477}
                    int retCode = jsonObject.getInt("RetCode");
                    Message msg = Message.obtain();
                    msg.what = 0x1006;
                    msg.obj = retCode;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    private void getRoomID() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomID");
                    Log.e("getClassRoomLessonID2", jsonObject.toString()); //{"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":2477}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            String retdate = jsonObject.getString("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1007;
                            msg.obj = retdate;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private void logout() {
        // TODO Auto-generated method stub
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                JSONObject jsonObject = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Logout");
                Log.e("dk", jsonObject.toString());
                formatlogout(jsonObject);
            }

        }).start(ThreadManager.getManager());
    }

    private Intent intent;

    private void formatlogout(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        try {
            int retCode = jsonObject.getInt("RetCode");
            String error = jsonObject.getString("ErrorMessage");
            switch (retCode) {
                case 0:
                    sharedPreferences = getActivity().getSharedPreferences(
                            AppConfig.LOGININFO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLogIn", false);
                    editor.commit();
                    AppConfig.isUpdateCustomer = false;
                    AppConfig.isUpdateDialogue = false;
                    AppConfig.HASUPDATAINFO = false;
                    RongIM.getInstance().logout();
                    RongIM.getInstance().disconnect();
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    MainActivity.instance.finish();
                    getActivity().overridePendingTransition(R.anim.tran_in_null, R.anim.tran_out_null);
                    break;
                case -1500:
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void getPersonInfo() {
        /*UserGet userget = new UserGet();
        Log.e("xxxx", "1");
        userget.setDetailListener(new UserGet.DetailListener() {

            @Override
            public void getUser(Customer user) {
                AvatarUrl = user.getUrl() + "";
                memberPoints = "0";
                expirationDate = user.getExpirationDate();
                SkilledFields = user.getSkilledFields();
                ArticleCount = "0";

                tv_pc_article.setText(ArticleCount);
                pc_tv_date.setText(getResources().getString(R.string.to) + " " + expirationDate);
                pi_goodatfield.setText(SkilledFields);
                tv_pc_integral.setText(memberPoints);
                tv_pc_account_level.setText(user.getPhone());
                downloadAttachment();

            }

            @Override
            public void getMember(MemberBean member) {
				*//*AvatarUrl = member.getAvatarUrl() + "";
                memberPoints = member.getMemberPoints();
				expirationDate = member.getExpirationDate();
				SkilledFields = member.getSkilledFields();
				ArticleCount = member.getArticleCount();

				tv_pc_article.setText(ArticleCount);
				pc_tv_date.setText("至"+expirationDate);
				pi_goodatfield.setText(SkilledFields);
				tv_pc_integral.setText(memberPoints);
                tv_pc_account_level.setText(member.getPhone());
				downloadAttachment();*//*
            }
        });
        userget.CustomerDetailRequest(getActivity(), AppConfig.UserID);*/


        LoginGet loginget = new LoginGet();
        loginget.setDetailGetListener(new LoginGet.DetailGetListener() {

            @Override
            public void getUser(Customer user) {
                AvatarUrl = user.getUrl() + "";
                memberPoints = "0";
                expirationDate = user.getExpirationDate();
                SkilledFields = user.getSkilledFields();
                account_name = user.getName();
                ArticleCount = "0";

//                tv_pc_article.setText(ArticleCount);
                pc_tv_date.setText("至" + expirationDate);
                pi_goodatfield.setText(SkilledFields);
                tv_pc_account_name.setText(account_name);
//                tv_pc_integral.setText(memberPoints);
                tv_pc_account_level.setText(user.getPhone());
                downloadAttachment();
            }

            @Override
            public void getMember(Customer member) {
                // TODO Auto-generated method stub

            }
        });
        loginget.CustomerDetailRequest(getActivity(), AppConfig.UserID);
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
        }).start(((App)(getActivity().getApplication())).getThreadMgr());
    }

    private CustomerYu formatjsonYu(JSONObject jsonObject) {
        CustomerYu bean = new CustomerYu();
        try {

            JSONObject object = jsonObject.getJSONObject("RetData");
            AvatarUrl = object.getString("AvatarUrl");
            account_name = object.getString("FullName");

            PrimaryPhone = object.getString("PrimaryPhone");

            bean.setAvatarUrl(AvatarUrl);
            bean.setFullName(FullName);
            bean.setPrimaryPhone(PrimaryPhone);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }




    public void downloadAttachment() {
        Uri imageUri = Uri.parse(AvatarUrl);
        contacts_portrait.setImageURI(imageUri);
    }

    private AlertDialog builder;

    public void DialogVip(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        View windov = inflater.inflate(R.layout.pc_recharge_dialogvip, null);

        windov.findViewById(R.id.tv_pc_apply).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        windov.findViewById(R.id.img_pc_close).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        builder = new AlertDialog.Builder(context).show();
        Window dialogWindow = builder.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的0.65
        p.height = (int) (d.getHeight() * 0.8);
        dialogWindow.setAttributes(p);
        builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        builder.setContentView(windov);
    }

    public void DialogSvip(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        View windov = inflater.inflate(R.layout.pc_recharge_dialogsvip, null);

        LinearLayout tv_pc_apply = (LinearLayout) windov
                .findViewById(R.id.tv_pc_apply);
        tv_pc_apply.setVisibility(View.GONE);
        LinearLayout ll_pc_svip = (LinearLayout) windov
                .findViewById(R.id.ll_pc_svip);
        ll_pc_svip.setVisibility(View.GONE);

        windov.findViewById(R.id.tv_pc_apply).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        windov.findViewById(R.id.img_pc_close).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        builder = new AlertDialog.Builder(context).show();
        Window dialogWindow = builder.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的0.65
        p.height = (int) (d.getHeight() * 0.8);
        dialogWindow.setAttributes(p);
        builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        builder.setContentView(windov);
    }

    public void LoginoutDialog(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        View windov = inflater.inflate(R.layout.pc_loginout_dialog, null);

        windov.findViewById(R.id.pc_loginout_dialog_cancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        builder.dismiss();
                    }
                });
        windov.findViewById(R.id.pc_loginout_dialog_report).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        logout();
                    }
                });
        builder = new AlertDialog.Builder(context).show();
        Window dialogWindow = builder.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
//        p.height = (int) (d.getHeight() * 0.3);
        p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(p);
        builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        builder.setContentView(windov);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //会议ID
            case R.id.rl_pc_klassroomID:
                modifyKlassRoomID();
                break;
                //个人设置
            case R.id.rl_contacts_portrait:
                intent = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_school_name:
                intent = new Intent(getActivity(), SchoolProfileActivity.class);
                startActivity(intent);
                break;
                //创建新账号
            case R.id.rl_cn_account:
                intent = new Intent(getActivity(), CreateOrganizationActivityV2.class);
                intent.putExtra("from_app_setting",false);
//                intent = new Intent(getActivity(), CreateOrganizationActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_teacher_profile:
                intent = new Intent(getActivity(), TeacherProfileActivity.class);
//                    startActivity(intent);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        new Pair(tv_teacher_profile, TeacherProfileActivity.汗));
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                break;
            case R.id.rl_pc_portrait:
                intent = new Intent(getActivity(),
                        SelfDescriptionActivity.class);
                intent.putExtra("topname", "description");
                startActivity(intent);
                break;
            case R.id.ll_pc_integral:
                intent = new Intent(getActivity(), FinishedCourseActivity.class);
                intent.putExtra("memberPoints", memberPoints);
                startActivity(intent);
                break;
            case R.id.ll_pc_publish_article:
                intent = new Intent(getActivity(),
                        MyCourseTemplateActivity.class);
                startActivity(intent);
                break;
                //账户设置
            case R.id.rl_cn_account_setting:
                intent = new Intent(getActivity(),
                        AccountSettingActivity.class);
                startActivity(intent);
                break;
                //我的收藏
            case R.id.ll_pc_collection:
                intent = new Intent(getActivity(), PersonalCollectionActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_pc_note:  //我的笔记
//                intent = new Intent(getActivity(), PersonalCollectionActivity.class);
//                startActivity(intent);
//                Toast.makeText(getActivity(),"my note",Toast.LENGTH_LONG).show();
                intent = new Intent(getActivity(), MyNoteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
                //切换语言
            case R.id.rl_pc_language:
                intent = new Intent(getActivity(), LanguageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.rl_pc_professionalField:
                intent = new Intent(getActivity(),
                        ProfessionalFieldActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_pc_effective:
                intent = new Intent(getActivity(), EffectiveActivity.class);
                Log.e("effective", expirationDate + "");
                intent.putExtra("effective", expirationDate);
                startActivity(intent);
                break;
                //跟换组织
            case R.id.lin_switch:
                getToSwitchCompany();
                break;
                //修改密码
            case R.id.rl_pc_password:
                intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
                break;
                //关于
            case R.id.rl_pc_about:
                GoToAbout();
                break;
                //帮助中心
            case R.id.rl_pc_hc:
                GoToHC();
                break;
                //分享Kloudsync
            case R.id.rl_pc_sk:
                ShareKloudsync();
                break;
                //退出登录
            case R.id.rl_pc_loginout:
                LoginoutDialog(getActivity());
                break;
            case R.id.rl_guide:
                startActivity(new Intent(getActivity(), GuideActivity.class));
                break;
            case R.id.rl_pc_synctv:
                startActivity(new Intent(getActivity(), SyncTvActivity.class));
                break;
            case R.id.ll_digital_note:
                startDigitalPens();
                break;
            default:
                break;
        }
    }

	private void startDigitalPens() {
		Intent intent;
        EverPen autoPen = EverPenManger.getInstance(getActivity()).getAutoPen();
        if (autoPen != null && mCurrentPen != null) {
            intent = new Intent(getActivity(), CurrentPenStatusActivity.class);
            intent.putExtra(CurrentPenStatusActivity.SIMILARPENSOURCE, mCurrentPen.getSimilaPenSource());
            intent.putExtra(CurrentPenStatusActivity.PENTYPE, mCurrentPen.getPenType());
		} else {
			intent = new Intent(getActivity(), DigitalPensActivity.class);
		}
        startActivity(intent);
    }

    @Override
    public void modifyClick(String newId) {
        UpdateClassRoomID(newId);
    }

	public void setCurrentPenNameAndStatus(boolean isConnected) {
        if (mPenSource != null && mPenStatus != null && mCurrentPenStatus != null) {
            mCurrentPen = EverPenManger.getInstance(getActivity()).getCurrentPen();
            EverPen autoPen = EverPenManger.getInstance(getActivity()).getAutoPen();
            if (autoPen != null) {
                mPenSource.setVisibility(View.VISIBLE);
                mPenStatus.setVisibility(View.VISIBLE);
                mCurrentPenStatus.setVisibility(View.VISIBLE);
                mPenSource.setText(mCurrentPen.getPenType() + mCurrentPen.getSimilaPenSource() + mCurrentPen.getPenName());
                if (isConnected) {
                    mPenStatus.setText(R.string.the_connected);
                    mCurrentPenStatus.setText("");
                } else {
                    mPenStatus.setText(R.string.not_connected);
                    mCurrentPenStatus.setText(R.string.is_scanning);
                }

            } else {
                mPenSource.setText("");
                mPenStatus.setText("");
                mCurrentPenStatus.setText("");
                mPenSource.setVisibility(View.GONE);
                mPenStatus.setVisibility(View.GONE);
                mCurrentPenStatus.setVisibility(View.GONE);
            }
        }
    }
}
