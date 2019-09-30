package com.kloudsync.techexcel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.changecode.CheckCountryHelp;
import com.kloudsync.techexcel.changecode.CountryCodeList;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.CountryCodeInfo;

import java.util.HashMap;
import java.util.List;


public class ChangeCountryCodeAdapter extends CommonAdapter<CountryCodeInfo> {

	private List<CountryCodeInfo> list = null;
	private Context mContext;
	private HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
	private CountryCodeList countryCodeList = new CountryCodeList();

	public ChangeCountryCodeAdapter(Context mContext, List<CountryCodeInfo> list) {
		super(mContext, list);
		this.mContext = mContext;
		this.list = list;
		hashMap = countryCodeList.hashMap;
	}

	public void updateListView(List<CountryCodeInfo> list) {
		this.list = list;
		updateAdapter(list);
	}

	boolean flag;

	public void SetSelected(boolean flag) {
		this.flag = flag;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void convert(ViewHolder holder, CountryCodeInfo ci, int position) {
		int sectionVisible = SideBarSortHelp.getPositionForSectionCC(list, ci
				.getSortLetters().charAt(0));
		int code = ci.getCode();
		holder.setTextfromHtml(R.id.tv_name, ci.getName())
						.setText(R.id.tv_sort, ci.getSortLetters())
						.setText(R.id.tv_code, "+" + ci.getCode())
				.setViewVisible(R.id.lin_sort, sectionVisible == position ? View.VISIBLE
						: View.GONE)
				.setViewVisible(R.id.tv_top, sectionVisible != position ? View.VISIBLE
						: View.GONE);
		if(flag){
			if (ci.getNcshow()) {
				holder.setTextfromHtml(R.id.tv_name, ci.getShowname());
			}else {
				holder.setTextfromHtml(R.id.tv_code, "+" + ci.getShowname());
			}
			
		}
		
		code = CheckCountryHelp.CheckCountryCode(code, ci.getName());
		Integer drawableid = hashMap.get(code);
		if (drawableid == null) {
//			holder.setImageResource(R.id.img_country, R.drawable.country86);
			holder.setImageBitmap(R.id.img_country, null);
		} else {
			holder.setImageResource(R.id.img_country, drawableid);
		}

	}

	@Override
	public int getLayout(int position) {
		// TODO Auto-generated method stub
		return R.layout.changecc_item;
	}

}
