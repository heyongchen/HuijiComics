package com.huiji.comic.bobcat.huijicomics.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.activity.ComicMenuActivity;
import com.huiji.comic.bobcat.huijicomics.activity.ComicViewActivity;
import com.huiji.comic.bobcat.huijicomics.activity.X5WebViewActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicDataBean;
import com.huiji.comic.bobcat.huijicomics.utils.SpKey;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeYongchen on 2017/7/28.
 */

public class ComicMenuAdapter extends RecyclerView.Adapter<ComicMenuAdapter.RvViewHolder> {

    private Context mContext;
    private List<ComicDataBean> mComicDataBeanList;
    private String mComicId;
    private String mComicTitle;

    public ComicMenuAdapter(Context context, String comicId, String comicTitle, List<ComicDataBean> comicDataBeanList) {
        this.mContext = context;
        this.mComicId = comicId;
        this.mComicTitle = comicTitle;
        this.mComicDataBeanList = comicDataBeanList;
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RvViewHolder holder = new RvViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_comic_menu, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, final int position) {
        holder.tvComicMenu.setText(mComicDataBeanList.get(position).getDataTitle());
        holder.tvComicMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, X5WebViewActivity.class);
                intent.putExtra(SpKey.COMIC_ID, mComicId);
                intent.putExtra(SpKey.COMIC_TITLE, mComicTitle);
                intent.putExtra(SpKey.COMIC_VIEW_URL, mComicDataBeanList.get(position).getDataUrl());
                intent.putExtra(SpKey.COMIC_MENU_TITLE, mComicDataBeanList.get(position).getDataTitle());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComicDataBeanList.size() - 2;
    }

    static class RvViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_comic_menu)
        TextView tvComicMenu;

        RvViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
