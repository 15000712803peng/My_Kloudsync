package com.kloudsync.user.techexcel.pi.tools;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.CommonAdapter;
import com.kloudsync.techexcel.adapter.ViewHolder;

public class AdressAdapter extends CommonAdapter<ProvinceBean> {

	private List<ProvinceBean> list = null;
	private Context mContext;

	public void updateListView(List<ProvinceBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@SuppressLint("NewApi")
	public void convert(ViewHolder holder, ProvinceBean kw, int position) {
		holder.setText(R.id.tv_adressName, kw.getName());

	}

	@Override
	public int getLayout(int position) {
		return R.layout.pi_adress_item;
	}

	public AdressAdapter(Context context, List<ProvinceBean> datas) {
		super(context, datas);
		this.list = datas;
		this.mContext = context;
	}

}
