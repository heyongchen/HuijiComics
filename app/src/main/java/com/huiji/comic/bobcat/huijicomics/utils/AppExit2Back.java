package com.huiji.comic.bobcat.huijicomics.utils;

import android.content.Context;
import android.widget.Toast;

import com.huiji.comic.bobcat.huijicomics.base.manager.AppManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HeYongchen on 2017/7/31.
 */

public class AppExit2Back {

    private static Boolean isExit = false;

    /**
     * 退出App程序应用
     *
     * @param context 上下文
     * @return boolean True退出|False提示
     */
    public static boolean exitApp(Context context) {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            //信息提示
            Toast.makeText(context, "再次点击返回键退出应用", Toast.LENGTH_SHORT).show();
            //创建定时器
            tExit = new Timer();
            //如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    //取消退出
                    isExit = false;
                }
            }, 2000);
        } else {
            AppManager.get().quit();
        }
        return isExit;
    }
}
