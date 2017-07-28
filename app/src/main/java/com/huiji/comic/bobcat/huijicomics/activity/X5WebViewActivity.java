package com.huiji.comic.bobcat.huijicomics.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.utils.SpKey;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class X5WebViewActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wv_tencent)
    WebView wvTencent;
    @BindView(R.id.pb_web_view)
    ProgressBar pbWebView;

    private String comicTitle;
    private String comicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_x5_web_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        comicUrl = getIntent().getStringExtra(SpKey.COMIC_VIEW_URL);
        comicTitle = getIntent().getStringExtra(SpKey.COMIC_TITLE);
        setTitle(comicTitle);
        toolbar.setVisibility(View.GONE);

        wvTencent.loadUrl(comicUrl);
        WebSettings webSettings = wvTencent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvTencent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wvTencent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    pbWebView.setVisibility(View.GONE);
                    //progressBar.setProgress(newProgress);
                } else {
                    pbWebView.setVisibility(View.VISIBLE);
                    pbWebView.setProgress(newProgress);//设置加载进度
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
