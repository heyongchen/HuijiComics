package com.huiji.comic.bobcat.huijicomics.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicListAdapter;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicMenuAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListBean;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.utils.InitComicsList;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicCollectionActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_comic_list)
    RecyclerView rvComicList;

    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());
    private List<ComicListDbInfo> dbComicList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_collection);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        rvComicList.setAdapter(new ComicListAdapter(ComicCollectionActivity.this, getComicList()));
    }

    public List<ComicListBean> getComicList() {
        List<ComicListBean> list = new ArrayList<>();
        List<ComicListDbInfo> result = new ArrayList<>();
        WhereBuilder b = WhereBuilder.b();
        b.and("isCollect", "=", "1");
        try {
            result = dbManager.selector(ComicListDbInfo.class).where(b).findAll();//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        for (ComicListDbInfo comicListDbInfo : result) {
            list.add(new ComicListBean(comicListDbInfo.getComicId(), comicListDbInfo.getImgUrl(), comicListDbInfo.getTitle()));
        }
        return list;
    }

}
