package com.huiji.comic.bobcat.huijicomics.utils;

import android.util.Log;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.bean.ComicDataBean;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListBean;
import com.huiji.comic.bobcat.huijicomics.bean.ComicUpdateBean;
import com.huiji.comic.bobcat.huijicomics.db.ComicListDbInfo;
import com.huiji.comic.bobcat.huijicomics.db.ComicUpdateDbInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class UrlUtils {

    private static final String TAG = "UrlUtils";
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
                if (comicIdList != null && comicIdList.size() > 0) {
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
                InitComicsList.clearComicDataBeanList();
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

    public static void checkUpdateList(final List<ComicUpdateBean> comicIdList, final CheckUpdateListener checkUpdateListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ComicUpdateBean comicUpdateBean : comicIdList) {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(C.getComicMenuUrl(comicUpdateBean.getComicId())).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (doc != null) {
                        Elements links = doc.select("a[href]");
                        int comicNum = links.size();
                        if (checkUpdate(comicUpdateBean.getComicId(), comicNum) == 1) {
                            changeNew(comicUpdateBean.getComicId());
                        }
                    }
                }
                if (checkUpdateListener != null) {
                    checkUpdateListener.ok();
                }
            }
        }).start();
    }

    private static int checkUpdate(String comicId, int comicNum) {
        ComicListDbInfo result = null;
        WhereBuilder b = WhereBuilder.b();
        b.and("comicId", "=", comicId);
        try {
            result = dbManager.selector(ComicListDbInfo.class).where(b).findFirst();//查询
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (result != null) {
            if (result.getMenuNum() < comicNum) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private static void changeNew(String comicId) {
        WhereBuilder b = WhereBuilder.b();
        b.and("comicId", "=", comicId);//条件
        KeyValue isNew = new KeyValue("isNew", "1");
        try {
            dbManager.update(ComicUpdateDbInfo.class, b, isNew);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public interface CheckUpdateListener {
        void ok();
    }

    public static void checkLink(final String comicId, final checkDataListener checkDataListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ComicListBean comicListBean = new ComicListBean();
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
                    comicListBean = new ComicListBean(comicId, imgUrl, title, author, msg);
                }

                if (checkDataListener != null) {
                    checkDataListener.ok(comicListBean);
                }
            }
        }).start();
    }

    public interface checkDataListener {
        void ok(ComicListBean comicListBean);
    }

    private static void print(String msg, Object... args) {
        Log.i(TAG, String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }
}
