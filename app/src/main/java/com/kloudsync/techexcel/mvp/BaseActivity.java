package com.kloudsync.techexcel.mvp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.dialog.LoadingDialog;
import com.kloudsync.techexcel.mvp.presenter.KloudPresenter;
import com.kloudsync.techexcel.mvp.view.KloudView;
import com.kloudsync.techexcel.tool.ToastUtils;

import butterknife.ButterKnife;

/**
 * Created by tonyan on 2019/10/29.
 */

public abstract class BaseActivity<P extends KloudPresenter> extends FragmentActivity implements KloudView, View.OnClickListener {

    protected P mPresenter;
    protected LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setCustomDensity(this, 0);
        setContentView(getLayout());
        ButterKnife.bind(this);
        createLoadingDialog();
        initPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initView();
        initListener();
        initData();

    }

    private void createLoadingDialog() {
        mLoadingDialog = new LoadingDialog.Builder(this).build();
    }

    protected abstract int getLayout();

    protected abstract void initPresenter();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    protected P getPresenter() {
        return mPresenter;
    }

    public void showToast(int resId) {
        ToastUtils.show(this, resId);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void toast(String msg) {
        ToastUtils.show(this, msg);
    }

    @Override
    public void showLoading() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing() && !this.isFinishing()) {
            mLoadingDialog.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing() && !this.isFinishing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
        System.gc();

    }
}
