package com.bc.wechat.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bc.wechat.R;

/**
 * 警告弹窗
 *
 * @author zhou
 */
public class WarningDialog extends Dialog {
    private Context mContext;

    private TextView mTitleTv;
    private TextView mContentTv;
    private Button mOkBtn;
    private OnDialogClickListener mDialogClickListener;

    // 标题
    private String mTitle;
    // 内容
    private String mContent;
    private String mConfirm;

    // 确认按钮颜色
    private int mOkBtnColor = -1;

    public WarningDialog(Context context, String title, String content,
                         String confirm) {
        super(context);
        this.mContext = context;
        this.mTitle = title;
        this.mContent = content;
        this.mConfirm = confirm;
        initalize();
    }

    public WarningDialog(Context context, String title, String content,
                         String confirm, int okBtnColor) {
        super(context);
        this.mContext = context;
        this.mTitle = title;
        this.mContent = content;
        this.mConfirm = confirm;
        this.mOkBtnColor = okBtnColor;
        initalize();
    }

    // 初始化View
    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_warning, null);
        setContentView(view);
        initWindow();

        mContentTv = findViewById(R.id.tv_content);
        mTitleTv = findViewById(R.id.tv_title);
        mOkBtn = findViewById(R.id.btn_ok);

        if (!TextUtils.isEmpty(mTitle)) {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(mTitle);
        } else {
            mTitleTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mContent)) {
            mContentTv.setText(mContent);
        }

        if (!TextUtils.isEmpty(mConfirm)) {
            mOkBtn.setText(mConfirm);
        }

        if (-1 != mOkBtnColor) {
            mOkBtn.setTextColor(mOkBtnColor);
        }


        mOkBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (mDialogClickListener != null) {
                    mDialogClickListener.onOkClick();
                }
            }
        });
    }

    /**
     * 添加黑色半透明背景
     */
    private void initWindow() {
        Window dialogWindow = getWindow();
        // 设置window背景
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));
        // 设置输入法显示模式
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        // 获取屏幕尺寸
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        // 宽度为屏幕80%
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.8);
        // 中央居中
        layoutParams.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(layoutParams);
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener) {
        mDialogClickListener = clickListener;
    }

    /**
     * 添加按钮点击事件
     */
    public interface OnDialogClickListener {
        /**
         * 确认
         */
        void onOkClick();
    }

}
