package com.huiji.comic.bobcat.huijicomics.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicViewAdapter;
import com.huiji.comic.bobcat.huijicomics.adapter.ComicViewPagerAdapter;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicDataBean;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.utils.C;
import com.huiji.comic.bobcat.huijicomics.utils.DisplayUtil;
import com.huiji.comic.bobcat.huijicomics.utils.IntentKey;
import com.huiji.comic.bobcat.huijicomics.utils.SPHelper;
import com.huiji.comic.bobcat.huijicomics.utils.SpKey;
import com.huiji.comic.bobcat.huijicomics.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicViewActivity extends BaseActivity {

    private static final String TAG = "ComicViewActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_comic_view)
    RecyclerView rvComicView;
    @BindView(R.id.vp_comic_view)
    ViewPager vpComicView;

    private String comicTitle;
    private String comicUrl;
    private String comicId;
    private String comicMenuId;
    private int comicMenuPosition = 0;
    private ArrayList<ComicDataBean> comicDataBeanList;
    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());

    private String conicJsonUrl;
    private List<String> comicPicList = new ArrayList<>();
    private RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_placeholder);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SPHelper.get().get(SpKey.VIEW_HORIZONTAL, false) && SPHelper.get().get(SpKey.VIEW_PAGE, C.viewType.FLOW).equals(C.viewType.FLOW)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        try {
            // 隐藏标题栏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (SPHelper.get().get(SpKey.VIEW_HORIZONTAL, false) && SPHelper.get().get(SpKey.VIEW_PAGE, C.viewType.FLOW).equals(C.viewType.FLOW)) {
                //隐藏状态栏
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            // 禁用系统截屏功能
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_comic_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        comicDataBeanList = getIntent().getParcelableArrayListExtra(IntentKey.COMIC_BEAN);
        comicMenuPosition = getIntent().getIntExtra(IntentKey.COMIC_MENU_POSITION, 0);
        comicTitle = getIntent().getStringExtra(IntentKey.COMIC_TITLE);
        comicId = getIntent().getStringExtra(IntentKey.COMIC_ID);
        comicUrl = comicDataBeanList.get(comicMenuPosition).getDataUrl();
        toolbar.setVisibility(View.GONE);

        String temp = comicUrl.replace("/shinmangaplayer/index.html", "");
        comicMenuId = temp.substring(temp.lastIndexOf("/") + 1);
        conicJsonUrl = C.getComicDataJson(comicMenuId);
        Log.i(TAG, "conicJsonUrl: " + conicJsonUrl);

        x.http().get(new RequestParams(conicJsonUrl), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //解析result
                Log.i(TAG, "conicJsonUrlResult: " + result);
                initComicView(result.substring(result.indexOf("\"pages\":") + 8));
            }

            //请求异常后的回调方法
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            //主动调用取消请求的回调方法
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void initComicView(String result) {
        Log.i(TAG, "conicJsonUrlResultJson: " + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonObjectPage = jsonObject.getJSONObject("page");
            String order = jsonObject.getJSONArray("order").toString().replace("[", "").replace("]", "").replaceAll("\"", "");
            String subS[] = order.split(",");
            List<String> list = Arrays.asList(subS);
            for (String sOrder : list) {
                comicPicList.add(C.getComicPicUrl(comicUrl, jsonObjectPage.getString(sOrder)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (SPHelper.get().get(SpKey.VIEW_PAGE, C.viewType.FLOW).equals(C.viewType.FLOW)) {
            rvComicView.setVisibility(View.VISIBLE);
            vpComicView.setVisibility(View.GONE);
            rvComicView.setLayoutManager(new LinearLayoutManager(this));
            ComicViewAdapter comicViewAdapter = new ComicViewAdapter(this, comicTitle, comicTitle, comicMenuId, comicPicList);
            rvComicView.setAdapter(comicViewAdapter);
            comicViewAdapter.setOnJumpClickListener(new ComicViewAdapter.OnJumpClickListener() {
                @Override
                public void onClick(boolean next) {
                    if (next) {
                        if (comicMenuPosition >= comicDataBeanList.size() - 1) {
                            Toast.makeText(ComicViewActivity.this, "当前已是最新话", Toast.LENGTH_SHORT).show();
                        } else {
                            addHistory(comicDataBeanList.get(comicMenuPosition + 1).getDataUrl());
                            Intent intent = new Intent(ComicViewActivity.this, ComicViewActivity.class);
                            intent.putExtra(IntentKey.COMIC_ID, comicId);
                            intent.putExtra(IntentKey.COMIC_TITLE, comicTitle);
                            intent.putParcelableArrayListExtra(IntentKey.COMIC_BEAN, comicDataBeanList);
                            intent.putExtra(IntentKey.COMIC_MENU_POSITION, comicMenuPosition + 1);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        if (comicMenuPosition <= 0) {
                            Toast.makeText(ComicViewActivity.this, "当前已是第一话", Toast.LENGTH_SHORT).show();
                        } else {
                            addHistory(comicDataBeanList.get(comicMenuPosition - 1).getDataUrl());
                            Intent intent = new Intent(ComicViewActivity.this, ComicViewActivity.class);
                            intent.putExtra(IntentKey.COMIC_ID, comicId);
                            intent.putExtra(IntentKey.COMIC_TITLE, comicTitle);
                            intent.putParcelableArrayListExtra(IntentKey.COMIC_BEAN, comicDataBeanList);
                            intent.putExtra(IntentKey.COMIC_MENU_POSITION, comicMenuPosition - 1);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        } else {
            rvComicView.setVisibility(View.GONE);
            vpComicView.setVisibility(View.VISIBLE);
            vpComicView.setOffscreenPageLimit(5);
            List<View> imgViewList = new ArrayList<>();
            for (String picUrl : comicPicList) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                linearLayout.setGravity(Gravity.CENTER);
                final ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.img_placeholder));
                final GlideUrl glideUrl = new GlideUrl(picUrl, new LazyHeaders.Builder()
                        .addHeader("Host", "smp.yoedge.com")
                        .addHeader("Referer", "http://smp.yoedge.com/smp-app/" + comicMenuId + "/shinmangaplayer/index.html?__okraw")
                        .build());
                Glide.with(this).asBitmap().load(glideUrl).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.height = DisplayUtil.getScreenWidth(ComicViewActivity.this) * resource.getHeight() / resource.getWidth();
                        layoutParams.width = DisplayUtil.getScreenWidth(ComicViewActivity.this);
                        Glide.with(ComicViewActivity.this)
                                .load(glideUrl)
                                .apply(requestOptions)
                                .into(imageView);
                    }
                });
                linearLayout.addView(imageView);
                imgViewList.add(linearLayout);
            }
            vpComicView.setAdapter(new ComicViewPagerAdapter(this, comicTitle, comicTitle, comicMenuId, imgViewList));
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
}
