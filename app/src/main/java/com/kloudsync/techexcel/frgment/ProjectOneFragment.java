package com.kloudsync.techexcel.frgment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class ProjectOneFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tv_title;
    private RelativeLayout layout_back;
    private WebView webView;
    private String url = "http://jiaxing.techexcel.com.cn/p1/#/p1ForKSApp?token=43106500-5f16-48a8-9dc9-f42ca3344f97";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.projectone_fragment, container, false);
            initView();
        }
        initFunction();
        return view;
    }

    private int backType = 0;

    private void initView() {
        webView = view.findViewById(R.id.webview);
        layout_back = view.findViewById(R.id.layout_back);
        tv_title = view.findViewById(R.id.tv_title);
        layout_back.setOnClickListener(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "AnalyticsWebInterface");//name:android在网页里面可以用window.name.方法名调用java方法

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                if (url.contains("p1ForKSAppTaskList")) {  // 任务列表
                    tv_title.setText("任务");
                    backType = 1;
                } else if (url.contains("p1ForKSApp")) { //项目列表
                    tv_title.setText("项目列表");
                    backType = 0;
                }
                Log.e("WebViewClient", view.getTitle() + "     " + url + "  " + backType);
                super.onPageFinished(view, url);
            }


        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);

    }

    private void initFunction() {
        webView.loadUrl(url);
    }

    @JavascriptInterface
    public void userSettingChangeFunction(final String opt) {
        Observable.just(opt).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Toast.makeText(getActivity(), opt, Toast.LENGTH_LONG).show();
            }
        }).subscribe();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
//                webView.loadUrl("javascript:saveEditProject()"); // 保存修改project
//                webView.loadUrl("javascript:goToBaseFamily()"); // 打开项目列表
//                webView.loadUrl("javascript:backToProjectList()"); //从修改projectdetail 返回到project list
//                webView.loadUrl("javascript:backToTaskList()"); // 从task detail返回到task list

                if (backType == 0) {
                    webView.loadUrl("javascript:backToProjectList()"); //从修改projectdetail 返回到project list
                } else if (backType == 1) {
                    webView.loadUrl("javascript:backToTaskList()"); // 从task detail返回到task list
                }

                break;
        }

    }


}
