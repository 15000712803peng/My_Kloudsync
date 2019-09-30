package com.kloudsync.techexcel.adapter;

import android.content.Context;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.PersonalInfo;

import java.util.List;

public class PersonalInfoAdapter extends CommonAdapter<PersonalInfo>{
	
	private List<PersonalInfo> list = null;
	private Context mContext;

	public PersonalInfoAdapter(Context mContext, List<PersonalInfo> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
	}
	
	public void updateListView(List<PersonalInfo> list) {
		this.list = list;
		updateAdapter(list);
	}

	@Override
	public void convert(ViewHolder holder, PersonalInfo pi, int position) {

		holder.setText(R.id.tv_label, pi.getLabel()).setText(R.id.tv_value,
				pi.getValue());

	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.personal_item;
	}
	

}
