package com.ub.service.activity;

import android.content.Context;
import android.util.Log;


import com.ub.techexcel.bean.LineItem;

import org.xwalk.core.XWalkView;



public class NoteWebviewBridge {


    private XWalkView noteXwalkview;
    private  Context context;
    private LineItem notebook;


    public NoteWebviewBridge(XWalkView noteXwalkview, Context context,LineItem notebook) {
        this.noteXwalkview=noteXwalkview;
        this.context=context;
        this.notebook=notebook;
    }



    @org.xwalk.core.JavascriptInterface
    public void afterLoadPageFunction() {
        Log.e("当前文档信息", "url  " +"afterLoadPageFunction");



    }

    @org.xwalk.core.JavascriptInterface
    public void userSettingChangeFunction(final String opt) {

    }


    @org.xwalk.core.JavascriptInterface
    public void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {

    }

    /**
     * 获取每一页上的 Action
     */
    @org.xwalk.core.JavascriptInterface
    public void afterChangePageFunction(final String pageNum, int type) {
        Log.e("webview-afterChangePage", pageNum + "  " + type);

    }


    /**
     * pdf 加载完成
     */
    @org.xwalk.core.JavascriptInterface
    public void afterLoadFileFunction() {


    }


    /**
     * 翻页或切换文档
     *
     * @param diff
     */
    @org.xwalk.core.JavascriptInterface
    public void autoChangeFileFunction(int diff) {


    }
}
