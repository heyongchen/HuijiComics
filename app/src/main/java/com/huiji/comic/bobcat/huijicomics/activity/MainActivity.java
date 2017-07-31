package com.huiji.comic.bobcat.huijicomics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicListAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListBean;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.utils.AppExit2Back;
import com.huiji.comic.bobcat.huijicomics.utils.CharacterParser;
import com.huiji.comic.bobcat.huijicomics.utils.InitComicsList;
import com.huiji.comic.bobcat.huijicomics.utils.PinyinComparator;
import com.huiji.comic.bobcat.huijicomics.utils.UrlUtils;
import com.huiji.comic.bobcat.huijicomics.widget.ClearEditText;
import com.huiji.comic.bobcat.huijicomics.widget.SideBar;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_comic_list)
    RecyclerView rvComicList;
    @BindView(R.id.tv_place_holder)
    TextView tvPlaceHolder;
    @BindView(R.id.et_filter_edit)
    ClearEditText etFilterEdit;
    @BindView(R.id.sb_side_bar)
    SideBar sbSideBar;
    @BindView(R.id.tv_dialog)
    TextView tvDialog;

    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());
    private List<ComicListDbInfo> dbComicList = null;
    private List<ComicListBean> comicListBeanList = null;
    private ComicListAdapter comicListAdapter;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //将焦点控制在toolbar上
        toolbar.setFocusable(true);
        toolbar.setFocusableInTouchMode(true);
        toolbar.requestFocus();
        toolbar.requestFocusFromTouch();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                    initViews();
                    dismissProgressDialog();
                    break;
            }
        }

        ;
    };

    private void initViews() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sbSideBar.setTextView(tvDialog);

        //设置右侧触摸监听
        sbSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = comicListAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    rvComicList.scrollToPosition(position);
                }

            }
        });

        comicListBeanList = filledData(getComicList());

        // 根据a-z进行排序源数据
        Collections.sort(comicListBeanList, pinyinComparator);
        comicListAdapter = new ComicListAdapter(MainActivity.this, comicListBeanList, true);
        rvComicList.setAdapter(comicListAdapter);

        //根据输入框输入值的改变来过滤搜索
        etFilterEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<ComicListBean> filterDateList = new ArrayList<ComicListBean>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = comicListBeanList;
        } else {
            filterDateList.clear();
            for (ComicListBean sortModel : comicListBeanList) {
                String name = sortModel.getTitle();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        comicListAdapter.updateListView(filterDateList);
    }

    /**
     * 为ListView填充数据
     *
     * @return
     */
    private List<ComicListBean> filledData(List<ComicListBean> data) {
        List<ComicListBean> mSortList = new ArrayList<ComicListBean>();

        for (int i = 0; i < data.size(); i++) {
            ComicListBean sortModel = new ComicListBean();
            sortModel.setTitle(data.get(i).getTitle());
            sortModel.setImgUrl(data.get(i).getImgUrl());
            sortModel.setComicId(data.get(i).getComicId());
            sortModel.setAuthor(data.get(i).getAuthor());
            sortModel.setMsg(data.get(i).getMsg());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(data.get(i).getTitle());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetter(sortString.toUpperCase());
            } else {
                sortModel.setSortLetter("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        return dbComicList != null && dbComicList.size() > 0;
    }

    public List<ComicListBean> getComicList() {
        List<ComicListBean> list = new ArrayList<>();
        try {
            dbComicList = dbManager.findAll(ComicListDbInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        for (ComicListDbInfo comicListDbInfo : dbComicList) {
            list.add(new ComicListBean(comicListDbInfo.getComicId(), comicListDbInfo.getImgUrl(), comicListDbInfo.getTitle(), comicListDbInfo.getAuthor(), comicListDbInfo.getMsg()));
        }
        if (list.size() > 0) {
            tvPlaceHolder.setVisibility(View.GONE);
        } else {
            tvPlaceHolder.setVisibility(View.VISIBLE);
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AppExit2Back.exitApp(this);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_collect:
                intent = new Intent(this, ComicCollectionActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_history:
                intent = new Intent(this, ComicHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_send:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
