package com.huiji.comic.bobcat.huijicomics.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.base.manager.PermissionManager;
import com.huiji.comic.bobcat.huijicomics.utils.AppUtils;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_about_us)
    ImageView ivAboutUs;
    @BindView(R.id.tv_about_us)
    TextView tvAboutUs;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.ll_version)
    LinearLayout llVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvVersion.setText(String.format("Version：#%s", AppUtils.getVersionName(this)));
        llVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PgyUpdateManager.register(AboutActivity.this, null, new UpdateManagerListener() {

                    @Override
                    public void onUpdateAvailable(final String result) {
                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        new AlertDialog.Builder(AboutActivity.this)
                                .setTitle("版本更新v" + appBean.getVersionName())
                                .setMessage(appBean.getReleaseNote())
                                .setPositiveButton("开始更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionManager.requestPermission(AboutActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionManager.OnPermissionCallback() {
                                            @Override
                                            public void onGranted() {
                                                //开始下载
                                                startDownloadTask(AboutActivity.this, appBean.getDownloadURL());
                                            }

                                            @Override
                                            public void onDenied() {
                                                PermissionManager.showAdvice(AboutActivity.this);
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("暂不更新", null)
                                .show();
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        Toast.makeText(AboutActivity.this, "当前已是最新版", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

}
