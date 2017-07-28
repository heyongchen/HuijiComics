package com.huiji.comic.bobcat.huijicomics.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicCollectionActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_collection);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

}
