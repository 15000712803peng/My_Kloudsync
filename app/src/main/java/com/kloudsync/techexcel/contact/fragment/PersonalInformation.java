package com.kloudsync.techexcel.contact.fragment;

import java.util.ArrayList;
import java.util.List;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.PersonalInfoAdapter;
import com.kloudsync.techexcel.info.PersonalInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PersonalInformation extends Fragment {
	
	private View view;
	private ListView lv_info;
	private PersonalInfoAdapter madapter;
	
	private String labels[] = {"身高","体重","婚姻","上次月经时间","平均月经周期","速度","力量"};
	private String values[] = {"163cm","55kg","未婚","2015-10-15","30","65m/s","卧推800kg"};
	private List<PersonalInfo> mlist = new ArrayList<PersonalInfo>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (null != view) {  
            ViewGroup parent = (ViewGroup) view.getParent();  
            if (null != parent) {  
                parent.removeView(view);  
            }  
        } else {  
    		view = inflater.inflate(R.layout.user_personal, container, false);

    		initView();
        } 

		return view;
	}

	private void initView() {
		lv_info = (ListView) view.findViewById(R.id.lv_info);
		
		getData();
	}

	private void getData() {
		mlist = new ArrayList<PersonalInfo>();
		for (int i = 0; i < labels.length; i++) {
			PersonalInfo pi = new PersonalInfo(labels[i], values[i]);
			mlist.add(pi);
		}
		madapter = new PersonalInfoAdapter(getActivity(), mlist);
		lv_info.setAdapter(madapter);
		
	}

}
