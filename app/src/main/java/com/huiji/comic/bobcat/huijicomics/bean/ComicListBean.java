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
    private String sortLetter;

    public ComicListBean() {
    }

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

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }
}
