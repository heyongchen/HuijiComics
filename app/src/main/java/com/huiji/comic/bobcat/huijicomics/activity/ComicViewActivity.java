package com.huiji.comic.bobcat.huijicomics.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicViewAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.utils.C;
import com.huiji.comic.bobcat.huijicomics.utils.IntentKey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicViewActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_comic_view)
    RecyclerView rvComicView;

    private String comicId;
    private String menuTitle;
    private String comicTitle;
    private String comicUrl;
    private List<String> comicPicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_comic_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        comicId = getIntent().getStringExtra(IntentKey.COMIC_ID);
        comicUrl = getIntent().getStringExtra(IntentKey.COMIC_VIEW_URL);
        comicTitle = getIntent().getStringExtra(IntentKey.COMIC_TITLE);
        menuTitle = getIntent().getStringExtra(IntentKey.COMIC_MENU_TITLE);
        rvComicView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 100; i++) {
                    String page = "";
                    if (i < 10) {
                        page = "00" + i;
                    } else if (i >= 10 && i < 100) {
                        page = "0" + i;
                    } else {
                        page = "" + i;
                    }
//                    if (UrlUtils.checkURL(C.getComicPicUrl(comicUrl, comicId, comicTitle, menuTitle, page))) {
                    comicPicList.add(C.getComicPicUrl(comicUrl, comicId, comicTitle, menuTitle, page));
//                    } else {
//                    break;
//                    }
                }
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }).start();

        rvComicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbar.getVisibility() == View.GONE) {
                    toolbar.setVisibility(View.VISIBLE);
                } else if (toolbar.getVisibility() == View.VISIBLE) {
                    toolbar.setVisibility(View.GONE);
                }
            }
        });

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //更新UI
            switch (msg.what) {
                case 1:
                    rvComicView.setAdapter(new ComicViewAdapter(ComicViewActivity.this, comicUrl, comicId, comicTitle, menuTitle, comicPicList));
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

}
