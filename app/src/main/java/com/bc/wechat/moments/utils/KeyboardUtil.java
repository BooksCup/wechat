package com.bc.wechat.moments.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 用于监听键盘是否弹出，并获取键盘高度
 */
public class KeyboardUtil {
    Activity mContext;
    /**
     * 虚拟键盘高度
     */
    int virtualKeyboardHeight;
    /**
     * 屏幕高度
     */
    int screenHeight;
    /**
     * 屏幕6分之一的高度，作用是防止获取到虚拟键盘的高度
     */
    int screenHeight6;
    View rootView;

    public KeyboardUtil(Activity context) {
        this.mContext = context;
        /**
         * 获取屏幕的高度,该方式的获取不包含虚拟键盘
         */
        screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
        screenHeight6 = screenHeight / 6;
        rootView = mContext.getWindow().getDecorView();
    }

//    /**
//     * @param listener
//     */
//    public void setOnKeyboardChangeListener(final KeyboardChangeListener listener) {
//        //当键盘弹出隐藏的时候会 调用此方法。
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            /**
//             * 回调该方法时rootView还未绘制，需要设置绘制完成监听
//             */
//            rootView.post(() -> {
//                Rect rect = new Rect();
//                /**
//                 * 获取屏幕底部坐标
//                 */
//                rootView.getWindowVisibleDisplayFrame(rect);
//                /**
//                 * 获取键盘的高度
//                 */
//                int heightDifference = screenHeight - rect.bottom;
//                if (heightDifference < screenHeight6) {
//                    virtualKeyboardHeight = heightDifference;
//                    if (listener != null) {
//                        listener.onKeyboardHide();
//                    }
//                } else {
//                    if (listener != null) {
//                        listener.onKeyboardShow(heightDifference - virtualKeyboardHeight);
//                    }
//                }
//            });
//        });
//    }

    /**
     * 软键盘状态切换监听
     */
    public interface KeyboardChangeListener {
        /**
         * 键盘弹出
         *
         * @param keyboardHight 键盘高度
         */
        void onKeyboardShow(int keyboardHight);

        /**
         * 键盘隐藏
         */
        void onKeyboardHide();
    }

    /**
     * 显示软键盘
     *
     * @param context 当前Activity
     */
    public static void showSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     *
     * @param activity 当前Activity
     */
    public static void hideSoftInput(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    public static boolean isShowSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //获取状态信息
        return imm.isActive();//true 打开
    }

    /**
     * 是否显示，显示则关闭，没显示则显示
     *
     * @param context
     */
    public static void isShow(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
