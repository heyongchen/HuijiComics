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
import com.huiji.comic.bobcat.huijicomics.bean.ComicUpdateBean;
import com.huiji.comic.bobcat.huijicomics.utils.IntentKey;
import com.huiji.comic.bobcat.huijicomics.widget.ImageShowDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class ComicListAdapter extends RecyclerView.Adapter<ComicListAdapter.RvViewHolder> {

    private Context mContext;
    private boolean mSort = false;
    private List<ComicListBean> mComicListBeanList;
    private List<ComicUpdateBean> mComicUpdateList = new ArrayList<>();

    public ComicListAdapter(Context context, List<ComicListBean> comicListBeanList, boolean sort) {
        this.mContext = context;
        this.mComicListBeanList = comicListBeanList;
        this.mSort = sort;
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
            holder.ivComicView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageShowDialog dialog = new ImageShowDialog(mContext, mComicListBeanList.get(position).getImgUrl());
                    dialog.show();
                }
            });
        }
        holder.tvComicTitle.setText(mComicListBeanList.get(position).getTitle() != null ? mComicListBeanList.get(position).getTitle() : "");
        holder.tvComicAuthor.setText(mComicListBeanList.get(position).getAuthor() != null ? mComicListBeanList.get(position).getAuthor() : "");
        holder.tvComicMsg.setText(mComicListBeanList.get(position).getMsg() != null ? mComicListBeanList.get(position).getMsg() : "");
        holder.llItemComicList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ComicMenuActivity.class);
                intent.putExtra(IntentKey.COMIC_ID, mComicListBeanList.get(position).getComicId());
                intent.putExtra(IntentKey.COMIC_TITLE, mComicListBeanList.get(position).getTitle());
                intent.putExtra(IntentKey.COMIC_AUTHOR, mComicListBeanList.get(position).getAuthor());
                intent.putExtra(IntentKey.COMIC_MSG, mComicListBeanList.get(position).getMsg());
                intent.putExtra(IntentKey.COMIC_IMG, mComicListBeanList.get(position).getImgUrl());
                mContext.startActivity(intent);
            }
        });
        if (mComicUpdateList.size() > 0) {
            if (checkNew(mComicListBeanList.get(position).getComicId())) {
                holder.tvTipNew.setVisibility(View.VISIBLE);
            } else {
                holder.tvTipNew.setVisibility(View.INVISIBLE);
            }
        }
        if (mSort) {
            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                holder.llSortLetter.setVisibility(View.VISIBLE);
                holder.tvSortLetter.setText(mComicListBeanList.get(position).getSortLetter());
            } else {
                holder.llSortLetter.setVisibility(View.GONE);
            }
        }
    }

    private boolean checkNew(String comicId) {
        for (ComicUpdateBean comicUpdateBean : mComicUpdateList) {
            if (comicUpdateBean.getComicId().equals(comicId)) {
                return true;
            }
        }
        return false;
    }

    public void setComicUpdateList(List<ComicUpdateBean> list) {
        mComicUpdateList = list;
        notifyDataSetChanged();
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mComicListBeanList.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mComicListBeanList.get(position).getSortLetter().charAt(0);
    }

    @Override
    public int getItemCount() {
        return mComicListBeanList.size();
    }

    public void updateListView(List<ComicListBean> filterDateList) {
        this.mComicListBeanList = filterDateList;
        notifyDataSetChanged();
    }

    static class RvViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_sort_letter)
        TextView tvSortLetter;
        @BindView(R.id.ll_sort_letter)
        LinearLayout llSortLetter;
        @BindView(R.id.iv_comic_view)
        ImageView ivComicView;
        @BindView(R.id.tv_comic_title)
        TextView tvComicTitle;
        @BindView(R.id.tv_tip_new)
        TextView tvTipNew;
        @BindView(R.id.tv_comic_author)
        TextView tvComicAuthor;
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
