package com.kloudsync.techexcel.mvp.presenter;

import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.mvp.view.IMainActivityView;
import com.kloudsync.techexcel.tool.SharedPreferencesUtils;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.ub.techexcel.bean.NewBookPagesBean;
import com.ub.techexcel.bean.NoteInfoBean;
import com.ub.techexcel.bean.SyncNoteBean;
import com.ub.techexcel.bean.UploadNoteBean;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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

    public void requestNewBookPages(final NewBookPagesBean newBookPagesBean) {
        Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, NoteInfoBean>() {
            @Override
            public NoteInfoBean apply(String s) throws Exception {
                String path = AppConfig.URL_LIVEDOC + "newBookPages";
                return mRequsetTools.requestNewBookPages(path, newBookPagesBean.getPeertimeToken(), newBookPagesBean.getBookPages());
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<NoteInfoBean>() {
            @Override
            public void accept(NoteInfoBean noteinfobean) throws Exception {
                if (noteinfobean != null) {
                    if (noteinfobean.isSuccess()) {
                        List<NoteInfoBean.DataBean> dataBeanList = noteinfobean.getData();
	                    List<NoteInfoBean.DataBean> list = SharedPreferencesUtils.getList(AppConfig.NEWBOOKPAGES, AppConfig.NEWBOOKPAGES, new TypeToken<List<NoteInfoBean.DataBean>>() {
	                    });
                        for (NoteInfoBean.DataBean bean : dataBeanList) {
	                        list.add(bean);
                        }
	                    SharedPreferencesUtils.putList(AppConfig.NEWBOOKPAGES, AppConfig.NEWBOOKPAGES, list);
                    } else {
                        if (getView() != null) {
                            NoteInfoBean.ErrorBean error = noteinfobean.getError();
                            String errorMessage = error.getErrorMessage();
                            getView().toast(errorMessage);
                        }
                    }
                }

            }
        }).subscribe();
    }

	public void uploadDrawing(final SyncNoteBean syncNoteBean) {
		Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, UploadNoteBean>() {
			@Override
			public UploadNoteBean apply(String s) throws Exception {
				String path = AppConfig.URL_LIVEDOC + "uploadDrawing";
				return mRequsetTools.uploadDrawing(path, syncNoteBean.getPeertimeToken(), syncNoteBean.getBookPages(), syncNoteBean.getDrawingData());
			}
		}).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<UploadNoteBean>() {
            @Override
            public void accept(UploadNoteBean bean) throws Exception {
	            if (bean != null) {
		            if (bean.isSuccess()) {

		            } else {
			            if (getView() != null) {
				            UploadNoteBean.ErrorBean error = bean.getError();
				            String errorMessage = error.getErrorMessage();
				            getView().toast(errorMessage);
			            }
		            }
	            }
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
