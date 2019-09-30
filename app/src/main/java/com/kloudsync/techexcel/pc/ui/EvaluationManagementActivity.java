package com.kloudsync.techexcel.pc.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.pc.adapter.EvaluationManagementAdapter;
import com.kloudsync.techexcel.pc.swipemenulistview.SwipeMenu;
import com.kloudsync.techexcel.pc.swipemenulistview.SwipeMenuCreator;
import com.kloudsync.techexcel.pc.swipemenulistview.SwipeMenuItem;
import com.kloudsync.techexcel.pc.swipemenulistview.SwipeMenuListView;
import com.umeng.analytics.MobclickAgent;

public class EvaluationManagementActivity extends Activity {

	private SwipeMenuListView lv_pc_evaluation_management;
	private LinearLayout img_back;
	private TextView tv_name;
	private List<String> list;
	private EvaluationManagementAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_evaluation_management);
		initView();
		list = new ArrayList<String>();
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		mAdapter = new EvaluationManagementAdapter(this, list);
		lv_pc_evaluation_management.setAdapter(mAdapter);

		// SwipeMenuListView
		sMCreator();

	}

	private void sMCreator() {
		// TODO Auto-generated method stub
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				openItem.setBackground(R.color.evaluation_orange);
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setTitle("回复");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(R.color.evaluation_red);
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setTitle("举报");
				// set item title fontsize
				deleteItem.setTitleSize(18);
				// set item title font color
				deleteItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		lv_pc_evaluation_management.setMenuCreator(creator);

		// step 2. 侦听器项目单击事件
		lv_pc_evaluation_management
				.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						// String item = list.get(position);
						switch (index) {
						case 0:

							break;
						case 1:
							Dialog(EvaluationManagementActivity.this);
							break;
						}
					}

				});
		// set SwipeListener
		lv_pc_evaluation_management.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		lv_pc_evaluation_management
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(),
								position + " long click", 0).show();
					}
				});
	}

	private AlertDialog builder;

	public void Dialog(Context context) {
		final LayoutInflater inflater = LayoutInflater.from(context);

		View windov = inflater.inflate(
				R.layout.pc_evaluation_managemen_item_dialog, null);
		
		windov.findViewById(R.id.pc_em_dialog_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						builder.dismiss();
					}
				});
		windov.findViewById(R.id.pc_em_dialog_report).setOnClickListener(
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
		p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
		p.height = (int) (d.getHeight() * 0.5);
		dialogWindow.setAttributes(p);
		builder.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		builder.setContentView(windov);
	}

	// 评价举报
	/*
	 * private void Report() { // TODO Auto-generated method stub LayoutInflater
	 * inflaterDl = LayoutInflater.from(this); View layout = (LinearLayout)
	 * inflaterDl.inflate( R.layout.pc_evaluation_managemen_item_dialog, null);
	 * 
	 * // 对话框 AlertDialog.Builder mBuilder = new AlertDialog.Builder(
	 * EvaluationManagementActivity.this); final AlertDialog dialog =
	 * mBuilder.create(); dialog.setView(layout, 0, 0, 0, 0);
	 * dialog.getWindow().setBackgroundDrawable(new
	 * ColorDrawable(Color.TRANSPARENT)); dialog.show();
	 * 
	 * ImageView pc_em_iv_raido1 = (ImageView) layout
	 * .findViewById(R.id.pc_em_iv_raido1); ImageView pc_em_iv_raido2 =
	 * (ImageView) layout .findViewById(R.id.pc_em_iv_raido2); ImageView
	 * pc_em_iv_raido3 = (ImageView) layout .findViewById(R.id.pc_em_iv_raido3);
	 * ImageView pc_em_iv_raido4 = (ImageView) layout
	 * .findViewById(R.id.pc_em_iv_raido4);
	 * 
	 * 
	 * // 取消按钮 LinearLayout btnOK = (LinearLayout) layout
	 * .findViewById(R.id.pc_em_dialog_cancel); btnOK.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * Toast.makeText(getApplicationContext(), "你是个好学生",
	 * Toast.LENGTH_SHORT).show(); dialog.dismiss(); } });
	 * 
	 * // 举报按钮 LinearLayout btnClose = (LinearLayout) layout
	 * .findViewById(R.id.pc_em_dialog_report); btnClose.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * Toast.makeText(getApplicationContext(), "这么屌的软件你也敢举报",
	 * Toast.LENGTH_SHORT).show(); dialog.dismiss(); } }); }
	 */

	private void initView() {
		lv_pc_evaluation_management = (SwipeMenuListView) findViewById(R.id.lv_pc_evaluation_management);
		tv_name = (TextView) findViewById(R.id.tv_name);
		img_back = (LinearLayout) findViewById(R.id.img_back);

		tv_name.setText(R.string.enaluation_management_title);

		img_back.setOnClickListener(new myOnClick());
	}

	private class myOnClick implements OnClickListener {
		Intent intent = new Intent();

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.img_back:
				finish();
				break;
			default:
				break;
			}

		}

	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("EvaluationManagementActivity"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("EvaluationManagementActivity");
	    MobclickAgent.onPause(this);
	}
}
