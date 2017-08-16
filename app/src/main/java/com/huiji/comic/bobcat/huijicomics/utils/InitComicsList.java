package com.huiji.comic.bobcat.huijicomics.utils;

import com.huiji.comic.bobcat.huijicomics.bean.ComicDataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class InitComicsList {
    private static List<String> comicIdList = null;
    private static List<ComicDataBean> comicDataBeanList = null;

    public static void setComicDataBeanList(List<ComicDataBean> mComicDataBeanList) {
        if (comicDataBeanList == null) {
            comicDataBeanList = new ArrayList<>();
        } else {
            comicDataBeanList.clear();
        }
        comicDataBeanList = mComicDataBeanList;
    }

    public static void clearComicDataBeanList() {
        if (comicDataBeanList == null) {
            comicDataBeanList = new ArrayList<>();
        }
        comicDataBeanList.clear();
    }

    public static List<ComicDataBean> getComicDataBeanList() {
        if (comicDataBeanList == null) {
            comicDataBeanList = new ArrayList<>();
        }
        return comicDataBeanList;
    }
    public static List<String> getComicIdList() {
        if (comicIdList == null) {
            comicIdList = new ArrayList<>();
        }
        comicIdList.clear();
        initComicIdList();
        return comicIdList;
    }

    private static void initComicIdList() {
        //数字/英文
        comicIdList.add(String.valueOf(1001176));
        comicIdList.add(String.valueOf(1000531));
        comicIdList.add(String.valueOf(1000873));
        comicIdList.add(String.valueOf(1001435));
        comicIdList.add(String.valueOf(1001869));
        comicIdList.add(String.valueOf(1001101));
        //A
        //B
        comicIdList.add(String.valueOf(1001185));
        comicIdList.add(String.valueOf(1001179));
        comicIdList.add(String.valueOf(1002082));
        comicIdList.add(String.valueOf(1002218));
        //C
        comicIdList.add(String.valueOf(1000591));
        //D
        comicIdList.add(String.valueOf(1002866));
        //E
        //F
        //G
        comicIdList.add(String.valueOf(1001275));
        comicIdList.add(String.valueOf(1001678));
        comicIdList.add(String.valueOf(1002248));
        //H
        comicIdList.add(String.valueOf(1002186));
        comicIdList.add(String.valueOf(1001214));
        comicIdList.add(String.valueOf(1002458));
        comicIdList.add(String.valueOf(1003101));
        //I
        //J
        comicIdList.add(String.valueOf(1000546));
        comicIdList.add(String.valueOf(1001178));
        comicIdList.add(String.valueOf(1001183));
        comicIdList.add(String.valueOf(1002370));
        comicIdList.add(String.valueOf(1002685));
        comicIdList.add(String.valueOf(1003191));
        //K
        //L
        comicIdList.add(String.valueOf(1001684));
        comicIdList.add(String.valueOf(1001739));
        comicIdList.add(String.valueOf(1001899));
        //M
        comicIdList.add(String.valueOf(1001182));
        comicIdList.add(String.valueOf(1001587));
        comicIdList.add(String.valueOf(1001505));
        comicIdList.add(String.valueOf(1001824));
        comicIdList.add(String.valueOf(1001838));
        comicIdList.add(String.valueOf(1001949));
        comicIdList.add(String.valueOf(1002010));
        comicIdList.add(String.valueOf(1002326));
        comicIdList.add(String.valueOf(1002779));
        comicIdList.add(String.valueOf(1002798));
        //N
        comicIdList.add(String.valueOf(1002250));
        //O
        //P
        comicIdList.add(String.valueOf(1001249));
        comicIdList.add(String.valueOf(1001586));
        //Q
        comicIdList.add(String.valueOf(1001542));
        comicIdList.add(String.valueOf(1001018));
        comicIdList.add(String.valueOf(1001791));
        //R
        comicIdList.add(String.valueOf(1001501));
        comicIdList.add(String.valueOf(1000205));
        comicIdList.add(String.valueOf(1002873));
        comicIdList.add(String.valueOf(1003008));
        //S
        comicIdList.add(String.valueOf(1001175));
        comicIdList.add(String.valueOf(1003146));
        //T
        comicIdList.add(String.valueOf(1002020));
        //U
        //V
        //W
        comicIdList.add(String.valueOf(1002730));
        comicIdList.add(String.valueOf(1001666));
        comicIdList.add(String.valueOf(1001181));
        comicIdList.add(String.valueOf(1001553));
        comicIdList.add(String.valueOf(1001933));
        comicIdList.add(String.valueOf(1002097));
        comicIdList.add(String.valueOf(1002890));
        comicIdList.add(String.valueOf(1002932));
        //X
        comicIdList.add(String.valueOf(1002235));
        comicIdList.add(String.valueOf(1000834));
        comicIdList.add(String.valueOf(1001264));
        //Y
        comicIdList.add(String.valueOf(1000975));
        comicIdList.add(String.valueOf(1000618));
        comicIdList.add(String.valueOf(1001177));
        comicIdList.add(String.valueOf(1000865));
        comicIdList.add(String.valueOf(1001378));
        comicIdList.add(String.valueOf(1001459));
        comicIdList.add(String.valueOf(1001572));
        comicIdList.add(String.valueOf(1001951));
        comicIdList.add(String.valueOf(1001767));
        comicIdList.add(String.valueOf(1001950));
        comicIdList.add(String.valueOf(1002106));
        comicIdList.add(String.valueOf(1002534));
        comicIdList.add(String.valueOf(1002814));
        comicIdList.add(String.valueOf(1002484));
        comicIdList.add(String.valueOf(1002902));
        comicIdList.add(String.valueOf(1003790));
        //Z
        comicIdList.add(String.valueOf(1001598));
        comicIdList.add(String.valueOf(1001248));
        comicIdList.add(String.valueOf(1001184));
        comicIdList.add(String.valueOf(1001284));
        comicIdList.add(String.valueOf(1002009));
        comicIdList.add(String.valueOf(1002845));
        comicIdList.add(String.valueOf(1002870));
        comicIdList.add(String.valueOf(1001862));
        comicIdList.add(String.valueOf(1003737));
    }

}
