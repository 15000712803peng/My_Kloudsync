package com.ub.techexcel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.AddFriend;

public class MyAddFriendsAdapter extends BaseAdapter {

	private List<AddFriend> list = new ArrayList<AddFriend>();
	private Context mContext;

	public MyAddFriendsAdapter(Context mContext, List<AddFriend> list) {
		this.mContext = mContext;
		this.list = list;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		final ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.newfriends_item, null);
			viewHolder.name = (TextView) view.findViewById(R.id.name);
			viewHolder.tvLetter = (TextView) view
					.findViewById(R.id.item1_catalog);
			viewHolder.tel = (TextView) view.findViewById(R.id.tel);
			viewHolder.addfriendbnt = (TextView) view
					.findViewById(R.id.addfriendbnt);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		final AddFriend entity = list.get(position);
		viewHolder.name.setText(entity.getName());
		viewHolder.tel.setText(Html.fromHtml(entity.getPhone())); 
		viewHolder.addfriendbnt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onTouchingLetterChangedListener
						.onTouchingLetterChanged(position);
				
			}
		});
		return view;
	}

	final static class ViewHolder {
		TextView tvLetter;  //首字母
		TextView name;
		TextView tel;
		TextView addfriendbnt;
	}

	private OnHealthChangedListener onTouchingLetterChangedListener;

	public void setOnHealthChangedListener(
			OnHealthChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnHealthChangedListener {
		public void onTouchingLetterChanged(int position);
	}

}
