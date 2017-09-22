package com.huiji.comic.bobcat.comics.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.huiji.comic.bobcat.comics.R;
import com.huiji.comic.bobcat.comics.adapter.UpdateNoteAdapter;
import com.huiji.comic.bobcat.comics.base.BaseActivity;
import com.huiji.comic.bobcat.comics.bean.UpdateNoteBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateNoteActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_update_note)
    RecyclerView rvUpdateNote;

    private List<UpdateNoteBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        rvUpdateNote.setLayoutManager(new LinearLayoutManager(this));

        initList();
        rvUpdateNote.setAdapter(new UpdateNoteAdapter(this, list));
    }

    private void initList() {
        list.add(new UpdateNoteBean("1.0.0", "ヾ(●´∀｀●)终于1.0了，暂时休息一下"));
        list.add(new UpdateNoteBean("0.9.9", "单独增加已更新漫画页面；"));
        list.add(new UpdateNoteBean("0.9.8", "更新提示框增加该版本不再提醒选项；"));
        list.add(new UpdateNoteBean("0.9.7", "增加更新日志；"));
        list.add(new UpdateNoteBean("0.9.5", "1、增加漫画《怪灭王与12人的星之巫女》；\n" +
                "2、增加删除漫画功能；\n" +
                "3、增加封面图点击放大功能；"));
        list.add(new UpdateNoteBean("0.9.1", "增加更新提醒功能（仅限已收藏漫画）"));
        list.add(new UpdateNoteBean("0.9.0", "1、漫画简介可点击展开；\n" +
                "2、个人中心页面增加手动添加漫画功能；\n" +
                "注：个人中心右上角+，可以手动添加SMP网站已有漫画。"));
        list.add(new UpdateNoteBean("0.8.5", "1、增加数据库查询保护；\n" +
                "2、增加浏览器退出保护；\n" +
                "3、希望Android 7.0用户可以在贴吧更新贴反馈一下是否可以应用内更新成功，谢谢！"));
        list.add(new UpdateNoteBean("0.8.4", "1、修改安装包下载功能，现在Android 7.0可以正常下载更新了；\n" +
                "2、优化部分布局；"));
        list.add(new UpdateNoteBean("0.8.1", "1、版本更新增加更新内容；\n" +
                "2、漫画收藏改为首字母排序且可检索；\n" +
                "3、优化检索为空提示；\n" +
                "4、漫画浏览器改为系统浏览器；"));
        list.add(new UpdateNoteBean("0.7.3", "1、漫画浏览页显示黑色底色状态栏（方便查看当前系统时间）；\n" +
                "2、漫画收藏、浏览记录页面记录列表滑动位置；"));
        list.add(new UpdateNoteBean("0.7.2", "1、优化Android 7.0更新下载；\n" +
                "2、关于页面增加手动检查更新功能；"));
        list.add(new UpdateNoteBean("0.7.1", "1、加载中提示语不可手动取消；\n" +
                "2、增加数据库查询保护；"));
        list.add(new UpdateNoteBean("0.7.0", "集成蒲公英自动更新及Crash报告；"));
        list.add(new UpdateNoteBean("0.6.7及之前版本", "实现基本功能，应用上传蒲公英；"));
    }

}
