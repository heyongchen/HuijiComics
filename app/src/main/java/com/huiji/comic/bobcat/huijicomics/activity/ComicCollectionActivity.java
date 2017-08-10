package com.huiji.comic.bobcat.huijicomics.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicListAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListBean;
import com.huiji.comic.bobcat.huijicomics.bean.ComicUpdateBean;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.db.ComicUpdateDbInfo;
import com.huiji.comic.bobcat.huijicomics.utils.CharacterParser;
import com.huiji.comic.bobcat.huijicomics.utils.PinyinComparator;
import com.huiji.comic.bobcat.huijicomics.widget.ClearEditText;
import com.huiji.comic.bobcat.huijicomics.widget.SideBar;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicCollectionActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_comic_list)
    RecyclerView rvComicList;
    @BindView(R.id.tv_place_holder)
    TextView tvPlaceHolder;
    @BindView(R.id.et_filter_edit)
    ClearEditText etFilterEdit;
    @BindView(R.id.tv_dialog)
    TextView tvDialog;
    @BindView(R.id.sb_side_bar)
    SideBar sbSideBar;

    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private List<ComicListBean> comicListBeanList = null;
    private ComicListAdapter comicListAdapter;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private int lastOffset = 0, lastPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_collection);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //将焦点控制在toolbar上
        toolbar.setFocusable(true);
        toolbar.setFocusableInTouchMode(true);
        toolbar.requestFocus();
        toolbar.requestFocusFromTouch();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rvComicList.setLayoutManager(linearLayoutManager);

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

    @Override
    protected void onStop() {
        getPositionAndOffset();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
        filterData(etFilterEdit.getText().toString());
        comicListAdapter.setComicUpdateList(getNewList());
    }

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
        comicListBeanList = filledData(getComicList());
        // 根据a-z进行排序源数据
        Collections.sort(comicListBeanList, pinyinComparator);
        comicListAdapter = new ComicListAdapter(ComicCollectionActivity.this, comicListBeanList, true);
        rvComicList.setAdapter(comicListAdapter);
        linearLayoutManager.scrollToPositionWithOffset(lastPosition, lastOffset);
    }

    private List<ComicListBean> getComicList() {
        List<ComicListBean> list = new ArrayList<>();
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

}
