package com.bc.wechat.moments.utils;

import android.content.Context;
import android.view.View;

/**
 * @作者: njb
 * @时间: 2019/7/22 10:53
 * @描述: popupwindow工具类
 */
public class PopupWindowUtil {
    /**
     * 计算popwindow在长按view 的什么位置显示
     *
     * @param anchorView 长按锚点的view
     * @param popView    弹出框
     * @param onScreenx  锚点距离屏幕左边的距离
     * @param onScreeny  锚点距离屏幕上方的距离
     * @return popwindow在长按view中的xy轴的偏移量
     */
    public static int[] calculatePopWindowPos(final View anchorView, final View popView, int onScreenx, int onScreeny) {
        final int[] windowPos = new int[2];
        final int[] anchorLoc = new int[2];
        // 获取触点在屏幕上相对左上角坐标位置
        anchorLoc[0] = onScreenx;
        anchorLoc[1] = onScreeny;
        //当前item的高度
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        // 测量popView 弹出框
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算弹出框的高宽
        final int popHeight = popView.getMeasuredHeight();
        final int popWidth = popView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        // 屏幕高度-触点距离左上角的高度 < popwindow的高度
        // 如果小于弹出框的高度那么说明下方空间不够显示 popwindow，需要放在触点的上方显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] < popHeight);
        // 判断需要向右边弹出还是向左边弹出显示
        //判断触点右边的剩余空间是否够显示popwindow 大于就说明够显示
        final boolean isNeedShowRight = (screenWidth - anchorLoc[0] > popWidth);
        if (isNeedShowUp) {
            //如果在上方显示 则用 触点的距离上方的距离 - 弹框的高度
            windowPos[1] = anchorLoc[1] - popHeight;
        } else {
            //如果在下方显示 则用 触点的距离上方的距离
            windowPos[1] = anchorLoc[1];
        }
        if (isNeedShowRight) {
            windowPos[0] = anchorLoc[0];
        } else {
            //显示在左边的话 那么弹出框的位置在触点左边出现，则是触点距离左边距离 - 弹出框的宽度
            windowPos[0] = anchorLoc[0] - popWidth;
        }
        return windowPos;
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
