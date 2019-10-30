package com.kloudsync.techexcel.mvp.view;

import android.content.Context;

/**
 * Created by tonyan on 2019/10/29.
 */

public interface IMainActivityView extends KloudView{

     void initBottomTabs();

     void showWxAddDocumentDialog(String wxPath);
}
