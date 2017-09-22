package com.huiji.comic.bobcat.comics.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.huiji.comic.bobcat.comics.MainApplication;
import com.huiji.comic.bobcat.comics.R;
import com.huiji.comic.bobcat.comics.adapter.ComicListAdapter;
import com.huiji.comic.bobcat.comics.base.BaseActivity;
import com.huiji.comic.bobcat.comics.bean.ComicListBean;
import com.huiji.comic.bobcat.comics.db.ComicListDbInfo;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicUpdateActivity extends BaseActivity {

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
        setContentView(R.layout.activity_comic_update);
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
        ComicListAdapter comicListAdapter = new ComicListAdapter(ComicUpdateActivity.this, getComicList(), false);
        rvComicList.setAdapter(comicListAdapter);
        comicListAdapter.setComicUpdateList(getNewList());
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
        List<ComicListBean> updateList = getNewList();
        List<ComicListBean> list = new ArrayList<>();
        if (updateList != null && updateList.size() > 0) {
            ComicListDbInfo result = new ComicListDbInfo();
            for (ComicListBean updateBean : updateList) {
                WhereBuilder b = WhereBuilder.b();
                b.and("comicId", "=", updateBean.getComicId());
                try {
                    result = dbManager.selector(ComicListDbInfo.class).where(b).findFirst();//查询
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (result != null) {
                    list.add(new ComicListBean(result.getComicId(), result.getImgUrl(), result.getTitle(), result.getAuthor(), result.getMsg()));
                }
            }
        }
        if (list.size() > 0) {
            tvPlaceHolder.setVisibility(View.GONE);
        } else {
            tvPlaceHolder.setVisibility(View.VISIBLE);
        }
        return list;
    }

    private List<ComicListBean> getNewList() {
        List<ComicListBean> list = new ArrayList<>();
        List<ComicListDbInfo> result = new ArrayList<>();
        WhereBuilder b = WhereBuilder.b();
        b.and("isNew", "=", 1);
        try {
            result = dbManager.selector(ComicListDbInfo.class).where(b).findAll();//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (result != null && result.size() > 0) {
            for (ComicListDbInfo comicListDbInfo : result) {
                list.add(new ComicListBean(comicListDbInfo.getComicId(), comicListDbInfo.getTitle()));
            }
        }
        return list;
    }

}
