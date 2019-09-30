package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.Customer;

import java.util.List;

public class ChatDetailAdapter extends CommonAdapter<Customer> {
	
	private List<Customer> list = null;
	private Context mContext;

	public ChatDetailAdapter(Context mContext, List<Customer> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
	}
	
	public void updateListView(List<Customer> list) {
		this.list = list;
		updateAdapter(list);
	}

	@SuppressLint("NewApi") @Override
	public void convert(ViewHolder holder, Customer customer, int position) {

		final SimpleDraweeView img = holder.getView(R.id.img_head);
		if (position < (list.size() - 1)) {
			holder.setText(R.id.tv_name, customer.getName());
			/*CircleImageView img = holder.getView(R.id.img_head);
			String url = customer.getUrl();
			if (null == url || url.length() < 1) {
				img.setImageResource(R.drawable.hello);			
			}else{
				imageLoader.DisplayImage(url, img);
			}*/
			String url = customer.getUrl();
			Uri imageUri = Uri.parse(url);
			img.setImageURI(imageUri);
		}else {
			img.setImageResource(R.drawable.selected_addcontact);
		}
		
		

	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.chatdetail_item;
	}
	

}
