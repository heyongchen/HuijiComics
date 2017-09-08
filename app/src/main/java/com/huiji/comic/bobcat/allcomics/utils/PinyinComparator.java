package com.huiji.comic.bobcat.allcomics.utils;

import com.huiji.comic.bobcat.allcomics.bean.ComicListBean;

import java.util.Comparator;

/**
 * Created by HeYongchen on 2017/7/31.
 */

public class PinyinComparator implements Comparator<ComicListBean> {

    public int compare(ComicListBean o1, ComicListBean o2) {
        if (o1.getSortLetter().equals("@")
                || o2.getSortLetter().equals("#")) {
            return -1;
        } else if (o1.getSortLetter().equals("#")
                || o2.getSortLetter().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetter().compareTo(o2.getSortLetter());
        }
    }

}
