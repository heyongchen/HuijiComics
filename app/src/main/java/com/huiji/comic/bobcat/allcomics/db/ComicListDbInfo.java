package com.huiji.comic.bobcat.allcomics.db;

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
    private int isCollect;
    @Column(name = "lastReadTime")
    private long lastReadTime;
    @Column(name = "lastReadUrl")
    private String lastReadUrl;
    @Column(name = "menuNum")
    private int menuNum;
    @Column(name = "isNew")
    private int isNew;

    public ComicListDbInfo() {
    }

    public ComicListDbInfo(String comicId, String title, String author, String msg, String imgUrl, int menuNum, int isCollect, int isNew) {
        this.comicId = comicId;
        this.title = title;
        this.author = author;
        this.msg = msg;
        this.imgUrl = imgUrl;
        this.menuNum = menuNum;
        this.isCollect = isCollect;
        this.isNew = isNew;
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

    public int getMenuNum() {
        return menuNum;
    }

    public String getLastReadUrl() {
        return lastReadUrl;
    }
}
