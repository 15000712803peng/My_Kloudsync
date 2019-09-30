package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.CommonUse;

import java.util.List;

public class CommonUsedAdapter extends CommonAdapter<CommonUse>{
	
	private List<CommonUse> list = null;
	private Context mContext;
	private int seId = -1;

	public CommonUsedAdapter(Context mContext, List<CommonUse> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
	}
	
	public void updateListView(List<CommonUse> list, int seId) {
		this.list = list;
		this.seId = seId;
		updateAdapter(list);
	}
	
	public void SelectedItem(int seId){
		this.seId = seId;
	}

	@SuppressLint("NewApi") @Override
	public void convert(ViewHolder holder, CommonUse cu, int position) {
		holder.setText(R.id.tv_show, cu.getName()).setTextColor(
				R.id.tv_show,
				mContext.getResources().getColor(
						cu.getID() == seId ? R.color.green : R.color.darkgrey));

	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.commonused_item;
	}

}
