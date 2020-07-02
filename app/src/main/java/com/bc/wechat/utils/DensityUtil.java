package com.bc.wechat.utils;

import android.content.Context;

/**
 * dp/px(像素)转换工具类
 *
 * @author zhou
 */
public class DensityUtil {
    /**
     * 根据手机的分辨率从dp的单位转成为px(像素)
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px(像素)值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从px(像素)的单位转成为 dp
     *
     * @param context 上下文
     * @param pxValue px(像素)值
     * @return dp值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
