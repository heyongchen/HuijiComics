package com.huiji.comic.bobcat.huijicomics;

import android.app.Application;
import android.util.Log;

import com.huiji.comic.bobcat.huijicomics.utils.C;
import com.huiji.comic.bobcat.huijicomics.utils.UIUtils;
import com.pgyersdk.crash.PgyCrashManager;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * Created by HeYongchen on 2017/7/27.
 */

public class MainApplication extends Application {

    private static DbManager.DaoConfig daoConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false); // 是否输出debug日志, 开启debug会影响性能.
        PgyCrashManager.register(this);
        UIUtils.init(this);
    }

    public static DbManager.DaoConfig getDbConfig() {
        if (daoConfig == null) {
            synchronized (MainApplication.class) {
                daoConfig = new DbManager.DaoConfig()
                        //设置数据库名，默认xUtils.db
                        .setDbName("Huiji.db")
                        //设置数据库路径，默认存储在app的私有目录
                        //.setDbDir(new File("/mnt/sdcard/"))
                        //设置数据库的版本号
                        .setDbVersion(C.DATABASE_VERSION)
                        //设置数据库打开的监听
                        .setDbOpenListener(new DbManager.DbOpenListener() {
                            @Override
                            public void onDbOpened(DbManager db) {
                                //开启数据库支持多线程操作，提升性能，对写入加速提升巨大
                                db.getDatabase().enableWriteAheadLogging();
                            }
                        })
                        //设置数据库更新的监听
                        .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                            @Override
                            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                                if (oldVersion < newVersion) {
                                    try {
                                        db.dropDb();
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                        //设置表创建的监听
                        .setTableCreateListener(new DbManager.TableCreateListener() {
                            @Override
                            public void onTableCreated(DbManager db, TableEntity<?> table) {
                                Log.i("JAVA", "onTableCreated：" + table.getName());
                            }
                        });
            }
        }
        return daoConfig;
    }

}
