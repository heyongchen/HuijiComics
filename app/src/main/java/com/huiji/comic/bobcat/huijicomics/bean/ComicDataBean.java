package com.huiji.comic.bobcat.huijicomics.bean;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class ComicDataBean {
    private String dataUrl;
    private String dataTitle;

    public ComicDataBean(String dataUrl, String dataTitle) {
        this.dataUrl = dataUrl;
        this.dataTitle = dataTitle;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public String getDataTitle() {
        return dataTitle;
    }
}
