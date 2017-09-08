package com.huiji.comic.bobcat.allcomics.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huiji.comic.bobcat.allcomics.R;
import com.huiji.comic.bobcat.allcomics.utils.DisplayUtil;

/**
 * Created by HeYongchen on 2017/8/7.
 */

public class AddComicDialog extends Dialog {

    private EditText etAddComicId;
    private TextView ButtonCancel;
    private TextView ButtonOK;

    private Context mContext;
    private View view;
    private OnConfirmListener mListener;

    public interface OnConfirmListener {
        void onCancel();

        void onOK(String comicId);
    }

    public AddComicDialog(@NonNull Context context) {
        super(context, R.style.confirm_dialog);
        mContext = context;
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
        view = LayoutInflater.from(mContext).inflate(R.layout.add_comic_dialog_layout, null);
        etAddComicId = (EditText) view.findViewById(R.id.et_add_comic_id);
        ButtonOK = (TextView) view.findViewById(R.id.Button_OK);
        ButtonCancel = (TextView) view.findViewById(R.id.Button_Cancel);

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
                if (!etAddComicId.getText().toString().isEmpty()) {
                    if (mListener != null) {
                        mListener.onOK(etAddComicId.getText().toString());
                    }
                    dismiss();
                } else {
                    Toast.makeText(mContext, "请输入漫画ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 设置输入监听
     *
     * @param listener
     * @return
     */
    public AddComicDialog setOnConfirmListener(OnConfirmListener listener) {
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
