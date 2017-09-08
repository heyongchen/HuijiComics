package com.huiji.comic.bobcat.allcomics.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huiji.comic.bobcat.allcomics.MainApplication;
import com.huiji.comic.bobcat.allcomics.R;
import com.huiji.comic.bobcat.allcomics.activity.X5WebViewActivity;
import com.huiji.comic.bobcat.allcomics.bean.ComicDataBean;
import com.huiji.comic.bobcat.allcomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.allcomics.utils.IntentKey;
import com.huiji.comic.bobcat.allcomics.utils.UrlUtils;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

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
    private String mReadHistory;
    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());

    public ComicMenuAdapter(Context context, String comicId, String comicTitle, String history, List<ComicDataBean> comicDataBeanList) {
        this.mContext = context;
        this.mComicId = comicId;
        this.mComicTitle = comicTitle;
        this.mReadHistory = history;
        this.mComicDataBeanList = comicDataBeanList;
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RvViewHolder holder = new RvViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_comic_menu, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, final int position) {
        holder.tvComicMenu.setBackground(getBackGround(position));
        holder.tvComicMenu.setText(mComicDataBeanList.get(position).getDataTitle());
        holder.tvComicMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHistory(mComicDataBeanList.get(position).getDataUrl());
                Intent intent = new Intent(mContext, X5WebViewActivity.class);
                intent.putExtra(IntentKey.COMIC_ID, mComicId);
                intent.putExtra(IntentKey.COMIC_TITLE, mComicTitle);
                intent.putExtra(IntentKey.COMIC_VIEW_URL, mComicDataBeanList.get(position).getDataUrl());
                intent.putExtra(IntentKey.COMIC_MENU_TITLE, mComicDataBeanList.get(position).getDataTitle());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComicDataBeanList.size() - 2;
    }

    private Drawable getBackGround(int position) {
        Drawable backGround;
        if (mReadHistory != null && !mReadHistory.isEmpty() && mReadHistory.equals(UrlUtils.replaceHost(mComicDataBeanList.get(position).getDataUrl()))) {
            backGround = ContextCompat.getDrawable(mContext, R.drawable.item_menu_background_2);
        } else {
            backGround = ContextCompat.getDrawable(mContext, R.drawable.item_menu_background);
        }
        return backGround;
    }

    static class RvViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_comic_menu)
        TextView tvComicMenu;

        RvViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void addHistory(String comicUrl) {
        if (!comicUrl.isEmpty()) {
            WhereBuilder b = WhereBuilder.b();
            b.and("comicId", "=", mComicId);//条件
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


    public void setReadHistory(String readHistory) {
        this.mReadHistory = readHistory;
        notifyDataSetChanged();
    }
}
