package com.huiji.comic.bobcat.huijicomics.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicListAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListBean;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicHistoryActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_comic_list)
    RecyclerView rvComicList;
    @BindView(R.id.tv_place_holder)
    TextView tvPlaceHolder;

    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private int lastOffset = 0, lastPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_history);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rvComicList.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rvComicList.setAdapter(new ComicListAdapter(ComicHistoryActivity.this, getComicList(), false));
        linearLayoutManager.scrollToPositionWithOffset(lastPosition, lastOffset);
    }

    @Override
    protected void onStop() {
        getPositionAndOffset();
        super.onStop();
    }

    private void getPositionAndOffset() {
        //获取可视的第一个view
        View topView = linearLayoutManager.getChildAt(0);
        if (topView != null) {
            //获取与该view的顶部的偏移量
            lastOffset = topView.getTop();
            //得到该View的数组位置
            lastPosition = linearLayoutManager.getPosition(topView);
        }
    }

    private List<ComicListBean> getComicList() {
        List<ComicListBean> list = new ArrayList<>();
        List<ComicListDbInfo> result = new ArrayList<>();
        WhereBuilder b = WhereBuilder.b();
        b.and("lastReadTime", ">", 0);
        try {
            result = dbManager.selector(ComicListDbInfo.class).orderBy("lastReadTime", true).where(b).findAll();//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (result != null && result.size() > 0) {
            for (ComicListDbInfo comicListDbInfo : result) {
                list.add(new ComicListBean(comicListDbInfo.getComicId(), comicListDbInfo.getImgUrl(), comicListDbInfo.getTitle(), comicListDbInfo.getAuthor(), comicListDbInfo.getMsg()));
            }
        }
        if (list.size() > 0) {
            tvPlaceHolder.setVisibility(View.GONE);
        } else {
            tvPlaceHolder.setVisibility(View.VISIBLE);
        }
        return list;
    }

}
