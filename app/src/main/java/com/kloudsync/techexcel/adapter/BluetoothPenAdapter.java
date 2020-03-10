package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EverPen;

import java.util.List;

public class BluetoothPenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context mContext;
	private List<EverPen> mList;

	public BluetoothPenAdapter(Context context, List<EverPen> list) {
		mContext = context;
		mList = list;

	}


	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_pen, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
		final ViewHolder viewHolder = (ViewHolder) holder;
		final EverPen everPen = mList.get(position);
		viewHolder.mBluetoothPenName.setText(everPen.getPenName());
		if (everPen.isConnected()) {
			viewHolder.mIvBluetoothPenConnected.setVisibility(View.VISIBLE);
		} else {
			viewHolder.mIvBluetoothPenConnected.setVisibility(View.GONE);
		}

		if (everPen.isClick()) {
			viewHolder.mPbBluetoothPen.setVisibility(View.VISIBLE);
		} else {
			viewHolder.mPbBluetoothPen.setVisibility(View.GONE);
		}
		viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onItemClick(viewHolder.mPbBluetoothPen, position, everPen);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
	}

	private static class ViewHolder extends RecyclerView.ViewHolder {

		private final TextView mBluetoothPenName;
		private final ProgressBar mPbBluetoothPen;
		private final ImageView mIvBluetoothPenConnected;

		public ViewHolder(View view) {
			super(view);
			mBluetoothPenName = view.findViewById(R.id.item_tv_bluetooth_pen_name);
			mPbBluetoothPen = view.findViewById(R.id.item_pb_bluetooth_pen);
			mIvBluetoothPenConnected = view.findViewById(R.id.iv_item_bluetooth_pen_connected);
		}
	}

	private OnItemClickListener mListener;

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}

	public interface OnItemClickListener {
		void onItemClick(ProgressBar progressBar, int position, EverPen everPen);
	}
}
