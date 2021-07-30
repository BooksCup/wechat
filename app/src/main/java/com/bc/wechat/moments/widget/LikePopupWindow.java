package com.bc.wechat.moments.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.moments.adapter.Utils;


/**
 * @作者: njb
 * @时间: 2019/7/24 11:32
 * @描述: 点赞评论popup
 */
public class LikePopupWindow extends PopupWindow implements View.OnClickListener {
    private Context mContext;

    private OnPraiseOrCommentClickListener mOnPraiseOrCommentClickListener;

    private int mPopupWindowHeight;
    private int mPopupWindowWidth;
    private int mCurrentPosition;
    private TextView commentPopupText;

    public LikePopupWindow(Context context, int isLike) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_like, null);
        this.setContentView(contentView);
        contentView.findViewById(R.id.ll_like).setOnClickListener(this);
        contentView.findViewById(R.id.ll_comment).setOnClickListener(this);
        //不设置宽高将无法显示popupWindow
        this.mPopupWindowHeight = Utils.dp2px(40);
        this.mPopupWindowWidth = Utils.dp2px(200);
        this.setHeight(mPopupWindowHeight);
        this.setWidth(mPopupWindowWidth);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //弹出动画
        this.setAnimationStyle(R.style.anim_push_bottom);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        commentPopupText = contentView.findViewById(R.id.tv_like);
        setTextView(isLike);
    }

    public LikePopupWindow setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
        return this;
    }

    public LikePopupWindow setTextView(int isLike) {
        commentPopupText.setText(isLike == 0 ? "点赞" : "取消点赞");
        return this;
    }

    public LikePopupWindow setOnPraiseOrCommentClickListener(OnPraiseOrCommentClickListener onPraiseOrCommentClickListener) {
        mOnPraiseOrCommentClickListener = onPraiseOrCommentClickListener;
        return this;
    }

    public void showPopupWindow(View anchor) {
        if (anchor == null) {
            return;
        }
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        int xOffset = location[0] - mPopupWindowWidth - Utils.dp2px(10f);
        int yOffset = location[1] + (anchor.getHeight() - mPopupWindowHeight) / 2;
        showAtLocation(anchor, Gravity.NO_GRAVITY, xOffset, yOffset);
        //showAsDropDown(anchor,0,0);
        Log.e("location", xOffset + " -------------------" + yOffset);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        int i = v.getId();
        if (i == R.id.ll_like) {
            if (mOnPraiseOrCommentClickListener != null) {
                mOnPraiseOrCommentClickListener.onPraiseClick(mCurrentPosition);
            }

        } else if (i == R.id.ll_comment) {
            if (mOnPraiseOrCommentClickListener != null) {
                mOnPraiseOrCommentClickListener.onCommentClick(mCurrentPosition);
            }
        }
    }
}
