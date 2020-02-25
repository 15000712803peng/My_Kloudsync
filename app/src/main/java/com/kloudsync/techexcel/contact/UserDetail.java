package com.kloudsync.techexcel.contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.PersonalInfoAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.CommonUse;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.PersonalInfo;
import com.kloudsync.techexcel.pc.ui.ShowUserInfoActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.friends.activity.DeleteFriendsActivity;
import com.ub.service.activity.MyOrderActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

public class UserDetail extends FragmentActivity {

    private Customer customer;
    private TextView tv_name, tv_gender, tv_peertimeid, tv_birthday,
            tv_message;
    //	private TextView tv_focus, tv_description;
    private TextView tv_dialogue, tv_mobile;
    private TextView tv_remark;
    private LinearLayout lin_add_richang ;
    //	private LinearLayout lin_gotoziliao;
    private LinearLayout lin_remark;
    private RelativeLayout rl_purchased;
    //	private LineChart mChart;
    private List<TextView> tvs = new ArrayList<TextView>();
    private List<TextView> tvs_line = new ArrayList<TextView>();

    private SimpleDraweeView img_head;
    private ImageView img_crown, img_new;
    private ImageView img_delete;
    private ImageView tv_back;
    // private CustomViewPager vp;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mTabs = new ArrayList<Fragment>();

    // private int tvIDs[] = { R.id.tv_hd, R.id.tv_pi };
    // private int tvLineIDs[] = { R.id.tv_hdline, R.id.tv_piline };

    private ListView lv_info;
    private PersonalInfoAdapter madapter;

    private String labels[] = {"身高", "体重", "婚姻", "上次月经时间", "平均月经周期", "速度",
            "力量"};
    private String values[] = {"163cm", "55kg", "未婚", "2015-10-15", "30",
            "65m/s", "卧推800kg"};
    private List<PersonalInfo> mlist = new ArrayList<PersonalInfo>();

    private String UserID;

    public static UserDetail instance;

