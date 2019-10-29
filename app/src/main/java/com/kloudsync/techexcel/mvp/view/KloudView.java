package com.kloudsync.techexcel.mvp.view;

import android.content.Context;

/**
 * Created by tonyan on 2019/10/29.
 */

public interface KloudView {

    void showLoading();
    
    void dismissLoading();

    void toast(String msg);

    Context getContext();
}
