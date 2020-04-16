package com.ub.service.activity;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;


import com.ub.techexcel.bean.LineItem;

public class NoteWebviewBridge {


    private WebView noteXwalkview;
    private  Context context;
    private LineItem notebook;


    public NoteWebviewBridge(WebView noteXwalkview, Context context,LineItem notebook) {
        this.noteXwalkview=noteXwalkview;
        this.context=context;
        this.notebook=notebook;
    }



    @JavascriptInterface
    public void afterLoadPageFunction() {
        Log.e("当前文档信息", "url  " +"afterLoadPageFunction");



    }

    @JavascriptInterface
    public void userSettingChangeFunction(final String opt) {

    }


    @JavascriptInterface
    public void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {

    }

    /**
     * 获取每一页上的 Action
     */
    @JavascriptInterface
    public void afterChangePageFunction(final String pageNum, int type) {
        Log.e("webview-afterChangePage", pageNum + "  " + type);

    }


    /**
     * pdf 加载完成
     */
    @JavascriptInterface
    public void afterLoadFileFunction() {


    }


    /**
     * 翻页或切换文档
     *
     * @param diff
     */
    @JavascriptInterface
    public void autoChangeFileFunction(int diff) {


    }
}
