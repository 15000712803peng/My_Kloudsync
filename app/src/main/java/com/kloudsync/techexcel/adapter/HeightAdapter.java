package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.kloudsync.techexcel.R;

import java.util.List;

public class HeightAdapter extends CommonAdapter<String>{
	
	private List<String> list = null;
	private Context mContext;
	private String seId = "-1";

	public HeightAdapter(Context mContext, List<String> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
	}
	
	public void updateListView(List<String> list, String seId) {
		this.list = list;
		this.seId = seId;
		updateAdapter(list);
	}
	
	public void SelectedItem(String seId){
		this.seId = seId;
	}

	@SuppressLint("NewApi") @Override
	public void convert(ViewHolder holder, String height, int position) {
		holder.setText(R.id.tv_name, height + "cm")
			.setViewVisible(R.id.img_selected, height.equals(seId) ? View.VISIBLE:View.GONE);
	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.height_item;
	}

}
