package com.huiji.comic.bobcat.huijicomics.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by HeYongchen on 2017/7/28.
 */

@Table(name = "ComicListInfo")
public class ComicListDbInfo {

    @Column(name = "id", isId = true, property = "NOT NULL")
    private int id;
    @Column(name = "comicId")
    private String comicId;
    @Column(name = "title")
    private String title;
    @Column(name = "imgUrl")
    private String imgUrl;
    @Column(name = "author")
    private String author;
    @Column(name = "msg")
    private String msg;

    public ComicListDbInfo() {
    }

    public ComicListDbInfo(String comicId, String title, String imgUrl) {
        this.comicId = comicId;
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public ComicListDbInfo(String comicId, String title, String imgUrl, String author, String msg) {
        this.comicId = comicId;
        this.title = title;
        this.imgUrl = imgUrl;
        this.author = author;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public String getComicId() {
        return comicId;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ComicListDbInfo{" +
                "id=" + id +
                ", comicId='" + comicId + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", author='" + author + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
