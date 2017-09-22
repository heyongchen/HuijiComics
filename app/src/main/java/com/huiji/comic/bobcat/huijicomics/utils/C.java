package com.huiji.comic.bobcat.huijicomics.utils;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class C {

    public static boolean hasNewComic = false;
    public static final String VIEW_TYPE_WEB = "viewTypeWeb";
    public static final String VIEW_TYPE_NATIVE = "viewTypeNative";
    public static final int DATABASE_VERSION = 3;

    public static class viewType {
        public static final String FLOW = "flow";
        public static final String PAGE = "page";
    }

    public static String getComicMenuUrl(String comicId) {
        return "http://smp.yoedge.com/view/omnibus/" + comicId;
    }

    public static String getComicDataUrl(String comicMenuId) {
        return "http://smp.yoedge.com/smp-app/" + comicMenuId + "/shinmangaplayer/index.html";
    }

    public static String getComicPicUrl(String urlHost, String urlFooter) {
        return urlHost.replace("index.html", "") + urlFooter;
    }

    public static String getComicDataJson(String comicMenuId) {
        return "http://smp.yoedge.com/smp-app/" + comicMenuId + "/shinmangaplayer/smp_cfg.json";
    }
}
