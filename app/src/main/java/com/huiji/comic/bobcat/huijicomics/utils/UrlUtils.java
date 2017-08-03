package com.huiji.comic.bobcat.huijicomics.utils;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.bean.ComicDataBean;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class UrlUtils {

    private static DbManager dbManager = x.getDb(MainApplication.getDbConfig());

    public static void getMenuList(final List<String> comicIdList, final RequestStateListener requestStateListener, final boolean update) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!update) {
                    try {
                        dbManager.delete(ComicListDbInfo.class);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                List<ComicListDbInfo> comicListDbInfoList = new ArrayList<ComicListDbInfo>();
                for (String comicId : comicIdList) {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(C.getComicMenuUrl(comicId)).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (doc != null) {

                        Elements links = doc.select("a[href]");
                        Elements media = doc.select("[src]");
                        Elements titleOrAuthor = doc.select("abbr");
                        Element msgDiv = doc.select("div.am-u-sm-8").first();

                        String imgUrl = "";
                        String title = titleOrAuthor.get(0).text();
                        String author = titleOrAuthor.get(1).text();
                        String msg = msgDiv.text().replace(title, "").replace(author, "").replaceAll(" ", "").trim();

                        print("\nMedia: (%d)", media.size());
                        for (Element src : media) {
                            if (src.tagName().equals("img"))
                                imgUrl = src.attr("abs:src");
                        }
                        print("\nLinks: (%d)", links.size());
                        int num = links.size();

                        comicListDbInfoList.add(new ComicListDbInfo(comicId, title, author, msg, imgUrl, num, "0"));
                    }
                }
                try {
                    dbManager.save(comicListDbInfoList);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (requestStateListener != null) {
                    requestStateListener.ok();
                }
            }
        }).start();
    }

    public interface RequestStateListener {
        void ok();
    }

    public static void getLinkList(final String comicId, final RequestDataListener requestDataListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InitComicsList.clearComicListBeanList();
                Document doc = null;
                try {
                    doc = Jsoup.connect(C.getComicMenuUrl(comicId)).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (doc != null) {
                    Elements links = doc.select("a[href]");

                    List<ComicDataBean> comicDataBeanList = new ArrayList<>();
                    print("\nLinks: (%d)", links.size());
                    for (Element link : links) {
                        comicDataBeanList.add(new ComicDataBean(link.attr("abs:href"), trim(link.text(), 35)));
                    }
                    InitComicsList.setComicDataBeanList(comicDataBeanList);
                }

                if (requestDataListener != null) {
                    requestDataListener.ok();
                }
            }
        }).start();
    }

    public interface RequestDataListener {
        void ok();
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }
}
