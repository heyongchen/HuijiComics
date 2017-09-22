package com.huiji.comic.bobcat.huijicomics.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.RequestOptions;
import com.huiji.comic.bobcat.huijicomics.R;

import java.util.List;

/**
 * Created by HeYongchen on 2017/9/22.
 */

public class ComicViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private String mComicId;
    private String mComicTitle;
    private String mMenuTitle;
    private String mComicUrl;
    private String mComicMenuId;
    private List<View> mViewList;
    private RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_placeholder);

    public ComicViewPagerAdapter(Context context, String comicTitle, String menuTitle, String menuId, List<View> viewList) {
        this.mContext = context;
        this.mComicTitle = comicTitle;
        this.mMenuTitle = menuTitle;
        this.mComicMenuId = menuId;
        this.mViewList = viewList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
