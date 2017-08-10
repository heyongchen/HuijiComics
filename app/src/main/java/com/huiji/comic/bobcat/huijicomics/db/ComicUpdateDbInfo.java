package com.huiji.comic.bobcat.huijicomics.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by HeYongchen on 2017/8/9.
 */

@Table(name = "ComicUpdateDbInfo")
public class ComicUpdateDbInfo {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "comicId")
    private String comicId;
    @Column(name = "title")
    private String title;
    @Column(name = "isNew")
    private int isNew;

    public ComicUpdateDbInfo() {
    }

    public ComicUpdateDbInfo(String comicId, String title, int isNew) {
        this.comicId = comicId;
        this.title = title;
        this.isNew = isNew;
    }

    public String getComicId() {
        return comicId;
    }

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }
}
