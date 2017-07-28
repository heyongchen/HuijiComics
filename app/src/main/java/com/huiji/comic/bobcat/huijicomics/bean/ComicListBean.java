package com.huiji.comic.bobcat.huijicomics.bean;

import java.util.List;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class ComicListBean {
    private String comicId;
    private String imgUrl;
    private String title;
    private String msg;

    public ComicListBean(String comicId, String imgUrl, String title) {
        this.comicId = comicId;
        this.imgUrl = imgUrl;
        this.title = title;
    }

    public String getComicId() {
        return comicId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }
}
