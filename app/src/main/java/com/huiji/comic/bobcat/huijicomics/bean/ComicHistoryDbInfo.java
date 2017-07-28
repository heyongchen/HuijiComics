package com.huiji.comic.bobcat.huijicomics.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by HeYongchen on 2017/7/28.
 */

@Table(name = "ComicHistoryInfo")
public class ComicHistoryDbInfo {

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
    @Column(name = "readHistory")
    private String readHistory;

    public ComicHistoryDbInfo() {
    }

    public ComicHistoryDbInfo(String comicId, String title, String imgUrl, String readHistory) {
        this.comicId = comicId;
        this.title = title;
        this.imgUrl = imgUrl;
        this.readHistory = readHistory;
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

    public String getReadHistory() {
        return readHistory;
    }

    @Override
    public String toString() {
        return "ComicHistoryDbInfo{" +
                "id=" + id +
                ", comicId='" + comicId + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", author='" + author + '\'' +
                ", msg='" + msg + '\'' +
                ", readHistory='" + readHistory + '\'' +
                '}';
    }
}
