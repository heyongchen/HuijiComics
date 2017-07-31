package com.huiji.comic.bobcat.huijicomics.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicMenuAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.utils.InitComicsList;
import com.huiji.comic.bobcat.huijicomics.utils.IntentKey;
import com.huiji.comic.bobcat.huijicomics.utils.UrlUtils;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicMenuActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_comic_view)
    ImageView ivComicView;
    @BindView(R.id.tv_comic_title)
    TextView tvComicTitle;
    @BindView(R.id.tv_comic_author)
    TextView tvComicAuthor;
    @BindView(R.id.tv_comic_msg)
    TextView tvComicMsg;
    @BindView(R.id.rv_comic_menu)
    RecyclerView rvComicMenu;
    @BindView(R.id.tv_comic_collect)
    TextView tvComicCollect;
    @BindView(R.id.tv_place_holder)
    TextView tvPlaceHolder;

    private String comicId;
    private String comicTitle;
    private String readHistory;
    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());
    private ComicMenuAdapter comicMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_menu);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        comicTitle = getIntent().getStringExtra(IntentKey.COMIC_TITLE);
        setTitle(comicTitle);

        rvComicMenu.setLayoutManager(new GridLayoutManager(this, 4));

        Glide.with(this)
                .load(getIntent().getStringExtra(IntentKey.COMIC_IMG))
                .into(ivComicView);
        tvComicTitle.setText(getIntent().getStringExtra(IntentKey.COMIC_TITLE));
        tvComicAuthor.setText(getIntent().getStringExtra(IntentKey.COMIC_AUTHOR));
        tvComicMsg.setText(getIntent().getStringExtra(IntentKey.COMIC_MSG));

        comicId = getIntent().getStringExtra(IntentKey.COMIC_ID);

        showProgressDialog();
        UrlUtils.getLinkList(comicId, new UrlUtils.RequestDataListener() {
            @Override
            public void ok() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        });

        if (checkCollect()) {
            tvComicCollect.setText("取消收藏");
        } else {
            tvComicCollect.setText("收藏漫画");
        }

        tvComicCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCollect()) {
                    tvComicCollect.setText("收藏漫画");
                    changeCollect(String.valueOf(0));
                } else {
                    tvComicCollect.setText("取消收藏");
                    changeCollect(String.valueOf(1));
                }
            }
        });
    }

    private void changeCollect(String state) {
        WhereBuilder b = WhereBuilder.b();
        b.and("comicId", "=", comicId);//条件
        KeyValue collect = new KeyValue("isCollect", state);
        try {
            dbManager.update(ComicListDbInfo.class, b, collect);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private boolean checkCollect() {
        List<ComicListDbInfo> list = null;
        WhereBuilder b = WhereBuilder.b();
        b.and("isCollect", "=", "1");
        b.and("comicId", "=", comicId);
        try {
            list = dbManager.selector(ComicListDbInfo.class).where(b).findAll();//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list != null && list.size() > 0;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //更新UI
            switch (msg.what) {
                case 1:
                    comicMenuAdapter = new ComicMenuAdapter(ComicMenuActivity.this, comicId, comicTitle, readHistory, InitComicsList.getComicDataBeanList());
                    if (InitComicsList.getComicDataBeanList().size() > 0) {
                        tvPlaceHolder.setVisibility(View.GONE);
                    } else {
                        tvPlaceHolder.setVisibility(View.VISIBLE);
                    }
                    rvComicMenu.setAdapter(comicMenuAdapter);
                    dismissProgressDialog();
                    break;
            }
        }

        ;
    };

    @Override
    protected void onResume() {
        super.onResume();
        checkHistory();
    }

    private void checkHistory() {
        ComicListDbInfo result = null;
        WhereBuilder b = WhereBuilder.b();
        b.and("comicId", "=", comicId);
        try {
            result = dbManager.selector(ComicListDbInfo.class).where(b).findFirst();//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (result != null) {
            readHistory = result.getLastReadUrl() != null ? result.getLastReadUrl() : "";
        }
        if (comicMenuAdapter != null) {
            comicMenuAdapter.setReadHistory(readHistory);
        }
    }


}
