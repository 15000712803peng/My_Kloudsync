package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.Sex;

import java.util.List;

public class SexAdapter extends CommonAdapter<Sex>{
	
	private List<Sex> list = null;
	private Context mContext;
	private String seId = "-1";

	public SexAdapter(Context mContext, List<Sex> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
	}
	
	public void updateListView(List<Sex> list, String seId) {
		this.list = list;
		this.seId = seId;
		updateAdapter(list);
	}
	
	public void SelectedItem(String seId){
		this.seId = seId;
	}

	@SuppressLint("NewApi") @Override
	public void convert(ViewHolder holder, Sex sex, int position) {
		holder.setText(R.id.tv_name, sex.getName())
			.setViewVisible(R.id.img_selected, sex.getID().equals(seId) ? View.VISIBLE:View.GONE);
	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.sex_item;
	}

}
