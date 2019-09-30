package com.ub.techexcel.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;

public class SendServiceAdapter extends BaseAdapter {
	private Context context;
	private List<ServiceBean> serviceList;
	public ImageLoader imageLoader;

	public SendServiceAdapter(Context context, List<ServiceBean> serviceList) {
		this.context = context;
		this.serviceList = serviceList;
		imageLoader = new ImageLoader(context.getApplicationContext());

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return serviceList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return serviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder2 holder = null;
		if (convertView == null) {
			holder = new ViewHolder2();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.sendservice_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			holder.lineItems = (LinearLayout) convertView
					.findViewById(R.id.lineitems);
			holder.concern = (TextView) convertView
					.findViewById(R.id.concerValue);

			holder.entercustomer = (LinearLayout) convertView
					.findViewById(R.id.entercustomer);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.img_selected = (ImageView) convertView
					.findViewById(R.id.img_selected);


			convertView.setTag(holder);
		} else {
			holder = (ViewHolder2) convertView.getTag();
		}
		ServiceBean bean = serviceList.get(position);

		if (bean.getStatusID() == 1) { // 已結束
			holder.status.setText("已结束");
		} else {
			holder.status.setText("进行中");
		}

		if (bean.getConcernID() > 0) {
			holder.concern.setVisibility(View.VISIBLE);
		} else {
			holder.concern.setVisibility(View.INVISIBLE);
		}

		holder.name.setText(bean.getCustomer().getName());
		holder.concern.setText(bean.getConcernName());
		String url = bean.getCustomer().getUrl();
		if (null == url || url.length() < 1) {
			holder.image.setImageResource(R.drawable.hello);
		} else {
			imageLoader.DisplayImage(url, holder.image);
		}
		
		if (bean.isSelect()) {
			holder.img_selected.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.select_b));
		} else {
			holder.img_selected.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.unselect));
		}

		initPlanDescription(holder.lineItems, bean);

		return convertView;
	}

	private void initPlanDescription(LinearLayout lineItems, ServiceBean bean) {
		lineItems.removeAllViews();
		LinearLayout convertView = null;
		TextView tv1;

		for (int i = 0; i < bean.getLineItems().size(); i++) {
			LineItem item = new LineItem();
			item = bean.getLineItems().get(i);
			convertView = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.service_item_child, null);
			tv1 = (TextView) convertView.findViewById(R.id.name);
			tv1.setText(item.getEventName());
			lineItems.addView(convertView);
		}
	}

}

class ViewHolder2 {
	TextView name, status, concern;
	LinearLayout lineItems, entercustomer;
	ImageView image, img_selected;

}
