package com.huiji.comic.bobcat.huijicomics.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
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
import com.huiji.comic.bobcat.huijicomics.utils.C;
import com.huiji.comic.bobcat.huijicomics.utils.DisplayUtil;
import com.huiji.comic.bobcat.huijicomics.utils.IntentKey;
import com.huiji.comic.bobcat.huijicomics.utils.SPHelper;
import com.huiji.comic.bobcat.huijicomics.utils.SpKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
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
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
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
        comicUrl = getIntent().getStringExtra(IntentKey.COMIC_VIEW_URL);
        comicTitle = getIntent().getStringExtra(IntentKey.COMIC_TITLE);
        comicId = getIntent().getStringExtra(IntentKey.COMIC_ID);
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
            rvComicView.setAdapter(new ComicViewAdapter(this, comicTitle, comicTitle, comicMenuId, comicPicList));
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
}
