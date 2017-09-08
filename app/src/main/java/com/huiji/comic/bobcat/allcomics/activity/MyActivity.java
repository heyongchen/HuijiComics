package com.huiji.comic.bobcat.allcomics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiji.comic.bobcat.allcomics.MainApplication;
import com.huiji.comic.bobcat.allcomics.R;
import com.huiji.comic.bobcat.allcomics.base.BaseActivity;
import com.huiji.comic.bobcat.allcomics.bean.ComicListBean;
import com.huiji.comic.bobcat.allcomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.allcomics.utils.C;
import com.huiji.comic.bobcat.allcomics.utils.UrlUtils;
import com.huiji.comic.bobcat.allcomics.widget.AddComicDialog;
import com.huiji.comic.bobcat.allcomics.widget.AddComicResultDialog;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

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

    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());
    private ComicListBean mComicListBean;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            AddComicDialog addComicDialog = new AddComicDialog(this);
            addComicDialog.setOnConfirmListener(new AddComicDialog.OnConfirmListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onOK(String comicId) {
                    showProgressDialog(getString(R.string.tip_add_comic_checking));
                    UrlUtils.checkLink(comicId, new UrlUtils.checkDataListener() {
                        @Override
                        public void ok(ComicListBean comicListBean) {
                            mComicListBean = comicListBean;
                            Message message = new Message();
                            message.what = 2;
                            mHandler.sendMessage(message);
                        }
                    });
                }
            });
            addComicDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //更新UI
            switch (msg.what) {
                case 2:
                    dismissProgressDialog();
                    if (mComicListBean == null || mComicListBean.getTitle() == null) {
                        Toast.makeText(MyActivity.this, R.string.tip_add_comic_error, Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkComic(mComicListBean.getComicId())) {
                            Toast.makeText(MyActivity.this, String.format(getString(R.string.tip_add_comic_duplicate), mComicListBean.getTitle()), Toast.LENGTH_SHORT).show();
                        } else {
                            AddComicResultDialog addComicResultDialog = new AddComicResultDialog(MyActivity.this, mComicListBean);
                            addComicResultDialog.setOnConfirmListener(new AddComicResultDialog.OnConfirmListener() {
                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onOK(String comicId) {
                                    List<String> list = new ArrayList<String>();
                                    list.add(comicId);
                                    showProgressDialog(getString(R.string.tip_loading_update_list));
                                    UrlUtils.getMenuList(list, new UrlUtils.RequestStateListener() {
                                        @Override
                                        public void ok() {
                                            Message message = new Message();
                                            message.what = 3;
                                            mHandler.sendMessage(message);
                                        }
                                    }, true);
                                }
                            });
                            addComicResultDialog.show();
                        }
                    }
                    break;
                case 3:
                    C.hasNewComic = true;
                    Toast.makeText(MyActivity.this, R.string.tip_add_comic_success, Toast.LENGTH_SHORT).show();
                    dismissProgressDialog();
                    break;
                default:
                    break;
            }
        }
    };

    private boolean checkComic(String comicId) {
        List<ComicListDbInfo> result = new ArrayList<>();
        WhereBuilder b = WhereBuilder.b();
        b.and("comicId", "=", comicId);
        try {
            result = dbManager.selector(ComicListDbInfo.class).where(b).findAll();//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result != null && result.size() > 0;
    }
}
