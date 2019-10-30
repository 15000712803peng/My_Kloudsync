package com.kloudsync.techexcel.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.kloudsync.techexcel.mvp.presenter.KloudPresenter;
import com.kloudsync.techexcel.mvp.view.KloudView;

/**
 * Created by tonyan on 2019/10/29.
 */

public  abstract class BaseActivity<P extends KloudPresenter> extends FragmentActivity implements KloudView{

    protected P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initPresenter();
        if(mPresenter != null){
            mPresenter.attachView(this);
        }
        initData();
        initView();

    }

    protected abstract void initData();

    protected abstract void initPresenter();

    protected abstract void initView();

    protected abstract int getLayout();

    protected P getPresenter(){
        return mPresenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null){
            mPresenter.detachView();
            mPresenter = null;
        }

    }
}
