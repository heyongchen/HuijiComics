package com.huiji.comic.bobcat.huijicomics.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.utils.DisplayUtil;

/**
 * Created by HeYongchen on 2017/8/16.
 */

public class ImageShowDialog extends Dialog {

    private Context mContext;
    private String mImageUrl;
    private View view;
    private ImageView imageView;

    public ImageShowDialog(@NonNull Context context, String imageUrl) {
        super(context, R.style.confirm_dialog);
        mContext = context;
        mImageUrl = imageUrl;
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (view != null) {
            setContentView(view);
            configDialog();
        }
    }

    private void initView() {
        view = LayoutInflater.from(mContext).inflate(
                R.layout.image_show_dialog, null);
        imageView = (ImageView) view.findViewById(R.id.iv_image);
        if (!mImageUrl.isEmpty()) {
            Glide.with(mContext).asBitmap().load(mImageUrl).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    layoutParams.height = DisplayUtil.getScreenWidth(mContext) * 3 / 4 * resource.getHeight() / resource.getWidth();
                    imageView.setImageBitmap(resource);
                }
            });
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageShowDialog.this.dismiss();
            }
        });
    }

    /**
     * 配置对话框
     */
    private void configDialog() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        //设置对话框居中
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DisplayUtil.getScreenWidth(mContext) * 3 / 4;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        //因为某些机型是虚拟按键的,所以要加上以下设置防止挡住按键.
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}
