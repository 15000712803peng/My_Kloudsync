package com.kloudsync.techexcel.contact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.friends.activity.DeleteFriendsActivity;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.pc.ui.ShowMemberInfoActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.DetailGetListener;
import com.kloudsync.techexcel.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class MemberDetail extends FragmentActivity {

	private Customer customer;
	private TextView tv_back, tv_name, tv_title, tv_phone, tv_address, tv_message;
	private TextView tv_description, tv_introduction;
	private TextView tv_allev;
	private TextView tv_dialogue, tv_mobile;
	private TextView tv_membername;
	private LinearLayout lin_paper, lin_case;
	private LinearLayout lin_gotoziliao;	
	private ImageView img_navi;
	
	@SuppressWarnings("unused")
	private ImageView img_crown, img_new;
	private CircleImageView img_head;

	private String UserID;
	
	public static MemberDetail instance;    
	
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memberdetail);
		
		instance = this;
		
//		customer = (Customer) getIntent().getSerializableExtra("Customer");
		UserID = getIntent().getStringExtra("UserID");
		
		initView();
		
		/*ImageView ii = (ImageView) findViewById(R.id.img_star);
		LayoutParams ps = (LayoutParams) ii.getLayoutParams();
        ps.height = 30;
        ps.width = 30;
        ii.setLayoutParams(ps); */

	}

	private void initView() {
		tv_description = (TextView) findViewById(R.id.tv_description);
		tv_introduction = (TextView) findViewById(R.id.tv_introduction);
		tv_allev = (TextView) findViewById(R.id.tv_allev);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_dialogue = (TextView) findViewById(R.id.tv_dialogue);
		tv_mobile = (TextView) findViewById(R.id.tv_mobile);
		tv_message = (TextView) findViewById(R.id.tv_message);
		tv_membername = (TextView) findViewById(R.id.tv_membername);
		img_head = (CircleImageView) findViewById(R.id.img_head);
		img_crown = (ImageView) findViewById(R.id.img_crown);
		img_new = (ImageView) findViewById(R.id.img_new);
		img_navi = (ImageView) findViewById(R.id.img_navi);
//		img_message = (ImageView) findViewById(R.id.img_message);
		lin_paper = (LinearLayout) findViewById(R.id.lin_paper);
		lin_case = (LinearLayout) findViewById(R.id.lin_case);
		lin_gotoziliao = (LinearLayout) findViewById(R.id.lin_gotoziliao);
		
		GetDetail();
		
		tv_back.setOnClickListener(new MyOnClick());
		tv_allev.setOnClickListener(new MyOnClick());
		lin_paper.setOnClickListener(new MyOnClick());
		lin_case.setOnClickListener(new MyOnClick());
		tv_dialogue.setOnClickListener(new MyOnClick());
		tv_mobile.setOnClickListener(new MyOnClick());
		tv_message.setOnClickListener(new MyOnClick());
		img_head.setOnClickListener(new MyOnClick());
		img_navi.setOnClickListener(new MyOnClick());
		lin_gotoziliao.setOnClickListener(new MyOnClick());
	}
	
	private void GetDetail() {
		LoginGet loginget = new LoginGet();
		loginget.setDetailGetListener(new DetailGetListener() {
			
			@Override
			public void getUser(Customer user) {
				
			}

			@Override
			public void getMember(Customer member) {
				customer = member;
				showInfo();				
			}
		});
		loginget.MemberDetailRequest(getApplicationContext(), UserID);

		
	}


    public ImageLoader imageLoader;
	private void showInfo() {
        imageLoader=new ImageLoader(getApplicationContext()); 
        
		tv_name.setText(customer.getName());
		tv_title.setText(customer.getTitle());
		tv_phone.setText(customer.getPhone());
		tv_address.setText(customer.getAddress());
		tv_description.setText(customer.getSkilledFields());
		
//		tv_membername.setText(1 == customer.getType() ? "优葆人详情" : "会员详情");
		
		/*Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.health1);
		RoundImageDrawable Rid = new RoundImageDrawable(bmp);
		img_head.setImageDrawable(Rid);*/
		String url = customer.getUrl();
		if (null == url || url.length() < 1) {
			img_head.setImageResource(R.drawable.hello);			
		}else{
			imageLoader.DisplayImage(customer.getUrl(), img_head);
		}
		img_crown.setVisibility(customer.isCrown() ? View.VISIBLE
				: View.GONE);
		img_new.setVisibility(customer.isNew() ? View.VISIBLE
				: View.GONE);
		
		String focus = "";
		int size = 0;
		if(customer.getFocusPoints() != null){
			size = (customer.getFocusPoints().size() > 3 ? 3 : customer
				.getFocusPoints().size());
		}
		for (int i = 0; i < size; i++) {
			String fp = customer.getFocusPoints().get(i);
			focus += fp;
		}
//		tv_description.setText(focus);
		
		tv_introduction.setText(customer.getSummary());
		
	}
	
	protected class MyOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				FinishActivityanim();
				break;
			case R.id.tv_allev:
				Toast.makeText(getApplicationContext(), "待完成", Toast.LENGTH_SHORT).show();
				break;
			case R.id.lin_paper:
				Toast.makeText(getApplicationContext(), "待完成", Toast.LENGTH_SHORT).show();
				break;
			case R.id.lin_case:
				Toast.makeText(getApplicationContext(), "待完成", Toast.LENGTH_SHORT).show();
				break;
			case R.id.tv_dialogue:
				GoToDialog();
				break;
			case R.id.tv_mobile:
				GoToMobile();
				break;
			case R.id.img_head:
				GoTOModifyUser();
				break;
			case R.id.img_navi:
				GoTODeleteUser();
				break;
			case R.id.tv_message:
				GoTOModifyUser();
				break;
			case R.id.lin_gotoziliao:
				GoTOModifyUser();
				break;

			default:
				break;
			}
			
		}
		
	}
	
	public void GoTOModifyUser() {
		Intent intent = new Intent(MemberDetail.this, ShowMemberInfoActivity.class);
		intent.putExtra("UserID", UserID);
		startActivity(intent);
		
	}
	
	public void GoTODeleteUser() {
		Intent intent = new Intent(MemberDetail.this, DeleteFriendsActivity.class);
		intent.putExtra("RongID", customer.getUBAOUserID());
		startActivity(intent);
	}

	public void GoToDialog() {
		AppConfig.Name = customer.getName();
		AppConfig.isUpdateDialogue = true;
		RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
			@Override
			public UserInfo getUserInfo(String s) {
				return new UserInfo(
						customer.getUBAOUserID(),
						customer.getName(),
						Uri.parse(customer.getUrl()));
			}
		},true);
		RongIM.getInstance().startPrivateChat(MemberDetail.this, customer.getUBAOUserID(), customer.getName());
		
		
	}
	
	public void GoToMobile() {
		String uri = "tel:" + customer.getPhone();
		Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(
	  			  uri));
	    startActivity(dialIntent); 
		
	}

	private void FinishActivityanim() {
		finish();
	}

	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("MemberDetail"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("MemberDetail");
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
