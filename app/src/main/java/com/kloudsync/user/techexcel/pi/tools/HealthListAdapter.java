package com.kloudsync.user.techexcel.pi.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;

public class HealthListAdapter extends BaseAdapter {
	private List<TaskBean> list = new ArrayList<TaskBean>();
	private Context mContext;
	private ImageLoader imageLoader;

	public HealthListAdapter(Context mContext, List<TaskBean> list) {
		this.mContext = mContext;
		this.list = list;
		imageLoader = new ImageLoader(mContext.getApplicationContext());
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		final ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.pi_health_tasklist, null);
			viewHolder.name = (TextView) view
					.findViewById(R.id.pi_health_task_name); // 名字
			viewHolder.image = (ImageView) view
					.findViewById(R.id.pi_health_task_image);
			viewHolder.add = (ImageView) view
					.findViewById(R.id.pi_health_task_add);
			viewHolder.description = (TextView) view
					.findViewById(R.id.pi_health_description);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		final int position1 = position;
		final TaskBean entity = list.get(position1);
		viewHolder.name.setText(entity.getName());
		viewHolder.description.setText(entity.getDescription());
		if (!(null == entity.getIconURL() || entity.getIconURL().length() < 1)) {
			imageLoader.DisplayImage3(entity.getIconURL(), viewHolder.image);
		}
		if(!entity.getTaskCount().equals("0")){
			viewHolder.add.setImageResource(R.drawable.add_a);
		}else{
			viewHolder.add.setImageResource(R.drawable.add_d);
		}
		viewHolder.add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onTaskStateChangedListener.onTaskStateChanged(position1);
				
			}
		});
	
		
		return view;
	}

	final static class ViewHolder {
		TextView name; // task名字
		ImageView image; // task图标
		ImageView add;// 添加按钮
		TextView description; // 描述
	}

	private OnTaskStateChangedListener onTaskStateChangedListener;

	public void setOnTaskStateChangedListener(
			OnTaskStateChangedListener onTaskStateChangedListener) {
		this.onTaskStateChangedListener = onTaskStateChangedListener;
	}

	public interface OnTaskStateChangedListener {
		public void onTaskStateChanged(int position);
	}

}
