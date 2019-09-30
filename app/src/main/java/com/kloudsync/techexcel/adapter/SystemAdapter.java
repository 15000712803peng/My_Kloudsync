package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.SystemShow;

import java.util.List;

public class SystemAdapter extends CommonAdapter<SystemShow>{
	
	private List<SystemShow> list = null;
	private Context mContext;
    public ImageLoader imageLoader;
    private static LayoutInflater inflater=null;  

	public SystemAdapter(Context mContext, List<SystemShow> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        imageLoader=new ImageLoader(mContext.getApplicationContext());  
	}
	
	public void updateListView(List<SystemShow> list) {
		this.list = list;
		updateAdapter(list);
	}
	

	@SuppressLint("NewApi") @Override
	public void convert(ViewHolder holder, SystemShow ss, int position) {
		holder.setText(R.id.tv_IssueTitle, ss.getTitle())
				.setText(R.id.tv_Description, ss.getTime())
				.setViewVisible(R.id.img_show,
						!ss.getPhotoUrl().equals("0") ? View.VISIBLE : View.GONE);
		
		if(!ss.getPhotoUrl().equals("0")){
			ImageView img = holder.getView(R.id.img_show);
			imageLoader.DisplayImage2(AppConfig.URL_IMAGE + ss.getPhotoUrl() , img);
		}

	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.knowledge_item;
	}

}
