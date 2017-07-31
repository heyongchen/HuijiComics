package com.huiji.comic.bobcat.huijicomics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huiji.comic.bobcat.huijicomics.MainApplication;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.utils.C;
import com.huiji.comic.bobcat.huijicomics.utils.SPHelper;
import com.huiji.comic.bobcat.huijicomics.utils.SpKey;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LauncherActivity extends BaseActivity {

    @BindView(R.id.tv_skip)
    TextView tvSkip;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private DbManager dbManager = x.getDb(MainApplication.getDbConfig());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 隐藏状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (SPHelper.get().get(SpKey.DATABASE_VERSION, 1) < C.DATABASE_VERSION) {
            try {
                dbManager.dropDb();
            } catch (DbException e) {
                e.printStackTrace();
            }
            SPHelper.get().put(SpKey.DATABASE_VERSION, C.DATABASE_VERSION);
        }

        handler.postDelayed(runnable, 2000);
    }

}
