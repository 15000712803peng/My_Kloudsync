package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.tool.PinyinComparator;

import java.util.Collections;
import java.util.List;

public class GroupAdapter extends CommonAdapter<Customer>{
	
	private List<Customer> list = null;
	private Context mContext;
//    public ImageLoader imageLoader;

	public GroupAdapter(Context mContext, List<Customer> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
//        imageLoader=new ImageLoader(mContext.getApplicationContext());
		SortCustomers();
	}
	
	public void updateListView(List<Customer> list) {
		this.list = list;
		//SortCustomers();
		updateAdapter(list);
	}
	boolean flag;
	
	public void SetSelected(boolean flag){
		this.flag = flag;
	}

	@SuppressLint("NewApi") @Override
	public void convert(ViewHolder holder, Customer customer, int position) {
		holder.setViewAlpha(R.id.lin_item, customer.isHasSelected() ? 0.5f : 1.0f)
				.setText(R.id.tv_name, customer.getName())
				.setText(R.id.tv_sort, customer.getSortLetters())
				.setImageDrawable(
						R.id.img_selected,
						mContext.getResources().getDrawable(
								(customer.isSelected() || customer
										.isHasSelected()) ? R.drawable.check2
										: R.drawable.unchecked2))
				.setViewVisible(R.id.tv_selected,
						customer.isHasSelected() ? View.VISIBLE : View.GONE);

		final SimpleDraweeView img = holder.getView(R.id.img_head);
		String url = customer.getUrl();
		Uri imageUri = Uri.parse(url);
//		if(!flag)
			img.setImageURI(imageUri);
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
		return R.layout.group_item;
	}
	
	private void SortCustomers() {
		Collections.sort(list, new PinyinComparator());
		
	}

}
