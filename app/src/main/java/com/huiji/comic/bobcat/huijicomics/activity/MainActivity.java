package com.huiji.comic.bobcat.huijicomics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicListAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListBean;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.utils.InitComicsList;
import com.huiji.comic.bobcat.huijicomics.utils.UrlUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_comic_list)
    RecyclerView rvComicList;

    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());
    private List<ComicListDbInfo> dbComicList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        rvComicList.setLayoutManager(new LinearLayoutManager(this));
        if (!isGetList()) {
            showProgressDialog();
            UrlUtils.getMenuList(InitComicsList.getComicIdList(), new UrlUtils.RequestStateListener() {
                @Override
                public void ok() {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            });
        } else {
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //更新UI
            switch (msg.what) {
                case 1:
                    rvComicList.setAdapter(new ComicListAdapter(MainActivity.this, getComicList()));
                    dismissProgressDialog();
                    break;
            }
        }

        ;
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isGetList() {
        try {
            dbComicList = dbManager.findAll(ComicListDbInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbComicList != null && dbComicList.size() >= 0;
    }

    public List<ComicListBean> getComicList() {
        List<ComicListBean> list = new ArrayList<>();
        try {
            dbComicList = dbManager.findAll(ComicListDbInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        for (ComicListDbInfo comicListDbInfo : dbComicList) {
            list.add(new ComicListBean(comicListDbInfo.getComicId(), comicListDbInfo.getImgUrl(), comicListDbInfo.getTitle()));
        }
        return list;
    }
}
