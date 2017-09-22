package com.huiji.comic.bobcat.huijicomics.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.base.BaseActivity;
import com.huiji.comic.bobcat.huijicomics.utils.C;
import com.huiji.comic.bobcat.huijicomics.utils.SPHelper;
import com.huiji.comic.bobcat.huijicomics.utils.SpKey;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sw_set_web)
    Switch swSetWeb;
    @BindView(R.id.sw_set_horizontal)
    Switch swSetHorizontal;
    @BindView(R.id.sw_set_page)
    Switch swSetPage;
    @BindView(R.id.ll_set_horizontal)
    LinearLayout llSetHorizontal;
    @BindView(R.id.ll_set_page)
    LinearLayout llSetPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (SPHelper.get().get(SpKey.VIEW_TYPE, "").isEmpty()) {
            SPHelper.get().put(SpKey.VIEW_TYPE, C.VIEW_TYPE_WEB);
            swSetWeb.setChecked(true);
            llSetPage.setVisibility(View.GONE);
        } else {
            if (SPHelper.get().get(SpKey.VIEW_TYPE, "").equals(C.VIEW_TYPE_NATIVE)) {
                swSetWeb.setChecked(false);
                llSetPage.setVisibility(View.VISIBLE);
            } else {
                swSetWeb.setChecked(true);
                llSetPage.setVisibility(View.GONE);
            }
        }
        if (SPHelper.get().get(SpKey.VIEW_PAGE, "").isEmpty()) {
            SPHelper.get().put(SpKey.VIEW_PAGE, C.viewType.FLOW);
            swSetPage.setChecked(false);
            llSetHorizontal.setVisibility(View.VISIBLE);
        } else {
            if (SPHelper.get().get(SpKey.VIEW_PAGE, "").equals(C.viewType.FLOW)) {
                swSetPage.setChecked(false);
                llSetHorizontal.setVisibility(View.VISIBLE);
            } else {
                swSetPage.setChecked(true);
                llSetHorizontal.setVisibility(View.GONE);
            }
        }
        if (SPHelper.get().get(SpKey.VIEW_HORIZONTAL, false)) {
            swSetHorizontal.setChecked(true);
        } else {
            swSetHorizontal.setChecked(false);
        }

        swSetWeb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPHelper.get().put(SpKey.VIEW_TYPE, C.VIEW_TYPE_WEB);
                    llSetPage.setVisibility(View.GONE);
                } else {
                    SPHelper.get().put(SpKey.VIEW_TYPE, C.VIEW_TYPE_NATIVE);
                    llSetPage.setVisibility(View.VISIBLE);
                }
            }
        });

        swSetPage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPHelper.get().put(SpKey.VIEW_PAGE, C.viewType.PAGE);
                    llSetHorizontal.setVisibility(View.GONE);
                } else {
                    SPHelper.get().put(SpKey.VIEW_PAGE, C.viewType.FLOW);
                    llSetHorizontal.setVisibility(View.VISIBLE);
                }
            }
        });

        swSetHorizontal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPHelper.get().put(SpKey.VIEW_HORIZONTAL, true);
                } else {
                    SPHelper.get().put(SpKey.VIEW_HORIZONTAL, false);
                }
            }
        });

    }

}
