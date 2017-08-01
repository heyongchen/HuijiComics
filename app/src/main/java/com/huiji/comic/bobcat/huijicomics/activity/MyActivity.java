package com.huiji.comic.bobcat.huijicomics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.iv_menu_collect)
    ImageView ivMenuCollect;
    @BindView(R.id.iv_menu_collect_more)
    ImageView ivMenuCollectMore;
    @BindView(R.id.rl_menu_collect)
    RelativeLayout rlMenuCollect;
    @BindView(R.id.iv_menu_history)
    ImageView ivMenuHistory;
    @BindView(R.id.iv_menu_history_more)
    ImageView ivMenuHistoryMore;
    @BindView(R.id.rl_menu_history)
    RelativeLayout rlMenuHistory;
    @BindView(R.id.iv_menu_about)
    ImageView ivMenuAbout;
    @BindView(R.id.iv_menu_about_more)
    ImageView ivMenuAboutMore;
    @BindView(R.id.rl_menu_about)
    RelativeLayout rlMenuAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.rl_menu_collect, R.id.rl_menu_history, R.id.rl_menu_about})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_menu_collect:
                intent = new Intent(this, ComicCollectionActivity.class);
                break;
            case R.id.rl_menu_history:
                intent = new Intent(this, ComicHistoryActivity.class);
                break;
            case R.id.rl_menu_about:
                intent = new Intent(this, AboutActivity.class);
                break;
        }
        startActivity(intent);
    }
}
