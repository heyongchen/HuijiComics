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
        progressDialog.setMessage("加载中，请稍后……");
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            initProgressDialog();
        }
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
