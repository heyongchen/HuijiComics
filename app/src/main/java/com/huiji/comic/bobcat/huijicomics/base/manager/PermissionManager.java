package com.huiji.comic.bobcat.huijicomics.base.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.huiji.comic.bobcat.huijicomics.R;

/**
 * 动态权限申请工具类
 * Created by wangzhen on 16/7/21.
 * 修改人：何泳郴
 * 修改时间：2017/7/20
 */
public class PermissionManager {
    /**
     * Activity请求相应权限
     *
     * @param permission 需要请求的权限
     * @return 是否请求成功
     */
    public static <T> void requestPermission(T object, String[] permission, OnPermissionCallback callback) {
        if (object instanceof OnBaseCallback) {
            ((OnBaseCallback) object).setPermissionCallback(callback);
        }
        Activity activity = AppManager.get().getActivity();
        // 6.0版本以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断权限是否被授予
            if (!hasPermission(activity, permission)) {
                activity.requestPermissions(permission, 1);
            } else {
                callback.onGranted();
            }
        } else {
            if (!hasPermission(activity, permission)) {
                callback.onDenied();
            } else {
                callback.onGranted();
            }
        }
    }

    /**
     * 解决Android6.0拒绝了权限仍返回获取成功问题
     *
     * @param permissions 需要判断的权限
     * @return 是否已有该权限（只有拥有所有权限时才返回true，否则为false）
     */
    private static boolean hasPermission(Context context, String[] permissions) {
        for (String permission : permissions) {
            String op = AppOpsManagerCompat.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) continue;
            int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result == AppOpsManagerCompat.MODE_IGNORED) return false;
            result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    /**
     * 手动开启权限
     *
     * @param context
     * @param message
     */
    public static void showAdvice(final Context context, String message) {
        if (TextUtils.isEmpty(message)) {
            showAdvice(context);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message);
            builder.setNegativeButton(R.string.tip_permission_cancel, null);
            builder.setPositiveButton(R.string.tip_permission_set, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intentSetting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intentSetting.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intentSetting);
                }
            });
            builder.show();
        }
    }

    /**
     * 手动开启权限
     *
     * @param context
     */
    public static void showAdvice(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.tip_permission_msg);
        builder.setNegativeButton(R.string.tip_permission_cancel, null);
        builder.setPositiveButton(R.string.tip_permission_set, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentSetting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intentSetting.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intentSetting);
            }
        });
        builder.show();
    }

    public interface OnPermissionCallback {
        void onGranted();

        void onDenied();
    }

    public interface OnBaseCallback {
        void setPermissionCallback(OnPermissionCallback callback);
    }
}
