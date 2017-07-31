package com.huiji.comic.bobcat.huijicomics.bean;

import java.util.List;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class ComicListBean {
    private String comicId;
    private String imgUrl;
    private String title;
    private String author;
    private String msg;

    public ComicListBean(String comicId, String imgUrl, String title, String author, String msg) {
        this.comicId = comicId;
        this.imgUrl = imgUrl;
        this.title = title;
        this.author = author;
        this.msg = msg;
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

    public String getAuthor() {
        return author;
    }

    public String getMsg() {
        return msg;
    }
}
