package com.huiji.comic.bobcat.huijicomics.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicListAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListBean;
import com.huiji.comic.bobcat.huijicomics.bean.ComicUpdateBean;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.db.ComicUpdateDbInfo;
import com.huiji.comic.bobcat.huijicomics.utils.AppExit2Back;
import com.huiji.comic.bobcat.huijicomics.utils.AppUtils;
import com.huiji.comic.bobcat.huijicomics.utils.C;
import com.huiji.comic.bobcat.huijicomics.utils.CharacterParser;
import com.huiji.comic.bobcat.huijicomics.utils.InitComicsList;
import com.huiji.comic.bobcat.huijicomics.utils.PinyinComparator;
import com.huiji.comic.bobcat.huijicomics.utils.UrlUtils;
import com.huiji.comic.bobcat.huijicomics.utils.updateUtil.UpdateUtil;
import com.huiji.comic.bobcat.huijicomics.widget.ClearEditText;
import com.huiji.comic.bobcat.huijicomics.widget.SideBar;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

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

    private final int NOTIFICATION_ID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //将焦点控制在toolbar上
        toolbar.setFocusable(true);
        toolbar.setFocusableInTouchMode(true);
        toolbar.requestFocus();
        toolbar.requestFocusFromTouch();

        UpdateUtil.check(this, false);

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
            }, false);
        } else if (needUpdate()) {
            showProgressDialog(getString(R.string.tip_loading_update_list));
            List<String> defaultIdList = InitComicsList.getComicIdList();
            List<String> dbIdList = new ArrayList<>();
            List<String> duplicateList = new ArrayList<>();
            for (ComicListDbInfo comicListDbInfo : dbComicList) {
                dbIdList.add(comicListDbInfo.getComicId());
            }
            defaultIdList.removeAll(dbIdList);
            for (String comicId : defaultIdList) {
                if (checkComic(comicId)) {
                    duplicateList.add(comicId);
                }
            }
            defaultIdList.removeAll(duplicateList);
            UrlUtils.getMenuList(defaultIdList, new UrlUtils.RequestStateListener() {
                @Override
                public void ok() {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            }, true);
        } else {
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }

    }

    private void checkNew() {
        List<ComicUpdateBean> collectionList = getCollectionComicList();
        int updateNum = getNewComicList().size();
        if (collectionList.size() > 0) {
            if (collectionList.size() != updateNum) {
                try {
                    dbManager.delete(ComicUpdateDbInfo.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                List<ComicUpdateDbInfo> comicUpdateDbInfoList = new ArrayList<>();
                for (ComicUpdateBean comicUpdateBean : collectionList) {
                    comicUpdateDbInfoList.add(new ComicUpdateDbInfo(comicUpdateBean.getComicId(), comicUpdateBean.getComicTitle(), 0));
                }
                try {
                    dbManager.save(comicUpdateDbInfoList);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
            UrlUtils.checkUpdateList(collectionList, new UrlUtils.CheckUpdateListener() {
                @Override
                public void ok() {
                    Message message = new Message();
                    message.what = 9;
                    mHandler.sendMessage(message);
                }
            });
        }
    }

    private List<String> getNewComicList() {
        List<String> list = new ArrayList<>();
        List<ComicUpdateDbInfo> result = new ArrayList<>();
        try {
            result = dbManager.findAll(ComicUpdateDbInfo.class);//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (result != null && result.size() > 0) {
            for (ComicUpdateDbInfo comicListDbInfo : result) {
                list.add(comicListDbInfo.getComicId());
            }
        }
        return list;
    }

    private List<ComicUpdateBean> getCollectionComicList() {
        List<ComicUpdateBean> list = new ArrayList<>();
        List<ComicListDbInfo> result = new ArrayList<>();
        WhereBuilder b = WhereBuilder.b();
        b.and("isCollect", "=", "1");
        try {
            result = dbManager.selector(ComicListDbInfo.class).where(b).findAll();//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (result != null && result.size() > 0) {
            for (ComicListDbInfo comicListDbInfo : result) {
                list.add(new ComicUpdateBean(comicListDbInfo.getComicId(), comicListDbInfo.getTitle()));
            }
        }
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (C.hasNewComic) {
            C.hasNewComic = false;
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }
    }

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

    private boolean needUpdate() {
        List<String> idList = InitComicsList.getComicIdList();
        List<String> dbIdList = new ArrayList<>();
        if (dbComicList != null) {
            if (dbComicList.size() < idList.size()) {
                return true;
            } else {
                for (ComicListDbInfo comicListDbInfo : dbComicList) {
                    dbIdList.add(comicListDbInfo.getComicId());
                }
                idList.removeAll(dbIdList);
                return idList.size() > 0;
            }
        } else {
            Toast.makeText(this, R.string.tip_loading_update_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //更新UI
            switch (msg.what) {
                case 1:
                    initViews();
                    dismissProgressDialog();
                    checkNew();
                    break;
                case 9:
                    try {
                        String notificationMsg = "";
                        List<ComicUpdateBean> list = getNewList();
                        if (list.size() > 0) {
                            if (list.size() == 1) {
                                notificationMsg = String.format(getString(R.string.tip_notification_new_one), list.get(0).getComicTitle());
                            } else {
                                notificationMsg = String.format(getString(R.string.tip_notification_new_more), list.size());
                            }
                            Intent intent = new Intent(MainActivity.this, ComicUpdateActivity.class);
                            Random random = new Random();
                            PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, random.nextInt(),
                                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Notification.Builder builder = new Notification.Builder(MainActivity.this);
                            builder.setSmallIcon(R.drawable.icon)
                                    .setWhen(System.currentTimeMillis())
                                    .setContentTitle(AppUtils.getAppName())
                                    .setContentText(notificationMsg)
                                    .setContentIntent(contentIntent)
                                    .setAutoCancel(true);
                            Notification notification = builder.build();
                            NotificationManager manager = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(NOTIFICATION_ID, notification);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private List<ComicUpdateBean> getNewList() {
        List<ComicUpdateBean> list = new ArrayList<>();
        List<ComicUpdateDbInfo> result = new ArrayList<>();
        WhereBuilder b = WhereBuilder.b();
        b.and("isNew", "=", "1");
        try {
            result = dbManager.selector(ComicUpdateDbInfo.class).where(b).findAll();//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (result != null && result.size() > 0) {
            for (ComicUpdateDbInfo comicListDbInfo : result) {
                list.add(new ComicUpdateBean(comicListDbInfo.getComicId(), comicListDbInfo.getTitle()));
            }
        }
        return list;
    }

    private void initViews() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sbSideBar.setTextView(tvDialog);

        //设置右侧触摸监听
        sbSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if (comicListAdapter != null) {
                    //该字母首次出现的位置
                    int position = comicListAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        rvComicList.scrollToPosition(position);
                    }
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
                if (comicListAdapter != null) {
                    //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                    filterData(s.toString());
                }
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
        if (filterDateList.size() > 0) {
            tvPlaceHolder.setVisibility(View.GONE);
        } else {
            tvPlaceHolder.setVisibility(View.VISIBLE);
        }
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_my) {
            startActivity(new Intent(MainActivity.this, MyActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isGetList() {
        try {
            dbComicList = dbManager.findAll(ComicListDbInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbComicList == null) {
            dbComicList = new ArrayList<>();
        }
        return dbComicList.size() > 0;
    }

    private List<ComicListBean> getComicList() {
        List<ComicListBean> list = new ArrayList<>();
        try {
            dbComicList = dbManager.findAll(ComicListDbInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbComicList != null && dbComicList.size() > 0) {
            for (ComicListDbInfo comicListDbInfo : dbComicList) {
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

    @Override
    public void onBackPressed() {
        AppExit2Back.exitApp(this);
    }
}
