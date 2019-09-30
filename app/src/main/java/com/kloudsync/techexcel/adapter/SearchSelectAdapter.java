package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.view.View;

import com.kloudsync.techexcel.R;

import java.util.List;

public class SearchSelectAdapter extends CommonAdapter<String>{
	
	private List<String> list = null;
	private Context mContext;
	private int pj_selected = -1;

	public SearchSelectAdapter(Context mContext, List<String> list , int pj_selected) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
		this.pj_selected = pj_selected;
	}	

	public void updateListView(List<String> list) {
		this.list = list;
		updateAdapter(list);
	}
	
	public void getPosition(int pj_selected){
		this.pj_selected = pj_selected;
		notifyDataSetChanged();
	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.serchselect_item;
	}

	@Override
	public void convert(ViewHolder holder, String t, int position) {
		holder.setText(R.id.tv_show, t);
		if(position == pj_selected){
			holder.setViewVisible(R.id.img_choosen, View.VISIBLE);
		}else{
			holder.setViewVisible(R.id.img_choosen, View.INVISIBLE);
		}
		
	}


}
