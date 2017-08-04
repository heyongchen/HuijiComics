package com.huiji.comic.bobcat.huijicomics.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.huiji.comic.bobcat.huijicomics.R;
import com.huiji.comic.bobcat.huijicomics.utils.DisplayUtil;

/**
 * 通用确认对话框
 *
 * @author Administrator
 * @since 2015年10月22日 18:22:23
 */
public class ConfirmDialog extends Dialog {
    private Context mContext;
    private View view;
    private TextView tv_title;
    private TextView tv_message;
    private TextView button_cancel;
    private TextView button_ok;
    private OnConfirmListener mListener;

    public interface OnConfirmListener {
        void onCancel();

        void onOK();
    }

    public ConfirmDialog(Context context) {
        super(context, R.style.confirm_dialog);
        mContext = context;
        initView();
    }

    private void initView() {
        view = LayoutInflater.from(mContext).inflate(
                R.layout.confirm_dialog_layout, null);
        tv_title = (TextView) view.findViewById(R.id.tv_confirm_title);
        tv_message = (TextView) view.findViewById(R.id.tv_confirm_message);
        button_cancel = (TextView) view.findViewById(R.id.Button_Cancel);
        button_ok = (TextView) view.findViewById(R.id.Button_OK);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCancel();
                }
                dismiss();
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onOK();
                }
                dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (view != null) {
            setContentView(view);
            configDialog();
        }
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public ConfirmDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置内容
     *
     * @param message
     * @return
     */
    public ConfirmDialog setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            tv_message.setText(message);
        }
        return this;
    }

    /**
     * 设置内容对齐方式
     *
     * @param gravity
     * @return
     */
    public ConfirmDialog setMessageAlignment(int gravity) {
        tv_message.setGravity(gravity);
        return this;
    }

    /**
     * 设置确定按钮文本
     *
     * @param text 默认 确定
     * @return
     */
    public ConfirmDialog setButtonOKText(String text) {
        if (!TextUtils.isEmpty(text))
            button_ok.setText(text);
        return this;
    }

    /**
     * 设置取消按钮文本
     *
     * @param text 默认 取消
     * @return
     */
    public ConfirmDialog setButtonCancelText(String text) {
        if (!TextUtils.isEmpty(text))
            button_cancel.setText(text);
        return this;
    }

    /**
     * 设置输入监听
     *
     * @param listener
     * @return
     */
    public ConfirmDialog setOnConfirmListener(ConfirmDialog.OnConfirmListener listener) {
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
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DisplayUtil.getScreenWidth(mContext) * 3 / 4;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        //因为某些机型是虚拟按键的,所以要加上以下设置防止挡住按键.
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}
