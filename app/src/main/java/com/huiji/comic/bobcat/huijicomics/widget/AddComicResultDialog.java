package com.huiji.comic.bobcat.huijicomics.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.bean.ComicListBean;
import com.huiji.comic.bobcat.huijicomics.utils.DisplayUtil;

/**
 * Created by HeYongchen on 2017/8/7.
 */

public class AddComicResultDialog extends Dialog {

    private ImageView ivComicView;
    private TextView tvComicTitle;
    private TextView tvComicAuthor;
    private TextView tvComicMsg;
    private TextView ButtonCancel;
    private TextView ButtonOK;

    private Context mContext;
    private View view;
    private OnConfirmListener mListener;
    private ComicListBean mComicListBean;

    public interface OnConfirmListener {
        void onCancel();

        void onOK(String comicId);
    }

    public AddComicResultDialog(@NonNull Context context, ComicListBean comicListBean) {
        super(context, R.style.confirm_dialog);
        mContext = context;
        mComicListBean = comicListBean;
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
        view = LayoutInflater.from(mContext).inflate(R.layout.add_comic_result_dialog_layout, null);
        ivComicView = (ImageView) view.findViewById(R.id.iv_comic_view);
        tvComicTitle = (TextView) view.findViewById(R.id.tv_comic_title);
        tvComicAuthor = (TextView) view.findViewById(R.id.tv_comic_author);
        tvComicMsg = (TextView) view.findViewById(R.id.tv_comic_msg);
        ButtonOK = (TextView) view.findViewById(R.id.Button_OK);
        ButtonCancel = (TextView) view.findViewById(R.id.Button_Cancel);

        if (mComicListBean.getImgUrl() != null) {
            Glide.with(mContext)
                    .load(mComicListBean.getImgUrl())
                    .into(ivComicView);
        }
        tvComicTitle.setText(mComicListBean.getTitle() != null ? mComicListBean.getTitle() : "");
        tvComicAuthor.setText(mComicListBean.getAuthor() != null ? mComicListBean.getAuthor() : "");
        tvComicMsg.setText(mComicListBean.getMsg() != null ? mComicListBean.getMsg() : "");

        ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCancel();
                }
                dismiss();
            }
        });

        ButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onOK(mComicListBean.getComicId());
                }
                dismiss();
            }
        });
    }

    /**
     * 设置输入监听
     *
     * @param listener
     * @return
     */
    public AddComicResultDialog setOnConfirmListener(OnConfirmListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * 配置对话框
     */
    private void configDialog() {
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        //设置对话框居中
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = DisplayUtil.getScreenWidth(mContext) * 3 / 4;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
            //因为某些机型是虚拟按键的,所以要加上以下设置防止挡住按键.
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}
