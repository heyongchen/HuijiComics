package com.huiji.comic.bobcat.huijicomics.utils.updateUtil;

import android.Manifest;
import android.app.Activity;
import android.widget.Toast;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.base.manager.PermissionManager;
import com.huiji.comic.bobcat.huijicomics.utils.AppUtils;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

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
                        new UpdateManager(context, false, appBean.getDownloadURL(), apkName, title, message, buttonCancel, buttonOk,
                                new UpdateManager.UpdateAppResultListener() {
                                    @Override
                                    public void updateToNext() {
                                        UpdateManagerListener.updateLocalBuildNumber(result);
                                    }

                                    @Override
                                    public void updateFailed(boolean forceUpdate) {

                                    }
                                });
                    }

                    @Override
                    public void onDenied() {
                        PermissionManager.showAdvice(context);
                    }
                });
            }

            @Override
            public void onNoUpdateAvailable() {
                if (tip) {
                    Toast.makeText(context, R.string.tip_update_already, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
