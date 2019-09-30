package com.kloudsync.techexcel.contact.fragment;

import com.kloudsync.techexcel.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MedicalReport extends Fragment {
	
	private View view;
	private FrameLayout fm_none;
	private TextView tv_upload;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (null != view) {  
            ViewGroup parent = (ViewGroup) view.getParent();  
            if (null != parent) {  
                parent.removeView(view);  
            }  
        } else {  
    		view = inflater.inflate(R.layout.user_medical_report, container, false);

    		initView();
        } 

		return view;
	}

	private void initView() {
		fm_none = (FrameLayout) view.findViewById(R.id.fm_none);
		tv_upload = (TextView) view.findViewById(R.id.tv_upload);
		
		tv_upload.setOnClickListener(new MyOnclick());
	}
	
	protected class MyOnclick implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_upload:
				Toast.makeText(getActivity(), "hahahahaha", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
			
		}
		
	}

}
