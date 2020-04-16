package com.kloudsync.techexcel.personal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

public class AboutWebActivity extends AppCompatActivity {

    public static String TAG = "tag";
    public static String ENURL = "enurl";
    public static String ZHURL = "zhurl";
    private ImageView img_notice;
    private WebView about_webview;
    private SharedPreferences sharedPreferences;
    int language;
    private TextView mTitle;
    private String mEnUrl;
    private String mZhUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_web);
        initView();
    }

    private void initView() {
        String tag = getIntent().getStringExtra(TAG);
        mEnUrl = getIntent().getStringExtra(ENURL);
        mZhUrl = getIntent().getStringExtra(ZHURL);
        img_notice = (ImageView) findViewById(R.id.img_notice);
        mTitle = findViewById(R.id.tv_title);
        about_webview = (WebView) findViewById(R.id.about_webview);
        mTitle.setText(tag);

        //获取当前语言
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        language = sharedPreferences.getInt("language",2);


        img_notice.setOnClickListener(new MyOnClick());

        OpenWeb();
    }

    protected class MyOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_notice:
//                    finish();
                    ActivityCompat.finishAfterTransition(AboutWebActivity.this);
                    break;
                default:
                    break;
            }
        }
    }

    private void OpenWeb() {
        //   Uri uri;
        if(language == 1){
            //uri = Uri.parse("https://kloudsync.peertime.cn/privacy.html");
            about_webview.loadUrl (mEnUrl);
            //Uri uri = Uri.parse("http://http;//www.peertime.com/privacy-statement");

        }else {
            // uri = Uri.parse("https://kloudsync.peertime.cn/privacy-cn.html");
            about_webview.loadUrl (mZhUrl);
        }

        // startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(AboutWebActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        about_webview.clearCache(true);
        System.gc();
    }
}
