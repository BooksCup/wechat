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
import android.widget.EditText;
import android.widget.TextView;

import com.bc.wechat.R;

/**
 * 编辑弹窗
 *
 * @author zhou
 */
public class EditDialog extends Dialog {
    private Context mContext;

    private TextView mTitleTv;
    private EditText mContentEt;
    private TextView mTipsTv;
    private Button mOkBtn;
    private Button mCancelBtn;
    private OnDialogClickListener mDialogClickListener;

    // 标题
    private String mTitle;
    // 内容
    private String mContent;
    // 提示内容
    private String mTips;
    // 确认
    private String mConfirm;
    // 取消
    private String mCancel;

    // 确认按钮颜色
    private int mOkBtnColor = -1;

    public EditDialog(Context context, String title, String content, String tips,
                      String confirm, String cancel) {
        super(context);
        this.mContext = context;
        this.mTitle = title;
        this.mContent = content;
        this.mTips = tips;
        this.mConfirm = confirm;
        this.mCancel = cancel;
        initalize();
    }

    // 初始化View
    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_edit, null);
        setContentView(view);

        initWindow();

        mContentEt = findViewById(R.id.et_content);
        mTitleTv = findViewById(R.id.tv_title);
        mTipsTv = findViewById(R.id.tv_tips);
        mOkBtn = findViewById(R.id.btn_ok);
        mCancelBtn = findViewById(R.id.btn_cancel);

        if (!TextUtils.isEmpty(mTitle)) {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(mTitle);
        } else {
            mTitleTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mContent)) {
            mContentEt.setText(mContent);
        }

        if (!TextUtils.isEmpty(mTips)) {
            mTipsTv.setText(mTips);
        }

        if (!TextUtils.isEmpty(mConfirm)) {
            mOkBtn.setText(mConfirm);
        }

        if (!TextUtils.isEmpty(mCancel)) {
            mCancelBtn.setText(mCancel);
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
        mCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (mDialogClickListener != null) {
                    mDialogClickListener.onCancelClick();
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
//        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();

        // 获取屏幕尺寸
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        // 宽度为屏幕80%
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.8);
        // 中央居中
        layoutParams.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(layoutParams);

        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener) {
        mDialogClickListener = clickListener;
    }

    public String getContent() {
        return mContentEt.getText().toString();
    }

    /**
     * 添加按钮点击事件
     */
    public interface OnDialogClickListener {
        /**
         * 确认
         */
        void onOkClick();

        /**
         * 取消
         */
        void onCancelClick();
    }

}
