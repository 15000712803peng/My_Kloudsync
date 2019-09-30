package com.kloudsync.techexcel.pc.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.pc.bean.PublishedArticleDetails;

public class PublishedArticleAdapter extends BaseAdapter {

	private List<PublishedArticleDetails> list=new ArrayList<PublishedArticleDetails>();
	private Context mContext;

	public PublishedArticleAdapter(Context mContext, List<PublishedArticleDetails> list) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		view = LayoutInflater.from(mContext).inflate(
				R.layout.pc_published_article_item, null);
		
		TextView pc_tv_ptitle = (TextView) view.findViewById(R.id.pc_tv_ptitle);
		
		String title = list.get(position).getIssueTitle();
		pc_tv_ptitle.setText(title);
		
		return view;
	}

}
