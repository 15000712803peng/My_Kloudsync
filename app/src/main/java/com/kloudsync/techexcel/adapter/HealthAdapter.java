package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.tool.PinyinComparator;
import com.kloudsync.techexcel.view.CircleImageView;

import java.util.Collections;
import java.util.List;

public class HealthAdapter extends CommonAdapter<Customer>{
	
	private List<Customer> list = null;
	private Context mContext;
//    public ImageLoader imageLoader;

	public HealthAdapter(Context mContext, List<Customer> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
//        imageLoader=new ImageLoader(mContext.getApplicationContext());  
		SortCustomers();
	}
	
	public void updateListView(List<Customer> list) {
		this.list = list;
		updateAdapter(list);
		SortCustomers();
	}

	@SuppressLint("NewApi") @Override
	public void convert(ViewHolder holder, Customer customer, int position) {

		holder.setText(R.id.tv_name, customer.getName())
				.setText(R.id.tv_gender,
						customer.getSex().equals("2") ? "女" : "男")
				.setText(R.id.tv_age, customer.getAge() + "岁")
				.setText(R.id.tv_serviceTimes, customer.getTitle())
				.setText(R.id.tv_distance, customer.getDistance())				
				.setText(R.id.tv_sort, customer.getSortLetters())
				.setText(R.id.tv_description, customer.getSummary());
		
		if(customer.getSex().equals("0")){
			holder.setText(R.id.tv_gender,"");
		}
		
		ImageView imgtpye = holder.getView(R.id.img_type);
		int type = customer.getType();
		/*if (0 == type) {
			imgtpye.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.vip));
		} else if (1 == type) {
			imgtpye.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.ubaoman));
		} else {
			imgtpye.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.userfriend));
		}*/
		/*Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.health1);
		RoundImageDrawable Rid = new RoundImageDrawable(bmp);
		holder.setImageDrawable(R.id.img_head, Rid).setImageDrawable(
				R.id.img_head, Rid);*/
		final CircleImageView img = holder.getView(R.id.img_head);
		String url = customer.getUrl();
		if (null == url || url.length() < 1) {
			img.setImageResource(R.drawable.hello);			
		}else{
			final String imgurl = customer.getUrl();
			/*new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					imageLoader.DisplayImage(imgurl, img);
				}
			}, 500);*/
			RequestQueue requestQueue = Volley.newRequestQueue(mContext);
			final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(
					20);
			ImageCache imageCache = new ImageCache() {
				@Override
				public void putBitmap(String key, Bitmap value) {
					lruCache.put(key, value);
				}

				@Override
				public Bitmap getBitmap(String key) {
					return lruCache.get(key);
				}
			};
			ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
			ImageListener listener = ImageLoader.getImageListener(img,
					R.drawable.hello, R.drawable.hello);
			
			imageLoader.get(imgurl, listener);
			/*imageLoader.get(imgurl, new ImageListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onResponse(ImageContainer response, boolean isImmediate) {
					// TODO Auto-generated method stub
					if (response.getBitmap() != null) {
	                    img.setImageBitmap(response.getBitmap());
	                } else  {
						img.setImageBitmap(response.getBitmap());
	                }
					
				}
			});*/
		}

		LinearLayout myLayout = holder.getView(R.id.lin_problem); 
		myLayout.removeAllViews();
		int size = 0;
		if(customer.getFocusPoints() != null){
			size = (customer.getFocusPoints().size() > 3 ? 3 : customer
				.getFocusPoints().size());
		}
		for (int i = 0; i < size; i++) {
			String problem = customer.getFocusPoints().get(i);
			TextView tv_problem = new TextView(mContext);
			tv_problem.setText(problem + "");
			tv_problem.setBackground(mContext.getResources().getDrawable(R.drawable.contact_tv_problem));
			tv_problem.setPadding(10, 0, 10, 0);
			tv_problem.setTextColor(mContext.getResources().getColor(R.color.white));
//			tv_problem.setBackgroundColor(mContext.getResources().getColor(R.color.green));
			tv_problem.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 10, 0);
			                      
			tv_problem.setLayoutParams(lp);  
			myLayout.addView ( tv_problem ) ;
		}
		
		int sectionVisible = SideBarSortHelp.getPositionForSection(list,
				customer.getSortLetters().charAt(0));
		if(sectionVisible == position){
			holder.setViewVisible(R.id.tv_sort, View.VISIBLE);
		}else{
			holder.setViewVisible(R.id.tv_sort, View.GONE);
		}
		

	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.health_item;
	}
	
	private void SortCustomers() {
		Collections.sort(list, new PinyinComparator());
		
	}

}
