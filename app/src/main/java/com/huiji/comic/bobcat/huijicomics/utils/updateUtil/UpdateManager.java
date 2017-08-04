package com.huiji.comic.bobcat.huijicomics.utils.updateUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.utils.AppUtils;
import com.huiji.comic.bobcat.huijicomics.utils.PathUtil;
import com.huiji.comic.bobcat.huijicomics.widget.ConfirmDialog;
import com.huiji.comic.bobcat.huijicomics.widget.DownloadProgressDialog;
import com.huiji.comic.bobcat.huijicomics.widget.TipDialog;

/**
 * Created by HeYongchen on 2017/6/21.
 */
public class UpdateManager implements DownloadApkUtil.DownloadListener {

    private Context mContext; //上下文
    private static final String savePath = PathUtil.getDownloadPath(); //apk保存到SD卡的路径
    private String saveFileName = savePath + "apkName.apk"; //完整路径名
    private boolean forceUpdate;
    private UpdateAppResultListener updateAppResultListener;
    private DownloadApkUtil downloadApkUtil; //下载apk协助类
    private DownloadProgressDialog progressDialog;
    private Notification.Builder builder;
    private NotificationManager manager;
    private long lastTime = 0;
    public static final int NOTIFICATION_ID = 101;//下载进度通知id

    /**
     * 构造函数
     */
    public UpdateManager(Context context, boolean forceUpdate, @NonNull String apkUrl, @NonNull String apkName,
                         @NonNull String updateTitle, @NonNull String updateDescription,
                         @NonNull String buttonCancel, @NonNull String buttonOk, UpdateAppResultListener listener) {
        mContext = context;
        this.forceUpdate = forceUpdate;
        String fileName = "apkName.apk";
        if (!apkName.equals("")) {
            fileName = apkName + ".apk";
        }
        saveFileName = savePath + "/" + fileName;
        updateAppResultListener = listener;

        downloadApkUtil = new DownloadApkUtil(mContext, this, apkUrl, savePath, saveFileName);
        //弹框下载
        showNoticeDialog(updateTitle, updateDescription, buttonCancel, buttonOk, forceUpdate);
    }

    /**
     * 开始下载安装包
     */
    private void startDownloadApk() {
        showDialog(mContext.getString(R.string.tip_download_prepare), 0);
        //下载apk
        if (downloadApkUtil != null) {
            downloadApkUtil.downloadAPK(mContext);
        }
    }


    @Override
    public void downloadProgress(int progress, String currentLength, String apkLength) {
        showDialog(String.format(mContext.getString(R.string.tip_download_progress), currentLength, apkLength), progress);
    }

    @Override
    public void downloadResult(boolean isSuccess) {
        if (isSuccess && forceUpdate) {
            //强制更新，保持一直转圈，不能进行后面流程
            showDialog(mContext.getString(R.string.tip_download_success_dialog), 100);
        } else {
            hideDialog();
        }
        if (updateAppResultListener != null) {
            if (isSuccess) {
                updateAppResultListener.updateToNext();
            } else {
                String err = "";
                if (forceUpdate) {
                    err = mContext.getString(R.string.tip_download_error_force_update);
                } else {
                    err = mContext.getString(R.string.tip_download_error);
                }

                showSimpleDialog(err);

                Notification.Builder builder = new Notification.Builder(mContext);
                builder.setSmallIcon(R.drawable.icon)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(AppUtils.getAppName())
                        .setContentText(err)
                        .setAutoCancel(true);
                Notification notification = builder.build();
                NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify((int) System.currentTimeMillis(), notification);

                updateAppResultListener.updateFailed(forceUpdate);
            }
        }
    }

    private void showSimpleDialog(String msg) {
        new AlertDialog.Builder(mContext)
                .setTitle(AppUtils.getAppName())
                .setMessage(msg)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * apk更新状态监听
     */
    public interface UpdateAppResultListener {

        void updateToNext();

        void updateFailed(boolean forceUpdate);
    }

    /**
     * 显示下载进度提示框
     *
     * @param msg      消息内容
     * @param progress 下载进度
     */
    private void showDialog(String msg, int progress) {
        try {//通知栏进度显示
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime > 200) {
                lastTime = currentTime;
                if (builder == null) {
                    builder = new Notification.Builder(mContext);
                    builder.setSmallIcon(R.drawable.icon)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle(AppUtils.getAppName())
                            .setContentText(msg)
                            .setProgress(100, progress, false)
                            .setAutoCancel(false);
                    Notification notification = builder.build();
                    manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(NOTIFICATION_ID, notification);
                } else {
                    builder.setProgress(100, progress, false)
                            .setContentText(msg);
                    manager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {//Dialog进度显示框
            if (progressDialog == null || !progressDialog.isShowing()) {
                progressDialog = new DownloadProgressDialog(mContext);
                progressDialog.setTitle(mContext.getString(R.string.tip_update_title));
                progressDialog.setMessage(msg, progress);
                progressDialog.setCancelable(false);
                progressDialog.show();
            } else if (progress >= 100) {
                progressDialog.setMessage(msg, 100);
            } else {
                progressDialog.setMessage(msg, progress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏下载进度提示框
     */
    private void hideDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示更新对话框
     *
     * @param title        更新标题
     * @param msg          更新文案
     * @param buttonCancel 取消按钮
     * @param buttonOk     确定按钮
     * @param forceUpdate  是否强制更新
     */
    private void showNoticeDialog(String title, String msg, String buttonCancel, String buttonOk, boolean forceUpdate) {
        if (!forceUpdate) {//判断是否为强制更新
            final ConfirmDialog dialog = new ConfirmDialog(mContext);
            dialog.setTitle(title);
            dialog.setMessage(msg);
            dialog.setButtonCancelText(buttonCancel);
            dialog.setButtonOKText(buttonOk);
            dialog.setOnConfirmListener(new ConfirmDialog.OnConfirmListener() {
                @Override
                public void onCancel() {
                    dialog.dismiss();
                }

                @Override
                public void onOK() {
                    //下载新版本
                    dialog.dismiss();
                    startDownloadApk();
                }
            });
            dialog.show();
        } else {//强制更新只有开始下载按钮
            final TipDialog dialog = new TipDialog(mContext);
            dialog.setTitle(title);
            dialog.setMessage(msg);
            dialog.setButtonOKText(buttonOk);
            dialog.setCancelable(false);
            dialog.setOnTipListener(new TipDialog.OnTipListener() {
                @Override
                public void onOK() {
                    //下载新版本
                    dialog.dismiss();
                    startDownloadApk();
                }
            });
            dialog.show();
        }
    }
}
