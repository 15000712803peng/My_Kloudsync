package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.mvp.view.IMainActivityView;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.ub.techexcel.tools.ServiceInterfaceTools;

/**
 * Created by tonyan on 2019/10/29.
 */

public class MainPresenter extends TQLPenSignalKloudPresenter<IMainActivityView> {
    ServiceInterfaceTools mRequsetTools;
    public MainPresenter() {
        mRequsetTools = ServiceInterfaceTools.getinstance();
    }

    public void requestUserPathInfo() {
//        mRequsetTools.
    }


    @Override
    public void getBleManager(PenCommAgent bleManager) {
        super.getBleManager(bleManager);
        if (getView() != null) {
            getView().getBleManager(bleManager);
        }
    }

    @Override
    public void onConnected() {
        if (getView() != null) {
            getView().onConnected();
        }
    }

    @Override
    public void onDisconnected() {
        if (getView() != null) {
            getView().onDisconnected();
        }
    }

    @Override
    public void onConnectFailed() {
        if (getView() != null) {
            getView().onConnectFailed();
        }
    }

    @Override
    public void onReceiveDot(Dot dot) {
        if (getView() != null) {
            getView().onReceiveDot(dot);
        }
    }

    @Override
    public void onReceiveOfflineStrokes(Dot dot) {
        if (getView() != null) {
            getView().onReceiveOfflineStrokes(dot);
        }
    }

    @Override
    public void bleConnectTimeout() {

    }

}
