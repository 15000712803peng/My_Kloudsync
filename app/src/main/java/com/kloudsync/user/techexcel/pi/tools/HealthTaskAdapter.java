package com.kloudsync.user.techexcel.pi.tools;

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
import com.kloudsync.techexcel.httpgetimage.ImageLoader;

public class HealthTaskAdapter extends BaseAdapter {
	private List<TaskBean> list = new ArrayList<TaskBean>();
	private Context mContext;
	private ImageLoader imageLoader;
	private boolean lalala = true;

	public HealthTaskAdapter(Context mContext, List<TaskBean> list) {
		this.mContext = mContext;
		this.list = list;
		imageLoader = new ImageLoader(mContext.getApplicationContext());
	}

	public void shifoudiaoyongdianjishijian(boolean lalala) {
		this.lalala = lalala;
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
					R.layout.pi_health_meiritask, null);
			viewHolder.name = (TextView) view
					.findViewById(R.id.pi_health_task_name); // 名字
			viewHolder.time = (TextView) view.findViewById(R.id.health_time);// 时间
			viewHolder.image = (ImageView) view
					.findViewById(R.id.pi_health_task_image);
			viewHolder.finish = (ImageView) view
					.findViewById(R.id.pi_health_task_finish);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		final int position1 = position;
		final TaskBean entity = list.get(position1);
		viewHolder.name.setText(entity.getName());
		viewHolder.time.setText(entity.getTriggers());
//		if (lalala) {
//			viewHolder.name.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					onGoToTaskDetailListener
//							.onGoToTaskDetailListener(position1);
//				}
//			});
//			viewHolder.time.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					onGoToTaskDetailListener
//							.onGoToTaskDetailListener(position1);
//				}
//			});
//		}
		if (entity.getTaskIfDoneToday().equals("0")) {
			viewHolder.finish.setImageResource(R.drawable.finish_d);

//			viewHolder.finish.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					onTaskStateChangedListener.onTaskStateChanged(position1);
//				}
//			});

		} else {
			viewHolder.finish.setImageResource(R.drawable.finish_a);
		}
		if (!(null == entity.getIconURL() || entity.getIconURL().length() < 1)) {
			imageLoader.DisplayImage3(entity.getIconURL(), viewHolder.image);
		}
		return view;
	}

	final static class ViewHolder {
		TextView time; // 是否添加
		TextView name; // task名字
		ImageView image; // task图标
		ImageView finish;// 完成按钮
	}
//
//	private OnTaskStateChangedListener onTaskStateChangedListener;
//
//	public void setOnTaskStateChangedListener(
//			OnTaskStateChangedListener onTaskStateChangedListener) {
//		this.onTaskStateChangedListener = onTaskStateChangedListener;
//	}
//
//	public interface OnTaskStateChangedListener {
//		public void onTaskStateChanged(int position);
//	}
//
//	private OnGoToTaskDetailListener onGoToTaskDetailListener;
//
//	public void setOnGoToTaskDetailListener(
//			OnGoToTaskDetailListener onGoToTaskDetailListener) {
//		this.onGoToTaskDetailListener = onGoToTaskDetailListener;
//	}
//
//	public interface OnGoToTaskDetailListener {
//		public void onGoToTaskDetailListener(int position);
//	}

}
