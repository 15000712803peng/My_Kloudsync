package com.kloudsync.techexcel.mvp.presenter;

import com.ub.techexcel.tools.ServiceInterfaceTools;

/**
 * Created by tonyan on 2019/10/29.
 */

public class MainPresenter<IMainActivityView> extends KloudPresenter {
    ServiceInterfaceTools requsetTools;
    public MainPresenter() {
        requsetTools = ServiceInterfaceTools.getinstance();
    }

    public void requestUserPathInfo() {
//        requsetTools.
    }
}
