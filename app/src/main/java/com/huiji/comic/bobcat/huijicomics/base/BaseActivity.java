package com.huiji.comic.bobcat.huijicomics.base;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.huiji.comic.bobcat.huijicomics.base.manager.AppManager;
import com.huiji.comic.bobcat.huijicomics.base.manager.PermissionManager;
import com.pgyersdk.crash.PgyCrashManager;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class BaseActivity extends AppCompatActivity implements PermissionManager.OnBaseCallback {

    private PermissionManager.OnPermissionCallback mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppManager.get().addActivity(this);
        PgyCrashManager.register(this);
    }

    private ProgressDialog progressDialog = null;

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            initProgressDialog();
        }
        progressDialog.setMessage("加载中，请稍候……");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            initProgressDialog();
        }
        progressDialog.setMessage(msg);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限被授予
                if (mCallback != null) {
                    mCallback.onGranted();
                }
            } else {
                //权限被拒绝
                if (mCallback != null) {
                    mCallback.onDenied();
                }
            }

        }
    }

    @Override
    public void setPermissionCallback(PermissionManager.OnPermissionCallback callback) {
        mCallback = callback;
    }
}
