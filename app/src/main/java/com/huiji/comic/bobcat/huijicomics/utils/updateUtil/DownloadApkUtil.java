package com.huiji.comic.bobcat.huijicomics.utils.updateUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.utils.AppUtils;
import com.pgyersdk.crash.PgyCrashManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Random;

import static com.huiji.comic.bobcat.huijicomics.utils.updateUtil.UpdateManager.NOTIFICATION_ID;

/**
 * Created by HeYongchen on 2017/6/21.
 */
public class DownloadApkUtil {

    private Context mContext;
    private DownloadListener downloadListener;
    private String apkUrl;
    private String savePath;
    private String saveFileName;

    private int progress; //下载进度
    private String apkLength, currentLength;
    private boolean cancelFlag = false; //取消下载标志位
    private String APK_TYPE = "application/vnd.android.package-archive";
    private DecimalFormat df = new DecimalFormat("#0.00");

    private static final int UPDATE_DOWNLOADING = 1;
    private static final int UPDATE_DOWNLOAD_SUCCESS = 2;
    private static final int UPDATE_DOWNLOAD_FAILED = 3;

    /**
     * 下载apk的线程
     */
    public DownloadApkUtil(Context context, DownloadListener downloadListener, String apkUrl, String savePath, String saveFileName) {
        this.mContext = context;
        this.downloadListener = downloadListener;
        this.apkUrl = apkUrl;
        this.savePath = savePath;
        this.saveFileName = saveFileName;

    }

    public void downloadAPK(final Context context) {
        //开启新线程下载apk
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    int length = conn.getContentLength();

                    File saveFile = new File(saveFileName);
                    //若文件已成功下载则直接打开（对比文件大小）
                    if (saveFile.exists() && saveFile.length() == length) {
                        handler.sendEmptyMessage(UPDATE_DOWNLOAD_SUCCESS);
                        installAPK();//安装apk
                        return;
                    }
                    apkLength = df.format((float) length / 1000000);//apk安装包总量，单位M
                    InputStream is = conn.getInputStream();

                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    FileOutputStream fos = new FileOutputStream(saveFile);

                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {
                        int numread = is.read(buf);
                        count += numread;
                        currentLength = df.format((float) count / 1000000);//apk当前已下载量，单位M
                        progress = (int) (((float) count / length) * 100);//更新进度
                        handler.sendEmptyMessage(UPDATE_DOWNLOADING);

                        if (numread <= 0) {
                            //下载完成通知安装
                            handler.sendEmptyMessage(UPDATE_DOWNLOAD_SUCCESS);
                            installAPK();//安装apk
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!cancelFlag); //点击取消就停止下载.

                    fos.close();
                    is.close();
                } catch (Exception e) {
                    handler.sendEmptyMessage(UPDATE_DOWNLOAD_FAILED);
                    PgyCrashManager.reportCaughtException(context, e);
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_DOWNLOADING:
                    if (downloadListener != null) {
                        downloadListener.downloadProgress(progress, currentLength, apkLength);
                    }
                    break;
                case UPDATE_DOWNLOAD_SUCCESS:
                    if (downloadListener != null) {
                        downloadListener.downloadResult(true);
                    }
                    break;
                case UPDATE_DOWNLOAD_FAILED:
                    if (downloadListener != null) {
                        downloadListener.downloadResult(false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 下载完成后自动安装apk
     */
    private void installAPK() {
        File apkFile = new File(saveFileName);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //兼容7.0私有文件权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileProvider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, APK_TYPE);
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), APK_TYPE);
        }
        mContext.startActivity(intent);
        //安装完成后通知栏消息提醒
        Random random = new Random();
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, random.nextInt(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setSmallIcon(R.drawable.icon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(AppUtils.getAppName())
                .setContentText(mContext.getString(R.string.tip_download_success_notification))
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * apk下载状态监听
     */
    public interface DownloadListener {
        void downloadProgress(int progress, String currentLength, String apkLength);

        void downloadResult(boolean isSuccess);
    }
}
