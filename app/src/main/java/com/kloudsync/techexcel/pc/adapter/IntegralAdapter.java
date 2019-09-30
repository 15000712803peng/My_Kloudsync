package com.kloudsync.techexcel.pc.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.pc.bean.IntegralDetails;

public class IntegralAdapter extends BaseAdapter {

	private List<IntegralDetails> list = new ArrayList<IntegralDetails>();
	private Context mContext;

	public IntegralAdapter(Context mContext, List<IntegralDetails> list) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		view = LayoutInflater.from(mContext).inflate(R.layout.pc_integral_item,
				null);

		TextView tv_pc_integral_type = (TextView) view
				.findViewById(R.id.tv_pc_integral_type);
		TextView tv_pc_integral_porint = (TextView) view
				.findViewById(R.id.tv_pc_integral_porint);
		TextView tv_pc_state = (TextView) view.findViewById(R.id.tv_pc_state);
		TextView pc_integral_time = (TextView) view
				.findViewById(R.id.pc_integral_time);
		ImageView img_pc_article = (ImageView) view
				.findViewById(R.id.img_pc_article);

		String pointValue = list.get(position).getPointValue();
		String changeTypeName = list.get(position).getChangeTypeName();
		String changeLog = list.get(position).getChangeLog();
		String changeDate = list.get(position).getChangeDate();
		
		
		int pValues = Integer.parseInt(pointValue);
		
		if (pValues < 0) {
			img_pc_article.setImageResource(R.drawable.integral_e);
		}else{
			img_pc_article.setImageResource(R.drawable.integral_i);
		}
		tv_pc_integral_type.setText(changeTypeName+":");
		tv_pc_integral_porint.setText(pointValue);
		tv_pc_state.setText(changeLog);
		pc_integral_time.setText(changeDate);
		return view;
	}
}
