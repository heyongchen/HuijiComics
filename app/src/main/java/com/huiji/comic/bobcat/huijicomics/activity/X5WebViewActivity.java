package com.huiji.comic.bobcat.huijicomics.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.utils.IntentKey;
import com.huiji.comic.bobcat.huijicomics.utils.UrlUtils;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;

public class X5WebViewActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wv_tencent)
    WebView wvComic;
    @BindView(R.id.pb_web_view)
    ProgressBar pbWebView;

    private String comicTitle;
    private String comicUrl;
    private String comicId;
    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // 隐藏标题栏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            // 禁用系统截屏功能
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_x5_web_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        comicUrl = getIntent().getStringExtra(IntentKey.COMIC_VIEW_URL);
        comicTitle = getIntent().getStringExtra(IntentKey.COMIC_TITLE);
        comicId = getIntent().getStringExtra(IntentKey.COMIC_ID);
        setTitle(comicTitle);
        toolbar.setVisibility(View.GONE);

        wvComic.loadUrl(comicUrl);
        WebSettings webSettings = wvComic.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        wvComic.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wvComic.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    pbWebView.setVisibility(View.GONE);
                    pbWebView.setProgress(newProgress);
                } else {
                    pbWebView.setVisibility(View.VISIBLE);
                    pbWebView.setProgress(newProgress);//设置加载进度
                }
            }
        });
    }

    private void webViewDestroy() {
        if (wvComic != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = wvComic.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(wvComic);
            }
            wvComic.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            wvComic.getSettings().setJavaScriptEnabled(false);
            wvComic.clearHistory();
            wvComic.clearView();
            wvComic.removeAllViews();
            try {
                wvComic.destroy();
            } catch (Throwable ignored) {

            }
        }
    }

    private void addHistory(String comicUrl) {
        WhereBuilder b = WhereBuilder.b();
        b.and("comicId", "=", comicId);//条件
        KeyValue history = new KeyValue("lastReadUrl", UrlUtils.replaceHost(comicUrl));
        KeyValue time = new KeyValue("lastReadTime", System.currentTimeMillis());
        try {
            dbManager.update(ComicListDbInfo.class, b, history);
            dbManager.update(ComicListDbInfo.class, b, time);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (wvComic != null && wvComic.getUrl() != null && !wvComic.getUrl().isEmpty()) {
            addHistory(UrlUtils.replaceHost(wvComic.getUrl()));
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        webViewDestroy();
        super.onDestroy();

    }

}
