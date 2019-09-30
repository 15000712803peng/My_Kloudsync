package com.kloudsync.techexcel.pc.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.ViewHolder;
import com.kloudsync.techexcel.bean.ConditionBean;


public class AllListFilterAdapter extends CommonAdapterW<ConditionBean> {
	private Context context;
	private List<ConditionBean> mDatas;
	private int itemLayoutId;
	private int selectposition; //上一次选中的位置

	public AllListFilterAdapter(Context context, List<ConditionBean> mDatas,
			int itemLayoutId, int selectposititon) {
		super(context, mDatas, itemLayoutId);
		this.context = context;
		this.mDatas = mDatas;
		this.itemLayoutId = itemLayoutId;
		this.selectposition = selectposititon;
	}

	public void changePosititon(int position){
		selectposition=position;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder = ViewHolder.get(context, convertView,
				parent, itemLayoutId, position);

		TextView name = viewHolder.getView(R.id.pc_currentstatus_item_name);
		TextView divder = viewHolder.getView(R.id.pc_currentstatus_item_divider);
		ImageView imageView = viewHolder.getView(R.id.pc_currentstatus_item_image);
		name.setText(mDatas.get(position).getFilterValue()); 
		if (position == mDatas.size() - 1) {
			divder.setVisibility(View.GONE);
		} else {
			divder.setVisibility(View.VISIBLE);
		}
		if (position == selectposition) {
			imageView.setVisibility(View.VISIBLE);
		} else {
			imageView.setVisibility(View.INVISIBLE);
		}
		return viewHolder.getConvertView();
	}

}
