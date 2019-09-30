package com.kloudsync.techexcel.dialog.message;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SystemAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.ShowKnowledgeDetail;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.SystemShow;

@ProviderTag(messageContent = SystemMessage.class , showPortrait = false , centerInHorizontal = true)
public class SystemMessageItemProvider extends IContainerItemProvider.MessageProvider<SystemMessage> {

	private ArrayList<SystemShow> list = new ArrayList<SystemShow>();
	private ArrayList<SystemShow> list2 = new ArrayList<SystemShow>();
	SystemAdapter sAdapter;

    public ImageLoader imageLoader;
	float density;
    
    class ViewHolder {
    	ImageView img_top;
        TextView tv_title;
        FrameLayout fl_top;
        ListView lv_show;
    }


	@Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.ubao_hint, null);
        ViewHolder holder = new ViewHolder();
//        holder.message = (TextView) view.findViewById(android.R.id.tv);
        holder.img_top = (ImageView) view.findViewById(R.id.img_top);
        holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
        holder.fl_top = (FrameLayout) view.findViewById(R.id.fl_top);
        holder.lv_show = (ListView) view.findViewById(R.id.lv_show);
        view.setTag(holder);
        imageLoader=new ImageLoader(context.getApplicationContext()); 
        
        DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
        return view;
    }

	@Override
	public void bindView(View v, int position, SystemMessage content,
			UIMessage arg3) {
		ViewHolder holder = (ViewHolder) v.getTag();
		SystemShow sss = new SystemShow();
		
		list2 = content.getList();
		int select = -1;
		for (int i = 0; i < list2.size(); i++) {
			SystemShow s = list2.get(i);
			if(s.getType().equals("3")){
				sss = s;
				select = i;
			}
		}
		final SystemShow ss = sss;
		
		list = new ArrayList<SystemShow>();
		for (int i = 0; i < list2.size(); i++) {
			SystemShow s = list2.get(i);
			if(i != select){
				list.add(s);
			}
		}
		
		holder.tv_title.setText(ss.getTitle());
		imageLoader.DisplayImage2(AppConfig.URL_IMAGE + ss.getPhotoUrl() , holder.img_top);
		
		holder.fl_top.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(v.getContext(), ShowKnowledgeDetail.class);
				intent.putExtra("content", ss.getTime());
				intent.putExtra("title", ss.getTitle());
				intent.putExtra("knowledgeID", ss.getUrl());
				intent.putExtra("imageID", ss.getPhotoUrl());
				intent.putExtra("videoInfo", ss.getType());
				v.getContext().startActivity(intent);
			}
		});
		
		LayoutParams params = (LayoutParams) holder.lv_show.getLayoutParams();
		params.height = (int) (list.size() * 106 * density);
		sAdapter = new SystemAdapter(v.getContext(), list);
		holder.lv_show.setAdapter(sAdapter);
		holder.lv_show.setOnItemClickListener(new Myitem());
	}

	@Override
	public Spannable getContentSummary(SystemMessage arg0) {

        return new SpannableString(arg0.getFirstTitle());
	}

	@Override
	public void onItemClick(View arg0, int arg1, SystemMessage arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onItemLongClick(View arg0, int arg1, SystemMessage arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
		
	}

	private class Myitem implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SystemShow s = list.get(position);
			Intent intent = new Intent(view.getContext(), ShowKnowledgeDetail.class);
			intent.putExtra("content", s.getTime());
			intent.putExtra("title", s.getTitle());
			intent.putExtra("knowledgeID", s.getUrl());
			intent.putExtra("imageID", s.getPhotoUrl());
			intent.putExtra("videoInfo", s.getType());
			view.getContext().startActivity(intent);
			
			
		}

		
	}


}
