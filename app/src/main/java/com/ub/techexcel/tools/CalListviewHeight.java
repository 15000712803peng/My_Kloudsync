package com.ub.techexcel.tools;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CalListviewHeight {

	/**
	 * set the height of listview
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
			Log.i("haha", listItem.getMeasuredHeight() + "");
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);

	}

	public static void setListViewHeight(ListView listView, BaseAdapter adapter,
			int count) {
		int totalHeight = 0;
		for (int i = 0; i < count; i++) {
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * count);
		listView.setLayoutParams(params);
	}

	/**
	 * cal gridview height
	 * 
	 * @param listView
	 */
	public static void setGridViewHeightBasedOnChildren(GridView listView) {
		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int itemHeight = 0;

		int count = listAdapter.getCount();
		View listItem = null;

		if (count > 0) {
			listItem = listAdapter.getView(0, null, listView);
			listItem.measure(0, 0);
			itemHeight = listItem.getMeasuredHeight() + 10;

			if (count % 3 != 0) {
				count = count / 3 + 1;
			} else {
				count = count / 3;
			}

			for (int i = 0; i < count; i++) {
				totalHeight += itemHeight;
			}
			totalHeight = totalHeight + 10;
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight;
		listView.setLayoutParams(params);

	}

}
