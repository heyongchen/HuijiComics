package com.huiji.comic.bobcat.huijicomics.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.activity.ComicMenuActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListBean;
import com.huiji.comic.bobcat.huijicomics.utils.IntentKey;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class ComicListAdapter extends RecyclerView.Adapter<ComicListAdapter.RvViewHolder> {


    private Context mContext;
    private List<ComicListBean> mComicListBeanList;

    public ComicListAdapter(Context context, List<ComicListBean> comicListBeanList) {
        this.mContext = context;
        this.mComicListBeanList = comicListBeanList;
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RvViewHolder holder = new RvViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_comic_list, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, final int position) {
        if (mComicListBeanList.get(position).getImgUrl() != null) {
            Glide.with(mContext)
                    .load(mComicListBeanList.get(position).getImgUrl())
                    .into(holder.ivComicView);
        }
        holder.tvComicTitle.setText(mComicListBeanList.get(position).getTitle() != null ? mComicListBeanList.get(position).getTitle() : "");
        holder.tvComicMsg.setText(mComicListBeanList.get(position).getMsg() != null ? mComicListBeanList.get(position).getMsg() : "");
        holder.llItemComicList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ComicMenuActivity.class);
                intent.putExtra(IntentKey.COMIC_ID, mComicListBeanList.get(position).getComicId());
                intent.putExtra(IntentKey.COMIC_TITLE, mComicListBeanList.get(position).getTitle());
                intent.putExtra(IntentKey.COMIC_IMG, mComicListBeanList.get(position).getImgUrl());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComicListBeanList.size();
    }

    static class RvViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_comic_view)
        ImageView ivComicView;
        @BindView(R.id.tv_comic_title)
        TextView tvComicTitle;
        @BindView(R.id.tv_comic_msg)
        TextView tvComicMsg;
        @BindView(R.id.ll_item_comic_list)
        LinearLayout llItemComicList;

        RvViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
