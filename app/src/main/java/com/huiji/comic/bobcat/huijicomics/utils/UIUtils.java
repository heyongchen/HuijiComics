package com.huiji.comic.bobcat.huijicomics.utils;

import android.content.Context;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huiji.comic.bobcat.huijicomics.MainApplication;

/**
 * Created by xz on 2017/2/20.
 * 常用工具类
 */

public class UIUtils {
    private static MainApplication mApp;

    /**
     * sp转px
     */
    public static int sp2px(Context context, float spVal) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public static void init(MainApplication app) {
        mApp = app;
    }

    /**
     * px转sp
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * dip转换px
     */
    public static int dip2px(float dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转换dip
     */
    public static int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 解析resId至视图(保持resid自带的layoutparam参数)
     *
     * @param resId        资源Id
     * @param root         根目录（已经依附在window上的父视图）
     * @param attachToRoot 是否追加到root视图上
     * @return 返回解析后的视图
     */
    public static View inflate(int resId, ViewGroup root, boolean attachToRoot) {
        if (root != null && root.getContext() != null) {
            return LayoutInflater.from(root.getContext()).inflate(resId, root, attachToRoot);
        }
        return LayoutInflater.from(getContext()).inflate(resId, root, attachToRoot);
    }

    /**
     * 解析resId为视图 (过滤了resId视图自带layoutparam参数)
     *
     * @param resId 资源Id
     * @return 返回解析后的视图
     */
    public static View inflate(int resId) {
        return LayoutInflater.from(getContext()).inflate(resId, null);
    }

    /**
     * 获取颜色值
     *
     * @return
     */
    public static int getColor(int resId) {
        return getContext().getResources().getColor(resId);
    }

    /**
     * @return 全局上下文
     */
    public static Context getContext() {
        return mApp;
    }

    /**
     * @return 获取app
     */
    public static MainApplication getApp() {
        return mApp;
    }

    /**
     * 防止连续点击的时间间隔
     */
    public static final long INTERVAL = 500L;
    /**
     * 上一次点击的时间
     */
    private static long lastClickTime = 0L;

    /**
     * 判断按钮是否双击
     *
     * @return true : 表示双击
     */
    public static boolean isDoubleClick() {
        long time = SystemClock.uptimeMillis();

        if ((time - lastClickTime) < INTERVAL) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static int getStatusbarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
