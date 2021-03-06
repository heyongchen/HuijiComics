package com.huiji.comic.bobcat.huijicomics.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.activity.ComicViewActivity;
import com.huiji.comic.bobcat.huijicomics.activity.X5WebViewActivity;
import com.huiji.comic.bobcat.huijicomics.bean.ComicDataBean;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.utils.C;
import com.huiji.comic.bobcat.huijicomics.utils.IntentKey;
import com.huiji.comic.bobcat.huijicomics.utils.SPHelper;
import com.huiji.comic.bobcat.huijicomics.utils.SpKey;
import com.huiji.comic.bobcat.huijicomics.utils.UrlUtils;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeYongchen on 2017/7/28.
 */

public class ComicMenuAdapter extends RecyclerView.Adapter<ComicMenuAdapter.RvViewHolder> {

    private Context mContext;
    private ArrayList<ComicDataBean> mComicDataBeanList;
    private String mComicId;
    private String mComicTitle;
    private String mReadHistory;
    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());

    public ComicMenuAdapter(Context context, String comicId, String comicTitle, String history, ArrayList<ComicDataBean> comicDataBeanList) {
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
                Intent intent;
                if (SPHelper.get().get(SpKey.VIEW_TYPE, "").equals(C.VIEW_TYPE_NATIVE)) {
                    intent = new Intent(mContext, ComicViewActivity.class);
                } else {
                    intent = new Intent(mContext, X5WebViewActivity.class);
                }
                intent.putExtra(IntentKey.COMIC_ID, mComicId);
                intent.putExtra(IntentKey.COMIC_TITLE, mComicTitle);
                intent.putParcelableArrayListExtra(IntentKey.COMIC_BEAN, mComicDataBeanList);
                intent.putExtra(IntentKey.COMIC_MENU_POSITION, position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComicDataBeanList.size();
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
