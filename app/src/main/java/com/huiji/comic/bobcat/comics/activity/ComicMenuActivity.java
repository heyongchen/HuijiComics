package com.huiji.comic.bobcat.comics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huiji.comic.bobcat.comics.MainApplication;
import com.huiji.comic.bobcat.comics.R;
import com.huiji.comic.bobcat.comics.adapter.ComicMenuAdapter;
import com.huiji.comic.bobcat.comics.base.BaseActivity;
import com.huiji.comic.bobcat.comics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.comics.utils.C;
import com.huiji.comic.bobcat.comics.utils.InitComicsList;
import com.huiji.comic.bobcat.comics.utils.IntentKey;
import com.huiji.comic.bobcat.comics.utils.UrlUtils;
import com.huiji.comic.bobcat.comics.widget.ConfirmDialog;
import com.huiji.comic.bobcat.comics.widget.ImageShowDialog;

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
    @BindView(R.id.tv_comic_continue)
    TextView tvComicContinue;

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
        ivComicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageShowDialog dialog = new ImageShowDialog(ComicMenuActivity.this, getIntent().getStringExtra(IntentKey.COMIC_IMG));
                dialog.show();
            }
        });
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
            tvComicCollect.setText(R.string.info_collect_off);
        } else {
            tvComicCollect.setText(R.string.info_collect_on);
        }

        tvComicCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCollect()) {
                    tvComicCollect.setText(R.string.info_collect_on);
                    changeCollect(0);
                } else {
                    tvComicCollect.setText(R.string.info_collect_off);
                    changeCollect(1);
                }
            }
        });

        tvComicMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvComicMsg.getMaxLines() == 3) {
                    tvComicMsg.setMaxLines(15);
                    tvComicMsg.setCompoundDrawablesWithIntrinsicBounds(null, null, null, ContextCompat.getDrawable(ComicMenuActivity.this, R.drawable.ic_up));
                } else {
                    tvComicMsg.setMaxLines(3);
                    tvComicMsg.setCompoundDrawablesWithIntrinsicBounds(null, null, null, ContextCompat.getDrawable(ComicMenuActivity.this, R.drawable.ic_down));
                }
            }
        });

        tvComicContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comicUrl = "";
                if (readHistory != null && !readHistory.isEmpty()) {
                    comicUrl = readHistory;
                } else if ((readHistory == null || readHistory.isEmpty()) && InitComicsList.getComicDataBeanList().size() > 0) {
                    comicUrl = InitComicsList.getComicDataBeanList().get(0).getDataUrl();
                    addHistory(comicUrl);
                }
                if (!comicUrl.isEmpty()) {
                    Intent intent = new Intent(ComicMenuActivity.this, X5WebViewActivity.class);
                    intent.putExtra(IntentKey.COMIC_ID, comicId);
                    intent.putExtra(IntentKey.COMIC_TITLE, comicTitle);
                    intent.putExtra(IntentKey.COMIC_VIEW_URL, comicUrl);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            ConfirmDialog dialog = new ConfirmDialog(this);
            dialog.setTitle(getString(R.string.info_delete_comic));
            dialog.setMessage(String.format(getString(R.string.info_delete_comic_msg), comicTitle));
            dialog.setButtonCancelText(getString(R.string.cancel));
            dialog.setButtonOKText(getString(R.string.delete_ok));
            dialog.setOnConfirmListener(new ConfirmDialog.OnConfirmListener() {
                @Override
                public void onIgnore(boolean ignore) {
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onOK() {
                    deleteComic();
                }
            });
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteComic() {
        WhereBuilder b = WhereBuilder.b();
        b.and("comicId", "=", comicId);//条件
        try {
            dbManager.delete(ComicListDbInfo.class, b);
        } catch (DbException e) {
            e.printStackTrace();
        }
        C.hasNewComic = true;
        Toast.makeText(this, R.string.tip_comic_delete_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void changeNew(String comicId, int comicNum) {
        WhereBuilder b = WhereBuilder.b();
        b.and("comicId", "=", comicId);//条件
        KeyValue isNew = new KeyValue("isNew", 0);
        KeyValue num = new KeyValue("menuNum", comicNum);
        try {
            dbManager.update(ComicListDbInfo.class, b, isNew);
            dbManager.update(ComicListDbInfo.class, b, num);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void addHistory(String comicUrl) {
        if (!comicUrl.isEmpty()) {
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
    }

    private void changeCollect(int state) {
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
        b.and("isCollect", "=", 1);
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
                        changeNew(comicId, InitComicsList.getComicDataBeanList().size());
                    } else {
                        tvPlaceHolder.setVisibility(View.VISIBLE);
                    }
                    rvComicMenu.setAdapter(comicMenuAdapter);
                    checkHistory();
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

    @Override
    protected void onDestroy() {
        InitComicsList.clearComicDataBeanList();
        super.onDestroy();

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
            if (result.getLastReadUrl() == null || result.getLastReadUrl().isEmpty()) {
                tvComicContinue.setText(R.string.info_start_read);
            } else {
                readHistory = UrlUtils.replaceHost(result.getLastReadUrl());
                tvComicContinue.setText(R.string.info_continue_read);
            }
        } else {
            tvComicContinue.setText(R.string.info_start_read);
        }
        if (comicMenuAdapter != null) {
            comicMenuAdapter.setReadHistory(readHistory);
        }
    }


}
