package com.kloudsync.techexcel.pc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolder {
	private SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	public ViewHolder(Context context, ViewGroup parent, int layoutId,
			int position) {
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		mConvertView.setTag(this);
	}

	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}

	/**
	 * 通过viewId获取控件
	 * @param viewId
	 * @return
	 */
	public<T extends View> T getView(int viewId){
		View view = mViews.get(viewId);
		
		if(view == null){
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		
		return (T) view;		
	}
	
	
	public View getConvertView() {
		return mConvertView;
	}
	
	/**
	 * 设置TextView的值
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId,String text){
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}
	
	/**
	 * 设置TextView的值可变色
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setTextfromHtml(int viewId,String text){
		TextView tv = getView(viewId);
		tv.setText(Html.fromHtml(text));
		return this;
	}
	
	/**
	 * 设置TextView的值的颜色
	 * @param viewId
	 * @param color
	 * @return
	 */
	public ViewHolder setTextColor(int viewId, int color) {
		TextView tv = getView(viewId);
		tv.setTextColor(color);
		return this;
	}
	
	/**
	 * 设置EditText的值
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setEditText(int viewId,String text){
		EditText et = getView(viewId);
		et.setText(text);
		return this;
	}

	/**
	 * 设置ImageViewView的值
	 * 通过ID
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int resId) {
		ImageView img = getView(viewId);
		img.setImageResource(resId);
		return this;
	}

	/**
	 * 设置ImageViewView的值
	 * 通过Bitmap
	 * @param viewId
	 * @param bitmap
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
		ImageView img = getView(viewId);
		img.setImageBitmap(bitmap);
		return this;
	}
	
	/**
	 * 设置ImageViewView的值
	 * 通过Drawable
	 * @param viewId
	 * @param drawable
	 * @return
	 */
	public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
		ImageView img = getView(viewId);
		img.setImageDrawable(drawable);
		return this;
	}
	
	/**
	 * 设置LinearLayout的背景颜色
	 * @param viewId
	 * @param resId
	 * @return
	 */
	public ViewHolder setLinearBackgroundColor(int viewId, int resId) {
		LinearLayout lin = getView(viewId);
		lin.setBackgroundColor(resId);
		return this;
	}


	/**
	 * 设置View是否可见
	 * @param viewId
	 * @param visible
	 * @return
	 */
	public ViewHolder setViewVisible(int viewId, int visible) {
		View view = getView(viewId);
		view.setVisibility(visible);				
		return this;
	}

}
