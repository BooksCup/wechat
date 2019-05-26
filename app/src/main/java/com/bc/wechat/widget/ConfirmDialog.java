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

public class ConfirmDialog extends Dialog {
    private Context mContext;
    private TextView mTitleTv;
    private Button mOkBtn;
    private Button mCancelBtn;
    private OnDialogClickListener dialogClickListener;


    private String title;
    private String confirm;
    private String cancel;

    public ConfirmDialog(Context context, String title,
                         String confirm, String cancel) {
        super(context);
        this.mContext = context;
        this.title = title;
        this.confirm = confirm;
        this.cancel = cancel;
        initalize();
    }

    //初始化View
    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_confirm, null);
        setContentView(view);
        initWindow();

        mTitleTv = findViewById(R.id.tv_title);
        mOkBtn = findViewById(R.id.btn_ok);
        mCancelBtn = findViewById(R.id.btn_cancel);

        if (!TextUtils.isEmpty(title)) {
            mTitleTv.setText(title);
        }

        if (!TextUtils.isEmpty(confirm)) {
            mOkBtn.setText(confirm);
        }

        if (!TextUtils.isEmpty(cancel)) {
            mCancelBtn.setText(cancel);
        }

        mOkBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (dialogClickListener != null) {
                    dialogClickListener.onOKClick();
                }
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (dialogClickListener != null) {
                    dialogClickListener.onCancelClick();
                }
            }
        });
    }

    /**
     * 添加黑色半透明背景
     */
    private void initWindow() {
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));//设置window背景
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//设置输入法显示模式
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics();//获取屏幕尺寸
        lp.width = (int) (d.widthPixels * 0.8); //宽度为屏幕80%
        lp.gravity = Gravity.CENTER;  //中央居中
        dialogWindow.setAttributes(lp);
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener) {
        dialogClickListener = clickListener;
    }

    /**
     * 添加按钮点击事件
     */
    public interface OnDialogClickListener {
        void onOKClick();

        void onCancelClick();
    }

}