    float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetail);

        instance = this;

        UserID = getIntent().getStringExtra("UserID");

        initView();

    }

    private void initView() {
//		tv_focus = (TextView) findViewById(tv_focus);
//		tv_description = (TextView) findViewById(R.id.tv_description);
        tv_back = findViewById(R.id.tv_back);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_peertimeid = (TextView) findViewById(R.id.tv_peertimeid);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
//		tv_age = (TextView) findViewById(tv_age);
//		tv_phone = (TextView) findViewById(R.id.tv_phone);
//		tv_address = (TextView) findViewById(R.id.tv_address);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_dialogue = (TextView) findViewById(R.id.tv_dialogue);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
//        lin_add_guanli = (LinearLayout) findViewById(R.id.lin_add_guanli);
        lin_add_richang = (LinearLayout) findViewById(R.id.lin_add_richang);
//		lin_add_health = (LinearLayout) findViewById(R.id.lin_add_health);
        lin_remark = (LinearLayout) findViewById(R.id.lin_remark);
        rl_purchased = (RelativeLayout) findViewById(R.id.rl_purchased);
        img_head = (SimpleDraweeView) findViewById(R.id.img_head);
        img_crown = (ImageView) findViewById(R.id.img_crown);
        img_new = (ImageView) findViewById(R.id.img_new);
        img_delete = (ImageView) findViewById(R.id.img_delete);
//		lin_gotoziliao = (LinearLayout) findViewById(lin_gotoziliao);
        lv_info = (ListView) findViewById(R.id.lv_info);
//		mChart = (LineChart) findViewById(R.id.lchart);

		/*
         * vp = (CustomViewPager) findViewById(R.id.vp);
		 * vp.setPagingEnabled(true); for (int i = 0; i < tvIDs.length; i++) {
		 * TextView tv = (TextView) findViewById(tvIDs[i]); TextView tv_line =
		 * (TextView) findViewById(tvLineIDs[i]); tv.setOnClickListener(new
		 * MyOnClick()); tvs.add(tv); tvs_line.add(tv_line); } initDatas();
		 * vp.setAdapter(mAdapter); GoTOTab(0);
		 */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;

        GetDetail();
//		getData();

    }

    private void GetDetail() {
        LoginGet loginget = new LoginGet();
        loginget.setDetailGetListener(new LoginGet.DetailGetListener() {

            @Override
            public void getUser(Customer user) {
                customer = user;
                showInfo();

//				initaction();// 健康仪表
            }

            @Override
            public void getMember(Customer member) {
                // TODO Auto-generated method stub

            }
        });
        loginget.CustomerDetailRequest(getApplicationContext(), UserID);
    }

	/*private void getData() {
		mlist = new ArrayList<PersonalInfo>();
		for (int i = 0; i < labels.length; i++) {
			PersonalInfo pi = new PersonalInfo(labels[i], values[i]);
			mlist.add(pi);
		}
		LayoutParams params = (LayoutParams) lv_info.getLayoutParams();
		params.height = (int) (mlist.size() * 50 * density);
		madapter = new PersonalInfoAdapter(getApplicationContext(), mlist);
		lv_info.setFocusable(false);
		lv_info.setAdapter(madapter);

	}*/

	/*
	 * private void initDatas() { HealthDashboard healthDashboard = new
	 * HealthDashboard(); PersonalInformation personalInformation = new
	 * PersonalInformation(); // MedicalReport medicalReport = new
	 * MedicalReport(); // UsersDaily usersDaily = new UsersDaily();
	 * 
	 * mTabs.add(healthDashboard); mTabs.add(personalInformation); //
	 * mTabs.add(medicalReport); // mTabs.add(usersDaily);
	 * 
	 * mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
	 * 
	 * @Override public int getCount() { return mTabs.size(); }
	 * 
	 * @Override public Fragment getItem(int position) { return
	 * mTabs.get(position); } };
	 * 
	 * }
	 */

	/*
	 * private void GoTOTab(int s) { for (int i = 0; i < tvs.size(); i++) { if
	 * (i == s) {
	 * tvs.get(s).setTextColor(getResources().getColor(R.color.green));
	 * tvs_line.get(s).setVisibility(View.VISIBLE); } else {
	 * tvs.get(i).setTextColor(getResources().getColor(R.color.darkgrey));
	 * tvs_line.get(i).setVisibility(View.GONE); } } vp.setCurrentItem(s,
	 * false);
	 * 
	 * }
	 */

    public ImageLoader imageLoader;

    private void showInfo() {
        imageLoader = new ImageLoader(getApplicationContext());

        tv_name.setText(customer.getName());
        tv_gender.setText(getString(customer.getSex().equals("2") ? R.string.Female : R.string.Male));
//		tv_age.setText(customer.getAge() + "岁");
//		tv_phone.setText(customer.getPhone());
//		tv_address.setText(customer.getAddress());
//		tv_description.setText(customer.getSelfDescription());
        tv_remark.setText(customer.getPersonalComment());
        tv_peertimeid.setText("null");
        tv_birthday.setText(customer.getBirthday());

        if (customer.getSex().equals("0")) {
            tv_gender.setText("");
        }

		/*
		 * Bitmap bmp = BitmapFactory.decodeResource(getResources(),
		 * R.drawable.user1); RoundImageDrawable Rid = new
		 * RoundImageDrawable(bmp); img_head.setImageDrawable(Rid);
		 */
        String url = customer.getUrl();
        Uri imageUri = Uri.parse(url);
        img_head.setImageURI(imageUri);
        /*if (null == url || url.length() < 1) {
            img_head.setImageResource(R.drawable.hello);
        } else {
            imageLoader.DisplayImage(customer.getUrl(), img_head);
        }*/
        img_crown.setVisibility(customer.isCrown() ? View.VISIBLE : View.GONE);
        img_new.setVisibility(customer.isNew() ? View.VISIBLE : View.GONE);

        String focus = "";
        int size = 0;
        if (customer.getFocusPoints() != null) {
            size = (customer.getFocusPoints().size() > 3 ? 3 : customer
                    .getFocusPoints().size());
        }
        for (int i = 0; i < size; i++) {
            String fp = customer.getFocusPoints().get(i);
            focus += fp;
        }
        // tv_description.setText(focus);
//		showHealthConcerns();

        tv_back.setOnClickListener(new MyOnClick());
//        tv_message.setOnClickListener(new MyOnClick());
        tv_dialogue.setOnClickListener(new MyOnClick());
        tv_mobile.setOnClickListener(new MyOnClick());
        img_head.setOnClickListener(new MyOnClick());
        img_delete.setOnClickListener(new MyOnClick());
//		lin_gotoziliao.setOnClickListener(new MyOnClick());
//        lin_add_guanli.setOnClickListener(new MyOnClick());
        lin_add_richang.setOnClickListener(new MyOnClick());
        // lin_add_health.setOnClickListener(new MyOnClick());
        lin_remark.setOnClickListener(new MyOnClick());
        rl_purchased.setOnClickListener(new MyOnClick());
    }

    private void showHealthConcerns() {
        String hConcerns = "";
        ArrayList<CommonUse> cu_list = new ArrayList<CommonUse>();
        cu_list = customer.getHealthConcerns();
        for (int i = 0; i < cu_list.size(); i++) {
            CommonUse commonuse = cu_list.get(i);
            hConcerns += commonuse.getName();
            if (i != (cu_list.size() - 1)) {
                hConcerns += "、";
            }
        }
//		tv_focus.setText(hConcerns);

    }

    protected class MyOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    FinishActivityanim();
                    break;
                /*case R.id.tv_message:
                    GoTOModifyUser();
                    break;*/
                case R.id.img_head:
                    GoTOModifyUser();
                    break;
                case R.id.img_delete:
                    GoTODeleteUser();
                    break;
                case R.id.tv_dialogue:
                    GoToDialog();
                    break;
                case R.id.tv_mobile:
                    GoToMobile();
                    break;
			/*case lin_gotoziliao:
				GoTOModifyUser();
				break;*/
			/*case R.id.lin_add_guanli:
				// GoToAddGuanli();
				break;*/
                case R.id.lin_add_richang:

                    break;
			/*case R.id.lin_add_health:
				GoToHealthManage();
				break;*/
                case R.id.lin_remark:
                    GoToCRemark();
                    break;
                case R.id.rl_purchased:
                    GoToPurchaedCourse();
                    break;
                default:
                    break;
            }

        }

    }

    private void GoTODeleteUser() {
        Intent intent = new Intent(UserDetail.this, DeleteFriendsActivity.class);
        intent.putExtra("RongID", customer.getUBAOUserID());
        startActivity(intent);
    }

    private void GoToPurchaedCourse() {
        Intent intent = new Intent(UserDetail.this, PurchasedCoursesActivity.class);
        startActivity(intent);
    }

    private void GoToCRemark() {
        Intent intent = new Intent(UserDetail.this, ChangeRemarkActivity.class);
        intent.putExtra("remark", customer.getPersonalComment());
        intent.putExtra("userId", UserID);
        startActivity(intent);

    }



    public void GoToDialog() {
        AppConfig.Name = customer.getName();
        AppConfig.isUpdateDialogue = true;
        /*RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                return new UserInfo(
                        customer.getUBAOUserID(),
                        customer.getName(),
                        Uri.parse(customer.getUrl()));
            }
        }, true);*/
        RongIM.getInstance().startPrivateChat(UserDetail.this,
                customer.getUBAOUserID(), customer.getName());
		/*RongCallKit.startSingleCall(UserDetail.this,
				customer.getUBAOUserID(), RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO);*/
		/*List<String> x = new ArrayList();
		x.add(customer.getUBAOUserID());
		RongCallClient.getInstance().startCall(Conversation.ConversationType.PRIVATE,
				customer.getUBAOUserID(), x,
				RongCallCommon.CallMediaType.AUDIO,
				"http://ub.servicewise.net.cn:120/scripts/texcel/customerwise/" +
						"TxSwDownload/e2f3020fe2f302dce2f302dce2f30129/StanardLesson-Toefl+Wristing.pdf");*/
    }

    public void GoToAddGuanli() {
        Intent i = new Intent(UserDetail.this, MyOrderActivity.class);
        i.putExtra("userId", customer.getUserID());
        startActivity(i);

    }

    public void GoToMobile() {
        String uri = "tel:" + customer.getPhone();
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CALL_PHONE}, 0x0010);
        }else {
            startActivity(dialIntent);
        }

    }

    public void GoTOModifyUser() {
        Intent intent = new Intent(UserDetail.this, ShowUserInfoActivity.class);
        intent.putExtra("UserID", UserID);
        startActivity(intent);

    }

    private void FinishActivityanim() {
        finish();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UserDetail");
        MobclickAgent.onResume(this); // 统计时长

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UserDetail");
        MobclickAgent.onPause(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FinishActivityanim();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
