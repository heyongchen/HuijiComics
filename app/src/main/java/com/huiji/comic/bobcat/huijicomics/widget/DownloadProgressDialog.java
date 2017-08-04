package com.huiji.comic.bobcat.huijicomics.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.utils.DisplayUtil;

/**
 * Created by HeYongchen on 2017/6/23.
 */

public class DownloadProgressDialog extends Dialog {

    private Context mContext;
    private View view;
    private TextView tv_title;
    private TextView tv_message;
    private TextView tv_progress;
    private ProgressBar pb_progress;

    public DownloadProgressDialog(Context context) {
        super(context, R.style.confirm_dialog);
        mContext = context;
        initView();
    }

    private void initView() {
        view = LayoutInflater.from(mContext).inflate(
                R.layout.download_progress_dialog_layout, null);
        tv_title = (TextView) view.findViewById(R.id.tv_download_progress_title);
        tv_message = (TextView) view.findViewById(R.id.tv_download_progress_message);
        tv_progress = (TextView) view.findViewById(R.id.tv_download_progress);
        pb_progress = (ProgressBar) view.findViewById(R.id.pb_download_progress);
        pb_progress.setMax(100);
        pb_progress.setIndeterminate(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (view != null) {
            setContentView(view);
            configDialog();
        }
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public DownloadProgressDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置内容
     *
     * @param message  消息内容
     * @param progress 安装进度
     * @return
     */
    public DownloadProgressDialog setMessage(String message, int progress) {
        if (!TextUtils.isEmpty(message)) {
            tv_message.setText(message);
        }
        pb_progress.setProgress(progress);
        tv_progress.setText(String.format(mContext.getString(R.string.tip_download_percent), progress));
        return this;
    }

    /**
     * 配置对话框
     */
    private void configDialog() {
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        //设置对话框居中
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DisplayUtil.getScreenWidth(mContext) * 3 / 4;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        //因为某些机型是虚拟按键的,所以要加上以下设置防止挡住按键.
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}
