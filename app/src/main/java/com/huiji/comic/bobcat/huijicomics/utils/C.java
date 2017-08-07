package com.huiji.comic.bobcat.huijicomics.utils;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class C {

    public static boolean hasNewComic = false;
    public static final int DATABASE_VERSION = 2;

    public static String getComicMenuUrl(String comicId) {
        return "http://smp.yoedge.com/view/omnibus/" + comicId;
    }

    public static String getComicDataUrl(String comicMenuId) {
        return "http://smp.yoedge.com/smp-app/" + comicMenuId + "/shinmangaplayer/index.html";
    }

    public static String getComicPicUrl(String urlHost, String comicDataId, String comicTitle, String menu, String page) {
        return urlHost.replace("index.html", "") + "res/pages/" + menu + "/" + page + ".jpg";
    }
}
