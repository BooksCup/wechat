package com.bc.wechat.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bc.wechat.R;
import com.bc.wechat.activity.AddFriendsActivity;
import com.bc.wechat.activity.ScanQrCodeActivity;

public class AddPopupWindow extends PopupWindow implements View.OnClickListener {
    private View convertView;
    private Context mContext;

    public AddPopupWindow(final Activity context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.popupwindow_add, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(convertView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        // 发起群聊
        RelativeLayout mCreateGroupRl = convertView.findViewById(R.id.rl_create_group);

        // 添加朋友
        RelativeLayout mAddFriendsRl = convertView.findViewById(R.id.rl_add_friends);

        // 扫一扫
        RelativeLayout mScanQrCodeRl = convertView.findViewById(R.id.rl_scan_qr_code);

        // 帮助和反馈
        RelativeLayout mHelpRl = convertView.findViewById(R.id.rl_help);

        mCreateGroupRl.setOnClickListener(this);
        mAddFriendsRl.setOnClickListener(this);
        mScanQrCodeRl.setOnClickListener(this);
        mHelpRl.setOnClickListener(this);
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_create_group:
                break;
            case R.id.rl_add_friends:
                Intent intent = new Intent(mContext, AddFriendsActivity.class);
                mContext.startActivity(intent);
                this.dismiss();
                break;
            case R.id.rl_scan_qr_code:
                mContext.startActivity(new Intent(mContext, ScanQrCodeActivity.class));
                break;
            case R.id.rl_help:
                break;
        }
    }
}
