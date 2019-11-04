package com.google.zxing.util;

import android.app.Dialog;
import android.widget.PopupWindow;

import java.util.HashSet;
import java.util.Set;

public class ProjectorUtil {

    private static ProjectorUtil sInstance = new ProjectorUtil();

    private ProjectorUtil() {
    }

    public static ProjectorUtil getInstance() {
        return sInstance;
    }

    protected final Set<Dialog> mDialogSet = new HashSet<>();
    protected final Set<PopupWindow> mPopupWindowSet = new HashSet<>();

    public void addDialog(Dialog dialog) {
//        mDialogSet.add(dialog);
    }

    public Set<Dialog> getDialogSet() {
        return mDialogSet;
    }

    public void addPopupWindow(PopupWindow popupWindow) {
//        mPopupWindowSet.add(popupWindow);
    }

    public Set<PopupWindow> getPopupWindowSet() {
        return mPopupWindowSet;
    }
}
