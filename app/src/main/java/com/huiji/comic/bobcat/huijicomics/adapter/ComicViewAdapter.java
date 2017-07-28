package com.huiji.comic.bobcat.huijicomics.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.utils.C;

import java.util.List;

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
    private List<String> mPicList;

    public ComicViewAdapter(Context context, String comicUrl, String comicId, String comicTitle, String menuTitle, List<String> picList) {
        this.mContext = context;
        this.mComicUrl = comicUrl;
        this.mComicId = comicId;
        this.mComicTitle = comicTitle;
        this.mMenuTitle = menuTitle;
        this.mPicList = picList;
    }


    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RvViewHolder holder = new RvViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_comic_view, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mPicList.get(position))
                .into(holder.ivComicPic);
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
