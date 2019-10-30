package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.mvp.view.KloudView;

import java.lang.ref.WeakReference;

/**
 * Created by tonyan on 2019/10/29.
 */

public abstract class KloudPresenter<V extends KloudView> {

    private WeakReference<V> KloudView;

    public void attachView(V view) {
        KloudView = new WeakReference<>(view);
    }

    public void detachView() {
        if (null != KloudView) {
            KloudView.clear();
            KloudView = null;
        }

    }

    protected boolean isViewAttached() {
        return null != KloudView && null != KloudView.get();
    }

    protected V getView() {
        return isViewAttached() ? KloudView.get() : null;
    }
}
