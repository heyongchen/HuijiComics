package com.huiji.comic.bobcat.huijicomics.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by HeYongchen on 2017/7/28.
 */

@Table(name = "ComicListInfo")
public class ComicListDbInfo {

    @Column(name = "id", isId = true)
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
    @Column(name = "isCollect")
    private String isCollect;

    public ComicListDbInfo() {
    }

    public ComicListDbInfo(String comicId, String title, String imgUrl, String isCollect) {
        this.comicId = comicId;
        this.title = title;
        this.imgUrl = imgUrl;
        this.isCollect = isCollect;
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

    public String getIsCollect() {
        return isCollect;
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
                ", isCollect='" + isCollect + '\'' +
                '}';
    }
}
