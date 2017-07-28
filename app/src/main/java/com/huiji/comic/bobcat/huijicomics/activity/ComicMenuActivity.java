package com.huiji.comic.bobcat.huijicomics.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicListAdapter;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicMenuAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.utils.InitComicsList;
import com.huiji.comic.bobcat.huijicomics.utils.SpKey;
import com.huiji.comic.bobcat.huijicomics.utils.UrlUtils;

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

    private String comicId;
    private String comicTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_menu);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        comicTitle = getIntent().getStringExtra(SpKey.COMIC_TITLE);
        setTitle(comicTitle);

        rvComicMenu.setLayoutManager(new GridLayoutManager(this, 4));

        Glide.with(this)
                .load(getIntent().getStringExtra(SpKey.COMIC_IMG))
                .into(ivComicView);
        tvComicTitle.setText(getIntent().getStringExtra(SpKey.COMIC_TITLE));

        comicId = getIntent().getStringExtra(SpKey.COMIC_ID);

        showProgressDialog();
        UrlUtils.getLinkList(comicId, new UrlUtils.RequestDataListener() {
            @Override
            public void ok() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //更新UI
            switch (msg.what) {
                case 1:
                    rvComicMenu.setAdapter(new ComicMenuAdapter(ComicMenuActivity.this, comicId, comicTitle, InitComicsList.getComicDataBeanList()));
                    dismissProgressDialog();
                    break;
            }
        }

        ;
    };

}
