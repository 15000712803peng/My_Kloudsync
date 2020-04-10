package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.bean.AccountSettingBean;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.mvp.view.DocAndMeetingView;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;

public class DocAndMeetingPresenter extends KloudPresenter<DocAndMeetingView> {

	public void getAccountInfo() {
		String url = AppConfig.URL_MEETING_BASE + "company/account_info?companyId=" + AppConfig.SchoolID;
		MeetingServiceTools.getInstance().getAccountInfo(url, MeetingServiceTools.GETACCOUNTINFO, new ServiceInterfaceListener() {
			@Override
			public void getServiceReturnData(Object object) {
				AccountSettingBean accountSettingBean = (AccountSettingBean) object;
//				if ()
			}
		});
	}
}
