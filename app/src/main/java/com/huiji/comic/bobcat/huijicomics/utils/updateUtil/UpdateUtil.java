package com.huiji.comic.bobcat.huijicomics.utils.updateUtil;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.base.manager.PermissionManager;
import com.huiji.comic.bobcat.huijicomics.utils.AppUtils;
import com.huiji.comic.bobcat.huijicomics.utils.PathUtil;
import com.huiji.comic.bobcat.huijicomics.utils.SPHelper;
import com.huiji.comic.bobcat.huijicomics.utils.SpKey;
import com.huiji.comic.bobcat.huijicomics.widget.ConfirmDialog;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import java.io.File;

/**
 * 更新工具类
 * Created by wangzhen on 2017/3/15.
 * 修改人：何泳郴
 * 修改日期：2017/6/26
 */

public class UpdateUtil {

    public static void check(final Activity context, final boolean tip) {

        PgyUpdateManager.register(context, null, new UpdateManagerListener() {

            @Override
            public void onUpdateAvailable(final String result) {
                // 将新版本信息封装到AppBean中
                final AppBean appBean = getAppBeanFromString(result);
                if (tip || !appBean.getVersionCode().equals(SPHelper.get().get(SpKey.IGNORE_VERSION, ""))) {
                    PermissionManager.OnBaseCallback callback = (PermissionManager.OnBaseCallback) context;
                    final String apkName = AppUtils.getAppName() + "_V" + appBean.getVersionName();
                    //标题
                    final String title = context.getString(R.string.tip_update_title);
                    //提示内容
                    final String message = appBean.getReleaseNote();
                    //取消按钮文本
                    final String buttonCancel = context.getString(R.string.tip_update_next_time);
                    //确定按钮文本
                    final String buttonOk = context.getString(R.string.tip_update_now);

                    PermissionManager.requestPermission(callback, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionManager.OnPermissionCallback() {
                        @Override
                        public void onGranted() {
                            //开始下载
                            new ConfirmDialog(context, true)
                                    .setTitle(title)
                                    .setMessage(message)
                                    .setButtonCancelText(buttonCancel)
                                    .setButtonOKText(buttonOk)
                                    .setOnConfirmListener(new ConfirmDialog.OnConfirmListener() {
                                        @Override
                                        public void onIgnore(boolean ignore) {
                                            if (ignore) {
                                                SPHelper.get().put(SpKey.IGNORE_VERSION, appBean.getVersionCode());
                                            }
                                        }

                                        @Override
                                        public void onCancel() {
                                        }

                                        @Override
                                        public void onOK() {
                                            //下载新版本
                                            download(context, appBean.getDownloadURL(), result);
                                        }
                                    })
                                    .show();
//                            new UpdateManager(context, appBean.getVersionCode(), false, appBean.getDownloadURL(), apkName, title, message, buttonCancel, buttonOk,
//                                    new UpdateManager.UpdateAppResultListener() {
//                                        @Override
//                                        public void updateToNext() {
//                                            UpdateManagerListener.updateLocalBuildNumber(result);
//                                        }
//
//                                        @Override
//                                        public void updateFailed(boolean forceUpdate) {
//
//                                        }
//                                    });
                        }

                        @Override
                        public void onDenied() {
                            PermissionManager.showAdvice(context);
                        }
                    });
                }
            }

            @Override
            public void onNoUpdateAvailable() {
                if (tip) {
                    Toast.makeText(context, R.string.tip_update_already, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 下载更新
     *
     * @param url
     */
    private static void download(Context context, String url, final String result) {
        final String localUrl = PathUtil.getDownloadPath() + "/" + AppUtils.getAppName() + ".apk";
        final File path = new File(localUrl);
        if (path.exists()) {
            path.delete();
        }
        final DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(AppUtils.getAppName());
        // 设置下载路径和文件名
        request.setDestinationUri(Uri.fromFile(path));
        request.setDescription("正在更新");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        // 获取此次下载的ID
        final long refernece = dManager.enqueue(request);
        Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
        // 注册广播接收器，当下载完成时自动安装
        final IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                long myDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (refernece == myDownloadId) {
                    Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                    UpdateManagerListener.updateLocalBuildNumber(result);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //兼容7.0私有文件权限
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", path);
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    } else {
                        install.setDataAndType(Uri.fromFile(path), "application/vnd.android.package-archive");
                    }
                    context.startActivity(install);
                }
            }
        };
        context.registerReceiver(receiver, filter);
    }
}
