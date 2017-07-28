package com.huiji.comic.bobcat.huijicomics.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huiji.comic.bobcat.huijicomics.R;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class T {

    private static Context mContext;
    private static T mInstance;
    private static Toast mToast;
    private static LayoutInflater mInflater;

    /**
     * 单例模式
     *
     * @param context
     * @return
     */
    public static T get(Context context) {
        if (mInstance == null || mToast == null) {
            synchronized (T.class) {
                mContext = context;
                mInstance = new T();
                mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                mInflater = LayoutInflater.from(mContext);
            }
        }
        return mInstance;
    }

    /**
     * 默认Toast
     *
     * @param msg
     */
    public void toast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        //获取自定义布局
        View view = mInflater.inflate(R.layout.toast_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_content);
        //设置文本
        tv.setText(msg);
        mToast.setView(view);
        mToast.show();
    }

}
