package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.mvp.view.IMainActivityView;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.ub.techexcel.bean.NewBookPagesBean;
import com.ub.techexcel.bean.SyncNoteBean;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tonyan on 2019/10/29.
 */

public class MainPresenter extends TQLPenSignalKloudPresenter<IMainActivityView> {
    ServiceInterfaceTools requsetTools;
    public MainPresenter() {
        requsetTools = ServiceInterfaceTools.getinstance();
    }

    public void requestUserPathInfo() {
//        requsetTools.
    }

    public void requestNewBookPages(NewBookPagesBean newBookPagesBean) {
        Observable.just(newBookPagesBean).observeOn(Schedulers.io()).doOnNext(new Consumer<NewBookPagesBean>() {
            @Override
            public void accept(NewBookPagesBean newBookPagesBean) throws Exception {
                final String url = AppConfig.URL_PUBLIC + "newBookPages";
                ServiceInterfaceTools.getinstance().requestNewBookPages(newBookPagesBean.getPeertimeToken(), newBookPagesBean.getBookPages());

            }
        }).subscribe();
    }

    public void uploadDrawing(SyncNoteBean syncNoteBean) {
        Observable.just(syncNoteBean).observeOn(Schedulers.io()).doOnNext(new Consumer<SyncNoteBean>() {
            @Override
            public void accept(SyncNoteBean noteBean) throws Exception {
//                final String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + recordId + "&startTime=" + startTime + "&endTime=" + endTime;
//                requsetTools.uploadDrawing(noteBean);
            }
        }).subscribe();
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
