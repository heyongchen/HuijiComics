package com.huiji.comic.bobcat.huijicomics.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.get().addActivity(this);
    }

    private ProgressDialog progressDialog = null;

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
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
}
