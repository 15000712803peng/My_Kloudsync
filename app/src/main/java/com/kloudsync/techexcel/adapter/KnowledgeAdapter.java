package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Knowledge;

import java.util.List;

public class KnowledgeAdapter extends CommonAdapter<Knowledge>{
	
	private List<Knowledge> list = null;
	private Context mContext;
    public ImageLoader imageLoader;

	public KnowledgeAdapter(Context mContext, List<Knowledge> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
        imageLoader=new ImageLoader(mContext.getApplicationContext());  
	}
	
	public void updateListView(List<Knowledge> list) {
		this.list = list;
		updateAdapter(list);
	}
	

	@SuppressLint("NewApi") @Override
	public void convert(ViewHolder holder, Knowledge kw, int position) {
		holder.setText(R.id.tv_IssueTitle, kw.getIssueTitle())
				.setText(R.id.tv_Description, kw.getDescription())
				.setViewVisible(R.id.img_show,
						kw.getImageID() > 0 ? View.VISIBLE : View.GONE);
		
		if (kw.getImageID() > 0) {
			ImageView img = holder.getView(R.id.img_show);
			imageLoader.DisplayImage(AppConfig.URL_IMAGE + kw.getImageID(), img);
		}

	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.knowledge_item;
	}

}
