package com.ub.techexcel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ub.techexcel.bean.LineItem;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

public class PlanAdapter extends BaseAdapter {

	private List<LineItem> list = new ArrayList<LineItem>();
	private Context context;

	public PlanAdapter(Context context, List<LineItem> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.addservicethree_itemthree, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.des = (TextView) convertView.findViewById(R.id.des);
			holder.ischeck = (LinearLayout) convertView
					.findViewById(R.id.ischeck);
			holder.istextview = (TextView) convertView
					.findViewById(R.id.istextview);
			holder.isImage = (ImageView) convertView.findViewById(R.id.isimage);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final LineItem bean = list.get(position);
		if (bean.getCheckOption() == AppConfig.OptionalNo) { // 默认没有选择
			holder.ischeck
					.setBackgroundResource(R.drawable.addservice_threenochoose);
			holder.istextview.setTextColor(context.getResources().getColor(
					R.color.c4));
			holder.istextview.setText("选择该服务");
			holder.isImage.setImageResource(R.drawable.select_n);
		} else if (bean.getCheckOption() == AppConfig.OptionalYes) { // 默认已经选择
			holder.ischeck.setBackgroundResource(R.drawable.addservice_three);
			holder.istextview.setTextColor(context.getResources().getColor(
					R.color.c7));
			holder.istextview.setText("已选择");
			holder.isImage.setImageResource(R.drawable.select_h);
		} else { // 强制性选择
			holder.ischeck.setBackgroundResource(R.drawable.addservice_three);
			holder.istextview.setTextColor(context.getResources().getColor(
					R.color.c7));
			holder.istextview.setText("已选择");
			holder.isImage.setImageResource(R.drawable.select_h);
		}

		holder.ischeck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onTouchingLetterChangedListener
						.onTouchingLetterChanged(position);
			}
		});
		holder.name.setText(bean.getEventName());
		holder.des.setText(bean.getDescription());
		return convertView;
	}

	class ViewHolder {
		TextView name, des;
		LinearLayout ischeck;
		TextView istextview;
		ImageView isImage;
	}

	private OnHealthChangedListener onTouchingLetterChangedListener;

	public void setOnHealthChangedListener(
			OnHealthChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnHealthChangedListener {
		public void onTouchingLetterChanged(int position);
	}

}
