package com.huiji.comic.bobcat.huijicomics.bean;

/**
 * Created by HeYongchen on 2017/8/9.
 */

public class ComicUpdateBean {

    private String comicId;
    private String comicTitle;
    private int isNew;

    public ComicUpdateBean(String comicId, String comicTitle) {
        this.comicId = comicId;
        this.comicTitle = comicTitle;
    }

    public String getComicId() {
        return comicId;
    }

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public String getComicTitle() {
        return comicTitle;
    }

    public void setComicTitle(String comicTitle) {
        this.comicTitle = comicTitle;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }
}
