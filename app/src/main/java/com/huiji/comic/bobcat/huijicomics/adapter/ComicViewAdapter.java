package com.huiji.comic.bobcat.huijicomics.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.utils.DisplayUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeYongchen on 2017/7/28.
 */

public class ComicViewAdapter extends RecyclerView.Adapter<ComicViewAdapter.RvViewHolder> {

    private Context mContext;
    private String mComicId;
    private String mComicTitle;
    private String mMenuTitle;
    private String mComicUrl;
    private String mComicMenuId;
    private List<String> mPicList;
    private RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_placeholder);

    public ComicViewAdapter(Context context, String comicTitle, String menuTitle, String menuId, List<String> picList) {
        this.mContext = context;
        this.mComicTitle = comicTitle;
        this.mMenuTitle = menuTitle;
        this.mComicMenuId = menuId;
        this.mPicList = picList;
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RvViewHolder holder = new RvViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_comic_view, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(final RvViewHolder holder, int position) {
        final GlideUrl glideUrl = new GlideUrl(mPicList.get(position), new LazyHeaders.Builder()
                .addHeader("Host", "smp.yoedge.com")
                .addHeader("Referer", "http://smp.yoedge.com/smp-app/" + mComicMenuId + "/shinmangaplayer/index.html?__okraw")
                .build());
        Glide.with(mContext).asBitmap().load(glideUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                ViewGroup.LayoutParams layoutParams = holder.ivComicPic.getLayoutParams();
                layoutParams.height = DisplayUtil.getScreenWidth(mContext) * resource.getHeight() / resource.getWidth();
                layoutParams.width = DisplayUtil.getScreenWidth(mContext);
                Glide.with(mContext)
                        .load(glideUrl)
                        .apply(requestOptions)
                        .into(holder.ivComicPic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPicList.size();
    }

    static class RvViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_comic_pic)
        ImageView ivComicPic;

        RvViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
